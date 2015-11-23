/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: ApplicationPDUDecorator.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import communication.utils.NumberParser;

public class ApplicationPDU extends PDUDecorator {

	// Fields
	private byte flags = (byte) 0b00000000;
	private byte commands = (byte) 0b00000000;
	private byte length = (byte) 0b00000000;

	// Constructor
	public ApplicationPDU(PDU data) {
		super(data);

		header = new byte[] { flags, commands, length };
	}

	public ApplicationPDU(int command, PDU pdu) {
		super(pdu);

		// TODO: check size is allowed
		commands = NumberParser.intToByte(command);
		length = NumberParser.intToByte(pdu.data.length);

		header = new byte[] { flags, commands, length };
	}

	// Methods
	public byte getFlags() {
		return flags;
	}

	public int getCommand() {
		return commands;
	}

	public int getPayloadLength() {
		return length;
	}

	public byte[] getPayload() {
		return data;
	}

	@Override
	protected byte[] getEnhanceDataCore(PDU packet) {
		try {

			// Add flag bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(header);
			outputStream.write(data);
			return outputStream.toByteArray();

		} catch (IOException e) {
			return data;
		}
	}

	@Override
	protected byte[] getInnerDataCore(PDU packet) {
		return packet.getInnerData();
	}
}
