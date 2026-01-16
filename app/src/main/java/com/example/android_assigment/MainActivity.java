package com.example.android_assigment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

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

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
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
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment loginFragment = null;
        if (navHostFragment != null && !navHostFragment.getChildFragmentManager().getFragments().isEmpty()) {
            loginFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        }
        View fragmentView = loginFragment != null ? loginFragment.getView() : null;

        TextInputEditText emailEdit = fragmentView != null ? fragmentView.findViewById(R.id.username_fill) : null;
        TextInputEditText passwordEdit = fragmentView != null ? fragmentView.findViewById(R.id.password_fill) : null;

        String email = emailEdit != null && emailEdit.getText() != null ? emailEdit.getText().toString() : "";
        String password = passwordEdit != null && passwordEdit.getText() != null ? passwordEdit.getText().toString() : "";


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
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment signupFragment = null;
        if (navHostFragment != null && !navHostFragment.getChildFragmentManager().getFragments().isEmpty()) {
            signupFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        }
        View fragmentView = signupFragment != null ? signupFragment.getView() : null;

        TextInputEditText emailEdit = fragmentView != null ? fragmentView.findViewById(R.id.email_filler) : null;
        TextInputEditText passwordEdit = fragmentView != null ? fragmentView.findViewById(R.id.password_filler) : null;
        TextInputEditText usernameEdit = fragmentView != null ? fragmentView.findViewById(R.id.username_filler) : null;
        TextInputEditText firstnameEdit = fragmentView != null ? fragmentView.findViewById(R.id.firstname_filler) : null;
        TextInputEditText lastnameEdit = fragmentView != null ? fragmentView.findViewById(R.id.lastname_filler) : null;

        String email = emailEdit != null && emailEdit.getText() != null ? emailEdit.getText().toString() : "";
        String password = passwordEdit != null && passwordEdit.getText() != null ? passwordEdit.getText().toString() : "";
        String username = usernameEdit != null && usernameEdit.getText() != null ? usernameEdit.getText().toString() : "";
        String firstname = firstnameEdit != null && firstnameEdit.getText() != null ? firstnameEdit.getText().toString() : "";
        String lastname = lastnameEdit != null && lastnameEdit.getText() != null ? lastnameEdit.getText().toString() : "";

        final User user = new User(firstname, lastname, email, password, username);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeData(user);
                            Toast.makeText(MainActivity.this, "register success", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "register failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void writeData(User user){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(user);
    }
}
