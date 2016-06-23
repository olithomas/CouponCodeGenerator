package com.codefarm.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;

/**
 * The FrontEndContext is created by the BackEndContext and handed to the front end. Once created it handles all comms
 * with the front end for the lifetime of that context.
 * The FrontEndContext contains the CodeSpaces, Codes, and Users associated with the FrontEndContext and provides methods
 * for the manipulation of these entities.
 * 
 * @author Home
 *
 */
public class FrontEndContext extends Observable {
	
	private ObjectIdGenerator _idFactory;
	private String _frontEndName;
	private String _frontEndIp;
	private Timestamp _created;
	private Hashtable<Integer, CodeSpace> _codeSpaces;
	private Hashtable<Integer, User> _users;
	private Hashtable<String, Code> _codes;
	private int _frontEndId;

	public FrontEndContext(String frontEndName) {
		_frontEndName = frontEndName;
		_created = new Timestamp(System.currentTimeMillis());
		_idFactory = new ObjectIdGenerator();
		_frontEndId = _idFactory.nextId();
		_codeSpaces = new Hashtable<Integer, CodeSpace>();
		_users = new Hashtable<Integer, User>();
		_codes = new Hashtable<String, Code>();
	}

	/**
	 * Interface spec - addCodeSpace(codeLen, excludes, codeSpaceName)
	 * Add a front-end-owned code space, with excluded characters
	 * 
	 * @param codeLen
	 * @param excludes
	 * @param codeSpaceName
	 * @return The new code space ID
	 * @throws IllegalArgumentException
	 */
	public int addCodeSpace(int codeLen, char[] excludes, String codeSpaceName)
			throws IllegalArgumentException {
		int newCodeSpaceId = _idFactory.nextId();
		CodeSpace newCodeSpace;
		try {
			newCodeSpace = new CodeSpace(codeLen, excludes, newCodeSpaceId, codeSpaceName);
		} catch (IllegalArgumentException e) {
			throw e;
		}
		newCodeSpace.setOwner(_frontEndId, Code.FRONTEND_TYPE);
		_codeSpaces.put(newCodeSpaceId, newCodeSpace);
		this.setChanged();
		this.notifyObservers(newCodeSpace);
		return newCodeSpaceId;
	}
	
	/**
	 * Interface spec - addCodeSpace(codeLen, codeSpaceName)
	 * Add a front-end-owned code space, with no excluded characters
	 * 
	 * @param codeLen
	 * @param excludes
	 * @param codeSpaceName
	 * @return The new code space ID
	 * @throws IllegalArgumentException
	 */
	public int addCodeSpace(int codeLen, String codeSpaceName)
			throws IllegalArgumentException {
		int newCodeSpaceId = _idFactory.nextId();
		CodeSpace newCodeSpace;
		try {
			newCodeSpace = new CodeSpace(codeLen, newCodeSpaceId, codeSpaceName);
		} catch (IllegalArgumentException e) {
			throw e;
		}
		newCodeSpace.setOwner(_frontEndId, Code.FRONTEND_TYPE);
		_codeSpaces.put(newCodeSpaceId, newCodeSpace);
		this.setChanged();
		this.notifyObservers(newCodeSpace);
		return newCodeSpaceId;
	}
	
	/**
	 * Interface spec - addCodeSpace(codeLen, excludes, codeSpaceName, userId)
	 * Add a user-owned code space, with excluded characters
	 * 
	 * @param codeLen
	 * @param excludes
	 * @param codeSpaceName
	 * @return The new code space ID
	 * @throws IllegalArgumentException
	 */
	public int addCodeSpace(int codeLen, char[] excludes, String codeSpaceName, int userId)
			throws IllegalArgumentException {
		if (!_users.containsKey(userId)) {
			throw new IllegalArgumentException("User does not exist.");
		}
		int newCodeSpaceId = _idFactory.nextId();
		CodeSpace newCodeSpace;
		try {
			newCodeSpace = new CodeSpace(codeLen, excludes, newCodeSpaceId, codeSpaceName);
		} catch (IllegalArgumentException e) {
			throw e;
		}
		newCodeSpace.setOwner(userId, Code.USER_TYPE);
		_codeSpaces.put(newCodeSpaceId, newCodeSpace);
		this.setChanged();
		this.notifyObservers(newCodeSpace);
		return newCodeSpaceId;
	}
	
