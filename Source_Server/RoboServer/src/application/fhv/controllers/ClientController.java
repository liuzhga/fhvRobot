package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communication.configurations.IConfiguration;
import communication.heartbeat.HeartbeatManager;
import communication.heartbeat.IHeartbeatHandler;
import controllers.factory.IClientFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Client;
import models.IExtendedConfiguration;
import network.IClientController;

public class ClientController<T extends IExtendedConfiguration> implements IClientController<T>, IHeartbeatHandler<T> {

	public interface IGlobalClientDisconnectListener<T extends IExtendedConfiguration> {
		void clientDisconnected(T client);
	}
	
	public interface ICommandListener<T extends IExtendedConfiguration> {
		void commandReceived(T client, int command, byte[] payload);
	}

	public interface IOperatorChangedListener<T extends IExtendedConfiguration> {
		void handleOperatorChanged(T operator);
	}
	
	// Static fields
	private static List<IGlobalClientDisconnectListener<IExtendedConfiguration>> globalListeners = new ArrayList<>();

	// Fields
	private final ObservableList<T> clients;
	private final IClientFactory<T> factory;
	private final Map<Integer, List<ICommandListener<T>>> commandListeners;

	private final HashMap<T, HeartbeatManager<T>> clientTimers;

	private T selectedClient;
	private List<IOperatorChangedListener<T>> operatorListeners;

	// Constructor
	public ClientController(IClientFactory<T> factory) {
		this.clients = FXCollections.observableArrayList();
		this.clientTimers = new HashMap<>();
		this.commandListeners = new HashMap<>();
		this.operatorListeners = new ArrayList<>();
		this.factory = factory;
	}

	// Methods
	public void setOperator(T operator) {
		if (operator == null) {
			return;
		}

		operator.setIsOperator(true);
		handleOperatorChanged(operator);
	}

	public void releaseOperator(T operator) {
		if (operator == null) {
			return;
		}

		operator.setIsOperator(false);
		handleOperatorChanged(operator);
	}

	public void releaseAllOperators() {
		for (T operator : clients) {
			if (operator.getIsOperator()) {
				operator.setIsOperator(false);
				handleOperatorChanged(operator);
			}
		}
	}

	public List<T> getOperators() {
		List<T> operators = new ArrayList<>();

		for (T client : clients) {
			if (client.getIsOperator()) {
				operators.add(client);
			}
		}

		return operators;
	}

	@Override
	public void addClient(T client) {
		if (client == null) {
			return;
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				clients.add(client);
			}
		});

		// Let the HeartbeatManager check the heartbeat in a specified interval
		HeartbeatManager<T> manager = new HeartbeatManager<T>(client, this);
		manager.run();

		clientTimers.put(client, manager);
	}

	@Override
	public void removeClient(T client) {
		if (client == null) {
			return;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				clients.remove(client);
				clientTimers.remove(client);
			}
		});
		
		ClientController.informGlobalDisconnectListeners(client);
	}

	@Override
	public ObservableList<T> getClients() {
		return clients;
	}

	@Override
	public IExtendedConfiguration createConfiguration() {
		T client = factory.create();
		addClient(client);
		return client;
	}

	@Override
	public List<IConfiguration> getConfigurations() {
		return new ArrayList<>(clients);
	}

	public T getSelectedClient() {
		return selectedClient;
	}

	public void setSelectedClient(T selectedClient) {
		this.selectedClient = selectedClient;
	}

	@Override
	public void handleNoHeartbeat(T client) {
		Platform.runLater(() -> {
			removeClient(client);
		});
		System.out.println("Missing heart beat, client disconnected: [" + client.getSessionId() + "]");
	}

	@Override
	public void handleHeartbeat(T client) {
		client.cleanHeartBeatCount();
	}

	public void addOperatorChangedListener(IOperatorChangedListener<T> operatorListener) {
		if (operatorListener != null) {
			operatorListeners.add(operatorListener);
		}
	}

	public void handleOperatorChanged(T operator) {
		operatorListeners.forEach(listener -> listener.handleOperatorChanged(operator));
	}

	public void addCommandListener(ICommandListener<T> commandListener, int command) {
		List<ClientController.ICommandListener<T>> listeners = commandListeners.get(command);

		if (listeners == null) {
			listeners = new ArrayList<>();
			commandListeners.put(command, listeners);
		}

		listeners.add(commandListener);
	}

	public void handleCommandReceived(T client, int command, byte[] payload) {
		List<ClientController.ICommandListener<T>> listeners = commandListeners.get(command);

		if (listeners != null) {
			for (ClientController.ICommandListener<T> l : listeners) {
				l.commandReceived(client, command, payload);
			}
		}
	}

	public static void registerGlobalClientDisconnectListener(IGlobalClientDisconnectListener<IExtendedConfiguration> listener) {
		globalListeners.add(listener);
	}
	

	public static void informGlobalDisconnectListeners(IExtendedConfiguration client) {
		for (IGlobalClientDisconnectListener<IExtendedConfiguration> l : globalListeners) {
			l.clientDisconnected(client);
		}
	}
}
