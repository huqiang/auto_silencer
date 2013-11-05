/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

}
