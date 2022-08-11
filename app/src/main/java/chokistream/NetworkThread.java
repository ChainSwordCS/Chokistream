package chokistream;

import java.io.IOException;

public class NetworkThread extends Thread {
	
	private VideoOutputInterface output;
	private StreamingInterface input;
	private boolean active = true;
	
	/**
	 * Start processing frames indefinitely
	 */
	public void run() {
		while(active) {
			try {
				output.renderFrame(input.getFrame());
			} catch(InterruptedException e) {
				output.displayError(e);
			} catch(IOException e) {
				output.displayError(e);
			}
		}
	}
	
	public NetworkThread(StreamingInterface input, VideoOutputInterface output) {
		this.input = input;
		this.output = output;
	}
	
	/**
	 * @param out the output object (e.g. a JavaFXVideo)
	 */
	public void setOutput(VideoOutputInterface out) {
		output = out;
	}
	
	/**
	 * @param in the input object (e.g. a NTRClient)
	 */
	public void setInput(StreamingInterface in) {
		input = in;
	}
	
	public void stopRunning() {
		active = false;
	}
}
