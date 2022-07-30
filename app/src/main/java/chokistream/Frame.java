package chokistream;

import javafx.scene.image.Image;

/**
 * Represents a frame received from a client.
 */
public class Frame {
	/**
	 * Which NTRScreen this Frame holds.
	 * Defaults to NTRScreen.TOP for HZModClient.
	 */
	public NTRScreen screen = NTRScreen.TOP;
	
	/**
	 * The Image received from a client.
	 */
	public Image image;
	
	/**
	 * HZMod version:
	 * Create a new Frame object.
	 * @param _image The Image to load into the Frame.
	 */
	public Frame(Image _image) {
		image = _image;
	}
	
	/**
	 * NTR version:
	 * Create a new Frame object.
	 * @param _screen Which NTRScreen this Frame holds.
	 * @param _image The Image to load into the Frame.
	 */
	public Frame(NTRScreen _screen, Image _image) {
		screen = _screen;
		image = _image;
	}

}
