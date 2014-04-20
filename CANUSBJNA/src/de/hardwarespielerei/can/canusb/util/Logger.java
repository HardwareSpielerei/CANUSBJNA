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

package de.hardwarespielerei.can.canusb.util;

import java.io.PrintStream;
import java.util.Date;
import java.util.NoSuchElementException;

import com.sun.jna.Platform;

import de.hardwarespielerei.can.canusb.AcceptanceCode;
import de.hardwarespielerei.can.canusb.AcceptanceMask;
import de.hardwarespielerei.can.canusb.Adapter;
import de.hardwarespielerei.can.canusb.AdapterIterator;
import de.hardwarespielerei.can.canusb.Bitrate;
import de.hardwarespielerei.can.canusb.CANMessage;
import de.hardwarespielerei.can.canusb.CANUSBException;
import de.hardwarespielerei.can.canusb.Channel;
import de.hardwarespielerei.can.canusb.Flag;
import de.hardwarespielerei.can.canusb.NoMessageException;
import de.hardwarespielerei.can.canusb.ReceiveCallback;
import de.hardwarespielerei.can.canusb.Status;
import de.hardwarespielerei.can.canusb.Version;

/**
 * Simple CAN sniffer and logger.
 * 
 * @author gabriel
 */
public class Logger
{
	/**
	 * Logger can either loop and read messages from the channel or register a
	 * call back to do so.
	 * 
	 * @author gabriel
	 */
	public enum Mode
	{
		/**
		 * Logger will loop and read messages from the channel.
		 */
		LOOP(),

		/**
		 * Logger will register a call back to read messages from the channel.
		 */
		CALLBACK();
	}

	private static class LogReceiveCallback implements ReceiveCallback
	{
		private Logger logger;

		private LogReceiveCallback(Logger logger)
		{
			// initialize member variables
			this.logger = logger;
		}

		@Override
		public void callback(CANMessage msg)
		{
			this.logger.logMessage(msg);
		}
	}

	private class StatusLogger implements Runnable
	{
		private Logger logger;
		private boolean stopThread = false;

		private StatusLogger(Logger logger)
		{
			// initialize member variables
			this.logger = logger;
		}

		private void stop()
		{
			// raise stop flag
			this.stopThread = true;
		}

