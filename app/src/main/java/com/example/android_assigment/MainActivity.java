package com.example.android_assigment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android_assigment_part2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance(
                "https://androidassigment-1b7b2-default-rtdb.europe-west1.firebasedatabase.app"
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void login() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        Fragment loginFragment = null;
        if (navHostFragment != null && !navHostFragment.getChildFragmentManager().getFragments().isEmpty()) {
            loginFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        }

        View fragmentView = loginFragment != null ? loginFragment.getView() : null;

        TextInputLayout emailLayout = fragmentView.findViewById(R.id.username_fill);
        TextInputLayout passLayout  = fragmentView.findViewById(R.id.password_fill);

        String email = "";
        String password = "";

        if (emailLayout != null && emailLayout.getEditText() != null) {
            email = emailLayout.getEditText().getText().toString().trim();
        }
        if (passLayout != null && passLayout.getEditText() != null) {
            password = passLayout.getEditText().getText().toString();
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "login success", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "login failed: " +
                                (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void reg() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

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

        String email = (emailEdit != null && emailEdit.getText() != null) ? emailEdit.getText().toString().trim() : "";
        String password = (passwordEdit != null && passwordEdit.getText() != null) ? passwordEdit.getText().toString() : "";
        String username = (usernameEdit != null && usernameEdit.getText() != null) ? usernameEdit.getText().toString().trim() : "";
        String firstname = (firstnameEdit != null && firstnameEdit.getText() != null) ? firstnameEdit.getText().toString().trim() : "";
        String lastname = (lastnameEdit != null && lastnameEdit.getText() != null) ? lastnameEdit.getText().toString().trim() : "";

        final User user = new User(firstname, lastname, email, password, username);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeData(user); // ✅ כותב ל-RTDB
                            Toast.makeText(MainActivity.this, "register success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "register failed: " +
                                    (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void writeData(User user) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "DB write failed: user not logged in", Toast.LENGTH_LONG).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ✅ כותבים דרך db עם ה-URL הנכון
        db.getReference("users")
                .child(uid)
                .setValue(user)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "DB write success", Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "DB write failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