	/**
	 * Interface spec - addCodeSpace(codeLen, codeSpaceName, userId)
	 * Add a user-owned code space, with no excluded characters
	 * 
	 * @param codeLen
	 * @param excludes
	 * @param codeSpaceName
	 * @return The new code space ID
	 * @throws IllegalArgumentException
	 */
	public int addCodeSpace(int codeLen, String codeSpaceName, int userId)
			throws IllegalArgumentException {
		if (!_users.containsKey(userId)) {
			throw new IllegalArgumentException("User does not exist.");
		}
		int newCodeSpaceId = _idFactory.nextId();
		CodeSpace newCodeSpace;
		try {
			newCodeSpace = new CodeSpace(codeLen, newCodeSpaceId, codeSpaceName);
		} catch (IllegalArgumentException e) {
			throw e;
		}
		newCodeSpace.setOwner(userId, Code.USER_TYPE);
		_codeSpaces.put(newCodeSpaceId, newCodeSpace);
		this.setChanged();
		this.notifyObservers(newCodeSpace);
		return newCodeSpaceId;
	}

	/**
	 * Interface spec - addUser(userData)
	 * Add a user to the front end
	 * 
	 * @param userData
	 * @return The new user ID
	 * @throws IllegalArgumentException
	 */
	public int addUser(String[] userData) throws IllegalArgumentException {
		int newUserId = _idFactory.nextId();
		User newUser;
		newUser = new User(newUserId, userData);
		_users.put(newUserId, newUser);
		this.setChanged();
		this.notifyObservers();
		return newUserId;
	}
	
