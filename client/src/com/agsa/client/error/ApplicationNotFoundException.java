package com.agsa.client.error;

public class ApplicationNotFoundException extends AGSAException {

	private static final long serialVersionUID = 4103309069830153110L;

	public ApplicationNotFoundException() {
		super();
	}

	public ApplicationNotFoundException(String message) {
		super(message);
	}

}
