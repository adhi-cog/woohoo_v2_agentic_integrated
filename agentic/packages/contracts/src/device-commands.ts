import { z } from 'zod';

/**
 * Commands the agent hands back to the Android hub to execute - integration review R2, R3.
 *
 * Two arrays leave a turn and they mean opposite things. `calls[]` is a REPORT: the work has
 * already happened server-side and re-dispatching it fires the vendor command twice, which is
 * the single sharpest hazard in this integration. `deviceCommands[]` is the opposite: work
 * that has NOT happened, because only the hub can do it - there is no server-side way to set
 * an alarm on a device or change its volume.
 *
 * The vocabulary below is deliberately the hub's EXISTING intent names, lifted from
 * Categories.java. That is what makes this an integration rather than a rewrite: every name
 * here already resolves through IntentProcessor's registry to a fragment that works today, so
 * alarms, music, video and the rest keep functioning when the voice engine is replaced.
 *
 * It is a closed enum for a reason. The model proposes these, and a model that can invent an
 * intent name invents one no fragment handles - the guest hears a confident confirmation and
 * nothing happens. Anything not on this list is rejected before it reaches the device.
 *
 * Note what is absent: `hub.devices.lights.*`. The hub's own lighting intents drive its local
 * discovery stack, and in a hotel the lights belong to the property's controller, reached
 * through a vendor adapter on the server. Emitting a lighting device-command would route a
 * guest's "turn off the light" to the wrong system entirely, so lighting is not expressible
 * here at all. There is a test for it.
 */

/** Hub actions that navigate or control the device itself. */
export const HUB_CONTROL_INTENTS = [
  'hub.controls.navigation.home',
  'hub.controls.player.play',
  'hub.controls.player.pause',
  'hub.controls.player.resume',
  'hub.controls.player.next',
  'hub.controls.player.previous',
  'hub.controls.player.stop',
  'hub.controls.system.volume.up',
  'hub.controls.system.volume.down',
  'hub.controls.system.volume.set',
  'hub.controls.system.volume.mute',
  'hub.controls.system.volume.unmute',
  'hub.controls.system.brightness.up',
  'hub.controls.system.brightness.down',
  'hub.controls.system.brightness.set',
  'hub.controls.system.night_mode.on',
  'hub.controls.system.night_mode.off',
  'hub.controls.system.bluetooth.on',
  'hub.controls.system.bluetooth.off',
  'hub.controls.system.bluetooth.open',
  'hub.controls.RemoteAlertModel.dismiss',
] as const;

/** On-device skills with no server-side equivalent. */
export const HUB_SKILL_INTENTS = [
  'hub.skills.alarms.show',
  'hub.skills.alarms.add',
  'hub.skills.alarms.delete.all',
  'hub.skills.alarms.delete.ordinal',
  'hub.skills.alarms.snooze',
  'hub.skills.timer.show',
  'hub.skills.timer.set',
  'hub.skills.timer.stop',
  'hub.skills.reminders.show',
  'hub.skills.reminders.add',
  'hub.skills.reminders.delete.all',
  'hub.skills.reminders.delete.title',
  'hub.skills.reminders.edit',
  'hub.skills.todos.show',
  'hub.skills.todos.add',
  'hub.skills.todos.delete.all',
  'hub.skills.todos.delete.ordinal',
  'hub.skills.qna.fallback',
  'hub.skills.qna.stop',
  'hub.skills.qna.currenttime',
  'hub.skills.qna.currentdate',
  'hub.skills.music.search',
  'hub.skills.music.play',
  'hub.skills.music.playlist',
  'hub.skills.music.controls.play',
  'hub.skills.music.controls.pause',
  'hub.skills.music.controls.next',
  'hub.skills.music.controls.previous',
  'hub.skills.music.controls.repeat',
  'hub.skills.music.controls.stop',
  'hub.skills.videos.search',
  'hub.skills.videos.search.autoplay',
  'hub.skills.videos.launch',
  'hub.skills.videos.launch.autoplay',
  'hub.skills.videos.play.ordinal',
  'hub.skills.videos.controls.play',
  'hub.skills.videos.controls.pause',
  'hub.skills.videos.controls.next',
  'hub.skills.videos.controls.previous',
  'hub.skills.videos.controls.back',
  'hub.skills.news.launch',
  'hub.skills.news.search',
  'hub.skills.news.location',
  'hub.skills.news.player.play',
  'hub.skills.news.player.pause',
  'hub.skills.news.player.stop',
  'hub.skills.news.player.next',
  'hub.skills.news.player.previous',
  'hub.skills.weather.search',
  'hub.skills.weather.show',
  'hub.skills.weather.condition',
  'hub.skills.weather.outfit',
  'hub.skills.camera.selfie',
  'hub.skills.translate',
  'hub.skills.relaxation_music.show',
  'hub.skills.relaxation_music.play',
  'hub.skills.relaxation_music.pause',
  'hub.skills.relaxation_music.resume',
  'hub.skills.relaxation_music.next',
  'hub.skills.relaxation_music.previous',
  'hub.skills.buy_sim.show',
  'hub.skills.buy_sim.select_package',
  'hub.skills.restaurants.show',
  'hub.skills.restaurants.book_table',
  'hub.communication.contacts.show',
  'hub.communication.calls.phone.contact',
] as const;

