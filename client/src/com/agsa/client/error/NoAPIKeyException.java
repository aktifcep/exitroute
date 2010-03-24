package com.agsa.client.error;

public class NoAPIKeyException extends AGSAException {

	private static final long serialVersionUID = 4103309069830153110L;

	public NoAPIKeyException() {
		super();
	}

	public NoAPIKeyException(String message) {
		super(message);
	}

}
