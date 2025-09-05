package com.ahofama.nextclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {

    private List<Instructor> instructors;
    private Context context;
    private OnInstructorClickListener listener;

    public interface OnInstructorClickListener {
        void onInstructorClick(Instructor instructor);
        void onContactClick(Instructor instructor);
    }

    public InstructorAdapter(List<Instructor> instructors, Context context) {
        this.instructors = instructors;
        this.context = context;
    }

    public void setOnInstructorClickListener(OnInstructorClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instructor, parent, false);
        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        Instructor instructor = instructors.get(position);
        holder.bind(instructor);
    }

    @Override
    public int getItemCount() {
        return instructors.size();
    }

    public class InstructorViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView instructorImage;
        private TextView instructorName;
        private TextView instructorSubject;
        private TextView instructorExperience;
        private TextView instructorRating;
        private TextView instructorPrice;
        private TextView instructorStatus;
        private View statusIndicator;
        private MaterialButton contactButton;
        private ChipGroup subjectChips;

        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);

            instructorImage = itemView.findViewById(R.id.instructor_image);
            instructorName = itemView.findViewById(R.id.instructor_name);
            instructorSubject = itemView.findViewById(R.id.instructor_subject);
            instructorExperience = itemView.findViewById(R.id.instructor_experience);
            instructorRating = itemView.findViewById(R.id.instructor_rating);
            instructorPrice = itemView.findViewById(R.id.instructor_price);
            instructorStatus = itemView.findViewById(R.id.instructor_status);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            contactButton = itemView.findViewById(R.id.btn_contact);
            subjectChips = itemView.findViewById(R.id.subject_chips);
        }

        public void bind(Instructor instructor) {
            // Set basic information
            instructorName.setText(instructor.getName());
            instructorSubject.setText(instructor.getSubject() + " • " + instructor.getSpecialization());
            instructorExperience.setText(instructor.getExperience());
            instructorRating.setText(instructor.getFormattedRating());
            instructorPrice.setText(instructor.getFormattedRate());

            // Set status with appropriate colors
            instructorStatus.setText(instructor.getStatus());
            updateStatusAppearance(instructor.isAvailable());

            // Set up subject chips
            setupSubjectChips(instructor.getTags());

            // Set contact button state
            contactButton.setEnabled(instructor.isAvailable());
            if (!instructor.isAvailable()) {
                contactButton.setText("Unavailable");
                contactButton.setAlpha(0.6f);
            } else {
                contactButton.setText("Contact");
                contactButton.setAlpha(1.0f);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInstructorClick(instructor);
                }
            });

            contactButton.setOnClickListener(v -> {
                if (listener != null && instructor.isAvailable()) {
                    listener.onContactClick(instructor);
                }
            });

            // Load profile image (placeholder for now)
            // You can use libraries like Glide or Picasso here
            // Glide.with(context).load(instructor.getImageUrl()).into(instructorImage);
        }

        private void updateStatusAppearance(boolean isAvailable) {
            if (isAvailable) {
                instructorStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                statusIndicator.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.holo_green_dark));
            } else {
                instructorStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
                statusIndicator.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.holo_orange_dark));
            }
        }

        private void setupSubjectChips(String[] tags) {
            subjectChips.removeAllViews();

            if (tags != null && tags.length > 0) {
                for (String tag : tags) {
                    Chip chip = new Chip(context);
                    chip.setText(tag);
                    chip.setTextSize(10);
                    chip.setClickable(false);
                    chip.setCheckable(false);

                    // Set chip colors based on subject
                    int[] chipColors = {
                            R.color.chip_blue,
                            R.color.chip_green,
                            R.color.chip_purple,
                            R.color.chip_orange
                    };

                    int colorIndex = Math.abs(tag.hashCode()) % chipColors.length;
                    chip.setChipBackgroundColorResource(chipColors[colorIndex]);

                    subjectChips.addView(chip);
                }
                subjectChips.setVisibility(View.VISIBLE);
            } else {
                subjectChips.setVisibility(View.GONE);
            }
        }
    }
}