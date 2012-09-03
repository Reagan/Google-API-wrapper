package org.aprilsecond.googleoauthwrapper;

import java.io.File;
import java.util.ArrayList;
import org.aprilsecond.asremind.Calendar.ASCalendar;
import org.aprilsecond.asremind.Calendar.ASEntry;
import org.aprilsecond.asremind.Calendar.ASReminder;
import org.aprilsecond.asremind.configurations.Configurations;

/**
 * This class tests the Google Calendar API to ensure
 * it returns correct data. The test data used is for the ASRemind application
 * @author Reagan
 */
public class TestGoogleCalendarAPI {    
     
    /**
     * stores the path to the config file 
     * with the client ID and secret to the application
     */
    private static String APP_CONFIG
            = "app.config" ;
    
    /**
     * stores the path to the config file to the application
     */
    private static String APP_CONFIG_FILE_PATH 
            = ("appSettings/configs/" + APP_CONFIG).replace('/',
                    File.separatorChar) ;
    
    
    public static void main(String [] args) {
                
        // load the configurations for the calendar object 
        Configurations configs = Configurations.getInstance(APP_CONFIG_FILE_PATH) ;
                
        // initialize Google Calendar Object
        GoogleCalendar cal = GoogleCalendar.getInstance(configs) ;
        
        // get the list of calendars for a user
        ArrayList<ASCalendar> calendars = cal.getCalendarsForUser() ;
        
        // loop through the user calendars and get the calendar details
        for (int calendarsCount = 0 ; calendarsCount < 
                calendars.size() ; calendarsCount++) {
            // display the calendar info
            System.out.println("\ncal title : " + calendars.get(calendarsCount).getTitle()) ;
            System.out.println("cal color : " + calendars.get(calendarsCount).getColor()) ;
            System.out.println("cal ID : " + calendars.get(calendarsCount).getID()) ;
            System.out.println("Number of entries : " + calendars.get(calendarsCount).getEntries().size()) ;
            
            // get the entry details for the current calendar
            ArrayList<ASEntry> calEntries = calendars.get(calendarsCount).getEntries();
            for (int entriesSize = 0 ; entriesSize 
                    < calEntries.size() ; 
                    entriesSize++) {
                ASEntry currEntry = calEntries.get(entriesSize) ;
                System.out.println("\t Entry # : " + entriesSize ) ;
                System.out.println("\t\t Title : " + currEntry.getTitle()) ;
                System.out.println("\t\t Start Time : " + currEntry.getStartTime()) ;
                System.out.println("\t\t Stop Time : " + currEntry.getStopTime()) ;
                
                // get the reminders for the current entry
                ArrayList<ASReminder> entryReminders = currEntry.getReminders() ;
                
                for (int remindersCounter = 0 ; 
                        remindersCounter < entryReminders.size(); 
                        remindersCounter ++ ) {
                    ASReminder currReminder = entryReminders.get(remindersCounter) ;
                    System.out.println("\t\t\t Reminder #:  " + remindersCounter) ;
                    System.out.println("\t\t\t\t Minutes #:  " + currReminder.getMinutes()) ;
                }
            }
        }
        /*
        for ( int calendarsCount = 0 ; calendarsCount < 
                calendars.size() ; calendarsCount ++) {
            System.out.println("Calendar : " + calendars.get(calendarsCount)) ;
        }
        
        // get the events for each of the calendars
        for (int calendarCounter = 0 ; calendarCounter < calendars.size() ;
                calendarCounter++) {            
                        
            // loop through displaying the events for the calendar
            System.out.println("\nCalendar *** " + calendars.get(calendarCounter) 
                    + " ***\n") ;
            ArrayList<Event> currEvents
                    = cal.getCalendarEvents(calendars.get(calendarCounter), null, null);
            
            if (currEvents == null) continue ;
            
            for (int i = 0 ; i <currEvents.size();
                    i++) {
                System.out.println("Event : " + currEvents.get(i).getSummary()
                        + " Start : " + currEvents.get(i).getStart() 
                        + " End : " + currEvents.get(i).getEnd());
            }
        }
        */
    }
}
