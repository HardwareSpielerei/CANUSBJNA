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
public class AcceptanceCode
{
	/**
	 * Use this AcceptanceCode to get all messages.
	 */
	public static final AcceptanceCode AcceptAll = new AcceptanceCode(
			NativeAccess.CANUSB_ACCEPTANCE_CODE_ALL);

	int acceptanceCode;

	/**
	 * Constructs an AcceptanceCode representing the given binary acceptance
	 * code.
	 * 
	 * @param acceptanceCode
	 *            contains a binary acceptance code.
	 */
	public AcceptanceCode(int acceptanceCode)
	{
		this.acceptanceCode = acceptanceCode;
	}

	protected int getAcceptanceCode()
	{
		return acceptanceCode;
	}
}
