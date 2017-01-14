package com.mabrouk.reminders.util;


import com.mabrouk.reminders.model.Reminder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by ahmad on 1/14/17.
 */

public class RxBusTest {
    RxBus bus;
    @Before
    public void setup() {
        bus = new RxBus();

        //Rx hooks to override schedulers
        //Schedulers aren't being used currently with the bus, but just in case i's added later on
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook(){
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @Test
    public void testBus() {
        List<Reminder> reminders = new ArrayList<>();
        bus.getObservable().subscribe(o -> {
            if(o instanceof Reminder)
                reminders.add((Reminder) o);
        });
        bus.send(Reminder.createTimeReminder(0, "", System.currentTimeMillis(), false));
        bus.send(new String("dummy string"));
        bus.send(Reminder.createTimeReminder(0, "", System.currentTimeMillis(), true));
        assertThat(reminders).size().isEqualTo(2);
    }
}
