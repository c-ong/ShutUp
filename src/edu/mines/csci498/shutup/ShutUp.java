package edu.mines.csci498.shutup;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;


public final class ShutUp extends ListActivity {
	
	private CalendarReader reader;
	private List<CalendarEvent> events;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shut_up);
		
		reader = new CalendarReader();
		events = reader.readCalendar(this);
	}
}

