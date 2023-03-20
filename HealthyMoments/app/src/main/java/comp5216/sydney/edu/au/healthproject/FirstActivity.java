package comp5216.sydney.edu.au.healthproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class FirstActivity extends Activity implements OnChartValueSelectedListener, OnChartGestureListener {
    private TextView prompt;
    private TextView foodNum;
    private TextView sportNum;
    private Button button1;
    private TextView requireNum;
    private TextView requireText;
    private TextView title;
    private TextView intake;
    private TextView consume;
    private Button clock;
    private TextView nowIntake;
    private TextView weather;
    private TextView step;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private long timestamp;
    private static Handler handler;
    private SensorManager sensorManager;
    private Thread detectorTimeStampUpdaterThread;
    private boolean isRunning = true;
    final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 99;
    private String name;
    LineChart chart;
    private ArrayList<Entry> yValues = new ArrayList<>();
    private Calendar nowDate;
    private static String requireIntake;
    private static int require;

    //Assigning tasks to WorkManager
    //Successful execution returns true
    public class myWorker extends Worker{
        public myWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            updateGraph();
            return Result.success();
        }
    }

    //Update Line Chart Data
    private void updateGraph(){
        Calendar calendar = Calendar.getInstance();
        int nowMonth = calendar.get(Calendar.YEAR);
        int nowDay = calendar.get(Calendar.MONTH)+1;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String[] energyNum= requireNum.getText().toString().split(" ");
        Float intakenum = Float.parseFloat(energyNum[0]);
        float month = (float) nowMonth ;
        float day = 0;

        //Converting dates to decimal form
        if(nowDay < 10){
            day = (float) (nowDay/100);
        } else{
            day = (float) (nowDay/10);
        }
        float dateF = month+day;

        //If the array already has 30 elements, it means that a month has passed
        //Clear out the old data
        if(yValues.size() == 30){
            yValues.clear();
        }
        //Data is added to the array, using a line graph representation
        yValues.add(new Entry(dateF,intakenum));
        LineDataSet set1 = new LineDataSet(yValues, "Data set 1");
        //Set the properties of the line chart
        set1.setFillAlpha(110);
        set1.setColor(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.GREEN);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        chart.setData(data);
    }




    @Override
    protected void onStart() {

        super.onStart();
        setK();
        requestData();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
        createNotificationChannel();
        button1=(Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstActivity.this, Intake_MainActivity.class);
                intent.putExtra("username",name);
                startActivity(intent);
            }
        });
        chart = (LineChart) findViewById(R.id.chart);

        chart.setOnChartGestureListener( FirstActivity.this);
        chart.setOnChartValueSelectedListener(FirstActivity.this);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        prompt= (TextView) findViewById(R.id.prompt);
        foodNum = (TextView) findViewById(R.id.food_num);
        sportNum = (TextView) findViewById(R.id.sport_num);
        requireNum = (TextView) findViewById(R.id.require_num);
        requireText = (TextView) findViewById(R.id.requireText);
        title= (TextView) findViewById(R.id.title);
        intake = (TextView) findViewById(R.id.intake);
        consume = (TextView) findViewById(R.id.consume);
        nowIntake = (TextView) findViewById(R.id.now);
        weather = (TextView) findViewById(R.id.textView6);
        clock = (Button) findViewById(R.id.graphButton);
        step = (TextView) findViewById(R.id.textView7);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRLNSDB.TTF");
        button1.setTypeface(typeface);
        prompt.setTypeface(typeface);
        foodNum.setTypeface(typeface);
        sportNum.setTypeface(typeface);
        requireNum.setTypeface(typeface);
        requireText.setTypeface(typeface);
        title.setTypeface(typeface);
        intake.setTypeface(typeface);
        consume.setTypeface(typeface);
        nowIntake.setTypeface(typeface);
        weather.setTypeface(typeface);
        clock.setTypeface(typeface);
        step.setTypeface(typeface);

        Intent intent = getIntent();
        String gender = intent.getStringExtra("gender");
        String isRequire = intent.getStringExtra("isRequire");
        name = intent.getStringExtra("username");
        if(isRequire == null){
            requireText.setText(requireIntake + " K" + " today");
        }
        else if(isRequire.equalsIgnoreCase("false")) {
            require = (int) intent.getDoubleExtra("energy", 0);
            requireText.setText(String.valueOf(require) + " K" + " today");
        } else{
            double require = Double.valueOf(intent.getStringExtra("requireIntake"));
            requireIntake = String.format("%.2f",require);
            requireText.setText(requireIntake + " K" + " today");
        }

        setAlarm();

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerForSensorEvents();
        setupDetectorTimestampUpdaterThread();

        //Setting constraints on WorkManager
        //Only if there is a network connection
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(true)
                .build();

        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        //Only performed at 23:59 every day
        dueDate.set(Calendar.HOUR_OF_DAY,23);
        dueDate.set(Calendar.MINUTE,59);
        dueDate.set(Calendar.SECOND,0);

        if(dueDate.before(currentDate)){
            dueDate.add(Calendar.HOUR_OF_DAY,24);
        }

        //Set the run delay, i.e. when to start the first task (use schedule time - current time)
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                //The run interval is one day
                .Builder(myWorker.class,24,TimeUnit.HOURS)
                //Set the run delay, i.e. when to start the first task (use schedule time - current time)
                .setInitialDelay(timeDiff,TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag("Line Graph update")
                .build();

        //Continuously adding tasks to the queue
        WorkManager.getInstance(this)
                .enqueue(periodicWorkRequest);

        //viewGraph();

    }

    private void viewGraph(){
        yValues.add(new Entry(0,60f));
        yValues.add(new Entry(1,40f));
        yValues.add(new Entry(2,50f));
        yValues.add(new Entry(3,30f));
        yValues.add(new Entry(4,20f));
        yValues.add(new Entry(5,70f));
        yValues.add(new Entry(6,80f));

        LineDataSet set1 = new LineDataSet(yValues, "Data set 1");

        set1.setFillAlpha(110);

        set1.setColor(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.GREEN);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        chart.setData(data);
    }

    //Step sensor implementation
    public void registerForSensorEvents(){
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float steps = sensorEvent.values[0];
                step.setText("Number of steps taken " + (int) steps);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                timestamp = System.currentTimeMillis()
                        + (sensorEvent.timestamp - SystemClock.elapsedRealtimeNanos()) /
                        1000000L;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        },sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_UI);
    }

    private void setupDetectorTimestampUpdaterThread(){
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
            }
        };
        detectorTimeStampUpdaterThread = new Thread() {
            @Override
            public void run(){
                while(isRunning){
                    try{
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        detectorTimeStampUpdaterThread.start();
    }


    @Override
    protected void onPause(){
        super.onPause();
        isRunning = false;
        detectorTimeStampUpdaterThread.interrupt();
    }

    public void goAlarmClock(View view){
        Intent intent = new Intent(FirstActivity.this,Setting_time.class);
        startActivity(intent);
    }

    private void setAlarm() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY,23);
        ca.set(Calendar.MINUTE,0);
        ca.set(Calendar.SECOND,0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,ca.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "HealthApp Reminder Channel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("healthApp",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void requestData() {
        String textUrl = "http://wthrcdn.etouch.cn/weather_mini?city=%E6%B5%B7%E5%8F%A3" ;
        HttpUtil.sendOkHttpRequest(textUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // thread back to the main thread, otherwise the system handover is misaligned
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(e);
                        //If the data capture fails, check the android:usesCleartextTraffic="true" in the Application node in Mainfest
                        //This problem is caused by "http:"
                        Toast.makeText(FirstActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                // thread back to the main thread, otherwise the system handover is misaligned
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject=JSONObject.parseObject(responseText);
                        System.out.println(responseText);

                        requestData1(jsonObject.getJSONObject("data").getString("ganmao"));


                    }
                });
            }

        });
    }
    public void requestData1(String data) {
        String textUrl = "https://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i="+data ;
        HttpUtil.sendOkHttpRequest(textUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // thread back to the main thread, otherwise the system handover is misaligned
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(e);
                        //If the data capture fails, check the android:usesCleartextTraffic="true" in the Application node in Mainfest
                        //This problem is caused by "http:"
                        Toast.makeText(FirstActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                // thread back to the main thread, otherwise the system handover is misaligned
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject=JSONObject.parseObject(responseText);

                        prompt.setText(jsonObject.getJSONArray("translateResult").getJSONArray(0).getJSONObject(0).getString("tgt"));


                    }
                });
            }

        });
    }
    public void setK(){
        Date d = new Date();
        System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        SharedPreferences sp =getSharedPreferences(dateNowStr+"_food",MODE_PRIVATE);
        System.out.println("_________");
        String data1=sp.getString("morningFood_data",null);
        String data2=sp.getString("noonFood_data",null);
        String data3=sp.getString("eveningFood_data",null);
        SharedPreferences sp1 =getSharedPreferences(dateNowStr+"_sport", MODE_PRIVATE);
        String data4=sp1.getString("morningSport_data",null);
        String data5=sp1.getString("noonSport_data",null);
        String data6=sp1.getString("eveningSport_data",null);
        String[] allFoodData={data1,data2,data3};
        String[] allSportData={data4,data5,data6};
        System.out.println(data1);
        int foodK=0;
        int sportK=0;
        nowDate = Calendar.getInstance();
        Calendar updateDate = Calendar.getInstance();
        if(updateDate.after(nowDate)){
            foodK = 0;
            nowDate = updateDate;
        }

        for(String key : allFoodData){
            if(key!=null) {
                for (String key1 : key.split(",")) {
                    foodK += Float.valueOf(key1.split("-")[1]) / 100 * Integer.valueOf(AllData.morningFoodList[Integer.valueOf(key1.split("-")[0])].split("-")[1]);
                }
            }
        }
        for(String key : allSportData){
            if(key!=null) {
                for (String key1 : key.split(",")) {
                    sportK += Float.valueOf(key1.split("-")[1]) / 10 * Integer.valueOf(AllData.morningSportList[Integer.valueOf(key1.split("-")[0])].split("-")[1]);
                }
            }
        }

        foodNum.setText(foodK+" K");
        sportNum.setText(sportK+" K");
        requireNum.setText((foodK - sportK) + " K");
    }

    //The build method required for the interface can be ignored
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}