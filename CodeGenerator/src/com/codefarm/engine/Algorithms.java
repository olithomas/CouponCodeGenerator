package com.codefarm.engine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.codefarm.entities.Code;

/**
 * Utility class for the algorithms
 * 
 * @author Oliver Thomas
 *
 */
public final class Algorithms {
	
	private Algorithms() {}
	
	/** 
	 * The QRPFunction will return a unique integer in the range 0 < x < p using the Quadratic Residue function
	 * <p>
	 * The QRPFunction makes no check on the value of x or p. It assumes the caller has implemented those 
	 * checks (i.e is x bigger than p, p is prime etc.)
	 * 
	 * @param x	A BigInteger that satisfies 0 < x < p. For all values of x where 2x < p, the quadratic residue
	 * (r) of x^2 will be unique. For all values of x where 2x >= p, the quadratic residue (p - r) will
	 * also be unique
	 * @param p A prime number that satisfies p = 3 mod 4
	 * @return The unique (for 0 < x < p) BigInteger result
	 */
	public static BigInteger QRPFunction(BigInteger p, BigInteger x) {
		
		BigInteger twoX = x.multiply(BigInteger.valueOf(2));
		BigInteger xSquare = x.multiply(x);
		
		BigInteger r;
		r = xSquare.mod(p);
		if (twoX.compareTo(p) < 0) {
			return r;
		} else {
			return p.subtract(r);
		}		
	}
	
	/**
	 * The permute function will return a randomised, unique BigInteger value in the range 0 < returnVal < p
	 * <p>
	 * It passes x and p through the QRPFunction once, then applies an XOR operation to the resultant 
	 * BigInteger (x2). Last of all, it runs the new BigInteger (x2withXor) through the QRPFunction once again.
	 * <p>
	 * This ensures a very random distribution across the whole code space, but as the XOR function is a 
	 * one-to-one function, uniqueness is still maintained.
	 * 
	 * @param p A prime number that satisfies p = 3 mod 4
	 * @param x The BigInteger seed value which should be a BigInteger in the range 0 < x < p.
	 * If x >= p then permute returns x
	 * @param xOrOperand A BigInteger with bit length less than or equal to the bit length of p. Preferably,
	 * it has the same bit length
	 * @return The BigInteger permutation of x
	 * 
	 * TODO Test this with a PRNG test suite!
	 * TODO Devise a test that will prove the uniqueness of all codes within a CodeSpace
	 * TODO Produce a mathematical proof that all permutations, no matter what the values of p and max permutations,
	 * will be unique!
	 */
	public static BigInteger permute(BigInteger p, BigInteger x, BigInteger xOrOperand) {

		BigInteger x2;
		BigInteger x2withXOr;
		if (x.compareTo(p) >= 0) {
			// p should be the closest prime value to the max-permutations of the code space 
			// that satisfies p=3(mod4)
			// x can therefore be greater than p, in which case, just return x..
			return x;
		} else {
			// I've done this in steps to make it more readable..
			// 1. Run the function once to get the second seed..
			x2 = QRPFunction(p, x);
			// 2. Compute the XOR result using the operand passed in..
			//    But check the Xor result isn't 0 (i.e. the Xor operand and x2 are equal).
			//    If so, then set x2withXOr to the same as the xOrOperand..
			if ((x2withXOr = x2.xor(xOrOperand)) == BigInteger.ZERO) x2withXOr = x2;
			// 3. Run the QRP function once again, using the 'XORed' result from the 1st function call..
			return QRPFunction(p, x2withXOr);
		}
	}
	
	/**
	 * Returns a prime number (P) that satisfies P = 3 (mod 4), and is as close as possible to the maximum
	 * permutations of the code space but is less than the maximum permutations.
	 * <p>
	 * The isProbablyPrime method of the BigInteger class is used to test primality. This method takes a single
	 * argument to specify the certainty of the prime test. 100 is passed which makes the test near-certain.
	 * @param maxPerms The maximum permutations of the code space
	 * @return A prime number (P) that satisfies P = 3 (mod 4), and is as close as possible to the maximum
	 * permutations of the code space but is less than the maximum permutations.
	 */
	public static BigInteger getBiggestPrime(BigInteger maxPerms) {
		
		BigInteger cVal = maxPerms;
		for (; cVal.compareTo(BigInteger.ZERO) > 0; cVal = cVal.subtract(BigInteger.ONE)) {
			if (cVal.isProbablePrime(100)) {
				// then this is a prime number with near perfect certainty
				if (cVal.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) == 0) {
					// then this is the closest prime that satisfies P = 3 (mod4)
					return cVal;
				}
			}
		}
		// Belt and braces!! (this is largest valid prime for a 32-bit integer)
		return new BigInteger("4294967291");
	}
	
	
	
	
	/**
	 * Convenience method to test a an ArrayList of Codes for duplicates
	 * 
	 * @param codes The ArrayList of Codes to test
	 * @return True if duplicates exist in the codes, false otherwise
	 */
	public static boolean hasDuplicates(ArrayList<Code> codes) {
		boolean isDuplicate;
		isDuplicate = false;
		Set<BigInteger> set = new HashSet<BigInteger>();
		for (Code code: codes) {
			isDuplicate = !set.add(code.getIntegerVal());
			if (isDuplicate) {
				set.clear();
				return true;
			}
		}
		set.clear();
		return false;
	}
	
	/**
	 * Convenience method to return an ArrayList of Code objects that are duplicates in the passed-in
	 * ArrayList
	 * @param codes The ArrayList to extract the duplicates from
	 * @return A new ArrayList containing the Codes that are duplicates in the passed-in ArrayList
	 */
	public static ArrayList<Code> getDuplicates(ArrayList<Code> codes) {
		boolean isDuplicate;
		isDuplicate = false;
		Set<BigInteger> testSet = new HashSet<BigInteger>();
		ArrayList<Code> returnList = new ArrayList<Code>();
		for (Code code: codes) {
			isDuplicate = !testSet.add(code.getIntegerVal());
			if (isDuplicate) {
				returnList.add(code);
			}
		}
		return returnList;
	}
	
	/**
	 * Convenience method to print the maximum BigInteger value in an ArrayList of Code objects
	 * 
	 * @param codes The ArrayList of codes to find the maximum BigInteger value in
	 * @return The BigInteger that is the maximum value in codes (as String)
	 */
	public static String printMax(ArrayList<Code> codes) {
		BigInteger result = BigInteger.ZERO;
		for (Code code : codes) {
			if (code.getIntegerVal().compareTo(result) > 0) {
				result = code.getIntegerVal();
			}
		}
		return result.toString();
	}
	
	/**
	 * Convenience method to print the minimum BigInteger value in an ArrayList of Code objects
	 * 
	 * @param codes The ArrayList of codes to find the minimum BigInteger value in
	 * @return The BigInteger that is the minimum value in codes (as String)
	 */
	public static String printMin(ArrayList<Code> codes) {
		BigInteger result;
		int i = 0;
		for (result = codes.get(i).getIntegerVal(); i < codes.size(); i++) {
			if (codes.get(i).getIntegerVal().compareTo(result) < 0) {
				result = codes.get(i).getIntegerVal();
			}
		}
		return result.toString();
	}
}
