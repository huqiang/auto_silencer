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

import sg.edu.nus.cs4274.autosilencer.MainActivity.OnDownloadReceiver;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author huqiang
 * 
 */
public class DownloadService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";
	// private final static String SERVER = "http://qiang.hu:4274/";
	// Change IP address here.
	private final static String SERVER = "http://172.28.178.46:4274/";

	/**
	 * @param name
	 */

	public DownloadService() {
		super("DownloadService");
	}

	public DownloadService(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		String id = intent.getStringExtra(PARAM_IN_MSG);
		String result = "";
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(OnDownloadReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		try {
			Log.d("Download", "Downloading: " + SERVER + id);
			result += downloadFile(SERVER + id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		broadcastIntent.putExtra(PARAM_OUT_MSG, result);
		Log.d("Download", "result: " + result);
		sendBroadcast(broadcastIntent);

	}

	private String downloadFile(String urlStr) throws IOException {
		String toReturn = "";
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		int responseCode = conn.getResponseCode();
		Log.d("Download", "Code: " + responseCode);

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
