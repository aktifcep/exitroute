package com.agsa.client.error;

public class InvalidAPIKeyException extends AGSAException {

	private static final long serialVersionUID = 4103309069830153110L;

	public InvalidAPIKeyException() {
		super();
	}

	public InvalidAPIKeyException(String message) {
		super(message);
	}

}
