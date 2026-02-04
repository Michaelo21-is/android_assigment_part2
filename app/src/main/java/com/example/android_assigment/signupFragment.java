package com.example.android_assigment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android_assigment_part2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class signupFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    public signupFragment() {
        // Required empty public constructor
    }

    public static signupFragment newInstance(String param1, String param2) {
        signupFragment fragment = new signupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance(
                "https://androidassigment-1b7b2-default-rtdb.europe-west1.firebasedatabase.app"
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        Button registerButton = view.findViewById(R.id.register_button);
        Button backButton = view.findViewById(R.id.back_to_login);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Navigation.findNavController(v)
                            .navigate(R.id.action_signupFragment_to_loginFragment);
                } catch (Exception e) {
                    Toast.makeText(requireContext(),
                            "failed to map to login fragment",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg();
            }
        });

        return view;
    }

    private void reg() {
        View fragmentView = getView();
        if (fragmentView == null) return;

        TextInputEditText emailEdit = fragmentView.findViewById(R.id.email_filler);
        TextInputEditText passwordEdit = fragmentView.findViewById(R.id.password_filler);
        TextInputEditText usernameEdit = fragmentView.findViewById(R.id.username_filler);
        TextInputEditText firstnameEdit = fragmentView.findViewById(R.id.firstname_filler);
        TextInputEditText lastnameEdit = fragmentView.findViewById(R.id.lastname_filler);

        String email = (emailEdit != null && emailEdit.getText() != null)
                ? emailEdit.getText().toString().trim() : "";

        String password = (passwordEdit != null && passwordEdit.getText() != null)
                ? passwordEdit.getText().toString() : "";

        String username = (usernameEdit != null && usernameEdit.getText() != null)
                ? usernameEdit.getText().toString().trim() : "";

        String firstname = (firstnameEdit != null && firstnameEdit.getText() != null)
                ? firstnameEdit.getText().toString().trim() : "";

        String lastname = (lastnameEdit != null && lastnameEdit.getText() != null)
                ? lastnameEdit.getText().toString().trim() : "";

        final User user = new User(firstname, lastname, email, password, username);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeData(user);
                            Toast.makeText(requireContext(), "register success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(),
                                    "register failed: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void writeData(User user) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "DB write failed: user not logged in", Toast.LENGTH_LONG).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.getReference("users")
                .child(uid)
                .setValue(user)
                .addOnSuccessListener(unused ->
                        Toast.makeText(requireContext(), "DB write success", Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "DB write failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
