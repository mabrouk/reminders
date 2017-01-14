package com.mabrouk.reminders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mabrouk.reminders.db.DBAccessor;
import com.mabrouk.reminders.fragments.DatePickerFragment;
import com.mabrouk.reminders.fragments.TimePickerFragment;
import com.mabrouk.reminders.model.Reminder;
import com.mabrouk.reminders.util.ReminderManager;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReminderFormActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener,
        TimePickerFragment.OnTimeSelectedListener, OnMapReadyCallback {
    public static final String EXTRA_RESULT = "reminder_result";

    public static void startInstanceForResult(Activity other, int requestCode) {
        Intent intent = new Intent(other, ReminderFormActivity.class);
        other.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.time_reminder_button) CheckedTextView timeButton;
    @BindView(R.id.location_reminder_button) CheckedTextView locationButton;
    @BindView(R.id.reminder_name) EditText reminderName;
    @BindView(R.id.time_pick_layout) View timeLayout;
    @BindView(R.id.time_pick_button) Button timePickButton;
    @BindView(R.id.date_pick_button) Button datePickButton;
    @BindView(R.id.create_button) Button createButton;
    @BindView(R.id.map_layout) View mapLayout;

    int year, month, day, hour, minute;
    GoogleMap map;

    Location currentLocation;
    LatLng selectedLatLng;
    Marker selectedMarker;
    ReactiveLocationProvider locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_form);
        setTitle("Create Reminder");
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        timeButton.setOnClickListener(v -> {
            locationButton.setChecked(false);
            timeButton.setChecked(true);
            timeLayout.setVisibility(View.VISIBLE);
            mapLayout.setVisibility(View.INVISIBLE);
        });

        locationButton.setOnClickListener(v -> {
            locationButton.setChecked(true);
            timeButton.setChecked(false);
            timeLayout.setVisibility(View.GONE);
            mapLayout.setVisibility(View.VISIBLE);
        });

        datePickButton.setOnClickListener(view -> {
            DatePickerFragment fragment = DatePickerFragment.createInstance(day, month, year);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });

        timePickButton.setOnClickListener(v -> {
            TimePickerFragment fragment = TimePickerFragment.newInstance(hour, minute);
            fragment.show(getSupportFragmentManager(), "timePicker");
        });

        createButton.setOnClickListener(v -> {
            if(reminderName.getText().length() == 0) {
                Toast.makeText(this, "Please enter a name for you reminder", Toast.LENGTH_SHORT).show();
                return;
            }

            if(timeButton.isChecked()) {
                createTimeReminder();
            }else if(locationButton.isChecked()) {
                createLocationReminder();
            }else{
                Toast.makeText(ReminderFormActivity.this, "Select type of reminder", Toast.LENGTH_SHORT).show();
            }
        });

        setDateTimeToNow();
        getCurrentLocation();
    }

    void createTimeReminder() {
        String name = reminderName.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Reminder timeReminder = Reminder.createTimeReminder(0, name, calendar.getTimeInMillis(), false);
        Observable.just(timeReminder)
                .map(DBAccessor.getInstance()::createReminder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::reminderCreated);
    }

    void createLocationReminder() {
        String name = reminderName.getText().toString();
        Reminder locationReminder = Reminder.createLocationReminder(0, name, selectedLatLng.latitude, selectedLatLng.longitude, false);
        Observable.just(locationReminder)
                .map(DBAccessor.getInstance()::createReminder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::reminderCreated);
    }

    void setDateTimeToNow() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    void getCurrentLocation() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setInterval(100);

        locationProvider = new ReactiveLocationProvider(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider.getUpdatedLocation(request).take(1).subscribe(this::gotLocation);
        }else{
            requestLocationPermission();
        }
    }

    void requestLocationPermission() {
        new RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        getCurrentLocation();
                    } else {
                        Toast.makeText(this, "Providing your location helps setting alrams in the same country as you are",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void gotLocation(Location location) {
        currentLocation = location;
        zoomToCurrentLocation();
    }

    void reminderCreated(@Nullable Reminder reminder) {
        if(reminder != null) {
            if(reminder.getType() == Reminder.TYPE_LOCATION_REMINDER)
                ReminderManager.createGeofenceForReminder(locationProvider, reminder, this);
            else
                ReminderManager.createAlarmForReminder(reminder, this);
            setResultAndFinish(reminder);
        }else{
            errorInReminderCreation(null);
        }
    }

    void errorInReminderCreation(@Nullable Throwable throwable) {
        if(throwable != null)
            throwable.printStackTrace();
        Toast.makeText(this, "An error occured in creating reminder, please try again", Toast.LENGTH_SHORT).show();
    }

    void setResultAndFinish(Reminder reminder) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RESULT, reminder);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        datePickButton.setText(String.format("%d-%d-%d", year, month + 1, day));
    }

    @Override
    public void onTimeSelected(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        timePickButton.setText(String.format("%d:%d", hour, minute));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            if(selectedMarker != null)
                selectedMarker.remove();
            selectedMarker = map.addMarker(new MarkerOptions().title("Selected location").position(selectedLatLng));
        });
        zoomToCurrentLocation();
    }

    void zoomToCurrentLocation() {
        if(selectedLatLng == null && map != null && currentLocation != null) {
            LatLng currentLocation = new LatLng(this.currentLocation.getLatitude(), this.currentLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }
}
