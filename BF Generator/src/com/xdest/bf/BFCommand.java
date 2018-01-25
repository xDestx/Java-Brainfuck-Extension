package com.xdest.bf;

/**
 * Enum for the different BF Commands
 * @author xDest
 *
 */
public enum BFCommand {
	/**
	 * Shift the pointer up one position
	 */
	SHIFT_UP('>'),
	/**
	 * Shift the pointer down one position
	 */
	SHIFT_DOWN('<'),
	/**
	 * Increment the value at the pointer by one
	 */
	INCREMENT('+'),
	/**
	 * Decrement the value at the pointer by one
	 */
	DECREMENT('-'),
	/**
	 * Print the char value of the pointers position in memory
	 */
	PRINT('.'),
	/**
	 * Accept one byte of input
	 */
	INPUT(','),
	/**
	 * Start a loop. If the pointer is pointing to a location with value 0, it will skip until a ']' is discovered.
	 */
	LOOP_START('['),
	/**
	 * End a loop. If the pointer is pointing to a location with value 0, it will end the loop. Otherwise, the program jumps back to the last '['.
	 */
	LOOP_END(']');

	private final char c;
	
	/**
	 * Assign char value
	 * @param c The char for this command
	 */
	BFCommand(char c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return ""+c;
	}
}
