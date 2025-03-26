package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class calendarPage_Adapter extends RecyclerView.Adapter<calendarPage_Adapter.MyViewHolder> {

    ArrayList<dataSets> dateList;
    Context context;
    private TaskDeleteListener deleteListener; // Added interface reference for Task Deleting

    // Interface for task deletion callback
    public interface TaskDeleteListener {
        void onDeleteTask(int taskId); // Pass taskId for deletion
    }

    public calendarPage_Adapter(ArrayList<dataSets> dateList, Context context) {
        this.dateList = dateList;
        this.context = context;
        // Check if the context implements the TaskDeleteListener
        if (context instanceof TaskDeleteListener) {
            this.deleteListener = (TaskDeleteListener) context;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_todolist_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        dataSets data = dateList.get(position);
        Log.d("ImportanceCheck", "Task: " + data.getTitle() + ", Importance: " + data.getImportance());
        holder.title.setText(data.getTitle());
        holder.date.setText(data.getDate());
        holder.time.setText(data.getTime());
        holder.description.setText(data.getDescription());
        holder.checkBox.setText("Done");
        holder.card.setVisibility(View.VISIBLE);
        holder.checkBox.setOnCheckedChangeListener(null);

// Set color based on the importance of the task
switch (data.getImportance()) {
    case "High":
        holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.high_importance)); // Red color for high importance
        break;
    case "Medium":
        holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.medium_importance)); // Yellow color for medium importance
        break;
    case "Low":
        holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.no_importance)); // Default color for no/low importance
        break;
}

// If checkbox is checked, change the status of the task and remove it from the screen
holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
    dbhelper database = new dbhelper(context.getApplicationContext());
    if (isChecked) {
        database.editItem(data.getTitle(), data.getDescription(), data.getDate(), data.getTime(), "true", data.getImportance());
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            dateList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, dateList.size());
        }
    }
});

// Delete button click handler
holder.deleteBtn.setOnClickListener(v -> {
    if (deleteListener != null) {
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition != RecyclerView.NO_POSITION) {
            deleteListener.onDeleteTask(data.getId()); // Pass taskId for deletion
            dateList.remove(currentPosition);
            notifyItemRemoved(currentPosition);
            notifyItemRangeChanged(currentPosition, dateList.size());
        }
    }
});

// When long-clicking the card view, jump to update task page and edit task
holder.card.setOnLongClickListener(v -> {
    Intent update = new Intent(context, addActivity.class);
    update.putExtra("update", true);
    
    if (context.getClass().equals(calendarPage.class)) {
        // Long click from calendar page
        update.putExtra("page", "calendar");
    } else {
        // Long press from list page
        update.putExtra("page", "dash");
    }
    
    // Put everything to the update page
    update.putExtra("title", data.getTitle());
    update.putExtra("description", data.getDescription());
    update.putExtra("date", data.getDate());
    update.putExtra("time", data.getTime());
    update.putExtra("importance", data.getImportance());
    
    context.startActivity(update);
    return true;
});


        // Checkbox change listener (Mark task as complete)
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbhelper database = new dbhelper(context.getApplicationContext());
            if (isChecked) {
                // Update task status to complete in the database
                database.editItem(data.getTitle(), data.getDescription(), data.getDate(), data.getTime(), "true");
                dateList.remove(position);
                holder.card.setVisibility(View.GONE);
                notifyItemRemoved(position);
            }
        });

        // Long click listener to update task
        holder.card.setOnLongClickListener(v -> {
            Intent update = new Intent(context, addActivity.class);
            update.putExtra("update", true);
            if (context.getClass().equals(calendarPage.class)) {
                // Long click from calendar page
                update.putExtra("page", "calendar");
            } else {
                // Long press from list page
                update.putExtra("page", "dash");
            }
            // Pass task data to update page
            update.putExtra("title", data.getTitle());
            update.putExtra("description", data.getDescription());
            update.putExtra("date", data.getDate());
            update.putExtra("time", data.getTime());
            context.startActivity(update);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time, description;
        CardView card;
        CheckBox checkBox;
        ImageButton deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
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
