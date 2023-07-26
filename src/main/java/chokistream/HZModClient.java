package chokistream;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import chokistream.props.ColorMode;
import chokistream.props.DSScreen;
import chokistream.props.DSScreenBoth;
import chokistream.props.InterpolationMode;
import chokistream.props.LogLevel;

/**
 * 
 */
public class HZModClient implements StreamingInterface {
	
	private static final byte TARGA_PACKET = 0x03;
	private static final byte JPEG_PACKET = 0x04;
	
	private Socket client = null;
	private InputStream in = null;
	private OutputStream out = null;
	private ColorMode colorMode;
	private double topScale;
	private double bottomScale;
	private InterpolationMode intrp;
	public int quality;
	private TGAPixelFormat topFormat = TGAPixelFormat.RGB8;
	private TGAPixelFormat bottomFormat = TGAPixelFormat.RGB8;
	private BufferedImage lastTopImage;
	private BufferedImage lastBottomImage;
	
	private int topFrames;
	private int bottomFrames;
	
	private static final Logger logger = Logger.INSTANCE;

	/**
	 * Create an HZModClient.
	 * @param host The host or IP to connect to.
	 * @param quality The quality to stream at.
	 * @param capCPU Cap CPU cycles.
	 * @param colorMode The color filter (option to enable hotfixColors).
	 */
	public HZModClient(String host, int quality, int capCPU, ColorMode receivedColorMode, int port,
			double topScale, double bottomScale, InterpolationMode intrp) throws UnknownHostException, IOException {
		// Connect to TCP port and set up client
		client = new Socket(host, port);
		client.setTcpNoDelay(true);
		in = client.getInputStream();
		out = client.getOutputStream();
		
		colorMode = receivedColorMode;
		this.topScale = topScale;
		this.bottomScale = bottomScale;
		this.intrp = intrp;
		this.quality = quality;
		
		lastTopImage = new BufferedImage(240, 400, BufferedImage.TYPE_INT_RGB);
		lastBottomImage = new BufferedImage(240, 320, BufferedImage.TYPE_INT_RGB);
		
		if (capCPU > 0) {
			sendLimitCPU(capCPU);
		}
		
		sendQuality(quality);
		
		sendInit((byte)0x01);
	}
	
	public void sendLimitCPU(int limitCPU) throws IOException {
		// Creates the limit CPU packet to the 3DS
		byte[] limitCPUPacket = new byte[9];
		limitCPUPacket[0] = 0x7E;
		limitCPUPacket[1] = 0x05;
		limitCPUPacket[4] = (byte) 0xFF;
		limitCPUPacket[8] = (byte) limitCPU;
		logger.log("Sending limit CPU packet", LogLevel.EXTREME);
		logger.log(limitCPUPacket, LogLevel.EXTREME);
		out.write(limitCPUPacket);
	}
	
	public void sendQuality(int quality) throws IOException {
		// Creates the quality packet to the 3DS
		byte[] qualityPacket = new byte[9];
		qualityPacket[0] = 0x7E;
		qualityPacket[1] = 0x05;
		qualityPacket[4] = 0x03;
		qualityPacket[8] = (byte) quality;
		logger.log("Sending quality packet", LogLevel.EXTREME);
		logger.log(qualityPacket, LogLevel.EXTREME);
		out.write(qualityPacket);
	}
	
	/**
	 * You shouldn't really ever need to call this outside of the constructor, but
	 * you can in case you really want to.
	 * @param screen 0x01*top | 0x02*bottom (Really, just send 1, or maybe sometimes 3)
	 * @throws IOException
	 */
	public void sendInit(byte screen) throws IOException {
		// Creates the initialization packet to the 3DS
		byte[] initializationPacket = new byte[9];
		initializationPacket[0] = 0x7E;
		initializationPacket[1] = 0x05;
		initializationPacket[8] = screen;
		logger.log("Sending initialization packet", LogLevel.EXTREME);
		logger.log(initializationPacket, LogLevel.EXTREME);
		out.write(initializationPacket);
	}

	@Override
	public void close() throws IOException {
		in.close();
		out.close();
		client.close();
	}

	/**
	 * Get a packet from HzMod
	 * @return The packet received from HzMod
	 * @throws IOException 
	 */
	private Packet getPacket() throws IOException {
		Packet returnPacket = new Packet();
		
		int type = in.read();
		if(type == -1) {
			throw new SocketException("Socket closed");
		}
		returnPacket.type = (byte) type;
		returnPacket.length = in.read() + (in.read() << 8) + (in.read() << 16);
		returnPacket.data = in.readNBytes(returnPacket.length);
		
		return returnPacket;
	}
	
