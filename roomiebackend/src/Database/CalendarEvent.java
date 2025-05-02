package Database;

import java.time.LocalDate;

public class CalendarEvent {
    private final int       calendarId;
    private final int       groupChatId;
    private final LocalDate eventDate;
    private final String    events;      // e.g. "laundry|42,cleaning|17"

    public CalendarEvent(int calendarId, int groupChatId, LocalDate eventDate, String events) {
        this.calendarId  = calendarId;
        this.groupChatId = groupChatId;
        this.eventDate   = eventDate;
        this.events      = events;
    }

    public int getCalendarId()  { return calendarId; }
    public int getGroupChatId() { return groupChatId; }
    public LocalDate getEventDate() { return eventDate; }
    public String getEvents()   { return events; }
}
