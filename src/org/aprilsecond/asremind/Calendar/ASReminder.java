package org.aprilsecond.asremind.Calendar;

/**
 * This class stores the reminder for an entry. It stores the 
 * following information about the reminder
 * <ol><li>minutes</li></ol>
 * @author Reagan Mbitiru <reaganmbitiru@gmail.com>
 */
public class ASReminder {
    
    /**
     * stores the number of minutes before reminder
     */
    private int reminderMinutes = 0 ;
    
    /**
     * null constructor initializes reminder
     */
    public ASReminder() {}
    
    /**
     * constructor initializes reminder time
     */
    public ASReminder (int minutes) {
        reminderMinutes = minutes ;
    }
    
    /**
     * gets the reminder time
     */
    public int getMinutes() {
        return reminderMinutes ;
    }
    
    /**
     * sets the reminder time
     */
    public void setMinutes(int minutes) {
        reminderMinutes = minutes ;
    }
}
