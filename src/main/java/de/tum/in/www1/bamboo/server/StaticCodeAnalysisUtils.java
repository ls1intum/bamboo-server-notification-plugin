package de.tum.in.www1.bamboo.server;

import org.apache.commons.io.FilenameUtils;

public class StaticCodeAnalysisUtils {

    /**
     * Returns whether the specified filename is a file supported by the
     * static code analysis parser.
     *
     * @param artifactLabel the label of the artifact
     * @param artifactFilename the file name of the artifact file.
     * @return true if the file is supported by the static code analysis parser.
     */
    public static boolean isStaticCodeAnalysisArtifact(String artifactLabel, String artifactFilename) {
        // The static code analysis parser only supports xml files and json files (just for parsing gcc output).
        if (!(FilenameUtils.getExtension(artifactFilename).equals("xml") || FilenameUtils.getExtension(artifactFilename).equals("json"))) {
            return false;
        }

        for (StaticCodeAnalysisArtifacts staticCodeAnalysisArtifact : StaticCodeAnalysisArtifacts.values()) {
            if (staticCodeAnalysisArtifact.toString().equalsIgnoreCase(artifactLabel)) {
                return true;
            }
        }

        return false;
    }
}
