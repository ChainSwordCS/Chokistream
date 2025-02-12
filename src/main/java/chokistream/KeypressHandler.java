package chokistream;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import chokistream.props.Controls;
import chokistream.props.LogLevel;

public class KeypressHandler implements KeyListener {
	
	private SwingVideo output;
	private StreamingInterface client;
	private ImageComponent topImageView;
	private ImageComponent bottomImageView;
	private ChokiKeybinds ck;
	private static final Logger logger = Logger.INSTANCE;
	
	public KeypressHandler(SwingVideo sv, StreamingInterface si, ImageComponent top, ImageComponent bottom, ChokiKeybinds keybinds) {
		output = sv;
		client = si;
		topImageView = top;
		bottomImageView = bottom;
		ck = keybinds;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Generic commands
		if(e.getKeyCode() == ck.get(Controls.SCREENSHOT)) {
			try {
				// Only take the screenshots if the image exists - mostly for HzMod, but perhaps
				// also if the images just haven't come yet because we're still initializing
				if(topImageView.getImage() != null) {
					File ft = new File("chokistream_top.png");
					ImageIO.write(topImageView.getImage(), "png", ft);
				}
				if(bottomImageView.getImage() != null) {
					File fb = new File("chokistream_bottom.png");
					ImageIO.write(bottomImageView.getImage(), "png", fb);
				}
				logger.log("Took a screenshot!", LogLevel.VERBOSE);
			} catch (IOException e1) {
				output.displayError(e1);
			}
		} else if(e.getKeyCode() == ck.get(Controls.RETURN)) {
			output.kill();
		}
		
		// Client-specific commands
		if(client instanceof HZModClient) {
			HZModClient c = (HZModClient) client;
			
			if(e.getKeyCode() == ck.get(Controls.QUALITY_UP) && c.quality < 100) {
				c.quality++;
				logger.log("Increasing quality to "+c.quality, LogLevel.VERBOSE);
				try {
					c.sendQuality(c.quality);
				} catch (IOException e1) {
					output.displayError(e1);
				}
			} else if(e.getKeyCode() == ck.get(Controls.QUALITY_DOWN) && c.quality > 0) {
				c.quality--;
				logger.log("Decreasing quality to "+c.quality, LogLevel.VERBOSE);
				try {
					c.sendQuality(c.quality);
				} catch (IOException e1) {
					output.displayError(e1);
				}
			}
		} else if(client instanceof ChirunoModClient) {
			ChirunoModClient c = (ChirunoModClient) client;
			
			if(e.getKeyCode() == ck.get(Controls.QUALITY_UP) && c.quality < 100) {
				c.quality++;
				logger.log("Increasing quality to "+c.quality, LogLevel.VERBOSE);
				try {
					c.sendQuality(c.quality);
				} catch (IOException e1) {
					output.displayError(e1);
				}
			} else if(e.getKeyCode() == ck.get(Controls.QUALITY_DOWN) && c.quality > 0) {
				c.quality--;
				logger.log("Decreasing quality to "+c.quality, LogLevel.VERBOSE);
				try {
					c.sendQuality(c.quality);
				} catch (IOException e1) {
					output.displayError(e1);
				}
			}
		}
	}
	
	// These methods need to be overridden, but aren't useful.
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
}
