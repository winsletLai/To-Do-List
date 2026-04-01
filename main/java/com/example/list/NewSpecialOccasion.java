package com.example.list;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.list.Model.ToDoModel;
import com.example.list.Utils.DatabaseHandler;
import com.example.list.Utils.DatePickerUtil;

import java.util.Locale;

public class NewSpecialOccasion extends AppCompatActivity {

    private EditText newTaskText;
    private Button newTaskSaveButton,btnDate, btnTime, deleteTaskButton;
    private RadioGroup row1, row2;

    private DatabaseHandler db = new DatabaseHandler(NewSpecialOccasion.this);

    String formattedDate, selectedTime = "00:00";
    String taskId,task,taskName,date;
    int hour,minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_special_occasion);

        find();
        getCategory();

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerUtil.showDatePicker(NewSpecialOccasion.this, (display, formatted) -> {
                    btnDate.setText(display);
                    formattedDate = formatted; // save this to insert into DB
                });
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour = selectedHour;
                        minute = selectedMinute;
                        selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                        btnTime.setText(selectedTime);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewSpecialOccasion.this, onTimeSetListener, hour, minute,true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        db.openDatabase();

        boolean isUpdate = false;

        if(getAndSetIntentData()){
            isUpdate = true;
            getAndSetIntentData();
            deleteTaskButton.setVisibility(View.VISIBLE);
            deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDialog();
                }
            });
        }
        else{
            formattedDate = DatePickerUtil.getTodayFormattedDate();
            setDateFormat(formattedDate);
        }

        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Category = getCategory();
                String TaskName = newTaskText.getText().toString();
                String Date = formattedDate;

                ToDoModel task = new ToDoModel();
                task.setTask(Category);
                task.setTaskName(TaskName);
                task.setDate(Date);
                task.setDays("");
                task.setTime(selectedTime);
                task.setType("Special");

                if(Category.isEmpty() || TaskName.isEmpty()){
                    if(Category.isEmpty()){
                        Toast.makeText(NewSpecialOccasion.this, "Please select a type", Toast.LENGTH_SHORT).show();}
                    else{
                        Toast.makeText(NewSpecialOccasion.this, "Please fill in the name", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(finalIsUpdate){
                        db.updateTask(taskId,task);
                        Intent intent = new Intent(NewSpecialOccasion.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        db.insertTask(task);
                        Intent intent = new Intent(NewSpecialOccasion.this,MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void find(){
        newTaskText = findViewById(R.id.newTaskText1);

        row1= findViewById(R.id.radioGroupSpecialOccasionRow1);
        row2 = findViewById(R.id.radioGroupSpecialOccasionRow2);

        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime1);
        newTaskSaveButton = findViewById(R.id.newTaskButton1);
        deleteTaskButton =findViewById(R.id.deleteTaskButton1);
    }

    private String getCategory(){
        row1.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                row2.clearCheck();
            }
        });
        row2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                row1.clearCheck();
            }
        });

        int selectedId1 = row1.getCheckedRadioButtonId();
        int selectedId2 = row2.getCheckedRadioButtonId();

        RadioButton selectedButton = null;

        if (selectedId1 != -1) {
            selectedButton = findViewById(selectedId1);
        } else if (selectedId2 != -1) {
            selectedButton = findViewById(selectedId2);
        }

        //Default
        String selectedCategory = "";

        if (selectedButton != null){
            selectedCategory = selectedButton.getText().toString();
        }
        return selectedCategory;
    }

    private boolean getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("taskName") && getIntent().hasExtra("date") && getIntent().hasExtra("task") && getIntent().hasExtra("time")) {

            taskId = getIntent().getStringExtra("id");
            taskName = getIntent().getStringExtra("taskName");
            date = getIntent().getStringExtra("date");
            task = getIntent().getStringExtra("task");
            selectedTime = getIntent().getStringExtra("time");

            newTaskText.setText(taskName);
            btnTime.setText(selectedTime);
            formattedDate = date;

            setDateFormat(date);

            return true;
        }
        else {
            return false;
        }
    }
    private void setDateFormat(String date1){
        String day = date1.substring(8);
        String monthStr = date1.substring(5,7);
        String year = date1.substring(0,4);

        int dayNo = Integer.parseInt(day);
        int monthNo = Integer.parseInt(monthStr);
        int yearNo = Integer.parseInt(year);

        monthNo -= 1;

        String month = DatePickerUtil.getMonthFormat(monthNo);

        btnDate.setText(month + " " + dayNo + " " + yearNo);
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + taskName + " ?");
        builder.setMessage("Are you sure you want to delete " + taskName + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int id = Integer.parseInt(taskId);
                db.deleteTask(id);
                Intent intent = new Intent(NewSpecialOccasion.this, MainActivity.class);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }
}