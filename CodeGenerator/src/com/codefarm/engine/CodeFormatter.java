package com.codefarm.engine;

import java.util.Arrays;
import java.math.BigInteger;

/**
 * Contains a definition of a code including it's length and character set and
 * provides methods to handle excluded characters and to produce the string representations of a BigIneger
 * generated within the code space.
 * <p>
 * The class provides two constructors: One which allows a code length only. If the caller wishes to specify excluded 
 * characters later, then they must call the initExcludes() method and pass in a char array of excluded characters; And 
 * another which allows a char array of excluded characters to be passed to the constructor.
 * <p>
 * Instances of {@link CodeGenerator} are passed an instance of this class in the constructor. 
 * The {@link CodeGenerator} uses the instance to calculate the maximum permutations possible in the code space
 * It then uses the instance to produce the code strings from the random BigIntegers generated.
 * 
 * @author Oliver Thomas
 */

public class CodeFormatter {
	
	/** The minimum length a code may be. */
	public static final int MIN_CODE_LENGTH = 5;
	/** The maximum length a code may be. */
	public static final int MAX_CODE_LENGTH = 15;
	/** The minimum length of a character set. */
	public static final int MIN_CHAR_SET_LENGTH = 5;
	/** All alpha-numeric characters. Convenience constant. */
	public static final char[] ALL_CHARS = 
		{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' }; 
	
	private char[] _excludedChars; // Characters excluded from use
	private char[] _replacementChars; // Replacements for the characters in _excludedChars. Will always be the same length.
	private final int _codeLen; // Number of characters in each code string
	private int _charSetLen; // Number of different characters that can be used in the code

	/** 
	 * Returns the length of the character set which is calculated as the max character set length
	 *  (36) minus any excluded characters passed to the constructor
	 *  @return charSetLen	The length of the character set of this instance
	 */  
	public int getCharSetLen() { return _charSetLen; }
	
	/** 
	 * Returns the length of the code for this instance (passed to the constructor)
	 *  @return codeLen	The length of the code of this instance
	 */   
	public int getCodeLen() { return _codeLen; }
	
	/** 
	 * Returns the final char array of excluded characters (the result of initExcludes)
	 *  @return excludedChars	The final char array of excluded characters
	 */   
	public char[] getExcludes() { return _excludedChars; }
	
	/** 
	 * Returns the final char array of replacement characters (the result of initExcludes)
	 *  @return excludedChars	The final char array of replacement characters
	 */   
	public char[] getReplacements() { return _replacementChars; }
	
	/**
	 * Constructs a new CodeFormatter object for codes of the specified length
	 * <p>
	 * After the constructor call, all alpha-numeric characters are assumed to be usable. If the caller wishes
	 * to specify excluded characters then they must call {@link initExclusions}, which will 
	 * re-initialise the exclusions and replacements arrays (initialised to zero size arrays by this constructor)
	 * @param codeLen	the length of the code that this format represents
	 * @throws IllegalArgumentException if the code length does not comply to the limits (limits specified as constants in this class)
	 */
	public CodeFormatter(int codeLen) throws IllegalArgumentException {
		// First check the code length..
		if (codeLen < MIN_CODE_LENGTH || codeLen > MAX_CODE_LENGTH) {
			String excStr = "The code length must be between " + 
					Integer.toString(MIN_CODE_LENGTH) +
					" and " +
					Integer.toString(MAX_CODE_LENGTH) + ".";
			throw new IllegalArgumentException(excStr);
		}
		// No excluded characters specified. All characters may be used.
		_charSetLen = 36;
		_codeLen = codeLen;
		// Initialise the missing and replacement arrays to zero size arrays..
		_excludedChars = new char[0];
		_replacementChars = new char[0];
	}
	
	/**
	 * Constructs a new CodeFormatter object for codes of the specified length, with the specified 
	 * excluded characters.
	 * <p>
	 * This constructor calls initExclusions with the passed in excludes.
	 * @param codeLen	The length of the code that this format represents
	 * @param excludes	A char array of the characters to exclude from the code string
	 * @throws IllegalArgumentException if the code length does not comply to the limits (limits specified as constants in this class)
	 */
	public CodeFormatter(int codeLen, char[] excludes) throws IllegalArgumentException {
		// First check the code length..
		if (codeLen < MIN_CODE_LENGTH || codeLen > MAX_CODE_LENGTH) {
			String excStr = "The code length must be between " + 
					Integer.toString(MIN_CODE_LENGTH) +
					" and " +
					Integer.toString(MAX_CODE_LENGTH) + ".";
			throw new IllegalArgumentException(excStr);
		}
		_codeLen = codeLen;
		// Initialise the missing and replacement arrays..
		try {
			initExclusions(excludes);
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}
	
	/**
	 * Initialises the _excludedChars and _replacementChars internal arrays. Call this after construction in
	 * order to exclude characters from the code strings.
	 * <p>
	 * The main idea behind this method is in the way the code string is formed.
	 * The code string is formed using the toString method of the BigInteger class. This takes a radix argument
	 * and will return the string representation of the BigInteger using that radix (base). Where the radix
	 * specified is > 10, the letters will be used for the places above the character 9. Only the letters A to Z are 
	 * available, making the maximum radix = 36 (10 numbers + 26 letters).
	 * <p>
	 * For example, in base 36, you would count from 1 to 36 like this:
	 * 123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ
	 * And BigInteger.valueOf(36).toString(36) returns "Z"
	 * <p>
	 * Just like in the decimal counting system, where there are 10 different symbols used to represent numbers,
	 * The number of different symbols that can be used to create an alpha-numeric code will be the radix that is used 
	 * when calling the toString method.
	 * <p>
	 * Now if some characters are specified as excluded, then the length of the character set, and hence the 
	 * radix, is reduced. If, for example, the following are excluded: {0, 1, I, L, Y}, then the character 
	 * set length, and hence the radix, is now 36 - 5 = 31. The toString function will now use all the numbers
	 * and all the letters except for the last 5, i.e. {V, W, X, Y, Z}.
	 * <p>
	 * Now we know the characters that must be excluded and the characters that will be excluded anyway due to 
	 * the reduced radix: we can create an array of the remaining characters that must be explicitly replaced 
	 * in the toString return value, as they are specified as excludes, and are not excluded naturally by the reduced radix;
	 * And we can also create an array of the characters we can use to replace them by starting with the natural 
	 * radix-reduction excludes, and removing any characters that are specified as excludes.
	 * <p>
	 * These two arrays can then be used by the other methods in order to return the code string representation
	 * needed without including any excluded characters, whilst maintaining the uniqueness of the code string.
	 * <p>
	 * The steps to implement are as follows:<p>
	 * 1. Create an array of the [tempExcludes.length] last characters in the ALL_CHARS array 
	 *    (e.g {X, Y, Z} where tempExcludes.length = 3). This is the temporary replacements array<p>
	 * 2. Build new replacement and new exclude arrays by iterating over both temporary arrays in turn:<p>
	 *		- Iterate over each character in excludes and check if it is in replacements.<p>
	 *		  If it is not found in replacements, then it will not be excluded automatically by the 
	 *		  effect of the reduced radix string and must be excluded explicitly.<p>
	 *		- Iterate over each character in replacements and check if it is in excludes.<p>
	 *		  If it is not found then it is not excluded and may be used to provide a replacement
	 *		  character.<p>
	 * 3. Initialise the instance variables by resizing (reducing) the arrays built in step 2.
	 * @param tempExcludes	A char array of all excluded characters, as passed into the constructor of this instance
	 * <p>
	 * NOTE: this argument is called tempExcludes because the final value of _excludedChars  will not include
	 * any characters that are excluded anyway due to reduced radix.
	 * @throws IllegalArgumentException If any of the passed in exclusion characters are not alpha-numeric, or
	 * if the resulting character set is less than the minimum (limits specified as constants in this class)
	 */
	public void initExclusions(char[] tempExcludes) throws IllegalArgumentException {
		
		// Sort and upper-case the excluded list, and check it's alpha-numeric at the same time..
		for (int i = 0; i < tempExcludes.length; i++) {
			tempExcludes[i] = Character.toUpperCase(tempExcludes[i]);
			if (Arrays.binarySearch(ALL_CHARS, tempExcludes[i]) < 0) {
				// c is not alpha-numeric
				throw new IllegalArgumentException("An excluded code characters must be alpha-numeric.");
			}
		}
		Arrays.sort(tempExcludes);
		
		// Then re-initialise the _charSetLen, taking account of excluded characters..
		int excludesLen = tempExcludes.length;
		_charSetLen = 36 - tempExcludes.length;
		if (_charSetLen < MIN_CHAR_SET_LENGTH) {
			throw new IllegalArgumentException(
				"The number of characters in the character set must be more than " + 
				Integer.toString(MIN_CHAR_SET_LENGTH));
		}
		
		/* 1. Create an array of the [tempExcludes.length] last characters in the ALL_CHARS array 
		 *    (e.g {X, Y, Z} where tempExcludes.length = 3). This is the temporary replacements array
		 */
		char[] tempReplacements = Arrays.copyOfRange(ALL_CHARS, 36 - excludesLen, 36);
		
		/* 2. Build new replacement and new exclude arrays by iterating both temp arrays in turn:
		 *		- Iterate over each character in excludes and check if it is in replacements.
		 *		  If it is not found in replacements, then it will not be excluded automatically by the 
		 *		  effect of the reduced radix string and must be excluded explicitly.
		 *		- Iterate over each character in replacements and check if it is in excludes.
		 *		  If it is not found then it is not excluded and may be used to provide a replacement
		 *		  character.
		 */
		char[] tempReplacements2 = new char[excludesLen];
		char[] tempExcludes2 = new char[excludesLen];
		int excludesIndex = -1, replacementsIndex = -1;
		for (int c = 0; c < excludesLen; c++) {
			if (Arrays.binarySearch(tempReplacements, tempExcludes[c]) < 0) {
				// this excluded character not found in replacements, so it must be explicitly excluded.
				// and the excluded character to the new excludes..
				excludesIndex++;
				tempExcludes2[excludesIndex] = tempExcludes[c];
			}
			if (Arrays.binarySearch(tempExcludes, tempReplacements[c]) < 0) {
				// this replacement character not found in excludes, so it can be used.
				// Add the replacement character to the  new replacements.
				replacementsIndex++;
				tempReplacements2[replacementsIndex] = tempReplacements[c];
			}
		}
		
		/* 3. Re-initialise the instance variables by resizing (reducing) the arrays built in step 2.
		 */
		_replacementChars = Arrays.copyOfRange(tempReplacements2, 0, excludesIndex + 1);
		_excludedChars = Arrays.copyOfRange(tempExcludes2, 0, excludesIndex + 1);
		
		// Check they are the same length
		assert _replacementChars.length == _excludedChars.length;
	}
	
	/**
	 * Returns the String representation of the specified {@link BigInteger} according to the code length and 
	 * character set of the instance
	 * @param intVal	the {@link BigInteger} instance used to generate the code string
	 * @return	{@link String} representation of this {@link BigInteger} instance according to the code length and character set of the instance
	 */
	public String getCodeString(BigInteger intVal) {
		String returnStr = toStringWithExcludes(padLeading(intVal.toString(_charSetLen)));
		assert returnStr.length() == this._codeLen;
		return returnStr;
	}
	
	/**
	 * Returns a new {@link String}, based on the specified code string, but with any excluded characters (from _excludedChars)
	 * replaced with their corresponding replacement characters (from _replacementChars)
	 * @param	codeStr	the {@link String} to be converted
	 * @return	The new {@link String} with excluded character replacements inserted
	 */
	private String toStringWithExcludes(String codeStr) {
		char[] returnCh = codeStr.toUpperCase().toCharArray();
		int matchIndex = -1;
		for (char c = 0; c < returnCh.length; c++) {
			// Look for this c in _excludedChars..
			if ((matchIndex = Arrays.binarySearch(_excludedChars, returnCh[c])) >= 0) {
				// c found in excludes, replace it with the corresponding replacement character..
				returnCh[c] = _replacementChars[matchIndex];
			}
		}
		// Quick check that all excluded chars have been expunged..
		for (char c : this._excludedChars) assert Arrays.binarySearch(returnCh, c) < 0;
		// If _excludedChars is a zero size array, this will return the original string..
		return String.copyValueOf(returnCh);
	}
	
	/**
	 * Returns a new {@link String}, based on the specified codeStr, that is exactly _codeLen characters long. 
	 * If the value of the {@link BigInteger} used to generate the code string resulted in a string shorter than
	 * the code length, then this method pads the leading characters with either '0', if it is not an excluded character,
	 * or the corresponding replacement character (from _replacementChars) for the character '0'.
	 * <p>
	 * If the code string is already of the correct length, this method simply returns the original string.
	 * @param	codeStr	the {@link String} to be padded
	 * @return	The new {@link String} of correct length
	 */
	private String padLeading(String codeStr) {
		char[] codeCh = codeStr.toCharArray();
		char[] returnCh = new char[_codeLen];
		char filler;
		int matchIndex = -1;
		// If _excludedChars is a zero size array then filler will always be '0'..
		if ((matchIndex = Arrays.binarySearch(_excludedChars, '0')) > 0) {
			filler = _replacementChars[matchIndex];
		} else {
			filler = '0';
		}
		int delta = _codeLen - codeCh.length;
		for (int i = 0; i < _codeLen; i++) {
			if (i < delta) {
				returnCh[i] = filler;
			} else {
				returnCh[i] = codeCh[i - delta];
			}
		}
		return String.copyValueOf(returnCh);
	}

}
