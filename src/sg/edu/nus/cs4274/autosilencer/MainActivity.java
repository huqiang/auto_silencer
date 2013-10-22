package sg.edu.nus.cs4274.autosilencer;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	WifiManager wifi;
	List<ScanResult> results;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled() == false) {
			Toast.makeText(getApplicationContext(),
					"wifi is disabled..making it enabled", Toast.LENGTH_LONG)
					.show();
			wifi.setWifiEnabled(true);
		}
		results = wifi.getScanResults();

		for (ScanResult result : results) {
			Log.d("result", result.SSID);
			
			if (result.SSID.equals("NUS")){
				//Silence the phone
				AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
		}
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();  // Always call the superclass
	    
	    // Stop method tracing that the activity started during onCreate()
	    android.os.Debug.stopMethodTracing();
	}

}
