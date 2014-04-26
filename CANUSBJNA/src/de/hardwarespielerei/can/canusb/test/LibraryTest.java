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

import com.sun.jna.Platform;

import de.hardwarespielerei.can.canusb.AcceptanceCode;
import de.hardwarespielerei.can.canusb.AcceptanceMask;
import de.hardwarespielerei.can.canusb.Adapter;
import de.hardwarespielerei.can.canusb.AdapterIterator;
import de.hardwarespielerei.can.canusb.Bitrate;
import de.hardwarespielerei.can.canusb.CANUSBException;
import de.hardwarespielerei.can.canusb.Channel;
import de.hardwarespielerei.can.canusb.Flag;
import de.hardwarespielerei.can.canusb.Status;
import de.hardwarespielerei.can.canusb.Version;

/**
 * Some basic test using the CANUSB library.
 * 
 * @author gabriel
 */
public class LibraryTest
{

	/**
	 * Lists all known CANUSB adapters using the Java Library.
	 * 
	 * @param args
	 *            references command line arguments.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public static void main(String[] args) throws CANUSBException
	{
		System.out.println("###############################");
		System.out.println("# CANUSB Library Test         #");
		System.out.println("# (C) 2014 by Gabriel Schmidt #");
		System.out.println("#  @see hardwarespielerei.de  #");
		System.out.println("###############################");
		System.out.println();
		System.out.println("CANUSB     V" + Version.VERSION);
		System.out.println("JNA        V" + com.sun.jna.Native.VERSION);
		System.out.println("JNA NATIVE V" + com.sun.jna.Native.VERSION_NATIVE);
		System.out.println();
		if (Platform.isWindows())
		{
			System.out.println("List of known adapters:");

			// iterate over the CANBUS adapter...
			AdapterIterator adapters = new AdapterIterator(Bitrate.Bitrate1Mbps);
			while (adapters.hasNext())
			{
				Adapter adapter = adapters.next();

				// open channel to CANBUS adapter to get its status and version
				// info...
				Status status;
				String versionInfo;
				Flag[] flags = { Flag.Timestamp };
				Channel channel = adapter.openChannel(Bitrate.Bitrate1Mbps,
						AcceptanceCode.AcceptAll, AcceptanceMask.AcceptAll,
						flags);
				try
				{
					// get the status...
					status = channel.getStatus();

					// get the version info...
					versionInfo = channel.getVersionInfo();

					// print list entry...
					System.out.println("CANUSB # " + adapter.getSerialNumber()
							+ "\tVersionInfo=" + versionInfo + "\tStatus="
							+ status);
				} finally
				{
					// close channel to CANBUS adapter
					channel.close();
				}
			}
			System.out.println("End of list.");
		} else
		{
			System.out.println("CANUSB is not supported on this platform!");
		}
	}
}
