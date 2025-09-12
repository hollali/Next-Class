package com.ahofama.nextclass;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessagingFragment extends Fragment {

    private static final String ARG_CHAT_ID = "chat_id";
    private String chatId;

    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private LinearLayout emptyStateLayout;

    // Header
    private ImageView ivBack;
    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvUserStatus;
    private ImageView ivMore;

    // Input
    private EditText etMessage;
    private ImageButton btnAttachment;
    private ImageButton btnEmoji;
    private FloatingActionButton btnSend;

    // Firebase
    private DatabaseReference messagesRef;
    private String currentUserId;

    public MessagingFragment() { }

    public static MessagingFragment newInstance(String chatId) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAT_ID, chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatId = getArguments().getString(ARG_CHAT_ID);
        }
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messagesRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(chatId)
                .child("messages");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        setupTextWatcher();
        listenForMessages();

        return view;
    }

    private void initViews(View view) {
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

        ivBack = view.findViewById(R.id.ivBack);
        ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserStatus = view.findViewById(R.id.tvUserStatus);
        ivMore = view.findViewById(R.id.ivMore);

        etMessage = view.findViewById(R.id.etMessage);
        btnAttachment = view.findViewById(R.id.btnAttachment);
        btnEmoji = view.findViewById(R.id.btnEmoji);
        btnSend = view.findViewById(R.id.btnSend);

        tvUserName.setText("Chat");  // you can fetch chat participants later
        tvUserStatus.setText("Active now");
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);

        adapter = new MessagesAdapter(getContext(), messageList);
        recyclerMessages.setAdapter(adapter);

        updateEmptyState();
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        btnEmoji.setOnClickListener(v -> etMessage.append("😊"));
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupTextWatcher() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSend.setAlpha(s.toString().trim().isEmpty() ? 0.5f : 1.0f);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) return;

        String messageId = messagesRef.push().getKey();
        Message newMessage = new Message(currentUserId, messageText, System.currentTimeMillis());

        if (messageId != null) {
            messagesRef.child(messageId).setValue(newMessage);
        }

        etMessage.setText("");
    }

    private void listenForMessages() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerMessages.scrollToPosition(messageList.size() - 1);
                    updateEmptyState();
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String prev) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String prev) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateEmptyState() {
        if (messageList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerMessages.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerMessages.setVisibility(View.VISIBLE);
        }
    }
}
