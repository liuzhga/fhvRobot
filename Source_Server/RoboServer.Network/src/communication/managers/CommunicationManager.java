package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClient;
import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

@Singleton
public class CommunicationManager {

	private NetworkManager networkManager;
	private TransportManager transportManager;
	private SessionManager sessionManager;
	private PresentationManager presentationManager;
	private ApplicationManager applicationManager;

	@Inject
	public CommunicationManager(NetworkManager networkManager, TransportManager transportManager,
			SessionManager sessionManager, PresentationManager presentationManager, ApplicationManager applicationManager) {
		this.networkManager = networkManager;
		this.transportManager = transportManager;
		this.sessionManager = sessionManager;
		this.presentationManager = presentationManager;
		this.applicationManager = applicationManager;
	}

	public void addClient(IClient client) {
		networkManager.addClient(client);
		transportManager.addClient(client);
		sessionManager.addClient(client);
		presentationManager.addClient(client);
		applicationManager.addClient(client);
	}

	public void removeClient(IClient client) {
		networkManager.removeClient(client);
		transportManager.removeClient(client);
		sessionManager.removeClient(client);
		presentationManager.removeClient(client);
		applicationManager.removeClient(client);
	}

	public InetAddress getIpAddress(IClient client) {
		return networkManager.getValue(client);
	}

	public void setIpAddress(IClient client, InetAddress ipAddress) {
		networkManager.setValueOfClient(client, ipAddress);
	}

	public int getPort(IClient client) {
		return transportManager.getValue(client);
	}

	public void setPort(IClient client, int port) {
		transportManager.setValueOfClient(client, port);
	}

	public int getSession(IClient client) {
		return sessionManager.getSession(client);
	}

	private PDU createPDU(IClient client, String message) {
		return new NetworkPDUDecorator(getIpAddress(client), new TransportPDUDecorator(getPort(client),
				new SessionPDUDecorator(new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(message))))));
	}

	public DatagramPacket createDatagramPacket(IClient client, String message) {
		// Create PDU
		PDU pdu = createPDU(client, message);
		byte[] sendData = pdu.getEnhancedData();
		int length = sendData.length;

		return new DatagramPacket(sendData, length, getIpAddress(client), getPort(client));
	}
	
	public String readDatagramPacket(IClient client, DatagramPacket packet) {
		String message = new String(packet.getData());
		PDU pdu = createPDU(client, message);
		
		return new String(pdu.getInnerData());
	}
}
