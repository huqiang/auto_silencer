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
	public int start;
	public int end;
	/**
	 * 
	 */
	public Schedule(String title, int start, int end) {
		this.title = title;
		this.start = start;
		this.end = end;
	}
	public static Schedule[] fromJSONString(String routerString) {
		Schedule[] result = null;
		try {
			JSONArray jsonArray = new JSONArray(routerString);
			result = new Schedule[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
		        JSONObject jsonObject = jsonArray.getJSONObject(i);
		        result[i] = new Schedule(jsonObject.getString("title"),
		        		jsonObject.getInt("start"),
		        		jsonObject.getInt("end"));
		      }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

}
