package com.ahofama.nextclass;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessagingFragment connected to Firebase Firestore.
 */
public class MessagingFragment extends Fragment {

    private static final String ARG_USER_NAME = "user_name";
    private String userName;

    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private LinearLayout emptyStateLayout;
    private LinearLayout typingIndicator;

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

    // Typing animation
    private View dot1, dot2, dot3;
    private ObjectAnimator animator1, animator2, animator3;

    private static final String CHANNEL_ID = "messages_channel";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    // Firebase
    private FirebaseFirestore db;

    public MessagingFragment() {}

    public static MessagingFragment newInstance(String userName) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_USER_NAME);
        }
        db = FirebaseFirestore.getInstance();
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
        createNotificationChannel();
        requestNotificationPermission();
        listenForMessages(); // 👈 listen for real-time updates

        return view;
    }

    private void initViews(View view) {
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        typingIndicator = view.findViewById(R.id.typingIndicator);

        ivBack = view.findViewById(R.id.ivBack);
        ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserStatus = view.findViewById(R.id.tvUserStatus);
        ivMore = view.findViewById(R.id.ivMore);

        etMessage = view.findViewById(R.id.etMessage);
        btnAttachment = view.findViewById(R.id.btnAttachment);
        btnEmoji = view.findViewById(R.id.btnEmoji);
        btnSend = view.findViewById(R.id.btnSend);

        /*dot1 = view.findViewById(R.id.dot1);
        dot2 = view.findViewById(R.id.dot2);
        dot3 = view.findViewById(R.id.dot3);*/

        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
            tvUserStatus.setText("Online");
        } else {
            tvUserName.setText("Study Group");
            tvUserStatus.setText("3 members online");
        }
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

        Map<String, Object> message = new HashMap<>();
        message.put("sender", "You"); // TODO: replace with actual user
        message.put("text", messageText);
        message.put("timestamp", System.currentTimeMillis());

        db.collection("chats")
                .document("chat_1") // 👉 use dynamic chatId if you have multiple
                .collection("messages")
                .add(message);

        etMessage.setText("");
    }

    private void listenForMessages() {
        db.collection("chats")
                .document("chat_1") // same chatId as above
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;
                        if (snapshots == null) return;

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String sender = dc.getDocument().getString("sender");
                                String text = dc.getDocument().getString("text");
                                long timestamp = dc.getDocument().getLong("timestamp");

                                Message newMsg = new Message(sender, text, timestamp);
                                messageList.add(newMsg);
                                adapter.notifyItemInserted(messageList.size() - 1);
                                recyclerMessages.scrollToPosition(messageList.size() - 1);
                                updateEmptyState();
                            }
                        }
                    }
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages";
            String description = "Message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_send)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTypingAnimation();
    }

    private void stopTypingAnimation() {
        if (animator1 != null) animator1.cancel();
        if (animator2 != null) animator2.cancel();
        if (animator3 != null) animator3.cancel();
    }
}
