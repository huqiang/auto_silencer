/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.nus.cs4274.autosilencer.R;
import android.widget.TextView;

/**
 * @author huqiang
 *
 */
public class Schedule {
	public String title;
	public int startHour;
	public int startMinute;
	public int endHour;
	public int endMinute;
	/**
	 * 
	 */
	public Schedule(String title, int startH, int startM, int endH, int endM) {
		this.title = title;
		this.startHour = startH;
		this.startMinute = startM;
		this.endHour = endH;
		this.endMinute = endM;
	}
	public static Schedule[] fromJSONString(String routerString) {
		Schedule[] result = null;
		try {
			JSONArray jsonArray = new JSONArray(routerString);
			result = new Schedule[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
		        JSONObject jsonObject = jsonArray.getJSONObject(i);
		        result[i] = new Schedule(jsonObject.getString("title"),
		        		jsonObject.getInt("startHour"),
		        		jsonObject.getInt("startMinute"),
		        		jsonObject.getInt("endHour"),
		        		jsonObject.getInt("endMinute"));
		      }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String toString(){
		String result = "Silent from ";
		Calendar cal = Calendar.getInstance();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	    
	    cal.set(Calendar.HOUR_OF_DAY, this.startHour);
		cal.set(Calendar.MINUTE, this.startMinute);
		cal.set(Calendar.SECOND, 0);
		
		result += sdf.format(cal.getTime());
		result += " to ";
		
		cal.set(Calendar.HOUR_OF_DAY, this.endHour);
		cal.set(Calendar.MINUTE, this.endMinute);
		cal.set(Calendar.SECOND, 0);
		
		result += sdf.format(cal.getTime());
		
		return result;
	}

}
