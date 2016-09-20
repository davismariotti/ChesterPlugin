package info.gomeow.chester.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Updater {

    public Updater(String v) throws SAXException, IOException, ParserConfigurationException {
        oldVersion = v.substring(0, 5);
        Document feed = loadDocument("https://dev.bukkit.org/bukkit-plugins/chester/files.rss");
        newVersion = feed.getElementsByTagName("title").item(1).getTextContent().substring(1);
        link = feed.getElementsByTagName("link").item(1).getTextContent();
        link = new BufferedReader(new InputStreamReader(new URL("https://is.gd/create.php?format=simple&url=" + link).openStream())).readLine();
        if(v.contains("SNAPSHOT") && !newVersion.equals(oldVersion)) {
            update = false;
            return;
        }
        update = !newVersion.equals(oldVersion);
    }

    public Document loadDocument(String url) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new URL(url).openStream());
    }

    private String oldVersion;
    private String newVersion;
    private String link;
    private boolean update;

    public boolean getUpdate() {
        return update;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public String getLink() {
        return link;
    }
}