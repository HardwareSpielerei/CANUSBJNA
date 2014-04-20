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

/**
 * Represents a CANBUS adapter. Use {@link AdapterIterator} to iterate over the
 * available adapters or to look for an adapter with a given serial number.
 * 
 * @author gabriel
 */
public class Adapter
{
	private String serialNumber;
	private String adapterID;

	protected Adapter(String serialNumber, String adapterID)
	{
		this.serialNumber = serialNumber;
		this.adapterID = adapterID;
	}

	/**
	 * @return serial number of this adapter.
	 */
	public String getSerialNumber()
	{
		return serialNumber;
	}

	/**
	 * @return ID of this adapter.
	 */
	protected String getAdapterID()
	{
		return adapterID;
	}

	/**
	 * Open a channel to this adapter. You can open several channels to the same
	 * adapter by calling this method several times. A virtual channel is opened
	 * for each call. When doing this it is important to remember to close the
	 * interface the same number of times as it was opened. The last close
	 * terminates the physical connection. There is therefore no need to close
	 * the virtual channels in a specific order.
	 * 
	 * The adapter ID is taken from this adapter and can not be specified.
	 * 
	 * Bitrate, acceptance code, acceptance mask and flags don't not have any
	 * meaning except for the first open.
	 * 
	 * @param bitrate
	 *            references the bit rate used on this channel.
	 * @param acceptanceCode
	 *            references the acceptance code.
	 * @param acceptanceMask
	 *            references the acceptance mask.
	 * @param flags
	 *            references the flags.
	 * @return channel.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public Channel openChannel(Bitrate bitrate, AcceptanceCode acceptanceCode,
			AcceptanceMask acceptanceMask, Flag[] flags) throws CANUSBException
	{
		return new Channel(this.adapterID, bitrate, acceptanceCode,
				acceptanceMask, flags);

	}

	@Override
	public String toString()
	{
		return ("CANUSB # " + this.serialNumber);
	}
}
