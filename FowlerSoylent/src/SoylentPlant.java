import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;

/**
 * The SoylentPlant class is the main GUI class for Soylent Green Management. It
 * is a bloated class to a degree, handling most of the implementation for this
 * simple application.
 * 
 * @author Max Fowler
 *
 */
public class SoylentPlant extends JFrame {

	private DefaultListModel<Cattle> cattleModel;
	private ArrayList<Cattle> processedCattle;
	private JList<Cattle> cattleList;
	private AudioClip backgroundMusic;
	private boolean playing;
	private JFrame myself;
	private JProgressBar cpb;
	private int quota;

	/**
	 * The constructor for SoylentPlant constructs the GUI components and sets
	 * up the files needed.
	 * 
	 * @throws MalformedURLException
	 *             - Related to music
	 */
	public SoylentPlant(int quota) throws MalformedURLException {
		this.quota = quota;

		URL imgurl = SoylentPlant.class.getResource("resources/cow.png");
		setTitle("Processing Plant");
		setBounds(100, 100, 400, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		myself = this;

		// Setting the frame image
		ImageIcon img = new ImageIcon(imgurl);
		setIconImage(img.getImage());

		// Set up the progress bar
		if (quota != -1) {
			cpb = new JProgressBar(0, this.quota);
			cpb.setValue(0);
			cpb.setStringPainted(true);
		} else {
			cpb = new JProgressBar(0, 100);
			cpb.setIndeterminate(true);
		}
		cpb.setBounds(240, 85, 136, 34);
		
		getContentPane().add(cpb);

		// Set up the button and such
		JButton processButton = new JButton("Process Cattle");
		processButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		processButton.setHorizontalTextPosition(SwingConstants.CENTER);
		processButton.setBounds(238, 130, 136, 68);
		ImageIcon buttonImg = makeIcon(imgurl, 50, 50);
		processButton.setIcon(buttonImg);
		getContentPane().add(processButton);

		processButton.addActionListener(new DequeueCattle());

		buildMenuBar();

		buildCattleList();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				saveCattle();
				System.exit(0);
			}

			private void saveCattle() {
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd_HH-mm-ss");
				File file = new File("CattleLog_" + dateFormat.format(date) + ".dat");
				BufferedWriter out;
				try {
					out = new BufferedWriter(new FileWriter(file));
					for (int i = 0; i < processedCattle.size(); i++) {
						out.write(processedCattle.get(i).cattleOutputData() + "\n");
					}
					out.close();
				} catch (IOException e) {

				}

			}
		});

	}

	/**
	 * This method builds the JList for cattle.
	 */
	private void buildCattleList() {
		processedCattle = new ArrayList<Cattle>();
		cattleModel = new DefaultListModel<Cattle>();
		cattleList = new JList<Cattle>();
		cattleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cattleList.setModel(cattleModel);

		JScrollPane jsp = new JScrollPane(cattleList);
		jsp.setLocation(20, 20);
		jsp.setSize(200, 300);
		getContentPane().add(jsp);

		Timer t = new Timer(1000, null);
		t.addActionListener(new QueueCattle());

		t.start();

	}

	/**
	 * Builds the JMenuBar and sets up the needed listeners.
	 * 
	 * @throws MalformedURLException
	 */
	private void buildMenuBar() throws MalformedURLException {
		JMenuBar jmb = new JMenuBar();
		JMenu fm = new JMenu("File");
		fm.setMnemonic(KeyEvent.VK_F);
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new BasicButtonHandler());
		exit.setMnemonic(KeyEvent.VK_X);
		fm.add(exit);
		
		
		JMenuItem quot = new JMenuItem("Set Quota");
		quot.addActionListener(new BasicButtonHandler());
		quot.setMnemonic(KeyEvent.VK_Q);
		fm.add(quot);
		
		jmb.add(fm);
		JCheckBoxMenuItem music = new JCheckBoxMenuItem("Toggle Music");
		music.setMnemonic(KeyEvent.VK_M);
		music.setSelected(false);
		jmb.add(music);
		music.addActionListener(new MusicHandler());
		setJMenuBar(jmb);
		playing = false;
		URL musicURL = SoylentPlant.class.getResource("resources/edin.wav");
		backgroundMusic = Applet.newAudioClip(musicURL);

	}

	public static void main(String[] args) {
		try {
			// select Look and Feel
			UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		SoylentPlant sp = null;
		try {
			CattleStats.intializeNameList();
			int quota;
			try {
				quota = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter daily quota",
						"Soylent Production Plant", JOptionPane.QUESTION_MESSAGE));
			} catch (Exception e) {
				quota = -1;
			}
			sp = new SoylentPlant(quota);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null, "The music failed", "D:", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		sp.setVisible(true);

	}

	/**
	 * This ActionListener handles the adding of cattle to the processing list.
	 * 
	 * @author Max Fowler
	 *
	 */
	private class QueueCattle implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			cattleModel.addElement(new Cattle());
		}

	}

	/**
	 * This listener removes cattle from the list, adding them to the processed
	 * list.
	 * 
	 * @author Max Fowler
	 *
	 */
	private class DequeueCattle implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int choice = cattleList.getSelectedIndex();
			if (choice != -1) {
				processedCattle.add(cattleModel.getElementAt(choice));
				cattleModel.remove(choice);
				if (cattleModel.size() != 0) {
					cattleList.setSelectedIndex(0);
				}
				int val = cpb.getValue();
				val += 1;
				cpb.setValue(val);
				if (val == quota) {
					JOptionPane.showMessageDialog(null, "Finished - good work today", "Soylent Production Plant",
							JOptionPane.INFORMATION_MESSAGE);
					myself.dispatchEvent(new WindowEvent(myself, WindowEvent.WINDOW_CLOSING));
				}
			}

		}

	}

	/**
	 * A catch all button handler for basic, extend-able functionality.
	 * 
	 * @author Max Fowler
	 *
	 */
	private class BasicButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Exit")) {
				// So our window listener closes
				myself.dispatchEvent(new WindowEvent(myself, WindowEvent.WINDOW_CLOSING));
			}
			if(e.getActionCommand().equals("Set Quota") && quota == -1){
				int qu;
				try {
					qu = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter daily quota",
							"Soylent Production Plant", JOptionPane.QUESTION_MESSAGE));
				} catch (Exception ex) {
					qu = -1;
				}
				
				quota = qu;
				if(quota != -1 && processedCattle.size() < quota){
					cpb.setMaximum(quota);
					cpb.setIndeterminate(false);
					cpb.setValue(processedCattle.size());
					cpb.setStringPainted(true);
				}
				else{
					quota = -1;
				}
			}
		}
	}

	/**
	 * Plays or stops music as needed
	 * 
	 * @author Max Fowler
	 *
	 */
	private class MusicHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!playing) {
				playing = true;

				backgroundMusic.loop();
			} else {
				playing = false;
				backgroundMusic.stop();
			}
		}
	}

	/**
	 * Helper method to scale images.
	 * 
	 * @param img
	 * @param i
	 * @param j
	 * @return - An image icon scaled
	 */
	private ImageIcon makeIcon(URL img, int i, int j) {
		// The process of scaling an image!
		ImageIcon ico = new ImageIcon(img);
		Image image = ico.getImage(); // transform it
		Image newimg = image.getScaledInstance(i, j, Image.SCALE_SMOOTH); // scale
																			// it
																			// the
																			// smooth
																			// way
		return new ImageIcon(newimg); // transform it back
	}
}
