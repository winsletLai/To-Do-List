package com.example.list.ui.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.list.NewDailyTask;
import com.example.list.NewSpecialOccasion;
import com.example.list.R;
import com.example.list.databinding.FragmentAddBinding;

public class AddFragment extends Fragment {

    Button dailyTask,specialOccasion;

    private FragmentAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dailyTask = root.findViewById(R.id.btnDailyReminder);
        specialOccasion = root.findViewById(R.id.btnSpecialOccasion);

        dailyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(root.getContext(), NewDailyTask.class);
                startActivity(intent);
            }
        });
        specialOccasion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(root.getContext(), NewSpecialOccasion.class);
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}