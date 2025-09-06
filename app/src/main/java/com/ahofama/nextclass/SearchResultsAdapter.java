package com.ahofama.nextclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<Course> courseList;
    private Context context;

    public SearchResultsAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.tvCourseTitle.setText(course.getTitle());
        holder.tvInstructor.setText("By " + course.getInstructor());

        // Load thumbnail with Glide
        Glide.with(context)
                .load(course.getThumbnailUrl())
                .placeholder(R.drawable.ic_courses) // fallback image
                .into(holder.ivThumbnail);

        // Button click
        holder.btnViewDetails.setOnClickListener(v -> {
            // For now, just a toast (you can replace with navigation later)
            android.widget.Toast.makeText(context,
                    "View Details: " + course.getTitle(),
                    android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvCourseTitle, tvInstructor;
        Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvInstructor = itemView.findViewById(R.id.tvInstructor);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
