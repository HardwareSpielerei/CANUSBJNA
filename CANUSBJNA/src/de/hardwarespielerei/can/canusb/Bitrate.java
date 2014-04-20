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
 * Bit rates available for CANUSB usage.
 * 
 * @author gabriel
 */
public enum Bitrate
{
	/**
	 * 10 kbps
	 */
	Bitrate10kbps("10"),

	/**
	 * 20 kbps
	 */
	Bitrate20kbps("20"),

	/**
	 * 50 kbps
	 */
	Bitrate50kbps("50"),

	/**
	 * 100 kbps
	 */
	Bitrate100kbps("100"),

	/**
	 * 250 kbps
	 */
	Bitrate250kbps("250"),

	/**
	 * 500 kbps
	 */
	Bitrate500kbps("500"),

	/**
	 * 800 kbps
	 */
	Bitrate800kbps("800"),

	/**
	 * 1 Mbps
	 */
	Bitrate1Mbps("1000");

	private String code;

	private Bitrate(String code)
	{
		this.code = code;
	}

	@Override
	public String toString()
	{
		return this.code;
	}
}
