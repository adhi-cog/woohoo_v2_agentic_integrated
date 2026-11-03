package com.sb.woohoo.ui.main.sip;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.base.BaseActivity;
import com.sb.woohoo.utils.others.MethodUtils;

import org.linphone.core.Call;
import org.linphone.core.CoreListenerStub;

import static com.sileria.android.Resource.getResources;

public class SipCallDialog extends BaseActivity {

    private Call mCall;
    private CoreListenerStub mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(LinphoneManager.getCore().getCalls().length > 0) {
            mCall = LinphoneManager.getCore().getCalls()[0];
            incomingCallDialog(mCall, mCall.getState());
        } else {
            finish();
        }

    }

    private Dialog inSipCallDialog = null;
    public void incomingCallDialog(Call call, Call.State cstate) {

        if (inSipCallDialog != null && inSipCallDialog.isShowing()) {
            inSipCallDialog.dismiss();
            inSipCallDialog = null;
        }
        inSipCallDialog = new Dialog(this, R.style.DialogTheme);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MethodUtils.setDialogMargins(inSipCallDialog);
        }

        inSipCallDialog.setContentView(R.layout.dialog_in_sip_call);

        Button cancel = inSipCallDialog.findViewById(R.id.button_negative);
        Button ok = inSipCallDialog.findViewById(R.id.button_positive);

        TextView title = inSipCallDialog.findViewById(R.id.permission_dialog_title);
        TextView address = inSipCallDialog.findViewById(R.id.permission_dialog_message);
        address.setText(LinphoneManager.getCore().getCurrentCall().getRemoteAddressAsString());

        if (cstate == Call.State.IncomingReceived) {
            title.setText("New Call, Ringing...");
            Toast.makeText(SipCallDialog.this, "Ringing...", Toast.LENGTH_LONG).show();

            cancel.setOnClickListener(v -> {
                call.terminate();
                inSipCallDialog.dismiss();
                finish();
            });

            ok.setOnClickListener(v -> {
                LinphoneManager.getCallManager().acceptCall(call);
                LinphoneManager.getAudioManager().routeAudioToSpeaker();
                inSipCallDialog.dismiss();
                finish();
            });

        } else if (cstate == Call.State.OutgoingInit) {
            title.setText("Outgoing Call...");
            Toast.makeText(SipCallDialog.this, "call state: " + Call.State.OutgoingInit, Toast.LENGTH_LONG).show();

            cancel.setOnClickListener(v -> {
                call.terminate();
                inSipCallDialog.dismiss();
                finish();
            });

            ok.setVisibility(View.INVISIBLE);

        } else if (cstate == Call.State.Connected) {
            title.setText("Call Connected");
            Toast.makeText(SipCallDialog.this, "Call.State.Connected.", Toast.LENGTH_LONG).show();

            cancel.setText("End Call");
            cancel.setOnClickListener(v -> {
                call.terminate();
                inSipCallDialog.dismiss();
                finish();
            });

            ok.setVisibility(View.INVISIBLE);

        }
        else if (cstate == Call.State.Released || cstate == Call.State.End) {
            title.setText("Call Ended");
            Toast.makeText(SipCallDialog.this, "Call.State.Released : " + cstate.toString(), Toast.LENGTH_LONG).show();

            cancel.setVisibility(View.INVISIBLE);
            ok.setText("Close");
            ok.setOnClickListener(v -> {
                inSipCallDialog.dismiss();
                finish();
            });

        } else {
            title.setText("Call: " + cstate.toString());

            cancel.setText("End Call");
            cancel.setOnClickListener(v -> {
                call.terminate();
                inSipCallDialog.dismiss();
                finish();
            });

            ok.setVisibility(View.INVISIBLE);

            Toast.makeText(SipCallDialog.this, "call state: " + cstate.toString(), Toast.LENGTH_LONG).show();
        }



        inSipCallDialog.setCancelable(false);
        if (inSipCallDialog != null && !inSipCallDialog.isShowing()) {
            inSipCallDialog.show();
        }

    }
}
