package communication;

import static org.junit.Assert.*;

import org.junit.Test;

import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

public class DecoratorEnhanceDataTests {

	@Test
	public void NetworkDecoratorEnhanceData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		String expectedAddress = "127.0.0.1";
		NetworkPDUDecorator decorator = new NetworkPDUDecorator(expectedAddress, new PDU(expectedData));

		byte[] data = decorator.getEnhancedData();
		assertArrayEquals(expectedData, data);

		String actualAddress = decorator.getIpAddress();
		assertEquals(expectedAddress, actualAddress);
	}

	@Test
	public void TransportDecoratorEnhanceData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		int expectedPort = 12;
		TransportPDUDecorator decorator = new TransportPDUDecorator(expectedPort, new PDU(expectedData));

		byte[] data = decorator.getEnhancedData();
		int port = decorator.getPort();
		assertArrayEquals(expectedData, data);
		assertEquals(expectedPort, port);
	}

	@Test
	public void SessionDecoratorEnhanceData() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedId = new byte[] { 0b00000000 };
		byte[] expectedFlags = new byte[] { 0b00000000 };

		SessionPDUDecorator decorator = new SessionPDUDecorator(new PDU(data));

		byte[] expectedData = new byte[] { expectedId[0], expectedFlags[0], data[0] };
		byte[] actualData = decorator.getEnhancedData();
		assertArrayEquals(expectedData, actualData);
		byte[] sessionId = decorator.getSessionId();
		assertArrayEquals(expectedId, sessionId);
		byte[] flags = decorator.getFlags();
		assertArrayEquals(expectedFlags, flags);
	}

	@Test
	public void PresentationDecoratorEnhanceData() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] { 0b00000000 };

		PresentationPDUDecorator decorator = new PresentationPDUDecorator(new PDU(data));

		byte[] expectedData = new byte[] { expectedFlags[0], data[0] };
		byte[] actualData = decorator.getEnhancedData();

		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void ApplicationDecoratorEnhanceData() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] { 0b00000000 };

		ApplicationPDUDecorator decorator = new ApplicationPDUDecorator(new PDU(data));

		byte[] expectedData = new byte[] { expectedFlags[0], data[0] };
		byte[] actualData = decorator.getEnhancedData();

		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void CombinedDecoratorEnhanceDataTest() {
		byte[] data = new byte[] { 0b01010101 };
		String address = "127.0.0.1";
		int port = 77;

		NetworkPDUDecorator combinedDecorator = new NetworkPDUDecorator(address, new TransportPDUDecorator(port,
				new SessionPDUDecorator(new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(data))))));

		byte[] sessionFlags = new byte[] { 0b00000000 };
		byte[] sessionId = new byte[] { 0b00000000 };
		byte[] presentationFlags = new byte[] { 0b00000000 };
		byte[] applicationFlags = new byte[] { 0b00000000 };
		byte[] expectedData = new byte[] { sessionFlags[0], sessionId[0], presentationFlags[0], applicationFlags[0],
				data[0] };

		byte[] actualData = combinedDecorator.getEnhancedData();

		assertArrayEquals(expectedData, actualData);
	}
}