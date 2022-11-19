package de.tum.in.www1.bamboo.server;

import java.util.Collection;
import java.util.Map;

import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.task.TaskResult;

public class ResultsContainer {

    private final PlanResultKey planResultKey;

    private final long initTimestamp = System.currentTimeMillis();

    private final Collection<TestResults> successfulTests;

    private final Collection<TestResults> skippedTests;

    private final Collection<TestResults> failedTests;

    private final Collection<TaskResult> taskResults;

    private final Map<String, String> repositoryToBranchMap;

    public ResultsContainer(PlanResultKey planResultKey, Collection<TestResults> successfulTests, Collection<TestResults> skippedTests, Collection<TestResults> failedTests,
            Collection<TaskResult> taskResults, Map<String, String> repositoryToBranchMap) {
        this.planResultKey = planResultKey;
        this.successfulTests = successfulTests;
        this.skippedTests = skippedTests;
        this.failedTests = failedTests;
        this.taskResults = taskResults;
        this.repositoryToBranchMap = repositoryToBranchMap;
    }

    public PlanResultKey getPlanResultKey() {
        return planResultKey;
    }

    public long getInitTimestamp() {
        return initTimestamp;
    }

    public Collection<TestResults> getSuccessfulTests() {
        return successfulTests;
    }

    public Collection<TestResults> getSkippedTests() {
        return skippedTests;
    }

    public Collection<TestResults> getFailedTests() {
        return failedTests;
    }

    public Collection<TaskResult> getTaskResults() {
        return taskResults;
    }

    public Map<String, String> getRepositoryToBranchMap() {
        return repositoryToBranchMap;
    }

    @Override
    public String toString() {
        return "ResultsContainer{" +
                "planResultKey=" + planResultKey +
                ", initTimestamp=" + initTimestamp +
                ", successfulTests=" + successfulTests +
                ", skippedTests=" + skippedTests +
                ", failedTests=" + failedTests +
                ", taskResults=" + taskResults +
                ", repositoriesToBranchMap=" + repositoryToBranchMap.toString() +
                '}';
    }
}
