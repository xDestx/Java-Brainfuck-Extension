package com.xdest.bf;

public enum BFCommand {
	SHIFT_UP('>'),SHIFT_DOWN('<'),INCREMENT('+'),DECREMENT('-'),PRINT('.'),INPUT(','),LOOP_START('['),LOOP_END(']');

	private final char c;
	
	BFCommand(char c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return ""+c;
	}
}
