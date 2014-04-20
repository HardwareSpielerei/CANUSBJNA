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
 * Describes an {@link NativeAccess#ERROR_CANUSB_GENERAL} error from CANUSB.
 * Subclasses describe specific errors.
 * 
 * @author gabriel
 */
public class CANUSBException extends java.lang.Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8335621053024358913L;

	/**
	 * @param returnCode
	 *            contains the native return code.
	 * @param message
	 *            references the detail message. The detail message is saved for
	 *            later retrieval by the {@link Throwable#getMessage()} method.
	 */
	protected CANUSBException(int returnCode, String message)
	{
		super(message + " RC = " + returnCode);
	}

	/**
	 * Throws the CANUSB exception corresponding to the given native return
	 * code. Returns without any effect on {@link NativeAccess#ERROR_CANUSB_OK}
	 * return code.
	 * 
	 * @param rc
	 *            contains the native return code.
	 * @param message
	 *            references the detail message. The detail message is saved for
	 *            later retrieval by the {@link Throwable#getMessage()} method.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	protected static void throwOnErrorCode(int rc, String message)
			throws CANUSBException
	{
		switch (rc)
		{
			case NativeAccess.ERROR_CANUSB_OK:
				// everything OK, do nothing...
				break;
			case NativeAccess.ERROR_CANUSB_GENERAL:
				throw new CANUSBException(rc, message);
			case NativeAccess.ERROR_CANUSB_OPEN_SUBSYSTEM:
				throw new OpenSubsystemException(rc, message);
			case NativeAccess.ERROR_CANUSB_COMMAND_SUBSYSTEM:
				throw new CommandSubsystemException(rc, message);
			case NativeAccess.ERROR_CANUSB_NOT_OPEN:
				throw new NotOpenException(rc, message);
			case NativeAccess.ERROR_CANUSB_TX_FIFO_FULL:
				throw new TransmitFifoFullException(rc, message);
			case NativeAccess.ERROR_CANUSB_INVALID_PARAM:
				throw new InvalidParamException(rc, message);
			case NativeAccess.ERROR_CANUSB_NO_MESSAGE:
				throw new NoMessageException(rc, message);
			case NativeAccess.ERROR_CANUSB_MEMORY_ERROR:
				throw new MemoryErrorException(rc, message);
			case NativeAccess.ERROR_CANUSB_NO_DEVICE:
				throw new NoDeviceException(rc, message);
			case NativeAccess.ERROR_CANUSB_TIMEOUT:
				throw new TimeoutException(rc, message);
			case NativeAccess.ERROR_CANUSB_INVALID_HARDWARE:
				throw new InvalidHardwareException(rc, message);
			default:
				throw new CANUSBException(rc, message);
		}
	}
}
