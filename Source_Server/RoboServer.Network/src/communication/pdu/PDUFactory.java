package communication.pdu;

import java.util.Arrays;

import communication.utils.NumberParser;

public class PDUFactory {

	public static NetworkPDU createNetworkPDU(byte[] data) {
		return new NetworkPDU(new PDU(data));
	}

	public static TransportPDU createTransportPDU(byte[] data) {
		return new TransportPDU(new PDU(data));
	}

	public static SessionPDU createSessionPDU(byte[] data) {
		// TODO: check data length
		int flags = NumberParser.intToByte(data[0]);
		int sessionId = NumberParser.intToByte(data[1]);
		byte[] newData = Arrays.copyOfRange(data, 2, data.length);

		return new SessionPDU(flags, sessionId, new PDU(newData));
	}

	public static PresentationPDU createPresentationPDU(byte[] data) {
		int flags = NumberParser.intToByte(data[0]);
		byte[] newData = Arrays.copyOfRange(data, 1, data.length);

		return new PresentationPDU(flags, new PDU(newData));
	}

	public static ApplicationPDU createApplicationPDU(byte[] data) {
		int flags = NumberParser.intToByte(data[0]);
		int command = NumberParser.intToByte(data[1]);
		int length = NumberParser.intToByte(data[2]);

		// TODO: compare length and newData length
		byte[] newData = Arrays.copyOfRange(data, 3, data.length);
		return new ApplicationPDU(command, new PDU(newData));
	}
}
