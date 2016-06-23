package com.codefarm.tests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgorithmsTest {
	
	BigInteger tenCharPrime;
	BigInteger eightCharPrime;
	BigInteger tenCharXorOperand;
	BigInteger eightCharXorOperand;
	BigInteger tenCharMaxPerms;
	BigInteger eightCharMaxPerms;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		/*
		 * Most tests use some BigInteger instances in there logic. These are defined here.
		 */
		
		// Primes checked using www.numberempire.com/primenumbers.php
		// for a 10 character code with 29 possible characters (alph + numbers - non-readables)
		// max permutations = 420707233300201
		// closest prime less than this that satisfies p = 3 mod 4 = 420707233300183
		tenCharMaxPerms = new BigInteger("420707233300201");
		tenCharPrime = new BigInteger("420707233300183");
		tenCharXorOperand = new BigInteger(tenCharMaxPerms.bitLength(),0,new Random());
		// for a 8 character code with 29 possible characters (alph + numbers - non-readables)
		// max permutations = 500246412961
		// closest prime less than this that satisfies p = 3 mod 4 = 500246412959
		eightCharMaxPerms = new BigInteger("500246412961");
		eightCharPrime = new BigInteger("500246412959");
		eightCharXorOperand = new BigInteger(eightCharMaxPerms.bitLength(),0,new Random());
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that the QRPFunction products are unique (as best as possible)
	 * Edge cases taken account of include:<p>
	 * - Start of the sequence<p>
	 * - Middle of the sequence, where 2x >= p and beyond<p>
	 * - End of the sequence, where x approaches (p - 1)
	 * <p>
	 * p is initialised as a large prime pertinent to a commonly encountered code size and character set.
	 * The value of x is changed and iterated through for each stage of the test.
	 * The results of the algorithm are stored in a set as they are generated.
	 * The boolean return value of the set add() method provides a method to check the uniqueness of the value
	 * being added to the set, as it is added. In this way, the full uniqueness of the set can be guarenteed.
	 * <p>
	 * The test is limited to sampling from the code space due to memory usage.
	 * <p>
	 * TODO Is there a better way to test a larger sample for uniqueness??
	 */
	@Test
	public void QRPFunctionTest() {
		
		Set<BigInteger> testSet = new HashSet<BigInteger>();
		HashMap<BigInteger, String> dupSet = new HashMap<BigInteger, String>();
		boolean result = true;
		
		BigInteger x = BigInteger.ONE;
		BigInteger p = tenCharPrime;
		
		// setSize derives all the other parameters.
		// The total size of the testSet will be 3 * setSize
		BigInteger setSize = new BigInteger("4000000");
		BigInteger halfSetSize = setSize.divide(BigInteger.valueOf(2L));
		
		BigInteger setOneLimit = setSize;
		BigInteger setTwoStart = p.divide(BigInteger.valueOf(2L)).subtract(halfSetSize);
		BigInteger setTwoLimit = setTwoStart.add(halfSetSize);
		BigInteger setThreeStart = p.subtract(setSize).subtract(BigInteger.ONE);
		BigInteger setThreeLimit = p.subtract(BigInteger.ONE);
		
		boolean isAdded = true;
		
		System.out.println("Set one started");
		// Set one
		for (; x.compareTo(setOneLimit) <= 0; x = x.add(BigInteger.ONE)) {
			isAdded = testSet.add(com.codefarm.engine.Algorithms.QRPFunction(p, x));
			if (!isAdded) {
				System.out.println("Duplicate found!! (Oh no!)");
				result = false;
				dupSet.put(com.codefarm.engine.Algorithms.QRPFunction(p, x), "Duplicate from set 1, x = " + x.toString());
			}
		}
		// Set two
		System.out.println("Set two started");
		x = setTwoStart;
		for (; x.compareTo(setTwoLimit) <= 0; x = x.add(BigInteger.ONE)) {
			isAdded = testSet.add(com.codefarm.engine.Algorithms.QRPFunction(p, x));
			if (!isAdded) {
				System.out.println("Duplicate found!! (Oh no!)");
				result = false;
				dupSet.put(com.codefarm.engine.Algorithms.QRPFunction(p, x), "Duplicate from set 2, x = " + x.toString());
			}
		}
		
		// Set three
		System.out.println("Set three started");
		x = setThreeStart;
		for (; x.compareTo(setThreeLimit) <= 0; x = x.add(BigInteger.ONE)) {
			isAdded = testSet.add(com.codefarm.engine.Algorithms.QRPFunction(p, x));
			if (!isAdded) {
				System.out.println("Duplicate found!! (Oh no!)");
				result = false;
				dupSet.put(com.codefarm.engine.Algorithms.QRPFunction(p, x), "Duplicate from set 3, x = " + x.toString());
			}
		}
		assertEquals(true, result);
		if (result) {
			System.out.println("No Duplicates found");
			System.out.println("Set size = " + testSet.size());
		}
		
		// write any duplicates to a file
		if (!dupSet.isEmpty()) {
			BufferedWriter out = null;
			try {
				File f = new File("duplicates.txt");
				if (f.exists()) {
					f.delete();
				}
				FileWriter fstream = new FileWriter(f, true); //true tells to append data.
				out = new BufferedWriter(fstream);
				out.write("Duplicates found in the set:\n\n");
				for (Map.Entry<BigInteger, String> dup : dupSet.entrySet()) {
						out.write("Duplicate: " + dup.getKey().toString() + ", " + dup.getValue());
					}
			} catch (IOException e) { 
				System.out.println("File error: " + e.getMessage()); 
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						System.out.println("File error: " + e.getMessage()); 
					} 
				}
			}
		}
	}
}
