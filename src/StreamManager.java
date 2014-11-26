import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StreamManager {
	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";
	private static final String ACCESS_TOKEN = "";
	private static final String ACCESS_TOKEN_SECRET = "";

	private TwitterStream stream;

	public StreamManager() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

		configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET)
				.setDebugEnabled(true);

		stream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
	}

	public void start() {
		stream.sample();
	}

	public void stop() {
		stream.shutdown();
	}

	public void setListener(StatusHandler listener) {
		stream.addListener(listener);
	}
}
