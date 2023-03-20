package comp5216.sydney.edu.au.healthproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class energy_requirements_calculator extends AppCompatActivity {

    //UI
    EditText heightText;
    EditText weightText;
    EditText ageText;
    RadioButton chooseMale;
    RadioButton chooseFemale;
    Spinner spinner;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    Button button;

    private double height;
    private double weight;
    private int age;
    private String gender;
    private double PAL = 0.0;
    private String name;

    //values
    String activity_Level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_requirements_calculator);

        //UI
        heightText = (EditText) findViewById(R.id.editHeight);
        weightText= (EditText) findViewById(R.id.editWeight);
        ageText = (EditText) findViewById(R.id.editAge);
        chooseMale = (RadioButton) findViewById(R.id.radioButton);
        chooseFemale = (RadioButton) findViewById(R.id.radioButton2);
        spinner = (Spinner) findViewById(R.id.spinner);
        textView1 = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        button = (Button) findViewById(R.id.button2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRLNSDB.TTF");
        heightText.setTypeface(typeface);
        weightText.setTypeface(typeface);
        ageText.setTypeface(typeface);
        chooseMale.setTypeface(typeface);
        chooseFemale.setTypeface(typeface);
        textView1.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView3.setTypeface(typeface);
        textView4.setTypeface(typeface);
        button.setTypeface(typeface);

        //sinner's adapter
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String[] stringArray = getResources().getStringArray(R.array.Physical_Activity_Level);
                activity_Level = stringArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Intent intent = getIntent();
        name = intent.getStringExtra("username");
    }

    //choose male or female
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radioButton:
                if (checked) {
                    gender = "Male";
                    break;
                }
            case R.id.radioButton2:
                if (checked) {
                    gender = "Female";
                    break;
                }
        }
    }

    //spinner,choose activity level
    private double getActivityLevel(){
        if(activity_Level.contains("1.2")){
            PAL = 1.2;
        }
        else if(activity_Level.contains("1.4")){
            PAL = 1.4;
        }
        else if(activity_Level.contains("1.6")){
            PAL = 1.6;
        }
        else if(activity_Level.contains("1.8")){
            PAL = 1.8;
        }
        else if(activity_Level.contains("2.0")){
            PAL = 2.0;
        }
        else if(activity_Level.contains("2.2")){
            PAL = 2.2;
        }
        return PAL;
    }


    //The user submits the data and uses the formula to calculate the required heat based on the data
    public void onSubmitClick (View view) {
        //Check if the user has filled in all the data
        //If not, access is denied
        if(gender == null){
            Toast.makeText(energy_requirements_calculator.this, "You need to choose your gender", Toast.LENGTH_SHORT).show();
        }
        else if(gender.equalsIgnoreCase("Male")){
            if(heightText.getText().toString().equals("")){
                Toast.makeText(energy_requirements_calculator.this, "You need to input your height", Toast.LENGTH_SHORT).show();
            }
            else if(weightText.getText().toString().equals("")){
                Toast.makeText(energy_requirements_calculator.this, "You need to input your weight", Toast.LENGTH_SHORT).show();
            }
            else if(ageText.getText().toString().equals("")){
                Toast.makeText(energy_requirements_calculator.this, "You need to input your age", Toast.LENGTH_SHORT).show();
            }
            else {
                height = Double.valueOf(heightText.getText().toString());
                weight = Double.valueOf(weightText.getText().toString());
                age = Integer.valueOf(ageText.getText().toString());
                getActivityLevel();
                if(PAL == 0.0){
                    Toast.makeText(energy_requirements_calculator.this, "You need to choose your activity level", Toast.LENGTH_SHORT).show();
                } else {
                    //Preparing to add data to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference().child("Users");
                    myRef.child(name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {

                                    //Calculate the calories for men
                                    DataSnapshot dataSnapshot = task.getResult();
                                    String isSetRequire = String.valueOf(dataSnapshot.child("setRequire").getValue());
                                    double male_RMR = (66.5 + (13.75 * weight) + (5 * height) - (6.76 * age)) * PAL * 4.184;

                                    //Pass the information to the next activity
                                    Intent intent = new Intent(energy_requirements_calculator.this, FirstActivity.class);
                                    intent.putExtra("energy", male_RMR);
                                    intent.putExtra("gender", gender);
                                    intent.putExtra("username",name);
                                    intent.putExtra("isRequire",isSetRequire);

                                    //The user only needs to calculate at the first login
                                    //The next login will not calculate the user's required heat again
                                    myRef.child(name).child("setRequire").setValue("true");
                                    //The user's heat requirements are stored in the database
                                    myRef.child(name).child("requireIntake").setValue(male_RMR);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(energy_requirements_calculator.this, "Username is not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(energy_requirements_calculator.this, "Read Data Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
       }
        else if(gender.equalsIgnoreCase("Female")){
            if(heightText.getText().toString().equals("")){
                Toast.makeText(energy_requirements_calculator.this, "You need to input your height", Toast.LENGTH_SHORT).show();
            }
            else if(weightText.getText().toString().equals("")){
                Toast.makeText(energy_requirements_calculator.this, "You need to input your weight", Toast.LENGTH_SHORT).show();
            }
            else if(ageText.getText().toString().equals("")){
                Toast.makeText(energy_requirements_calculator.this, "You need to input your age", Toast.LENGTH_SHORT).show();
            }
            else {
                height = Double.valueOf(heightText.getText().toString());
                weight = Double.valueOf(weightText.getText().toString());
                age = Integer.valueOf(ageText.getText().toString());
                getActivityLevel();
                if(PAL == 0.0){
                    Toast.makeText(energy_requirements_calculator.this, "You need to choose your activity level", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference().child("Users");
                    myRef.child(name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    //Calculate the calories for men
                                    DataSnapshot dataSnapshot = task.getResult();
                                    String isSetRequire = String.valueOf(dataSnapshot.child("setRequire").getValue());
                                    double female_RMR = (66.5 + (9.56 * weight) + (1.85 * height) - (4.68 * age) * PAL) * 4.184;

                                    //Pass the information to the next activity
                                    Intent intent = new Intent(energy_requirements_calculator.this, FirstActivity.class);
                                    intent.putExtra("energy", female_RMR);
                                    intent.putExtra("gender", gender);
                                    intent.putExtra("username",name);
                                    intent.putExtra("isRequire",isSetRequire);

                                    //The user only needs to calculate at the first login
                                    //The next login will not calculate the user's required heat again
                                    myRef.child(name).child("setRequire").setValue("true");
                                    //The user's heat requirements are stored in the database
                                    myRef.child(name).child("requireIntake").setValue(female_RMR);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(energy_requirements_calculator.this, "Username is not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(energy_requirements_calculator.this, "Read Data Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }
}