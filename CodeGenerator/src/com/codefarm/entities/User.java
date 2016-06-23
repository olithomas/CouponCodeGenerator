package com.codefarm.entities;

public class User {
	private String _firstName;
	private String _lastName;
	private String _email;
	private int _userId;
	
	public String getFirstName() {
		return _firstName;
	}

	public String getLastName() {
		return _lastName;
	}

	public String getEmail() {
		return _email;
	}

	public int getUserId() {
		return _userId;
	}

	public User(int userId, String[] userData) {
		_firstName = userData[0];
		_lastName = userData[1];
		_email = userData[2];
		_userId = userId;
	}

	@Override
	public String toString() {
		return _firstName + " " + _lastName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _userId;
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
		User other = (User) obj;
		if (_userId != other._userId)
			return false;
		return true;
	}
}
