package de.tum.in.www1.bamboo.server.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ReportParser {

    ParserStrategy parserStrategy;
    ParserPolicy parserPolicy = new ParserPolicy(this);

    public String transformToJSONReport(File file, String tool) throws ParserException {
        try {
            Report report = getReport(file, tool);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(report);
        }
        catch (ParserException | ParserConfigurationException | SAXException | IOException e) {
            throw new ParserException(e.getMessage(), e);
        }
    }

    private Report getReport(File file, String tool) throws ParserException, ParserConfigurationException, SAXException, IOException {
        parserPolicy.configure(tool);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = builderFactory.newDocumentBuilder();
        Document doc = db.parse(file);
        return parserStrategy.parse(doc, tool);
    }

    public void setParserStrategy(ParserStrategy parserStrategy) {
        this.parserStrategy = parserStrategy;
    }
}
