/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.service;

import java.util.List;

import sg.edu.nus.cs4274.autosilencer.MainActivity.RouterFoundReceiver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * @author huqiang
 *
 */
public class RouterDetectionService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
    private WifiManager wifi;
    private List<ScanResult> scanResults;
    
    public RouterDetectionService() {
        super("RouterDetectionService");
    }
	/**
	 * @param name
	 */
	public RouterDetectionService(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
//		String msg = intent.getStringExtra(PARAM_IN_MSG);
		String resultTxt = scanWifi();
//		Broadcast result
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(RouterFoundReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_MSG, resultTxt);
		sendBroadcast(broadcastIntent);
	}

	private String scanWifi(){
		String result = "";
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled() == false) {
			Toast.makeText(getApplicationContext(),
					"wifi is disabled..making it enabled", Toast.LENGTH_SHORT)
					.show();
			wifi.setWifiEnabled(true);
		}
		scanResults = wifi.getScanResults();

		for (ScanResult r : scanResults) {
			if (r.SSID.startsWith("NUS")){
				result +=r.SSID+" "; 
			}
		}
		
		return result;
	}
}
