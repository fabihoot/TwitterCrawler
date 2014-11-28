import org.w3c.dom.Element;

import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

public class StatusHandler implements twitter4j.StatusListener {

	private final PersistenceManager mPersistenceManager;
	public int mMaxTweets = 0;
	public int mTweetCounter = 0;
	private int mIgnoredTweetCounter = 0;
	private StatusHandlerListener mListener;

	public StatusHandler() {
		mPersistenceManager =  PersistenceManager.getInstance();
	}

	public void onStatus(Status status) {
		if (true) {
			Element tweetNode = mPersistenceManager.appendTweet();

			Element tagNode = mPersistenceManager.createNode("id", String.valueOf(status.getId()));
			tweetNode.appendChild(tagNode);

			Element nameNode = mPersistenceManager.createNode("name", status.getUser().getName());
			tweetNode.appendChild(nameNode);

			Element textNode = mPersistenceManager.createNode("text", status.getText());
			tweetNode.appendChild(textNode);

			Element hashtagNode = mPersistenceManager.createHashtagNode(status);
			tweetNode.appendChild(hashtagNode);

			mTweetCounter++;
			mListener.numberOfAddedTweets(mTweetCounter);

			if (mTweetCounter >= mMaxTweets) {
				mPersistenceManager.persist();
				mListener.done();
			}
		} else {
			mIgnoredTweetCounter++;
			mListener.numberOfIgnoredTweet(mIgnoredTweetCounter);
		}
	}

	// Bedingung, dass Tweets nur in einer best. Sprache gespeichert werden
	private boolean statusWithLanguage(Status status, String language) {
		return status.getUser().getLang().equals(language.toLowerCase());
	}

	// Bedingung, dass nur Tweets, die ein Hashtag enthalten, gespeichert werden
	private boolean statusContainsHashtag(Status status) {
		return status.getHashtagEntities().length > 0;
	}

	// Bedingung, dass nur Tweets, die einen Lündercode enthalten, gespeichert
	// werden
	public boolean statusWithCountryCode(Status status, String countryCode) {
		return status.getPlace().getCountryCode().equals(countryCode.toUpperCase());
	}

	// Bedingung, dass nur Tweets gespeichert werden, welche eine bestimmtes
	// Hashtag enthalten
	private boolean statusContainsHashtag(Status status, String hashtag) {
		if (statusContainsHashtag(status)) {
			for (HashtagEntity entity : status.getHashtagEntities()) {
				if (entity.getText().toLowerCase()
						.equals(hashtag.toLowerCase())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
	}

	public void setListener(StatusHandlerListener listener) {
		mListener = listener;
	}

	public void setMaxTweets(int maxTweets) {
		mMaxTweets = maxTweets;
	}

	public void setTweetCounter(int tweetCounter) {
		mTweetCounter = tweetCounter;
	}

	public interface StatusHandlerListener {
		public void done();
		public void numberOfAddedTweets(int tweetCounter);
		public void numberOfIgnoredTweet(int ignoredTweetCounter);
	}
}
