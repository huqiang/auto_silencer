/**
 * 
 */
package sg.edu.nus.cs4274.autosilencer.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author huqiang
 * 
 */
public class Coor {
	public int X;
	public int Y;
	public int Z;

	/**
	 * 
	 */
	public Coor() {
	}

	public void fromJSON(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			this.X = obj.getInt("X");
			this.Y = obj.getInt("Y");
			this.Z = obj.getInt("Z");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
