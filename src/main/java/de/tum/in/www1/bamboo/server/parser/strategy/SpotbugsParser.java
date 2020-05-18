package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Finding;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

// TODO: Use XOM, JDOM, DOM4J instead of DOM (for Collection support)
public class SpotbugsParser implements ParserStrategy {

    private static final String FILE_TAG = "file";
    private static final String FILE_ATT_CLASSNAME = "classname";
    private static final String BUGINSTANCE_ATT_TYPE = "type";
    private static final String BUGINSTANCE_ATT_PRIORITY = "priority";
    private static final String BUGINSTANCE_ATT_CATEGORY = "category";
    private static final String BUGINSTANCE_ATT_MESSAGE = "message";
    private static final String BUGINSTANCE_ATT_LINENUMBER = "lineNumber";

    @Override
    public Report parse(Document doc, String tool) {
        Report report = new Report(tool);
        List<Finding> findings = new ArrayList<>();
        NodeList fileNodes = doc.getElementsByTagName(FILE_TAG);

        for (int i = 0; i < fileNodes.getLength(); i++) {
            Node fileNode = fileNodes.item(i);
            NamedNodeMap fileAttributes = fileNode.getAttributes();
            String classname = fileAttributes.getNamedItem(FILE_ATT_CLASSNAME).getNodeValue();
            NodeList bugInstances = fileNode.getChildNodes();

            for (int j = 0; j < bugInstances.getLength(); j++) {
                Node bugInstance = bugInstances.item(j);
                NamedNodeMap bugInstanceAttributes = bugInstance.getAttributes();
                String type = bugInstanceAttributes.getNamedItem(BUGINSTANCE_ATT_TYPE).getNodeValue();
                String priority = bugInstanceAttributes.getNamedItem(BUGINSTANCE_ATT_PRIORITY).getNodeValue();
                String category = bugInstanceAttributes.getNamedItem(BUGINSTANCE_ATT_CATEGORY).getNodeValue();
                String message = bugInstanceAttributes.getNamedItem(BUGINSTANCE_ATT_MESSAGE).getNodeValue();
                Integer line = Integer.parseInt(bugInstanceAttributes.getNamedItem(BUGINSTANCE_ATT_LINENUMBER).getNodeValue());
                findings.add(new Finding(classname, type, priority, category, message, line));
            }
        }
        report.setFindings(findings);
        return report;
    }

}
