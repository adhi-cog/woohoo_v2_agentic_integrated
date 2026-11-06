#!/usr/bin/env bash
#
# Server-side bootstrap for the pilot host. Idempotent: safe to re-run for every deploy.
#
#   scp -i woohoo-dev.pem woohoo-pilot.tar.gz woohoo.env ubuntu@HOST:/tmp/
#   ssh -i woohoo-dev.pem ubuntu@HOST 'bash /tmp/bootstrap.sh'
#
# Runs ON THE SERVER. The guard below is not decoration: the previous iteration of this
# script was pasted into PowerShell, where `cat`, `chmod` and heredocs all failed while
# `docker compose up` succeeded, producing a build on the wrong machine.

set -euo pipefail

APP_DIR=/opt/woohoo
BUNDLE=/tmp/woohoo-pilot.tar.gz
ENV_SRC=/tmp/woohoo.env

if [[ "$(uname -s)" != "Linux" ]]; then
  echo "ERROR: this script runs on the Linux server, not locally." >&2
  exit 1
fi

echo "==> packages"
export DEBIAN_FRONTEND=noninteractive
sudo apt-get update -qq
sudo apt-get install -y -qq ca-certificates curl gnupg nginx >/dev/null

# Docker from the official repo when it is reachable, distro docker.io otherwise. Either
# gives us compose v2; the fallback is checked for explicitly further down rather than
# assumed, because docker.io on older Ubuntu ships compose v1 under a different name.
if ! command -v docker >/dev/null 2>&1; then
  echo "==> installing docker"
  sudo install -m 0755 -d /etc/apt/keyrings
  if curl -fsSL --max-time 30 https://download.docker.com/linux/ubuntu/gpg \
       | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg 2>/dev/null; then
    sudo chmod a+r /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
      | sudo tee /etc/apt/sources.list.d/docker.list >/dev/null
    sudo apt-get update -qq
    sudo apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-compose-plugin >/dev/null
  else
    echo "    docker.com unreachable, using distro packages"
    sudo apt-get install -y -qq docker.io docker-compose-v2 >/dev/null
  fi
fi
sudo systemctl enable --now docker

if sudo docker compose version >/dev/null 2>&1; then
  COMPOSE=(sudo docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE=(sudo docker-compose)
else
  echo "ERROR: no docker compose available." >&2
  exit 1
fi
echo "    compose: ${COMPOSE[*]}"

echo "==> source"
sudo mkdir -p "$APP_DIR"
sudo chown "$USER":"$USER" "$APP_DIR"
if [[ -f "$BUNDLE" ]]; then
  tar -xzf "$BUNDLE" -C "$APP_DIR"
  rm -f "$BUNDLE"
else
  echo "    no bundle at $BUNDLE, redeploying existing source"
fi

PILOT_DIR="$APP_DIR/deploy/pilot"
[[ -d "$PILOT_DIR" ]] || { echo "ERROR: $PILOT_DIR missing - bundle is wrong." >&2; exit 1; }

echo "==> env"
# Written fresh, never appended: appending is how you end up with three values for the
# same key and no idea which one compose read.
if [[ -f "$ENV_SRC" ]]; then
  install -m 600 "$ENV_SRC" "$PILOT_DIR/.env"
  rm -f "$ENV_SRC"
fi
[[ -f "$PILOT_DIR/.env" ]] || { echo "ERROR: deploy/pilot/.env missing." >&2; exit 1; }
chmod 600 "$PILOT_DIR/.env"

# shellcheck disable=SC1090
PILOT_KEY="$(grep -E '^PILOT_KEY=' "$PILOT_DIR/.env" | cut -d= -f2-)"
if [[ ! "$PILOT_KEY" =~ ^[A-Za-z0-9_-]{16,}$ ]]; then
  echo "ERROR: PILOT_KEY in deploy/pilot/.env is missing or too short." >&2
  exit 1
fi
# The gateway generates an admin token per restart and prints it. Pinning it means a
# restart does not invalidate the provisioning commands in the runbook.
if ! grep -qE '^WOOHOO_ADMIN_TOKEN=.{16,}$' "$PILOT_DIR/.env"; then
  echo "ERROR: WOOHOO_ADMIN_TOKEN must be pinned in deploy/pilot/.env." >&2
  exit 1
fi

echo "==> nginx"
sed "s|__PILOT_KEY__|${PILOT_KEY}|g" "$PILOT_DIR/nginx-woohoo.conf.template" \
  | sudo tee /etc/nginx/sites-available/woohoo >/dev/null
sudo chmod 640 /etc/nginx/sites-available/woohoo
sudo ln -sfn /etc/nginx/sites-available/woohoo /etc/nginx/sites-enabled/woohoo
# The stock default server would otherwise answer on 8443 as well and shadow ours.
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx || sudo systemctl restart nginx

if sudo ufw status 2>/dev/null | grep -q '^Status: active'; then
  echo "==> ufw is active, allowing 8443"
  sudo ufw allow 8443/tcp >/dev/null
fi

echo "==> build and start"
cd "$PILOT_DIR"
"${COMPOSE[@]}" -f compose.pilot.yml up -d --build --remove-orphans

echo "==> waiting for the gateway"
for i in $(seq 1 60); do
  if curl -fsS --max-time 3 http://127.0.0.1:8080/health >/dev/null 2>&1; then
    echo "    gateway up after ${i}s"
    break
  fi
  [[ $i -eq 60 ]] && { echo "ERROR: gateway did not become healthy." >&2
                       "${COMPOSE[@]}" -f compose.pilot.yml logs --tail=60 device-gateway >&2
                       exit 1; }
  sleep 1
done

echo "==> verifying the public edge"
code=$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 http://127.0.0.1:8443/health)
echo "    /health through nginx          : $code  (expect 200)"
code=$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 http://127.0.0.1:8443/)
echo "    console through nginx          : $code  (expect 404 - must NOT be 200)"
code=$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 http://127.0.0.1:8443/v1/devices)
echo "    device list through nginx      : $code  (expect 404)"
code=$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 -X POST \
        -H 'content-type: application/json' -d '{"deviceId":"x","transcript":"x"}' \
        http://127.0.0.1:8443/v1/turn)
echo "    /v1/turn without the key       : $code  (expect 403)"

echo
echo "==> done. Remaining step is outside this box: the AWS security group must allow"
echo "    inbound TCP 8443. Nothing above can open it."
