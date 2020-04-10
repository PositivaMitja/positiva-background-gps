package si.positiva.plugins.background_gps;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.apache.cordova.CallbackContext;
import android.app.Activity;
import android.content.Intent;
import static android.content.Context.BIND_AUTO_CREATE;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import si.positiva.plugins.background_gps.BackgroundService.BackgroundBinder;

public class BackgroundGPS extends CordovaPlugin 
{
	public BackgroundGPS() {}
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
	{
		if (action.equals("test")) 
		{
			System.out.println("mitja dela");
			startService();
		}
		return true;
	}

	private BackgroundService service;

	private final ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected (ComponentName name, IBinder service)
		{
			System.out.println("mitja onServiceConnected");
			BackgroundBinder binder = (BackgroundBinder) service;
			System.out.println("mitja binder");
			BackgroundGPS.this.service = binder.getService();
			System.out.println("mitja getService");
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
			System.out.println("mitja bind");
			context.bindService(intent, connection, BIND_AUTO_CREATE);
			System.out.println("mitja startService");
			context.startService(intent);
			System.out.println("mitja intent");
		} catch (Exception e) {
			System.out.println("mitja " + e.getMessage());
		}
	}
}