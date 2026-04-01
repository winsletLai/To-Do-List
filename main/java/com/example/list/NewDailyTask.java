package com.example.list;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.list.Model.ToDoModel;
import com.example.list.Utils.DatabaseHandler;
import com.example.list.Utils.DatePickerUtil;

import java.util.Locale;

public class NewDailyTask extends AppCompatActivity {

    private EditText newTaskText;
    private Button newTaskSaveButton,btnTime,deleteTaskButton;
    private RadioGroup row1, row2;

    private CheckBox checkBoxOnce,checkBoxMon,checkBoxTue,checkBoxWed,checkBoxThu,checkBoxFri,checkBoxSat,checkBoxSun;
    private DatabaseHandler db = new DatabaseHandler(NewDailyTask.this);

    String selectedTime = "00:00";
    int hour,minute;
    String taskId,task,taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_daily_task);

        find();
        getCategory();
        setUpCheckBox();

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewDailyTask.this, onTimeSetListener, hour, minute,true);
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
            deleteTaskButton.setVisibility(View.GONE);
        }

        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Category = getCategory();
                String TaskName = getTaskName(Category);
                String Date = DatePickerUtil.getTodayFormattedDate();;
                String Days = getDays();
                String Type = "Recurring";

                ToDoModel task = new ToDoModel();
                task.setTask(Category);
                task.setTaskName(TaskName);
                task.setDate(Date);
                task.setDays(Days);
                task.setTime(selectedTime);
                task.setType(Type);

                if(Days.isEmpty() || Category.isEmpty()){
                    if(Days.isEmpty()){
                        Toast.makeText(NewDailyTask.this, "Please select a day", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(NewDailyTask.this, "Please select a type", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(finalIsUpdate){
                        db.updateTask(taskId,task);
                        Intent intent = new Intent(NewDailyTask.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        db.insertTask(task);
                        Intent intent = new Intent(NewDailyTask.this,MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void find(){
        newTaskText = findViewById(R.id.newTaskText);

        row1= findViewById(R.id.radioGroupDailyTaskRow1);
        row2 = findViewById(R.id.radioGroupDailyTaskRow2);

        btnTime = findViewById(R.id.btnTime);
        checkBoxOnce = findViewById(R.id.checkBoxOnce);
        checkBoxMon = findViewById(R.id.checkBoxMonday);
        checkBoxTue = findViewById(R.id.checkBoxTuesday);
        checkBoxWed = findViewById(R.id.checkBoxWednesday);
        checkBoxThu = findViewById(R.id.checkBoxThursday);
        checkBoxFri = findViewById(R.id.checkBoxFriday);
        checkBoxSat = findViewById(R.id.checkBoxSaturday);
        checkBoxSun = findViewById(R.id.checkBoxSunday);
        newTaskSaveButton = findViewById(R.id.newTaskButton);
        deleteTaskButton = findViewById(R.id.deleteTaskButton);
    }

    private String getCategory(){
        row1.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                row2.clearCheck();
                newTaskText.setVisibility(View.GONE);
            }
        });
        row2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                row1.clearCheck();
                RadioButton rb = findViewById(checkedId);
                String buttonType = rb.getText().toString();
                if(buttonType.equals("Custom")){
                    newTaskText.setVisibility(View.VISIBLE);
                }
                else {
                    newTaskText.setVisibility(View.GONE);
                }
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

    private String getTaskName(String selectedCategory){
        String TaskName;
        if(selectedCategory.equals("Custom")){
            TaskName = newTaskText.getText().toString();
            if(TaskName.isEmpty()){
                TaskName = selectedCategory;
            }
        }
        else{
            TaskName = selectedCategory;
        }
        return TaskName;
    }

    private void setUpCheckBox(){
        checkBoxOnce.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Uncheck all weekdays
                checkBoxMon.setChecked(false);
                checkBoxTue.setChecked(false);
                checkBoxWed.setChecked(false);
                checkBoxThu.setChecked(false);
                checkBoxFri.setChecked(false);
                checkBoxSat.setChecked(false);
                checkBoxSun.setChecked(false);
            }
        });

        CompoundButton.OnCheckedChangeListener weekdayListener = (buttonView, isChecked) -> {
            if (isChecked) {
                // Uncheck "Once" if any weekday is checked
                checkBoxOnce.setChecked(false);
            }
        };
        // Apply the weekday listener to all weekday boxes
        checkBoxMon.setOnCheckedChangeListener(weekdayListener);
        checkBoxTue.setOnCheckedChangeListener(weekdayListener);
        checkBoxWed.setOnCheckedChangeListener(weekdayListener);
        checkBoxThu.setOnCheckedChangeListener(weekdayListener);
        checkBoxFri.setOnCheckedChangeListener(weekdayListener);
        checkBoxSat.setOnCheckedChangeListener(weekdayListener);
        checkBoxSun.setOnCheckedChangeListener(weekdayListener);
    }

    private String getDays() {

        StringBuilder selectedDays = new StringBuilder();
        if (checkBoxOnce.isChecked()) {
            selectedDays.append("Once");
        } else {
            if (checkBoxMon.isChecked()) selectedDays.append("Monday, ");
            if (checkBoxTue.isChecked()) selectedDays.append("Tuesday, ");
            if (checkBoxWed.isChecked()) selectedDays.append("Wednesday, ");
            if (checkBoxThu.isChecked()) selectedDays.append("Thursday, ");
            if (checkBoxFri.isChecked()) selectedDays.append("Friday, ");
            if (checkBoxSat.isChecked()) selectedDays.append("Saturday, ");
            if (checkBoxSun.isChecked()) selectedDays.append("Sunday, ");
        }

        // Remove last comma and space if needed
        String repeatDays = selectedDays.toString().trim();
        if (repeatDays.endsWith(",")) {
            repeatDays = repeatDays.substring(0, repeatDays.length() - 1);

        }
        return repeatDays;
    }

    private boolean getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("taskName") && getIntent().hasExtra("time") && getIntent().hasExtra("task")) {
            //Getting data from intent from the budgetCustomAdapter
            taskId = getIntent().getStringExtra("id");
            taskName = getIntent().getStringExtra("taskName");
            selectedTime = getIntent().getStringExtra("time");
            task = getIntent().getStringExtra("task");

            newTaskText.setText(taskName);
            btnTime.setText(selectedTime);
            return true;
        }
        else {
            return false;
        }
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
                Intent intent = new Intent(NewDailyTask.this, MainActivity.class);
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