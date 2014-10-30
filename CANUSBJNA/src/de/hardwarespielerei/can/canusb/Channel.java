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

import com.sun.jna.Native;
import com.sun.jna.NativeLong;

import de.hardwarespielerei.can.canusb.jna.NativeAccess.CANMsgByReference;
import de.hardwarespielerei.can.canusb.jna.NativeAccess.NativeReceiveCallback;

/**
 * Represents a channel to a CANBUS adapter.
 * 
 * @author gabriel
 */
public class Channel
{
	private static final byte[] DUMMY_BYTE = new byte[1];

	private class ReceiveCallbackTranslator implements NativeReceiveCallback
	{
		ReceiveCallback callback;

		private ReceiveCallbackTranslator(ReceiveCallback callback)
		{
			this.callback = callback;
		}

		@Override
		public void callback(CANMsgByReference msg)
		{
			this.callback.callback(new NativeMessage(msg));
		}

	}

	private String adapterID;
	private NativeLong handle;
	private NativeReceiveCallback nativeCallBack;

	protected Channel(String adapterID, Bitrate bitrate,
			AcceptanceCode acceptanceCode, AcceptanceMask acceptanceMask,
			Flag[] flags) throws CANUSBException
	{
		this.adapterID = adapterID;
		int nativeFlags = 0;
		for (Flag flag : flags)
		{
			nativeFlags |= flag.getNativeFlag();
		}
		this.handle = Library.call().canusb_Open(adapterID, bitrate.toString(),
				acceptanceCode.getAcceptanceCode(),
				acceptanceMask.getAcceptanceMask(), nativeFlags);
		if (this.handle.longValue() <= 0)
		{
			CANUSBException.throwOnErrorCode(this.handle.intValue(),
					"Can't open channel to adapter " + adapterID + "!");
		}
	}

	/**
	 * Closes this channel.
	 * 
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public void close() throws CANUSBException
	{
		CANUSBException.throwOnErrorCode(
				Library.call().canusb_Close(this.handle),
				"Error while closing channel to adapter " + this.adapterID
						+ "!");
	}

	/**
	 * Get adapter status for this channel. If this method is called very often
	 * performance will degrade. It is recommended that it is called at most
	 * once every ten seconds.
	 * 
	 * @return status.
	 * @throws TimeoutException
	 *             if unable to get status in time. Call canusb_status again if
	 *             this happens.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public Status getStatus() throws CANUSBException
	{
		int status = Library.call().canusb_Status(this.handle);
		if (status < 0)
		{
			CANUSBException.throwOnErrorCode(status,
					"Can't get status from adapter " + this.adapterID + "!");
		}
		return new Status(status);
	}

	/**
	 * Get hardware / firmware and driver version for this channel.
	 * 
	 * @return hardware / firmware and driver version.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public String getVersionInfo() throws CANUSBException
	{
		byte[] buffer = Native
				.toByteArray("VHhFf - Nxxxx - n.n.n - CCCCCCCCCC");
		CANUSBException.throwOnErrorCode(
				Library.call().canusb_VersionInfo(this.handle, buffer),
				"Can't get version info from adapter " + this.adapterID + "!");
		return Native.toString(buffer);
	}

	/**
	 * Read message from this channel.
	 * 
	 * @return next message.
	 * @throws NoMessageException
	 *             if there is no message to read.
	 * @throws CANUSBException
	 *             if a callback function is defined and on all other CANUSB
	 *             errors.
	 */
	public CANMessage read() throws CANUSBException
	{
		CANMessage msg = null;
		CANMsgByReference nativeMsg = new CANMsgByReference();
		int status = Library.call().canusb_Read(this.handle, nativeMsg);
		if (status > 0)
		{
			msg = new NativeMessage(nativeMsg);
		} else if (status < 0)
		{
			CANUSBException.throwOnErrorCode(status, "Can't read from adapter "
					+ this.adapterID + "!");
		}

		return msg;
	}

	/**
	 * Set a receive call back. Set the callback to NULL to reset it. This
	 * channel will keep a Java reference on the native callback object to avoid
	 * it from being disposed.
	 * 
	 * @param callBack
	 *            references the callback to set or NULL to reset it.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public void setReceiveCallBack(ReceiveCallback callBack)
			throws CANUSBException
	{
		NativeReceiveCallback nextNativeCallBack = null;
		if (null != callBack)
		{
			nextNativeCallBack = new ReceiveCallbackTranslator(callBack);
		}
		CANUSBException.throwOnErrorCode(Library.call()
				.canusb_setReceiveCallBack(this.handle, nextNativeCallBack),
				"Can't set receive call back to " + callBack + " on adapter "
						+ this.adapterID + "!");
		// success - store in member variable...
		this.nativeCallBack = nextNativeCallBack;
	}

	/**
	 * Write message to this channel.
	 * 
	 * @param msg
	 *            references the message structure.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public void write(CANMessage msg) throws CANUSBException
	{
		NativeMessage nativeMsg;
		if (msg instanceof NativeMessage)
		{
			nativeMsg = (NativeMessage) msg;
		} else
		{
			nativeMsg = new NativeMessage(msg.getId(), msg.getTimestamp(),
					msg.isExtendedID(), msg.isRemoteFrame(), msg.getReserver(),
					msg.getLength(),
					// Workaround: CANUSB doesn't like arrays of length zero!
					(0 == msg.getLength() ? DUMMY_BYTE : msg.getData()));
		}
		CANUSBException.throwOnErrorCode(
				Library.call().canusb_Write(this.handle,
						nativeMsg.getNativeMessage()),
				"Can't write to adapter " + this.adapterID + "!");
	}

	/**
	 * @return true if a receive call back was set, false otherwise.
	 */
	public boolean isReceiveCallBackSet()
	{
		return null != this.nativeCallBack;
	}

	@Override
	public String toString()
	{
		return ("Channel " + this.handle.longValue() + " to adapter " + this.adapterID);
	}
}
