/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.receiver;

import sg.edu.nus.cs4274.autosilencer.service.RouterDetectionService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author huqiang
 *
 */
public class RouterFoundReceiver extends BroadcastReceiver {

	/**
	 * 
	 */
	public static final String ACTION_RESP =
	 	      "sg.edu.nus.cs4274.intent.action.ROUTER_FOUND";
	public RouterFoundReceiver() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String str = intent.getStringExtra(RouterDetectionService.PARAM_OUT_MSG);
		
	}

}
