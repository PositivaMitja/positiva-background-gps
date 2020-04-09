package si.positiva.plugins.background_gps;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.apache.cordova.CallbackContext;

public class BackgroundGPS extends CordovaPlugin 
{
	public BackgroundGPS() {}
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
	{
		if (action.equals("test")) 
		{
			System.out.println("dela");
		}
		return true;
	}
}