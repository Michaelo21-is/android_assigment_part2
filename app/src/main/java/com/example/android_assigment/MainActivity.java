package com.example.android_assigment;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.android_assigment_part2.R;
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void login(){
        String email = ((EditText) findViewById(R.id.username_fill)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_fill)).getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "login success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void reg() {
        String email = ((EditText) findViewById(R.id.email_filler)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_filler)).getText().toString();
        String username = ((EditText) findViewById(R.id.username_filler)).getText().toString();
        String firstname = ((EditText) findViewById(R.id.firstname_filler)).getText().toString();
        String lastname = ((EditText) findViewById(R.id.lastname_filler)).getText().toString();

        User user = new User(email, password, username, firstname, lastname);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeData();
                            Toast.makeText(MainActivity.this, "register success", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "register failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    public void writeData(){
        // Write a message to the database
        String email = ((EditText) findViewById(R.id.email_filler)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_filler)).getText().toString();
        String username = ((EditText) findViewById(R.id.username_filler)).getText().toString();
        String firstname = ((EditText) findViewById(R.id.firstname_filler)).getText().toString();
        String lastname = ((EditText) findViewById(R.id.lastname_filler)).getText().toString();
        User user = new User(firstname,  lastname,  email,  password,  username);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(email);
        myRef.setValue(user);
    }
}