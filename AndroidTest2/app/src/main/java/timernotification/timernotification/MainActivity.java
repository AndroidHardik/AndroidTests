package timernotification.timernotification;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int JOB_ID=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isMyServiceRunning(MainActivity.this,TimerService.class)) {
            SharedPreferences sp=getSharedPreferences("sp",MODE_PRIVATE);
            sp.edit().putLong("timer",300000).commit();
            startService(new Intent(MainActivity.this, TimerService.class));
        }
    }
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
