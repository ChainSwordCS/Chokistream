package chokistream;

import javafx.application.Application;
import javafx.stage.Stage;


public class App extends Application {
	
	private SettingsGUI scene;
	private StreamingInterface client;
	private VideoOutputInterface output;
	private Stage stage;
	
    /**
     * Adds the basic elements to a JavaFX stage. Triggered via main.
     * 
     * @param stage	The Stage to display on.
     */
    @Override
    public void start(Stage stage) throws Exception {
    	scene = new SnickerstreamGUI(this);
        stage.setScene(scene);
        stage.setTitle("Chokistream");
        stage.show();
        this.stage = stage;
    }
    
    /**
     * Starts a new instance of Chickerstream.
     * 
     * @param args	Currently unused
     */
    public static void main(String[] args) {
    	launch();
    }
    
    /**
     * Triggered from the UI. Attempts to instantiate the connection to the 3DS.
     */
    public void connect() {
    	// These are universal, so get these first and then sort out the rest by mod.
    	// Technically quality could be here.
    	Mod mod;
    	String ip;
    	Layout layout;
    	try {
			mod = scene.getMod();
			ip = scene.getIp();
			layout = scene.getLayout();
		} catch (InvalidOptionException e) {
			scene.displayError(e);
			return;
		}
    	
    	switch(mod) {
    		case NTR:
				try {
					int quality = scene.getQuality();
	    			NTRScreen screen = scene.getScreen();
	    			int priority = scene.getPriority();
	    			int qos = scene.getQos();
	    			
	    			// Initializes connection
	    			client = new NTRClient(ip, quality, screen, priority, qos);
	    			output = new JavaFXVideo(client, layout);
	    			stage.close();
				} catch (Exception e) {
					scene.displayError(e);
					return;
				}
				break;
    		case HZMOD:
    			try {
    				int quality = scene.getQuality();
    				int capCpu = scene.getCapCPU();
    				
    				// Initializes connection
    				client = new HZModClient(ip, quality, capCpu);
    				output = new JavaFXVideo(client, layout);
    				stage.close();
    			} catch (Exception e) {
    				scene.displayError(e);
    			}
    	}
    }

}