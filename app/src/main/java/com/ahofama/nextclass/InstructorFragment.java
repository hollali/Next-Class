package com.ahofama.nextclass;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class InstructorFragment extends Fragment {

    private RecyclerView instructorsRecyclerView;
    private InstructorAdapter instructorAdapter;
    private List<Instructor> instructorList;
    private List<Instructor> filteredInstructorList;

    private EditText searchEditText;
    private ChipGroup chipGroup;
    private LinearLayout emptyState;
    private ProgressBar loadingProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructor, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupFilters();
        loadInstructors();

        return view;
    }

    private void initViews(View view) {
        instructorsRecyclerView = view.findViewById(R.id.instructors_recyclerview);
        searchEditText = view.findViewById(R.id.search_edittext);
        chipGroup = view.findViewById(R.id.chip_group);
        emptyState = view.findViewById(R.id.empty_state);
        loadingProgress = view.findViewById(R.id.loading_progress);
    }

    private void setupRecyclerView() {
        instructorList = new ArrayList<>();
        filteredInstructorList = new ArrayList<>();

        instructorAdapter = new InstructorAdapter(filteredInstructorList, getContext());
        instructorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        instructorsRecyclerView.setAdapter(instructorAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterInstructors();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            filterInstructors();
        });
    }

    private void loadInstructors() {
        showLoading(true);

        // Simulate loading data - replace with actual API call
        // For now, create sample data
        createSampleData();

        // Simulate network delay
        instructorsRecyclerView.postDelayed(() -> {
            filteredInstructorList.addAll(instructorList);
            instructorAdapter.notifyDataSetChanged();
            showLoading(false);
            updateEmptyState();
        }, 1500);
    }

    private void createSampleData() {
        instructorList.clear();

        // Sample instructors - replace with actual data from your backend
        instructorList.add(new Instructor(
                "Dr. Sarah Johnson",
                "Mathematics",
                "Calculus • Algebra",
                "5+ years experience • PhD in Mathematics",
                4.8f,
                25.0,
                "Available",
                true,
                new String[]{"Calculus", "Algebra", "Statistics"}
        ));

        instructorList.add(new Instructor(
                "Prof. Michael Chen",
                "Computer Science",
                "Programming • Data Structures",
                "8+ years experience • MS Computer Science",
                4.9f,
                35.0,
                "Available",
                true,
                new String[]{"Java", "Python", "Algorithms"}
        ));

        instructorList.add(new Instructor(
                "Dr. Emily Rodriguez",
                "Science",
                "Chemistry • Biology",
                "6+ years experience • PhD Chemistry",
                4.7f,
                30.0,
                "Busy",
                false,
                new String[]{"Organic Chemistry", "Biochemistry"}
        ));

        instructorList.add(new Instructor(
                "James Wilson",
                "English",
                "Literature • Writing",
                "4+ years experience • MA English Literature",
                4.6f,
                20.0,
                "Available",
                true,
                new String[]{"Essay Writing", "Literature Analysis"}
        ));

        instructorList.add(new Instructor(
                "Dr. Lisa Thompson",
                "History",
                "World History • American History",
                "7+ years experience • PhD History",
                4.8f,
                28.0,
                "Available",
                true,
                new String[]{"World War II", "Ancient History"}
        ));
    }

    private void filterInstructors() {
        filteredInstructorList.clear();

        String searchQuery = searchEditText.getText().toString().toLowerCase().trim();
        String selectedSubject = getSelectedSubject();

        for (Instructor instructor : instructorList) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    instructor.getName().toLowerCase().contains(searchQuery) ||
                    instructor.getSubject().toLowerCase().contains(searchQuery) ||
                    instructor.getSpecialization().toLowerCase().contains(searchQuery);

            boolean matchesSubject = selectedSubject.equals("All") ||
                    instructor.getSubject().equalsIgnoreCase(selectedSubject);

            if (matchesSearch && matchesSubject) {
                filteredInstructorList.add(instructor);
            }
        }

        instructorAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private String getSelectedSubject() {
        int checkedChipId = chipGroup.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip selectedChip = chipGroup.findViewById(checkedChipId);
            if (selectedChip != null) {
                return selectedChip.getText().toString();
            }
        }
        return "All";
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingProgress.setVisibility(View.VISIBLE);
            instructorsRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
        } else {
            loadingProgress.setVisibility(View.GONE);
            instructorsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (filteredInstructorList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            instructorsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            instructorsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}