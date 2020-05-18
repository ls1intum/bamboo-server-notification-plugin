package de.tum.in.www1.bamboo.server.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import de.tum.in.www1.bamboo.server.parser.exception.ParserException;
import de.tum.in.www1.bamboo.server.parser.exception.UnsupportedToolException;
import de.tum.in.www1.bamboo.server.parser.strategy.ParserPolicy;
import de.tum.in.www1.bamboo.server.parser.strategy.ParserStrategy;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

// TODO: Split this class into a library API and a proper strategy context
public class ReportParser {

    ParserStrategy parserStrategy;
    ParserPolicy parserPolicy = new ParserPolicy(this);

    /**
     * Transform a given static code analysis report into a JSON representation.
     * All supported tools share the same JSON format.
     *
     * @param file Reference to the static code analysis report
     * @param tool String determining to which tool the report belongs
     * @return Static code analysis report represented as a JSON String
     * @throws ParserException - If any error occurs parsing the report
     */
    public String transformToJSONReport(File file, String tool) throws ParserException {
        try {
            Report report = getReport(file, tool);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(report);
        }
        catch (Exception e) {
            throw new ParserException(e.getMessage(), e);
        }
    }

    private Report getReport(File file, String tool) throws UnsupportedToolException, ParserConfigurationException, SAXException, IOException {
        // Configure the strategy given the name of the tool
        parserPolicy.configure(tool);

        // TODO: Use XOM, JDOM, DOM4J instead of DOM (for Collection support)
        // Build the DOM and parse the document using the configured strategy
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = builderFactory.newDocumentBuilder();
        Document doc = db.parse(file);
        return parserStrategy.parse(doc, tool);
    }

    public void setParserStrategy(ParserStrategy parserStrategy) {
        this.parserStrategy = parserStrategy;
    }
}
