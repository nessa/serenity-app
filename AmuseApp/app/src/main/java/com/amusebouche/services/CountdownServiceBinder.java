package com.amusebouche.services;

import android.os.Binder;

/**
 * Binder for {@link CountdownService}.
 *
 * @author Noelia Sales Montes
 */
public class CountdownServiceBinder extends Binder {

    private CountdownService service;

    public CountdownServiceBinder(CountdownService service) {
        this.service = service;
    }

    public CountdownService getCountdownService() {
        return this.service;
    }
}
