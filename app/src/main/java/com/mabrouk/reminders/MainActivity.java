package com.mabrouk.reminders;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mabrouk.reminders.adapter.ReminderAdapter;
import com.mabrouk.reminders.db.DBAccessor;
import com.mabrouk.reminders.model.Reminder;
import com.mabrouk.reminders.util.ReminderApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_REQUEST_CODE = 1;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.add_button) FloatingActionButton addReminderButton;
    @BindView(R.id.section_spinner) Spinner spinner;

    ReminderAdapter activeAdapter;
    ReminderAdapter completedAdapter;

    Subscription busSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item,
                getResources().getStringArray(R.array.spinner_sections)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recyclerView.setAdapter(position == 0 ? activeAdapter : completedAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        addReminderButton.setOnClickListener(v -> ReminderFormActivity.startInstanceForResult(this, ADD_REQUEST_CODE));

        Observable.just(0)
                .map(dummy -> DBAccessor.getInstance().getActiveReminders())
                .subscribe(reminders -> {
                    activeAdapter = new ReminderAdapter(reminders);
                    if(spinner.getSelectedItemPosition() == 0)
                        recyclerView.setAdapter(activeAdapter);
                });

        Observable.just(0)
                .map(dummy -> DBAccessor.getInstance().getCompletedReminders())
                .subscribe(reminders -> {
                    completedAdapter = new ReminderAdapter(reminders);
                    if(spinner.getSelectedItemPosition() == 1)
                        recyclerView.setAdapter(completedAdapter);
                });

        busSubscription = ReminderApplication.getRxBusInstance().getObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::reminderCompleted);
    }

    void reminderCompleted(Object object) {
        if(object instanceof Reminder) {
            Reminder reminder = (Reminder) object;
            activeAdapter.removeReminder(reminder);
            completedAdapter.addReminder(reminder);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            Reminder reminder = data.getParcelableExtra(ReminderFormActivity.EXTRA_RESULT);
            activeAdapter.addReminder(reminder);
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busSubscription.unsubscribe();
    }
}
