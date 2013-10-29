package sg.edu.nus.cs4274.autosilencer;

import java.util.Calendar;
import java.util.List;

import sg.edu.nus.cs4274.autosilencer.service.RouterDetectionService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	private RouterFoundReceiver receiver;
	private final static String SERVER = ":4274";
	private final static int POLLING_INTERVAL = 30;
	private String[] ROUTERS;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IntentFilter filter = new IntentFilter(RouterFoundReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new RouterFoundReceiver();
		registerReceiver(receiver, filter);

		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(this, RouterDetectionService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				POLLING_INTERVAL * 1000, pintent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy(); // Always call the superclass

		// Stop method tracing that the activity started during onCreate()
		unregisterReceiver(receiver);
		android.os.Debug.stopMethodTracing();
	}

	public class RouterFoundReceiver extends BroadcastReceiver {

		/**
		 * 
		 */
		public static final String ACTION_RESP = "sg.edu.nus.cs4274.intent.action.ROUTER_FOUND";

		public RouterFoundReceiver() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.BroadcastReceiver#onReceive(android.content.Context,
		 * android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String routerString = intent
					.getStringExtra(RouterDetectionService.PARAM_OUT_MSG);
			ROUTERS = routerString.split(" ");
			onReceivedRouters();
		}

		private void onReceivedRouters() {
//			If not connect to network, silence phone
			if(!isNetworkAvailable()){
				AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}else{
				//TODO when network is available....
			}
		}
		
		private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    TextView displayText = (TextView) findViewById(R.id.textView2);
	    AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    if (on) {
	    	//Silence the phone
	    	
	    	
	    	
	    	//Context context = getApplicationContext();
			//CharSequence text = "Button On!";
			//int duration = Toast.LENGTH_SHORT;

			//Toast toast = Toast.makeText(context, text, duration);
			//toast.show();
	    	
	    	
	    	displayText.setText("Slient Off");
	    	
			audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			
	    } else {
	    	//Silence the phone
	    	
	    	
	    	//Context context = getApplicationContext();
			//CharSequence text = "Button Off!";
			//int duration = Toast.LENGTH_SHORT;

			//Toast toast = Toast.makeText(context, text, duration);
			//toast.show();
			displayText.setText("Slient On");
			audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	    }
	}
	
}
