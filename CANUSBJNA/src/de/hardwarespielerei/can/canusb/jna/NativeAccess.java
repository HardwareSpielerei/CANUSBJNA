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

package de.hardwarespielerei.can.canusb.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

/**
 * Native access to CANUSB dll via JNA.
 * 
 * @author gabriel
 */
public interface NativeAccess extends Library
{
	// Status bits

	/**
	 * CANSTATUS_RECEIVE_FIFO_FULL
	 */
	public static final int CANSTATUS_RECEIVE_FIFO_FULL = 0x01;
	/**
	 * CANSTATUS_TRANSMIT_FIFO_FULL
	 */
	public static final int CANSTATUS_TRANSMIT_FIFO_FULL = 0x02;
	/**
	 * CANSTATUS_ERROR_WARNING
	 */
	public static final int CANSTATUS_ERROR_WARNING = 0x04;
	/**
	 * CANSTATUS_DATA_OVERRUN
	 */
	public static final int CANSTATUS_DATA_OVERRUN = 0x08;
	/**
	 * CANSTATUS_ERROR_PASSIVE
	 */
	public static final int CANSTATUS_ERROR_PASSIVE = 0x20;
	/**
	 * CANSTATUS_ARBITRATION_LOST
	 */
	public static final int CANSTATUS_ARBITRATION_LOST = 0x40;
	/**
	 * CANSTATUS_BUS_ERROR
	 */
	public static final int CANSTATUS_BUS_ERROR = 0x80;

	// Filter mask settings

	/**
	 * Use this acceptance code to get all messages.
	 */
	public static final int CANUSB_ACCEPTANCE_CODE_ALL = 0x00000000;

	/**
	 * Use this acceptance mask to get all messages.
	 */
	public static final int CANUSB_ACCEPTANCE_MASK_ALL = 0xFFFFFFFF;

	// CANMessage flags

	/**
	 * Extended CAN ID.
	 */
	public static final int CANMSG_EXTENDED = 0x80; // Extended CAN id
	/**
	 * Remote frame
	 */
	public static final int CANMSG_RTR = 0x40; // Remote frame

	// Error return codes

	/**
	 * ERROR_CANUSB_OK
	 */
	public static final int ERROR_CANUSB_OK = 1;

	/**
	 * ERROR_CANUSB_GENERAL
	 */
	public static final int ERROR_CANUSB_GENERAL = -1;

	/**
	 * ERROR_CANUSB_OPEN_SUBSYSTEM
	 */
	public static final int ERROR_CANUSB_OPEN_SUBSYSTEM = -2;

	/**
	 * ERROR_CANUSB_COMMAND_SUBSYSTEM
	 */
	public static final int ERROR_CANUSB_COMMAND_SUBSYSTEM = -3;

	/**
	 * ERROR_CANUSB_NOT_OPEN
	 */
	public static final int ERROR_CANUSB_NOT_OPEN = -4;

	/**
	 * ERROR_CANUSB_TX_FIFO_FULL
	 */
	public static final int ERROR_CANUSB_TX_FIFO_FULL = -5;

	/**
	 * ERROR_CANUSB_INVALID_PARAM
	 */
	public static final int ERROR_CANUSB_INVALID_PARAM = -6;

	/**
	 * ERROR_CANUSB_NO_MESSAGE
	 */
	public static final int ERROR_CANUSB_NO_MESSAGE = -7;

	/**
	 * ERROR_CANUSB_MEMORY_ERROR
	 */
	public static final int ERROR_CANUSB_MEMORY_ERROR = -8;

	/**
	 * ERROR_CANUSB_NO_DEVICE
	 */
	public static final int ERROR_CANUSB_NO_DEVICE = -9;

	/**
	 * ERROR_CANUSB_TIMEOUT
	 */
	public static final int ERROR_CANUSB_TIMEOUT = -10;

	/**
	 * ERROR_CANUSB_INVALID_HARDWARE
	 */
	public static final int ERROR_CANUSB_INVALID_HARDWARE = -11;

	// Open flags

	/**
	 * Timestamp messages.
	 */
	public static final int CANUSB_FLAG_TIMESTAMP = 0x0001;

	/**
	 * If input queue is full remove oldest message and insert new message.
	 */
	public static final int CANUSB_FLAG_QUEUE_REPLACE = 0x0002;

	/**
	 * Block receive/transmit.
	 */
	public static final int CANUSB_FLAG_BLOCK = 0x0004;

	/**
	 * Check ACK/NACK's.
	 */
	public static final int CANUSB_FLAG_SLOW = 0x0008;

	/**
	 * Don't send transmitted frames on other local channels for the same
	 * interface.
	 */
	public static final int CANUSB_FLAG_NO_LOCAL_SEND = 0x0010;

	/**
	 * CAN Frame
	 * 
	 * @author gabriel
	 */
	public class CANMsg extends Structure
	{
		/**
		 * message ID
		 */
		public int id;

		/**
		 * timestamp in milliseconds
		 */
		public int timestamp;

		/**
		 * [extended_id|1][RTR:1][reserver:6]
		 */
		public byte flags;

		/**
		 * Frame size (0.8)
		 */
		public byte length;

		/**
		 * Databytes 0..7
		 */
		public byte data[] = new byte[8];

