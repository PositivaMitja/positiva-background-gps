package si.positiva.plugins.background_gps;

import android.app.Service;
import android.os.Binder;
import android.os.IBinder;
import android.content.Intent;
import android.os.PowerManager;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import android.annotation.SuppressLint;

public class BackgroundService extends Service
{
	class BackgroundBinder extends Binder
    {
        BackgroundService getService()
        {
			System.out.println("mitja BackgroundService getService");
            return BackgroundService.this;
        }
    }
	@Override
    public void onCreate()
    {
        super.onCreate();
        System.out.println("mitja start background");
		keepAwake();
    }
	@Override
    public IBinder onBind (Intent intent) {
		System.out.println("mitja IBinder");
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
}