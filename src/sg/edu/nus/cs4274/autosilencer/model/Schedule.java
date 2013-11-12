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

/**
 * @author huqiang
 * 
 */
public class Schedule {
	public String title;
	public String position;
	public int[] region;
	public int startHour;
	public int startMinute;
	public int endHour;
	public int endMinute;

	/**
	 * 
	 */
	public Schedule(String title, String position_name, int[] region,
			int startH, int startM, int endH, int endM) {
		this.title = title;
		this.position = position_name;
		this.region = region;
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
				JSONArray regionArr = jsonObject.getJSONArray("region");
				int[] region = new int[6];
				for (int j = 0; j < 6; j++) {
					region[i] = regionArr.getInt(j);
				}
				result[i] = new Schedule(jsonObject.getString("title"),
						jsonObject.getString("position_name"), region,
						jsonObject.getInt("startHour"),
						jsonObject.getInt("startMinute"),
						jsonObject.getInt("endHour"),
						jsonObject.getInt("endMinute"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public String toString() {
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
