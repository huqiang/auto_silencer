package sg.edu.nus.cs4274.autosilencer;

import java.util.Calendar;
import java.util.List;

import sg.edu.nus.cs4274.autosilencer.service.DownloadService;
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
import android.widget.Toast;

public class MainActivity extends Activity {
	private RouterFoundReceiver routerFoundReceiver;
	private OnDownloadReceiver onDownloadReceiver;
	private final static String SERVER = ":4274";
	private final static int POLLING_INTERVAL = 30;
	private String[] ROUTERS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter(RouterFoundReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		routerFoundReceiver = new RouterFoundReceiver();
		registerReceiver(routerFoundReceiver, filter);

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
		unregisterReceiver(routerFoundReceiver);
		android.os.Debug.stopMethodTracing();
	}

	public class RouterFoundReceiver extends BroadcastReceiver {

		/**
		 * 
		 */
		public static final String ACTION_RESP = "sg.edu.nus.cs4274.intent.action.ROUTER_FOUND";
		private Context context;
		public RouterFoundReceiver() {
			super();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			this.context = context;
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
				//TODO when network is available.... Assume only one router presents.
				Intent intent = new Intent(this.context, DownloadService.class);
				for (int i = 0; i < ROUTERS.length; i++){
					intent.putExtra(DownloadService.PARAM_IN_MSG, SERVER+ROUTERS[i]);
					
				}
				
			}
		}
		
		private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
	}
	
	public class OnDownloadReceiver extends BroadcastReceiver {

		/**
		 * 
		 */
		public static final String ACTION_RESP = "sg.edu.nus.cs4274.intent.action.DOWNLOADED";

		public OnDownloadReceiver() {
			super();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String routerString = intent
					.getStringExtra(RouterDetectionService.PARAM_OUT_MSG);
			
			
		}
	}

}
