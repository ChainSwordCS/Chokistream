package chokistream;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import chokistream.props.DSScreen;
import chokistream.props.Layout;
import chokistream.props.LogLevel;

public class SwingVideo implements VideoOutputInterface {
	
	private StreamingInterface client;
	private NetworkThread networkThread;
	private ArrayList<JFrame> frames = new ArrayList<>();	
	private ImageComponent topImageView;
	private ImageComponent bottomImageView;
	private double uiScale;
	private static final Logger logger = Logger.INSTANCE;
	private double topScale;
	private double bottomScale;
	private int topFPS = 0;
	private int bottomFPS = 0;
	private Timer fpsTimer;

	public SwingVideo(StreamingInterface client, Layout layout, int dpi, double topScale, double bottomScale) {
		this.client = client;
		this.topScale = topScale;
		this.bottomScale = bottomScale;
		
		networkThread = new NetworkThread(this.client, this);
		
		switch(layout) {
		case SEPARATE: {
			JFrame top = new JFrame();
			JFrame bottom = new JFrame();
			topImageView = new ImageComponent(DSScreen.TOP, topScale);
			bottomImageView = new ImageComponent(DSScreen.BOTTOM, bottomScale);
			top.add(topImageView);
			bottom.add(bottomImageView);
			top.pack();
			bottom.pack();
			top.setTitle("Chokistream - Top Screen");
			bottom.setTitle("Chokistream - Bottom Screen");
			top.setVisible(true);
			bottom.setVisible(true);
			frames.add(top);
			frames.add(bottom);
			break;
		} case TOP_ONLY: {
			JFrame top = new JFrame();
			topImageView = new ImageComponent(DSScreen.TOP, topScale);
			top.add(topImageView);
			top.pack();
			top.setTitle("Chokistream");
			top.setVisible(true);
			frames.add(top);
			break;
		} case BOTTOM_ONLY: {
			JFrame bottom = new JFrame();
			bottomImageView = new ImageComponent(DSScreen.BOTTOM, bottomScale);
			bottom.add(bottomImageView);
			bottom.pack();
			bottom.setTitle("Chokistream");
			bottom.setVisible(true);
			frames.add(bottom);
			break;
		} case HORIZONTAL: {
			JFrame f = new JFrame();
			f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.X_AXIS));
			topImageView = new ImageComponent(DSScreen.TOP, topScale);
			topImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(topImageView);
			bottomImageView = new ImageComponent(DSScreen.BOTTOM, bottomScale);
			bottomImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(bottomImageView);
			f.pack();
			f.setTitle("Chokistream");
			f.setVisible(true);
			frames.add(f);
			break;
		} case HORIZONTAL_INV: {
			JFrame f = new JFrame();
			f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.X_AXIS));
			bottomImageView = new ImageComponent(DSScreen.BOTTOM, bottomScale);
			bottomImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(bottomImageView);
			topImageView = new ImageComponent(DSScreen.TOP, topScale);
			topImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(topImageView);
			f.pack();
			f.setTitle("Chokistream");
			f.setVisible(true);
			frames.add(f);
			break;
		} case VERTICAL: {
			JFrame f = new JFrame();
			f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
			topImageView = new ImageComponent(DSScreen.TOP, topScale);
			topImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(topImageView);
			bottomImageView = new ImageComponent(DSScreen.BOTTOM, bottomScale);
			bottomImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(bottomImageView);
			f.pack();
			f.setTitle("Chokistream");
			f.setVisible(true);
			frames.add(f);
			break;
		} case VERTICAL_INV: {
			JFrame f = new JFrame();
			f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.X_AXIS));
			bottomImageView = new ImageComponent(DSScreen.BOTTOM, bottomScale);
			bottomImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(bottomImageView);
			topImageView = new ImageComponent(DSScreen.TOP, topScale);
			topImageView.setAlignmentX(Component.CENTER_ALIGNMENT);
			f.add(topImageView);
			f.pack();
			f.setTitle("Chokistream");
			f.setVisible(true);
			frames.add(f);
			break;
		} default: {
			logger.log("Unsupported layout!");
			return;
		}}
		
		for(JFrame f : frames) {
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setResizable(false);
			f.setIconImage(IconLoader.get64x());
			f.addWindowListener(new WindowAdapter() {
			    @Override
			    public void windowClosing(WindowEvent e) {
			    	kill();
			    }
			});
		}
		
		networkThread.start();
		
		logger.log("Starting Swing video", LogLevel.VERBOSE);
	}

	@Override
	public void renderFrame(Frame frame) {
		if(frame.screen == DSScreen.TOP) {
			if(topImageView != null)
				topImageView.updateImage(frame.image);
		} else {
			if(bottomImageView != null)
				bottomImageView.updateImage(frame.image);
		}
	}

	@Override
	public void displayError(Exception e) {
		JFrame f = new JFrame();
		JOptionPane.showMessageDialog(f, e, "Error", JOptionPane.ERROR_MESSAGE);
		f.setVisible(true);
	}

	@Override
	public void kill() {
		fpsTimer.cancel();
		networkThread.stopRunning();
		try {
			client.close();
		} catch (IOException e) {
			displayError(e);
		}
		for(JFrame f : frames) {
			f.setVisible(false);
		}
	}

}
