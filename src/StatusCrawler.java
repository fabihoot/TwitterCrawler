import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StatusCrawler {

	// Authentifizierungstokens f�r den Zugriff auf die Twitter API
	// Bite eigene Logins eintragen!
	private static final String CONSUMER_KEY = "jsT8x1bQpzWFvUWD10zyA";
	private static final String CONSUMER_SECRET = "221c0J7TFprjEAmIQSdhDWzwWtPscNb8eS6jDVlc";
	private static final String ACCESS_TOKEN = "2289566022-Q13zR4GVQSO8qJZiIxc8ureuwJJwiw5BqFqMLxD";
	private static final String ACCESS_TOKEN_SECRET = "nXYrEgYtq3yBHJ9j6Oh44fgd6oEugOqih352VrNzcUkHu";

	private ConfigurationBuilder config;
	public StatusListener listener;
	private TwitterStream twitterStream;

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	private Element rootElement;

	public StatusCrawler(int taskNumber) {
		initAuth();
		initXML();
		initStream(taskNumber);

	}

	public int getTweetCount() {
		return listener.tweetCounter;
	}

	public int getMaxTweetCount() {
		return listener.maxtweets;
	}

	public void run() {
		twitterStream.sample();
	}

	private void initAuth() {
		setAuth();
	}

	private void initStream(int taskNumber) {
		listener = new StatusListener(doc, rootElement, this, taskNumber);
		twitterStream = new TwitterStreamFactory(config.build()).getInstance();
		twitterStream.addListener(listener);

	}

	private void setAuth() {
		config = new ConfigurationBuilder();
		config.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

	}

	private void initXML() {

		try {
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("root");
			doc.appendChild(rootElement);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	public void writeXML(int taskNumber) {

		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc);

			File targetFile = new File("tweets/tweets_aufgabe_" + taskNumber + ".xml");
			File parent = targetFile.getParentFile();
			if(!parent.exists() && !parent.mkdirs()){
			    throw new IllegalStateException("Couldn't create dir: " + parent);
			}
			
			StreamResult result = new StreamResult(targetFile);
			transformer.transform(source, result);
			twitterStream.shutdown();
			System.out.println("XML has been written");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();

		} catch (TransformerException e) {
			e.printStackTrace();

		}

	}

}
