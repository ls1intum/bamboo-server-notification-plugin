package de.tum.in.www1.bamboo.server;

import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.atlassian.bamboo.v2.build.events.PostBuildCompletedEvent;
import com.atlassian.event.api.EventListener;

public class BuildCompleteListener {

    @EventListener
    public void onPostBuildComplete(final PostBuildCompletedEvent postBuildCompletedEvent) {
        CurrentBuildResult currentBuildResult = postBuildCompletedEvent.getContext().getBuildResult();

        TestResultsContainer testResultsContainer = new TestResultsContainer(postBuildCompletedEvent.getPlanResultKey(), currentBuildResult.getSuccessfulTestResults(), currentBuildResult.getSkippedTestResults(), currentBuildResult.getFailedTestResults());
        ServerNotificationRecipient.getCachedTestResults().put(postBuildCompletedEvent.getPlanResultKey().toString(), testResultsContainer);

        // Remove old TestResultsContainer based on their initialization timestamp
        ServerNotificationRecipient.clearOldTestResultsContainer();
    }

}
