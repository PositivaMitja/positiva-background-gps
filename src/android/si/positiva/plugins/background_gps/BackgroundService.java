package si.positiva.plugins.background_gps;

import android.app.Service;
import android.os.Binder;
import android.os.IBinder;
import android.content.Intent;
import android.os.PowerManager;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import android.annotation.SuppressLint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import android.location.Location;
import com.google.android.gms.tasks.Task;
import android.support.annotation.NonNull;
import android.os.Looper;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.os.Build;
import org.json.JSONObject;
import org.json.JSONArray;
import android.os.AsyncTask;
import android.content.Context;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import android.os.Environment;
import java.util.Date;

public class BackgroundService extends Service
{
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
	private NotificationManager notificationManager;
	private Location lastLocation;
	private boolean tracking = false;
	private int notificationId;

	class BackgroundBinder extends Binder
    {
        BackgroundService getService()
        {
			System.out.println("mitja back getService");
            return BackgroundService.this;
        }
    }
	
	public void setTracking(boolean tracking)
	{
		System.out.println("mitja settracking " + tracking);
		this.tracking = tracking;
	}
	
	@Override
    public void onCreate()
    {
        super.onCreate();
		System.out.println("mitja onCreate");
		JSONObject settings = BackgroundGPS.getSettings();
		if (settings.length() == 0)
		{
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
	public void onRealCreate()
	{
		keepAwake();
		//JSONObject settings = BackgroundGPS.getSettings();
		/*if (settings.length() > 0)
		{*/
		//System.out.println("mitja json " + settings.toString());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
				lastLocation = locationResult.getLastLocation();
                System.out.println("mitja geo " + lastLocation.getLatitude() + "|" + lastLocation.getLongitude());
				if (tracking)
				{
					new Thread(new Runnable(){
						@Override
						public void run() {
							try
							{
								FileWriter writeFile = new FileWriter(new File(BackgroundGPS.getSettings().getString("file_path").replace("file://", "")), true);
								JSONObject log = new JSONObject();
								log.put("timestamp", String.valueOf(new Date().getTime()));
								log.put("latitude", String.valueOf(lastLocation.getLatitude()));
								log.put("longitude", String.valueOf(lastLocation.getLongitude()));
								log.put("altitude", String.valueOf(lastLocation.getAltitude()));
								log.put("accuracy", String.valueOf(lastLocation.getAccuracy()));
								log.put("altitudeAccuracy", String.valueOf(lastLocation.getVerticalAccuracyMeters()));
								log.put("heading", String.valueOf(lastLocation.getBearing()));
								log.put("speed", String.valueOf(lastLocation.getSpeed()));
								//jsonContent.put(log);
								writeFile.write(log.toString() + "\n");
								writeFile.close();
							}
							catch (IOException e) { System.out.println("mitja io " + e.getMessage()); }
							catch (JSONException e) { System.out.println("mitja json " + e.getMessage()); }
						}
					}).start();
					new Thread(new Runnable(){
						@Override
						public void run() {
							try
							{
								JSONObject settings = BackgroundGPS.getSettings();
								String param = "{\"vehicle_id\":" + settings.getString("vehicle_id") + ",";
								param += "\"user_id\":" + settings.getString("user_id") + ",";
								param += "\"latitude\":" + String.valueOf(lastLocation.getLatitude()) + ",";
								param += "\"longitude\":" + String.valueOf(lastLocation.getLongitude()) + ",";
								param += "\"altitude\":" + String.valueOf(lastLocation.getAltitude()) + ",";
								param += "\"accuracy\":" + String.valueOf(lastLocation.getAccuracy()) + ",";
								param += "\"altitudeAccuracy\":" + String.valueOf(lastLocation.getVerticalAccuracyMeters()) + ",";
								param += "\"heading\":" + String.valueOf(lastLocation.getBearing()) + ",";
								param += "\"speed\":" + String.valueOf(lastLocation.getSpeed()) + ",";
								param += "\"tracking_datetime\":" + String.valueOf(new Date().getTime()) + "}";
								HttpURLConnection connection = (HttpURLConnection) new URL(settings.getString("api_url")).openConnection();
								connection.setRequestMethod("POST");
								connection.setRequestProperty("Content-Type", "application/json");
								System.out.println("mitja api " + settings.getString("api_url") + param);
								OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
								writer.write(param);
								writer.flush();
								writer.close();
								String responseString = IOUtils.toString(connection.getInputStream());
								System.out.println(responseString);
							}
							catch (MalformedURLException e) { System.out.println("mitja 1 " + e.getMessage()); }
							catch (IOException e) { System.out.println("mitja 2 " +e.getMessage()); }
							catch (JSONException e) { System.out.println("mitja 3 " +e.getMessage()); }
						}
					}).start();
				}
            }
        };
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel("mitja01", "mitja", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        createLocationRequest();
		System.out.println("mitja back onCreate end");
		/*}
		else
		{
			android.os.Process.killProcess(android.os.Process.myPid());
		}*/
    }
	@Override
    public IBinder onBind (Intent intent) {
		System.out.println("mitja back onBind");
		onRealCreate();
        return binder;
    }
	private final IBinder binder = new BackgroundBinder();
	@SuppressLint("WakelockTimeout")
    private void keepAwake()
    {
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PARTIAL_WAKE_LOCK, "BackgroundService:wakelock");
        wakeLock.acquire();
    }
	private PowerManager.WakeLock wakeLock;
	@Override
    public void onDestroy()
    {
		System.out.println("mitja onDestroy");
		JSONObject settings = BackgroundGPS.getSettings();
		if (settings.length() > 0)
		{
		stopForeground(true);
        notificationManager.cancel(notificationId);
		fusedLocationClient.removeLocationUpdates(locationCallback);
		System.out.println("mitja onDestroy end");
		}
		super.onDestroy();
    }
    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
		System.out.println("mitja back onStartCommand");
		requestLocationUpdates();
		notificationId = (int) new Date().getTime();
		startForeground(notificationId, getNotification());
        return START_STICKY;
    }
    private void createLocationRequest() {
		/*try
		{*/
			locationRequest = new LocationRequest();
			/*locationRequest.setInterval(BackgroundGPS.getSettings().getInt("interval") * 1000);
			locationRequest.setFastestInterval((BackgroundGPS.getSettings().getInt("interval") * 1000) - 1000);
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setSmallestDisplacement(10);*/
			locationRequest.setInterval(10000);
			locationRequest.setFastestInterval(5000);
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			//locationRequest.setSmallestDisplacement(1);
		/*}
		catch (JSONException e) { }*/
    }
    public Location getLastLocation() {
        return lastLocation;
    }
	public void onLowMemory() {
		System.out.println("mitja onLowMemory");
    }
	public void onTrimMemory(int level) {
		System.out.println("mitja onTrimMemory" + level);
    }
	public boolean onUnbind(Intent intent) {
		System.out.println("mitja onUnbind");
		//fusedLocationClient.removeLocationUpdates(locationCallback);
        return true;
    }
	public void onTaskRemoved(Intent rootIntent) {
		System.out.println("mitja onTaskRemoved");
		//onDestroy();
    }
	public void requestLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            System.out.println("mitja requestLocationUpdates " + unlikely);
        }
    }
	private Notification getNotification() {
        Intent intent = new Intent(this, BackgroundService.class);
        intent.putExtra("si.positiva.plugins.background_gps.started_from_notification", true);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
		Intent intentNew = new Intent("si.salus.tms");
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                intentNew, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(getResources().getIdentifier("icon", "drawable", getPackageName()), "mitja start",
                        activityPendingIntent)
                .addAction(getResources().getIdentifier("icon", "drawable", getPackageName()), "mitja cancel",
                        servicePendingIntent)
                .setContentText("mitja text")
                .setContentTitle("mitja title")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(getResources().getIdentifier("icon", "drawable", getPackageName()))
                .setTicker("mitja text")
                .setWhen(System.currentTimeMillis());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("mitja01"); // Channel ID
        }
        return builder.build();
    }
}