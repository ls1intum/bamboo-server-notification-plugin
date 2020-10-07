package de.tum.in.www1.bamboo.server;

import java.util.ArrayList;
import java.util.Collection;

import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.task.TaskResult;

public class ResultsContainer {

    private final PlanResultKey planResultKey;

    private final long initTimestamp = System.currentTimeMillis();

    private Collection<TestResults> successfulTests = new ArrayList<>();

    private Collection<TestResults> skippedTests = new ArrayList<>();

    private Collection<TestResults> failedTests = new ArrayList<>();

    private Collection<TaskResult> taskResults = new ArrayList<>();

    public ResultsContainer(PlanResultKey planResultKey) {
        this.planResultKey = planResultKey;
    }

    public ResultsContainer(PlanResultKey planResultKey, Collection<TestResults> successfulTests, Collection<TestResults> skippedTests, Collection<TestResults> failedTests,
            Collection<TaskResult> taskResults) {
        this.planResultKey = planResultKey;
        this.successfulTests = successfulTests;
        this.skippedTests = skippedTests;
        this.failedTests = failedTests;
        this.taskResults = taskResults;
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

    @Override
    public String toString() {
        return "ResultsContainer{" +
                "planResultKey=" + planResultKey +
                ", initTimestamp=" + initTimestamp +
                ", successfulTests=" + successfulTests +
                ", skippedTests=" + skippedTests +
                ", failedTests=" + failedTests +
                ", taskResults=" + taskResults +
                '}';
    }
}
