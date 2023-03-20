package comp5216.sydney.edu.au.healthproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Intake_MainActivity extends AppCompatActivity {

    private Button morningButton;
    private Button morningButton1;
    private Button noonButton;
    private Button noonButton1;
    private Button eveningButton;
    private Button eveningButton1;
    private TextView morningStatus;
    private TextView noonStatus;
    private TextView eveningStatus;
    private TextView morning;
    private TextView noon;
    private TextView evening;
    private String name;
    private Button back;
    @Override
    protected void onStart() {
        super.onStart();
        Date d = new Date();
        System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        SharedPreferences sp =getSharedPreferences(dateNowStr+"_food", MODE_PRIVATE);
        SharedPreferences sp1 =getSharedPreferences(dateNowStr+"_sport", MODE_PRIVATE);
        System.out.println(sp.getString("morningFood_data", null));
        if (checkNull(sp.getString("morningFood_data", null))||checkNull(sp1.getString("morningSport_data", null))) {
            morningStatus.setText("customized");
        } else {
            morningStatus.setText("not customized");
        }
        if (checkNull(sp.getString("noonFood_data", null))||checkNull(sp1.getString("noonSport_data", null))) {
            noonStatus.setText("customized");
        } else {
            noonStatus.setText("not customized");
        }if (checkNull(sp.getString("eveningFood_data", null))||checkNull(sp1.getString("eveningSport_data", null))) {
            eveningStatus.setText("customized");
        } else {
            eveningStatus.setText("not customized");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake_main);

        morningButton = (Button) findViewById(R.id.morning_button);
        morningButton1 = (Button) findViewById(R.id.morning_button1);
        noonButton=(Button) findViewById(R.id.noon_button);
        noonButton1=(Button) findViewById(R.id.noon_button1);
        eveningButton=(Button) findViewById(R.id.evening_button);
        eveningButton1=(Button) findViewById(R.id.evening_button1);
        morningStatus=(TextView) findViewById(R.id.morning_status);
        noonStatus=(TextView) findViewById(R.id.noon_status);
        eveningStatus=(TextView) findViewById(R.id.evening_status);
        morning=(TextView) findViewById(R.id.morning);
        noon=(TextView) findViewById(R.id.noon);
        evening=(TextView) findViewById(R.id.evening);
        back=(Button) findViewById(R.id.button7);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRLNSDB.TTF");
        morningButton.setTypeface(typeface);
        morningButton1.setTypeface(typeface);
        noonButton.setTypeface(typeface);
        noonButton1.setTypeface(typeface);
        eveningButton.setTypeface(typeface);
        eveningButton1.setTypeface(typeface);
        morningStatus.setTypeface(typeface);
        noonStatus.setTypeface(typeface);
        eveningStatus.setTypeface(typeface);
        morning.setTypeface(typeface);
        noon.setTypeface(typeface);
        evening.setTypeface(typeface);
        back.setTypeface(typeface);

        Intent intent = getIntent();
        name = intent.getStringExtra("username");

        morningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intake_MainActivity.this, FoodListViewActivity.class);
                intent.putExtra("extra_data", "morning");
                intent.putExtra("username",name);
                startActivityForResult(intent,1);
            }
        });
        morningButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intake_MainActivity.this, SportListViewActivity.class);
                intent.putExtra("extra_data", "morning");
                intent.putExtra("username",name);
                startActivityForResult(intent,1);
            }
        });
        noonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intake_MainActivity.this, FoodListViewActivity.class);
                intent.putExtra("extra_data", "noon");
                intent.putExtra("username",name);
                startActivityForResult(intent,1);
            }
        });
        noonButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intake_MainActivity.this, SportListViewActivity.class);
                intent.putExtra("extra_data", "noon");
                intent.putExtra("username",name);
                startActivityForResult(intent,1);
            }
        });
        eveningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intake_MainActivity.this, FoodListViewActivity.class);
                intent.putExtra("extra_data", "evening");
                intent.putExtra("username",name);
                startActivityForResult(intent,1);
            }
        });
        eveningButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intake_MainActivity.this, SportListViewActivity.class);
                intent.putExtra("extra_data", "evening");
                intent.putExtra("username",name);
                startActivityForResult(intent,1);
            }
        });
    }
    public boolean checkNull(String key){
        if(key==null){
            return false;
        }
        for(String i :key.split(",")){
            if(Integer.valueOf(i.split("-")[1])>0){
                return true;
            }
        }
        return false;
    }

    public void goBack(View view){
        finish();
    }


}