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
 * CAN frame.
 * 
 * @author gabriel
 */
public interface CANMessage
{
	/**
	 * @return message ID.
	 */
	public int getId();

	/**
	 * @return timestamp in milliseconds.
	 */
	public int getTimestamp();

	/**
	 * @return true if this message has an extended ID, false otherwise.
	 */
	public boolean isExtendedID();

	/**
	 * @return true if this message is a remote frame, false otherwise.
	 */
	public boolean isRemoteFrame();

	/**
	 * @return the reserver in the 6 LSB.
	 */
	public byte getReserver();

	/**
	 * @return the frame size.
	 */
	public byte getLength();

	/**
	 * @return the data bytes. If the array contains more bytes then the
	 *         specified frame size, the redundant bytes must be ignored.
	 */
	public byte[] getData();
}
