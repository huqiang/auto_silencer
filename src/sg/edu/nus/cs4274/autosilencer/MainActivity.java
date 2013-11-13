package sg.edu.nus.cs4274.autosilencer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.cs4274.autosilencer.model.Schedule;
import sg.edu.nus.cs4274.autosilencer.receiver.SilenceReceiver;
import sg.edu.nus.cs4274.autosilencer.receiver.UnSilenceReceiver;
import sg.edu.nus.cs4274.autosilencer.service.DownloadService;
import sg.edu.nus.cs4274.autosilencer.service.RouterDetectionService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View;

public class MainActivity extends Activity {
	private RouterFoundReceiver routerFoundReceiver;
	private OnDownloadReceiver onDownloadReceiver;
	private LocUpdateReceiver locUpdateReceiver;
	private AlarmManager alarm;
	private Calendar cal;
	private final static long POLLING_INTERVAL_SHORT = 6000;
	private final static long POLLING_INTERVAL_LONG = 18000;
	private String[] ROUTERS;
	private Schedule[] SCHEDULES;
	public int[] region = new int[3];
	private static MainActivity mainActivity;
	private PendingIntent pollingPIntent;
	private Intent pollingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Main", "Start main activity");
		MainActivity.mainActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkVolStatus();
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

		locUpdateReceiver = new LocUpdateReceiver();
		registerReceiver(locUpdateReceiver, new IntentFilter(
				"COMBINATION_LOCATION_MESSAGE"));

		pollingIntent = new Intent(getApplicationContext(), RouterDetectionService.class);
		pollingPIntent = PendingIntent.getService(this, 0, pollingIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		Log.d("JSON", "Time: "+Long.toString(System.currentTimeMillis()));
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), POLLING_INTERVAL_SHORT, pollingPIntent);

		Intent mIntent = new Intent("android.intent.action,MAIN");
		ComponentName comp = new ComponentName("com.fyp.locationfinder",
				"com.fyp.locationfinder.CombinationService");
		mIntent.setComponent(comp);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		mIntent.addCategory("android.intent.category.LAUNCHER");
		startService(mIntent);

		//Shake Code
		//mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

				
	}
	
