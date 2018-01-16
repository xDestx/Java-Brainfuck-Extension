package com.xdest.bf;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

public class BFScript {
	/*
	 * BF Stuff
	 */
	private Stack<BFCommand> commandStack;
	private int pointerPosition,falsePointer;
	private int[] memory;
	private InputStream is;
	private Stack<Integer> loopPositions;
	
	
	/*
	 * BF Watcher Stuff
	 */
	private Map<String,MemoryChunk> memoryUsed;
	private boolean silentMode;
	
	/**
	 * Create a BFScript
	 * @param size Memory size in bytes
	 */
	public BFScript(int size, InputStream is) {
		commandStack = new Stack<BFCommand>();
		pointerPosition = 0;
		memory = new int[size];
		memoryUsed = new HashMap<String,MemoryChunk>();
		this.is = is;
		this.loopPositions = new Stack<Integer>();//Integer representing locations in the command stack...
		silentMode = false;
	}
	
	/*
	 * CORE METHODS
	 */
	
	protected void movePointerUp() {
		if(!silentMode) {
			pointerPosition++;
		} else {
			falsePointer++;
		}
		commandStack.push(BFCommand.SHIFT_UP);
	}
	
	protected void movePointerDown() {
		if(!silentMode) {
			pointerPosition--;	
		} else {
			falsePointer--;
		}
		commandStack.push(BFCommand.SHIFT_DOWN);
	}
	
	protected void incrementValue() {
		if(!silentMode) {
			memory[pointerPosition]++;
		}
		commandStack.push(BFCommand.INCREMENT);
	}
	
	protected void decrementValue() {
		if(!silentMode) {
			memory[pointerPosition]--;
		}
		commandStack.push(BFCommand.DECREMENT);
	}
	
	protected void printValueAtPosition() {
		if(!silentMode) {
			System.out.print((char)memory[pointerPosition]);
		}
		commandStack.push(BFCommand.PRINT);
	}
	
	protected void inputAtPosition() {
		if(!silentMode) {
			readOne();
		}
		commandStack.push(BFCommand.INPUT);
	}
	
