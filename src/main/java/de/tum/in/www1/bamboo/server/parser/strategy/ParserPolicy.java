package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.ReportParser;
import de.tum.in.www1.bamboo.server.parser.domain.StaticAssessmentTool;
import de.tum.in.www1.bamboo.server.parser.exception.UnsupportedToolException;

public class ParserPolicy {

    private ReportParser parser;

    public ParserPolicy(ReportParser parser) {
        this.parser = parser;
    }

    /**
     * Selects the appropriate parsing strategy.
     *
     * @param tool String identifying the static code analysis tool
     * @throws UnsupportedToolException - If the specified tool is not supported
     */
    public void configure(String tool) {
        // TODO: Inspect the document (identifying unique nodes) to select the appropriate strategy
        if (StaticAssessmentTool.SPOTBUGS.name().equalsIgnoreCase(tool)) {
            parser.setParserStrategy(new SpotbugsParser());
        }
        else if (StaticAssessmentTool.CHECKSTYLE.name().equalsIgnoreCase(tool)) {
            parser.setParserStrategy(new CheckstyleParser());
        }
        else if (StaticAssessmentTool.PMD.name().equalsIgnoreCase(tool)) {
            parser.setParserStrategy(new PMDParser());
        }
        else {
            throw new UnsupportedToolException("Report parsing for tool " + tool + " is not supported");
        }
    }
}
