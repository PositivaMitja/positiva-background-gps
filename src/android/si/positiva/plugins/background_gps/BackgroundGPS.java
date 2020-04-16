package si.positiva.plugins.background_gps;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.cordova.CallbackContext;
import android.app.Activity;
import android.content.Intent;
import static android.content.Context.BIND_AUTO_CREATE;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import si.positiva.plugins.background_gps.BackgroundService.BackgroundBinder;
import android.location.Location;

public class BackgroundGPS extends CordovaPlugin 
{
	private static JSONObject settings = new JSONObject();
	public BackgroundGPS() {}
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
	{
		if (action.equals("init")) 
		{
			settings = args.optJSONObject(0);
		}
		else if (action.equals("startBackground")) 
		{
			startService();
		}
		else if (action.equals("stopBackground")) 
		{
		}
		else if (action.equals("getLocation")) 
		{
			return getLastLocation();
		}
	}

	private BackgroundService service;

	private final ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected (ComponentName name, IBinder service)
		{
			BackgroundBinder binder = (BackgroundBinder) service;
			BackgroundGPS.this.service = binder.getService();
		}

		@Override
		public void onServiceDisconnected (ComponentName name)
		{
			System.out.println("mitja service disconnected");
		}
	};

	private void startService()
	{
		Activity context = cordova.getActivity();
		Intent intent = new Intent(context, BackgroundService.class);
		try {
			context.bindService(intent, connection, BIND_AUTO_CREATE);
			context.startService(intent);
		} catch (Exception e) {
			System.out.println("mitja " + e.getMessage());
		}
	}
	
	static JSONObject getSettings() {
        return settings;
    }
	
	private Location getLastLocation()
    {
        return service.getLastLocation();
    }
}