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
 * Used to filter messages, e. g. to get all messages.
 * 
 * @author gabriel
 */
public class AcceptanceMask
{
	/**
	 * Use this AcceptanceMask to get all messages.
	 */
	public static final AcceptanceMask AcceptAll = new AcceptanceMask(
			NativeAccess.CANUSB_ACCEPTANCE_MASK_ALL);

	int acceptanceMask;

	/**
	 * Constructs an AcceptanceMask representing the given binary acceptance
	 * mask.
	 * 
	 * @param acceptanceMask
	 *            contains a binary acceptance mask.
	 */
	public AcceptanceMask(int acceptanceMask)
	{
		this.acceptanceMask = acceptanceMask;
	}

	protected int getAcceptanceMask()
	{
		return this.acceptanceMask;
	}
}
