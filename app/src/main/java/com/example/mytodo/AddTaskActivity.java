package com.example.mytodo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytodo.data.Task;
import com.example.mytodo.data.TaskDatabase;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.mytodo.EXTRA_ID";

    private TextInputEditText editTextTitle, editTextDescription;
    private RadioGroup radioGroupPriority;
    private Button buttonPickDate;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private int taskId = -1;
    private boolean isCurrentTaskCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = findViewById(R.id.toolbar_add_task);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        radioGroupPriority = findViewById(R.id.radio_group_priority);
        buttonPickDate = findViewById(R.id.button_pick_date);
        Button buttonSaveTask = findViewById(R.id.button_save_task);

        buttonPickDate.setOnClickListener(v -> showDatePicker());
        buttonSaveTask.setOnClickListener(v -> saveTask());

        if (getIntent().hasExtra(EXTRA_ID)) {
            taskId = getIntent().getIntExtra(EXTRA_ID, -1);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Task");
            }
            loadTaskDetails();
        } else {
            updateDateButtonText();
        }
    }

    private void loadTaskDetails() {
        new Thread(() -> {
            Task task = TaskDatabase.getInstance(this).taskDao().getTaskById(taskId);
            if (task != null) {
                runOnUiThread(() -> {
                    editTextTitle.setText(task.getTitle());
                    editTextDescription.setText(task.getDescription());
                    isCurrentTaskCompleted = task.isCompleted();
                    calendar.setTimeInMillis(task.getDate());
                    updateDateButtonText();

                    switch (task.getPriority()) {
                        case 1:
                            radioGroupPriority.check(R.id.radio_low);
                            break;
                        case 2:
                            radioGroupPriority.check(R.id.radio_medium);
                            break;
                        case 3:
                        default:
                            radioGroupPriority.check(R.id.radio_high);
                            break;
                    }
                });
            }
        }).start();
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateButtonText();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButtonText() {
        buttonPickDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void saveTask() {
        String title = editTextTitle.getText() != null ? editTextTitle.getText().toString().trim() : "";
        String description = editTextDescription.getText() != null ? editTextDescription.getText().toString().trim() : "";
        long date = calendar.getTimeInMillis();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            return;
        }

        int priority;
        int checkedId = radioGroupPriority.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_low) {
            priority = 1;
        } else if (checkedId == R.id.radio_medium) {
            priority = 2;
        } else {
            priority = 3;
        }

        new Thread(() -> {
            if (taskId == -1) {
                Task task = new Task(title, description, priority, false, date);
                TaskDatabase.getInstance(this).taskDao().insert(task);
            } else {
                Task task = new Task(title, description, priority, isCurrentTaskCompleted, date);
                task.setId(taskId);
                TaskDatabase.getInstance(this).taskDao().update(task);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, taskId == -1 ? "Task Saved" : "Task Updated", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
