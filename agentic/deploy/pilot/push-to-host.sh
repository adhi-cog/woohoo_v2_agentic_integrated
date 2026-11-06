#!/usr/bin/env bash
#
# Local side of the deploy: bundle, copy, run bootstrap on the host. Run from anywhere.
#
#   bash agentic/deploy/pilot/push-to-host.sh \
#        /c/Users/adhit/Documents/woohoo_key/woohoo-dev.pem 65.1.106.31
#
# On Windows run this in Git Bash, not PowerShell. PowerShell does not have tar's exclude
# syntax or scp's argument handling, and a half-executed deploy is worse than none.

set -euo pipefail

KEY="${1:?usage: push-to-host.sh <path-to-pem> [host]}"
HOST="${2:-65.1.106.31}"
USER_AT="ubuntu@${HOST}"

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
AGENTIC="$(cd "$HERE/../.." && pwd)"
BUNDLE="$(mktemp -d)/woohoo-pilot.tar.gz"

[[ -f "$KEY" ]]          || { echo "ERROR: no key at $KEY" >&2; exit 1; }
[[ -f "$HERE/.env" ]]    || { echo "ERROR: $HERE/.env missing - copy .env.example and fill it." >&2; exit 1; }

echo "==> checking the host answers on 22"
if ! ssh -i "$KEY" -o BatchMode=yes -o ConnectTimeout=15 \
        -o StrictHostKeyChecking=accept-new "$USER_AT" true 2>/dev/null; then
  echo "ERROR: cannot reach $USER_AT on port 22." >&2
  echo "       The AWS security group must allow inbound TCP 22 from this address:" >&2
  echo "       $(curl -s --max-time 10 https://api.ipify.org || echo '<could not determine>')" >&2
  exit 1
fi

echo "==> bundling $AGENTIC"
# node_modules is rebuilt in the image; the pem and .env must never travel in the bundle.
tar --exclude=node_modules --exclude=.git --exclude='*.tar.gz' --exclude='*.pem' \
    --exclude='.env' --exclude=cloud_deployment \
    -czf "$BUNDLE" -C "$AGENTIC" .
echo "    $(du -h "$BUNDLE" | cut -f1)"

echo "==> copying"
scp -q -i "$KEY" "$BUNDLE"           "$USER_AT:/tmp/woohoo-pilot.tar.gz"
scp -q -i "$KEY" "$HERE/.env"        "$USER_AT:/tmp/woohoo.env"
scp -q -i "$KEY" "$HERE/bootstrap.sh" "$USER_AT:/tmp/bootstrap.sh"
rm -f "$BUNDLE"

echo "==> running bootstrap on $HOST"
ssh -i "$KEY" "$USER_AT" 'bash /tmp/bootstrap.sh'

echo
echo "==> checking the edge from here, over the public internet"
PILOT_KEY="$(grep -E '^PILOT_KEY=' "$HERE/.env" | cut -d= -f2-)"
probe() { curl -s -o /dev/null -w '%{http_code}' --max-time 10 "$@" 2>/dev/null || echo "---"; }
echo "    /health                   : $(probe "http://$HOST:8443/health")          expect 200"
echo "    /  (console)              : $(probe "http://$HOST:8443/")                expect 404"
echo "    /v1/devices               : $(probe "http://$HOST:8443/v1/devices")       expect 404"
echo "    /v1/admin/rooms           : $(probe "http://$HOST:8443/v1/admin/rooms")   expect 404"
echo "    /v1/turn  no key          : $(probe -X POST -H 'content-type: application/json' \
                                         -d '{"deviceId":"d_412a","transcript":"hello"}' \
                                         "http://$HOST:8443/v1/turn")               expect 403"
echo "    /v1/turn  with key        : $(probe -X POST -H 'content-type: application/json' \
                                         -H "x-pilot-key: $PILOT_KEY" \
                                         -d '{"deviceId":"d_412a","transcript":"turn up the volume"}' \
                                         "http://$HOST:8443/v1/turn")               expect 200"
