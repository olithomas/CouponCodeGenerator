package com.codefarm.entities;

import java.math.BigInteger;
import java.util.ArrayList;

import com.codefarm.engine.*;

/**
 * Instance of a code generation and formatting engine, or Code Space. Instances of this object hold their 
 * own code generation state and therefore guarantee the production of unique codes within the context of 
 * the instance, for the life of the instance.
 * <p>
 * A FrontEndContext may hold one or more code spaces at any time. The primary method generateCodes is used to 
 * return the next n unique codes from this code space.
 * <p>
 * A code space may be owned by either a User or the FrontEndContext. An example of a front-end-owned code space might
 * be for a front end that offers different users a set of codes to perform some function. An example where a user
 * might own a code space is where a front end wants to offer entire sequences to it's customers in order to run a 
 * coupen promotion for example.
 * 
 * @author Oliver Thomas
 *
 */

public class CodeSpace {
	/**
	 * Minimum number of permutations that may be generated in one go 
	 */
	public static final int MIN_PERM = 10;
	/**
	 * Maximum number of permutations that may be generated in one go 
	 */
	public static final int MAX_PERM = 100000000;
	
	private CodeGenerator _generator;
	private int _codeLen;
	private char[] _excludes;
	private int _codeSpaceId;
	private String _codeSpaceName;
	private int _ownerId;
	private int _ownerType;
	
	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public int getOwnerId() { return _ownerId; } 
	public int getOwnerType() { return _ownerType; } 
	public String getCodeSpaceName() { return _codeSpaceName; } 
	public int getCodeSpaceId() { return _codeSpaceId; } 
	public int getCodeLen() { return _codeLen; }

	public char[] getExcludes() {
		// this method is only used for displaying so return N,o,n,e,. if it's empty.
		if (_excludes == null) return "None.".toCharArray();
		else return _excludes;
	}
	
	public boolean hasExcludes() {
		if (_excludes == null) return false;
		return true;
	}

	/**
	 * Construct a new CodeSpace instance using the specified code length, excluded, ID and name
	 * 
	 * @param codeLen The required code length
	 * @param excludes A char array of the characters to exclude from the code strings
	 * @param codeSpaceId The ID for the new code space.
	 * @param codeSpaceName the name for the new code space.
	 */
	protected CodeSpace(int codeLen, char[] excludes, int codeSpaceId, String codeSpaceName) {
		_codeSpaceId = codeSpaceId;
		_codeSpaceName = codeSpaceName;
		_codeLen = codeLen;
		_excludes = excludes;
		_generator = new CodeGenerator(new CodeFormatter(codeLen, excludes), BigInteger.ONE);
		_ownerId = -1; // Owner not initialised
		_ownerType = Code.FRONTEND_TYPE; // Which is default
	}

	/**
	 * Construct a new CodeSpace instance using the specified code length, ID and name (no excludes)
	 * 
	 * @param codeLen The required code length
	 * @param codeSpaceId The ID for the new code space.
	 * @param codeSpaceName the name for the new code space.
	 */
	protected CodeSpace(int codeLen, int codeSpaceId, String codeSpaceName) {
		_codeSpaceId = codeSpaceId;
		_codeSpaceName = codeSpaceName;
		_codeLen = codeLen;
		_generator = new CodeGenerator(new CodeFormatter(codeLen), BigInteger.ONE);
		_ownerId = -1; // Owner not initialised
		_ownerType = Code.FRONTEND_TYPE; // Which is default
	}
	
	protected void setOwner(int ownerId, int ownerType) {
		_ownerId = ownerId;
		if (ownerType < Code.FRONTEND_TYPE || ownerType > Code.USER_TYPE) {
			_ownerType = Code.FRONTEND_TYPE; // set to default if passed an incorrect value
		} else {
			_ownerType = ownerType;
		}
	}
	
	/**
	 * Generate the next n unique codes in this code space, and update the state ready for the next call.
	 * <p>
	 * The method currently uses the CodeGenerator next() method to retrieve the codes, and then shuffles
	 * the resulting set before returning. This uses the built in Collections.shuffle API. This was included
	 * before the CodeGenerator was as refined as it is now (i.e. there were concerns over the distribution
	 * of the returned results). The CodeGenerator now has very good distribution, so it may not be necessary
	 * now.
	 * <p>
	 * The function forces the returned permutation count between MIN_PERM and MAX_PERM
	 * The MIN_PERM value exists to keep the shuffled ArrayList sufficiently large to ensure
	 * good random distribution of the permutations.
	 * The MAX_PERM value sets a limit on the number of permutations to stop large values
	 * passed in error from killing the software!
	 * @param n The number of codes to generate
	 * @param ownerId The user ID of the owner for this code
	 * @return An ArrayList of generated code strings
	 */
	protected ArrayList<Code> generateCodes(int n, int ownerId, int ownerType) {
		
		ArrayList<Code> returnCodes = new ArrayList<Code>();
		
		if (n < CodeSpace.MIN_PERM) n = CodeSpace.MIN_PERM;
		if (n > CodeSpace.MAX_PERM) n = CodeSpace.MAX_PERM;
		
		for (int i = 0; i < n; i++) {
			Code newCode = _generator.next();
			newCode.setOwner(ownerId, ownerType);
			newCode.setGeneratorId(this.getCodeSpaceId());
			returnCodes.add(newCode);
		}
		return returnCodes;
	}
	
	@Override
	public String toString() {
		return _codeSpaceName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _codeSpaceId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeSpace other = (CodeSpace) obj;
		if (_codeSpaceId != other._codeSpaceId)
			return false;
		return true;
	}
}
