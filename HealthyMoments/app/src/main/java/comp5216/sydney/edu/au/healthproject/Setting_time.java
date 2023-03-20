package comp5216.sydney.edu.au.healthproject;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Setting_time extends AppCompatActivity {

    TextView breakfastTime;
    TextView morningSportTime;
    TextView lunchTime;
    TextView noonSportTime;
    TextView dinnerTime;
    TextView nightSportTime;
    TextView title;
    TextView morning;
    TextView noon;
    TextView evening;
    Button breakfast;
    Button morningSport;
    Button lunch;
    Button noonSport;
    Button dinner;
    Button eveningSport;
    Button back;


    String buttonName;

    TimePickerDialog timePickerDialog;

    Calendar calendar;
    int currentHour;
    int currentMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time);
        breakfastTime = (TextView) findViewById(R.id.breakfasttime);
        morningSportTime = (TextView) findViewById(R.id.morningsporttime);
        lunchTime = (TextView) findViewById(R.id.lunchtime);
        noonSportTime = (TextView) findViewById(R.id.noonsporttime);
        dinnerTime = (TextView) findViewById(R.id.dinnertime);
        nightSportTime = (TextView) findViewById(R.id.nightsporttime);
        title = (TextView) findViewById(R.id.textView5);
        morning = (TextView) findViewById(R.id.textView8);
        noon = (TextView) findViewById(R.id.textView9);
        evening = (TextView) findViewById(R.id.textView10);
        breakfast = (Button) findViewById(R.id.setMorning1);
        morningSport = (Button) findViewById(R.id.setMorning2);
        lunch = (Button) findViewById(R.id.setNoon1);
        noonSport = (Button) findViewById(R.id.setNoon2);
        dinner = (Button) findViewById(R.id.setNight1);
        eveningSport = (Button) findViewById(R.id.setNight2);
        back = (Button) findViewById(R.id.button6);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRLNSDB.TTF");
        breakfastTime.setTypeface(typeface);
        morningSportTime.setTypeface(typeface);
        lunchTime.setTypeface(typeface);
        noonSportTime.setTypeface(typeface);
        dinnerTime.setTypeface(typeface);
        nightSportTime.setTypeface(typeface);
        title.setTypeface(typeface);
        morning.setTypeface(typeface);
        noon.setTypeface(typeface);
        evening.setTypeface(typeface);
        breakfast.setTypeface(typeface);
        morningSport.setTypeface(typeface);
        lunch.setTypeface(typeface);
        noonSport.setTypeface(typeface);
        dinner.setTypeface(typeface);
        eveningSport.setTypeface(typeface);
        back.setTypeface(typeface);
    }

    public void breakfastOnClick(View view){
        buttonName = "breakfast";
        setTimeOnClick(buttonName);
    }
    public void morningSportOnClick(View view){
        buttonName = "morningSport";
        setTimeOnClick(buttonName);
    }
    public void lunchOnClick(View view){
        buttonName = "lunch";
        setTimeOnClick(buttonName);
    }
    public void noonSportOnClick(View view){
        buttonName = "noonSport";
        setTimeOnClick(buttonName);
    }
    public void dinnerOnClick(View view){
        buttonName = "dinner";
        setTimeOnClick(buttonName);
    }
    public void nightSportOnClick(View view){
        buttonName = "nightSport";
        setTimeOnClick(buttonName);
    }

    private void setTimeOnClick(String buttonName){
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(Setting_time.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                //Get the time set by the user in HH:mm format
                if(buttonName.equals("breakfast")){
                    String time = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
                    breakfastTime.setText(time);
                    //timeMap.put(buttonName,time);
                    setAlarmClock(buttonName);
                }
                else if(buttonName.equals("morningSport")){
                    String time = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
                    morningSportTime.setText(time);
                    //timeMap.put(buttonName,time);
                    setAlarmClock(buttonName);
                }
                else if(buttonName.equals("lunch")){
                    String time = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
                    lunchTime.setText(time);
                    //timeMap.put(buttonName,time);
                    setAlarmClock(buttonName);
                }
                else if(buttonName.equals("noonSport")){
                    String time = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
                    noonSportTime.setText(time);
                   // timeMap.put(buttonName,time);
                    setAlarmClock(buttonName);
                }
                else if(buttonName.equals("dinner")){
                    String time = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
                    dinnerTime.setText(time);
                   // timeMap.put(buttonName,time);
                    setAlarmClock(buttonName);
                }
                else{
                    String time = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
                    nightSportTime.setText(time);
                   //timeMap.put(buttonName,time);
                    setAlarmClock(buttonName);
                }
            }
        } ,currentHour,currentMinute,false);
        timePickerDialog.show();
    }

    //Go to the alarm clock screen to view the set alarms
    private void setAlarmClock(String buttonName){
        if(buttonName.equals("breakfast")){
            String time = breakfastTime.getText().toString();
            String[] timeArray = time.split(":");
            String hour = timeArray[0];
            String minute = timeArray[1];
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR,Integer.parseInt(hour));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minute));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE,"eat breakfast");
            startActivity(intent);
        }
        else if(buttonName.equals("morningSport")){
            String time = morningSportTime.getText().toString();
            String[] timeArray = time.split(":");
            String hour = timeArray[0];
            String minute = timeArray[1];
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR,Integer.parseInt(hour));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minute));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE,"do morning sport");
            startActivity(intent);
        }
        else if(buttonName.equals("lunch")){
            String time = lunchTime.getText().toString();
            String[] timeArray = time.split(":");
            String hour = timeArray[0];
            String minute = timeArray[1];
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR,Integer.parseInt(hour));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minute));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE,"eat lunch");
            startActivity(intent);
        }
        else if(buttonName.equals("noonSport")){
            String time = noonSportTime.getText().toString();
            String[] timeArray = time.split(":");
            String hour = timeArray[0];
            String minute = timeArray[1];
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR,Integer.parseInt(hour));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minute));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE,"do noon sport");
            startActivity(intent);
        }
        else if(buttonName.equals("dinner")){
            String time = dinnerTime.getText().toString();
            String[] timeArray = time.split(":");
            String hour = timeArray[0];
            String minute = timeArray[1];
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR,Integer.parseInt(hour));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minute));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE,"eat dinnner");
            startActivity(intent);
        }
        else{
            String time = nightSportTime.getText().toString();
            String[] timeArray = time.split(":");
            String hour = timeArray[0];
            String minute = timeArray[1];
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR,Integer.parseInt(hour));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minute));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE,"do night sport");
            startActivity(intent);
        }
    }

    public void goBack(View view){
        finish();
    }
}