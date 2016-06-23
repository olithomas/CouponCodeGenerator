package com.codefarm.entities;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Container for generated codes including the String and BigInteger representations
 * 
 * @author Oliver Thomas
 *
 */
public class Code {
	
	/**
	 * Constants for identifying the type of IDs passed in methods.
	 */
	public static final int FRONTEND_TYPE = 1;
	public static final int USER_TYPE = 2;
	public static final int CODE_SPACE_TYPE = 3;
	
	private final BigInteger _integerVal;
	private final String _stringVal;
	private String _codeData;
	private Timestamp _lastUpdate;
	private Timestamp _created;
	private int _ownerId;
	private int _ownerType; // can be USER or FRONT_END
	private int _generatorId; // Always a CodeSpace

	public int getOwnerType() { return _ownerType; }
	public int getGeneratorId() { return _generatorId; }
	public void setGeneratorId(int generatorId) { _generatorId = generatorId; }

	/**
	 * Return the BigInteger value of this Code
	 * 
	 * @return The BigInteger value of this Code
	 */
	public BigInteger getIntegerVal() { return _integerVal; }

	/**
	 * Return the String value of this Code
	 * 
	 * @return The String value of this Code
	 */
	public String getStringVal() { return _stringVal; }
	
	/**
	 * Return the owner User ID of this Code
	 * 
	 * @return The owner User ID of this Code
	 */
	public int getOwnerId() { return _ownerId; }
	
	/**
	 * Returns the code data for this code (an Object array with the code data and last update Timestamp)
	 * 
	 * @return The code data for this code
	 * @throws IllegalStateException If the code data has not been initialised
	 */
	public Object[] getCodeData() throws IllegalStateException {
		if (_codeData == null) {
			throw new IllegalStateException("Code Data not initialised");
		}
		return new Object[] { _codeData, _lastUpdate };
	}
	
	/**
	 * Returns the creation timestamp for this code
	 * 
	 * @return The creation timestamp for this code
	 */
	public Timestamp getCreationTimestamp() { return _created; }
	
	/**
	 * Constructs a new instance of Code
	 * 
	 * @param integerVal The BigInteger value of the Code
	 * @param codeString The String value of the Code
	 */
	public Code(BigInteger integerVal, String codeString) {
		_integerVal = integerVal;
		_stringVal = codeString;
		_created = new Timestamp(System.currentTimeMillis());
		_ownerType = Code.FRONTEND_TYPE; // Default is front end owner
		_ownerId = -1; // -1 = no owner until setOwner is called
	}
	
	/**
	 * Sets the code data string and updates the last updated timestamp
	 * 
	 * @param codeData The code data String to set
	 * @throws IllegalArgumentException If the code data String is less than 1 or greater than 
	 * 140 characters long
	 */
	public void setCodeData(String codeData) throws IllegalArgumentException {
		if (codeData.length() > 140 || codeData.length() <= 0) {
			throw new IllegalArgumentException("Code data must be between 1 and 140 characters");
		}
		_codeData = codeData;
		_lastUpdate = new Timestamp(System.currentTimeMillis());
	}
	
	public void setOwner(int ownerId, int ownerType) { 
		_ownerId = ownerId; // either a front end or user ID, access ownerType for type. Up to the caller to have the ID right
		if (ownerType < 1 || ownerType > 2) _ownerType = Code.FRONTEND_TYPE; // default is Front End owner
		else _ownerType = ownerType;
	}
	
	@Override
	public String toString() {
		return _stringVal.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_integerVal == null) ? 0 : _integerVal.hashCode());
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
		Code other = (Code) obj;
		if (_integerVal == null) {
			if (other._integerVal != null)
				return false;
		} else if (!_integerVal.equals(other._integerVal))
			return false;
		return true;
	}
}
