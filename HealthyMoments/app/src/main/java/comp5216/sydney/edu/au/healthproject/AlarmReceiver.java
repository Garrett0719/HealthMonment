package comp5216.sydney.edu.au.healthproject;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Set the FirstActivity screen to open automatically for the user when the user confirms the notification
        Intent i = new Intent(context,FirstActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

        //Set notification properties
        //Notice Title
        //Notice Information


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"healthApp")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("HealthApp Notification") //
                .setContentText("One day will pass, please don't forget record your intake today!")

                //Notifications are automatically cancelled if the user does not click on them in time
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                //Notification priority
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(01,builder.build());
    }
}
