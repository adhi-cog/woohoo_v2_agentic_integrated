import { readFileSync } from 'node:fs';
import { join } from 'node:path';
import { beforeEach, describe, expect, it } from 'vitest';
import {
  HUB_INTENTS,
  HUB_TOOLS,
  isHubTool,
  toolSurfaceFor,
  type CapabilityManifest,
  type Scope,
} from '@woohoo/contracts';
import { DeviceCommandError, DeviceCommandService } from './device-commands.js';

/**
 * The hub command contract - integration review R2, R3.
 *
 * Two classes of failure are worth tests here. The first is the one that silently breaks a
 * room: a lighting command addressed to the hub, which still carries its consumer-era local
 * lighting stack, so it would succeed against the wrong devices while the hotel's own
 * controller never hears about it. The second is drift: this vocabulary is a copy of the
 * hub's, and a copy that nobody checks is a copy that goes stale.
 */

const SCOPE: Scope = {
  tenantId: 't_demo',
  propertyId: 'p_blr_whitefield',
  roomId: 'r_412',
  deviceId: 'd_412a',
};

let svc: DeviceCommandService;

beforeEach(() => {
  svc = new DeviceCommandService();
});

function issue(intent: string, turnId = 'turn_1') {
  return svc.issue({ scope: SCOPE, turnId, tool: 'hub.media', intent });
}

describe('room devices are never addressed through the hub', () => {
  it.each([
    'hub.devices.lights.all.toggle.on',
    'hub.devices.lights.all.toggle.off',
    'hub.devices.lights.all.brightness',
    'hub.devices.lights.scene',
    'hub.devices.lights',
  ])('refuses %s', (intent) => {
    expect(() => issue(intent)).toThrow(DeviceCommandError);
    try {
      issue(intent);
    } catch (e) {
      expect((e as DeviceCommandError).code).toBe('FORBIDDEN_INTENT');
      // The reason has to explain the hazard, because the next person to read it will be
      // wondering why the hub cannot do something it visibly can.
      expect((e as DeviceCommandError).message).toMatch(/property controller|wrong system/i);
    }
  });

  it('keeps lighting out of the hub vocabulary entirely', () => {
    expect(HUB_INTENTS.filter((i) => i.includes('lights'))).toEqual([]);
  });
});

describe('an intent the hub cannot dispatch is refused before it is sent', () => {
  it.each(['hub.skills.teleport', 'lighting.set', '', 'hub.skills.alarms.destroy'])(
    'refuses %s',
    (intent) => {
      expect(() => issue(intent)).toThrow(DeviceCommandError);
    },
  );

  it('reports it as unknown rather than forbidden', () => {
    try {
      issue('hub.skills.teleport');
    } catch (e) {
      expect((e as DeviceCommandError).code).toBe('UNKNOWN_INTENT');
    }
  });
});

describe('a valid command is minted so the hub can deduplicate it', () => {
  it('returns the intent and an expiry', () => {
    const c = issue('hub.skills.alarms.add');
    expect(c.intent).toBe('hub.skills.alarms.add');
    expect(Date.parse(c.expiresAt)).toBeGreaterThan(Date.now());
  });

  it('derives the id from the turn, so a retried turn repeats it rather than duplicating', () => {
    const a = new DeviceCommandService().issue({
      scope: SCOPE, turnId: 'turn_8f21c', tool: 'hub.media', intent: 'hub.skills.music.play',
    });
    const b = new DeviceCommandService().issue({
      scope: SCOPE, turnId: 'turn_8f21c', tool: 'hub.media', intent: 'hub.skills.music.play',
    });
    expect(a.commandId).toBe(b.commandId);
    expect(a.commandId).toContain('turn_8f21c');
  });

  it('gives two commands in one turn distinct ids', () => {
    const a = issue('hub.skills.music.play');
    const b = issue('hub.controls.system.volume.up');
    expect(a.commandId).not.toBe(b.commandId);
  });

  it('carries slots through untouched, since they are the hub\'s vocabulary not ours', () => {
    const c = svc.issue({
      scope: SCOPE, turnId: 'turn_1', tool: 'hub.reminders',
      intent: 'hub.skills.alarms.add',
      slots: { time: '07:00', label: 'wake up', ordinal: 2 },
    });
    expect(c.slots).toEqual({ time: '07:00', label: 'wake up', ordinal: 2 });
  });
});

