/**
 * Defines different levels of ring volume
 * @author Lauren Aberle
 * @author Thomas Brown
 */

package edu.mines.csci498.shutup;

public enum RingVolume {
	
	NOT_SELECTED(1),
	SILENT(2),
	VIBRATE(3),
	LOUD(4);
	
	private int id;
	
	private RingVolume(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id; 
	}
}
