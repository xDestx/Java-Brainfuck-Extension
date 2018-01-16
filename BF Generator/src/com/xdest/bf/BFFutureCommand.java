package com.xdest.bf;

import com.xdest.bf.BFScript.MemoryChunk;

public enum BFFutureCommand {
	INCREMENT, DECREMENT, SHIFT_UP, SHIFT_DOWN, INPUT, PRINT, LOOP_START, LOOP_END, MOVE_TO_CHUNK, PRINT_CHUNK, COPY_CHUNK_A_TO_B;
	
	private MemoryChunk chunk, destChunk;
	
	BFFutureCommand() {
		chunk = destChunk = null;
	}
	
	public int getChunkSize() {
		if(chunk == null) return 0;
		return this.chunk.getSize();
	}
	
	public int getDestChunkSize() {
		if(destChunk == null) {
			return 0;
		}
		return this.destChunk.getSize();
	}
	
	public int getChunkAddress() {
		if(chunk == null) return -1;
		return this.chunk.getAddress();
	}
	
	public int getDestChunkAddress() {
		if(destChunk == null) return -1;
		return this.destChunk.getAddress();
	}
	
	public BFFutureCommand setChunk(MemoryChunk c) {
		this.chunk = c;
		return this;
	}
	
	public BFFutureCommand setDestChunk(MemoryChunk c) {
		this.destChunk = c;
		return this;
	}
	
	public MemoryChunk getChunk() {
		return this.chunk;
	}
	
	public MemoryChunk getDestChunk() {
		return this.destChunk;
	}
	
}
