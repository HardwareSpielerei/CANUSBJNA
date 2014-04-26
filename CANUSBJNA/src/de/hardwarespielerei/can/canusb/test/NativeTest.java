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

package de.hardwarespielerei.can.canusb.test;

import java.io.IOException;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;

import de.hardwarespielerei.can.canusb.Version;
import de.hardwarespielerei.can.canusb.jna.NativeAccess;

/**
 * @author gabriel
 * 
 */
public class NativeTest
{

	/**
	 * Lists all known CANUSB adapters using the CANUSB DLL and JNA.
	 * 
	 * @param args
	 *            command line arguments.
	 * @throws IOException
	 *             on errors while accessing CANUSB.
	 */
	public static void main(String[] args) throws IOException
	{
		System.out.println("################################");
		System.out.println("# CANUSB Native Test           #");
		System.out.println("# (C) 2014 by Gabriel Schmidt  #");
		System.out.println("#  @see hardwarespielerei.de   #");
		System.out.println("################################");
		System.out.println();
		System.out.println("CANUSB     V" + Version.VERSION);
		System.out.println("JNA        V" + com.sun.jna.Native.VERSION);
		System.out.println("JNA NATIVE V" + com.sun.jna.Native.VERSION_NATIVE);
		System.out.println();
		if (Platform.isWindows())
		{
			System.out.println("List of known adapters:");

			// get first CANBUS adapter...
			byte[] buffer = new byte[32];
			int numberOfCANUSB = NativeAccess.INSTANCE.canusb_getFirstAdapter(
					buffer, buffer.length);
			String adapterID = Native.toString(buffer);
			for (int i = 0; i < numberOfCANUSB; i++)
			{
				// open channel to CANBUS adapter to get its status and version
				// info...
				NativeLong handle = NativeAccess.INSTANCE.canusb_Open(
						adapterID, "1000",
						NativeAccess.CANUSB_ACCEPTANCE_CODE_ALL,
						NativeAccess.CANUSB_ACCEPTANCE_MASK_ALL,
						NativeAccess.CANUSB_FLAG_TIMESTAMP);
				if (handle.longValue() <= 0)
				{
					throw new IOException("Error while opening CANUSB channel!");
				}
				try
				{
					// get the status...
					int status = NativeAccess.INSTANCE.canusb_Status(handle);

					// get the version info...
					byte[] info = Native
							.toByteArray("VHhFf - Nxxxx - n.n.n - CCCCCCCCCC");
					int rc = NativeAccess.INSTANCE.canusb_VersionInfo(handle,
							info);
					if (rc <= 0)
					{
						throw new IOException(
								"Error while reading VersionInfo from CANUSB!");
					}

					// print list entry...
					System.out.println(i + ". CANUSB-Adapter\tID=" + adapterID
							+ "\tVersionInfo=" + Native.toString(info)
							+ "\tStatus=" + status);
				} finally
				{
					// close channel to CANBUS adapter
					int rc = NativeAccess.INSTANCE.canusb_Close(handle);
					if (rc <= 0)
					{
						throw new IOException(
								"Error while closing CANUSB channel!");
					}
				}

				// get next CANBUS adapter...
				int rc = NativeAccess.INSTANCE.canusb_getNextAdapter(buffer,
						buffer.length);
				if (rc <= 0)
				{
					throw new IOException(
							"Error while listing CANUSB adapters!");
				}
				adapterID = Native.toString(buffer);
			}
			System.out.println("End of list.");
		} else
		{
			System.out.println("CANUSB is not supported on this platform!");
		}
	}
}
