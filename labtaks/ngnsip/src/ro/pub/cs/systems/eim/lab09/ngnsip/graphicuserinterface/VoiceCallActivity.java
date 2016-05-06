package ro.pub.cs.systems.eim.lab09.ngnsip.graphicuserinterface;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ro.pub.cs.systems.eim.lab09.ngnsip.R;
import ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver.CallStateBroadcastReceiver;
import ro.pub.cs.systems.eim.lab09.ngnsip.broadcastreceiver.RegistrationStateBroadcastReceiver;
import ro.pub.cs.systems.eim.lab09.ngnsip.general.Constants;

public class VoiceCallActivity extends Activity {

	private NgnEngine ngnEngine = null;
	public INgnSipService ngnSipService = null;
	
	private IntentFilter registrationIntentFilter;
	private RegistrationStateBroadcastReceiver registrationStateBroadcastReceiver;
	
	private IntentFilter callIntentFilter;
	private CallStateBroadcastReceiver callStateBroadcastReceiver;
	
	public NgnAVSession ngnAVSession = null;

	private Button registerButton = null;
	private Button unregisterButton = null;
	private TextView registrationStatusTextView = null;
	
	private EditText SIPAddressEditText = null;
	private Button makeCallButton = null;
	private Button hangUpCallButton = null;
	private TextView callStatusTextView = null;
	
	private Button dtmfButton = null;
	private EditText dtmfEditText = null;
	
	private Button chatButton = null;

