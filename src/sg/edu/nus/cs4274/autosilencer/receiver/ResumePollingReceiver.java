/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.receiver;

import sg.edu.nus.cs4274.autosilencer.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author huqiang
 *
 */
public class ResumePollingReceiver extends BroadcastReceiver {
	public static final String ACTION_RESP = "sg.edu.nus.cs4274.intent.action.RESUME_POLLING";

	/**
	 * 
	 */
	public ResumePollingReceiver() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		MainActivity.getInstance().resumePolling();

	}

}
