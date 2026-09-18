package com.example.mytodo.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytodo.R;
import com.example.mytodo.data.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private final List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskDelete(Task task);
        void onTaskStatusChanged(Task task, boolean isCompleted);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> newTasks) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return tasks.size();
            }

            @Override
            public int getNewListSize() {
                return newTasks.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return tasks.get(oldItemPosition).getId() == newTasks.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Task oldTask = tasks.get(oldItemPosition);
                Task newTask = newTasks.get(newItemPosition);
                return oldTask.getTitle().equals(newTask.getTitle()) &&
                        oldTask.getDescription().equals(newTask.getDescription()) &&
                        oldTask.getPriority() == newTask.getPriority() &&
                        oldTask.isCompleted() == newTask.isCompleted() &&
                        oldTask.getDate() == newTask.getDate();
            }
        });

        this.tasks.clear();
        this.tasks.addAll(newTasks);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.textViewTitle.setText(currentTask.getTitle());
        holder.textViewDescription.setText(currentTask.getDescription());
        holder.checkBoxCompleted.setChecked(currentTask.isCompleted());

        // Apply Google Keep strikethrough and dim effect
        if (currentTask.isCompleted()) {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.itemView.setAlpha(1.0f);
        }

        // Set priority badge color
        int priorityColor;
        switch (currentTask.getPriority()) {
            case 1: // Low
                priorityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_low);
                break;
            case 2: // Medium
                priorityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_medium);
                break;
            case 3: // High
            default:
                priorityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_high);
                break;
        }
        holder.priorityBadge.setBackgroundColor(priorityColor);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final CheckBox checkBoxCompleted;
        private final View priorityBadge;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            priorityBadge = itemView.findViewById(R.id.priority_badge);
            ImageButton buttonDelete = itemView.findViewById(R.id.button_delete);

            buttonDelete.setOnClickListener(v -> {
                TaskAdapter adapter = (TaskAdapter) getBindingAdapter();
                int position = getBindingAdapterPosition();
                if (adapter != null && adapter.listener != null && position != RecyclerView.NO_POSITION) {
                    adapter.listener.onTaskDelete(adapter.tasks.get(position));
                }
            });

            checkBoxCompleted.setOnClickListener(v -> {
                TaskAdapter adapter = (TaskAdapter) getBindingAdapter();
                int position = getBindingAdapterPosition();
                if (adapter != null && adapter.listener != null && position != RecyclerView.NO_POSITION) {
                    adapter.listener.onTaskStatusChanged(adapter.tasks.get(position), checkBoxCompleted.isChecked());
                }
            });

            itemView.setOnClickListener(v -> {
                TaskAdapter adapter = (TaskAdapter) getBindingAdapter();
                int position = getBindingAdapterPosition();
                if (adapter != null && adapter.listener != null && position != RecyclerView.NO_POSITION) {
                    adapter.listener.onTaskClick(adapter.tasks.get(position));
                }
            });
        }
    }
}
