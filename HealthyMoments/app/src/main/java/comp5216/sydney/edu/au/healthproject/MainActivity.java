package comp5216.sydney.edu.au.healthproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    Button login;
    Button signup;
    TextView title;
    String username;
    String password;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference root = db.getReference().child("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        login = (Button) findViewById(R.id.button5);
        signup = (Button) findViewById(R.id.button3);
        title = (TextView) findViewById(R.id.Title);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRLNSDB.TTF");
        usernameText.setTypeface(typeface);
        passwordText.setTypeface(typeface);
        login.setTypeface(typeface);
        signup.setTypeface(typeface);
        title.setTypeface(typeface);
    }

    //Perform user login
    private void loginCheck() {
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();

        //Check if the user has entered a username
        //Whether the user name exists in the database
        root.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String userPassword = String.valueOf(dataSnapshot.child("Password").getValue());
                        String isSetRequire = String.valueOf(dataSnapshot.child("setRequire").getValue());
                        //Check if the user enters a password
                        //If the password matches the database data
                        if(password.equals(userPassword)) {
                            if(isSetRequire.equalsIgnoreCase("false")) {
                                Intent intent = new Intent(MainActivity.this, energy_requirements_calculator.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                            } else{
                                String require = String.valueOf(dataSnapshot.child("requireIntake").getValue());
                                Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("isRequire",isSetRequire);
                                intent.putExtra("requireIntake",require);
                                startActivity(intent);
                                finish();
                            }
                        } else{
                            Toast.makeText(MainActivity.this, "Password is not correct", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Username is not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Read Data Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //onClick
    public void loginClick(View view){
        loginCheck();
    }
    // go to signup
    public void onSignUpClick(View view){
        Intent intent = new Intent (MainActivity.this, Signup_Activity.class);
        startActivity(intent);
        finish();
    }
}