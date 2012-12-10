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
	private int eventId;
	private RingVolume ringVolume;
	
	public CalendarEvent(String title, long start, long end, int eventId, RingVolume volume) {
		super();
		this.title = title;
		this.startTime = start;
		this.endTime = end;
		this.eventId = eventId;
		this.ringVolume = volume;
		calendar = GregorianCalendar.getInstance();
	}
	
	/**
	 * Turns start time into human readable date
	 * @return - start time (e.g. Wed, Jan 5, 2012 - 2:30 PM)
	 */
	public String getStartString() {
		SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm aaa");
		calendar.setTimeInMillis(startTime);
		return format.format(calendar.getTime());
	}
	
	/**
	 * Turns end time into human readable date
	 * @return - end time (e.g. Wed, Jan 5, 2012 - 2:30 PM)
	 */
	public String getEndString() {
		SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm aaa");
		calendar.setTimeInMillis(endTime);
		return format.format(calendar.getTime());
	}
	
	@Override
	public String toString() {
		return "CalendarEvent [title=" + title + ", start=" + getStartString() + ", end="
				+ getEndString() + " , id=" + eventId + "]";
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

	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public RingVolume getRingVolume() {
		return ringVolume;
	}
	public void setVolume(RingVolume volume) {
		this.ringVolume = volume;
	}
}
