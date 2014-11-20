import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

public class StatusListener implements twitter4j.StatusListener {

	private Document doc;
	private Element rootElement;
	private StatusCrawler crawler;
	public int tweetCounter = 0;
	private Status status;
	private int taskNumber;

	// Legt die maximale Anzahl der Tweets fest die gecrawlet werden soll
	public static final int maxtweets = 10;

	public StatusListener(Document doc, Element rootElement,
			StatusCrawler crawler, int taskNumber) {
		this.doc = doc;
		this.rootElement = rootElement;
		this.crawler = crawler;
		this.taskNumber = taskNumber;
	}

	//Bedingung, dass Tweets nur in einer best. Sprache gespeichert werden
	public boolean saveTweetsInLanguage() {
		String language = "en";
		if (status.getUser().getLang().equals(language)) {
			return true;
		}
		return false;
	}

	//Bedingung, dass nur Tweets, die ein best. Zeichen (Hashtag) enthalten, gespeichert werden
	public boolean saveTweetsWithHashtag() {
		String hashtag = "#";
		if (status.getText().contains(hashtag)) {
			return true;
		}
		return false;
	}

	//Bedingung, dass nur Tweets, die ein Hashtag enthalten, gespeichert werden
	public boolean saveTweetsWithTag() {
		if (status.getHashtagEntities().length > 0) {
			return true;
		}
		return false;
	}
	//Bedingung, dass nur Tweets, die einen Lündercode enthalten, gespeichert werden
	public boolean saveTweetWithCountryCode(){
		if(!status.getPlace().getCountryCode().equals("")){
			return true;
		}
		return false;
	}

	//Methode wird aufgerufen wenn ein ein Tweet gecrawlet wurde
	//Status-Objekt enthült alle Informationen die von Twitter zur Verfgung gestellt werden
	public void onStatus(Status status) {
		this.status = status;

		//Abfrage der Bedingungen damit ein Tweet gespeichert werden soll
		boolean conditionA = saveTweetsInLanguage();
		boolean conditionB = saveTweetsWithHashtag();
		boolean conditionC = saveTweetsWithTag();
		boolean conditionD = saveTweetWithCountryCode();
		if (conditionC) {
			System.out.println("tweet saved");
		} else {
			System.out.println("tweet not saved");
			return;
		}

		//Abbruchbedingung wenn die maximale Anzahl der Tweets erreicht wurde
		if (tweetCounter >= maxtweets) {
			crawler.writeXML(taskNumber);
			return;
		}

		tweetCounter++;

		// Wurzelelement fuer jeden Tweet erstellen
		try {
			Element tweet = doc.createElement("tweet");
			rootElement.appendChild(tweet);

			// Element erstellen mit Inhalt fuellen und in die Baumstruktur
			// einhaengen

			createElementId(tweet);
			createElementUser(tweet);
			createElementScreenname(tweet);
			createElementText(tweet);
			//createElementDate(tweet);
			// createElementLanguage(tweet);
			// createElementLocation(tweet);
			//createElementCountryCode(tweet);
			createElementHashtag(tweet);

		} catch (DOMException e) {
			e.printStackTrace();
		}
	}

	// Erstellt für jeden Tweet ein Kindelement mit der ID des Users
	private void createElementId(Element tweet) {

		// Auslesen des gewünschten Wertes aus dem Statusobjekt
		// Erstellen des Tag-Elements mit entspr. Namen
		// Einfügen des Wertes in das Tag-Element
		// Anhängen des Tag-Elements in die Baumstruktur des .xml-Files

		String valueID = String.valueOf(status.getUser().getId());
		Element fieldId = doc.createElement("id");
		fieldId.appendChild(doc.createTextNode(valueID));
		tweet.appendChild(fieldId);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Namen des Users
	private void createElementUser(Element tweet) {
		String valueUser = status.getUser().getName();
		Element fieldUser = doc.createElement("user");
		fieldUser.appendChild(doc.createTextNode(valueUser));
		tweet.appendChild(fieldUser);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Nickname des Users
	private void createElementScreenname(Element tweet) {
		String valueScreenname = status.getUser().getScreenName();
		Element fieldScreenName = doc.createElement("screenName");
		fieldScreenName.appendChild(doc.createTextNode(valueScreenname));
		tweet.appendChild(fieldScreenName);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Text des Tweets
	private void createElementText(Element tweet) {
		String valueText = status.getText();
		Element fieldText = doc.createElement("text");
		fieldText.appendChild(doc.createTextNode(valueText));
		tweet.appendChild(fieldText);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Datum und der Uhrzeit
	// des Tweets
	private void createElementDate(Element tweet) {
		String valueTime = String.valueOf(status.getCreatedAt());
		Element fieldTime = doc.createElement("time");
		fieldTime.appendChild(doc.createTextNode(valueTime));
		tweet.appendChild(fieldTime);
	}

	// Erstellt für jeden Tweet ein Kindelement mit der eingestellten Sprache
	// des Users
	private void createElementLanguage(Element tweet) {
		String valueLanguage = status.getUser().getLang();
		Element fieldLang = doc.createElement("language");
		fieldLang.appendChild(doc.createTextNode(valueLanguage));
		tweet.appendChild(fieldLang);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Ort des Users
	private void createElementLocation(Element tweet) {
		String valueLocation = status.getUser().getLocation();
		Element fieldLocation = doc.createElement("location");
		fieldLocation.appendChild(doc.createTextNode(valueLocation));
		tweet.appendChild(fieldLocation);
	}

	// Erstellt für jeden Tweet ein Kindelement mit dem Lündercode des Users
	private void createElementCountryCode(Element tweet) {
		String valueCountryCode = status.getPlace().getCountryCode();
		Element fieldCountryCode = doc.createElement("countryCode");
		fieldCountryCode.appendChild(doc.createTextNode(valueCountryCode));
		tweet.appendChild(fieldCountryCode);
	}

	// Erstellt für jeden Tweet eine Auflistung der verwendeten Hashtags
	private void createElementHashtag(Element tweet) {
		HashtagEntity[] allHashtags = status.getHashtagEntities();
		if (allHashtags.length > 0) {
			Element fieldHashtags = doc.createElement("hashtags");
			for (HashtagEntity hashtagEntity : allHashtags) {
				Element fieldTag = doc.createElement("tag");
				fieldTag.appendChild(doc.createTextNode(hashtagEntity.getText()));
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