		@Override
		protected List<String> getFieldOrder()
		{
			return Arrays.asList(new String[] { "id", "timestamp", "flags",
					"length", "data" });
		}
	}

	/**
	 * Native CAN frame reference.
	 * 
	 * @author gabriel
	 * @see CANMsg
	 */
	public class CANMsgByReference extends CANMsg implements
			Structure.ByReference
	{
	}

	/**
	 * Interface for native receive call backs.
	 * 
	 * @author gabriel
	 * @see NativeAccess#canusb_setReceiveCallBack(NativeLong,
	 *      NativeReceiveCallback)
	 */
	public interface NativeReceiveCallback extends Callback
	{
		/**
		 * Implement this method to handle received CAN messages.
		 * 
		 * @param msg
		 *            references the received native CAN message.
		 */
		void callback(CANMsgByReference msg);
	}

	/**
	 * Get the first found adapter that is connected to this machine.
	 * 
	 * @param szAdapter
	 *            buffer that will receive the serial number of the first found
	 *            adapter if one is found.
	 * @param size
	 *            size of the buffer to receive the serial number. 32 bytes is a
	 *            good choice.
	 * @return <= 0 on failure. Number of available adapters on success.
	 */
	public int canusb_getFirstAdapter(byte[] szAdapter, int size);

	/**
	 * Get the found adapter(s) in turn that is connected to this machine.
	 * 
	 * Before this call is done a call to canusb_getFirstAdapter must have been
	 * made.
	 * 
	 * If called repeatedly this call will return found adapters in order for
	 * ever.
	 * 
	 * @param szAdapter
	 *            buffer that will receive the serial number of a found adapter
	 *            if one is found.
	 * @param size
	 *            size of the buffer to receive the serial number. 32 bytes is a
	 *            good choice.
	 * @return <= 0 on failure. Positive integer on success.
	 */
	public int canusb_getNextAdapter(byte[] szAdapter, int size);

	/**
	 * Open CAN interface to device.
	 * 
	 * @param szID
	 *            references the serial number for adapter or NULL to open the
	 *            first found.
	 * @param szBitrate
	 *            "10" for 10kbps, "20" for 20kbps, "50" for 50kbps, "100" for
	 *            100kbps, "250" for 250kbps, "500" for 500kbps, "800" for
	 *            800kbps, "1000" for 1Mbps.
	 * @param acceptance_code
	 *            set to CANUSB_ACCEPTANCE_CODE_ALL to get all messages.
	 * @param acceptance_mask
	 *            set to CANUSB_ACCEPTANCE_MASk_ALL to get all messages.
	 * @param flags
	 *            set to CANUSB_FLAG_TIMESTAMP - Timestamp will be set by
	 *            adapter.
	 * @return handle to device if open was successful or zero or negative error
	 *         code on failure.
	 */
	public NativeLong canusb_Open(String szID, String szBitrate,
			int acceptance_code, int acceptance_mask, int flags);

	/**
	 * Close channel with handle h.
	 * 
	 * @param h
	 *            handle of channel.
	 * @return <= 0 on failure. >0 on success.
	 */
	public int canusb_Close(NativeLong h);

	/**
	 * Get adapter status for channel with handle h.
	 * 
	 * @param h
	 *            handle of channel.
	 * @return status bits or {@link NativeAccess#ERROR_CANUSB_TIMEOUT} if
	 *         unable to get status in time. Call canusb_status again if this
	 *         happens. If this method is called very often performance will
	 *         degrade. It is recommended that it is called at most once every
	 *         ten seconds.
	 */
	public int canusb_Status(NativeLong h);

	/**
	 * Get hardware / firmware and driver version for channel with handle h.
	 * 
	 * @param h
	 *            handle of channel.
	 * @param verinfo
	 *            hardware / firmware and driver version.
	 * @return <= 0 on failure. >0 on success.
	 */
	public int canusb_VersionInfo(NativeLong h, byte[] verinfo);

	/**
	 * Read message from channel with handle h.
	 * 
	 * @param h
	 *            handle of channel.
	 * @param msg
	 *            references the message structure.
	 * @return <= 0 on failure. >0 on success. ERROR_CANUSB_NO_MESSAGE is
	 *         returned if there is no message to read. If a callback function
	 *         is defined this call will not work and returns
	 *         ERROR_CANUSB_GENERAL.
	 */
	public int canusb_Read(NativeLong h, CANMsg.ByReference msg);

	/**
	 * Set a receive call back function. Set the callback to NULL to reset it.
	 * It seems to be necessary to keep a Java reference on the callback object.
	 * Otherwise it can be disposed and the callback threads doesn't reach your
	 * callback implementation.
	 * 
	 * @param handle
	 *            handle of channel.
	 * @param fn
	 *            callback to NULL to reset it.
	 * @return <= 0 on failure. >0 for a valid adapter return.
	 */
	public int canusb_setReceiveCallBack(NativeLong handle,
			NativeReceiveCallback fn);

	/**
	 * Write message to channel with handle h.
	 * 
	 * @param h
	 *            handle of channel.
	 * @param msg
	 *            references the message structure.
	 * @return <= 0 on failure. >0 on success.
	 */
	public int canusb_Write(NativeLong h, CANMsg msg);
}
