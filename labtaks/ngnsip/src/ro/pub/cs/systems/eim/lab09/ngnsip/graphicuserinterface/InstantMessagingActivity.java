package ro.pub.cs.systems.eim.lab09.ngnsip.graphicuserinterface;

import org.doubango.ngn.events.NgnMessagingEventArgs;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ro.pub.cs.systems.eim.lab09.ngnsip.R;
import ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver.MessageBroadcastReceiver;
import ro.pub.cs.systems.eim.lab09.ngnsip.general.Constants;

public class InstantMessagingActivity extends Activity {

	private IntentFilter messageIntentFilter;
	private MessageBroadcastReceiver messageBroadcastReceiver;

	private String SIPAddress = null;
	
	private EditText messageEditText = null;
	private Button sendButton = null;
	private TextView conversationTextView = null;
	
	private SendButtonClickListener sendButtonClickListener = new SendButtonClickListener();
	private class SendButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			if (VoiceCallActivity.instance.ngnSipService != null) {
				String remotePartyUri = SIPAddress;

				// TODO: exercise 11a
				// - create an NgnMessagingSession for each message being transmitted
				// passing as arguments the SipStack and the URI of the remote party
				// hint: use the static method createOutgoingSession
				// - send the message using the sendTextMessage() method of the session
				// and display it in the graphic user interface
				// !!! don't forget to release the session NgnMessagingSession.relaseSession()
				
			} else { 
				Toast.makeText(InstantMessagingActivity.this, "The SIP Service instance is null", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(Constants.TAG, "onCreate() callback method was invoked");
		setContentView(R.layout.activity_instant_messaging);

		Intent intent = getIntent();
		if (intent != null && intent.getExtras().containsKey(Constants.SIP_ADDRESS)) {
			SIPAddress = intent.getStringExtra(Constants.SIP_ADDRESS);
		}
		
		messageEditText = (EditText)findViewById(R.id.message_edit_text);
		
		sendButton = (Button)findViewById(R.id.send_button);
		sendButton.setOnClickListener(sendButtonClickListener);
		
		conversationTextView = (TextView)findViewById(R.id.conversation_text_view);
		conversationTextView.setMovementMethod(new ScrollingMovementMethod());
		
		enableMessageBroadcastReceiver();
	}
	
	public void enableMessageBroadcastReceiver() {
		messageBroadcastReceiver = new MessageBroadcastReceiver(conversationTextView);
		messageIntentFilter = new IntentFilter();
		messageIntentFilter.addAction(NgnMessagingEventArgs.ACTION_MESSAGING_EVENT);
		registerReceiver(messageBroadcastReceiver, messageIntentFilter);
	}
	
	public void disableMessageBroadcastReceiver() {
		if (messageBroadcastReceiver != null) {
			unregisterReceiver(messageBroadcastReceiver);
			messageBroadcastReceiver = null; 
		}
	}

	@Override
	protected void onDestroy() {
		Log.i(Constants.TAG, "onDestroy() callback method was invoked");
		super.onDestroy();
	}

}
