package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Report;
import org.w3c.dom.Document;


public interface ParserStrategy {

    /**
     * Parse a static code analysis report into a common Java representation.
     *
     * @param doc W3C DOM Document
     * @param tool String identifying the tool
     * @return Report object containing the parsed report information
     */
    Report parse(Document doc, String tool);

}
