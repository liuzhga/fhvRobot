/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: NetworkServer.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network;

import java.io.IOException;
import java.net.SocketException;

import communication.commands.Commands;
import communication.flags.Flags;
import controllers.OperatorManager;
import models.Client;
import network.communication.AppCommunication;
import network.communication.Communication;
import network.communication.CommunicationDelegator;
import network.communication.GamingCommunication;
import network.communication.RoboCommunication;

public class NetworkServer {

	// Fields
	private final Communication roboCommunication;
	private final Communication appCommunication;
	private final Communication gamingCommunication;
	private final CommunicationDelegator delegator;
	private final OperatorManager operatorManager;
	private final IClientController<Client> appController;

	// Ports
	private final int roboPort = 998;
	private final int appPort = 997;
	private final int gamingPort = 999;

	// Constructor
	public NetworkServer(IClientController<Client> roboController, IClientController<Client> appController,
			IClientController<Client> gamingController) throws SocketException {

		this.appController = appController;
		this.delegator = new CommunicationDelegator(roboController, appController);

		// Added network sender and receiver which can log
		this.roboCommunication = new RoboCommunication(roboController, roboPort);
		delegator.setChannelA(roboCommunication);

		this.appCommunication = new AppCommunication(appController, delegator, appPort);
		delegator.setChannelB(appCommunication);

		// Gaming communication
		this.gamingCommunication = new GamingCommunication(gamingController, gamingPort);

		// This used for managing the current operator of the robo
		this.operatorManager = new OperatorManager(appController, appCommunication);

		new Thread(roboCommunication).start();
		new Thread(appCommunication).start();
		new Thread(gamingCommunication).start();

		new Thread(operatorManager).start();

	}

	// Methods
	public void sendToRobo(Client client) {
		try {
			roboCommunication.sendToClient(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToRobo(Client client, int flags, int command, byte[] payload) {
		roboCommunication.sendToClient(client, flags, command, payload);
	}

	public void shutdown() {
		roboCommunication.stop();
		appCommunication.stop();
	}

	public void sendToApp(Client client) {
		try {
			appCommunication.sendToClient(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToApp(Client client, int flags, int command, byte[] payload) {
		appCommunication.sendToClient(client, flags, command, payload);
	}

	public void DisconnectedAppClient(Client client) {
		sendToApp(client, Flags.REQUEST_FLAG, Commands.DISCONNECTED, new byte[] { 0 });
	}

	public void DisconnectedRoboClient(Client client) {
		sendToRobo(client, Flags.REQUEST_FLAG, Commands.DISCONNECTED, new byte[] { 0 });
	}

	public IClientController<Client> getAppController() {
		return appController;
	}
}