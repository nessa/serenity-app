package com.amusebouche.services;

/**
 * Represents the internal state of the Countdown Service.
 *
 * @author Noelia Sales Montes
 */
public enum CountdownServiceState {

    /**
     * This is the initial state, either the service has not yet begun to count down or a
     * former countdown has reached zero or was stopped by the user (in which case this
     * service returns to this initial state).
     */
    WAITING,

    /**
     * The service is currently counting down.
     */
    COUNTING_DOWN,

    /**
     * The countdown has reached zero and the alarm is ringing. The alarm has not yet been
     * stopped.
     */
    BEEPING,

    /**
     * The countdown had reached zero and the alarm has been ringing and has been stopped by
     * the user interaction or automatically (at the end of the Long.MAX_VALUE milliseconds).
     */
    FINISHED
}