	@Override
	public Frame getFrame() throws IOException {
		Frame returnFrame = null;
		Packet packet = new Packet();
		
		while (packet.type != JPEG_PACKET && packet.type != TARGA_PACKET) {
			packet = getPacket();
			String pType = switch(packet.type) {
				case JPEG_PACKET -> "JPEG";
				case TARGA_PACKET -> "TGA";
				case 0x01 -> "Disconnect";
				case 0x02 -> "Set mode";
				case 0x7E -> "CFGBLK";
				case (byte) 0xFF -> "Debug";
				default -> "Unknown";
			};
			logger.log(String.format("Recieved packet of type 0x%02X (Type %s)", packet.type, pType), LogLevel.VERBOSE);
			logger.log(""+packet.length, LogLevel.EXTREME);
			logger.log(packet.data, LogLevel.EXTREME);
			
			// If we get a Set Mode packet, we need to update our pixel format
			if(packet.type == 0x02) {
				topFormat = TGAPixelFormat.fromInt(packet.data[0] & 0x07);
				bottomFormat = TGAPixelFormat.fromInt(packet.data[8] & 0x07);
				logger.log("Set top TGA pixel format to "+topFormat, LogLevel.VERBOSE);
				logger.log("Set bottom TGA pixel format to "+bottomFormat, LogLevel.VERBOSE);
			} else if(packet.type == 0x01) {
				// Might as well respect disconnect packets
				logger.log("Recieved disconnect packet, closing");
				close();
			}
		}
		
		// First two bytes is pixel offset (actually four, but other two are never used)
		int offset = (packet.data[0] & 0xff) + ((packet.data[1] & 0xff) << 8);
		
		// Values >= 400 indicate bottom screen
		DSScreen screen = offset >= 400 ? DSScreen.BOTTOM : DSScreen.TOP;
		
		// If bottom screen, subtract 400 to get actual offset
		offset %= 400;
		
		// First 8 bytes are header, trim them off for the image data
		byte[] data = Arrays.copyOfRange(packet.data, 8, packet.data.length);
		
		BufferedImage image = null;
		
		if (packet.type == JPEG_PACKET) {
			WritableInputStream imageData = new WritableInputStream(data, true);
			image = ImageIO.read(imageData.getInputStream());
			// For some reason the red and blue channels are swapped. Fix it.
			image = ColorHotfix.doColorHotfix(image, colorMode, true);
		} else if (packet.type == TARGA_PACKET) {
			image = TargaParser.parseBytes(data, screen, screen == DSScreen.BOTTOM ? bottomFormat : topFormat);
			image = ColorHotfix.doColorHotfix(image, colorMode, false);
		}
		
		logger.log(screen.getLongName()+":"+image.getWidth()+","+image.getHeight(), LogLevel.VERBOSE);
		
		if(screen == DSScreen.BOTTOM) {
			if(image.getHeight() == 320) {
				bottomFrames++;
				lastBottomImage = image;
			} else if(image.getHeight() > 1) {
				if(image.getHeight() + offset == 320) bottomFrames++;
				image = addFractional(lastBottomImage, image, offset);
				lastBottomImage = image;
			} else {
				// 1-wide frame, use the last one instead
				image = lastBottomImage;
			}
			image = Interpolator.scale(image, intrp, bottomScale);
		} else {
			if(image.getHeight() == 400) {
				topFrames++;
				lastTopImage = image;
			} else if(image.getHeight() > 1) {
				if(image.getHeight() + offset == 400) topFrames++;
				image = addFractional(lastTopImage, image, offset);
				lastTopImage = image;
			} else {
				// 1-wide frame, use the last one instead
				image = lastTopImage;
			}
			image = Interpolator.scale(image, intrp, topScale);
		}
		
		returnFrame = new Frame(screen, image);
		
		return returnFrame;
	}
	
	@Override
	public int getFrameCount(DSScreenBoth screens) {
		switch(screens) {
			case TOP:
				int f = topFrames;
				topFrames = 0;
				return f;
			case BOTTOM:
				int f2 = bottomFrames;
				bottomFrames = 0;
				return f2;
			case BOTH:
				int f3 = topFrames + bottomFrames;
				topFrames = 0;
				bottomFrames = 0;
				return f3;
			default:
				return 0; // Should never happen
		}
	}
	
	/*
	 * It's really split by *rows* of the image, which correspond to *columns* of the screen.
	 */
	private BufferedImage addFractional(BufferedImage oldIm, BufferedImage newIm, int offset) {
		int height = newIm.getHeight();
		for(int row = 0; row < height; row++) {
			for(int col = 0; col < 240; col++) {
				try {
					oldIm.setRGB(col, offset+row, newIm.getRGB(col, row));
				} catch(Exception e) {
					logger.log("Failed to get/set pixel.", LogLevel.VERBOSE);
					logger.log("Get location:"+col+","+row+" in "+newIm.getWidth()+","+newIm.getHeight(), LogLevel.VERBOSE);
					logger.log("Set location:"+col+","+(offset+row)+" in "+oldIm.getWidth()+","+oldIm.getHeight(), LogLevel.VERBOSE);
					break; // Somehow this fixes things. I don't understand it.
				}
			}
		}
		return oldIm;
	}
	
	/**
	 * Represents a packet received from HzMod
	 */
	private class Packet {
		public byte type;
		public int length;
		public byte[] data;
	}
	
}
