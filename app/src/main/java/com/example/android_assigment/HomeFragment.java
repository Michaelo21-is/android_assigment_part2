package com.example.android_assigment;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android_assigment_part2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private static final String DB_URL =
            "https://androidassigment-1b7b2-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView usernameTv = view.findViewById(R.id.greet_username);

        usernameTv.setText("טוען...");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            usernameTv.setText("אורח");
            return view;
        }

        String uid = user.getUid();
        Log.d("HOME", "uid=" + uid);

        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL)
                .getReference("users")
                .child(uid)
                .child("username");

        Button button = view.findViewById(R.id.add_group_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Navigation.findNavController(v)
                            .navigate(R.id.action_homeFragment_to_addGroupFragment);

                } catch (Exception e) {
                    Toast.makeText(requireContext(),
                            "failed to map to add group fragment",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("HOME", "exists=" + snapshot.exists() + " value=" + snapshot.getValue());

                String name = snapshot.getValue(String.class);
                if (name == null || name.trim().isEmpty()) {
                    usernameTv.setText("משתמש");
                } else {
                    usernameTv.setText(name + "שלום, ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HOME", "db error=" + error.getMessage());
                usernameTv.setText("שגיאה: " + error.getMessage());
            }
        });

        return view;
    }
}
