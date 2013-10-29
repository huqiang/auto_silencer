package sg.edu.nus.cs4274.autosilencer;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
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
	WifiManager wifi;
	List<ScanResult> results;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled() == false) {
			Toast.makeText(getApplicationContext(),
					"wifi is disabled..making it enabled", Toast.LENGTH_LONG)
					.show();
			wifi.setWifiEnabled(true);
		}
		results = wifi.getScanResults();
		AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		for (ScanResult result : results) {
			Log.d("result", result.SSID);
			
			if (result.SSID.equals("NUS")){
				//Silence the phone
				
				
				audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
		}
		
		
		TextView displayText1 = (TextView) findViewById(R.id.textView3);
		displayText1.setText("Auto Silence");
		
		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

		switch (am.getRingerMode()) {
		    case AudioManager.RINGER_MODE_SILENT:
		    	displayText1.setText("RINGER_MODE_SILENT");
		        break;
		    case AudioManager.RINGER_MODE_VIBRATE:
		    	displayText1.setText("RINGER_MODE_VIBRATE");
		        break;
		    case AudioManager.RINGER_MODE_NORMAL:
		    	displayText1.setText("RINGER_MODE_NORMAL");
		        break;
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
