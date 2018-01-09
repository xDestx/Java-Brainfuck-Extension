package com.xdest.bf;
import com.xdest.bf.BFScript.MemoryChunk;

public class Main {

	public static void main(String[] args) {
		BFScript bfs = new BFScript(128, null);
		MemoryChunk mc = bfs.allocateMemory("myFirstVar", 12);
		MemoryChunk mc2 = bfs.allocateMemory("mySecondVar", 12);
		MemoryChunk mc3 = bfs.allocateMemory("string1", 5);
		MemoryChunk mc4 = bfs.allocateMemory("string2", 5);
		mc.store("Hello World!\n".toCharArray());
		mc3.store("var1 ".toCharArray());
		mc4.store("var2 ".toCharArray());
		mc2.copyFrom(mc);
		bfs.print(mc3);
		bfs.print(mc);
		bfs.print(mc4);
		bfs.print(mc2);
		bfs.end();
		System.out.println(bfs.getBF());


		bfs.printDebug();
	}
}
