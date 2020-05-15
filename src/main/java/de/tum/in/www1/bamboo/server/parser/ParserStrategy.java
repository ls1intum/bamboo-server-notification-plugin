package de.tum.in.www1.bamboo.server.parser;

import de.tum.in.www1.bamboo.server.parser.domain.Report;
import org.w3c.dom.Document;


public interface ParserStrategy {

    Report parse(Document doc, String tool);

}
