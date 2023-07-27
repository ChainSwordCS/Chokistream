package chokistream;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatLightLaf;

import chokistream.props.ColorMode;
import chokistream.props.ConsoleModel;
import chokistream.props.DSScreen;
import chokistream.props.DSScreenBoth;
import chokistream.props.EnumProp;
import chokistream.props.InterpolationMode;
import chokistream.props.Layout;
import chokistream.props.LogLevel;
import chokistream.props.LogMode;
import chokistream.props.Mod;
import chokistream.props.OutputFormat;
import chokistream.props.Prop;

public class SwingGUI extends SettingsUI {
	
	private JFrame f;
	
	// General settings
	private JComboBox<String> mod;
	private JTextField ip;
	private JComboBox<String> layout;
	private JTextField topScale;
	private JTextField bottomScale;
	private JComboBox<String> intrpMode;
	private JTextField dpi;
	private JComboBox<String> colorMode;
	private JTextField port;
	private JComboBox<String> logMode;
	private JComboBox<String> logLevel;
	private JTextField logFile;
	private JComboBox<String> outputFormat;
	
	// Video settings
	private JComboBox<String> videoCodec;
	private JTextField videoFile;
	
	// Image sequence settings
	private JTextField sequenceDir;
	private JTextField sequencePrefix;
	
	// HzMod settings
	private JFrame hzSettings;
	private JTextField qualityHz;
	private JTextField cpuCapHz;
	private JCheckBox tgaHz;
	
	// ChirunoMod settings
	private JFrame chmSettings;
	private JTextField qualityCHM;
	private JTextField cpuCapCHM;
	private JComboBox<String> reqScreenCHM;
	private JCheckBox tgaCHM;
	private JCheckBox interlace;
	private JCheckBox vsync;
	
	// NTR settings
	private JFrame ntrSettings;
	private JTextField qualityNTR;
	private JComboBox<String> priScreen;
	private JTextField priFac;
	private JTextField qos;
	
	// NTR patch screen
	private JFrame ntrPatch;
	
	private static final Logger logger = Logger.INSTANCE;
	
	private static final String ABOUT_TEXT = "<html>Made by Eiim, herronjo, and ChainSwordCS.<br>"
								+ "<br>"
								+ "This software and its source code are provided under the GPL-3.0 License.  See LICENSE for the full license.<br>"
								+ "<br>"
								+ "Chokistream was made possible by the use and reference of several projects. Special thanks to:<br>"
								+ " * RattletraPM for Snickerstream<br>"
								+ " * Sono for HzMod<br>"
								+ " * Cell9/44670 for BootNTR<br>"
								+ " * Nanquitas for BootNTRSelector<br>"
								+ " * toolboc for UWPStreamer<br>"
								+ " * All other open-source contributors";
	
