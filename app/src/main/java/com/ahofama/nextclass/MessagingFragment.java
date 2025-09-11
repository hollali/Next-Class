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

import java.util.ArrayList;
import java.util.List;

/**
 * MessagingFragment: Shown when a user clicks a chat from InboxFragment.
 * Displays chat header, messages, input bar, typing indicator, and notifications.
 */
public class MessagingFragment extends Fragment {

    private static final String ARG_USER_NAME = "user_name";
    private String userName; // filled from arguments

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

    public MessagingFragment() { /* required empty constructor */ }

    /**
     * Factory method to create a new instance of MessagingFragment with a username.
     */
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
        // Read arguments early so they are available in initViews
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_USER_NAME);
        }
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

        dot1 = view.findViewById(R.id.dot1);
        dot2 = view.findViewById(R.id.dot2);
        dot3 = view.findViewById(R.id.dot3);

        // Use passed-in userName if available
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
            tvUserStatus.setText("Online"); // you can set a dynamic status later
        } else {
            tvUserName.setText("Study Group");
            tvUserStatus.setText("3 members online");
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);

        addSampleMessages();
        adapter = new MessagesAdapter(getContext(), messageList);
        recyclerMessages.setAdapter(adapter);

        updateEmptyState();
    }

    private void addSampleMessages() {
        messageList.clear();
        messageList.add(new Message("Alice", "Hey everyone! Ready for today's study session?", System.currentTimeMillis() - 300000));
        messageList.add(new Message("Bob", "Yes! I've prepared the materials we discussed", System.currentTimeMillis() - 240000));
        messageList.add(new Message("Clara", "Great! Should we start with the math problems?", System.currentTimeMillis() - 180000));
        messageList.add(new Message("You", "Sounds good! I'll share my screen", System.currentTimeMillis() - 120000));
        messageList.add(new Message("Alice", "Perfect! Let's do this 📚", System.currentTimeMillis() - 60000));
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        ivMore.setOnClickListener(v -> {
            // TODO: Popup menu
        });

        btnAttachment.setOnClickListener(v -> {
            // TODO: File picker or camera
            showTypingIndicator(); // demo
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

        Message newMessage = new Message("You", messageText, System.currentTimeMillis());
        messageList.add(newMessage);
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerMessages.scrollToPosition(messageList.size() - 1);
        etMessage.setText("");
        updateEmptyState();

        showNotification("New message from You", messageText);
        simulateResponse();
    }

    private void simulateResponse() {
        recyclerMessages.postDelayed(() -> {
            showTypingIndicator();
            recyclerMessages.postDelayed(() -> {
                hideTypingIndicator();
                String[] responses = {"That's a great point!", "I agree with that approach", "Let me think about that...", "Thanks for sharing!"};
                String[] senders = {"Alice", "Bob", "Clara"};
                String response = responses[(int) (Math.random() * responses.length)];
                String sender = senders[(int) (Math.random() * senders.length)];
                Message responseMessage = new Message(sender, response, System.currentTimeMillis());
                messageList.add(responseMessage);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerMessages.scrollToPosition(messageList.size() - 1);
            }, 2000);
        }, 1000);
    }

    private void showTypingIndicator() {
        typingIndicator.setVisibility(View.VISIBLE);
        animateTypingDots();
        recyclerMessages.postDelayed(() -> recyclerMessages.scrollToPosition(messageList.size() - 1), 100);
    }

    private void hideTypingIndicator() {
        typingIndicator.setVisibility(View.GONE);
        stopTypingAnimation();
    }

    private void animateTypingDots() {
        if (animator1 != null) animator1.cancel();
        if (animator2 != null) animator2.cancel();
        if (animator3 != null) animator3.cancel();

        animator1 = ObjectAnimator.ofFloat(dot1, "alpha", 0.4f, 1.0f);
        animator1.setDuration(600);
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        animator1.setRepeatMode(ObjectAnimator.REVERSE);

        animator2 = ObjectAnimator.ofFloat(dot2, "alpha", 0.4f, 1.0f);
        animator2.setDuration(600);
        animator2.setRepeatCount(ObjectAnimator.INFINITE);
        animator2.setRepeatMode(ObjectAnimator.REVERSE);
        animator2.setStartDelay(200);

        animator3 = ObjectAnimator.ofFloat(dot3, "alpha", 0.4f, 1.0f);
        animator3.setDuration(600);
        animator3.setRepeatCount(ObjectAnimator.INFINITE);
        animator3.setRepeatMode(ObjectAnimator.REVERSE);
        animator3.setStartDelay(400);

        animator1.start(); animator2.start(); animator3.start();
    }

    private void stopTypingAnimation() {
        if (animator1 != null) animator1.cancel();
        if (animator2 != null) animator2.cancel();
        if (animator3 != null) animator3.cancel();
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
                return; // permission missing
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
            } else {
                if (!messageList.isEmpty()) {
                    showNotification("Welcome to " + (userName != null ? userName : "Chat"),
                            "Stay connected!");
                }
            }
        } else {
            if (!messageList.isEmpty()) {
                showNotification("Welcome", "Stay connected!");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification("Notifications Enabled", "You'll now receive message notifications!");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTypingAnimation();
    }
}
