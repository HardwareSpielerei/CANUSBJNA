/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Gabriel Schmidt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hardwarespielerei.can.canusb;

import java.util.Date;

import de.hardwarespielerei.can.canusb.jna.NativeAccess;
import de.hardwarespielerei.can.canusb.jna.NativeAccess.CANMsg;

/**
 * CAN frame.
 * 
 * @author gabriel
 */
public class NativeMessage implements CANMessage
{
	private CANMsg msg;

	/**
	 * Constructs a CAN frame.
	 * 
	 * @param id
	 *            contains the message ID.
	 * @param timestamp
	 *            contains the timestamp in milliseconds.
	 * @param extendedID
	 *            contains the extended ID flag.
	 * @param remoteFrame
	 *            contains the remote frame flag.
	 * @param reserver
	 *            contains the reserver.
	 * @param length
	 *            contains the frame size.
	 * @param data
	 *            contains data bytes.
	 * @throws IllegalArgumentException
	 *             is the reserver has more then 6 bits or the data is more than
	 *             8 bytes long.
	 */
	public NativeMessage(int id, int timestamp, boolean extendedID,
			boolean remoteFrame, byte reserver, byte length, byte data[])
	{
		if (0 != (reserver & 0xc0))
		{
			throw new IllegalArgumentException("Reserver has more then 6 bits!");
		}
		if (data.length > 8)
		{
			throw new IllegalArgumentException(
					"Data is more than 8 bytes long!");
		}
		this.msg = new CANMsg();
		this.msg.id = id;
		this.msg.timestamp = timestamp;
		this.msg.flags = (byte) (reserver
				| (extendedID ? NativeAccess.CANMSG_EXTENDED : 0) | (remoteFrame ? NativeAccess.CANMSG_RTR
				: 0));
		this.msg.length = length;
		this.msg.data = data;
	}

	protected NativeMessage(CANMsg msg)
	{
		this.msg = msg;
	}

	protected CANMsg getNativeMessage()
	{
		return this.msg;
	}

	/**
	 * @return message ID.
	 */
	public int getId()
	{
		return this.msg.id;
	}

	/**
	 * @return timestamp in milliseconds.
	 */
	public int getTimestamp()
	{
		return this.msg.timestamp;
	}

	/**
	 * @return true if this message has an extended ID, false otherwise.
	 */
	public boolean isExtendedID()
	{
		return NativeAccess.CANMSG_EXTENDED == (this.msg.flags & NativeAccess.CANMSG_EXTENDED);
	}

	/**
	 * @return true if this message is a remote frame, false otherwise.
	 */
	public boolean isRemoteFrame()
	{
		return NativeAccess.CANMSG_RTR == (this.msg.flags & NativeAccess.CANMSG_RTR);
	}

	/**
	 * @return the reserver in the 6 LSB.
	 */
	public byte getReserver()
	{
		return (byte) (this.msg.flags & 0x3F);
	}

	/**
	 * @return the frame size.
	 */
	public byte getLength()
	{
		return this.msg.length;
	}

	/**
	 * @return the data bytes. If the array contains more bytes then the
	 *         specified frame size, the redundant bytes must be ignored.
	 */
	public byte[] getData()
	{
		return this.msg.data;
	}

	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("ID:          0x").append(toHexString(this.getId()))
				.append("\t").append(toBinaryString(this.getId())).append("\n");
		result.append("Timestamp:   0x")
				.append(toHexString(this.getTimestamp())).append("\t")
				.append(new Date(this.getTimestamp())).append("\n");
		result.append("ExtendedID:  " + Boolean.toString(this.isExtendedID()))
				.append("\n");
		result.append("RemoteFrame: ")
				.append(Boolean.toString(this.isRemoteFrame())).append("\n");
		result.append("Reserver:    0x")
				.append(toHexString(this.getReserver())).append("\n");
		result.append("Length:      ").append(this.getLength()).append("\t0x")
				.append(toHexString(this.getLength())).append("\n");
		String dataPrefix = "Data:\t";
		for (byte data : this.getData())
		{
			result.append(dataPrefix).append(data).append("\t0x")
					.append(toHexString(data)).append("\t")
					.append(toBinaryString(data)).append("\n");
			dataPrefix = "\t";
		}
		return result.toString();
	}

	private static String toHexString(int value, int len)
	{
		String hexString = Integer.toHexString(value);
		if (hexString.length() > len)
		{
			hexString = hexString.substring(hexString.length() - len,
					hexString.length());
		}
		StringBuffer result = new StringBuffer(hexString);
		while (result.length() < len)
		{
			result.insert(0, '0');
		}
		return result.toString();
	}

	private static String toHexString(int value)
	{
		return toHexString(value, 8);
	}

	private static String toHexString(byte value)
	{
		return toHexString(value, 2);
	}

	private static String toBinaryString(int value, int len)
	{
		String hexString = Integer.toBinaryString(value);
		if (hexString.length() > len)
		{
			hexString = hexString.substring(hexString.length() - len,
					hexString.length());
		}
		StringBuffer result = new StringBuffer(hexString);
		while (result.length() < len)
		{
			result.insert(0, '0');
		}
		return result.toString();
	}

	private static String toBinaryString(int value)
	{
		return toBinaryString(value, 32);
	}

	private static String toBinaryString(byte value)
	{
		return toBinaryString(value, 8);
	}
}
