# Testing the agentic build

Send this file with the APK. The app needs a gateway to talk to, and without one every voice
command fails in a way that looks like the app is broken.

## What this build is

The hub with its voice routing moved onto the agentic platform. Wake word and speech
recognition are unchanged. What changed is what happens to the transcript: instead of an
intent classifier, it goes to the agentic gateway, which decides what was meant, applies
policy, drives the room itself, and sends back speech plus anything only the device can do.

`v2.3.3-agentic-pilot`, versionCode 233. One above the 232 on current test devices, so it
installs over them and the next official build still supersedes it.

## 1. Run the gateway

It has to run somewhere the device can reach. On your machine:

```bash
git clone https://github.com/adhi-cog/woohoo_v2_agentic_integrated.git
cd woohoo_v2_agentic_integrated/agentic
npm install
npm run gateway            # listens on 8443
```

It prints an admin token at startup. Keep the window open.

Check it is up, and note the address the device will use:

```bash
curl localhost:8443/health
```

## 2. Install

```bash
adb install -r wh-release_233.apk
```

If that fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, the device has a build signed with a
different key. Uninstall first, which also clears onboarding:

```bash
adb uninstall com.sb.woohoo
adb install wh-release_233.apk
```

## 3. Point the app at your gateway

**Settings -> Engine**. Pick **Agentic Assistant**, then set **Agentic gateway URL**:

| Device | URL |
| --- | --- |
| Emulator, gateway on the same machine | `http://10.0.2.2:8443` |
| Physical hub, gateway on a machine on the same network | `http://<that machine's LAN IP>:8443` |
| Deployed gateway | its URL |

`10.0.2.2` is the emulator's alias for its host. On a physical device it means nothing, which
is the usual reason a hub "cannot reach the assistant".

For a LAN IP, allow port 8443 through the host firewall, and confirm from the device's network
that `http://<ip>:8443/health` answers.

## 4. The device has to exist in the catalog

The hub sends the `device_id` it was given at enrolment and the platform resolves it to a
room. A device the catalog does not know gets a 404 and no turn runs at all.

Find the id in the logs after one attempt:

```bash
adb logcat | grep -i agentic
```

Then provision it, using the admin token the gateway printed:

```bash
curl -X POST http://localhost:8443/v1/admin/rooms \
  -H "x-admin-token: <token>" -H 'content-type: application/json' \
  -d '{"roomNumber":"412","vendorRoomId":"2389","deviceId":"<device_id from the log>",
       "capabilities":[{"capability":"lighting.on_off","zones":["bedroom"]},
                       {"capability":"climate.setpoint","minC":18,"maxC":26}],
       "zones":["bedroom","living"]}'
```

## 5. What to try

| Say | What should happen |
| --- | --- |
| "turn up the volume" | Volume changes on the device |
| "set an alarm for 7 am" | Alarm screen, alarm at 07:00 |
| "play some music" | Music starts |
| "what time is it" | Spoken time |
| "turn on the bedroom light" | Request goes to the room controller, not the device |

The last one is the important one. In this build the hub does **not** drive lights itself: the
platform does, through the property's controller. If you are running against the simulator
rather than real hardware, nothing physical moves and that is correct.

Watch the gateway window; every turn prints there.

## What you will hear, and why

**"I have asked for that, give it a moment."** Correct, not a bug. The controller accepted the
command and there is no way to read the device back, so the assistant does not claim it is
done. A confident "Done" over a command that never landed costs more trust than an honest
wait.

**"That room does not have ..."** Correct. The room's capability manifest does not include it.

**"I could not reach the assistant just then."** The gateway is unreachable. Check the URL in
Settings, that the gateway is running, and the firewall.

## Switching back

**Settings -> Engine -> Enterprise SmartFlow**. Immediate, per device, no reinstall. If
anything behaves oddly, switch back first and report second.

## Reporting a problem

Useful report: what you said, what it said, what the gateway window printed, and:

```bash
adb logcat -d > logcat.txt
```

The gateway's own output matters more than the app log, because the decision happened there.
