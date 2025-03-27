package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



    // RecyclerView Adapter for displaying tasks in calendar view
    // Handles completion marking, deletion, updating and task display
public class calendarPage_Adapter extends RecyclerView.Adapter<calendarPage_Adapter.MyViewHolder> {

    ArrayList<dataSets> dateList;  // List of tasks to display
    Context context;              // Activity context
    private TaskDeleteListener deleteListener; // Callback interface for task deletion



    //Interface for communicating task deletion to parent activity
    public interface TaskDeleteListener {
        void onDeleteTask(int taskId); // Called when a task needs to be deleted
    }



    // Constructor for the adapter
    //List of tasks to display

    public calendarPage_Adapter(ArrayList<dataSets> dateList, Context context) {
        this.dateList = dateList;
        this.context = context;
        // Verify that the hosting activity implements our callback interface
        if (context instanceof TaskDeleteListener) {
            this.deleteListener = (TaskDeleteListener) context;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_todolist_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get the task data for current position
        dataSets data = dateList.get(position);
        Log.d("ImportanceCheck", "Task: " + data.getTitle() + ", Importance: " + data.getImportance());

        // Set view contents from task data
        holder.title.setText(data.getTitle());
        holder.date.setText(data.getDate());
        holder.time.setText(data.getTime());
        holder.description.setText(data.getDescription());
        holder.checkBox.setText("Done");
        holder.card.setVisibility(View.VISIBLE);

        // Clear previous checkbox listener to avoid recycling issues
        holder.checkBox.setOnCheckedChangeListener(null);

        // Set card color based on task importance
        switch (data.getImportance()) {
            case "High":
                holder.card.setCardBackgroundColor(context.getResources()
                        .getColor(R.color.high_importance)); // Red for high priority
                break;
            case "Medium":
                holder.card.setCardBackgroundColor(context.getResources()
                        .getColor(R.color.medium_importance)); // Yellow for medium
                break;
            case "Low":
                holder.card.setCardBackgroundColor(context.getResources()
                        .getColor(R.color.no_importance)); // Default for low
                break;
        }

        // Handle task completion (checkbox checked)
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbhelper database = new dbhelper(context.getApplicationContext());
            if (isChecked) {
                // Mark task as completed in database
                database.editItem(data.getTitle(), data.getDescription(),
                        data.getDate(), data.getTime(), "true", data.getImportance());

                // Remove task from view
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    dateList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    notifyItemRangeChanged(adapterPosition, dateList.size());
                }
            }
        });

        // Handle task deletion
        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Notify parent activity to handle deletion
                    deleteListener.onDeleteTask(data.getId());
                    // Update UI
                    dateList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, dateList.size());
                }
            }
        });

        // Handle task editing (long press)
        holder.card.setOnLongClickListener(v -> {
            Intent update = new Intent(context, addActivity.class);
            update.putExtra("update", true);

            // Track which page initiated the update
            if (context.getClass().equals(calendarPage.class)) {
                update.putExtra("page", "calendar"); // From calendar page
            } else {
                update.putExtra("page", "dash");      // From dashboard
            }

            // Pass all task data to edit activity
            update.putExtra("title", data.getTitle());
            update.putExtra("description", data.getDescription());
            update.putExtra("date", data.getDate());
            update.putExtra("time", data.getTime());
            update.putExtra("importance", data.getImportance());

            context.startActivity(update);
            return true; // Consume the long click
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size(); // Return total number of tasks
    }

    /**
     * ViewHolder class that holds references to all views in a single item
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time, description;
        CardView card;
        CheckBox checkBox;
        ImageButton deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize all view references
            card = itemView.findViewById(R.id.card);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
            checkBox = itemView.findViewById(R.id.checkBox);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }
}
