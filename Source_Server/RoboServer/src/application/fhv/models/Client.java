/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: Client.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package models;

import communication.IConfiguration;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client implements Comparable<Client>, IConfiguration {

	// fields
	private IntegerProperty id;
	private StringProperty name;
	private StringProperty sendData;
	private StringProperty receiveData;
	private StringProperty ipAddress;
	private IntegerProperty port;
	private IntegerProperty sessionId;

	// constructor
	public Client() {
		id = new SimpleIntegerProperty();
		name = new SimpleStringProperty();
		sendData = new SimpleStringProperty();
		receiveData = new SimpleStringProperty();
		ipAddress = new SimpleStringProperty();
		port = new SimpleIntegerProperty();
		sessionId = new SimpleIntegerProperty();

		setName("Anonymous");
	}

	// getter and setter for the properties
	public final int getId() {
		return id.get();
	}

	public final void setId(int value) {
		id.set(value);
	}

	public IntegerProperty IdProperty() {
		return id;
	}

	public final String getName() {
		return name.get();
	}

	public final void setName(String value) {
		name.set(value);
	}

	public StringProperty NameProperty() {
		return name;
	}

	public final String getSendData() {
		return sendData.get();
	}

	public final void setSendData(String value) {
		sendData.set(value);
	}

	public StringProperty SendDataProperty() {
		return sendData;
	}

	public final String getReceiveData() {
		return receiveData.get();
	}

	public final void setReceiveData(String value) {
		receiveData.set(value);
	}

	public StringProperty ReceiveDataProperty() {
		return receiveData;
	}

	public final String getIpAddress() {
		return ipAddress.get();
	}

	public final void setIpAddress(String value) {
		ipAddress.set(value);
	}

	public StringProperty IpAddressProperty() {
		return ipAddress;
	}

	public final int getPort() {
		return port.get();
	}

	public final void setPort(int value) {
		port.set(value);
	}

	public IntegerProperty PortProperty() {
		return port;
	}

	public final int getSessionId() {
		return sessionId.get();
	}

	public final void setSessionId(int value) {
		sessionId.set(value);
	}

	public IntegerProperty SessionIdProperty() {
		return sessionId;
	}

	@Override
	public int compareTo(Client o) {
		if (o == null) {
			return -1;
		}

		return getIpAddress().compareTo(o.getIpAddress());
	}
}
