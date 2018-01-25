package com.xdest.bf;

import com.xdest.bf.BFScript.MemoryChunk;

/**
 * Similar to {@link BFCommand}, but includes higher level commands.
 * @author xDest
 *
 */
public enum BFFutureCommand {
	/**
	 * Increment the value at the pointer by one
	 */
	INCREMENT, 
	/**
	 * Decrement the value at the pointer by one
	 */
	DECREMENT, 
	/**
	 * Shift the pointer up one position
	 */
	SHIFT_UP, 
	/**
	 * Shift the pointer down one position
	 */
	SHIFT_DOWN, 
	/**
	 * Accept input saved to the currently pointed at position
	 */
	INPUT, 
	/**
	 * Print out the value at this pointed position
	 */
	PRINT, 
	/**
	 * Start a loop. If the pointed at position is 0, the the program will skip following commands until ']' is reached.
	 */
	LOOP_START,
	/**
	 * End a loop. If the pointed at position is 0, the loop will end. Otherwise, the program will return to the last ']'
	 */
	LOOP_END,
	/**
	 * Move to the starting position of a {@link MemoryChunk}.
	 * @see MemoryChunk
	 */
	MOVE_TO_CHUNK, 
	/**
	 * Print a {@link MemoryChunk} by moving to it's position and printing each value in it.
	 * @see MemoryChunk
	 */
	PRINT_CHUNK,
	/**
	 * Copy one {@link MemoryChunk} to another.
	 * @see MemoryChunk
	 */
	COPY_CHUNK_A_TO_B;
	
	private MemoryChunk chunk, destChunk;
	
	/**
	 * Initialize default values to null
	 */
	BFFutureCommand() {
		chunk = destChunk = null;
	}
	
	/**
	 * Get the size of the chunk used in this command
	 * @see MemoryChunk
	 * @return The size, or 0 if there is no chunk.
	 */
	public int getChunkSize() {
		if(chunk == null) return 0;
		return this.chunk.getSize();
	}
	
	/**
	 * Get the size of the destination chunk used in this command.
	 * @see MemoryChunk
	 * @return The {@link MemoryChunk#getSize()} of the destination chunk, or 0 if it doesn't exist.
	 */
	public int getDestChunkSize() {
		if(destChunk == null) {
			return 0;
		}
		return this.destChunk.getSize();
	}
	
	/**
	 * Get the memory address of the chunk in this command.
	 * @see MemoryChunk
	 * @return Memory address, or -1 if there is no chunk
	 */
	public int getChunkAddress() {
		if(chunk == null) return -1;
		return this.chunk.getAddress();
	}
	
	/**
	 * Get the memory address of the destination chunk in this command.
	 * @see MemoryChunk
	 * @return Memory address, or -1 if there is no chunk
	 */
	public int getDestChunkAddress() {
		if(destChunk == null) return -1;
		return this.destChunk.getAddress();
	}
	
	/**
	 * Set the chunk used in this command. Returns self for method chaining.
	 * @param c Chunk used
	 * @return Self
	 */
	public BFFutureCommand setChunk(MemoryChunk c) {
		this.chunk = c;
		return this;
	}
	
	/**
	 * Set the destination chunk for this command. Returns self for method chaining.
	 * @param c Chunk destination
	 * @return Self
	 */
	public BFFutureCommand setDestChunk(MemoryChunk c) {
		this.destChunk = c;
		return this;
	}
	
	/**
	 * Get the chunk used in this command
	 * @return The chunk
	 */
	public MemoryChunk getChunk() {
		return this.chunk;
	}
	
	/**
	 * Get the destination chunk used in this command
	 * @return The chunk
	 */
	public MemoryChunk getDestChunk() {
		return this.destChunk;
	}
	
}
