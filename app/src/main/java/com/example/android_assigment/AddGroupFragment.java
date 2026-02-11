package com.example.android_assigment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android_assigment_part2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class AddGroupFragment extends Fragment {

    private static final String DB_URL =
            "https://androidassigment-1b7b2-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_add_group, container, false);

        EditText groupName = root.findViewById(R.id.group_name_fill);
        EditText groupDescription = root.findViewById(R.id.group_description_fill);

        ImageButton goBackBtn = root.findViewById(R.id.go_back_btn_add);
        goBackBtn.setOnClickListener(v -> {
                    NavHostFragment.findNavController(AddGroupFragment.this)
                            .navigate(R.id.action_addGroupFragment_to_homeFragment);
                });

        RadioGroup gameGroup = root.findViewById(R.id.game_group);
        Button submitButton = root.findViewById(R.id.submit_add_group_button);

        submitButton.setOnClickListener(v -> {
            String groupNameText = groupName.getText().toString().trim();
            String groupDescriptionText = groupDescription.getText().toString().trim();

            if (groupNameText.isEmpty()) {
                Toast.makeText(requireContext(), "תכתוב שם קבוצה", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = gameGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "תבחר משחק", Toast.LENGTH_SHORT).show();
                return;
            }

            GameTopic chosenGame;
            if (selectedId == R.id.game_apex) {
                chosenGame = GameTopic.APEXLEGENDS;
            } else if (selectedId == R.id.game_cs) {
                chosenGame = GameTopic.COUNTERSTRIKE;
            } else if (selectedId == R.id.game_fortnite) {
                chosenGame = GameTopic.FORTINTE; // אם תרצה: תשנה enum ל-FORTNITE גם
            } else if (selectedId == R.id.game_fc26) {
                chosenGame = GameTopic.FC26;
            } else {
                Toast.makeText(requireContext(), "משחק לא מוכר", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(requireContext(), "אתה לא מחובר", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = user.getUid();

            GroupChat groupChat = new GroupChat(
                    chosenGame,
                    groupNameText,
                    groupDescriptionText,
                    1,
                    Collections.singletonList(uid),  // managementId
                    Collections.singletonList(uid),  // membersID
                    new ArrayList<>()                // listOfMesseges
            );

            DatabaseReference groupsRef = FirebaseDatabase
                    .getInstance(DB_URL)
                    .getReference("groupChats");

            String groupId = groupsRef.push().getKey();
            if (groupId == null) {
                Toast.makeText(requireContext(), "שגיאה ביצירת מזהה קבוצה", Toast.LENGTH_SHORT).show();
                return;
            }

            groupsRef.child(groupId).setValue(groupChat)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(requireContext(), "הקבוצה נשמרה", Toast.LENGTH_SHORT).show();

                        // ניווט אחרי הצלחה:
                        if (isAdded()) {
                            NavHostFragment.findNavController(AddGroupFragment.this)
                                    .navigate(R.id.action_addGroupFragment_to_homeFragment);
                            // או: .popBackStack(); אם אתה רוצה פשוט לחזור אחורה
                        }
                    })

                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "שמירה נכשלה: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );

        });

        return root;
    }
}
