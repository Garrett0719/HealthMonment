package comp5216.sydney.edu.au.healthproject;

import static comp5216.sydney.edu.au.healthproject.AllData.morningFoodList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class FoodListViewActivity extends Activity implements View.OnClickListener {
    private String extra_data;
    private GridView gd;
    private GridviewAdapter mAdapter;
    private ArrayList<Food> foods;
    private String SP_INFO = "";
    private TextView mydate;
    private TextView tv1;
    private Button bt_sumit;
    private int checkNum; // record check list num
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        extra_data = intent.getStringExtra("extra_data");
        Date d = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String dateNowStr = sdf.format(d);
        SP_INFO = dateNowStr;

        System.out.println(dateNowStr);

        SharedPreferences sp =getSharedPreferences(dateNowStr+"_food", MODE_PRIVATE);
        String data=sp.getString("food_data",null);
        System.out.println(data);
        if(data!=null)
            checkNum = data.split(",").length;

        setContentView(R.layout.food_view);
        initView();
        initData();
        mydate.setText(dateNowStr);
        tv1.setText(extra_data.toUpperCase()+" FOOD LIST");

        Intent intent1 = getIntent();
        name = intent1.getStringExtra("username");
    }

    //Initializing data
    private void initData() {
        Food mFood;

        for (int i = 1; i <=morningFoodList.length; i++) {
            mFood = new Food();
            mFood.setName(morningFoodList[i-1].split("-")[0]);
            // mPerson.setId(Character.valueOf((char)(i+65))+" ");
            mFood.setId(i + "");
            mFood.setPower(Integer.parseInt(morningFoodList[i-1].split("-")[1]));
            foods.add(mFood);
        }
        mAdapter = new GridviewAdapter(foods, this,extra_data);
        gd.setAdapter(mAdapter);
    }


    //Initializing controls
    private void initView() {
        gd = (GridView) findViewById(R.id.gd);



        bt_sumit = (Button) findViewById(R.id.bt_submit);
        foods = new ArrayList<Food>();
        mydate = (TextView) findViewById(R.id.mydate);
        tv1=(TextView) findViewById(R.id.tv1);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRLNSDB.TTF");
        bt_sumit.setTypeface(typeface);
        mydate.setTypeface(typeface);
        tv1.setTypeface(typeface);

        bt_sumit.setOnClickListener(this);
    }

    //submit
    private void submit() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String dateNowStr = sdf.format(d);
        SharedPreferences sp = getSharedPreferences(SP_INFO+"_food", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(Tool.list.size()>0) {
            String value = "";
            for(Map.Entry<Integer, Integer> entry : Tool.list.entrySet()){
                value+=entry.getKey()+"-"+entry.getValue()+",";
            }
                value = value.substring(0, value.length() - 1);
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myRef = database.getReference().child("Record");

            myRef.child(name).child(dateNowStr+"-"+extra_data+"Food_data").setValue(value);
            editor.putString(extra_data+"Food_data", value);
            editor.commit();
            AlertDialog.Builder builder2 = new AlertDialog.Builder(FoodListViewActivity.this);
            builder2.setMessage("save successful");
            builder2.show();
        }else{
            AlertDialog.Builder builder2 = new AlertDialog.Builder(FoodListViewActivity.this);
            builder2.setMessage("please select");
            builder2.show();
        }
        Tool.list.clear();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                submit();
                break;
        }
    }
}
