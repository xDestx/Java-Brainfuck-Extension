package com.xdest.bf;

import java.util.Stack;

import com.xdest.bf.BFScript.MemoryChunk;

public class FutureCommands {

	private Stack<BFFutureCommand> actions;
	
	public FutureCommands() {
		actions = new Stack<BFFutureCommand>();
	}
	
	protected void movePointerUp() {
		actions.push(BFFutureCommand.SHIFT_UP);
	}
	
	protected void movePointerDown() {
		actions.push(BFFutureCommand.SHIFT_DOWN);
	}
	
	protected void incrementValue() {
		actions.push(BFFutureCommand.INCREMENT);
	}
	
	protected void decrementValue() {
		actions.push(BFFutureCommand.DECREMENT);
	}
	
	protected void printValueAtPosition() {
		actions.push(BFFutureCommand.PRINT);
	}
	
	protected void inputAtPosition() {
		actions.push(BFFutureCommand.INPUT);
	}
	
	/**
	 * Always check loop when using this
	 */
	protected void loopStart() {
		actions.push(BFFutureCommand.LOOP_START);
	}
	
	protected void loopEnd() {
		actions.push(BFFutureCommand.LOOP_END);
	}
	
	public void moveToChunk(MemoryChunk a) {
		actions.push(BFFutureCommand.MOVE_TO_CHUNK.setChunk(a));
	}
	
	public void copyChunkAToB(MemoryChunk a, MemoryChunk b) {
		actions.push(BFFutureCommand.COPY_CHUNK_A_TO_B.setChunk(a).setDestChunk(b));
	}
	
	public void printChunkA(MemoryChunk a) {
		actions.push(BFFutureCommand.PRINT_CHUNK.setChunk(a));
	}
	
	public Stack<BFFutureCommand> getActions() {
		return this.actions;
	}
}
