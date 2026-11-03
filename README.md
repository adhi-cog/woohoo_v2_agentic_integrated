# woohoo_v2_agentic_integrated

The Woohoo V2 agentic platform and the woohoo-hub Android app in one tree, with the hub's
voice routing moved from Dialogflow/SmartFlow onto the agentic system.

```
agentic/   the platform: gateway, orchestrator, policy, vendor adapters, catalog
android/   the in-room hub app, with an agentic engine alongside the existing ones
```

## What changed, in one paragraph

The hub still captures the wake word and still runs Google Cloud ASR. What changes is
everything after the transcript. Instead of posting to an intent classifier and dispatching
whatever came back, the hub posts the transcript to the agentic gateway, which decides what
was meant, applies policy, executes room automation itself, and returns speech plus any work
only the device can do. The hub speaks, and executes that leftover work.

## The one thing to understand before reading the code

A turn response carries two arrays that mean opposite things.

| | What it is | What the hub does |
| --- | --- | --- |
| `calls[]` | Work the platform **already executed** server-side | Nothing. Report only |
| `deviceCommands[]` | Work only the device can do: alarms, media, volume, screens | Executes and acknowledges |

The legacy NLP response had a single `intentAction` the app was expected to run. Carrying
that habit across is how you get every light command firing twice: once on the server, once
on the device. `calls[]` is never dispatched.

## Why lighting is refused on the device

The hub carries a Philips Hue stack from its consumer days, and `hub.devices.lights.*`
intents still work. In a hotel the lights belong to the property's controller, reached
through a vendor adapter on the server. A lighting command executed on the hub would
therefore succeed against the wrong devices while the room never moved. The platform refuses
to issue those intents and the dispatcher refuses to run them, so a stale or tampered
response cannot slip one through.

## Running it locally

**1. The platform**

```bash
cd agentic
npm install
npm run gateway            # http://localhost:8443
```

With a real model and the production room data, see `agentic/docs/RUNBOOK-masterdb.md`:

```bash
GEMINI_API_KEY=... WOOHOO_ADAPTER=masterdb WOOHOO_SEED_PROPERTY=167 npm run gateway
```

**2. The app**

Point the build at the gateway. From an emulator, `10.0.2.2` is the host machine:

```properties
# android/local.properties
agenticBaseUrl=http://10.0.2.2:8443
```

```bash
cd android
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest          # dispatcher and wire-format tests
```

Then pick **Settings -> Engine -> Agentic Assistant** on the device. The option is hidden
when no gateway is compiled in, because an option that cannot work is worse than one that is
not offered.

**3. The device has to exist in the catalog**

The hub sends the `device_id` it was given at enrolment, and the platform resolves it to a
room. A device the catalog does not know gets a 404 and no turn runs. Provision one:

```bash
curl -X POST localhost:8443/v1/admin/rooms \
  -H "x-admin-token: $TOKEN" -H 'content-type: application/json' \
  -d '{"roomNumber":"412","vendorRoomId":"2389","deviceId":"<the hub device_id>",
       "capabilities":[{"capability":"lighting.on_off","zones":["bedroom"]}],
       "zones":["bedroom"]}'
```

The admin token is printed in the gateway's startup log.

## Rollout and rollback

The engine is a per-device preference, which is what makes this reversible without a deploy.
A room behaving oddly goes back to its previous engine from the Settings screen, by whoever
is standing in front of it. There is no fleet-wide switch to throw, deliberately.

## Security, before any rollout

Three things in the Android app need action and are excluded from this repository:

1. **`app/src/main/res/raw/credential.json`** is a Google Cloud service-account private key.
   It was committed to the original repository and ships inside every APK, so anyone who
   downloads the app can extract it and bill Speech API calls to the project. Rotate it, and
   prefer minting short-lived tokens from a backend over shipping a key in a client.
   Template at `android/credential.json.example`.
2. **`app/woohoohub-keystore` and `app/platform.keystore`** are signing keys, with the
   password inline in `app/build.gradle` as `woohoo`. Anyone holding them can sign an APK as
   this app. Move them to CI secrets and rotate.
3. **Release builds are signed with `platform.keystore`, alias `woohoodebugkey`.** A debug
   named key signing production builds is worth fixing before the next release.

Separately, the app sets `usesCleartextTraffic="true"` application-wide. That is required for
plain HTTP to a laptop during development and should be narrowed to a debug-only network
security config before shipping.

## Device identity

In production the hub authenticates with mTLS. The TLS terminator verifies the client
certificate and forwards its serial in `X-Client-Cert-Serial`, which the gateway compares
against the device record; a `deviceId` in the body is an addressing field and never proof of
access. The app deliberately sends no credential of its own, because a token the app can set
is a token an attacker can set. Enforcement is off until `WOOHOO_REQUIRE_DEVICE_AUTH` is set,
and `GET /health` reports which mode is live so a deployment cannot quietly run open.

## Documentation

| Document | What it covers |
| --- | --- |
| `agentic/docs/openapi.yaml` | Both API surfaces, tagged by who implements them |
| `agentic/docs/Woohoo_V2_Integration_Review_Response.docx` | Responses to the integration review |
| `agentic/docs/Woohoo-V2-Integration-Guide.docx` | Architecture, APIs, storage, sequences |
| `agentic/docs/RUNBOOK-masterdb.md` | Driving real production room data locally |
