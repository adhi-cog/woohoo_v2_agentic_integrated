package com.sb.woohoo.ui.main.sip.state

sealed class SipCallState {
    object Idle: SipCallState()
    class IncomingReceived (val address: String): SipCallState()
    class OutgoingInit  (val address: String) : SipCallState()
    object OutgoingProgress: SipCallState()
    object OutgoingRinging: SipCallState()

    object Connected: SipCallState()
    object Running: SipCallState()

    object Error: SipCallState()
    object End: SipCallState()

    object Released: SipCallState()
}


            /*case 0: return Idle;
            case 1: return IncomingReceived;
            case 2: return OutgoingInit;
            case 3: return OutgoingProgress;
            case 4: return OutgoingRinging;
            case 5: return OutgoingEarlyMedia;
            case 6: return Connected;
            case 7: return StreamsRunning;
            case 8: return Pausing;
            case 9: return Paused;
            case 10: return Resuming;
            case 11: return Referred;
            case 12: return Error;
            case 13: return End;
            case 14: return PausedByRemote;
            case 15: return UpdatedByRemote;
            case 16: return IncomingEarlyMedia;
            case 17: return Updating;
            case 18: return Released;
            case 19: return EarlyUpdatedByRemote;
            case 20: return EarlyUpdating;*/