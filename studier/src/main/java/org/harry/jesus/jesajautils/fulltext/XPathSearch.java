package org.harry.jesus.jesajautils.fulltext;

import generated.XMLBIBLE;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.Optional;

/**
 * The type X path search.
 */
public class XPathSearch {

    private final XMLBIBLE actBible;

    private final Optional<Document> actBibleDoc;

    private XPathFactory factory;

    /**
     * Instantiates a new X path search.
     *
     * @param actBible the act bible
     */
    public XPathSearch(XMLBIBLE actBible) {
        this.actBible = actBible;
        this.actBibleDoc = initialize();
    }

    /**
     * Initialize optional.
     *
     * @return the optional
     */
    public Optional<Document> initialize() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(XMLBIBLE.class);
            Marshaller marshaller = ctx.createMarshaller();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            Document doc = domBuilder.newDocument();
            marshaller.marshal(actBible, doc);
            factory = XPathFactory.newInstance();
            return Optional.of(doc);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     * Search node list optional.
     *
     * @param xPathExpr the x path expr
     * @return the optional
     */
    public Optional<NodeList> searchNodeList(String xPathExpr) {
        try {
            XPath xpath = factory.newXPath();
            NodeList result = (NodeList) xpath
                    .evaluate(xPathExpr, actBibleDoc, XPathConstants.NODESET);
            return Optional.of(result);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     * Search node optional.
     *
     * @param xPathExpr the x path expr
     * @return the optional
     */
    public Optional<Node> searchNode(String xPathExpr) {
        try {
            XPath xpath = factory.newXPath();
            Node result = (Node) xpath
                    .evaluate(xPathExpr, actBibleDoc, XPathConstants.NODE);
            return Optional.of(result);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
