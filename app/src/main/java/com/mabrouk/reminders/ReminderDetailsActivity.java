package com.mabrouk.reminders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mabrouk.reminders.model.Reminder;
import com.mabrouk.reminders.util.DateFormatUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String EXTRA_REMINDER = "reminder";

    public static void startInstance(Context other, Reminder reminder) {
        Intent intent = createIntent(other, reminder);
        other.startActivity(intent);
    }

    public static Intent createIntent(Context context, Reminder reminder) {
        Intent intent = new Intent(context, ReminderDetailsActivity.class);
        intent.putExtra(EXTRA_REMINDER, reminder);
        return intent;
    }

    @BindView(R.id.time_text) TextView timeText;
    @BindView(R.id.map_layout) View mapLayout;
    @BindView(R.id.time_layout) View timeLayout;

    Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_details);
        reminder = getIntent().getParcelableExtra(EXTRA_REMINDER);
        setTitle(reminder.getName());

        ButterKnife.bind(this);

        if(reminder.getType() == Reminder.TYPE_LOCATION_REMINDER) {
            mapLayout.setVisibility(View.VISIBLE);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }else{
            Date date = new Date(reminder.getTimestamp());
            timeLayout.setVisibility(View.VISIBLE);
            timeText.setText(DateFormatUtil.formatDate(date));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(reminder.getLatitude(), reminder.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng).title(reminder.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
}