	private RegisterButtonClickListener registerButtonClickListener = new RegisterButtonClickListener();
	private class RegisterButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO: exercise 5a
			// - set the NGN engine parameters via the configureStack() method
			// - start the NGN engine and register the activity to the SIP Service
			// invoke the startNgnEngine() and registerSipService() methods respectively
		}
	}
	
	private UnregisterButtonClickListener unregisterButtonClickListener = new UnregisterButtonClickListener();
	private class UnregisterButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO: exercise 5b
			// unregister the SIP Service by invoking the unregisterSipService() method
		}
	}
	
	private MakeCallButtonListener makeCallButtonListener = new MakeCallButtonListener();
	private class MakeCallButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			String validUri = NgnUriUtils.makeValidSipUri(SIPAddressEditText.getText().toString());
			if (validUri == null) {
				Log.e(Constants.TAG, "Invalid SIP address");
				return;
			}
			if (!ngnEngine.isStarted() || !ngnSipService.isRegistered()) {
				Log.e(Constants.TAG, "NGN Engine is not started or NGN Sip Service is not registered!");
				return;
			}
			
			// TODO: exercise 7a
			// - create an NgnAVSession by invoking the static method createOutgoingSession
			// passing as arguments the SipStack and the media type (NgnMediaType.Audio)
			// - if the call can be made, set the callStatusTextView to calling and log the information
			// - if the call cannot be made, log the information accordingly
			// hint: use the makeCall() method of the session
			
		}
	}
	
	private HangupCallButtonListener hangupCallButtonListener = new HangupCallButtonListener();
	private class HangupCallButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {

			// TODO: exercise 7b
			// this method should be check is the session was previously created
			// hint: use the hangUpCall() method of the session

		}
	}
	
	private DTMFButtonClickListener dtmfButtonClickListener = new DTMFButtonClickListener();
	private class DTMFButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			if (ngnAVSession != null) {
				
				// TODO: exercise 10 (optional)
				// - get the character from the DTMF edit text
				// - compute its code (0-9 for digits, 10 for *, 11 for #)
				// - use the sendDTMF method of the NGN AV session
				// - log the result using LogCat
				
			}			
		}
	}
	
	private ChatButtonClickListener chatButtonClickListener = new ChatButtonClickListener();
	private class ChatButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			Intent intent = new Intent(getApplicationContext(), InstantMessagingActivity.class);
			intent.putExtra(Constants.SIP_ADDRESS, SIPAddressEditText.getText().toString());
			startActivity(intent);
		}
	}

	public static VoiceCallActivity instance;

	public VoiceCallActivity getInstance() {
		return instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_call);
		instance = this;

		ngnEngine = NgnEngine.getInstance();
		if (ngnEngine == null) {
			Log.i(Constants.TAG, "Failed to obtain the NGN engine!");
		}
		ngnSipService = ngnEngine.getSipService();
		
		registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(registerButtonClickListener);
		unregisterButton = (Button)findViewById(R.id.unregister_button);
		unregisterButton.setOnClickListener(unregisterButtonClickListener);
		registrationStatusTextView = (TextView)findViewById(R.id.registration_status_text_view);
		
		SIPAddressEditText = (EditText)findViewById(R.id.SIP_address_edit_text);
		makeCallButton = (Button)findViewById(R.id.make_call_button);
		makeCallButton.setOnClickListener(makeCallButtonListener);
		hangUpCallButton = (Button)findViewById(R.id.hang_up_call_button);
		hangUpCallButton.setOnClickListener(hangupCallButtonListener);
		callStatusTextView = (TextView)findViewById(R.id.call_status_text_view);
		
		dtmfButton = (Button)findViewById(R.id.dtmf_button);
		dtmfButton.setOnClickListener(dtmfButtonClickListener);
		dtmfEditText = (EditText)findViewById(R.id.dtmf_edit_text);
		
		chatButton = (Button)findViewById(R.id.chat_button);
		chatButton.setOnClickListener(chatButtonClickListener);

		enableRegistrationStateBroadcastReceiver();
		enableCallStateBroadcastReceiver();
	}

	public void configureStack() {
		NgnEngine ngnEngine = NgnEngine.getInstance();
		INgnConfigurationService ngnConfigurationService = ngnEngine.getConfigurationService();
		ngnConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, Constants.IDENTITY_IMPI);
		ngnConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", Constants.USERNAME, Constants.DOMAIN));
		ngnConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, Constants.IDENTITY_PASSWORD);
		ngnConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, Constants.NETWORK_PCSCF_HOST);
		ngnConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, Constants.NETWORK_PCSCF_PORT);
		ngnConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, Constants.NETWORK_REALM);

		ngnConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G, true);
		ngnConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT, Constants.NETWORK_REGISTRATION_TIMEOUT);

		ngnConfigurationService.commit();
	}
	
	public boolean startNgnEngine() {
		if (!ngnEngine.isStarted()) {
			if (!ngnEngine.start()) {
				Log.e(Constants.TAG, "Failed to start the NGN engine!");
				return false;
			}
		}
		return true;
	}
	
	public boolean stopNgnEngine() {
		if (ngnEngine.isStarted()) {
			if (!ngnEngine.stop()) {
				Log.e(Constants.TAG, "Failed to stop the NGN engine!");
				return false;
			}
		}
		return true;
	}
	
	public void registerSipService() {
		if (!ngnSipService.isRegistered()) {
			ngnSipService.register(this);
		}
	}
	
	public void unregisterSipService() {
		if (ngnSipService.isRegistered()) {
			ngnSipService.unRegister();
		}
	}
	
	public void enableRegistrationStateBroadcastReceiver() {

		// TODO: exercise 6a
		// - create a RegistrationBroadcastReceiver instance
		// - create an IntentFilter instance for NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT action
		// - register the broadcast intent with the intent filter

	}
	
	public void disableRegistrationStateBroadcastReceiver() {

		// TODO: exercise 6b
		// unregister the broadcast receiver

	}
	
	public void enableCallStateBroadcastReceiver() {

		// TODO: exercise 8a
		// - create a CallStateBroadcastReceiver instance
		// - create an IntentFilter instance for NgnInviteEventArgs.ACTION_INVITE_EVENT action
		// - register the broadcast intent with the intent filter

	}
	
	public void disableCallStateBroadcastReceiver() {

		// TODO: exercise 8b
		// unregister the broadcast receiver

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(Constants.TAG, "onResume() callback method was invoked");
	}

	@Override
	protected void onPause() {
		Log.i(Constants.TAG, "onPause() callback method was invoked");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.i(Constants.TAG, "onDestroy() callback method was invoked");
		stopNgnEngine();
		disableRegistrationStateBroadcastReceiver();
		disableCallStateBroadcastReceiver();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
