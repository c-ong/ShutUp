/**
 * This class holds all necessary data fields for each calendar event.
 * @author Lauren Aberle
 * @author Thomas Brown
 */

package edu.mines.csci498.shutup;

import java.util.Calendar;

public class CalendarEvent {
	
	private String title;
	private Calendar start;
	private Calendar end;
	private boolean allDay;
	
	public CalendarEvent(String title, Calendar start, Calendar end, boolean allDay) {
		super();
		this.title = title;
		this.start = start;
		this.end = end;
		this.allDay = allDay;
	}
	
	@Override
	public String toString() {
		return "CalendarEvent [title=" + title + ", start=" + start + ", end="
				+ end + ", allDay=" + allDay + "]";
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Calendar getStart() {
		return start;
	}
	public void setStart(Calendar start) {
		this.start = start;
	}
	public Calendar getEnd() {
		return end;
	}
	public void setEnd(Calendar end) {
		this.end = end;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

}
