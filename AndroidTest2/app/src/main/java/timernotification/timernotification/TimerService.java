package timernotification.timernotification;


//DEVELOPER NAME : BHALANI SANJAY

//PROJECT NAME : TimerNotification

//COMPANY NAME : SILICON IT HUB PVT. LTD. Ahmedabad.

//PURPOSE : THIS CLASS IS USED TO

//MODIFY DATE : 26/3/18


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

//public class TimerService extends JobIntentService
//public class TimerService extends JobIntentService
public class TimerService extends JobIntentService//Service
{

    private static final String TAG = TimerService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 1;

    // Service binder
    private final IBinder serviceBinder = new RunServiceBinder();
    private TimerTask timerTask;
    private Timer timer;
    public class RunServiceBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //if(intent!=null)
        countDownTimer();
        Log.d("Flags:",flags+"");
        //return Service.START_CONTINUATION_MASK;
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Binding service");
        }
        return serviceBinder;
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        countDownTimer(); // for Android O
    }
    private void countDownTimer()
    {
        new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
               millisUntilFinished=getSharedPreferences("sp",MODE_PRIVATE).getLong("timer",0);
               if(millisUntilFinished!=0) {
                   long min = (millisUntilFinished / 1000) / 60;
                   long second = (millisUntilFinished - (min * 1000) * 60) / 1000;
                   String times = "0" + min + ":";
                   if (second < 10) {
                       times += "0" + second;
                   } else {
                       times += second;
                   }
                   notification(times + "");
                   millisUntilFinished=millisUntilFinished-1000;
                   SharedPreferences sp=getSharedPreferences("sp",MODE_PRIVATE);
                   sp.edit().putLong("timer",millisUntilFinished).commit();
               }else
               {
                   SharedPreferences sp=getSharedPreferences("sp",MODE_PRIVATE);
                   sp.edit().putLong("timer",300000).commit();
               }
            }
            public void onFinish() {

                JobScheduler jobScheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE );
                jobScheduler.cancelAll();
                notification("00");
            }
        }.start();
    }
    private void notification(String s)
    {
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        //String s= Calendar.getInstance().get(Calendar.MINUTE)+":"+Calendar.getInstance().get(Calendar.SECOND);
        contentView.setTextViewText(R.id.title, "You are on the break");
        contentView.setTextViewText(R.id.text, "Time left:"+s);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = "my_channel_01";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOnlyAlertOnce(true)
                .setContent(contentView);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}

