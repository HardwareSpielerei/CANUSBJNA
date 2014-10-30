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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;

import de.hardwarespielerei.can.canusb.jna.NativeAccess;

/**
 * Use this to iterate over the available CANBUS adapters or to look for an
 * adapter with a given serial number. Using the serial number allows keyless
 * communication in the application using CANUSB.
 * 
 * @author gabriel
 */
public class AdapterIterator implements Iterator<Adapter>
{
	private Bitrate bitrate;
	private int numberOfCANUSB;
	private int indexOfCANUSB;
	private String adapterID;

	/**
	 * Constructs an iterator over the available CANBUS adapters.
	 * 
	 * @param bitrate
	 *            references the bit rate to use while reading the serial number
	 *            from an adapter.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public AdapterIterator(Bitrate bitrate) throws CANUSBException
	{
		this.bitrate = bitrate;
		byte[] buffer = new byte[32];
		this.numberOfCANUSB = Library.call().canusb_getFirstAdapter(
				buffer, buffer.length);
		this.indexOfCANUSB = 0;
		this.adapterID = Native.toString(buffer);
	}

	@Override
	public boolean hasNext()
	{
		return this.indexOfCANUSB < this.numberOfCANUSB;
	}

	@Override
	public Adapter next()
	{
		if (!this.hasNext())
		{
			throw new NoSuchElementException();
		}

		Adapter result;
		String serialNumber;
		try
		{
			// TODO adapt acceptance code, mask and flags
			NativeLong handle = Library.call().canusb_Open(
					this.adapterID, this.bitrate.toString(),
					NativeAccess.CANUSB_ACCEPTANCE_CODE_ALL,
					NativeAccess.CANUSB_ACCEPTANCE_MASK_ALL,
					NativeAccess.CANUSB_FLAG_TIMESTAMP);
			if (0 >= handle.longValue())
			{
				CANUSBException.throwOnErrorCode(handle.intValue(),
						"Error while opening CANUSB channel!");
			}
			try
			{
				byte[] info = Native
						.toByteArray("VHhFf - Nxxxx - n.n.n - CCCCCCCCCC");
				CANUSBException.throwOnErrorCode(
						Library.call().canusb_VersionInfo(handle, info),
						"Error while reading VersionInfo from CANUSB!");
				serialNumber = Native.toString(info).substring(9, 13);
			} finally
			{
				CANUSBException.throwOnErrorCode(
						Library.call().canusb_Close(handle),
						"Error while closing CANUSB channel!");
			}
			result = new Adapter(serialNumber, this.adapterID);
			this.indexOfCANUSB++;

			if (this.hasNext())
			{
				byte[] buffer = new byte[32];
				CANUSBException.throwOnErrorCode(Library.call()
						.canusb_getNextAdapter(buffer, buffer.length),
						"Error while listing CANUSB adapters!");
				this.adapterID = Native.toString(buffer);
			}
		} catch (CANUSBException e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * @param serialNumber
	 *            references the serial number of a CANBUS adapter.
	 * @return the Adapter with the given serial number.
	 * @throws NoSuchElementException
	 *             if the iteration has no (more) adapters with the given serial
	 *             number.
	 */
	public Adapter next(String serialNumber)
	{
		Adapter result = null;
		while (this.hasNext())
		{
			Adapter next = this.next();
			if (next.getSerialNumber().equals(serialNumber))
			{
				result = next;
			}
		}
		if (null == result)
		{
			throw new NoSuchElementException();
		}
		return result;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException(AdapterIterator.class.getName()
				+ " doesn't support remove!");
	}
}
