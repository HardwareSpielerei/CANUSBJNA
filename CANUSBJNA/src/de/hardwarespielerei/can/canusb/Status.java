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

import de.hardwarespielerei.can.canusb.jna.NativeAccess;

/**
 * CANUSB status.
 * 
 * @author gabriel
 */
public class Status
{
	private int status;

	/**
	 * Constructs a CANUSB status from the native status.
	 * 
	 * @param status
	 *            native CANUSB status.
	 * @see Channel#getStatus()
	 */
	protected Status(int status)
	{
		this.status = status;
	}

	/**
	 * @return true if CAN status is OK, false otherwise.
	 */
	public boolean isOK()
	{
		return (0 == this.status);
	}

	/**
	 * @return true if CAN status is RECEIVE FIFO FULL, false otherwise.
	 */
	public boolean isReceiveFifoFull()
	{
		return (this.status & NativeAccess.CANSTATUS_RECEIVE_FIFO_FULL) == NativeAccess.CANSTATUS_RECEIVE_FIFO_FULL;
	}

	/**
	 * @return true if CAN status is TRANSMIT FIFO FULL, false otherwise.
	 */
	public boolean isTransmitFifoFull()
	{
		return (this.status & NativeAccess.CANSTATUS_TRANSMIT_FIFO_FULL) == NativeAccess.CANSTATUS_TRANSMIT_FIFO_FULL;
	}

	/**
	 * @return true if CAN status is ERROR WARNING, false otherwise.
	 */
	public boolean isErrorWarning()
	{
		return (this.status & NativeAccess.CANSTATUS_ERROR_WARNING) == NativeAccess.CANSTATUS_ERROR_WARNING;
	}

	/**
	 * @return true if CAN status is DATA OVERRUN, false otherwise.
	 */
	public boolean isDataOverrun()
	{
		return (this.status & NativeAccess.CANSTATUS_DATA_OVERRUN) == NativeAccess.CANSTATUS_DATA_OVERRUN;
	}

	/**
	 * @return true if CAN status is ERROR PASSIVE, false otherwise.
	 */
	public boolean isErrorPassive()
	{
		return (this.status & NativeAccess.CANSTATUS_ERROR_PASSIVE) == NativeAccess.CANSTATUS_ERROR_PASSIVE;
	}

	/**
	 * @return true if CAN status is ARBITRATION LOST, false otherwise.
	 */
	public boolean isArbitrationLost()
	{
		return (this.status & NativeAccess.CANSTATUS_ARBITRATION_LOST) == NativeAccess.CANSTATUS_ARBITRATION_LOST;
	}

	/**
	 * @return true if CAN status is BUS ERROR, false otherwise.
	 */
	public boolean isBusError()
	{
		return (this.status & NativeAccess.CANSTATUS_BUS_ERROR) == NativeAccess.CANSTATUS_BUS_ERROR;
	}

	@Override
	public String toString()
	{
		return (this.isOK() ? "OK, " : "")
				+ (this.isReceiveFifoFull() ? "CANSTATUS_RECEIVE_FIFO_FULL, "
						: "")
				+ (this.isTransmitFifoFull() ? "CANSTATUS_TRANSMIT_FIFO_FULL, "
						: "")
				+ (this.isErrorWarning() ? "CANSTATUS_ERROR_WARNING, " : "")
				+ (this.isDataOverrun() ? "CANSTATUS_DATA_OVERRUN, " : "")
				+ (this.isErrorPassive() ? "CANSTATUS_ERROR_PASSIVE, " : "")
				+ (this.isArbitrationLost() ? "CANSTATUS_ARBITRATION_LOST, "
						: "")
				+ (this.isBusError() ? "CANSTATUS_BUS_ERROR, " : "")
				+ (0 == (this.status & ~(NativeAccess.CANSTATUS_RECEIVE_FIFO_FULL
						| NativeAccess.CANSTATUS_TRANSMIT_FIFO_FULL
						| NativeAccess.CANSTATUS_ERROR_WARNING
						| NativeAccess.CANSTATUS_DATA_OVERRUN
						| NativeAccess.CANSTATUS_ERROR_PASSIVE
						| NativeAccess.CANSTATUS_ARBITRATION_LOST | NativeAccess.CANSTATUS_BUS_ERROR)) ? ""
						: "unknown error(s), ") + "0x"
				+ Integer.toHexString(this.status);
	}
}