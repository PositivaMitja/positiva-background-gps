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

public class BackgroundService extends Service
{
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
	private NotificationManager notificationManager;

	class BackgroundBinder extends Binder
    {
        BackgroundService getService()
        {
            return BackgroundService.this;
        }
    }
	@Override
    public void onCreate()
    {
        super.onCreate();
		keepAwake();
		System.out.println("mitja create");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                System.out.println("mitja locs " + locationResult.getLastLocation());
            }
        };
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel("mitja01", "mitja", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        createLocationRequest();
        //getLastLocation();
		requestLocationUpdates();
    }
	@Override
    public IBinder onBind (Intent intent) {
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
        super.onDestroy();
    }
    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
		System.out.println("mitja onStartCommand");
        return START_STICKY;
    }
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void getLastLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
							System.out.println("mitja OnCompleteListener");
                            if (task.isSuccessful() && task.getResult() != null) {
                                System.out.println("mitja location " + task.getResult());
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            System.out.println("mitja Lost location permission.");
        }
    }
	public void onLowMemory() {
		System.out.println("mitja onLowMemory");
    }
	public void onTrimMemory(int level) {
		System.out.println("mitja onTrimMemory" + level);
		startForeground(123456789, getNotification());
    }
	public boolean onUnbind(Intent intent) {
		System.out.println("mitja onUnbind");
		startForeground(123456789, getNotification());
        return true;
    }
	public void onTaskRemoved(Intent rootIntent) {
		System.out.println("mitja onTaskRemoved");
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
		Intent intentNew = new Intent("io.ionic.start");
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                intentNew, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(1, "mitja start",
                        activityPendingIntent)
                .addAction(1, "mitja cancel",
                        servicePendingIntent)
                .setContentText("mitja text")
                .setContentTitle("mitja title")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(1)
                .setTicker("mitja text")
                .setWhen(System.currentTimeMillis());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("mitja01"); // Channel ID
        }
        return builder.build();
    }
}