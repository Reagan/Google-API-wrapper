package org.aprilsecond.googleoauthwrapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.aprilsecond.asremind.Calendar.ASCalendar;
import org.aprilsecond.asremind.Calendar.ASEntry;
import org.aprilsecond.asremind.Calendar.ASReminder;
import org.aprilsecond.asremind.configurations.Configurations;
/**
 * This class creates a Google Calendar Instance that 
 * includes authentication and defines all APIs required. The class implements
 * a singleton design to ensure that the necessary authentication
 * is done before the calendar class is used.
 * 
 * @author Reagan Mbitiru <reaganmbitiru@gmail.com>. 
 */
public class GoogleCalendar {
    
    /**
     * stores this instance of the object
     */
    private static GoogleCalendar thisInstance ;
    
    /**
     * stores the calendar service for the component
     */
    private Calendar service ;
        
    /**
     * stores the config file for the Google calendar class
     */
    private static Configurations configurations ;
    
    /**
     * stores the authentication object
     */
    private GoogleOAuthAPiWrapper auth ;
    
    /**
     * stores the calendars for the user
     */
    private ArrayList<ASCalendar> calendars ;
    
    /**
     * stores the reminders for each of the events
     */
    private List<EventReminder> reminders ;
    
    /**
     * constructor initializes Singleton design
     */
    private GoogleCalendar() throws IOException {
        
        // initialize the authorization module
        auth = new GoogleOAuthAPiWrapper(configurations) ;
        
        // authenticate
        auth.authenticate();
        
        // initialize the calendar service
        initializeCalendarService() ;
    }
    
    /**
     * implements getInstance()
     */
    public static GoogleCalendar getInstance(Configurations configs) {
        
        // initialize the configs
        configurations = configs ;
        
        // load the Google Calendar using the initialized settings
        try {
            thisInstance = new GoogleCalendar() ;
        } catch (IOException ex) {
            System.out.println("Error initializing Google Calendar"
                    + " instance : " + ex);
        }             
        
        return thisInstance ;
    }
    
    /**
     * initializes the calendar service
     */
    private void initializeCalendarService() {
        service =  Calendar.builder(auth.httpTransport, auth.jsonFactory)
                .setApplicationName("ASRemind").setHttpRequestInitializer(auth.credential)
                .build() ;        
    }
    
