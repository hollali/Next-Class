package com.ahofama.nextclass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment implements InboxAdapter.OnChatClickListener {

    private RecyclerView recyclerChats;
    private InboxAdapter adapter;
    private List<Chat> chatList = new ArrayList<>();

    private DatabaseReference chatsRef;
    private String currentUserId;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        recyclerChats = view.findViewById(R.id.recyclerChats);
        recyclerChats.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InboxAdapter(chatList, this);
        recyclerChats.setAdapter(adapter);

        // Get current userId from FirebaseAuth
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to /chats in Realtime Database
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        loadChats();

        return view;
    }

    private void loadChats() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    Chat chat = chatSnapshot.getValue(Chat.class);
                    if (chat != null && chat.getParticipants() != null) {
                        if (chat.getParticipants().containsKey(currentUserId)) {
                            chat.setId(chatSnapshot.getKey()); // set chatId
                            chatList.add(chat);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    @Override
    public void onChatClick(Chat chat) {
        // Navigate to MessagingFragment
        Fragment messagingFragment = MessagingFragment.newInstance(chat.getId());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, messagingFragment)
                .addToBackStack(null)
                .commit();
    }
}
