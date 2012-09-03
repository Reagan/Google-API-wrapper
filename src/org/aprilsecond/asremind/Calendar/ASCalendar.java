package org.aprilsecond.asremind.Calendar;

import java.awt.Color;
import java.util.ArrayList;

/**
 * This class stores an instance of a calendar capturing the following information 
 * about the calendar entry : 
 * <ol>
 *  <li>Title</li>
 *  <li>Color</li>
 *  <li>id</li>
 *  <li>entries for the calendar</li>
 * </ol>
 * 
 * @author Reagan Mbitiru <reaganmbitiru@gmail.com>
 */
public class ASCalendar {
    
    /**
     * stores the calendar title
     */
    private String calTitle ;
    
    /**
     * stores the color ID for the calendar
     */
    private String calColorID ;
    
    /**
     * stores the color for the calendar
     */
    private Color calColor ; 
    
    /**
     * Stores the ID for the calendar
     */
    private String calID ;
    
    /**
     * stores the entries for the calendar
     */
    private ArrayList<ASEntry> calEntries ;
    
    /**
     * null constructor
     */
    public ASCalendar() {}
    
    /**
     * constructor initializes required components
     */
    public ASCalendar (String calendarTitle, Color calendarColor, 
            String calendarID, ArrayList<ASEntry> calendarEntries) {
        calTitle = calendarTitle ;
        calColor = calendarColor ; 
        calID = calendarID ;
        calEntries = calendarEntries ;
    }
    
    /**
     * sets the calendar title
     */
    public void setTitle(String calendarTitle) {
        calTitle = calendarTitle ;
    }
    
    /**
     * gets the calendar title
     */
    public String getTitle() {
        return calTitle ;
    }
    
    /**
     * sets the calendar color ID
     */
    public void setColorID(String colorID) {
        calColorID = colorID ;
    }
    
    /**
     * gets the calendar color ID
     */
    public String getColorID() {
        return calColorID ;
    }
    
    /**
     * sets the calendar Color
     */
    public void setColor(Color calendarColor) {
        calColor = calendarColor;
    }
    
    /**
     * gets the calendar Color
     */
    public Color getColor() {
        return calColor ;
    }
    
     /**
     * sets the calendar ID
     */
    public void setID(String calendarID) {
        calID = calendarID;
    }
    
    /**
     * gets the calendar ID
     */
    public String getID() {
        return calID ;
    }
    
    /**
     * sets the calendar Entries
     */
    public void setEntries(ArrayList<ASEntry> calendarEntries) {
        calEntries = calendarEntries;
    }
    
    /**
     * gets the calendar Entries
     */
    public ArrayList<ASEntry> getEntries() {
        return calEntries ;
    }
}
