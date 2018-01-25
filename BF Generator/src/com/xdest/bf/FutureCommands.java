package com.xdest.bf;

import java.util.Stack;

import com.xdest.bf.BFScript.MemoryChunk;

/**
 * Class to wrap Future Commands together
 * @author xDest
 *
 */
public class FutureCommands {

	private Stack<BFFutureCommand> actions;
	
	/**
	 * Initialize a new stack
	 */
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
	
	/**
	 * Move to a provoided chunk
	 * @param a The chunk to move to
	 */
	public void moveToChunk(MemoryChunk a) {
		actions.push(BFFutureCommand.MOVE_TO_CHUNK.setChunk(a));
	}
	
	/**
	 * Copy chunk A to chunk b
	 * @param a Chunk a
	 * @param b Chunk b
	 */
	public void copyChunkAToB(MemoryChunk a, MemoryChunk b) {
		actions.push(BFFutureCommand.COPY_CHUNK_A_TO_B.setChunk(a).setDestChunk(b));
	}
	
	/**
	 * Print out the given chunk
	 * @param a The chunk
	 */
	public void printChunkA(MemoryChunk a) {
		actions.push(BFFutureCommand.PRINT_CHUNK.setChunk(a));
	}
	
	/**
	 * Get all actions as a stack
	 * @return The held commands
	 */
	public Stack<BFFutureCommand> getActions() {
		return this.actions;
	}
}
