package com.example.list.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.example.list.ui.home.HomeFragment;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> toDoList;
    private HomeFragment activity;
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db,HomeFragment activity){
        this.db = db;
        this.activity=activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position){
        db.openDatabase();
        final ToDoModel item = toDoList.get(position);

        holder.task.setOnCheckedChangeListener(null);

        //Set UI
        holder.task.setText(item.getTaskName());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.time.setText(item.getTime());


        if(item.getStatus() == 1){
            holder.task.setChecked(true);
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.rowLayout.setBackgroundColor(Color.GRAY);
        } else {
            holder.task.setPaintFlags(0);
            holder.rowLayout.setBackgroundColor(Color.WHITE);
        }

        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setStatus(isChecked ? 1 : 0);
            db.updateStatus(item.getId(), item.getStatus());
            if(isChecked){
                holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.rowLayout.setBackgroundColor(Color.GRAY);
            } else {
                holder.task.setPaintFlags(0);
                holder.rowLayout.setBackgroundColor(Color.WHITE);
            }
        });

        if(item.getType().equals("Recurring")){
            holder.title.setText("Reminder");
            holder.image.setImageResource(R.drawable.ic_baseline_alarm);
        }
        else{
            holder.title.setText(item.getTask());
            holder.image.setImageResource(R.drawable.ic_baseline__docs);
        }
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    public int getItemCount(){
        return toDoList.size();
    }

    public Context getContext(){
        Context context = activity.getContext();
        return context;
    }


    public void setTasks(List<ToDoModel> toDoList){
        this.toDoList = toDoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        ToDoModel item = toDoList.get(position);
        db.deleteTask(item.getId());
        toDoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        ToDoModel item = toDoList.get(position);

        String type = item.getType();
        if(type.equals("Recurring")){
            Intent intent = new Intent(getContext(), NewDailyTask.class);
            intent.putExtra("id",(String.valueOf(item.getId())));
            intent.putExtra("task",item.getTask());
            intent.putExtra("taskName",item.getTaskName());
            intent.putExtra("time",item.getTime());
            activity.startActivity(intent);
        }
        else{
            Intent intent = new Intent(getContext(), NewSpecialOccasion.class);
            intent.putExtra("id",(String.valueOf(item.getId())));
            intent.putExtra("task",item.getTask());
            intent.putExtra("taskName",item.getTaskName());
            intent.putExtra("date",item.getDate());
            intent.putExtra("time",item.getTime());
            activity.startActivity(intent);
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView title,time;
        ImageView image;
        ConstraintLayout rowLayout;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.toDoCheckBox);
            title = view.findViewById(R.id.toDoTitle);
            time = view.findViewById(R.id.toDoTime);
            image = view.findViewById(R.id.toDoImage);
            rowLayout = view.findViewById(R.id.toDoListRowLayout);
        }

    }
}
