package de.tum.in.www1.bamboo.server.parser;

import de.tum.in.www1.bamboo.server.parser.domain.Finding;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class SpotbugsParser implements ParserStrategy {

    public Report parse(Document doc, String tool) {
        Report report = new Report(tool);
        report.setThreshold(doc.getDocumentElement().getAttribute("threshold"));
        report.setEffort(doc.getDocumentElement().getAttribute("effort"));

        List<Finding> findings = new ArrayList<>();
        NodeList fileNodes = doc.getElementsByTagName("file");
        for (int i = 0; i < fileNodes.getLength(); i++) {
            Node fileNode = fileNodes.item(i);
            NamedNodeMap fileAttributes = fileNode.getAttributes();
            String classname = fileAttributes.getNamedItem("classname").getNodeValue();
            NodeList bugInstances = fileNode.getChildNodes();

            for (int j = 0; j < bugInstances.getLength(); j++) {
                Node bugInstance = bugInstances.item(j);
                NamedNodeMap bugInstanceAttributes = fileNode.getAttributes();
                String type = fileAttributes.getNamedItem("classname").getNodeValue();
                String priority = fileAttributes.getNamedItem("priority").getNodeValue();
                String category = fileAttributes.getNamedItem("category").getNodeValue();
                String message = fileAttributes.getNamedItem("message").getNodeValue();
                Integer line = Integer.valueOf(fileAttributes.getNamedItem("line").getNodeValue());
                findings.add(new Finding(type, priority, category, message, line));
            }
        }
        report.setFindings(findings);
        return report;
    }

}
