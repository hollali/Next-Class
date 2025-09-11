package com.ahofama.nextclass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * InboxFragment: Shows list of chats/conversations.
 * Clicking a chat opens MessagingFragment.
 */
public class InboxFragment extends Fragment {

    private RecyclerView recyclerChats;
    private InboxAdapter adapter;
    private List<Chat> chatList = new ArrayList<>();

    public InboxFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        recyclerChats = view.findViewById(R.id.recyclerChats);
        recyclerChats.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSampleChats();
        adapter = new InboxAdapter(chatList, chat -> openChat(chat));
        recyclerChats.setAdapter(adapter);

        return view;
    }

    private void loadSampleChats() {
        chatList.add(new Chat("Alice", "Hey, are you ready?", System.currentTimeMillis()));
        chatList.add(new Chat("Bob", "Let’s meet at 3pm", System.currentTimeMillis() - 600000));
        chatList.add(new Chat("Clara", "I shared the doc", System.currentTimeMillis() - 1200000));
    }

    private void openChat(Chat chat) {
        MessagingFragment fragment = MessagingFragment.newInstance(chat.getName());

        FragmentTransaction tx = getParentFragmentManager().beginTransaction();
        tx.replace(R.id.frame_layout, fragment); // use frame_layout from DashboardActivity
        tx.addToBackStack(null);
        tx.commit();
    }


}
