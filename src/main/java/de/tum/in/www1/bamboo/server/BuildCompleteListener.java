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
        log.info("[BAMBOO-SERVER-NOTIFICATION] onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString());
        CurrentBuildResult currentBuildResult = postBuildCompletedEvent.getContext().getBuildResult();
        ResultsContainer resultsContainer = new ResultsContainer(postBuildCompletedEvent.getPlanResultKey(),
                currentBuildResult.getSuccessfulTestResults() != null ? currentBuildResult.getSuccessfulTestResults() : Collections.emptySet(),
                currentBuildResult.getSkippedTestResults() != null ? currentBuildResult.getSkippedTestResults() : Collections.emptySet(),
                currentBuildResult.getFailedTestResults() != null ? currentBuildResult.getFailedTestResults() : Collections.emptySet(),
                currentBuildResult.getTaskResults());
        ServerNotificationRecipient.getCachedTestResults().put(postBuildCompletedEvent.getPlanResultKey().toString(), resultsContainer);

        // Remove old ResultsContainer based on their initialization timestamp
        ServerNotificationRecipient.clearOldTestResultsContainer();
    }

}
