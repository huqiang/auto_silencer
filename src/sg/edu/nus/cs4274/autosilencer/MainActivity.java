package sg.edu.nus.cs4274.autosilencer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import sg.edu.nus.cs4274.autosilencer.model.Schedule;
import sg.edu.nus.cs4274.autosilencer.receiver.SilenceReceiver;
import sg.edu.nus.cs4274.autosilencer.receiver.UnSilenceReceiver;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	private RouterFoundReceiver routerFoundReceiver;
	private OnDownloadReceiver onDownloadReceiver;
	private SilenceReceiver silenceReceiver;
	private UnSilenceReceiver unSilenceReceiver;
	private AlarmManager alarm;
	private Calendar cal;
	private final static String SERVER = "http://qiang.hu:4274/";
	private final static int POLLING_INTERVAL = 30;
	private String[] ROUTERS;
	private Schedule[] SCHEDULES;
	private static MainActivity mainActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Main", "Start main activity");
		MainActivity.mainActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// Register routerFoundReceiver
		IntentFilter filterRouterFound = new IntentFilter(
				RouterFoundReceiver.ACTION_RESP);
		filterRouterFound.addCategory(Intent.CATEGORY_DEFAULT);
		routerFoundReceiver = new RouterFoundReceiver();
		registerReceiver(routerFoundReceiver, filterRouterFound);

		// Register onDownloadReceiver
		IntentFilter filterDownloaded = new IntentFilter(
				OnDownloadReceiver.ACTION_RESP);
		filterDownloaded.addCategory(Intent.CATEGORY_DEFAULT);
		onDownloadReceiver = new OnDownloadReceiver();
		registerReceiver(onDownloadReceiver, filterDownloaded);

		
		cal = Calendar.getInstance();
		Intent intent = new Intent(this, RouterDetectionService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				POLLING_INTERVAL * 1000, pintent);

	}

	@Override
	public void onDestroy() {
		super.onDestroy(); // Always call the superclass

		// Stop method tracing that the activity started during onCreate()
		unregisterReceiver(routerFoundReceiver);
		unregisterReceiver(onDownloadReceiver);
//		unregisterReceiver(silenceReceiver);
//		unregisterReceiver(unSilenceReceiver);
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
			ROUTERS = intent.getStringArrayExtra(RouterDetectionService.PARAM_OUT_MSG);
			onReceivedRouters();
		}

		private void onReceivedRouters() {
			checkVolStatus();
			// If not connect to network, silence phone
			if (!isNetworkAvailable()) {
				// if (true) {
				silencePhone();
				checkVolStatus();
			} else {
				getSchedules();
			}
		}

		private void getSchedules() {
			Intent intent = new Intent(this.context, DownloadService.class);
			intent.putExtra(DownloadService.PARAM_IN_MSG, ROUTERS);
			startService(intent);
		}

		private boolean isNetworkAvailable() {
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
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
					.getStringExtra(DownloadService.PARAM_OUT_MSG);
			Log.d("JSON", routerString);
			SCHEDULES = Schedule.fromJSONString(routerString);
			scheduleEvents();
			
			Intent intent1 = new Intent(getApplicationContext(), RouterDetectionService.class);
			PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent1, 0);
			alarm.cancel(pintent);
		}
	}


	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();
		TextView displayText = (TextView) findViewById(R.id.textView2);
		AudioManager audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (on) {

			displayText.setText("Slient Off");

			audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			checkVolStatus();

		} else {

			displayText.setText("Slient On");
			audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			checkVolStatus();
		}
	}

	public void scheduleEvents() {
//		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		int index = 0;
		for (Schedule s : SCHEDULES) {
			start.set(Calendar.HOUR_OF_DAY, s.startHour);
			start.set(Calendar.MINUTE, s.startMinute);
			start.set(Calendar.SECOND, 0);
			end.set(Calendar.HOUR_OF_DAY, s.endHour);
			end.set(Calendar.MINUTE, s.endMinute);
			end.set(Calendar.SECOND, 0);
			cal.setTimeInMillis(System.currentTimeMillis());
			Log.d("Time", "Current Time: "+cal.getTimeInMillis());
			Log.d("Time", "Start Time: "+start.getTimeInMillis());
			Log.d("Time", "End Time: "+end.getTimeInMillis());
			if(cal.before(start) ){
//			if(true){
				//Now is before start time, the silence phone at start time and unsilence phone after end
				Log.d("Time", "Setting silence event");
				Intent silenceIntent = new Intent(this, sg.edu.nus.cs4274.autosilencer.receiver.SilenceReceiver.class);
				silenceIntent.setClass(this, SilenceReceiver.class);
//				silenceIntent.addCategory(Intent.CATEGORY_DEFAULT);
				PendingIntent pendingSilenceIntent = PendingIntent.getBroadcast(getApplicationContext(), index++, silenceIntent, PendingIntent.FLAG_ONE_SHOT);
				Log.d("Index", "Index: "+ index);
				alarm.set(AlarmManager.RTC_WAKEUP, start.getTimeInMillis(),pendingSilenceIntent);
				Log.d("Time", "Setting unSilence event");				
				Intent unsilenceIntent = new Intent(this, UnSilenceReceiver.class);
				unsilenceIntent.setAction(UnSilenceReceiver.ACTION_RESP);
				PendingIntent pendingUnSilenceIntent = PendingIntent.getBroadcast(getApplicationContext(),index++, unsilenceIntent, PendingIntent.FLAG_ONE_SHOT);
				alarm.set(AlarmManager.RTC_WAKEUP, end.getTimeInMillis(),
						pendingUnSilenceIntent);
			}else if(cal.before(end)){
				//Silence phone when now is in the middle of event, unsilence after event ends.
				silencePhone();		
				checkVolStatus();
				Intent unsilenceIntent = new Intent(getApplicationContext(), UnSilenceReceiver.class);
				unsilenceIntent.setAction(UnSilenceReceiver.ACTION_RESP);
				unsilenceIntent.addCategory(Intent.CATEGORY_DEFAULT);
				PendingIntent pendingUnSilenceIntent = PendingIntent.getBroadcast(getApplicationContext(),index++, unsilenceIntent, PendingIntent.FLAG_ONE_SHOT);
				alarm.set(AlarmManager.RTC_WAKEUP, end.getTimeInMillis(),
						pendingUnSilenceIntent);
			}else{
				//Nothing to do. If the event is ended already.
			}
			//display stuff
		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		    sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//		    sdf.format(start.getTime());
		    
			TextView displayText = (TextView) findViewById(R.id.textView4);
			displayText.setText("slient From " +sdf.format(start.getTime())+ " to "+ sdf.format(end.getTime()) );
			
		}
		
		
		
	}

	public static void silencePhone() {
		AudioManager audiomanage = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
		audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);

		TextView displayText = (TextView) mainActivity.findViewById(R.id.textView3);
		displayText.setText("RINGER_MODE_SILENT");
	}

	public static void unSilencePhone() {
		AudioManager audiomanage = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
		audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

		TextView displayText = (TextView) mainActivity.findViewById(R.id.textView3);
		displayText.setText("RINGER_MODE_NORMAL");

		
	}

	private void checkVolStatus() {
		ImageView imgView;
		imgView = (ImageView) findViewById(R.id.imageView1);
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		TextView displayText = (TextView) findViewById(R.id.textView3);
		switch (am.getRingerMode()) {
		case AudioManager.RINGER_MODE_SILENT:
			imgView.setImageResource(R.drawable.nosound);
			displayText.setText("RINGER_MODE_SILENT");
			break;
		case AudioManager.RINGER_MODE_NORMAL:
			imgView.setImageResource(R.drawable.sound);
			displayText.setText("RINGER_MODE_SOUND");
			break;
		}

	}

}
