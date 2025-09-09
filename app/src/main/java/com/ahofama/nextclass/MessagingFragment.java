package com.ahofama.nextclass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagingFragment extends Fragment {

    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    private static final String CHANNEL_ID = "messages_channel";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    public MessagingFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        // Example messages
        messageList.add(new Message("Alice", "Hey, are you free today?", System.currentTimeMillis()));
        messageList.add(new Message("Bob", "Don’t forget the meeting!", System.currentTimeMillis()));
        messageList.add(new Message("Clara", "Let’s catch up later", System.currentTimeMillis()));

        adapter = new MessagesAdapter(getContext(), messageList);
        recyclerMessages.setAdapter(adapter);

        // Create notification channel (for Android 8+)
        createNotificationChannel();

        // Ask for permission if needed, then show notification
        requestNotificationPermission();

        return view;
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
        // Check permission again before posting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return; // Don’t crash — permission missing
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message) // Ensure this exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(1, builder.build());
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
                // Already granted → show a notification
                showNotification("New message from " + messageList.get(0).getSender(),
                        messageList.get(0).getContent());
            }
        } else {
            // No runtime permission needed below Android 13
            showNotification("New message from " + messageList.get(0).getSender(),
                    messageList.get(0).getContent());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted → now show notification
                showNotification("New message from " + messageList.get(0).getSender(),
                        messageList.get(0).getContent());
            } else {
                // User denied → you can show a Toast or Snackbar here
            }
        }
    }
}
