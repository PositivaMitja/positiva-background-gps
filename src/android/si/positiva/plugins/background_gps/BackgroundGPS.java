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
	public BackgroundGPS() { System.out.println("mitja GPS construct"); }
	private CallbackContext callback;
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
	{
		System.out.println("mitja GPS execute " + action);
		if (action.equals("init")) 
		{
			settings = args.optJSONObject(0);
			PluginResult res = new PluginResult(Status.OK, true);
			callbackContext.sendPluginResult(res);
			callbackContext.success();
		}
		else if (action.equals("startBackground")) 
		{
			this.callback = callbackContext;
			String [] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };
			if(cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
			{
				startService();
				PluginResult res = new PluginResult(Status.OK, true);
				callbackContext.sendPluginResult(res);
				callbackContext.success();
			}
			else
			{
				cordova.requestPermissions(this, 1, permissions);
			}
		}
		else if (action.equals("stopBackground")) 
		{
			stopService();
		}
		else if (action.equals("startTracking")) 
		{
			System.out.println("mitja GPS execute " + args.optJSONObject(0).toString());
			setSettings(args.optJSONObject(0));
                        if (service != null)
                        {
                            service.setTracking(true);
                            PluginResult res = new PluginResult(Status.OK, true);
                            callbackContext.sendPluginResult(res);
                            callbackContext.success();
                        }
		}
		else if (action.equals("stopTracking")) 
		{
                        if (service != null)
                        {
                            service.setTracking(false);
                        }
		}
		else if (action.equals("getLocation")) 
		{
			if (this.callback != null)
			{
				JSONObject lastLocation = getLastLocation();
				PluginResult res = new PluginResult(Status.OK, lastLocation);
				callbackContext.sendPluginResult(res);
				callbackContext.success();
			}
			else
			{
				PluginResult res = new PluginResult(Status.OK, new JSONObject());
				callbackContext.sendPluginResult(res);
				callbackContext.success();
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	private BackgroundService service = null;

	private final ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected (ComponentName name, IBinder service)
		{
			System.out.println("mitja GPS onServiceConnected");
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
		System.out.println("mitja GPS startService");
		Activity context = cordova.getActivity();
		Intent intent = new Intent(context, BackgroundService.class);
		try {
			context.bindService(intent, connection, BIND_AUTO_CREATE);
			context.startService(intent);
		} catch (Exception e) {
			System.out.println("mitja " + e.getMessage());
		}
	}
	
	private void stopService()
	{
		System.out.println("mitja GPS stopService");
		Activity context = cordova.getActivity();
        Intent intent    = new Intent(context, BackgroundService.class);
        context.unbindService(connection);
        context.stopService(intent);
	}
	
	static JSONObject getSettings() {
        return settings;
    }
	
	private JSONObject getLastLocation()
    {
        JSONObject object = new JSONObject();
        if (service != null)
        {
            Location location = service.getLastLocation();
		try
		{
			object.put("latitude", String.valueOf(location.getLatitude()));
			object.put("longitude", String.valueOf(location.getLongitude()));
			object.put("altitude", String.valueOf(location.getAltitude()));
			object.put("accuracy", String.valueOf(location.getAccuracy()));
		}
		catch (JSONException e) {}
        }
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
	
	@Override
    public void onPause(boolean multitasking)
    {
        System.out.println("mitja CordovaPlugin onPause");
    }
	
	@Override
    public void onStop () {
        System.out.println("mitja CordovaPlugin onStop");
    }
	
	@Override
    public void onResume (boolean multitasking)
    {
        System.out.println("mitja CordovaPlugin onResume");
    }
	
	@Override
    public void onDestroy()
    {
        System.out.println("mitja CordovaPlugin onDestroy");
		stopService();
		//android.os.Process.killProcess(android.os.Process.myPid());
    }
	
	private void setSettings(JSONObject settings)
	{
		try
		{
			if (settings.has("user_id"))
			{
				this.settings.put("user_id", settings.getInt("user_id"));
			}
			if (settings.has("vehicle_id"))
			{
				this.settings.put("vehicle_id", settings.getInt("vehicle_id"));
			}
			if (settings.has("interval"))
			{
				this.settings.put("interval", settings.getInt("interval"));
			}
		}
		catch (JSONException e) {}
	}
}