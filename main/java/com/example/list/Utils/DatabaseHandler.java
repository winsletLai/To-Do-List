package com.example.list.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.list.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id", TASK = "task", TASKNAME = "taskName", DATE = "date", DAYS = "days", TIME = "time", TYPE = "type", STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
    + TASK + " TEXT, " + TASKNAME + " TEXT, " + DATE + " DATE, " + DAYS + " TEXT, " + TIME + " TEXT, " + TYPE + " TEXT, " + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context, NAME, null,VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
        //Drop the older tables
        db.execSQL("DROP TABlE IF EXISTS " + TODO_TABLE);
        //Create table again
        onCreate(db);
    }

    public void openDatabase(){
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK,task.getTask());
        cv.put(TASKNAME,task.getTaskName());
        cv.put(DATE,task.getDate());
        cv.put(DAYS,task.getDays());
        cv.put(TIME,task.getTime());
        cv.put(TYPE,task.getType());
        cv.put(STATUS,0);
        long result = db.insert(TODO_TABLE,null,cv);
        if(result == -1){
            Toast.makeText(context, "Failed to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Save", Toast.LENGTH_SHORT).show();
        }
    }

    public List<ToDoModel> getTodayTasks(String date,String day){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();

        try{
            String query = "SELECT * FROM " + TODO_TABLE +
                    " WHERE (" + TYPE + " = 'Special' AND " + DATE + " = ?)" +
                    " OR (" + TYPE + " = 'Recurring' AND " + DAYS + " LIKE ?)" +
                    " OR (" + TYPE + " = 'Recurring' AND " + DAYS + " = 'Once' AND " + DATE + " = ?)" +
                    " ORDER BY " + STATUS + " ASC, " + TIME + " ASC ";

            String dayParam = "%" + day + "%";
            cur = db.rawQuery(query, new String[]{date, dayParam,date});

            if(cur!=null) {
                if (cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setTaskName(cur.getString(cur.getColumnIndex(TASKNAME)));
                        task.setDate(cur.getString(cur.getColumnIndex(DATE)));
                        task.setDays(cur.getString(cur.getColumnIndex(DAYS)));
                        task.setTime(cur.getString(cur.getColumnIndex(TIME)));
                        task.setType(cur.getString(cur.getColumnIndex(TYPE)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS,status);
        db.update(TODO_TABLE,cv,ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(String id,ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK,task.getTask());
        cv.put(TASKNAME,task.getTaskName());
        cv.put(DATE,task.getDate());
        cv.put(DAYS,task.getDays());
        cv.put(TIME,task.getTime());
        cv.put(TYPE,task.getType());
        cv.put(STATUS,0);
        long result = db.update(TODO_TABLE,cv, ID + "=?", new String[]{id});
        if(result == -1){
            Toast.makeText(context, "Failed to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Save", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTask(int id){
        long result = db.delete(TODO_TABLE,ID + "=?", new String[]{String.valueOf(id)});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Delete", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteWholeData(){
        db.execSQL("DELETE FROM " + TODO_TABLE);
        Toast.makeText(context, "Successfully Delete", Toast.LENGTH_SHORT).show();
    };

}
