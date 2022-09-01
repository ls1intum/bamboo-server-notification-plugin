package de.tum.in.www1.bamboo.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.bamboo.vcs.configuration.PlanRepositoryDefinition;
import com.atlassian.bamboo.vcs.configuration.VcsBranchDefinition;
import org.apache.log4j.Logger;

import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.atlassian.bamboo.v2.build.events.PostBuildCompletedEvent;
import com.atlassian.event.api.EventListener;

public class BuildCompleteListener {

    private static final Logger log = Logger.getLogger(BuildCompleteListener.class);

    @EventListener
    public void onPostBuildComplete(final PostBuildCompletedEvent postBuildCompletedEvent) {
        long startTime = System.currentTimeMillis();
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString(), null, postBuildCompletedEvent.getPlanKey(), log);
        CurrentBuildResult currentBuildResult = postBuildCompletedEvent.getContext().getBuildResult();

        // We can only access the branch names of the repository from the BuildContext -> Extract it here and add it to the ResultContainer, so that it can be extracted from the ServerNotificationTransport
        Map<String, String> repositoryToBranchMap = new HashMap<>();
        for (PlanRepositoryDefinition repository : postBuildCompletedEvent.getContext().getVcsRepositories()) {
            VcsBranchDefinition vcsBranchDefinition = repository.getBranch();
            if (vcsBranchDefinition != null) {
                repositoryToBranchMap.put(repository.getName(), vcsBranchDefinition.getVcsBranch().getName());
            }
        }

        ResultsContainer resultsContainer = new ResultsContainer(postBuildCompletedEvent.getPlanResultKey(),
                currentBuildResult.getSuccessfulTestResults() != null ? currentBuildResult.getSuccessfulTestResults() : Collections.emptySet(),
                currentBuildResult.getSkippedTestResults() != null ? currentBuildResult.getSkippedTestResults() : Collections.emptySet(),
                currentBuildResult.getFailedTestResults() != null ? currentBuildResult.getFailedTestResults() : Collections.emptySet(),
                currentBuildResult.getTaskResults(), repositoryToBranchMap);

        long containerCreationTime = System.currentTimeMillis() - startTime;
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString() + " - Container created took " + containerCreationTime + "ms", null,
                postBuildCompletedEvent.getPlanKey(), log);

        startTime = System.currentTimeMillis();
        ServerNotificationRecipient.getCachedTestResults().put(postBuildCompletedEvent.getPlanResultKey().toString(), resultsContainer);
        long containerStoreTime = System.currentTimeMillis() - startTime;
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString() + " - Container stored took " + containerStoreTime + "ms", null,
                postBuildCompletedEvent.getPlanKey(), log);

        // Remove old ResultsContainer based on their initialization timestamp
        startTime = System.currentTimeMillis();
        ServerNotificationRecipient.clearOldTestResultsContainer();
        long containerClearTime = System.currentTimeMillis() - startTime;
        LoggingUtils.logInfo(
                "onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString() + " - Cleared old test results container took " + containerClearTime + "ms", null,
                postBuildCompletedEvent.getPlanKey(), log);
        LoggingUtils.logInfo("onPostBuildComplete: " + postBuildCompletedEvent.getPlanResultKey().toString() + " - Number of entries in cache: "
                + ServerNotificationRecipient.getCachedTestResults().size(), null, postBuildCompletedEvent.getPlanKey(), log);
    }

}
