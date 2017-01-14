package com.mabrouk.reminders.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mabrouk.reminders.R;
import com.mabrouk.reminders.ReminderDetailsActivity;
import com.mabrouk.reminders.model.Reminder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by ahmad on 1/13/17.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    List<Reminder> reminders;
    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        notifyItemInserted(reminders.size() - 1);
    }

    public void removeReminder(Reminder reminder) {
        int index = indexOfReminder(reminder);
        reminders.remove(index);
        notifyItemRemoved(index);
    }

    private int indexOfReminder(Reminder reminder) {
        int size = reminders.size();
        for(int i = 0; i < size; i++) {
            if(reminder.getId() == reminders.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reminder, parent, false);
        ReminderViewHolder holder = new ReminderViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.name.setText(reminder.getName());
        holder.itemView.setOnClickListener(v -> ReminderDetailsActivity.startInstance(v.getContext(), reminder));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;
        public ReminderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
