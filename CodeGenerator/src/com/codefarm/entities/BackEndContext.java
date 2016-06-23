package com.codefarm.entities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

/**
 * The BackEndContext is a single context that handles all comms not associated with a particular FrontEndContext. 
 * The BackEndContext contains the FrontEndContexts associated with the BackEndContext and provides methods
 * for the manipulation of these entities.
 * <p>
 * The BackEndContext is both an Observer of it's FrontEndContexts and is Observable to the back end window.
 * This allows the back end window to update it's tables dynamically with changes to both the BackEndContext (i.e. new
 * or deleted FrontEndContexts) and the active FrontEndContexts (i.e. users and CodeSPaces added).
 * 
 * @author Oliver Thomas
 *
 */

public class BackEndContext extends Observable implements Observer {
	
	/**
	 * TemporaryContext is handed back to a front end window upon connection. Once the full FrontEndContext is 
	 * created successfully and handed to the front end window, the TemporaryContext is destroyed.
	 * 
	 * @author Oliver Thomas
	 *
	 */
	public class TemporaryContext {
		private boolean _isAuthenticated;
		private String _frontEndIp;
		
		protected boolean isAuthenticated() { return _isAuthenticated; }
		/**
		 * Protected setter for authentication flag - not part of the interface spec.
		 * @param isAuthenticated
		 */
		protected void setAuthenticated(boolean isAuthenticated) { _isAuthenticated = isAuthenticated; }
		protected String getFrontEndIp() { return _frontEndIp; }
		
		protected TemporaryContext(String ip, boolean authenticated) {
			_frontEndIp = ip;
			_isAuthenticated = authenticated;
		}
		
		@Override
		public boolean equals(Object v) {
			boolean retVal = false;
			
			if (v instanceof TemporaryContext) {
				TemporaryContext temporaryContextV = (TemporaryContext) v;
				retVal = temporaryContextV._frontEndIp.equals(this._frontEndIp);
			}
			return retVal;
		}
		
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 17 * hash + ((this._frontEndIp == null) ? 0 : this._frontEndIp.hashCode());
			return hash;
		}
	}
	
	private String _backEndPassword;
	private String _backEndIP;
	private ArrayList<TemporaryContext> _tempContexts;
	private Hashtable<Integer, FrontEndContext> _fullContexts;
	
	public String getIpAddress() { return _backEndIP; }

	public BackEndContext(String ipAddress, String password) {
		_backEndIP = ipAddress;
		_backEndPassword = password;
		_tempContexts = new ArrayList<TemporaryContext>();
		_fullContexts = new Hashtable<Integer, FrontEndContext>();
	}
	
	/**
	 * Interface spec - connect()
	 * 
	 * @param frontEndIp
	 * @return a TemporaryContext containing the front end IP and the authentication flag (set false)
	 */
	public TemporaryContext connect(String frontEndIp) {
		TemporaryContext temporaryContext = new TemporaryContext(frontEndIp, false);
		_tempContexts.add(temporaryContext);
		return temporaryContext;
	}
	
	/**
	 * Interface spec - authenticate()
	 * 
	 * @param tempContext
	 * @param password
	 * @return
	 * @throws IllegalStateException
	 */
	public boolean authenticate(TemporaryContext tempContext, String password) throws IllegalStateException {
		boolean hasContext = _tempContexts.contains(tempContext);
		if (!hasContext) throw new IllegalStateException("The Front End has no temporary context (is not connected)");
		boolean authResult = password.equals(_backEndPassword);
		tempContext.setAuthenticated(authResult);
		return authResult;
	}
	
	/**
	 * Interface spec - register
	 * 
	 * @param tempContext
	 * @param frontEndName
	 * @return
	 * @throws IllegalStateException
	 */
	public FrontEndContext register(TemporaryContext tempContext, String frontEndName) throws IllegalStateException {
		boolean hasContext = _tempContexts.contains(tempContext);
		if (!hasContext) throw new IllegalStateException("The Front End has no temporary context (is not connected)");
		if (!tempContext.isAuthenticated()) throw new IllegalStateException("The Front End is not authenticated");
		FrontEndContext fe = new FrontEndContext(frontEndName);
		fe.addObserver(this);
		fe.setFrontEndIp(tempContext.getFrontEndIp());
		_fullContexts.put(fe.getFrontEndId(), fe);
		_tempContexts.remove(tempContext);
		this.setChanged();
		this.notifyObservers();
		return fe;
	}

	/**
	 * Interface spec - deRegister()
	 * 
	 * @param frontEndId
	 * @throws IllegalArgumentException
	 */
	public void deRegister(int frontEndId)
			throws IllegalArgumentException {
		if (!_fullContexts.containsKey(frontEndId)) {
			throw new IllegalArgumentException("Front End does not exist.");
		}
		_fullContexts.remove(frontEndId);
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Not part of the interface spec - just a convenience method for the example UI.
	 * 
	 * @return
	 */
	public ArrayList<FrontEndContext> getFrontEndContexts() {
		return new ArrayList<FrontEndContext>(_fullContexts.values());
	}

	/**
	 * Not part of the interface spec - just a convenience method for the example UI.
	 * 
	 * @return
	 */
	public FrontEndContext getFrontEndById(int frontEndId)
			throws IllegalArgumentException {
		if (!_fullContexts.containsKey(frontEndId)) {
			throw new IllegalArgumentException("Front End does not exist.");
		}
		return _fullContexts.get(frontEndId);
	}

	@Override
	public void update(Observable o, Object arg) {
		// If a front end notifies this of a change, just notify our Observers..
		this.setChanged();
		this.notifyObservers();		
	}
	
	
}
