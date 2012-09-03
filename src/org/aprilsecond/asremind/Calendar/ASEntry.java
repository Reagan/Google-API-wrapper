package org.aprilsecond.asremind.Calendar;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class stores an instance of each of the events for a Google calendar and
 * stores the following information about the Entry <ol> <li>title</li>
 * <li>start time</li> <li>stop time</li> <li>reminder</li> </ol>
 *
 * @author Reagan Mbitiru <reaganmbitiru@gmail.com>
 */
public class ASEntry {

    /**
     * stores the entry title
     */
    private String title;
    /**
     * stores the start time
     */
    private Calendar startTime;
    /**
     * stores the stop time
     */
    private Calendar stopTime;
    /**
     * stores the reminder
     */
    private ArrayList<ASReminder> reminders;

    /**
     * null constructor
     */
    public ASEntry() {
    }

    /**
     * constructor initializes the entry components
     */
    public ASEntry(String _title, Calendar _startTime, Calendar _stopTime,
            ArrayList<ASReminder> _reminders) {
        title = _title;
        startTime = _startTime;
        stopTime = _stopTime;
        reminders = _reminders;
    }

    /**
     * sets the entry title
     */
    public void setTitle(String _title) {
        title = _title;
    }

    /**
     * gets the entry title
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets the entry start time
     */
    public void setStartTime(Calendar _startTime) {
        startTime = _startTime;
    }

    /**
     * gets the entry start time
     */
    public Calendar getStartTime() {
        return startTime;
    }

    /**
     * sets the entry stop time
     */
    public void setStopTime(Calendar _stopTime) {
        stopTime = _stopTime;
    }

    /**
     * gets the entry stop time
     */
    public Calendar getStopTime() {
        return stopTime;
    }

    /**
     * sets the entry reminder
     */
    public void setReminders(ArrayList<ASReminder> _reminders) {
        reminders = _reminders;
    }

    /**
     * gets the entry reminder
     */
    public ArrayList<ASReminder> getReminders() {
        return reminders;
    }
}
