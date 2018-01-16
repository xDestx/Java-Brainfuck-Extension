package com.xdest.bf;

import com.xdest.bf.BFScript.MemoryChunk;

public class Main {

	public static void main(String[] args) throws Exception {
		BFScript bfs = new BFScript(128, System.in);
		
		MemoryChunk helloText = bfs.allocateMemory("helloVar", 11);
		helloText.store("HELLO WORLD".toCharArray());
		
		bfs.print(helloText);
		
		
		System.out.println("\n"+bfs.getBF());
		bfs.end();

		bfs.printDebug();
	}
}
