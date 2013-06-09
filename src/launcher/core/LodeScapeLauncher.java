package launcher.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class LodeScapeLauncher extends Thread {
	private static final String GAME_NAME = "LodeScape_";
	private static final String VERSION_FILE_URL = "http://wreed12345.github.io/resources/";
	private static final String VERSION_FILE_NAME_AND_EXTENSION = "version.txt";
	/*
	 * LodeScapeLauncher.Java
	 * show a window + progress bar + start button + background
	 * fire the game updater on a new thread
	 * if the client needs updating download the .JAR & enable the start button once finished
	 * Once the start button's clicked execute the new .JAR and close the launcher.
	 * Author: GabrielBailey74
	 */
	private boolean started;
	private boolean running;
	private ImageIcon backgroundImage;
	private JFrame launcher;
	private LodeScapeRenderer renderer;
	public String percentageText;
	private GameUpdater updater = null;
	private String loadingText;

	/**
	 * Main entry point into the program (.EXE jar wrapper)
	 * @param args The arguments passed via the user (NONE)
	 */
	public static void main(String[] args) {

		new LodeScapeLauncher().start();
	}

	public LodeScapeLauncher() {
		setBackgroundImage(new ImageIcon("background.png"));
	}

	@Override
	public void run() {
		started = true;
		initUI();

		running = started;
		// currentClientVersion, versionLocation, versionFileNameAndExtension, gameName
		try {
			setUpdater(new GameUpdater(this, GameUpdater.getVersionFromUrl(VERSION_FILE_URL
					+ VERSION_FILE_NAME_AND_EXTENSION), VERSION_FILE_URL,
					VERSION_FILE_NAME_AND_EXTENSION, GAME_NAME));

		} catch (MalformedURLException e) {
			showMalformedURLException(e);
		} catch (IOException e) {
			showIOException(e);
		}
		if (getUpdater().needsUpdating()) {
			try {
				getUpdater().downloadUpdatedJar(false);
				// after the updates have been downloaded....
				getRenderer().statusLabel
						.setText("New updates have been downloaded, your client is up to date! (V"
								+ getUpdater().getMostRecentVersion() + ")");
				getRenderer().bar.setValue(getRenderer().bar.getMaximum());
				getRenderer().bar.setEnabled(false);
				getRenderer().button_1.setEnabled(true);
			} catch (MalformedURLException e) {
				showMalformedURLException(e);
			} catch (IOException e) {
				showIOException(e);
			}
		} else {
			getRenderer().bar.setValue(getRenderer().bar.getMaximum());
			getRenderer().bar.setEnabled(false);
			getRenderer().statusLabel.setText("Your client is up to date! (V"
					+ getUpdater().getMostRecentVersion() + ")");
			getRenderer().button_1.setEnabled(true);
		}

	}

	void showIOException(IOException e) {
		JOptionPane.showMessageDialog(launcher.getContentPane(), e.getMessage(), e.getCause()
				.toString(), JOptionPane.ERROR_MESSAGE);
	}

	private void showMalformedURLException(MalformedURLException e) {
		JOptionPane.showMessageDialog(launcher.getContentPane(), e.getMessage(),
				e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
	}

	private void initUI() {
		renderer = new LodeScapeRenderer(this);
		launcher = new JFrame("LodeScape Launcher");
		launcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		launcher.setResizable(false);
		launcher.setAlwaysOnTop(true);
		launcher.getContentPane().setLayout(new BorderLayout());
		launcher.getContentPane().setBackground(java.awt.Color.black);
		launcher.setPreferredSize(new java.awt.Dimension(645, 490));
		launcher.getContentPane().add(renderer, "Center");
		launcher.pack();
		launcher.setLocationRelativeTo(null);
		launcher.setVisible(true);
	}

	/**
	 * @return the backgroundImage
	 */
	public ImageIcon getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(ImageIcon backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public LodeScapeRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @return the updater
	 */
	public GameUpdater getUpdater() {
		return updater;
	}

	/**
	 * @param updater the updater to set
	 */
	public void setUpdater(GameUpdater updater) {
		this.updater = updater;
	}

	public JFrame getLauncher() {
		return launcher;
	}

	public void setLauncher(JFrame launcher) {
		this.launcher = launcher;
	}

	/**
	 * @return the loadingText
	 */
	public String getLoadingText() {
		return loadingText;
	}

	/**
	 * @param loadingText the loadingText to set
	 */
	public void setLoadingText(String loadingText) {
		this.loadingText = loadingText;
	}
}

// rendering class
class LodeScapeRenderer extends JPanel {
	private static final long serialVersionUID = -2814754220163030705L;
	JPanel panel_1;
	JButton button_1;
	JPanel panel_2;
	JProgressBar bar;
	LodeScapeLauncher launcher;
	JLabel statusLabel;

	public LodeScapeRenderer(final LodeScapeLauncher launcher) {
		this.launcher = launcher;
		LodeScapeGUILayout customLayout = new LodeScapeGUILayout();
		setLayout(customLayout);
		setBackground(Color.black);
		panel_1 = new JPanel(null);
		panel_1.setLayout(null);
		statusLabel = new JLabel("Checking for updates...");
		statusLabel.setBounds(20, 10, 600, 20);
		statusLabel.setBackground(Color.red);
		statusLabel.setForeground(Color.green);
		bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		bar.setForeground(Color.green);
		bar.setBounds(10, 35, 500, 20);
		panel_1.setBackground(Color.black);
		panel_1.add(bar);
		panel_1.add(statusLabel);
		add(panel_1);
		button_1 = new JButton("Play");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String jarName = launcher.getUpdater().getGameName()
						+ launcher.getUpdater().getMostRecentVersion() + ".jar";
				String[] args = {};
				// TODO: Implement loading arguments.CFG? o_O
				try {
					System.out.println("Launching " + jarName + "!");
					launcher.getLauncher().dispose();
					System.out.println("Executing");
					new JarExecutor(jarName, args).execute();

				} catch (IOException e) {
					launcher.showIOException(e);
				}
			}
		});
		button_1.setEnabled(false);
		add(button_1);
		panel_2 = new JPanel(null) {
			private static final long serialVersionUID = -3949512662742201921L;

			public void paint(Graphics g) {
				final Graphics2D g2d = createG2d(g, true);
				renderTopPanel(g2d);
			}
		};
		panel_2.setBackground(Color.black);
		add(panel_2);
		Timer timer = new Timer(10, new PanelPainter());
		timer.start();
	}

	protected void renderTopPanel(Graphics2D g2d) {
		g2d.setColor(super.getBackground());
		g2d.fillRect(0, 0, super.getWidth(), super.getHeight());
		g2d.drawImage(launcher.getBackgroundImage().getImage(), 0, 0, super.getWidth(),
				super.getHeight(), null);
	}

	/*
	 * A Simple loop to repeatedly repaint our GUI ^_^
	 */
	class PanelPainter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			panel_1.repaint();
		}
	}

	/*
	 * Custom method for high quality rendering ^_^
	 */
	public static Graphics2D createG2d(Graphics g, boolean b) {
		Graphics2D g2d = (Graphics2D) g;
		if (b) {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 100);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
		}
		return g2d;
	}
}

// gui layout class
class LodeScapeGUILayout implements LayoutManager {

	public LodeScapeGUILayout() {
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		Insets insets = parent.getInsets();
		dim.width = 643 + insets.left + insets.right;
		dim.height = 470 + insets.top + insets.bottom;

		return dim;
	}

	public Dimension minimumLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);
		return dim;
	}

	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();

		Component c;
		c = parent.getComponent(0);
		if (c.isVisible()) {
			c.setBounds(insets.left + 8, insets.top + 392, 520, 64);
		}
		c = parent.getComponent(1);
		if (c.isVisible()) {
			c.setBounds(insets.left + 536, insets.top + 392, 96, 64);
		}
		c = parent.getComponent(2);
		if (c.isVisible()) {
			c.setBounds(insets.left + 0, insets.top + 0, 640, 384);
		}
	}
}