/**
 * Screens the hub opens directly from the NLP response rather than through the intent
 * registry. MainRepository.handleNLPResponse special-cases each of these, so they reach a
 * different dispatch path on the device and have to be listed separately to be reachable.
 */
export const HUB_SCREEN_INTENTS = [
  'hub.skills.faq',
  'hub.skills.feedback',
  'hub.skills.hotelinfo',
  'hub.skills.housekeeping',
  'hub.skills.service.book',
  'hub.skills.music.choose_source',
  'FnB.Show',
] as const;

export const HUB_INTENTS = [
  ...HUB_CONTROL_INTENTS,
  ...HUB_SKILL_INTENTS,
  ...HUB_SCREEN_INTENTS,
] as const;

export const HubIntentSchema = z.enum(
  HUB_INTENTS as unknown as [string, ...string[]],
);
export type HubIntent = (typeof HUB_INTENTS)[number];

/**
 * Intents the hub understands but must never be told to run, with the reason. Kept as data
 * rather than a comment so the guard can cite it and a test can assert on it.
 */
export const FORBIDDEN_HUB_INTENT_PREFIXES: ReadonlyArray<{ prefix: string; reason: string }> = [
  {
    prefix: 'hub.devices.lights',
    reason:
      'Room lighting belongs to the property controller and is applied server-side through a '
      + 'vendor adapter. The hub\'s lighting intents drive its own local discovery stack, so '
      + 'dispatching one would act on the wrong system.',
  },
];

export const DeviceCommandSchema = z.object({
  /**
   * Idempotency key. The hub may receive the same command twice across a retry or a
   * reconnect, and executing it twice has to be safe.
   */
  commandId: z.string().min(1),
  intent: HubIntentSchema,
  /** Parameters, in the hub's existing slot vocabulary. Passed through unchanged. */
  slots: z.record(z.string(), z.unknown()).default({}),
  /**
   * Spoken alongside this command, when the command itself is the answer. Usually empty,
   * because the turn's own `speech` already carries what the guest hears.
   */
  speech: z.string().optional(),
  /**
   * After this, the hub must discard the command rather than run it. A volume change that
   * arrives ninety seconds late because a network stalled is not what the guest asked for.
   */
  expiresAt: z.string().datetime(),
});
export type DeviceCommand = z.infer<typeof DeviceCommandSchema>;

export const DeviceCommandAckSchema = z.object({
  status: z.enum(['applied', 'failed', 'expired', 'unsupported']),
  detail: z.string().optional(),
  /** Hub clock, for measuring how long the device actually took. */
  at: z.string().datetime().optional(),
});
export type DeviceCommandAck = z.infer<typeof DeviceCommandAckSchema>;

/** Default validity window. Long enough for a slow network, short enough to stay relevant. */
export const DEVICE_COMMAND_TTL_MS = 60_000;

export function isForbiddenHubIntent(intent: string): { forbidden: boolean; reason?: string } {
  for (const f of FORBIDDEN_HUB_INTENT_PREFIXES) {
    if (intent === f.prefix || intent.startsWith(f.prefix + '.')) {
      return { forbidden: true, reason: f.reason };
    }
  }
  return { forbidden: false };
}

/** True when `intent` is dispatchable on the hub. Anything else must not be sent. */
export function isHubIntent(intent: string): intent is HubIntent {
  return (HUB_INTENTS as readonly string[]).includes(intent);
}
