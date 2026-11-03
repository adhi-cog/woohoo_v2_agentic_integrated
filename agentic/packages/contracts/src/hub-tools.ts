import { z } from 'zod';
import { RiskTier } from './tiers.js';
import type { ToolDefinition, ToolMeta } from './tools.js';

/**
 * Tools whose execution happens on the Android hub - integration review R2, R3.
 *
 * Everything else in the tool surface is executed server-side and reported back. These are
 * the opposite: the platform cannot set an alarm on a device or change its volume, so the
 * agent's "execution" is to hand the hub a command and the hub does the work.
 *
 * Grouped into five tools rather than one per skill. The tool surface is capped at roughly
 * twenty-five by design, and the hub alone has ninety dispatchable intents; exposing them
 * individually would quadruple the surface and measurably degrade selection. Grouping by
 * user-facing domain keeps each enum small enough to read, keeps the total within budget, and
 * lets risk tiers differ - placing a call is not volume control.
 *
 * The `intent` enums are the hub's own names. They are closed: a model that can invent one
 * invents a name no fragment handles, and the guest hears a confirmation for something that
 * never happened.
 */

const meta = (m: Partial<ToolMeta> & { tier: RiskTier }): ToolMeta => ({
  idempotent: false,
  verifyRead: false,
  requiresConfirmation: false,
  spendCapApplies: false,
  // Generous next to a vendor call: this is only the time to mint a command and return it.
  // The hub's own execution is acknowledged separately and is not on the turn's critical path.
  timeoutMs: 1200,
  untrustedResult: false,
  ...m,
});

export const HUB_MEDIA_INTENTS = [
  'hub.controls.player.play',
  'hub.controls.player.pause',
  'hub.controls.player.resume',
  'hub.controls.player.next',
  'hub.controls.player.previous',
  'hub.controls.player.stop',
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
  'hub.skills.relaxation_music.show',
  'hub.skills.relaxation_music.play',
  'hub.skills.relaxation_music.pause',
  'hub.skills.relaxation_music.resume',
  'hub.skills.relaxation_music.next',
  'hub.skills.relaxation_music.previous',
] as const;

export const HUB_REMINDER_INTENTS = [
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
] as const;

export const HUB_SETTING_INTENTS = [
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
  'hub.controls.navigation.home',
  'hub.controls.RemoteAlertModel.dismiss',
] as const;

export const HUB_SHOW_INTENTS = [
  'hub.skills.faq',
  'hub.skills.feedback',
  'hub.skills.hotelinfo',
  'hub.skills.housekeeping',
  'hub.skills.service.book',
  'hub.skills.music.choose_source',
  'FnB.Show',
  'hub.skills.restaurants.show',
  'hub.skills.restaurants.book_table',
  'hub.skills.buy_sim.show',
  'hub.skills.buy_sim.select_package',
  'hub.skills.translate',
  'hub.skills.weather.search',
  'hub.skills.weather.show',
  'hub.skills.weather.condition',
  'hub.skills.weather.outfit',
  'hub.skills.camera.selfie',
  'hub.skills.qna.currenttime',
  'hub.skills.qna.currentdate',
  'hub.skills.qna.fallback',
  'hub.skills.qna.stop',
  'hub.communication.contacts.show',
] as const;

export const HUB_CALL_INTENTS = ['hub.communication.calls.phone.contact'] as const;

const enumOf = (values: readonly string[]) =>
  z.enum(values as unknown as [string, ...string[]]);

/** Free-form parameters in the hub's own slot vocabulary, passed through untouched. */
const slots = z
  .record(z.string(), z.unknown())
  .default({})
  .describe('Parameters for the hub, such as time, label, ordinal, query or contact name.');

export const HUB_TOOLS: ToolDefinition[] = [
  {
    name: 'hub.media',
    description:
      'Play or control media on the in-room hub: music, video, news and relaxation audio, plus '
      + 'transport controls for whatever is currently playing. Use this for anything the guest '
      + 'wants to hear or watch on the device. Not for room devices such as lights or curtains.',
    input: z.object({ intent: enumOf(HUB_MEDIA_INTENTS), slots }).strict(),
    meta: meta({ tier: RiskTier.T1 }),
  },
  {
    name: 'hub.reminders',
    description:
      'Alarms, timers, reminders and to-do items, which live on the hub itself. Use for "wake me '
      + 'at seven", "set a timer for ten minutes", "remind me to call reception".',
    input: z.object({ intent: enumOf(HUB_REMINDER_INTENTS), slots }).strict(),
    meta: meta({ tier: RiskTier.T1 }),
  },
  {
    name: 'hub.device_settings',
    description:
      'Settings of the hub device itself: its speaker volume, its screen brightness, night '
      + 'mode, Bluetooth, returning to the home screen, dismissing an alert. This is the hub\'s '
      + 'own hardware, never the room\'s lighting or climate.',
    input: z.object({ intent: enumOf(HUB_SETTING_INTENTS), slots }).strict(),
    meta: meta({ tier: RiskTier.T1 }),
  },
  {
    name: 'hub.show',
    description:
      'Open a screen on the hub or run an on-device informational skill: the food and beverage '
      + 'menu, housekeeping, hotel information, FAQ, feedback, restaurants, SIM packages, '
      + 'translation, weather, the camera, the time or date, contacts.',
    input: z.object({ intent: enumOf(HUB_SHOW_INTENTS), slots }).strict(),
    meta: meta({ tier: RiskTier.T0 }),
  },
  {
    name: 'hub.call',
    description:
      'Place a call from the hub to a named hotel contact. Confirm who is being called before '
      + 'using this.',
    input: z.object({ intent: enumOf(HUB_CALL_INTENTS), slots }).strict(),
    // Higher than the rest: a call reaches a person, and a wrong one reaches the wrong person.
    meta: meta({ tier: RiskTier.T2 }),
  },
];

/** Every tool in this file, for the orchestrator to recognise hub-executed calls. */
export const HUB_TOOL_NAMES: ReadonlySet<string> = new Set(HUB_TOOLS.map((t) => t.name));

export function isHubTool(name: string): boolean {
  return HUB_TOOL_NAMES.has(name);
}
