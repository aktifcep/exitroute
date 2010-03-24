package com.agsa.client.error;

public abstract class AGSAException extends Exception {

	private static final long serialVersionUID = 6722315235954695830L;

	public AGSAException() {
		super();
	}

	public AGSAException(String message) {
		super(message);
	}
	
}
