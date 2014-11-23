import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class MainApplication {
	public static StatusCrawler crawler = null;

	public static void main(String[] args) {
		MainFrame window = new MainFrame();
		window.frame.setVisible(true);
	}

	public static void testTwitterStream(int taskNumber) {
		crawler = new StatusCrawler(taskNumber);
		crawler.run();
	}

	public static void shutdown() {
		if (crawler != null) {
			crawler.shutdown();
		}

		System.exit(0);
	}

	public static int getTweetCount() {
		return crawler.getTweetCount();
	}

	public static int getmaxTweetCount() {
		return crawler.getMaxTweetCount();
	}

}

//Hilfsklasse für eine GUI zur einfacheren Bedienung des Crawlers
class MainFrame extends JFrame {
	JFrame frame;
	private JTextField txtTaskNumber;

	public MainFrame() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Twitter Crawler");
		frame.setBounds(100, 100, 289, 199);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		frame.setBounds(100, 100, 289, 199);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblTaskNumber = new JLabel("Aufgabe Nr.:");
		lblTaskNumber.setBounds(12, 13, 81, 16);
		frame.getContentPane().add(lblTaskNumber);

		txtTaskNumber = new JTextField();
		txtTaskNumber.setBounds(92, 10, 59, 22);
		frame.getContentPane().add(txtTaskNumber);
		txtTaskNumber.setColumns(10);

		JButton startButton = new JButton("Starte Crawler");
		startButton.setBounds(12, 57, 141, 25);
		frame.getContentPane().add(startButton);

		JButton closeButton = new JButton("Beenden");
		closeButton.setBounds(12, 90, 141, 25);
		frame.getContentPane().add(closeButton);

		final JLabel lblCount = new JLabel("Anzahl Tweets: 0");
		lblCount.setBounds(12, 125, 199, 16);
		frame.getContentPane().add(lblCount);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				MainApplication.testTwitterStream(Integer.valueOf(txtTaskNumber
						.getText()));
				Timer timer = new Timer(100, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						lblCount.setText("Count: " + MainApplication.getTweetCount()
								+ " / " + MainApplication.getmaxTweetCount());

					}
				});
				timer.start();
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainApplication.shutdown();
			}
		});

		validate();
		repaint();
	}

}
