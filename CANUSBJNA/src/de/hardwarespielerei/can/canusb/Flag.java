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
 * Used to configure a CANUSB channel.
 * 
 * @author gabriel
 */
public enum Flag
{
	/**
	 * Timestamp will be set by adapter. If not set timestamp will be set by the
	 * driver.
	 */
	Timestamp(NativeAccess.CANUSB_FLAG_TIMESTAMP, "CANUSB_FLAG_TIMESTAMP"),

	/**
	 * Normally when the input queue is full new messages received are
	 * disregarded by setting this flag the first message is the queue is
	 * removed to make room for the new message. This flag is useful when using
	 * the ReadFirst method.
	 */
	QueueReplace(NativeAccess.CANUSB_FLAG_QUEUE_REPLACE,
			"CANUSB_FLAG_QUEUE_REPLACE"),

	/**
	 * Can be set to make LOOP and Writes blocking. Default is an infinite block
	 * but the timeout can be changed with canusb_SetTimeouts. Note that
	 * ReadFirst and ReadFirstEx never block.
	 */
	Block(NativeAccess.CANUSB_FLAG_BLOCK, "CANUSB_FLAG_BLOCK"),

	/**
	 * This flag can be used at slower transmission speeds where the data stream
	 * still can be high. Every effort is made to transfer the frame even if the
	 * adapter reports that the internal buffer is full. Normally a frame is
	 * trashed if the hardware buffer becomes full to promote speed.
	 */
	Slow(NativeAccess.CANUSB_FLAG_SLOW, "CANUSB_FLAG_SLOW"),

	/**
	 * Normally when several channels has been opened on the same physical
	 * adapter a send of a frame on one channel will be seen on the other
	 * channels. By setting this flag this is prevented.
	 */
	NoLocalSende(NativeAccess.CANUSB_FLAG_NO_LOCAL_SEND,
			"CANUSB_FLAG_NO_LOCAL_SEND");

	private int nativeFlag;
	private String description;

	private Flag(int nativeFlag, String description)
	{
		this.nativeFlag = nativeFlag;
	}

	protected int getNativeFlag()
	{
		return this.nativeFlag;
	}

	@Override
	public String toString()
	{
		return this.description;
	}

}
