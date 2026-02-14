package com.example.android_assigment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String DB_URL =
            "https://androidassigment-1b7b2-default-rtdb.europe-west1.firebasedatabase.app";

    private EditText searchInput;
    private RadioGroup searchByGameRadio;
    private RecyclerView searchRv;
    private SearchAdapter searchAdapter;
    private final List<SearchItem> results = new ArrayList<>();
    private List<SearchItem> pendingUsers = new ArrayList<>();
    private List<SearchItem> pendingGroups = new ArrayList<>();
    private boolean usersDone = false;
    private boolean groupsDone = false;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton goBackBtn = view.findViewById(R.id.go_back_btn_search);
        goBackBtn.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_searchFragment_to_homeFragment);
        });

        searchInput = view.findViewById(R.id.search_input);
        searchByGameRadio = view.findViewById(R.id.search_by_game);
        Button clearGameBtn = view.findViewById(R.id.clear_game_filter_btn);
        searchRv = view.findViewById(R.id.searchrv);
        searchRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchAdapter = new SearchAdapter(results, this::onAddSearchItemClicked);
        searchRv.setAdapter(searchAdapter);

        // הקלדה מסירה אוטומטית את בחירת הרדיו ומבצעת חיפוש לפי טקסט
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchByGameRadio.clearCheck();
                String query = s.toString().trim();
                performPrefixSearch(query);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // בחירת משחק מציגה רק קבוצות עם אותו GameTopic
        searchByGameRadio.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                clearGameBtn.setVisibility(View.GONE);
                results.clear();
                searchAdapter.notifyDataSetChanged();
                return;
            }
            clearGameBtn.setVisibility(View.VISIBLE);
            GameTopic topic = getGameTopicFromRadioId(checkedId);
            if (topic != null) {
                performSearchByGame(topic);
            }
        });

        // הסרת סינון לפי משחק בלי להקליד
        clearGameBtn.setOnClickListener(v -> {
            searchByGameRadio.clearCheck();
            clearGameBtn.setVisibility(View.GONE);
            results.clear();
            searchAdapter.notifyDataSetChanged();
        });

        return view;
    }

    private GameTopic getGameTopicFromRadioId(int radioId) {
        if (radioId == R.id.game_apex) return GameTopic.APEXLEGENDS;
        if (radioId == R.id.game_cs) return GameTopic.COUNTERSTRIKE;
        if (radioId == R.id.game_fortnite) return GameTopic.FORTINTE;
        if (radioId == R.id.game_fc26) return GameTopic.FC26;
        return null;
    }

    /**
     * מציג רק קבוצות עם ה-GameTopic שנבחר (בלי משתמשים).
     */
    private void performSearchByGame(GameTopic selectedTopic) {
        results.clear();
        usersDone = true;
        groupsDone = false;
        pendingUsers = new ArrayList<>();

        final String topicName = selectedTopic.name();

        DatabaseReference groupsRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("groupChats");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SearchItem> groupResults = new ArrayList<>();
                for (DataSnapshot groupSnap : snapshot.getChildren()) {
                    String gameTopicVal = groupSnap.child("gameTopic").getValue(String.class);
                    if (topicName.equals(gameTopicVal)) {
                        String groupName = groupSnap.child("groupName").getValue(String.class);
                        if (groupName != null) {
                            String trimmed = groupName.trim();
                            if (!trimmed.isEmpty()) {
                                String groupId = groupSnap.getKey();
                                groupResults.add(new SearchItem(
                                        SearchItem.Type.GROUP,
                                        groupId,
                                        trimmed
                                ));
                            }
                        }
                    }
                }
                mergeAndNotify(null, groupResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /**
     * מבצע חיפוש פריפיקס על טבלת המשתמשים (username) ועל טבלת הקבוצות (groupName).
     * מציג משתמשים וקבוצות יחד אחרי ששתי השאילתות מסתיימות.
     */
    private void performPrefixSearch(String query) {
        if (query.isEmpty()) {
            results.clear();
            searchAdapter.notifyDataSetChanged();
            return;
        }

        // ניקוי והכנה לחיפוש חדש
        results.clear();
        usersDone = false;
        groupsDone = false;
        final String lowerQuery = query.toLowerCase();

        DatabaseReference usersRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("users");
        DatabaseReference groupsRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("groupChats");

        // טעינת כל המשתמשים ופילטר בצד לקוח (פריפיקס לא תלוי רישיות) – כמו ב-groupName
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SearchItem> userResults = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null && user.getUsername() != null) {
                        String username = user.getUsername().trim();
                        if (username.isEmpty()) continue;
                        if (!username.toLowerCase().startsWith(lowerQuery)) continue;
                        String uid = userSnap.getKey();
                        userResults.add(new SearchItem(
                                SearchItem.Type.USER,
                                uid,
                                username
                        ));
                    }
                }
                mergeAndNotify(userResults, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SearchItem> groupResults = new ArrayList<>();
                for (DataSnapshot groupSnap : snapshot.getChildren()) {
                    String groupName = groupSnap.child("groupName").getValue(String.class);
                    if (groupName != null) {
                        String trimmed = groupName.trim();
                        if (trimmed.isEmpty()) continue;
                        if (!trimmed.toLowerCase().startsWith(lowerQuery)) continue;
                        String groupId = groupSnap.getKey();
                        groupResults.add(new SearchItem(
                                SearchItem.Type.GROUP,
                                groupId,
                                trimmed
                        ));
                    }
                }
                mergeAndNotify(null, groupResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /**
     * לחיצה על פלוס: משתמש – יוצר/מעדכן צ'אט בטבלת chatWithFriend;
     * קבוצה – מוסיף את ה-uid של המשתמש המחובר ל-membersID של הקבוצה.
     */
    private void onAddSearchItemClicked(SearchItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "אתה לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }
        String myUid = user.getUid();

        if (item.getType() == SearchItem.Type.USER) {
            addChatWithFriend(myUid, item.getId(), item.getDisplayName());
        } else {
            addUserToGroup(myUid, item.getId(), item.getDisplayName());
        }
    }

    /** יוצר רשומה בטבלת chatWithFriend אם עדיין אין צ'אט עם החבר */
    private void addChatWithFriend(String myUid, String friendUid, String friendDisplayName) {
        if (myUid.equals(friendUid)) {
            Toast.makeText(requireContext(), "לא ניתן להוסיף את עצמך", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference chatRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("chatWithFriend");

        // בודקים אם כבר קיים צ'אט (בכל כיוון)
        chatRef.orderByChild("userId").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chatSnap : snapshot.getChildren()) {
                    String fid = chatSnap.child("friendId").getValue(String.class);
                    if (friendUid.equals(fid)) {
                        Toast.makeText(requireContext(), "כבר קיים צ'אט עם " + friendDisplayName, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                chatRef.orderByChild("userId").equalTo(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        for (DataSnapshot chatSnap : snapshot2.getChildren()) {
                            String fid = chatSnap.child("friendId").getValue(String.class);
                            if (myUid.equals(fid)) {
                                Toast.makeText(requireContext(), "כבר קיים צ'אט עם " + friendDisplayName, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        String chatId = chatRef.push().getKey();
                        if (chatId == null) return;
                        ChatWithFriend chat = new ChatWithFriend(friendUid, myUid, new ArrayList<>());
                        chat.setChatId(chatId);
                        chatRef.child(chatId).setValue(chat)
                                .addOnSuccessListener(unused ->
                                        Toast.makeText(requireContext(), "נוסף צ'אט עם " + friendDisplayName, Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /** מוסיף את המשתמש המחובר ל-membersID של הקבוצה ומעדכן sumOfMember */
    private void addUserToGroup(String myUid, String groupId, String groupName) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("groupChats").child(groupId);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "הקבוצה לא נמצאה", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> members = new ArrayList<>();
                DataSnapshot membersSnap = snapshot.child("membersID");
                if (membersSnap.exists()) {
                    for (DataSnapshot m : membersSnap.getChildren()) {
                        String id = m.getValue(String.class);
                        if (id != null) members.add(id);
                    }
                }
                if (members.contains(myUid)) {
                    Toast.makeText(requireContext(), "אתה כבר בקבוצה " + groupName, Toast.LENGTH_SHORT).show();
                    return;
                }
                members.add(myUid);
                Integer sumVal = snapshot.child("sumOfMember").getValue(Integer.class);
                final int sum = sumVal != null ? sumVal : 0;
                groupRef.child("membersID").setValue(members)
                        .addOnSuccessListener(unused -> {
                            groupRef.child("sumOfMember").setValue(sum + 1)
                                    .addOnSuccessListener(u ->
                                            Toast.makeText(requireContext(), "הצטרפת לקבוצה " + groupName, Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(requireContext(), "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(requireContext(), "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "שגיאה: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mergeAndNotify(List<SearchItem> userResults, List<SearchItem> groupResults) {
        if (userResults != null) {
            pendingUsers = userResults;
            usersDone = true;
        }
        if (groupResults != null) {
            pendingGroups = groupResults;
            groupsDone = true;
        }
        if (!usersDone || !groupsDone) return;

        results.clear();
        results.addAll(pendingUsers);
        results.addAll(pendingGroups);
        usersDone = false;
        groupsDone = false;
        searchAdapter.notifyDataSetChanged();
    }
}