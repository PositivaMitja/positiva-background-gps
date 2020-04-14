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

public class BackgroundService extends Service
{
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                System.out.println("mitja " + locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        getLastLocation();
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
                            if (task.isSuccessful() && task.getResult() != null) {
                                System.out.println("mitja location " + task.getResult());
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            System.out.println("mitja Lost location permission.");
        }
    }
}