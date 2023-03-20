package comp5216.sydney.edu.au.healthproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup_Activity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    EditText confirmText;
    Button signup;
    TextView title;
    Button back;

    private String username;
    private String password;
    private String confirmPassword;
    private String setRequire = "false";

    FirebaseDatabase db = FirebaseDatabase.getInstance("https://healthproject-92bb2-default-rtdb.firebaseio.com/");
    DatabaseReference root = db.getReference().child("Users");

    HashMap<String, String> userMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameText = (EditText) findViewById(R.id.usernameEdit);
        passwordText = (EditText) findViewById(R.id.passwordEdit);
        confirmText = (EditText) findViewById(R.id.confirmEdit);
        signup = (Button) findViewById(R.id.button4);
        title = (TextView) findViewById(R.id.SignTitle);
        back = (Button) findViewById(R.id.backbutton);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRLNSDB.TTF");
        usernameText.setTypeface(typeface);
        passwordText.setTypeface(typeface);
        confirmText.setTypeface(typeface);
        signup.setTypeface(typeface);
        title.setTypeface(typeface);
        back.setTypeface(typeface);
    }

    //Check that the password entered twice is the same
    private boolean confirmPassword() {
        password = passwordText.getText().toString();
        confirmPassword = confirmText.getText().toString();
        if (password.equalsIgnoreCase(confirmPassword)) {
            return true;
        } else {
            Toast.makeText(getBaseContext(), "Password not same", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void existUsername() {
        username = usernameText.getText().toString();
        //Check if the user enters the user name
        //Check if the user name is used
        root.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(Signup_Activity.this, "Username is exist", Toast.LENGTH_SHORT).show();
                    } else {
                        if (confirmPassword()) {
                            userMap.put("Username", username);
                            userMap.put("Password", password);
                            userMap.put("setRequire", setRequire);
                            root.child(username).setValue(userMap);
                            userMap.clear();
                            Intent intent = new Intent(Signup_Activity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(Signup_Activity.this, "Read Data Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //onClick
    public void onCreateAccountClick(View view) {
        existUsername();
    }

    public void goBack(View view){
        Intent intent = new Intent(Signup_Activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}