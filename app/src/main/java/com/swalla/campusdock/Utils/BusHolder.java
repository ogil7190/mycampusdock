package com.swalla.campusdock.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by vivekrajpoot on 11/03/18.
 */

public class BusHolder {
    private static EventBus eventBus;

    public static EventBus getInstnace() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    private BusHolder() {
    }
}