//	private SensorManager mSensorManager;
//
//	
//	
//	private final SensorEventListener mSensorListener = new SensorEventListener()
//	{
//		
//		float z_current;
//		public void onAccuracyChanged(Sensor sensor, int accuracy) {
//		}
//
//		@Override
//		public void onSensorChanged(SensorEvent se) {
//			z_current = se.values[2];
//			TextView displayText = (TextView) findViewById(R.id.textView2);
//			AudioManager audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//			if(z_current > 15 )
//			{
//				//face up.
//				displayText.setText("Flip Detected: Face up");
//				audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//				delayPolling();
//				
//			}
//			if(z_current < -15)
//			{
//				//face down.
//				displayText.setText("Flip Detected: Face down");
//				audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//				delayPolling();
//			}
//			checkVolStatus();
//		}
//	};
	

		
/*	@Override
    protected void onResume() {
      super.onResume();	
      mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
      mSensorManager.unregisterListener(mSensorListener);
     
      super.onStop();
    }	
*/
		
		

	@Override
	public void onDestroy() {
		super.onDestroy(); // Always call the superclass

		// Stop method tracing that the activity started during onCreate()
		unregisterReceiver(routerFoundReceiver);
		unregisterReceiver(onDownloadReceiver);
		unregisterReceiver(locUpdateReceiver);
		// unregisterReceiver(silenceReceiver);
		// unregisterReceiver(unSilenceReceiver);
		android.os.Debug.stopMethodTracing();
	}

	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();
		
		AudioManager audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (on) {

			audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			delayPolling();

		} else {

			audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			delayPolling();
		}

		checkVolStatus();
	}

	private void delayPolling() {
		alarm.cancel(pollingPIntent);
		Intent resumeIntent = new Intent(
				this,
				sg.edu.nus.cs4274.autosilencer.receiver.ResumePollingReceiver.class);
		resumeIntent.setClass(this, SilenceReceiver.class);
		// silenceIntent.addCategory(Intent.CATEGORY_DEFAULT);
		PendingIntent pendingResumeIntent = PendingIntent
				.getBroadcast(getApplicationContext(), 4274,
						resumeIntent, PendingIntent.FLAG_ONE_SHOT);
		alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3600000,
				pendingResumeIntent);
		
	}

	public void resumePolling(){
		changePollingInterval(POLLING_INTERVAL_SHORT);
	}
	private void updateListView() {
		ListView lv = (ListView) findViewById(R.id.displayListView);
		ArrayList<String> displayArrayList = new ArrayList<String>();
		for (Schedule s : SCHEDULES) {

			displayArrayList.add(s.toString());
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.simplerow, displayArrayList);
		lv.setAdapter(arrayAdapter);

	}

	public void scheduleEvents() {
		// AlarmManager alarm = (AlarmManager)
		// getSystemService(Context.ALARM_SERVICE);
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		int index = 0;
		cal = Calendar.getInstance();
		if (SCHEDULES == null)
			return;
		for (Schedule s : SCHEDULES) {
			start.set(Calendar.HOUR_OF_DAY, s.startHour);
			start.set(Calendar.MINUTE, s.startMinute);
			start.set(Calendar.SECOND, 0);
			end.set(Calendar.HOUR_OF_DAY, s.endHour);
			end.set(Calendar.MINUTE, s.endMinute);
			end.set(Calendar.SECOND, 0);
			cal.setTimeInMillis(System.currentTimeMillis());
			Log.d("Time", "Current Time: " + cal.getTimeInMillis());
			Log.d("Time", "Start Time: " + start.getTimeInMillis());
			Log.d("Time", "End Time: " + end.getTimeInMillis());
			if (cal.before(start)) {
				// if(true){
				// Now is before start time, the silence phone at start time and
				// unsilence phone after end
				Log.d("Time", "Setting silence event");
				Intent silenceIntent = new Intent(     //<-------intent issue
						getApplicationContext(),
						sg.edu.nus.cs4274.autosilencer.receiver.SilenceReceiver.class);
				silenceIntent.putExtra("region", s.region);
				// silenceIntent.addCategory(Intent.CATEGORY_DEFAULT);
				PendingIntent pendingSilenceIntent = PendingIntent
						.getBroadcast(getApplicationContext(), index++,
								silenceIntent, PendingIntent.FLAG_ONE_SHOT);
				Log.d("Index", "Index: " + index);
				alarm.set(AlarmManager.RTC_WAKEUP, start.getTimeInMillis(),
						pendingSilenceIntent);
				Log.d("Time", "Setting unSilence event");
				Intent unsilenceIntent = new Intent(getApplicationContext(),
						UnSilenceReceiver.class);
				unsilenceIntent.setAction(UnSilenceReceiver.ACTION_RESP);
				PendingIntent pendingUnSilenceIntent = PendingIntent
						.getBroadcast(getApplicationContext(), index++,
								unsilenceIntent, PendingIntent.FLAG_ONE_SHOT);
				alarm.set(AlarmManager.RTC_WAKEUP, end.getTimeInMillis(),
						pendingUnSilenceIntent);
			} else if (cal.before(end)) {
				// Silence phone when now is in the middle of event, unsilence
				// after event ends.
				silencePhone();
				Intent unsilenceIntent = new Intent(getApplicationContext(),
						UnSilenceReceiver.class);
				unsilenceIntent.setAction(UnSilenceReceiver.ACTION_RESP);
				unsilenceIntent.addCategory(Intent.CATEGORY_DEFAULT);
				PendingIntent pendingUnSilenceIntent = PendingIntent
						.getBroadcast(getApplicationContext(), index++,
								unsilenceIntent, PendingIntent.FLAG_ONE_SHOT);
				alarm.set(AlarmManager.RTC_WAKEUP, end.getTimeInMillis(),
						pendingUnSilenceIntent);
			} else {
				// Nothing to do. If the event is ended already.
			}
			// display stuff
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

		}

	}

	public static void silencePhone() {
		Log.d("JSON", "silencePhone");
		AudioManager audiomanage = (AudioManager) mainActivity
				.getSystemService(Context.AUDIO_SERVICE);
		if(audiomanage.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
			audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			mainActivity.changePollingInterval(POLLING_INTERVAL_LONG);
		}

		TextView displayText = (TextView) mainActivity
				.findViewById(R.id.textView3);
		displayText.setText("RINGER_MODE_SILENT");

		mainActivity.checkVolStatus();
	}

	public void changePollingInterval(long pollingIntervalLong) {
		mainActivity.alarm.cancel(pollingPIntent);
//		pollingIntent = new Intent(mainActivity.getApplicationContext(),
//				RouterDetectionService.class);
//		pollingPIntent = PendingIntent.getService(
//				mainActivity.getApplicationContext(), 0, pollingIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//		 mainActivity.alarm.s
//		Log.d("JSON", "Change polling rate!!");
		mainActivity.alarm.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), pollingIntervalLong,
				pollingPIntent);
	}

	public static void unSilencePhone() {
		Log.d("JSON", "unSilencePhone");
		AudioManager audiomanage = (AudioManager) mainActivity
				.getSystemService(Context.AUDIO_SERVICE);
		if(audiomanage.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
			audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			mainActivity.changePollingInterval(POLLING_INTERVAL_SHORT);
		}

		TextView displayText = (TextView) mainActivity
				.findViewById(R.id.textView3);
		displayText.setText("RINGER_MODE_NORMAL");
		mainActivity.checkVolStatus();
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

	public static MainActivity getInstance() {
		return mainActivity;
	}

	public class LocUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("location");
			try {
				JSONObject resultObj = new JSONObject(result);
				JSONObject coorObj = resultObj.getJSONObject("coor");
				int x = coorObj.getInt("X");
				int y = coorObj.getInt("Y");
				int z = coorObj.getInt("Z");
				Toast.makeText(getApplicationContext(), x + " " + y + "  " + z,
						Toast.LENGTH_LONG).show();
				region[0] = x;
				region[1] = y;
				region[2] = z;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

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
			this.context = context;
			ROUTERS = intent
					.getStringArrayExtra(RouterDetectionService.PARAM_OUT_MSG);
			
			Log.d("JSON", Integer.toString(ROUTERS.length));
			onReceivedRouters();
		}

		private void onReceivedRouters() {
			// If not connect to network, silence phone
			if (ROUTERS.length >= 1){
				if (!isNetworkAvailable()) {
					// if (true) {
					silencePhone();
				} else {
					getSchedules();
				}
			}
			else{
				unSilencePhone();
			}
		}

		private void getSchedules() {
			for (String id : ROUTERS) {
				Intent intent = new Intent(this.context, DownloadService.class);
				intent.putExtra(DownloadService.PARAM_IN_MSG, id);
				startService(intent);
			}
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
			String routerString = intent
					.getStringExtra(DownloadService.PARAM_OUT_MSG);
			Log.d("JSON", routerString);
			if (routerString != "") {
				SCHEDULES = Schedule.fromJSONString(routerString);
			}

			if (SCHEDULES != null && SCHEDULES.length > 0) {
				scheduleEvents(); // error here....
				updateListView();
				changePollingInterval(POLLING_INTERVAL_LONG);
			}
		}
	}

}
