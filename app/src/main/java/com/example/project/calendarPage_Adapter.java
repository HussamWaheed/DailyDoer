package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class calendarPage_Adapter extends RecyclerView.Adapter<calendarPage_Adapter.MyViewHolder> {

    ArrayList<dataSets> dateList;
    Context context;

    public calendarPage_Adapter(ArrayList<dataSets> dateList, Context context){
        this.dateList = dateList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_todolist_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        dataSets data= dateList.get(position);
        holder.title.setText(data.getTitle());
        holder.date.setText(data.getDate());
        holder.time.setText(data.getTime());
        holder.description.setText(data.getDescription());
        holder.checkBox.setText("Done");
        holder.card.setVisibility(View.VISIBLE);
        holder.checkBox.setOnCheckedChangeListener(null);

        //if check box is checked, change the status of the task and remove it from the screen
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbhelper database = new dbhelper(context.getApplicationContext());
                if (isChecked){
                    database.editItem(data.getTitle(), data.getDescription(), data.getDate(), data.getTime(), "true");
                    dateList.remove(position);
                    holder.card.setVisibility(View.GONE);
                    notifyItemRemoved(position);
                }


        });
        //when long click the card view, jump to update task page and edit task
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent update = new Intent(context, addActivity.class);
                update.putExtra("update",true);
                if (context.getClass().equals(calendarPage.class)){
                    //long click from calendar page
                    update.putExtra("page","calendar");
                }else{
                    //long press from list page
                    update.putExtra("page","dash");
                }
                //put everything to the update page
                update.putExtra("title", data.getTitle());
                update.putExtra("description",data.getDescription());
                update.putExtra("date", data.getDate());
                update.putExtra("time", data.getTime());
                context.startActivity(update);
                return true;
            }
        });


    }


    @Override
    public int getItemCount() {
        return dateList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title, date, time, description;
        CardView card;
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            title =itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

    }

}

