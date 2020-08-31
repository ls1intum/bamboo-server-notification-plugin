package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Report;
import nu.xom.Document;


public interface ParserStrategy {

    /**
     * Parse a static code analysis report into a common Java representation.
     *
     * @param doc XOM DOM Document
     * @return Report object containing the parsed report information
     */
    Report parse(Document doc);
}
