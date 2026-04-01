package com.example.list.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.list.Adapter.ToDoAdapter;
import com.example.list.Model.ToDoModel;
import com.example.list.R;
import com.example.list.RecyclerItemTouchHelper;
import com.example.list.Utils.DatabaseHandler;
import com.example.list.Utils.DatePickerUtil;
import com.example.list.databinding.FragmentHomeBinding;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView taskRecyclerView;
    private ToDoAdapter taskAdapter;
    private List<ToDoModel> taskList;
    private DatabaseHandler db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = new DatabaseHandler(root.getContext());
        db.openDatabase();

        taskList = new ArrayList<>();

        taskRecyclerView = root.findViewById(R.id.tasksRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        taskAdapter = new ToDoAdapter(db,HomeFragment.this);
        taskRecyclerView.setAdapter(taskAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);

        String date = DatePickerUtil.getTodayFormattedDate();
        LocalDate todayDate = LocalDate.parse(date);
        String dayOfTodayDate = todayDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        TextView taskDate = root.findViewById(R.id.tasksDate);
        TextView taskDay = root.findViewById(R.id.tasksDay);
        taskDate.setText(date);
        taskDay.setText(dayOfTodayDate);
        taskList = db.getTodayTasks(date,dayOfTodayDate);
        taskAdapter.setTasks(taskList);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}