		@Override
		public void run()
		{
			long lastStatusMillis = 0;
			while (!this.stopThread)
			{
				// log status every second
				long now = System.currentTimeMillis();
				if (now - lastStatusMillis >= 10000)
				{
					try
					{
						Status status = this.logger.getStatus();
						this.logger.logStatus(status);
					} catch (CANUSBException e)
					{
						this.logger.out.println("["
								+ new Date(System.currentTimeMillis())
								+ "][ERROR][" + e.getMessage() + "]");
						e.printStackTrace(this.logger.out);
					}
					lastStatusMillis = now;
				}

				if (this.logger.mode.equals(Mode.LOOP))
				{
					// log all messages received
					do
					{
						try
						{
							CANMessage msg = this.logger.channel.read();
							this.logger.out.println("["
									+ new Date(System.currentTimeMillis())
									+ "][MSGRECEIVE]");
							this.logger.out.println(msg);
						} catch (NoMessageException e)
						{
							// no more messages waiting, stop loop and go to
							// sleep...
							break;
						} catch (CANUSBException e)
						{
							this.logger.out.println("["
									+ new Date(System.currentTimeMillis())
									+ "][ERROR][" + e.getMessage() + "]");
							e.printStackTrace();
						}
					} while (true);
				}

				// sleep
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					// do nothing
				}
			}
		}
	}

	private Mode mode;
	private Bitrate bitrate;
	private ReceiveCallback callback;
	private Adapter adapter;
	protected Channel channel;
	protected PrintStream out;
	private StatusLogger statusLogger;
	private Thread statusLoggerThread;

	private Logger(Mode mode, Adapter adapterToUse, Bitrate bitrate,
			ReceiveCallback callback, PrintStream out) throws CANUSBException
	{
		// initialize member variables
		this.mode = mode;
		this.bitrate = bitrate;
		this.callback = callback;
		this.adapter = adapterToUse;
		this.out = out;

		// open channel
		Flag[] flags = { Flag.Timestamp };
		this.channel = this.adapter.openChannel(this.bitrate,
				AcceptanceCode.AcceptAll, AcceptanceMask.AcceptAll, flags);
		try
		{
			// log version info
			this.out.println("[" + new Date(System.currentTimeMillis())
					+ "][VERSIONINFO][" + channel.getVersionInfo() + "]");

			if (this.mode.equals(Mode.CALLBACK))
			{
				if (null == this.callback)
				{
					this.callback = new LogReceiveCallback(this);
				}
				// set log receive callback
				channel.setReceiveCallBack(this.callback);
			}

			// start status logger
			this.statusLogger = new StatusLogger(this);
			this.statusLoggerThread = new Thread(this.statusLogger,
					"StatusLogger");
			this.statusLoggerThread.start();
		} catch (Exception e)
		{
			// cleanup
			synchronized (this.channel)
			{
				this.channel.close();
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructs a logger sniffing the CAN communication on the given adpater
	 * in loop or call back mode and logging to the given output stream.
	 * 
	 * @param mode
	 *            determines whether the logger reads CAN messages in a loop or
	 *            via call back.
	 * @param adapterToUse
	 *            references the adapter to use.
	 * @param bitrate
	 *            references the bit rate used on the CAN bus.
	 * @param out
	 *            references the output stream for printing the log content.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public Logger(Mode mode, Adapter adapterToUse, Bitrate bitrate,
			PrintStream out) throws CANUSBException
	{
		this(mode, adapterToUse, bitrate, null, out);
	}

	/**
	 * Constructs a logger sniffing the CAN communication on the given adpater
	 * in call back mode and logging via the given receive callback.
	 * 
	 * @param adapterToUse
	 *            references the adapter to use.
	 * @param bitrate
	 *            references the bit rate used on the CAN bus.
	 * @param callback
	 *            references the call back.
	 * @param out
	 *            references the output stream for printing the log content.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public Logger(Adapter adapterToUse, Bitrate bitrate,
			ReceiveCallback callback, PrintStream out) throws CANUSBException
	{
		this(Mode.CALLBACK, adapterToUse, bitrate, callback, out);
	}

	/**
	 * Closes this logger.
	 * 
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public void close() throws CANUSBException
	{
		// stop status logger
		this.statusLogger.stop();
		this.statusLoggerThread.interrupt();

		// stop receive callback
		synchronized (this.channel)
		{
			this.channel.setReceiveCallBack(null);
		}

		// wait for termination of status logger
		while (this.statusLoggerThread.isAlive())
		{
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				// do nothing
			}
		}

		// close channel
		synchronized (this.channel)
		{
			this.channel.close();
		}
	}

	/**
	 * Gets the status of the channel used by this logger.
	 * 
	 * @return the status.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public Status getStatus() throws CANUSBException
	{
		synchronized (this.channel)
		{
			return this.channel.getStatus();
		}
	}

	/**
	 * Callback method for status updates to be logged.
	 * 
	 * @param status
	 *            references the CANBUS status.
	 */
	public void logStatus(Status status)
	{
		this.out.println("[" + new Date(System.currentTimeMillis())
				+ "][STATUS][" + status + "]");
	}

	/**
	 * Callback method for received messages to be logged.
	 * 
	 * @param msg
	 *            references the CAN message.
	 */
	public void logMessage(CANMessage msg)
	{
		this.out.println("[" + new Date(System.currentTimeMillis())
				+ "][MSGRECEIVE]");
		this.out.println(msg);
	}

	private static class ShutdownHook extends Thread
	{
		private Logger logger;

		protected ShutdownHook(Logger logger)
		{
			this.logger = logger;
		}

		@Override
		public void run()
		{
			try
			{
				logger.close();
			} catch (CANUSBException e)
			{
				System.err.println("ERROR: Can't close CANUSB properly!");
				e.printStackTrace();
			}
			System.out.println("INFO: CANUSB log ends.");
		}
	}

	/**
	 * @param args
	 *            references command line arguments.
	 * @throws CANUSBException
	 *             on errors while accessing CANUSB.
	 */
	public static void main(String[] args) throws CANUSBException
	{
		System.out.println("###############################");
		System.out.println("# CANUSB Logger V" + Version.VERSION + "     #");
		System.out.println("# (C) 2014 by Gabriel Schmidt #");
		System.out.println("#  @see hardwarespielerei.de  #");
		System.out.println("###############################");
		System.out.println();
		if (Platform.isWindows())
		{
			// set defaults
			Mode mode = Mode.CALLBACK;
			Bitrate bitrate = Bitrate.Bitrate250kbps;
			String serialNumber = null;

			// parse command line arguments
			int argPos = 0;
			while (argPos < args.length)
			{
				String arg = args[argPos++];
				try
				{
					switch (arg)
					{
						case "-bitrate":
							String bitrateArg = args[argPos++];
							switch (bitrateArg)
							{
								case "10":
								case "10kbps":
									bitrate = Bitrate.Bitrate10kbps;
									break;
								case "20":
								case "20kbps":
									bitrate = Bitrate.Bitrate20kbps;
									break;
								case "50":
								case "50kbps":
									bitrate = Bitrate.Bitrate50kbps;
									break;
								case "100":
								case "100kbps":
									bitrate = Bitrate.Bitrate100kbps;
									break;
								case "250":
								case "250kbps":
									bitrate = Bitrate.Bitrate250kbps;
									break;
								case "500":
								case "500kbps":
									bitrate = Bitrate.Bitrate500kbps;
									break;
								case "800":
								case "800kbps":
									bitrate = Bitrate.Bitrate800kbps;
									break;
								case "1000":
								case "1Mbps":
									bitrate = Bitrate.Bitrate1Mbps;
									break;
							}
							break;
						case "-mode":
							String modeArg = args[argPos++];
							try
							{
								mode = Mode.valueOf(modeArg.toUpperCase());
							} catch (IllegalArgumentException e)
							{
								System.err.println("WARNING: \"" + modeArg
										+ "\" is not a valid mode!");
							}
							break;
						case "-sn":
							serialNumber = args[argPos++];
							break;
						default:
							System.err.println("WARNING: Unkown argument \""
									+ arg + "\"!");
					}
				} catch (ArrayIndexOutOfBoundsException e)
				{
					System.err
							.println("WARNING: Missing parameter for option \""
									+ arg + "\"!");
				}
			}

			// search for adapter to use...
			Adapter adapterToUse = null;
			if (null == serialNumber)
			{
				System.out
						.println("No serial number specified - searching for CANUSB adapter with "
								+ bitrate + " kbps...");
				try
				{
					adapterToUse = new AdapterIterator(bitrate).next();
				} catch (NoSuchElementException e)
				{
					throw new NullPointerException("No CANUSB adapter found!");
				}
			} else
			{
				System.out.println("Searching for CANUSB # " + serialNumber
						+ " with " + bitrate + " kbps...");
				try
				{
					adapterToUse = new AdapterIterator(bitrate)
							.next(serialNumber);
				} catch (NoSuchElementException e)
				{
					throw new NullPointerException("No CANUSB # "
							+ serialNumber + " found!");
				}
			}

			// start logging...
			System.out.println("Start sniffing on CANUSB # "
					+ adapterToUse.getSerialNumber() + " with " + bitrate
					+ " kbps in " + mode.toString().toLowerCase() + " mode...");
			System.out.println("Press [Control+C] to stop logging...");
			Logger logger = new Logger(mode, adapterToUse, bitrate, System.out);
			ShutdownHook shutdownHook = new ShutdownHook(logger);
			Runtime.getRuntime().addShutdownHook(shutdownHook);
			try
			{
				boolean goon = true;
				do
				{
					// sleep...
					try
					{
						Thread.sleep(100);
					} catch (InterruptedException e)
					{
						// do nothing
					}
				} while (goon);
			} finally
			{
				// exiting do-while-loop unexpectedly...
				System.err.println("WARNING: CANUSB log ends unexpectedly!");
				// remove shutdown hook...
				Runtime.getRuntime().removeShutdownHook(shutdownHook);
				// do what has to be done...
				logger.close();
			}
		} else
		{
			System.err
					.println("ERROR: CANUSB is not supported on this platform!");
		}
	}
}
