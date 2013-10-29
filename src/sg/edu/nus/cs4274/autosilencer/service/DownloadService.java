/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import sg.edu.nus.cs4274.autosilencer.MainActivity.RouterFoundReceiver;

import android.app.IntentService;
import android.content.Intent;

/**
 * @author huqiang
 *
 */
public class DownloadService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";

	/**
	 * @param name
	 */
	
	public DownloadService() {
        super("DownloadService");
    }
	
	public DownloadService(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		String url = intent.getStringExtra(PARAM_IN_MSG);
		String result;
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(RouterFoundReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		try {
			result = downloadFile(url);
			broadcastIntent.putExtra(PARAM_OUT_MSG, result);
		} catch (IOException e) {
			e.printStackTrace();
			broadcastIntent.putExtra(PARAM_OUT_MSG, "");
		}
		sendBroadcast(broadcastIntent);
		
		
	}
	
	private String downloadFile(String urlStr) throws IOException {
	    String toReturn = "";
	    URL url = new URL(urlStr);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	    int responseCode = conn.getResponseCode();
	    System.out.println("Code: " + responseCode);

	    if (responseCode == HttpURLConnection.HTTP_OK) {

	        BufferedInputStream is = new BufferedInputStream(
	                conn.getInputStream());
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));

	        String line;

	        while ((line = br.readLine()) != null) {
	            toReturn += line;
	        }

	        br.close();
	        is.close();
	        conn.disconnect();

	    } else {
	        throw new IllegalStateException("HTTP response: " + responseCode);
	    }
	    return toReturn;
	}
}
