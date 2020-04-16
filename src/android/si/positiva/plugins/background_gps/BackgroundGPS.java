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
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONException;
import android.content.pm.PackageManager;
import android.Manifest;

public class BackgroundGPS extends CordovaPlugin 
{
	private static JSONObject settings = new JSONObject();
	public BackgroundGPS() {}
	private CallbackContext callback;
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
	{
		if (action.equals("init")) 
		{
			settings = args.optJSONObject(0);
		}
		else if (action.equals("startBackground")) 
		{
			this.callback = callbackContext;
			String [] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };
			if(cordova.hasPermission(permissions))
			{
				startService();
			}
			else
			{
				cordova.requestPermissions(this, 1, permissions);
			}
		}
		else if (action.equals("stopBackground")) 
		{
		}
		else if (action.equals("getLocation")) 
		{
			JSONObject lastLocation = getLastLocation();
			PluginResult res = new PluginResult(Status.OK, lastLocation);
			callbackContext.sendPluginResult(res);
			callbackContext.success();
		}
		else
		{
			return false;
		}
		return true;
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
	
	private JSONObject getLastLocation()
    {
        Location location = service.getLastLocation();
		JSONObject object = new JSONObject();
		try
		{
			object.put("latitude", String.valueOf(location.getLatitude()));
			object.put("longitude", String.valueOf(location.getLongitude()));
			object.put("altitude", String.valueOf(location.getAltitude()));
			object.put("accuracy", String.valueOf(location.getAccuracy()));
		}
		catch (JSONException e) {}
		return object;
    }
	
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
	{
		for(int r:grantResults)
		{
			if(r == PackageManager.PERMISSION_DENIED)
			{
				this.callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PackageManager.PERMISSION_DENIED));
				return;
			}
		}
		switch(requestCode)
		{
			case 1:
				startService();
				break;
		}
	}
}