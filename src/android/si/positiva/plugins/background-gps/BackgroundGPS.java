package si.positiva.plugins.background-gps;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.apache.cordova.CallbackContext;
import android.widget.Toast;

public class BackgroundGPS extends CordovaPlugin 
{
	public BackgroundGPS() {}
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
	{
		if (action.equals("test")) 
		{
			Toast.makeText(this, "dela", Toast.LENGTH_LONG).show();
		}
		return true;
	}
}