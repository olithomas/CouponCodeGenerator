package com.codefarm.engine;

import java.math.BigInteger;
import java.util.Random;

import com.codefarm.entities.Code;

/**
 * An instance of a generator. The generator produces uniformly distributed, random, and unique BigIntegers
 * in a range from 1 to the maximum permutations of the specified CodeFormatter object.
 * The class uses a modified Quadratic Residue function to produce the permutations, with the addition of a
 * randomly generated XOR operand that is used as a one-to-one function to improve the distribution, and to
 * differentiate the sequences of codes from instance to instance.
 * <p>
 * Each instance of a CodeGenerator maintains a CodeFormatter object, and is managed in turn by a CodeSpace.
 * A CodeSpace may only be instantiated with one CodeGenerator, but a front end may contain multiple CodeSpaces.
 * <p>
 * In any case, with the addition of the randomly chosen XOR operand, each sequence of codes should be unique
 * between different instances of CodeSpace.
 * <p>
 * The main method that this class implements is the next() method, which simply returns the next BigInteger 
 * in the sequence.
 * 
 * @author Oliver Thomas
 *
 */
public class CodeGenerator {
	
	private final CodeFormatter _codeFormatter;
	private BigInteger _x;
	private final BigInteger _p;
	private final BigInteger _xOrOperand;
	private final BigInteger _maxPerms;
	
	/**
	 * Return the maximum permutations of the CodeGenerator
	 * 
	 * @return The maximum permutations of the CodeGenerator
	 */
	public BigInteger getMaxPerms() { return _maxPerms; }

	/**
	 * Construct a new instance of CodeGenerator with the specified CodeFormatter and seed value (usually 1)
	 * <p>
	 * The Constructor initialises all the values needed to produce the sequence:<p>
	 * - The large prime used in the Quadratic Residue Function (_p)<p>
	 * - The XOR operand used along with the Quadratic Residue Function (_xOrOperand)<p>
	 * - The maximum permutations of the code space (_maxPerms)<p>
	 * All these values are declared final as they remain the same for the whole sequence. The only value
	 * needed for the generation that is not final is the _x value, the initial value of which can be passed in
	 * (the seed value). This value is incremented each time the next() method is called. The initial value
	 * of this will usually be 1 and will continue to increment up to the maximum permutations of the code space.
	 * <p>
	 * The XOR operand is produced using the random BigInteger constructor and an instance of Random.
	 * This could be extended in future to use an external source of randomness.
	 * 
	 * @param codeFormatter The CodeFormatter object to use in the CodeGenerator
	 * @param seed The initial value of _x (usually 1)
	 */
	public CodeGenerator(CodeFormatter codeFormatter, BigInteger seed) {

		_codeFormatter = codeFormatter;
		_x = seed; // Usually 1
		
		// The max permutations of the code space is found from (n^r)-1 where:
		// n = the number of characters in the CodeFormatter character set
		// r = the length (number of characters) in the code
		_maxPerms = BigInteger.valueOf(
				_codeFormatter.getCharSetLen()).pow(
						_codeFormatter.getCodeLen()).subtract(
								BigInteger.ONE);
		
		// P is the biggest prime less than the max permutations that satisfies P = 3 mod 4
		_p = Algorithms.getBiggestPrime(_maxPerms);
		
		// The xOrOperand is a random bit string the same length as the max permutations.
		// Currently uses a standard Random instance for the source of randomness.
		// TODO enable use of custom random source??
		_xOrOperand = new BigInteger(_maxPerms.bitLength(),0,new Random());
	}
	
	/**
	 * Increments the seed value (_x) and returns the next Code in the sequence 
	 * 
	 * @return The next Code in the sequence
	 */
	public Code next() {
		Code code;
		BigInteger intVal;
		String stringVal;
		
		// Increment the seed
		_x = _x.add(BigInteger.ONE);
		
		// get the permutation for _x
		intVal = Algorithms.permute(_p, _x, _xOrOperand);
		
		// Get the code string for this permutation from the CodeFormatter
		stringVal = _codeFormatter.getCodeString(intVal);
		
		// And then build and return the Code object
		code = new Code(intVal, stringVal);
		return code;
	}
}
