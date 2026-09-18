package com.example.mytodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytodo.adapter.TaskAdapter;
import com.example.mytodo.data.Task;
import com.example.mytodo.data.TaskDatabase;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TextView textViewEmptyState;
    private TaskDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = TaskDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recycler_view_tasks);
        textViewEmptyState = findViewById(R.id.text_view_empty_state);
        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter();
        adapter.setOnTaskClickListener(this);
        recyclerView.setAdapter(adapter);

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        new Thread(() -> {
            List<Task> tasks = database.taskDao().getAllTasks();
            runOnUiThread(() -> {
                if (tasks.isEmpty()) {
                    textViewEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    textViewEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setTasks(tasks);
                }
            });
        }).start();
    }

    @Override
    public void onTaskClick(Task task) {
        showTaskDetailBottomSheet(task);
    }

    private void showTaskDetailBottomSheet(Task task) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_task_detail, findViewById(android.R.id.content), false);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView tvTitle = bottomSheetView.findViewById(R.id.detail_title);
        TextView tvDescription = bottomSheetView.findViewById(R.id.detail_description);
        TextView tvPriority = bottomSheetView.findViewById(R.id.detail_priority);
        TextView tvStatus = bottomSheetView.findViewById(R.id.detail_status);
        Button btnEdit = bottomSheetView.findViewById(R.id.button_edit_from_detail);

        tvTitle.setText(task.getTitle());
        tvDescription.setText(task.getDescription());

        // Setup Priority Badge
        switch (task.getPriority()) {
            case 1:
                tvPriority.setText(R.string.low);
                tvPriority.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_low));
                break;
            case 2:
                tvPriority.setText(R.string.medium);
                tvPriority.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_medium));
                break;
            case 3:
            default:
                tvPriority.setText(R.string.high);
                tvPriority.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_high));
                break;
        }

        // Setup Status Badge
        if (task.isCompleted()) {
            tvStatus.setText(R.string.completed);
            tvStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_low));
        } else {
            tvStatus.setText(R.string.pending);
            tvStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.priority_medium));
        }

        btnEdit.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            intent.putExtra(AddTaskActivity.EXTRA_ID, task.getId());
            startActivity(intent);
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onTaskDelete(Task task) {
        new Thread(() -> {
            database.taskDao().delete(task);
            runOnUiThread(() -> {
                Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
                loadTasks();
            });
        }).start();
    }

    @Override
    public void onTaskStatusChanged(Task task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        new Thread(() -> {
            database.taskDao().update(task);
            runOnUiThread(this::loadTasks);
        }).start();
    }
}
