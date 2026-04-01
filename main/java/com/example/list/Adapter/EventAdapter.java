package com.example.list.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.list.Model.ToDoModel;
import com.example.list.NewDailyTask;
import com.example.list.NewSpecialOccasion;
import com.example.list.R;
import com.example.list.Utils.DatabaseHandler;
import com.example.list.ui.history.HistoryFragment;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<ToDoModel> toDoList;
    private HistoryFragment activity;
    private DatabaseHandler db;

    public EventAdapter(DatabaseHandler db, HistoryFragment activity, List<ToDoModel> toDoList) {
        this.db = db;
        this.activity = activity;
        this.toDoList = toDoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();
        final ToDoModel item = toDoList.get(position);

        holder.title.setText(item.getTaskName());
        holder.time.setText(item.getTime());
        if (item.getType().equals("Recurring")) {
            holder.image.setImageResource(R.drawable.ic_baseline_alarm);
        } else {
            holder.image.setImageResource(R.drawable.ic_baseline__docs);
        }

        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = item.getType();
                if (type.equals("Recurring")) {
                    Intent intent = new Intent(getContext(), NewDailyTask.class);
                    intent.putExtra("id", (String.valueOf(item.getId())));
                    intent.putExtra("task", item.getTask());
                    intent.putExtra("taskName", item.getTaskName());
                    intent.putExtra("time", item.getTime());
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), NewSpecialOccasion.class);
                    intent.putExtra("id", (String.valueOf(item.getId())));
                    intent.putExtra("task", item.getTask());
                    intent.putExtra("taskName", item.getTaskName());
                    intent.putExtra("date", item.getDate());
                    intent.putExtra("time", item.getTime());
                    activity.startActivity(intent);
                }
            }
        });
    }

    public int getItemCount() {
        return toDoList.size();
    }

    public Context getContext() {
        Context context = activity.getContext();
        return context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, time;
        ImageView image;
        ConstraintLayout rowLayout;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.eventTitle);
            time = view.findViewById(R.id.eventTime);
            image = view.findViewById(R.id.eventImage);
            rowLayout = view.findViewById(R.id.eventRowLayout);
        }

    }
}
