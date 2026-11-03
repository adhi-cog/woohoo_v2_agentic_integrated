import {
  DEVICE_COMMAND_TTL_MS,
  isForbiddenHubIntent,
  isHubIntent,
  type DeviceCommand,
  type DeviceCommandAck,
  type Scope,
} from '@woohoo/contracts';

/**
 * Issues and tracks commands the Android hub executes - integration review R2, R3.
 *
 * The hub is the only executor for alarms, media, volume and screens, so for those the
 * platform's job ends at handing over a well-formed instruction. Three properties make that
 * safe to do:
 *
 *  - The intent is validated against a closed vocabulary before it leaves. A model that
 *    invents a name produces a command no fragment handles, and the guest hears a confident
 *    confirmation for something that never ran.
 *  - Room-device intents are refused outright. The hub has its own lighting stack from its
 *    consumer days; in a hotel the lights belong to the property controller, so a lighting
 *    command sent to the hub would act on the wrong system while looking correct.
 *  - The id is derived from the turn, not random. A hub that retries after a dropped response
 *    presents the same id, and the second execution is suppressed rather than doubling it.
 *
 * Acknowledgements are recorded but never awaited. The turn has already been spoken by the
 * time the hub acts, and blocking a guest-facing response on a device round trip would trade
 * a real latency cost for information nobody is waiting on.
 */

export interface IssuedCommand {
  command: DeviceCommand;
  scope: Scope;
  turnId: string;
  tool: string;
  issuedAt: number;
  ack?: DeviceCommandAck & { receivedAt: number };
}

export class DeviceCommandError extends Error {
  constructor(
    message: string,
    readonly code: 'UNKNOWN_INTENT' | 'FORBIDDEN_INTENT',
  ) {
    super(message);
    this.name = 'DeviceCommandError';
  }
}

export class DeviceCommandService {
  private readonly issued = new Map<string, IssuedCommand>();
  /** Per-turn counter, so two commands in one turn get distinct deterministic ids. */
  private readonly seqByTurn = new Map<string, number>();

  constructor(
    private readonly opts: { ttlMs?: number; capacity?: number; now?: () => number } = {},
  ) {}

  private now(): number {
    return this.opts.now?.() ?? Date.now();
  }

  /**
   * Validate and mint a command. Throws rather than returning a soft failure: an invalid
   * intent is a bug in the tool surface or the model's output, not a condition to paper over.
   */
  issue(args: {
    scope: Scope;
    turnId: string;
    tool: string;
    intent: string;
    slots?: Record<string, unknown>;
    speech?: string;
  }): DeviceCommand {
    const forbidden = isForbiddenHubIntent(args.intent);
    if (forbidden.forbidden) {
      throw new DeviceCommandError(forbidden.reason ?? 'Intent is not permitted on the hub.', 'FORBIDDEN_INTENT');
    }
    if (!isHubIntent(args.intent)) {
      throw new DeviceCommandError(
        'No hub action named ' + args.intent + '. It is not in the device vocabulary.',
        'UNKNOWN_INTENT',
      );
    }

    const seq = (this.seqByTurn.get(args.turnId) ?? 0) + 1;
    this.seqByTurn.set(args.turnId, seq);

    const ttl = this.opts.ttlMs ?? DEVICE_COMMAND_TTL_MS;
    const command: DeviceCommand = {
      // Derived, not random: a retried turn mints the same id and the hub can deduplicate.
      commandId: 'dc_' + args.turnId + '_' + seq,
      intent: args.intent,
      slots: args.slots ?? {},
      ...(args.speech ? { speech: args.speech } : {}),
      expiresAt: new Date(this.now() + ttl).toISOString(),
    };

    this.issued.set(command.commandId, {
      command,
      scope: args.scope,
      turnId: args.turnId,
      tool: args.tool,
      issuedAt: this.now(),
    });
    this.evictIfNeeded();
    return command;
  }

  /**
   * Record what the hub did with a command. Returns undefined for an id this process never
   * issued, which a caller should treat as 404 rather than as success: after a restart the
   * hub's acknowledgements have nowhere to land, and pretending otherwise hides it.
   */
  ack(commandId: string, ack: DeviceCommandAck): IssuedCommand | undefined {
    const row = this.issued.get(commandId);
    if (!row) return undefined;
    // First acknowledgement wins. A duplicate is the hub retrying, not a second execution,
    // and overwriting an "applied" with a later "expired" would misreport what happened.
    if (!row.ack) row.ack = { ...ack, receivedAt: this.now() };
    return row;
  }

  /** Commands minted during one turn, in issue order. What the hub is handed. */
  forTurn(turnId: string): DeviceCommand[] {
    return [...this.issued.values()]
      .filter((r) => r.turnId === turnId)
      .map((r) => r.command);
  }

  get(commandId: string): IssuedCommand | undefined {
    return this.issued.get(commandId);
  }

  /** Issued, unexpired and not yet acknowledged, for one room. Operator visibility. */
  outstandingFor(roomId: string): IssuedCommand[] {
    const now = this.now();
    return [...this.issued.values()].filter(
      (r) => r.scope.roomId === roomId && !r.ack && Date.parse(r.command.expiresAt) > now,
    );
  }

  all(): IssuedCommand[] {
    return [...this.issued.values()];
  }

  private evictIfNeeded(): void {
    const capacity = this.opts.capacity ?? 500;
    if (this.issued.size <= capacity) return;
    // Oldest first. Insertion order is issue order, so the first keys are the oldest.
    const excess = this.issued.size - capacity;
    let dropped = 0;
    for (const key of this.issued.keys()) {
      this.issued.delete(key);
      if (++dropped >= excess) break;
    }
    // Counters are bounded on their own rather than by which commands survived eviction.
    // Tying them together would let a turn whose first command was evicted mint a second
    // command reusing seq 1, handing the hub an id it may already hold.
    const counterCap = capacity * 2;
    if (this.seqByTurn.size > counterCap) {
      let drop = this.seqByTurn.size - counterCap;
      for (const turnId of this.seqByTurn.keys()) {
        this.seqByTurn.delete(turnId);
        if (--drop <= 0) break;
      }
    }
  }
}
