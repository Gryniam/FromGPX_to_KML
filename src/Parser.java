import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import java.io.File;

public class Parser {
    public static void main(String[] args) {
        CreateKml();
    }

    private static void CreateKml() {
        File file = new File("input.xml");
        DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dbuilder = dbFact.newDocumentBuilder();
            Document xml = dbuilder.parse(file);
            Document doc = dbuilder.newDocument();
            xml.getDocumentElement().normalize();

            NodeList ndesc = xml.getDocumentElement().getElementsByTagName("cmt");
            NodeList nlink = xml.getDocumentElement().getElementsByTagName("link");
            NodeList nwpt = xml.getDocumentElement().getElementsByTagName("wpt");

            String sometext = "";
            String somedescription = "";
            String lon = "";
            String lat = "";
            Element KMLelement = doc.createElement("kml");
            doc.appendChild(KMLelement);
            Element document = doc.createElement("Document");
            KMLelement.appendChild(document);
            int listlenght = ndesc.getLength();
            for (int i = 0; i < listlenght; i++) {
                Node nNode = ndesc.item(i);
                sometext = nNode.getTextContent();

                Node link = nlink.item(i);
                NamedNodeMap attributes = link.getAttributes();
                Node attr = attributes.item(0);
                somedescription = attr.getTextContent();

                Node wpt = nwpt.item(i);
                NamedNodeMap wptatributes = wpt.getAttributes();
                Node wptattr = wptatributes.item(0);
                lat = wptattr.getTextContent();

                /*
                В одному блоці є два атрибути,
                Прийшлося звідти нам його дістати,
                Не знали як жити, не знали як бути,
                Прийшлося для лонга нам ще дописати.♥
                 */
                Node wptforlon = nwpt.item(i);
                NamedNodeMap wptforlon_atributes = wptforlon.getAttributes();
                Node wptlonattr = wptforlon_atributes.item(1);
                lon = wptlonattr.getTextContent();

                Element placemark = doc.createElement("Placemark");
                document.appendChild(placemark);

                Element name = doc.createElement("name");
                placemark.appendChild(name);
                name.appendChild(doc.createTextNode(sometext));

                Element description = doc.createElement("description");
                placemark.appendChild(description);
                description.appendChild(doc.createTextNode(somedescription));

                Element point = doc.createElement("Point");
                placemark.appendChild(point);

                Element coordinates = doc.createElement("coordinates");
                point.appendChild(coordinates);
                coordinates.appendChild(doc.createTextNode(lat));
                coordinates.appendChild(doc.createTextNode(", "));
                coordinates.appendChild(doc.createTextNode(lon));
            }

            //Створення kml файлу і запис в нього даних
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("newkmlfile.kml"));
            transformer.transform(source, result);
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
