import org.w3c.dom.Document;
import org.w3c.dom.Element;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by micha on 26/11/14.
 */
public class PersistenceManager {
	private Document mDocument;
	private Element mRootElement;

	private static PersistenceManager instance = null;
	private String mExerciseNumber;

	public static PersistenceManager getInstance() {
		if (instance == null) {
			instance = new PersistenceManager();
		}
		return instance;
	}

	public void initXML() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			mDocument = docBuilder.newDocument();
			mRootElement = mDocument.createElement("root");
			mDocument.appendChild(mRootElement);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public Element appendTweet() {
		Element tweet = mDocument.createElement("tweet");
		mRootElement.appendChild(tweet);
		return tweet;
	}

	public Element createNode(String tagName) {
		return mDocument.createElement(tagName);
	}

	public Element createNode(String tagName, String value) {
		Element element = mDocument.createElement(tagName);
		element.appendChild(mDocument.createTextNode(value));

		return element;
	}

	// Erstellt für jeden Tweet eine Auflistung der verwendeten Hashtags
	public Element createHashtagNode(Status status) {
		HashtagEntity[] hashtags = status.getHashtagEntities();
		if (hashtags.length > 0) {
			Element hashtagsNode = createNode("hashtags");

			for (HashtagEntity hashtagEntity : hashtags) {
				Element tagNode = createNode("tag", hashtagEntity.getText());
				hashtagsNode.appendChild(tagNode);
			}
			return hashtagsNode;
		}

		return null;
	}

	public void persist() {
		if (mDocument == null) return;

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(mDocument);

			File targetFile = new File("tweets/tweets_aufgabe_" + mExerciseNumber + ".xml");
			File parent = targetFile.getParentFile();
			if (!parent.exists() && !parent.mkdirs()) {
				throw new IllegalStateException("Couldn't create dir: " + parent);
			}

			StreamResult result = new StreamResult(targetFile);
			transformer.transform(source, result);
			System.out.println("XML has been written");

			mDocument = null;
			mRootElement = null;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();

		} catch (TransformerException e) {
			e.printStackTrace();

		}
	}

	public void setExerciseNumber(String exerciseNumber) {
		this.mExerciseNumber = exerciseNumber;
	}
}
