package de.tum.in.www1.bamboo.server;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.plan.PlanKey;

import org.apache.log4j.Logger;

public class LoggingUtils {

    public static void logDebug(String message, PlanKey planKey, Logger log) {
        String planKeyString = planKey != null ? "[" + planKey + "] " : "";
        String logMessage = "[BAMBOO-SERVER-NOTIFICATION] " + planKeyString + message;
        log.debug(logMessage);
    }

    public static void logInfo(String message, BuildLoggerManager buildLoggerManager, PlanKey planKey, Logger log) {
        String planKeyString = planKey != null ? "[" + planKey + "] " : "";
        String logMessage = "[BAMBOO-SERVER-NOTIFICATION] " + planKeyString + message;
        if (buildLoggerManager != null && planKey != null) {
            BuildLogger buildLogger = buildLoggerManager.getLogger(planKey);
            buildLogger.addBuildLogEntry(logMessage);
        }
        log.info(logMessage);
    }

    public static void logError(String message, BuildLoggerManager buildLoggerManager, PlanKey planKey, Logger log, Exception e) {
        String planKeyString = planKey != null ? "[" + planKey + "] " : "";
        String logMessage = "[BAMBOO-SERVER-NOTIFICATION] " + planKeyString + message;
        if (buildLoggerManager != null && planKey != null) {
            BuildLogger buildLogger = buildLoggerManager.getLogger(planKey);
            buildLogger.addErrorLogEntry(logMessage);
        }
        if (e != null) {
            log.error(logMessage, e);
        } else {
            log.error(logMessage);
        }
    }

}
