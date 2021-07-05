package de.tum.in.www1.bamboo.server;

/**
 * The name of the artifacts generated by Bamboo that are
 * supported by the static code analysis parser.
 *
 * The names must match the names defined in the Artemis project
 * within the StaticCodeAnalysisTool.java file.
 *
 * The names must match the artifact name defined within the Bamboo
 * plan configuration view.
 */
public enum StaticCodeAnalysisArtifacts {
    CHECKSTYLE,
    SPOTBUGS,
    PMD,
    PMD_CPD,
    SWIFTLINT
}
