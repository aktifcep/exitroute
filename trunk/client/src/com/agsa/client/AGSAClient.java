package com.agsa.client;

import java.io.IOException;
import java.util.Date;

import com.agsa.client.error.InvalidAPIKeyException;
import com.agsa.client.error.ApplicationNotFoundException;
import com.agsa.client.model.AGSAMessage;

/**
 * Client for the AGSA services
 * 
 * @author naholyr
 */
public interface AGSAClient {

	/**
	 * Retrieve list of applications for this API key
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidAPIKeyException
	 */
	public String[] getApplications() throws IOException, InvalidAPIKeyException;
	
	/**
	 * Helper for asynchronous call
	 * 
	 * @see AGSAClient#getApplications()
	 * @param responseHandler
	 */
	public void getApplications(AGSAResponseHandler<String[]> responseHandler);

	/**
	 * Sets the current application.
	 * 
	 * Call this method before any other method refering to application-wide data.
	 * This concerns ALL methods except getApplications.
	 * 
	 * @param appName
	 * @throws IOException
	 * @throws InvalidAPIKeyException
	 * @throws ApplicationNotFoundException
	 */
	public void useApplication(String appName) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException;
	
	/**
	 * Returns current application
	 * 
	 * @return
	 */
	public String getCurrentApplication();
	
	/**
	 * Helper for asynchronous call
	 * 
	 * @see AGSAClient#useApplication(String)
	 * @param appName
	 * @param responseHandler
	 */
	public void useApplication(String appName, AGSAResponseHandler<Void> responseHandler);

	/**
	 * Retrieves messages, ordered by descending date, attached to current application, for 
	 * the provided type and more recent than the provided date.
	 * 
	 * @param type
	 * @param publishedAfter
	 * @return
	 * @throws IOException
	 * @throws InvalidAPIKeyException
	 * @throws ApplicationNotFoundException
	 */
	public AGSAMessage[] getRecentMessages(String type, Date publishedAfter) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException;

	/**
	 * Helper for asynchronous call
	 * 
	 * @see AGSAClient#getRecentMessages(String, Date)
	 * @param appName
	 * @param responseHandler
	 */
	public void getRecentMessages(String type, Date publishedAfter, AGSAResponseHandler<AGSAMessage[]> responseHandler);
	
	/**
	 * Retrieves the total number of message attached to current application for the
	 * provided type.
	 * 
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws InvalidAPIKeyException
	 * @throws ApplicationNotFoundException
	 */
	public int getNbMessages(String type) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException;
 
	/**
	 * Helper for asynchronous call
	 * 
	 * @see AGSAClient#getNbMessages(String)
	 * @param appName
	 * @param responseHandler
	 */
	public void getNbMessages(String type, AGSAResponseHandler<Integer> responseHandler);
	
	/**
	 * Retrieves the list of messages, ordered by descending date, in a paginated way.
	 * 
	 * @param type
	 * @param start
	 * @param count
	 * @return
	 * @throws IOException
	 * @throws InvalidAPIKeyException
	 * @throws ApplicationNotFoundException
	 */
	public AGSAMessage[] getMessages(String type, int start, int count) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException;

	/**
	 * Helper for asynchronous call
	 * 
	 * @see AGSAClient#getMessages(String, int, int)
	 * @param appName
	 * @param responseHandler
	 */
	public void getMessages(String type, int start, int count, AGSAResponseHandler<AGSAMessage[]> responseHandler);
	
	/**
	 * Add a new message to the current application
	 * 
	 * @param type
	 * @param title
	 * @param content
	 * @return
	 * @throws IOException
	 * @throws InvalidAPIKeyException
	 * @throws ApplicationNotFoundException
	 */
	public AGSAMessage createMessage(String type, String title, String content) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException;

	/**
	 * Helper for asynchronous call
	 * 
	 * @see AGSAClient#createMessage(String, String, String)
	 * @param appName
	 * @param responseHandler
	 */
	public void createMessage(String type, String title, String content, AGSAResponseHandler<AGSAMessage> responseHandler);
	
}
