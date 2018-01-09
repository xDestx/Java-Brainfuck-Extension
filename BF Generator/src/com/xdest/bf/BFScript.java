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
	private int pointerPosition;
	private int[] memory;
	private InputStream is;
	private Stack<Integer> loopPositions;
	
	
	/*
	 * BF Watcher Stuff
	 */
	private Map<String,MemoryChunk> memoryUsed;
	
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
	}
	
	/*
	 * CORE METHODS
	 */
	
	protected void movePointerUp() {
		commandStack.push(BFCommand.SHIFT_UP);
		pointerPosition++;
	}
	
	protected void movePointerDown() {
		commandStack.push(BFCommand.SHIFT_DOWN);
		pointerPosition--;
	}
	
	protected void incrementValue() {
		commandStack.push(BFCommand.INCREMENT);
		memory[pointerPosition]+=1;
	}
	
	protected void decrementValue() {
		commandStack.push(BFCommand.DECREMENT);
		memory[pointerPosition]-=1;
	}
	
	protected void printValueAtPosition() {
		commandStack.push(BFCommand.PRINT);
		System.out.print((char)memory[pointerPosition]);
	}
	
	protected void inputAtPosition() {
		commandStack.push(BFCommand.INPUT);
		readOne();
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
		commandStack.push(BFCommand.LOOP_START);
		loopPositions.push(commandStack.size());
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
	
	protected boolean checkLoop() {
		//TODO: THIS
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
		if(location == pointerPosition || location < 0 || location >= memory.length) return;
		int direction = pointerPosition > location ? -1:1;
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
	
	//TODO: Create a "runnable" type of thing
	public void ifGT(MemoryChunk a, MemoryChunk b) {
		//Not allowed to use a.read() or b.read();
		
		//wtf does this do right now
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
			MemoryChunk saveVal = allocateMemory(UUID.randomUUID().toString(), size);
			moveToLocation(mc.getAddress());
			//Move to MC
			//Start transfer loop
			//Transfer to this ANd to saveVal
			//end transfer loop
			//Start transfer from saveVal back to MC
			for(int i = 0; i < size; i++) {
				moveToLocation(mc.getAddress()+i);
				loopStart();
					decrementValue();
					moveToLocation(this.getAddress()+i);
					incrementValue();
					moveToLocation(saveVal.getAddress()+i);
					incrementValue();
					moveToLocation(mc.getAddress()+i);
				loopEnd();
				moveToLocation(saveVal.getAddress()+i);
				//System.out.println(i + "SAVE: " + saveVal.read()[0] + "   MC " + mc.read()[0] + "\nLOOP?? " + loopPositions.size());
				//try {
			//		Thread.sleep(10000);
			//	} catch (Exception e) {
					
		//		}
				loopStart();
					decrementValue();
					moveToLocation(mc.getAddress()+i);
					incrementValue();
					moveToLocation(saveVal.getAddress()+i);
				loopEnd();
			}
			deallocateMemory(saveVal);
		}
		
		/**
		 * Clears memory used and becomes unusable
		 */
		protected void free() {
			for(int i = 0; i < size; i++) {
				moveToLocation(startPos + i);
				if(read()[i] != 0) {
					loopStart();
					decrementValue();
					loopEnd();
				}
			}
			startPos = -1;
			size = -1;
		}
		
	}
}