	public SwingGUI() {
		FlatLightLaf.setup();
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Chokistream");
		JPanel p = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		frameSetup(f, p, c);
		
		add(new JLabel(Prop.MOD.getLongName()), p, c, 0, 0);
		add(new JLabel(Prop.IP.getLongName()), p, c, 0, 2);
		add(new JLabel(Prop.LAYOUT.getLongName()), p, c, 0, 3);
		add(new JLabel(Prop.TOPSCALE.getLongName()), p, c, 0, 4);
		add(new JLabel(Prop.BOTTOMSCALE.getLongName()), p, c, 0, 5);
		add(new JLabel(Prop.INTRPMODE.getLongName()), p, c, 0, 6);
		add(new JLabel(Prop.DPI.getLongName()), p, c, 0, 7);
		
		mod = new JComboBox<String>(EnumProp.getLongNames(Mod.class));
		add(mod, p, c, 1, 0);
		ip = new JTextField(Prop.IP.getDefault());
		add(ip, p, c, 1, 2);
		layout = new JComboBox<String>(EnumProp.getLongNames(Layout.class));
		add(layout, p, c, 1, 3);
		topScale = new JTextField(Prop.TOPSCALE.getDefault().toString());
		add(topScale, p, c, 1, 4);
		bottomScale = new JTextField(Prop.BOTTOMSCALE.getDefault().toString());
		add(bottomScale, p, c, 1, 5);
		intrpMode = new JComboBox<String>(EnumProp.getLongNames(InterpolationMode.class));
		add(intrpMode, p, c, 1, 6);
		dpi = new JTextField(Prop.DPI.getDefault().toString());
		add(dpi, p, c, 1, 7);
		
		add(new JSeparator(SwingConstants.VERTICAL), p, c, 2, 0, 1, 8);
		
		add(new JLabel(Prop.COLORMODE.getLongName()), p, c, 3, 0);
		add(new JLabel(Prop.PORT.getLongName()), p, c, 3, 1);
		add(new JLabel(Prop.LOGMODE.getLongName()), p, c, 3, 2);
		add(new JLabel(Prop.LOGLEVEL.getLongName()), p, c, 3, 3);
		add(new JLabel(Prop.LOGFILE.getLongName()), p, c, 3, 4);
		add(new JLabel(Prop.OUTPUTFORMAT.getLongName()), p, c, 3, 5);
		
		colorMode = new JComboBox<String>(EnumProp.getLongNames(ColorMode.class));
		add(colorMode, p, c, 4, 0);
		port = new JTextField(Prop.PORT.getDefault().toString());
		add(port, p, c, 4, 1);
		logMode = new JComboBox<String>(EnumProp.getLongNames(LogMode.class));
		add(logMode, p, c, 4, 2);
		logLevel = new JComboBox<String>(EnumProp.getLongNames(LogLevel.class));
		add(logLevel, p, c, 4, 3);
		logFile = new JTextField(Prop.LOGFILE.getDefault());
		add(logFile, p, c, 4, 4);
		outputFormat = new JComboBox<String>(EnumProp.getLongNames(OutputFormat.class));
		add(outputFormat, p, c, 4, 5);
		
		JButton modSettings = new JButton("Mod Settings");
		JButton outputSettings = new JButton("Output Settings");
		JButton controls = new JButton("Controls");
		JButton about = new JButton("About");
		JButton connect = new JButton("Connect!");
		add(modSettings, p, c, 0, 1, 2, 1);
		add(connect, p, c, 0, 8, 2, 1);
		add(outputSettings, p, c, 3, 6, 2, 1);
		add(controls, p, c, 3, 7, 2, 1);
		add(about, p, c, 3, 8, 2, 1);
		
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {createAbout();}
		});
		
		modSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(getPropEnum(Prop.MOD, Mod.class)) {
				case HZMOD -> hzSettings.setVisible(true);
				case CHIRUNOMOD -> chmSettings.setVisible(true);
				case NTR -> ntrSettings.setVisible(true);
				}
			}
		});
		
		f.getRootPane().setDefaultButton(connect);
		f.pack();
		f.setVisible(true);
		
		createHzModSettings();
		createNTRSettings();
		createNTRPatch();
		createCHMSettings();
	}
	
	@Override
	public int getPropInt(Prop<Integer> p) {
		if(p.equals(Prop.QUALITY)) {
			return switch(getPropEnum(Prop.MOD, Mod.class)) {
				case NTR -> Integer.parseInt(qualityNTR.getText());
				case HZMOD -> Integer.parseInt(qualityHz.getText());
				case CHIRUNOMOD -> Integer.parseInt(qualityCHM.getText());
			};
		} else if(p.equals(Prop.PRIORITYFACTOR)) {
			return Integer.parseInt(priFac.getText());
		} else if(p.equals(Prop.QOS)) {
			return Integer.parseInt(qos.getText());
		} else if(p.equals(Prop.CPUCAP)) {
			return switch(getPropEnum(Prop.MOD, Mod.class)) {
				case NTR -> p.getDefault(); // Hopefully never happens
				case HZMOD -> Integer.parseInt(cpuCapHz.getText());
				case CHIRUNOMOD -> Integer.parseInt(cpuCapCHM.getText());
			};
		} else if(p.equals(Prop.PORT)) {
			return Integer.parseInt(port.getText());
		} else if(p.equals(Prop.DPI)) {
			return Integer.parseInt(dpi.getText());
		} else {
			return p.getDefault();
		}
	}

	@Override
	public String getPropString(Prop<String> p) {
		if(p.equals(Prop.IP)) {
			return ip.getText();
		} else if(p.equals(Prop.LOGFILE)) {
			return logFile.getText();
		} else if(p.equals(Prop.VIDEOFILE)) {
			return videoFile.getText();
		} else {
			return p.getDefault();
		}
	}

	@Override
	public double getPropDouble(Prop<Double> p) {
		if(p.equals(Prop.TOPSCALE)) {
			return Double.parseDouble(topScale.getText());
		} else if(p.equals(Prop.BOTTOMSCALE)) {
			return Double.parseDouble(bottomScale.getText());
		} else {
			return p.getDefault();
		}
	}

	@Override
	public boolean getPropBoolean(Prop<Boolean> p) {
		if(p.equals(Prop.REQTGA)) {
			return tgaCHM.isSelected(); // Only used for ChirunoMod
		} else if(p.equals(Prop.INTERLACE)) {
			return interlace.isSelected();
		} else if(p.equals(Prop.VSYNC)) {
			return vsync.isSelected();
		} else {
			return p.getDefault();
		}
	}

	@Override
	public <T extends Enum<T> & EnumProp> T getPropEnum(Prop<T> p, Class<T> c) {
		if(p.equals(Prop.MOD)) {
			return EnumProp.fromLongName(c, mod.getSelectedItem().toString());
		} else if(p.equals(Prop.LAYOUT)) {
			return EnumProp.fromLongName(c, layout.getSelectedItem().toString());
		} else if(p.equals(Prop.PRIORITYSCREEN)) {
			return EnumProp.fromLongName(c, priScreen.getSelectedItem().toString());
		} else if(p.equals(Prop.REQSCREEN)) {
			return switch(getPropEnum(Prop.MOD, Mod.class)) {
				case NTR -> p.getDefault(); // Hopefully never happens
				case HZMOD -> p.getDefault(); // Hopefully never happens
				case CHIRUNOMOD -> EnumProp.fromLongName(c, reqScreenCHM.getSelectedItem().toString());
			};
		} else if(p.equals(Prop.COLORMODE)) {
			return EnumProp.fromLongName(c, colorMode.getSelectedItem().toString());
		} else if(p.equals(Prop.LOGMODE)) {
			return EnumProp.fromLongName(c, logMode.getSelectedItem().toString());
		} else if(p.equals(Prop.LOGLEVEL)) {
			return EnumProp.fromLongName(c, logLevel.getSelectedItem().toString());
		} else if(p.equals(Prop.INTRPMODE)) {
			return EnumProp.fromLongName(c, intrpMode.getSelectedItem().toString());
		} else if(p.equals(Prop.OUTPUTFORMAT)) {
			return EnumProp.fromLongName(c, outputFormat.getSelectedItem().toString());
		} else if(p.equals(Prop.VIDEOCODEC)) {
			return EnumProp.fromLongName(c, videoCodec.getSelectedItem().toString());
		} else {
			return p.getDefault();
		}
	}

	@Override
	public void displayError(Exception e) {
		JOptionPane.showMessageDialog(f, e, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void saveSettings() {
		// TODO Auto-generated method stub
		super.saveSettings();
	}

	@Override
	public void loadSettings() {
		// TODO Auto-generated method stub
		super.loadSettings();
	}

	public void createAbout() {
		JFrame f = new JFrame();
		f.setResizable(false);
		f.setIconImage(IconLoader.get64x());
		f.setTitle("About");
		
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(5, 10, 7, 10));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		f.add(p);
		
		JLabel header = new JLabel("Chokistream", new ImageIcon(IconLoader.get64x()), JLabel.LEFT);
		header.setFont(new Font("System", Font.BOLD, 60));
		p.add(header);
		
		JLabel info = new JLabel(ABOUT_TEXT);
		p.add(info);
		
		f.pack();
		f.setVisible(true);
	}
	
	public void createHzModSettings() {
		hzSettings = new JFrame();
		JPanel p = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		frameSetup(hzSettings, p, c);
		hzSettings.setTitle("HzMod Settings");
		
		JLabel header = new JLabel("HzMod Settings");
		header.setFont(new Font("System", Font.PLAIN, 20));
		add(header, p, c, 0, 0, 2, 1);
		
		add(new JLabel("Quality"), p, c, 0, 1);
		add(new JLabel("Request TGA?"), p, c, 0, 2);
		add(new JLabel("CPU Cap"), p, c, 0, 3);
		
		qualityHz = new JTextField(Prop.QUALITY.getDefault().toString());
		add(qualityHz, p, c, 1, 1);
		tgaHz = new JCheckBox(null, null, Prop.REQTGA.getDefault());
		add(tgaHz, p, c, 1, 2);
		cpuCapHz = new JTextField(Prop.CPUCAP.getDefault().toString());
		add(cpuCapHz, p, c, 1, 3);
		
		JButton apply = new JButton("Apply");
		add(apply, p, c, 0, 4, 2, 1);
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				hzSettings.setVisible(false);
			}
		});
		
		hzSettings.pack();
	}
	
	public void createNTRSettings() {
		ntrSettings = new JFrame();
		JPanel p = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		frameSetup(ntrSettings, p, c);
		ntrSettings.setTitle("NTR Settings");
		
		JLabel header = new JLabel("NTR Settings");
		header.setFont(new Font("System", Font.PLAIN, 20));
		add(header, p, c, 0, 0, 2, 1);
		
		add(new JLabel("Quality"), p, c, 0, 1);
		add(new JLabel("Priority Screen"), p, c, 0, 2);
		add(new JLabel("Priority Factor"), p, c, 0, 3);
		add(new JLabel("QoS"), p, c, 0, 4);
		
		qualityNTR = new JTextField(Prop.QUALITY.getDefault().toString());
		add(qualityNTR, p, c, 1, 1);
		priScreen = new JComboBox<>(EnumProp.getLongNames(DSScreen.class));
		add(priScreen, p, c, 1, 2);
		priFac = new JTextField(Prop.PRIORITYFACTOR.getDefault().toString());
		add(priFac, p, c, 1, 3);
		qos = new JTextField(Prop.QOS.getDefault().toString());
		add(qos, p, c, 1, 4);
		
		JButton patch = new JButton("Patch NTR");
		add(patch, p, c, 0, 5, 2, 1);
		patch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ntrPatch.setVisible(true);
			}
		});
		
		JButton apply = new JButton("Apply");
		add(apply, p, c, 0, 5, 2, 1);
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				ntrSettings.setVisible(false);
			}
		});
		
		ntrSettings.pack();
	}
	
	public void createNTRPatch() {
		ntrPatch = new JFrame();
		JPanel p = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		frameSetup(ntrPatch, p, c);
		ntrPatch.setTitle("NTR Patch");
		
		JLabel header = new JLabel("NTR Patch");
		header.setFont(new Font("System", Font.PLAIN, 20));
		add(header, p, c, 0, 0, 2, 1);
		
		JButton n3ds = new JButton("New 3DS");
		add(n3ds, p, c, 0, 1);
		n3ds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
    				ntrPatch.setVisible(false);
					NTRClient.sendNFCPatch(getPropString(Prop.IP), getPropInt(Prop.PORT), null, ConsoleModel.N3DS);
				} catch (IOException | RuntimeException ex) {
					displayError(ex);
				}
			}
		});
		
		JButton o3ds = new JButton("Old 3DS");
		add(o3ds, p, c, 1, 1);
		o3ds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
    				ntrPatch.setVisible(false);
					NTRClient.sendNFCPatch(getPropString(Prop.IP), getPropInt(Prop.PORT), null, ConsoleModel.O3DS);
				} catch (IOException | RuntimeException ex) {
					displayError(ex);
				}
			}
		});
		
		ntrPatch.pack();
	}
	
	public void createCHMSettings() {
		chmSettings = new JFrame();
		JPanel p = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		frameSetup(chmSettings, p, c);
		chmSettings.setTitle("ChirunoMod Settings");
		
		JLabel header = new JLabel("ChirunoMod Settings");
		header.setFont(new Font("System", Font.PLAIN, 20));
		add(header, p, c, 0, 0, 2, 1);
		
		add(new JLabel("Quality"), p, c, 0, 1);
		add(new JLabel("Request TGA?"), p, c, 0, 2);
		add(new JLabel("CPU Cap"), p, c, 0, 3);
		add(new JLabel("Requested Screen"), p, c, 0, 4);
		add(new JLabel("Interlace?"), p, c, 0, 5);
		add(new JLabel("VSync/HSync"), p, c, 0, 6);
		
		qualityCHM = new JTextField(Prop.QUALITY.getDefault().toString());
		add(qualityCHM, p, c, 1, 1);
		tgaCHM = new JCheckBox(null, null, Prop.REQTGA.getDefault());
		add(tgaCHM, p, c, 1, 2);
		cpuCapCHM = new JTextField(Prop.CPUCAP.getDefault().toString());
		add(cpuCapCHM, p, c, 1, 3);
		reqScreenCHM = new JComboBox<>(EnumProp.getLongNames(DSScreenBoth.class));
		add(reqScreenCHM, p, c, 1, 4);
		interlace = new JCheckBox(null, null, Prop.INTERLACE.getDefault());
		add(interlace, p, c, 1, 5);
		vsync = new JCheckBox(null, null, Prop.VSYNC.getDefault());
		add(vsync, p, c, 1, 6);
		
		JButton apply = new JButton("Apply");
		add(apply, p, c, 0, 7, 2, 1);
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				chmSettings.setVisible(false);
			}
		});
		
		chmSettings.pack();
	}
	
	private void frameSetup(JFrame f, JPanel p, GridBagConstraints c) {
		f.setResizable(false);
		f.setIconImage(IconLoader.get64x());
		
		p.setBorder(BorderFactory.createEmptyBorder(5, 10, 7, 10));
		p.setLayout(new GridBagLayout());
		f.add(p);
		
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 3;
		c.ipady = 3;
		c.insets = new Insets(3, 3, 3, 3);
	}
	
	private void add(Component co, JPanel f, GridBagConstraints c, int x, int y) {
		c.gridx = x;
		c.gridy = y;
		f.add(co, c);
	}
	
	private void add(Component co, JPanel f, GridBagConstraints c, int x, int y, int w, int h) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = w;
		c.gridheight = h;
		f.add(co, c);
		c.gridwidth = 1;
		c.gridheight = 1;
	}
}
