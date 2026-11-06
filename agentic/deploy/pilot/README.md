# Pilot deployment — 65.1.106.31

Runs the agentic platform alone on one public host, so hubs and testers reach a gateway
that is always up instead of someone's laptop.

## What runs where

```
hub / APK  ──http──▶  :8443 nginx  ──▶  127.0.0.1:8080  ──▶  device-gateway container
   x-pilot-key          allowlists                              │
                        3 routes                                └──▶ mock-honeywell
                                                                     (compose network,
                                                                      no published port)
```

nginx is the only thing listening on a public port. The gateway container publishes to
loopback only.

This matters more than it looks. The gateway serves two surfaces on one port: the device
surface (`/health`, `/v1/turn`, the ack route) and an operator surface (test console at
`/`, `/v1/devices`, `/v1/rooms/…`, `/v1/admin/…`, `/v1/events`). Device auth is off here,
so `/v1/turn` accepts anyone who knows a `deviceId` — and the console at `/` lists every
`deviceId` on the property. Published directly, port 8443 would be an unauthenticated
room-control API with its own directory attached. nginx forwards three routes and 404s
the rest.

## Prerequisites

Two things this repo cannot do for you:

1. **Security group: inbound TCP 8443 from `0.0.0.0/0`,** and TCP 22 from wherever you
   deploy. A hub's egress IP is whatever the property's ISP hands it that morning, so
   8443 cannot be narrowed to a list of addresses. Port 22 should be narrowed.
2. The `woohoo-dev.pem` key, mode 600.

## Deploy

From `agentic/` on your machine:

```bash
tar --exclude=node_modules --exclude=.git --exclude='*.tar.gz' --exclude='*.pem' \
    -czf /tmp/woohoo-pilot.tar.gz .
scp -i KEY /tmp/woohoo-pilot.tar.gz          ubuntu@65.1.106.31:/tmp/
scp -i KEY deploy/pilot/.env                 ubuntu@65.1.106.31:/tmp/woohoo.env
scp -i KEY deploy/pilot/bootstrap.sh         ubuntu@65.1.106.31:/tmp/
ssh -i KEY ubuntu@65.1.106.31 'bash /tmp/bootstrap.sh'
```

`bootstrap.sh` is idempotent — it is the deploy command, not a one-time setup script. It
installs Docker and nginx if absent, writes `/etc/nginx/sites-available/woohoo` with the
pilot key substituted in, rebuilds the images, restarts the stack, and then checks the
edge from the inside: `/health` must answer 200, `/` and `/v1/devices` must answer 404,
and an unkeyed `/v1/turn` must answer 403. A deploy that silently exposed the console
fails those checks rather than going unnoticed.

## Verify from outside

```bash
curl -s http://65.1.106.31:8443/health
curl -s -o /dev/null -w '%{http_code}\n' http://65.1.106.31:8443/        # want 404
curl -s -X POST http://65.1.106.31:8443/v1/turn \
     -H 'content-type: application/json' \
     -H "x-pilot-key: $PILOT_KEY" \
     -d '{"deviceId":"d_412a","transcript":"turn up the volume"}'
```

## Provisioning a device

A hub sends the `device_id` it got at enrolment, and the platform resolves it to a room.
A device the catalog does not know gets a 404 and no turn runs. Admin routes are not on
the internet, so provision over the tunnel:

```bash
ssh -i KEY -L 9443:127.0.0.1:8080 ubuntu@65.1.106.31
# then, locally:
curl -X POST http://localhost:9443/v1/admin/rooms \
  -H "x-admin-token: $WOOHOO_ADMIN_TOKEN" -H 'content-type: application/json' \
  -d '{"roomNumber":"412","vendorRoomId":"2389","deviceId":"<device_id from the hub log>",
       "capabilities":[{"capability":"lighting.on_off","zones":["bedroom"]},
                       {"capability":"climate.setpoint","minC":18,"maxC":26}],
       "zones":["bedroom","living"]}'
```

The same tunnel serves the test console at `http://localhost:9443/`. The console already
expects to be reached that way, so its origin handling copes with being served from
localhost.

## Operating

```bash
cd /opt/woohoo/deploy/pilot
sudo docker compose -f compose.pilot.yml ps
sudo docker compose -f compose.pilot.yml logs -f --tail=100 device-gateway
sudo docker compose -f compose.pilot.yml restart device-gateway
sudo tail -f /var/log/nginx/woohoo.access.log
```

Container logs are capped at 10MB × 5 files. An uncapped json-file driver filling the
root volume is the usual way a small box dies.

## What this does not protect against

Stated plainly, because a security control that is overclaimed is worse than one that is
absent.

- **Traffic is cleartext.** Guest speech crosses the internet in plain HTTP. There is no
  domain on this host, so there is no certificate. The fix needs no purchase: point
  `65-1-106-31.sslip.io` at it, run certbot against port 80, and rebuild the APK with an
  `https://` base URL. Worth doing before any real guest speech goes through it.
- **`x-pilot-key` is not device identity.** It is compiled into the APK and extractable
  by anyone holding the APK. It stops port scanners reaching `/v1/turn`; it does not stop
  someone who has the build. Real device identity is `WOOHOO_REQUIRE_DEVICE_AUTH=1` plus a
  TLS terminator doing client-certificate verification, which needs certificates
  provisioned into the hubs — not yet done.
- **One host, no redundancy.** A reboot drops every hub until Docker restarts the stack
  (`restart: unless-stopped` handles that, but there is a gap).
- **The room is a mock.** `WOOHOO_ADAPTER=honeywell` points at `mock-honeywell`, not at a
  property controller. Lighting turns are answered honestly but nothing physical moves.

## Rolling a device back

Nothing here is required to be used. **Settings → Engine → Enterprise SmartFlow** puts a
device back on the engine it had before, per device, immediately, with no reinstall and no
deploy.
