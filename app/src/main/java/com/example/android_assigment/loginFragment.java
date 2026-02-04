package com.example.android_assigment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android_assigment_part2.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    public loginFragment() {}

    public static loginFragment newInstance(String param1, String param2) {
        loginFragment fragment = new loginFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> login());

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            try {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "failed to map to signup fragment", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // אם המשתמש כבר מחובר (Firebase שמר את הסשן) → ישר לבית
        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null) {
            String uid = current.getUid();

            // אופציונלי: לשמור uid ב-SharedPreferences (לא חובה, אבל נוח)
            requireContext()
                    .getSharedPreferences("session", Context.MODE_PRIVATE)
                    .edit()
                    .putString("uid", uid)
                    .apply();

            View view = getView();
            if (view != null) {
                NavController nav = Navigation.findNavController(view);

                // הגנה נגד ניווט כפול
                if (nav.getCurrentDestination() != null
                        && nav.getCurrentDestination().getId() == R.id.loginFragment) {
                    nav.navigate(R.id.action_loginFragment_to_homeFragment);
                }
            }
        }
    }

    private void login() {
        View fragmentView = getView();
        if (fragmentView == null) return;

        TextInputLayout emailLayout = fragmentView.findViewById(R.id.username_fill);
        TextInputLayout passLayout = fragmentView.findViewById(R.id.password_fill);

        String email = "";
        String password = "";

        if (emailLayout != null && emailLayout.getEditText() != null) {
            email = emailLayout.getEditText().getText().toString().trim();
        }
        if (passLayout != null && passLayout.getEditText() != null) {
            password = passLayout.getEditText().getText().toString();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (!isAdded()) return;

                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {
                            Toast.makeText(requireContext(), "login success but user is null", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String uid = user.getUid();

                        requireContext()
                                .getSharedPreferences("session", Context.MODE_PRIVATE)
                                .edit()
                                .putString("uid", uid)
                                .apply();

                        View view = getView();
                        if (view != null) {
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_loginFragment_to_homeFragment);
                        }

                    } else {
                        Toast.makeText(requireContext(),
                                "login failed: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
