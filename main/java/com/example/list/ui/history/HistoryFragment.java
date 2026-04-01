package com.example.list.ui.history;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.list.Adapter.CalendarAdapter;
import com.example.list.Adapter.EventAdapter;
import com.example.list.MainActivity;
import com.example.list.Model.ToDoModel;
import com.example.list.R;
import com.example.list.Utils.DatabaseHandler;
import com.example.list.Utils.DatePickerUtil;
import com.example.list.databinding.FragmentHistoryBinding;
import com.example.list.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment implements CalendarAdapter.OnItemListener{

    private FragmentHistoryBinding binding;
    private TextView monthYearText, eventTitle;
    private Button previousMonthAction, nextMonthAction;
    private RecyclerView calendarRecyclerView, eventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<ToDoModel> eventList;
    private DatabaseHandler db;
    private LocalDate selectedDate;
    private FloatingActionButton fabDeleteAll;
    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.rootView = root;

        db = new DatabaseHandler(root.getContext());
        db.openDatabase();

        eventList = new ArrayList<>();

        find(root);
        selectedDate = LocalDate.now();
        setMonthView(root);
        String dayOfTodayDate = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String pickedDate = "" + selectedDate;
        eventList = db.getTodayTasks(pickedDate,dayOfTodayDate);
        setEventView(root);

        fabDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog(root);
            }
        });

        String date = DatePickerUtil.getTodayFormattedDate();
        String day = date.substring(8);
        String message = "Event of: " + day + " " + monthYearFromDate(selectedDate);
        eventTitle.setText(message);

        previousMonthAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.minusMonths(1);
                setMonthView(root);
            }
        });

        nextMonthAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = selectedDate.plusMonths(1);
                setMonthView(root);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setEventView(View root) {
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(root)));
        eventAdapter = new EventAdapter(db,HistoryFragment.this,eventList);
        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void find(View root)
    {
        eventsRecyclerView = root.findViewById(R.id.eventRecyclerView);
        calendarRecyclerView = root.findViewById(R.id.calendarRecyclerView);
        monthYearText = root.findViewById(R.id.monthYearTV);
        eventTitle = root.findViewById(R.id.EventTitle);
        fabDeleteAll = root.findViewById(R.id.fabDeleteAll);
        previousMonthAction = root.findViewById(R.id.previousMonthAction);
        nextMonthAction = root.findViewById(R.id.nextMonthAction);
    }

    private void setMonthView(View root)
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(root), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }


    @Override
    public void onItemClick(int position, String dayText)
    {
        if(!dayText.equals(""))
        {
            String message = "Event of: " + dayText + " " + monthYearFromDate(selectedDate);

            int day = Integer.parseInt(dayText);

            String monthYearString = monthYearFromDate(selectedDate);
            DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
            YearMonth yearMonth = YearMonth.parse(monthYearString, monthYearFormatter);

            LocalDate pickedDate = yearMonth.atDay(day);

            String dayOfPickedDate = pickedDate.getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.getDefault());

            eventTitle.setText(message);

            String pickedDateStr = "" + pickedDate;

            eventList.clear();
            eventList = db.getTodayTasks(pickedDateStr,dayOfPickedDate);
            eventAdapter.notifyDataSetChanged();
            setEventView(rootView);


        }
    }
    void confirmDialog(View root){
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        builder.setTitle("Delete All Data?");
        builder.setMessage("Are you sure you want to delete all recorded task?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.deleteWholeData();
                Context context = root.getContext();
                Intent intent = new Intent(context, MainActivity.class);
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
    private Context getContext(View root){
        return root.getContext();
    }
}