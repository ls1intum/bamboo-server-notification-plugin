package de.tum.in.www1.bamboo.server;

import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.atlassian.bamboo.v2.build.events.PostBuildCompletedEvent;
import com.atlassian.event.api.EventListener;
import org.apache.log4j.Logger;

import java.util.Collections;

public class BuildCompleteListener {

    private static final Logger log = Logger.getLogger(BuildCompleteListener.class);

    @EventListener
    public void onPostBuildComplete(final PostBuildCompletedEvent postBuildCompletedEvent) {
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString(), null, postBuildCompletedEvent.getPlanKey(), log);
        CurrentBuildResult currentBuildResult = postBuildCompletedEvent.getContext().getBuildResult();
        ResultsContainer resultsContainer = new ResultsContainer(postBuildCompletedEvent.getPlanResultKey(),
                currentBuildResult.getSuccessfulTestResults() != null ? currentBuildResult.getSuccessfulTestResults() : Collections.emptySet(),
                currentBuildResult.getSkippedTestResults() != null ? currentBuildResult.getSkippedTestResults() : Collections.emptySet(),
                currentBuildResult.getFailedTestResults() != null ? currentBuildResult.getFailedTestResults() : Collections.emptySet(),
                currentBuildResult.getTaskResults());
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString() + " - Container created", null, postBuildCompletedEvent.getPlanKey(), log);
        ServerNotificationRecipient.getCachedTestResults().put(postBuildCompletedEvent.getPlanResultKey().toString(), resultsContainer);
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString() + " - Container stored", null, postBuildCompletedEvent.getPlanKey(), log);

        // Remove old ResultsContainer based on their initialization timestamp
        ServerNotificationRecipient.clearOldTestResultsContainer();
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString() + " - Cleared old test results container", null, postBuildCompletedEvent.getPlanKey(), log);
    }

}
