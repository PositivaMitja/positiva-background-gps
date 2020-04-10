package si.positiva.plugins.background_gps;

import android.app.Service;
import android.os.Binder;

public class BackgroundService extends Service
{
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
        System.out.println("mitja start background");
    }
}