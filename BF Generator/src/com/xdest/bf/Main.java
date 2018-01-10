package com.xdest.bf;
import com.xdest.bf.BFScript.MemoryChunk;

public class Main {

	public static void main(String[] args) {
		BFScript bfs = new BFScript(128, null);
		printN();
		MemoryChunk mc = bfs.allocateMemory("myFirstVar", 12);
		printN();
		MemoryChunk mc2 = bfs.allocateMemory("mySecondVar", 12);
		printN();
		MemoryChunk mc3 = bfs.allocateMemory("string1", 5);
		printN();
		MemoryChunk mc4 = bfs.allocateMemory("string2", 5);
		printN();
		mc.store("Hello World!\n".toCharArray());
		printN();
		mc3.store("var1 ".toCharArray());
		printN();
		mc4.store("var2 ".toCharArray());
		printN();
		try {
			mc2.copyFrom(mc);
			printN();
		} catch (StackOverflowError e) {
			e.printStackTrace();
		}
		printN();
		bfs.print(mc3);
		printN();
		bfs.print(mc);
		printN();
		bfs.print(mc4);
		printN();
		bfs.print(mc2);
		printN();
		bfs.end();
		printN();
		bfs.moveToLocation(34);
		System.out.println(bfs.getBF());


		bfs.printDebug();
	}
	
	private static int n = 0;
	public static void printN() {
		//System.out.println(n++);
	}
}
