/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.receiver;

import sg.edu.nus.cs4274.autosilencer.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author huqiang
 *
 */
public class SilenceReceiver extends BroadcastReceiver {
	public static final String ACTION_RESP = "sg.edu.nus.cs4274.intent.action.SILENCEPHONE";
	/**
	 * 
	 */
	public SilenceReceiver() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver", "Received SilenceReceiver!!");
		MainActivity.silencePhone();
	}

}
