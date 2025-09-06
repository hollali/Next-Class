package com.ahofama.nextclass;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private TextInputEditText etSearch;
    private RecyclerView recyclerSearchResults;
    private TextView tvEmptyState;

    private SearchResultsAdapter adapter;
    private List<Course> courseList = new ArrayList<>(); // ✅ now Course, not String
    private FirebaseFirestore db;

    public SearchFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Init Firestore
        db = FirebaseFirestore.getInstance();

        // Bind views
        etSearch = view.findViewById(R.id.etSearch);
        recyclerSearchResults = view.findViewById(R.id.recyclerSearchResults);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        // Setup RecyclerView
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchResultsAdapter(getContext(), courseList);
        recyclerSearchResults.setAdapter(adapter);

        // Search listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCourses(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void searchCourses(String query) {
        if (query.isEmpty()) {
            courseList.clear();
            adapter.notifyDataSetChanged();
            tvEmptyState.setVisibility(View.GONE);
            return;
        }

        db.collection("courses")
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff") // Firestore prefix search
                .get()
                .addOnSuccessListener(snapshots -> {
                    courseList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String title = doc.getString("title");
                        String instructor = doc.getString("instructor");
                        String thumbnailUrl = doc.getString("thumbnailUrl");

                        if (title != null && instructor != null) {
                            courseList.add(new Course(title, instructor, thumbnailUrl));
                        }
                    }
                    adapter.notifyDataSetChanged();

                    // Empty state
                    tvEmptyState.setVisibility(courseList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    tvEmptyState.setText("Error loading results");
                    tvEmptyState.setVisibility(View.VISIBLE);
                });
    }
}
