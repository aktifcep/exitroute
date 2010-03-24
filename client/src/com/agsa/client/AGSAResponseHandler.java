package com.agsa.client;

public interface AGSAResponseHandler<T> {

	public void onSuccess(T result);
	
	public void onFailure(Throwable e);
	
}
