import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

import java.util.List;

public class StatusListener implements twitter4j.StatusListener {

	private Document mDoc;
	private Element mRootElement;
	private StatusCrawler mCrawler;
	public int mTweetCounter = 0;
	private Status mStatus;
	private int mTaskNumber;

	// Legt die maximale Anzahl der Tweets fest die gecrawlet werden soll
	public static final int sMaxTweets = 10;

	public StatusListener(Document doc, Element rootElement,
			StatusCrawler crawler, int taskNumber) {
		mDoc = doc;
		mRootElement = rootElement;
		mCrawler = crawler;
		mTaskNumber = taskNumber;
	}

	// Methode wird aufgerufen wenn ein ein Tweet gecrawlet wurde
	// Status-Objekt enthüllt alle Informationen die von Twitter zur Verfgung
	// gestellt werden
	public void onStatus(Status status) {
		mStatus = status;

		// Abfrage ob ein Tweet gespeichert werden soll
		boolean condition = false;
		if (condition) {
			mTweetCounter++;

			// Wurzelelement fuer jeden Tweet erstellen
			try {
				Element tweet = mDoc.createElement("tweet");
				mRootElement.appendChild(tweet);

				// Element erstellen mit Inhalt fuellen und in die Baumstruktur
				// einhaengen

				createElementId(tweet);
				createElementUser(tweet);
				createElementScreenname(tweet);
				createElementText(tweet);

				System.out.println("tweet added");
			} catch (DOMException e) {
				e.printStackTrace();
			}

			// Abbruchbedingung wenn die maximale Anzahl der Tweets erreicht
			// wurde
			if (mTweetCounter >= sMaxTweets) {
				mCrawler.writeXML(mTaskNumber);
			}
		} else {
			System.out.println("tweet not added");
		}
	}

	/*********************************************************
	 * 
	 * Hier werden die Bedingungen definiert,nach denen Tweets untersucht werden
	 * sollen
	 * 
	 *********************************************************/

	// Bedingung, dass Tweets nur in einer best. Sprache gespeichert werden
	public boolean tweetWithLanguage(String language) {
		return mStatus.getUser().getLang().equals(language);
	}

	// Bedingung, dass nur Tweets, die ein best. Zeichen (Hashtag) enthalten,
	// gespeichert werden
	public boolean tweetContainsHash() {
		String hashtag = "#";
		return mStatus.getText().contains(hashtag);
	}

	// Bedingung, dass nur Tweets, die ein Hashtag enthalten, gespeichert werden
	public boolean tweetContainsHashtag() {
		return mStatus.getHashtagEntities().length > 0;
	}

	// Bedingung, dass nur Tweets, die einen Lündercode enthalten, gespeichert
	// werden
	public boolean tweetWithCountryCode(String countryCode) {
		return mStatus.getPlace().getCountryCode().equals(countryCode);
	}

	// Bedingung, dass nur Tweets gespeichert werden, welche eine bestimmtes
	// Hashtag enthalten
	public boolean tweetContainsSpecificHashtag() {
		String hashtag = "#MtvStars";
		if (tweetContainsHashtag()) {
			for (HashtagEntity entity : mStatus.getHashtagEntities()) {
				if (entity.getText().toLowerCase()
						.equals(hashtag.toLowerCase())) {
					return true;
				}
			}

		}

		return false;
	}

	/*********************************************************
	 * 
	 * Hier stehen die Aufrufe für das Ablegen von Informationen im XML
	 * 
	 *********************************************************/

	// Erstellt für jeden Tweet ein Kindelement mit der ID des Users
	private void createElementId(Element tweet) {
		// Auslesen des gewünschten Wertes aus dem Statusobjekt
		// Erstellen des Tag-Elements mit entspr. Namen
		// Einfügen des Wertes in das Tag-Element
		// Anhängen des Tag-Elements in die Baumstruktur des .xml-Files

		String valueID = String.valueOf(mStatus.getUser().getId());
		Element fieldId = mDoc.createElement("id");
		fieldId.appendChild(mDoc.createTextNode(valueID));
		tweet.appendChild(fieldId);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Namen des Users
	private void createElementUser(Element tweet) {
		String valueUser = mStatus.getUser().getName();
		Element fieldUser = mDoc.createElement("user");
		fieldUser.appendChild(mDoc.createTextNode(valueUser));
		tweet.appendChild(fieldUser);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Nickname des Users
	private void createElementScreenname(Element tweet) {
		String valueScreenname = mStatus.getUser().getScreenName();
		Element fieldScreenName = mDoc.createElement("screenName");
		fieldScreenName.appendChild(mDoc.createTextNode(valueScreenname));
		tweet.appendChild(fieldScreenName);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Text des Tweets
	private void createElementText(Element tweet) {
		String valueText = mStatus.getText();
		Element fieldText = mDoc.createElement("text");
		fieldText.appendChild(mDoc.createTextNode(valueText));
		tweet.appendChild(fieldText);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Datum und der Uhrzeit
	// des Tweets
	private void createElementDate(Element tweet) {
		String valueTime = String.valueOf(mStatus.getCreatedAt());
		Element fieldTime = mDoc.createElement("time");
		fieldTime.appendChild(mDoc.createTextNode(valueTime));
		tweet.appendChild(fieldTime);
	}

	// Erstellt für jeden Tweet ein Kindelement mit der eingestellten Sprache
	// des Users
	private void createElementLanguage(Element tweet) {
		String valueLanguage = mStatus.getUser().getLang();
		Element fieldLang = mDoc.createElement("language");
		fieldLang.appendChild(mDoc.createTextNode(valueLanguage));
		tweet.appendChild(fieldLang);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Ort des Users
	private void createElementLocation(Element tweet) {
		String valueLocation = mStatus.getUser().getLocation();
		Element fieldLocation = mDoc.createElement("location");
		fieldLocation.appendChild(mDoc.createTextNode(valueLocation));
		tweet.appendChild(fieldLocation);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Lündercode des Users
	private void createElementCountryCode(Element tweet) {
		String valueCountryCode = mStatus.getPlace().getCountryCode();
		Element fieldCountryCode = mDoc.createElement("countryCode");
		fieldCountryCode.appendChild(mDoc.createTextNode(valueCountryCode));
		tweet.appendChild(fieldCountryCode);
	}

	// Erstellt für jeden Tweet eine Auflistung der verwendeten Hashtags
	private void createElementHashtag(Element tweet) {
		HashtagEntity[] allHashtags = mStatus.getHashtagEntities();
		if (allHashtags.length > 0) {
			Element fieldHashtags = mDoc.createElement("hashtags");
			for (HashtagEntity hashtagEntity : allHashtags) {
				Element fieldTag = mDoc.createElement("tag");
				fieldTag.appendChild(mDoc.createTextNode(hashtagEntity
						.getText()));
				fieldHashtags.appendChild(fieldTag);
			}
			tweet.appendChild(fieldHashtags);
		}
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
}
