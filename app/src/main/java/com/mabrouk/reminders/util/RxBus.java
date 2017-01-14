package com.mabrouk.reminders.util;

import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.Relay;

import rx.Observable;


public class RxBus {

    private final Relay<Object, Object> bus = PublishRelay.create().toSerialized();

    public void send(Object o) {
        bus.call(o);
    }

    public Observable<Object> getObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}