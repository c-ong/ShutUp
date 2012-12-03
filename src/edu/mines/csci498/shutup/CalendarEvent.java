/**
 * This class holds all necessary data fields for each calendar event.
 * @author Lauren Aberle
 * @author Thomas Brown
 */

package edu.mines.csci498.shutup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarEvent {
	
	private static Calendar calendar;
	
	private String title;
	private long startTime;
	private long endTime;
	
	public CalendarEvent(String title, long start, long end) {
		super();
		this.title = title;
		this.startTime = start;
		this.endTime = end;
		calendar = GregorianCalendar.getInstance();
	}
	
	@Override
	public String toString() {
		return "CalendarEvent [title=" + title + ", start=" + getStartString() + ", end="
				+ getEndString() + "]";
	}
	
	public String getStartString() {
		SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm aaa");
		calendar.setTimeInMillis(startTime);
		return format.format(calendar.getTime());
	}
	
	public String getEndString() {
		SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm aaa");
		calendar.setTimeInMillis(endTime);
		return format.format(calendar.getTime());
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
