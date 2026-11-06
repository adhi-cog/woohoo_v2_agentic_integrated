# Testing the agentic build

Send this file with the APK.

## What this build is

The hub with its voice routing moved onto the agentic platform. Wake word and speech
recognition are unchanged. What changed is what happens to the transcript: instead of an
intent classifier, it goes to the agentic gateway, which decides what was meant, applies
policy, drives the room itself, and sends back speech plus anything only the device can do.

`v2.3.4-agentic-hosted`, versionCode 234. It installs over the 232 on current test devices
and over the earlier 233 pilot build, and the next official build still supersedes it.

**There is nothing to run.** This build points at a hosted gateway at
`http://65.1.106.31:8443`, compiled in. The earlier 233 build needed you to start a
gateway on your own machine; this one does not.

## 1. Install

```bash
adb install -r wh-release_234.apk
```

If that fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, the device has a build signed with
a different key. Uninstall first, which also clears onboarding:

```bash
adb uninstall com.sb.woohoo
adb install wh-release_234.apk
```

## 2. Check the device can reach the gateway

From the device's network:

```bash
curl http://65.1.106.31:8443/health
```

If that does not answer, the device's network blocks outbound 8443 — some hotel and office
networks pass only 80 and 443. Say so rather than working around it; that is a real finding
about where this can be deployed.

You do not need to touch **Settings → Engine** unless you want to point the device at a
different gateway. It already selects the agentic engine and knows the URL.

## 3. The device has to exist in the catalog

The hub sends the `device_id` it was given at enrolment and the platform resolves it to a
room. A device the catalog does not know gets a 404 and no turn runs at all.

Find the id in the logs after one attempt:

```bash
adb logcat | grep -i agentic
```

Then send it to Adhithya to be provisioned — admin routes are deliberately not exposed on
the public internet, so this is not something you can do with a curl from your laptop.

## 4. What to try

| Say | What should happen |
| --- | --- |
| "turn up the volume" | Volume changes on the device |
| "set an alarm for 7 am" | Alarm screen, alarm at 07:00 |
| "play some music" | Music starts |
| "what time is it" | Spoken time |
| "turn on the bedroom light" | Request goes to the room controller, not the device |

The last one is the important one. In this build the hub does **not** drive lights itself:
the platform does, through the property's controller. The pilot host runs a simulated
controller, so nothing physical moves and that is correct.

## What you will hear, and why

**"I have asked for that, give it a moment."** Correct, not a bug. The controller accepted
the command and there is no way to read the device back, so the assistant does not claim it
is done. A confident "Done" over a command that never landed costs more trust than an
honest wait.

**"That room does not have ..."** Correct. The room's capability manifest does not include
it.

**"I could not reach the assistant just then."** The device could not reach
`65.1.106.31:8443`. Check step 2 before anything else.

## Switching back

**Settings → Engine → Enterprise SmartFlow**. Immediate, per device, no reinstall. If
anything behaves oddly, switch back first and report second.

## Reporting a problem

Useful report: what you said, what it said, and:

```bash
adb logcat -d > logcat.txt
```

Include the rough time — the gateway's own log is the record of what was decided, and it is
matched up by timestamp.

## Two things to know about the hosted gateway

- **Traffic is cleartext HTTP.** There is no domain on that host, so there is no
  certificate. Fine for testing with scripted phrases; it should not carry real guest
  speech. A TLS front end is the next piece of work.
- **It is a shared pilot host, not production.** One box, no redundancy, a simulated room
  controller, and a request key compiled into the APK rather than real device certificates.
  Treat anything you say to it as visible to the team.

## Appendix: running your own gateway instead

Still supported, and the only way to see the gateway's own decision log live.

```bash
git clone https://github.com/adhi-cog/woohoo_v2_agentic_integrated.git
cd woohoo_v2_agentic_integrated/agentic
npm install
npm run gateway            # listens on 8443, prints an admin token
```

Then **Settings → Engine → Agentic gateway URL**:

| Device | URL |
| --- | --- |
| Emulator, gateway on the same machine | `http://10.0.2.2:8443` |
| Physical hub, gateway on the same network | `http://<that machine's LAN IP>:8443` |

`10.0.2.2` is the emulator's alias for its host. On a physical device it means nothing,
which is the usual reason a hub "cannot reach the assistant". For a LAN IP, allow 8443
through the host firewall.

A local gateway asks for no request key, and the one the app sends is ignored. Provision
the device against it directly:

```bash
curl -X POST http://localhost:8443/v1/admin/rooms \
  -H "x-admin-token: <token the gateway printed>" -H 'content-type: application/json' \
  -d '{"roomNumber":"412","vendorRoomId":"2389","deviceId":"<device_id from the log>",
       "capabilities":[{"capability":"lighting.on_off","zones":["bedroom"]},
                       {"capability":"climate.setpoint","minC":18,"maxC":26}],
       "zones":["bedroom","living"]}'
```