describe('acknowledgement records what the device actually did', () => {
  it('records the first acknowledgement', () => {
    const c = issue('hub.skills.timer.set');
    const row = svc.ack(c.commandId, { status: 'applied' });
    expect(row?.ack?.status).toBe('applied');
  });

  it('keeps the first and ignores a retry, so a late expiry cannot overwrite a success', () => {
    const c = issue('hub.skills.timer.set');
    svc.ack(c.commandId, { status: 'applied' });
    svc.ack(c.commandId, { status: 'expired' });
    expect(svc.get(c.commandId)?.ack?.status).toBe('applied');
  });

  it('returns undefined for an id it never issued', () => {
    expect(svc.ack('dc_nonexistent_1', { status: 'applied' })).toBeUndefined();
  });

  it('lists outstanding commands for a room until they are acknowledged', () => {
    const c = issue('hub.skills.music.play');
    expect(svc.outstandingFor('r_412')).toHaveLength(1);
    svc.ack(c.commandId, { status: 'applied' });
    expect(svc.outstandingFor('r_412')).toHaveLength(0);
  });

  it('does not leak one room\'s commands into another', () => {
    issue('hub.skills.music.play');
    expect(svc.outstandingFor('r_999')).toHaveLength(0);
  });
});

describe('expiry', () => {
  it('drops out of the outstanding list once expired', () => {
    let now = 1_000_000;
    const s = new DeviceCommandService({ ttlMs: 5_000, now: () => now });
    s.issue({ scope: SCOPE, turnId: 't', tool: 'hub.media', intent: 'hub.skills.music.play' });
    expect(s.outstandingFor('r_412')).toHaveLength(1);
    now += 6_000;
    expect(s.outstandingFor('r_412')).toHaveLength(0);
  });
});

describe('the hub tool surface', () => {
  const manifest: CapabilityManifest = {
    version: '1.0',
    zones: ['bedroom'],
    capabilities: [{ capability: 'lighting.on_off', zones: ['bedroom'] }],
    scenes: [],
  };

  it('is offered by default, because the hub is the device in front of the guest', () => {
    const names = toolSurfaceFor(manifest, { consentPreferences: false }).map((t) => t.name);
    expect(names).toContain('hub.media');
    expect(names).toContain('hub.show');
  });

  it('can be withheld from a room fronted by something other than a hub', () => {
    const names = toolSurfaceFor(manifest, { consentPreferences: false, hubTools: false })
      .map((t) => t.name);
    expect(names.filter(isHubTool)).toEqual([]);
    // Server-side tools are unaffected.
    expect(names).toContain('lighting.set');
  });

  it('keeps the total surface within the documented budget', () => {
    const names = toolSurfaceFor(
      { version: '1.0', zones: ['bedroom'], capabilities: [], scenes: [] },
      { consentPreferences: true },
    );
    // tools.ts states the surface is capped at roughly 25. Five grouped hub tools is what
    // keeps ninety hub intents inside that budget.
    expect(names.length).toBeLessThanOrEqual(25);
  });

  it('only advertises intents the dispatcher will accept', () => {
    for (const tool of HUB_TOOLS) {
      const shape = (tool.input as { shape?: Record<string, unknown> }).shape;
      const intentSchema = shape?.intent as { options?: string[] } | undefined;
      expect(intentSchema?.options, tool.name + ' has no intent enum').toBeDefined();
      for (const intent of intentSchema!.options!) {
        expect(
          (HUB_INTENTS as readonly string[]).includes(intent),
          tool.name + ' advertises ' + intent + ', which the dispatcher would refuse',
        ).toBe(true);
      }
    }
  });
});

/**
 * The anti-drift test. This vocabulary is a copy of the Android app's, and the app is the
 * thing that actually has to handle each name. If someone renames an intent there, every
 * command carrying the old name starts failing silently on the device, which is exactly the
 * failure this cannot be allowed to have.
 */
describe('the vocabulary matches the Android app', () => {
  const androidRoot = join(
    import.meta.dirname, '..', '..', '..', '..', '..',
    'android', 'app', 'src', 'main', 'java', 'com', 'sb', 'woohoo', 'ui', 'main',
  );

  function androidIntentNames(): Set<string> {
    const categories = readFileSync(join(androidRoot, 'Categories.java'), 'utf8');
    const repository = readFileSync(join(androidRoot, 'MainRepository.java'), 'utf8');
    const found = new Set<string>();
    // Constants in Categories.java, plus the names MainRepository switches on directly for
    // the screens it opens without going through the intent registry.
    for (const src of [categories, repository]) {
      for (const m of src.matchAll(/"((?:hub|FnB)\.[A-Za-z_.]+)"/g)) found.add(m[1]!);
    }
    return found;
  }

  it('finds the Android source, so this test cannot pass vacuously', () => {
    const names = androidIntentNames();
    expect(names.size).toBeGreaterThan(100);
    expect(names.has('hub.skills.alarms.add')).toBe(true);
  });

  it.each(HUB_INTENTS)('%s exists in the Android app', (intent) => {
    expect(androidIntentNames().has(intent)).toBe(true);
  });
});
