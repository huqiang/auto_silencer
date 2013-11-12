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
		int[] region = intent.getIntArrayExtra("region");
		if(inRegion(MainActivity.getInstance().region, region)){
			MainActivity.silencePhone();			
		}
		
	}

	private boolean inRegion(int[] point, int[] region) {
		// TODO Auto-generated method stub
		if(point[0] >= region[0] && point[0] <= region[3]
				&& point[1] >= region[1] && point[1] <= region[4]
						&& point[2] >= region[2] && point[1] <= region[5])
			return true;
		return false;
	}

}
