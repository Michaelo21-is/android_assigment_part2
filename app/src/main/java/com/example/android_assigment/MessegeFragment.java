package com.example.android_assigment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;

import com.android_assigment_part2.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessegeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessegeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String DB_URL =
            "https://androidassigment-1b7b2-default-rtdb.europe-west1.firebasedatabase.app";

    private RecyclerView msgRv;
    private MessegeAdapter messegeAdapter;
    private final List<Messege> messages = new ArrayList<>();

    private String groupId;
    private String groupName;
    private String chatId;   // לצ'אט עם חבר
    private String friendId;

    private EditText inputMessageEt;
    private MaterialButton sendBtn;

    // שם המשתמש מתוך Realtime Database (users/{uid}/username)
    private String currentUsername;

    public MessegeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessegeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessegeFragment newInstance(String param1, String param2) {
        MessegeFragment fragment = new MessegeFragment();
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

            groupId = getArguments().getString("groupId");
            groupName = getArguments().getString("groupName");
            chatId = getArguments().getString("chatId");
            friendId = getArguments().getString("friendId");
            if (groupName == null) groupName = "חבר";
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_messege, container, false);
        //כפתור חזור חזרה
        ImageButton goBackBtn = root.findViewById(R.id.go_back_btn_msg);
        goBackBtn.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_messegeFragment_to_homeFragment);
        });
        TextView groupNameTv = root.findViewById(R.id.group_name_msg);
        groupNameTv.setText(groupName != null ? groupName : "חבר");

        msgRv = root.findViewById(R.id.msg_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        // מציג את ההודעות כך שהחדשות למטה ומתחיל מהתחתית (כמו וואטסאפ)
        // ביצובע הרסייקל ויוו
        layoutManager.setStackFromEnd(true);
        msgRv.setLayoutManager(layoutManager);
        messegeAdapter = new MessegeAdapter(messages);
        msgRv.setAdapter(messegeAdapter);

        inputMessageEt = root.findViewById(R.id.input_messge_msg);
        sendBtn = root.findViewById(R.id.send_btn_msg);

        sendBtn.setOnClickListener(v -> sendMessage());
        // שליחת הודעה גם בלחיצת Enter/Send במקלדת (כמו בצ'אט קבוצה)
        inputMessageEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // טוען את שם המשתמש מה-Realtime Database לפי ה-UID
        loadCurrentUsername();

        if (groupId != null) {
            loadMessagesForGroup();
        } else if (chatId != null) {
            loadMessagesForFriend();
        }

        return root;
    }

    /**
     * טוען את כל ההודעות של הקבוצה מפיירבייס ומציג אותן מהחדשה לישנה.
     */
    private void loadMessagesForGroup() {
        if (groupId == null || groupId.trim().isEmpty()) {
            return;
        }

        DatabaseReference groupRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("groupChats")
                .child(groupId)
                .child("listOfMesseges");

        // מאזין לרשימת ההודעות של הקבוצה בריל-טיים
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // נועד כדי שלא יתווצר כפיליות
                messages.clear();
                // עובר על כל ההודעות של חברים או קבוצות שם אותם במערך של ההודעות ולאחר מכאן מעדכן את האדפטר
                for (DataSnapshot msgChild : snapshot.getChildren()) {
                    Messege m = msgChild.getValue(Messege.class);
                    if (m != null) {
                        messages.add(m);
                    }
                }
                // מתריע לאדפטר סיימתי לקרוא מהשרת ותתחיל לעבוד
                messegeAdapter.notifyDataSetChanged();

                // גלילה לתחתית העמוד כמו בווטסאפ
                if (!messages.isEmpty()) {
                    msgRv.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // אפשר להוסיף Toast אם תרצה
            }
        });
    }

    /**
     * טוען את הודעות צ'אט עם חבר מ-chatWithFriend/{chatId}/listOfMesseges.
     */
    private void loadMessagesForFriend() {
        if (chatId == null || chatId.trim().isEmpty()) {
            return;
        }
        DatabaseReference messagesRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("chatWithFriend")
                .child(chatId)
                .child("listOfMesseges");

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot msgChild : snapshot.getChildren()) {
                    Messege m = msgChild.getValue(Messege.class);
                    if (m != null) messages.add(m);
                }
                messegeAdapter.notifyDataSetChanged();
                if (!messages.isEmpty()) {
                    msgRv.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /**
     * קריאת ה-username מהטבלה users/{uid}/username ושמירתו במשתנה currentUsername.
     */
    private void loadCurrentUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();
        // טעינת היוזרניים לפי האיידי של השתמש
        DatabaseReference userRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("users")
                .child(uid)
                .child("username");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUsername = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    // שליחת הודעה בודק לפני לאיזה סוג של צאט לשלוח
    private void sendMessage() {
        DatabaseReference messagesRef = null;
        // בודק לאיפה לשים את הרפרנס אם זה לגרופ צאט או לצאט וויט פרינדס
        if (groupId != null && !groupId.trim().isEmpty()) {
            messagesRef = FirebaseDatabase.getInstance(DB_URL)
                    .getReference("groupChats")
                    .child(groupId)
                    .child("listOfMesseges");
        } else if (chatId != null && !chatId.trim().isEmpty()) {
            messagesRef = FirebaseDatabase.getInstance(DB_URL)
                    .getReference("chatWithFriend")
                    .child(chatId)
                    .child("listOfMesseges");
        }

        String text = inputMessageEt.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String userNameForMessage = currentUsername;
        if (userNameForMessage == null || userNameForMessage.trim().isEmpty()) {
            userNameForMessage = user.getEmail() != null ? user.getEmail() : user.getUid();
        }

        // ספרייה של הזמנים
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Messege newMsg = new Messege(time, date, userNameForMessage, text);

        // יוצר איידי ייחודי להודעה
        String newKey = messagesRef.push().getKey();
        if (newKey == null) {
            return;
        }
        // שומר את הצאט בדטא בייס
        messagesRef.child(newKey).setValue(newMsg)
                .addOnSuccessListener(unused -> inputMessageEt.setText(""))
                .addOnFailureListener(e -> { });
    }
}