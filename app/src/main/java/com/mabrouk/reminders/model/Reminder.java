package com.mabrouk.reminders.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmad on 1/13/17.
 */

public class Reminder implements Parcelable {
    public static final int TYPE_TIME_REMINDER = 1;
    public static final int TYPE_LOCATION_REMINDER = 2;

    public static Reminder createTimeReminder(long id, String name, long timestamp, boolean completed) {
        return new Reminder(id, TYPE_TIME_REMINDER, name, timestamp, 0, 0, completed);
    }

    public static Reminder createLocationReminder(long id, String name, double latitude, double longitude,
                                                  boolean completed) {
        return new Reminder(id, TYPE_LOCATION_REMINDER, name, 0, latitude, longitude, completed);
    }

    long id;
    int type;
    String name;
    long timestamp;
    double latitude;
    double longitude;
    boolean completed;

    private Reminder(long id, int type, String name, long timestamp, double latitude, double longitude, boolean completed) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.name);
        dest.writeLong(this.timestamp);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeByte(this.completed ? (byte) 1 : (byte) 0);
    }

    protected Reminder(Parcel in) {
        this.id = in.readLong();
        this.type = in.readInt();
        this.name = in.readString();
        this.timestamp = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.completed = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel source) {
            return new Reminder(source);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };
}