    /**
     * gets the calendars for a specific user
     */
    public ArrayList<ASCalendar> getCalendarsForUser() {
                //initializeCalendarService();
        // initialize the calendars object
        calendars = new ArrayList<ASCalendar>() ;       
        
        // run the CalendarList
        CalendarList calendarsEntries = null ;
        try {
            calendarsEntries = service.calendarList().list().execute();
        } catch (IOException ex) {
            System.out.println("Error obtaining Calendars' entries : " + ex);
        }
        
        // loop through the calendar list entries
        while (true) {
            try {
                for (CalendarListEntry calendarListEntry : service.calendarList().list().execute().getItems()) {
                    
                    // initialize instance of current calendar
                    ASCalendar currCal = new ASCalendar() ;
                    
                    // get the ID for the calendar Title
                    String title = calendarListEntry.getSummary() ;
                    currCal.setTitle(title);
                    
                    // get the title Color
                    String calColorID = calendarListEntry.getColorId() ;
                    currCal.setColorID(calColorID);
                                    
                    // get the title ID
                    String calID = calendarListEntry.getId() ;
                    currCal.setID(calID);
                    
                    // get the color for the calendar 
                    Color calColor = getCalendarColor(calColorID) ;
                    currCal.setColor(calColor);
                    
                    // get the events for the calendar
                    currCal.setEntries(getCalendarEvents(calID, null, null));
                    
                    // add to main list
                    calendars.add(currCal);
                }
            } catch (IOException ex) {
                Logger.getLogger(GoogleCalendar.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String pageToken = calendarsEntries.getNextPageToken();
            if (pageToken != null && !pageToken.isEmpty()) {
                try {
                    calendarsEntries = service.calendarList().list().setPageToken(pageToken).execute();                    
                } catch (IOException ex) {
                    System.out.println("Error setting page token for next calendar"
                            + ": " + ex);
                }
            } else {
                break;
            }
        }
        
        // return calendarNames ;
        return calendars ;
    }
    
    /**
     * gets the color definition for a calendar
     */
    private Color getCalendarColor(String calendarColorID) {
        Color calendarColor = null  ;
        
        try {
            Colors colors = service.colors().get().execute() ;
            
            for (Map.Entry<String, ColorDefinition> color 
                    : colors.getCalendar().entrySet()) {
                String currCalColorID = color.getKey() ;
                if(currCalColorID.equals(calendarColorID)) {
                    calendarColor 
                            = convertHexStringToColor(color.getValue().getBackground()) ;
                    break ;
                }
            }
        } catch (IOException ex) {
           System.out.println("Error obtaining the calendar color :" 
                   + ex);
        }  
        
        return calendarColor ;
    }
    
    /**
     * This method returns a color object from a 
     * hex string such as #cccccc
     * @param colorHexString
     * @return 
     */
    private Color convertHexStringToColor(String colorHexString) {
        Integer colorStringToHex = Integer.parseInt(colorHexString.substring(1), 16) ;
        return new Color(colorStringToHex) ;
    }
    
    /**
     * Get the list of calendar activities (Events) for a 
     * specific date/time range. If either the start or the stop
     * time are null, then all the Events for the calendar are returned
     */
    public ArrayList<ASEntry> getCalendarEvents(String calendar,
            DateTime startTime, DateTime stopTime) {
        
        // create the calendar to store the required events
        ArrayList<Event> calendarEvents = new ArrayList<Event>() ;
        ArrayList<ASEntry> calendarEntries = new ArrayList<ASEntry>() ;
        
        // loop through and obtain the calendars for each of the events
        try {
            Events events = service.events().list(calendar).execute();
            
            // populate the reminders with the default settings
            reminders = events.getDefaultReminders() ;
            
            // loop through obtaining events for calendar            
            if (null != events.getItems()) {
                while(true) {
                    for (Event event : events.getItems()) {
                        
                        // initialize store for current entry
                        ASEntry currEntry = new ASEntry() ;
                        
                        // find out if the event is within the 
                        // required time scale
                        if (startTime == null && stopTime == null) {
                            // calendarEvents.add(event);
                            // get the title
                            String entryTitle = event.getSummary() ;
                            currEntry.setTitle(entryTitle);
                            
                            // get the start time
                            java.util.Calendar eventStartTime 
                                    = (event.getStart().getDateTime() == null) ?
                                    DatatypeConverter
                                        .parseDate(event.getStart().getDate()) : 
                                    DatatypeConverter
                                        .parseDateTime(event.getStart().getDateTime().toString());
                                    
                            currEntry.setStartTime(eventStartTime);
                            
                            // get the stop time
                            java.util.Calendar eventStopTime 
                                    = (event.getEnd().getDateTime()==null)?
                                    DatatypeConverter
                                        .parseDate(event.getEnd().getDate()) :
                                    DatatypeConverter
                                        .parseDateTime(event.getEnd().getDateTime().toString()) ;
                            
                            currEntry.setStopTime(eventStopTime);
                            
                            // get the reminders for the event if they are unique
                            if (!event.getReminders().getUseDefault()) {
                                reminders = event.getReminders().getOverrides();
                            }
                            
                            // populate the reminders list
                            ArrayList<ASReminder> remindersList = new ArrayList<ASReminder>() ;
                            if (null != reminders) {
                                for (EventReminder reminder : reminders) {
                                    ASReminder currReminder = new ASReminder(reminder.getMinutes());
                                    remindersList.add(currReminder);
                                }
                            } 
                            
                            currEntry.setReminders(remindersList);
                            
                            // add the current entry to the main list of entries
                            calendarEntries.add(currEntry);
                            
                            // calendarEntries.add(null);
                        } else if (startTime != null && stopTime == null) {
                            // add if an event occurs after a specific time
                            if((event.getStart().getDateTime()
                                    .toStringRfc3339()).compareTo(startTime.toStringRfc3339()) > 0 ) {
                                calendarEvents.add(event);
                            }
                        } else if (stopTime != null && startTime == null) {
                            // add if an event happens before a certain stop time
                            if((event.getEnd().getDateTime()
                                    .toStringRfc3339()).compareTo(stopTime.toStringRfc3339()) < 0 ) {
                                calendarEvents.add(event);
                            }
                        } else if (stopTime != null && startTime != null) {
                            // add if an event happens within a specific time range
                             if(((event.getStart().getDateTime()
                                    .toStringRfc3339()).compareTo(startTime.toStringRfc3339()) > 0) &&
                                     ((event.getEnd().getDateTime().toStringRfc3339())
                                        .compareTo(stopTime.toStringRfc3339()) < 0 )) {
                                 calendarEvents.add(event);
                            }
                        }
                    }
                    String pageToken = events.getNextPageToken();
                    if (pageToken != null && !pageToken.isEmpty()) {
                        events = service.events().list("primary").setPageToken(pageToken).execute();
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Error accessing events for calendar : " + ex);
        }
                
        // return calendarEvents ;
        return calendarEntries ;
    }
}