	/**
	 * Interface spec - addCodes(n, codeSpaceId)
	 * Add n codes to the code space specified by codeSpaceId. The owner of the codes is set as this front end.
	 * 
	 * @param n
	 * @param codeSpaceId
	 * @return The newly created codes
	 * @throws IllegalArgumentException
	 */
	public ArrayList<Code> addCodes(int n, int codeSpaceId)
			throws IllegalArgumentException {
		CodeSpace cs = _codeSpaces.get(codeSpaceId);
		if (cs == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		ArrayList<Code> returnCodes = cs.generateCodes(n, _frontEndId, Code.FRONTEND_TYPE);
		for (Code code : returnCodes) _codes.put(code.getStringVal(), code);
		this.setChanged();
		this.notifyObservers();
		return returnCodes;
	}
	
	/**
	 * Interface spec - addCodes(n, codeSpaceId, userId)
	 * Add n codes to the code space specified by codeSpaceId, and set the owner of the codes to the User specified by
	 * userId.
	 * 
	 * @param n
	 * @param codeSpaceId
	 * @param userId
	 * @return The newly created codes
	 * @throws IllegalArgumentException
	 */
	public ArrayList<Code> addCodes(int n, int codeSpaceId, int userId) 
			throws IllegalArgumentException {
		User user = _users.get(userId);
		CodeSpace cs = _codeSpaces.get(codeSpaceId);
		if (user == null || cs == null) {
			throw new IllegalArgumentException("User or Code Space does not exist.");
		}
		ArrayList<Code> returnCodes = cs.generateCodes(n, userId, Code.USER_TYPE);
		for (Code code : returnCodes) _codes.put(code.getStringVal(), code);
		this.setChanged();
		this.notifyObservers();
		return returnCodes;
	}
	
	/**
	 * Convenience method not part of the interface spec.
	 * Gets the Code object specified by the code String.
	 * 
	 * @param code
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Code getCode (String code)
			throws IllegalArgumentException {
		Code returnCode;
		if ((returnCode = _codes.get(code)) == null) {
			throw new IllegalArgumentException("Code does not exist.");
		}
		return returnCode;
	}
	
	/**
	 * Interface spec - getCodesByUserId(userId)
	 * Gets all codes with owner set to userId.
	 * 
	 * @param userId
	 * @return All codes with owner set to userId.
	 * @throws IllegalArgumentException
	 */
	public ArrayList<Code> getCodesByUserId(int userId) 
			throws IllegalArgumentException {
		ArrayList<Code> returnCodes = new ArrayList<Code>();
		if (!_users.containsKey(userId)) {
			throw new IllegalArgumentException("User does not exist.");
		}
		for (Map.Entry<String, Code> entry : _codes.entrySet()) {
			Code thisCode = entry.getValue();
			if (thisCode.getOwnerId() == userId && thisCode.getOwnerType() == Code.USER_TYPE) {
				returnCodes.add(thisCode);
			}
		}
		return returnCodes;
	}
	
	/**
	 * Interface spec - getCodesByCodeSpaceId(codeSpaceId)
	 * Gets all codes with generatorId set to codeSpaceId.
	 * 
	 * @param userId
	 * @return All codes with generatorId set to codeSpaceId.
	 * @throws IllegalArgumentException
	 */
	public ArrayList<Code> getCodesByCodeSpaceId(int codeSpaceId) 
			throws IllegalArgumentException {
		ArrayList<Code> returnCodes = new ArrayList<Code>();
		if (!_codeSpaces.containsKey(codeSpaceId)) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		for (Map.Entry<String, Code> entry : _codes.entrySet()) {
			Code thisCode = entry.getValue();
			if (thisCode.getGeneratorId() == codeSpaceId) {
				returnCodes.add(thisCode);
			}
		}
		return returnCodes;
	}
	
	/**
	 * Interface spec - setCodeData(userId, code, newData)
	 * Sets the data associated with this code. Will only update the information if the code is owned by the User 
	 * specified by userId. (only a user can update his own codes)
	 * 
	 * @param userId
	 * @param code
	 * @param newData
	 * @throws IllegalArgumentException
	 */
	public void setCodeData(int userId, Code code, String newData)
			throws IllegalArgumentException {
		if (code.getOwnerId() != userId) {
			throw new IllegalArgumentException("User does not own this code.");
		}
		code.setCodeData(newData);
	}
	
	/**
	 * Interface spec - getCodeSpace(codeSpaceId)
	 * Get the code space specified by codeSpaceId
	 * 
	 * @param codeSpaceId
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CodeSpace getCodeSpace(int codeSpaceId)
			throws IllegalArgumentException {
		CodeSpace cs;
		if ((cs = _codeSpaces.get(codeSpaceId)) == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		return cs;
	}
	
	/**
	 * Convenience method, not part of interface spec.
	 * 
	 * @return
	 */
	public ArrayList<CodeSpace> getCodeSpaces() {
		return new ArrayList<CodeSpace>(_codeSpaces.values());
	}
	
	/**
	 * Convenience method, not part of interface spec.
	 * 
	 * @return
	 */
	public ArrayList<CodeSpace> getMyCodeSpaces() {
		ArrayList<CodeSpace> allCs = new ArrayList<CodeSpace>(_codeSpaces.values());
		ArrayList<CodeSpace> selectedCs = new ArrayList<CodeSpace>();
		for (CodeSpace cs : allCs) {
			if (cs.getOwnerId() == _frontEndId) selectedCs.add(cs);
		}
		return selectedCs;
	}
	
	public int getCodeSpaceOwnerId(int codeSpaceId) 
			throws IllegalArgumentException {
		CodeSpace cs;
		if ((cs = _codeSpaces.get(codeSpaceId)) == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		return cs.getOwnerId();
	} 
	
	public int getCodeSpaceOwnerType(int codeSpaceId) 
			throws IllegalArgumentException {
		CodeSpace cs;
		if ((cs = _codeSpaces.get(codeSpaceId)) == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		return cs.getOwnerType(); 
	} 
	
	public String getCodeSpaceName(int codeSpaceId) 
			throws IllegalArgumentException {
		CodeSpace cs;
		if ((cs = _codeSpaces.get(codeSpaceId)) == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		return cs.getCodeSpaceName(); 
	} 
	
	public int getCodeSpaceId(int codeSpaceId) 
			throws IllegalArgumentException {
		CodeSpace cs;
		if ((cs = _codeSpaces.get(codeSpaceId)) == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		return cs.getCodeSpaceId(); 
	} 
	
	public char[] getCodeSpaceExcludes(int codeSpaceId) 
			throws IllegalArgumentException {
		CodeSpace cs;
		if ((cs = _codeSpaces.get(codeSpaceId)) == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		return cs.getExcludes(); 
	} 
	
	public int getCodeSpaceCodeLen(int codeSpaceId) 
			throws IllegalArgumentException {
		CodeSpace cs;
		if ((cs = _codeSpaces.get(codeSpaceId)) == null) {
			throw new IllegalArgumentException("Code Space does not exist.");
		}
		return cs.getCodeLen(); 
	}
	
	/**
	 * Interface spec - getUser(userId)
	 * Get the user specified by userId
	 * 
	 * @param userId
	 * @return
	 * @throws IllegalArgumentException
	 */
	public User getUser(int userId)
			throws IllegalArgumentException {
		User user;
		if ((user = _users.get(userId)) == null) {
			throw new IllegalArgumentException("User does not exist.");
		}
		return user;
	}
	
	/**
	 * Convenience method, not part of interface spec.
	 * 
	 * @return
	 */
	public ArrayList<User> getUsers() {
		return new ArrayList<User>(_users.values());
	}
	
	/**
	 * Interface spec - removeUser(userId)
	 * Remove the specified user.
	 * 
	 * @param userId
	 */
	public void removeUser(int userId) {
		_users.remove(userId);
	}
	
	/**
	 * Convenience getters and setters.
	 * 
	 * @return
	 */
	public String getName() { return _frontEndName; }
	public int getFrontEndId() { return _frontEndId; }
	public Timestamp getCreated() { return _created; }
	public String getFrontEndIp() { return _frontEndIp; }

	public void setFrontEndIp(String frontEndIP) {
		this._frontEndIp = frontEndIP;
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public String toString() {
		return _frontEndName + " - " + _frontEndIp;
	}
}