	protected void readOne() {
		try {
			int i = is.read();
			memory[pointerPosition] = i;
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	/**
	 * Always check loop when using this
	 */
	protected void loopStart() {
		if(!silentMode) {
			loopPositions.push(commandStack.size());
		}
		commandStack.push(BFCommand.LOOP_START);
	}
	
	
	protected void loopEnd() {
		commandStack.push(BFCommand.LOOP_END);
		if(checkLoop()) {
			loopReturn();
		} else {
			loopPositions.pop();
		}
	}
	
	protected void loopReturn() {
		//System.out.println("a" + memory[pointerPosition]);
		int loopStartPos = loopPositions.peek();
		simulate(loopStartPos);
		//System.out.println("b" + memory[pointerPosition]);
		//loopPositions.pop();
	}
	
	/**
	 * Check if loop should execute based on current pointer position
	 * @return true, if the loop will execute
	 */
	protected boolean checkLoop() {
		if(silentMode) {
			return memory[falsePointer] != 0;
		}
		if(memory[pointerPosition] == 0) {
			//Continue
			return false;
		} else {
			return true;
		}
	}
	
	public void printDebug() {
		//Print memory
		//Print pointer
		System.out.println("=== MEMORY ===");
		for(int i = 0; i < memory.length; i++) {
			if(i == pointerPosition) System.out.print('*');
			System.out.print((char)memory[i]);
			if(i == pointerPosition) System.out.print('*');
			System.out.print(' ');
			if(i%5 == 0) {
				System.out.print('\n');
			}
		}
		System.out.println("=== MEMORY ===");
		System.out.println("Pointer: " + pointerPosition + "\n\n");
	}
	
	protected void simulate(int startPos) {
		Iterator<BFCommand> cmds = commandStack.listIterator(startPos);
		while(cmds.hasNext()) {
			BFCommand c = cmds.next();
			//printDebug();
			switch (c) { 
			case DECREMENT:
				memory[pointerPosition]--;
				break;
			case INCREMENT:
				memory[pointerPosition]++;
				break;
			case INPUT:
				readOne();
				break;
			case LOOP_END:
				if(checkLoop()) {
					loopReturn();
				} else {
					loopPositions.pop();
				}
				break;
			case LOOP_START:
				if(checkLoop()) loopPositions.push(startPos);
				break;
			case PRINT:
				System.out.print((char)memory[pointerPosition]);
				break;
			case SHIFT_DOWN:
				pointerPosition--;
				break;
			case SHIFT_UP:
				pointerPosition++;
				break;
			default:
				break;
			}
			startPos++;
		}
	}
	
	public void end() {
		if(is!=null)
			try {
				this.is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		Set<String> keys = new HashSet<String>();
		for(String s : memoryUsed.keySet()) {
			keys.add(s);
		}
		
		for(String s : keys) {
			deallocateMemory(s);
		}
		moveToLocation(0);
	}
	
	public void setSilentMode(boolean b) {
		this.silentMode = b;
		if(b) {
			falsePointer = pointerPosition;
		}
	}
	
	public void toggleSilent() {
		setSilentMode(!this.silentMode);
	}
	
	/*
	 * END CORE METHODS
	 */
	
	/**
	 * Allocate Memory in BFVM?
	 * @param size Memory Allocation Size
	 * @return Memory "Address", -1 if failed
	 */
	public MemoryChunk allocateMemory(String name, int size) {
		boolean posFound = false;
		int pos = 0;
		while(!posFound) {
			posFound = true;
			for(int i = 0; i < size; i++) {
				for(MemoryChunk c : memoryUsed.values()) {
					if(c.containsPosition(i+pos)) {
						posFound = false;
						pos = c.getSize() + c.getAddress()+1;
						break;
					}
				}
			}
			if(pos >= memory.length-size) {
				return null;
			}
		}
		MemoryChunk newChunk = new MemoryChunk(pos,size);
		memoryUsed.put(name, newChunk);
		return newChunk;
		
	}
	
	/**
	 * Slower version of deallocate memory
	 * @param c
	 */
	public void deallocateMemory(MemoryChunk c) {
		if(c!=null && memoryUsed.containsValue(c) && c.getAddress() != -1) {
			c.free();
			for(String s : memoryUsed.keySet()) {
				if(memoryUsed.get(s) == c) {
					memoryUsed.remove(s);
					break;
				}
			}
		}
	}
	
	public void deallocateMemory(String s) {
		if(memoryUsed.containsKey(s)) {
			MemoryChunk mc = memoryUsed.get(s);
			mc.free();
			memoryUsed.remove(s);
		}
	}
	
	/**
	 * Write int at pointer position
	 * @param n the int to write
	 */
	protected void writeInt(int n) {
		int direction = n < memory[pointerPosition] ? -1:1;
		while(n != memory[pointerPosition]) {
			if(direction > 0) {
				this.incrementValue();
			} else if(direction < 0) {
				this.decrementValue();
			}
		}
	}
	
	protected void writeToLocation(int location, int data) {
		moveToLocation(location);
		this.writeInt(data);
	}
	
	
	protected void moveToLocation(int location) {
		int direction = pointerPosition > location ? -1:1;
		
		if(silentMode) {
			if(location == falsePointer || location < 0 || location >= memory.length) return;
			direction = falsePointer > location ? -1:1;
			while(falsePointer != location) {
				if(direction > 0) {
					movePointerUp();
				} else {
					movePointerDown();
				}
			}
			return;
		}
		
		if(location == pointerPosition || location < 0 || location >= memory.length) return;
		while(pointerPosition != location) {
			if(direction > 0) {
				movePointerUp();
			} else {
				movePointerDown();
			}
		}
	}

	public void print(MemoryChunk c) {
		this.moveToLocation(c.getAddress());
		for(int i = 0; i < c.getSize(); i++) {
			this.printValueAtPosition();
			this.movePointerUp();
		}
	}
	
	public void print(MemoryChunk...c) {
		for(MemoryChunk chunk : c) {
			print(chunk);
		}
	}
	
	public void ifTrueDoThis(int ifLocation, BFCommand[] cmds) {
		moveToLocation(ifLocation);
		if(!checkLoop()) {
			this.setSilentMode(true);
		}
		loopStart();
		for(BFCommand c : cmds) {
			switch(c) {
			case DECREMENT:
				decrementValue();
				break;
			case INCREMENT:
				incrementValue();
				break;
			case INPUT:
				inputAtPosition();
				break;
			case LOOP_END:
				loopEnd();
				break;
			case LOOP_START:
				loopStart();
				break;
			case PRINT:
				printValueAtPosition();
				break;
			case SHIFT_DOWN:
				movePointerDown();
				break;
			case SHIFT_UP:
				movePointerUp();
				break;
			}
		}
		loopEnd();
	}
	
	
	/**
	 * Returns chunk with value
	 * @param a Chunk a
	 * @param b Chunk b
	 * @return Chunk of size 1 with value
	 */
	public MemoryChunk getDifference(MemoryChunk a, MemoryChunk b) {
		MemoryChunk response = allocateMemory(UUID.randomUUID().toString(), 1);
		moveToLocation(a.getAddress());
		
		
		
		if(!checkLoop()) {
			silentMode = true;
		}
		loopStart();
		decrementValue();
		moveToLocation(b.getAddress());
		
		
		
		return response;
	}
	
	/**
	 * IF a > B do cmds
	 * @param aLocation a value (using 0 value)
	 * @param bLocation b value (using 0 value);
	 * @param cmds commands to or to not execute
	 */
	public void ifAGTBdoThis(MemoryChunk aLocation, MemoryChunk bLocation, FutureCommands cmds) {
		MemoryChunk valA = allocateMemory(UUID.randomUUID().toString(),1);
		MemoryChunk valB = allocateMemory(UUID.randomUUID().toString(),1);
		MemoryChunk difference = allocateMemory(UUID.randomUUID().toString(),1);
		MemoryChunk loopExitor = allocateMemory(UUID.randomUUID().toString(),1);
		valA.copyFrom(aLocation);
		valB.copyFrom(bLocation);
		printDebug();
		/*
		 * A (val a)

			B (val b)
			
			C (Difference)
			
			D 0 (Loop exit)
			
			Start at B
			
			IF B > 0 Loop start [
			MOVE TO A
			IF A > 0 Loop start [
			MOVE TO C >>
			INCREMENT C +
			MOVE TO A <<
			DECREMENT A -
			MOVE TO D (EXIT LOOP)
			]
			MOVE TO B >>
			DECREMENT B -
			LOOP END ]
			
			IF C > 0, EXECUTE THE c o d e s
			MOVE TO C
			LOOP START [
			STUFF
			LOOP START [
			DECREMENT c TO 0
			LOOP END ]
			LOOP END ]
			
			RELEASE C
			RELEASE D
			RELEASE A
			RELEASE B
		 */
		
		//Difference Calculator
		moveToLocation(valB.getAddress());
		if(!checkLoop()) {
			this.setSilentMode(true);
		}
		loopStart();
		moveToLocation(valA.getAddress());
		loopStart();
		moveToLocation(difference.getAddress());
		incrementValue();
		moveToLocation(valA.getAddress());
		decrementValue();
		moveToLocation(loopExitor.getAddress());
		loopEnd();
		moveToLocation(valB.getAddress());
		decrementValue();
		if(this.silentMode) {
			this.silentMode = false;
		}
		loopEnd();
		moveToLocation(difference.getAddress());
		if(!checkLoop()) {
			this.setSilentMode(true);
		}
		loopStart();
		execute(cmds);
		moveToLocation(loopExitor.getAddress());
		if(silentMode) {
			silentMode = false;
		}
		loopEnd();
		deallocateMemory(loopExitor);
		deallocateMemory(difference);
		deallocateMemory(valA);
		deallocateMemory(valB);
	}
	
	protected void execute(FutureCommands cmds) {
		for(BFFutureCommand c : cmds.getActions()) {
			switch (c) {
			case COPY_CHUNK_A_TO_B:
				c.getDestChunk().copyFrom(c.getChunk());
				break;
			case DECREMENT:
				this.decrementValue();
				break;
			case INCREMENT:
				this.incrementValue();
				break;
			case INPUT:
				this.inputAtPosition();
				break;
			case LOOP_END:
				this.loopEnd();
				break;
			case LOOP_START:
				this.loopStart();
				break;
			case MOVE_TO_CHUNK:
				this.moveToLocation(c.getChunkAddress());
				break;
			case PRINT:
				this.printValueAtPosition();
				break;
			case PRINT_CHUNK:
				this.print(c.getChunk());
				break;
			case SHIFT_DOWN:
				this.movePointerDown();
				break;
			case SHIFT_UP:
				this.movePointerUp();
				break;
			}
		}
	}
	
	
	public String getBF() {
		String s = "";
		Iterator<BFCommand> bfi = commandStack.iterator();
		while(bfi.hasNext()) {
			s+=bfi.next();
		}
		return s;
	}
	
	
	/**
	 * Memory Chunks cannot be split.
	 * @author xDest
	 *
	 */
	public class MemoryChunk {
		
		private int size,startPos;
		
		protected MemoryChunk(int startingPosition, int size) {
			this.size = size;
			this.startPos = startingPosition;
		}
		
		public int getAddress() {
			return this.startPos;
		}
		
		public int getSize() {
			return this.size;
		}
		
		/**
		 * Does not store past it's size
		 * @param data Data to store in memory
		 */
		public void store(int[] data) {
			int loopLength;
			if(data.length > size) {
				loopLength = size;
			} else {
				loopLength = data.length;
			}
			for(int i = 0; i < loopLength; i++) {
				writeToLocation(startPos+i,data[i]);
			}
		}
		
		/**
		 * Does not store past it's size
		 * @param data Data to store in memory
		 */
		public void store(char[] data) {
			int loopLength;
			if(data.length > size) {
				loopLength = size;
			} else {
				loopLength = data.length;
			}
			for(int i = 0; i < loopLength; i++) {
				writeToLocation(startPos+i,data[i]);
			}
		}
		
		public int[] read() {
			int[] data = new int[size];
			for(int i = 0; i < size; i++) {
				data[i] = memory[i+startPos];
			}
			return data;
		}
		
		public boolean containsPosition(int i) {
			if(i >= startPos && i <= startPos+size) {
				return true;
			}
			return false;
		}
		
		public void copyFrom(MemoryChunk mc) {
			int size = mc.getSize();
			if(size > getSize()) {
				size = getSize();
			}
			MemoryChunk saveVal = allocateMemory(UUID.randomUUID().toString(), size);
			moveToLocation(mc.getAddress());
			//Move to MC
			//Start transfer loop
			//Transfer to this ANd to saveVal
			//end transfer loop
			//Start transfer from saveVal back to MC
			for(int i = 0; i < size; i++) {
				moveToLocation(mc.getAddress()+i);
				boolean silent = false;
				if(!checkLoop()) {
					silent = true;
					setSilentMode(true);
				}
				loopStart();
					decrementValue();
					moveToLocation(this.getAddress()+i);
					incrementValue();
					moveToLocation(saveVal.getAddress()+i);
					incrementValue();
					moveToLocation(mc.getAddress()+i);
					if(silent) {
						setSilentMode(false);
					}
				loopEnd();
				moveToLocation(saveVal.getAddress()+i);
				silent = false;
				if(!checkLoop()) {
					silent = true;
					setSilentMode(true);
				}
				loopStart();
					decrementValue();
					moveToLocation(mc.getAddress()+i);
					incrementValue();
					moveToLocation(saveVal.getAddress()+i);
					if(silent) {
						setSilentMode(false);
					}
				loopEnd();
			}
			deallocateMemory(saveVal);
		}
		
		/**
		 * Allow input for the specified count. -1 for full memory value, anything over this chunk's size will not be enacted
		 * @param count
		 */
		public void input(int count) {
			count = count == -1 || count > size?size:count;
			for(int i = 0; i < count; i++) {
				moveToLocation(this.getAddress()+i);
				inputAtPosition();
			}
		}
		
		/**
		 * Clears memory used and becomes unusable
		 */
		protected void free() {
			for(int i = 0; i < size; i++) {
				moveToLocation(startPos + i);
				boolean silent = false;
				if(!checkLoop()) {
					silent = true;
					setSilentMode(true);
				}
				loopStart();
				decrementValue();
				if(silent) {
					setSilentMode(false);
				}
				loopEnd();
			}
			startPos = -1;
			size = -1;
		}
		
	}
}
