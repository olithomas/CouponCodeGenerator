package com.codefarm.tests;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.codefarm.engine.*;

@RunWith(Parameterized.class)
public class CodeFormatterTest {

	private char[] _excludes;
	private char[] _expectedFinalExclusions;
	private char[] _expectedFinalReplacements;
	private int _codeLen;
	private int _expectedCharSetLen;
	private CodeFormatter _testInstance;
	
	private BigInteger _numberInput;
	private String _expectedCodeString;
	
	@Parameters
	public static Collection<Object[]> data() {
		// build arrays for max excludes test..
		char[] maxExclusions = new char[36 - CodeFormatter.MIN_CHAR_SET_LENGTH];
		char[] maxFinalExcludes = new char[CodeFormatter.MIN_CHAR_SET_LENGTH];
		char[] maxFinalReplacements = new char[CodeFormatter.MIN_CHAR_SET_LENGTH];
		
		char exclude = '0';
		for (int i = 0; i < maxExclusions.length; i++) {
			maxExclusions[i] = exclude;
			exclude++;
			// skip characters between '9' and 'A'...
			while (exclude > '9' && exclude < 'A') {
				exclude++;
			}
		}
		
		exclude = '0';
		char replacement = 'Z';
		for (int i = 0; i < CodeFormatter.MIN_CHAR_SET_LENGTH; i++) {
			maxFinalExcludes[i] = exclude;
			maxFinalReplacements[CodeFormatter.MIN_CHAR_SET_LENGTH - 1 - i] = replacement;
			exclude++; replacement--;
		}
		
		// set up the data sets...
		Object[] dataSet1 = new Object[] { 
				new char[] {'0','1','5','I','J','L','O','S'}, // excludes
				new char[] {'0','1','5','I','J','L','O'}, // expected final excludes
				new char[] {'T','U','V','W','X','Y','Z'}, // expected final replacements
				10, // code length
				28, // expected character set length
				BigInteger.ONE, // number input
				"TTTTTTTTTU" }; // expected code string
		
		Object[] dataSet2 = new Object[] { 
				new char[] {'0','1','5','I','J','L','O','S'}, // excludes
				new char[] {'0','1','5','I','J','L','O'}, // expected final excludes
				new char[] {'T','U','V','W','X','Y','Z'}, // expected final replacements
				10, // code length
				28, // expected character set length
				BigInteger.ONE, // number input
				"TTTTTTTTTU" }; // expected code string
		
		// max permutations test
		Object[] dataSet3 = new Object[] { 
				new char[] {'0','1','5','I','J','L','O','S'}, // excludes
				new char[] {'0','1','5','I','J','L','O'}, // expected final excludes
				new char[] {'T','U','V','W','X','Y','Z'}, // expected final replacements
				10, // code length
				28, // expected character set length
				BigInteger.valueOf(28).pow(10).subtract(BigInteger.ONE), // number input (max permutations)
				"RRRRRRRRRR" }; // expected code string
		
		Object[] dataSet4 = new Object[] { 
				new char[] {'0','1','5','I','J','L','O','S'}, // excludes
				new char[] {'0','1','5','I','J','L','O'}, // expected final excludes
				new char[] {'T','U','V','W','X','Y','Z'}, // expected final replacements
				10, // code length
				28, // expected character set length
				new BigInteger("45321387562136"), // number input
				"47QPVTE2WT" }; // expected code string
		
		// max excludes test
		Object[] dataSet5 = new Object[] { 
				maxExclusions, // maximum exclusion count (36 - MIN_CHAR_SET_LENGTH)
				maxFinalExcludes, // expected final excludes
				maxFinalReplacements, // expected final replacements
				10, // code length
				5, // expected character set length
				new BigInteger("4655122"), // number input
				"XWZXZYVZZX" }; // expected code string
		
		Object[][] data = new Object[][] { dataSet1, dataSet2, dataSet3, dataSet4, dataSet5 };
		return Arrays.asList(data);
	}
	
	public CodeFormatterTest(char[] excludes, char[] expectedFinalExcludes, 
			char[] expectedFinalReplacements, int codeLen, int expectedCharSetLen, 
			BigInteger numberInput, String expectedCodeString) {
		_excludes = excludes;
		_expectedFinalExclusions = expectedFinalExcludes;
		_expectedFinalReplacements = expectedFinalReplacements;
		_codeLen = codeLen;
		_expectedCharSetLen = expectedCharSetLen;
		_numberInput = numberInput;
		_expectedCodeString = expectedCodeString;
		
		_testInstance = new CodeFormatter(_codeLen);
		_testInstance.initExclusions(_excludes);
	}

	@Test
	public void codeLenAndCharSetLenShouldSetupCorrectly() {
		assertEquals("Code Length setup correctly", _codeLen, _testInstance.getCodeLen());
		assertEquals("Code Length setup correctly", _expectedCharSetLen, _testInstance.getCharSetLen());
	}
	
	@Test
	public void initExclusionsShouldProduceCorrectArrays() {
		assertArrayEquals("Final exclusions set up correctly", _expectedFinalExclusions, _testInstance.getExcludes());
		assertArrayEquals("Final replacements set up correctly", _expectedFinalReplacements, _testInstance.getReplacements());
	}
	
	@Test
	public void getCodeStringShouldProduceCorrectString1() {
		assertEquals("value 1 leads to correct string", _expectedCodeString, _testInstance.getCodeString(_numberInput));
	}
}
