package de.tum.in.www1.bamboo.server;

import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.atlassian.bamboo.v2.build.events.PostBuildCompletedEvent;
import com.atlassian.event.api.EventListener;

import java.util.Collections;

public class BuildCompleteListener {

    @EventListener
    public void onPostBuildComplete(final PostBuildCompletedEvent postBuildCompletedEvent) {
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
