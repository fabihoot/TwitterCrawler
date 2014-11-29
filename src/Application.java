import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by micha on 26/11/14.
 */
public class Application implements Window.WindowListener, StatusHandler.StatusHandlerListener {

	private StreamManager mStreamManager;
	private final StatusHandler mStatusHandler;
	private final Window mWindow;

	public Application() {
		mWindow = new Window();
		mWindow.setListener(this);

		mStatusHandler = new StatusHandler();
		mStatusHandler.setListener(this);

		mStreamManager = new StreamManager();
		mStreamManager.setListener(mStatusHandler);
	}

	@Override
	public void onStart(String exerciseNumber, String maxTweets) {
		PersistenceManager.getInstance().initXML();
		PersistenceManager.getInstance().setExerciseNumber(exerciseNumber);

		mStatusHandler.setMaxTweets(Integer.parseInt(maxTweets));
		mStatusHandler.setTweetCounter(0);

		mWindow.setAddedTweets(0);
		mWindow.setIgnoredTweets(0);

		mStreamManager.start();
	}

	@Override
	public void onStop() {
		if (mStreamManager != null) {
			mStreamManager.stop();
		}

		PersistenceManager.getInstance().persist();
	}

	@Override
	public void done() {
		mStreamManager.stop();
	}

	@Override
	public void numberOfAddedTweets(int tweetCounter) {
		mWindow.setAddedTweets(tweetCounter);
	}

	@Override
	public void numberOfIgnoredTweet(int ignoredTweetCounter) {
		mWindow.setIgnoredTweets(ignoredTweetCounter);
	}
}

class Window extends JFrame {

	JFrame frame;
	private WindowListener mListener;
	private final JLabel mStoredTweetsLabel;
	private final JLabel mIgnoredTweetsLabel;

	public void setListener(WindowListener listener) {
		mListener = listener;
	}

	public Window() {
		frame = new JFrame();
		frame.setTitle("Twitter Crawler");
		frame.setBounds(0, 0, 160, 240);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JLabel exerciseLabel = new JLabel("Aufgabe Nr.:");
		exerciseLabel.setBounds(10, 10, 80, 20);
		frame.getContentPane().add(exerciseLabel);

		final JTextField exerciseNumberField = new JTextField();
		exerciseNumberField.setBounds(110, 10, 30, 20);
		exerciseNumberField.setHorizontalAlignment(JTextField.RIGHT);
		frame.getContentPane().add(exerciseNumberField);

		JLabel tweetCountLabel = new JLabel("Anzahl:");
		tweetCountLabel.setBounds(10, 40, 80, 20);
		frame.getContentPane().add(tweetCountLabel);

		final JTextField tweetCountField = new JTextField();
		tweetCountField.setBounds(90, 40, 50, 20);
		tweetCountField.setHorizontalAlignment(JTextField.RIGHT);
		frame.getContentPane().add(tweetCountField);

		JButton startButton = new JButton("Starte Crawler");
		startButton.setBounds(10, 70, 140, 25);
		frame.getContentPane().add(startButton);

		JButton closeButton = new JButton("Beenden");
		closeButton.setBounds(10, 100, 140, 25);
		frame.getContentPane().add(closeButton);

		mStoredTweetsLabel = new JLabel();
		mStoredTweetsLabel.setBounds(10, 140, 90, 16);
		frame.getContentPane().add(mStoredTweetsLabel);

		mIgnoredTweetsLabel = new JLabel();
		mIgnoredTweetsLabel.setBounds(10, 170, 90, 16);
		frame.getContentPane().add(mIgnoredTweetsLabel);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mListener.onStart(exerciseNumberField.getText(), tweetCountField.getText());
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mListener.onStop();
			}
		});

		validate();
		repaint();

		frame.setVisible(true);
	}

	public void setAddedTweets(int tweetCounter) {
		mStoredTweetsLabel.setText("Tweets: " + tweetCounter);
	}

	public void setIgnoredTweets(int ignoredTweetCounter) {
		mIgnoredTweetsLabel.setText("Ignoriert: " + ignoredTweetCounter);
	}

	public interface WindowListener {
		public void onStart(String exerciseNumber, String maxTweets);
		public void onStop();
	}
}
