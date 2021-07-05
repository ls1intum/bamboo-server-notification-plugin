package de.tum.in.www1.bamboo.server;

import org.apache.commons.io.FilenameUtils;

public class StaticCodeAnalysisUtils {

    /**
     * Returns whether the specified filename is a file supported by the
     * static code analysis parser.
     *
     * @param filename the name of the file with the extension.
     * @return true if the file is supported by the static code analysis parser.
     */
    public static boolean isStaticCodeAnalysisArtifactFile(String filename) {
        // The static code analysis parser only supports xml files.
        if (!FilenameUtils.getExtension(filename).equals("xml")){
            return false;
        }

        for (StaticCodeAnalysisArtifacts staticCodeAnalysisArtifact : StaticCodeAnalysisArtifacts.values()) {
            if (staticCodeAnalysisArtifact.getArtifactFilename().equals(filename)){
                return true;
            }
        }

        return false;
    }
}
