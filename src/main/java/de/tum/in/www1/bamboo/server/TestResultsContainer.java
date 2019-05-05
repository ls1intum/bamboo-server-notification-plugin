package de.tum.in.www1.bamboo.server;

import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.results.tests.TestResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestResultsContainer {

    private final PlanResultKey planResultKey;
    private final long initTimestamp = System.currentTimeMillis();

    private Collection<TestResults> successfulTests = new ArrayList<>();
    private Collection<TestResults> skippedTests = new ArrayList<>();
    private Collection<TestResults> failedTests = new ArrayList<>();

    public TestResultsContainer(PlanResultKey planResultKey) {
        this.planResultKey = planResultKey;
    }

    public TestResultsContainer(PlanResultKey planResultKey, Collection<TestResults> successfulTests, Collection<TestResults> skippedTests, Collection<TestResults> failedTests) {
        this.planResultKey = planResultKey;
        this.successfulTests = successfulTests;
        this.skippedTests = skippedTests;
        this.failedTests = failedTests;
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

    @Override
    public String toString() {
        return "TestResultsContainer{" +
                "planResultKey=" + planResultKey +
                ", initTimestamp=" + initTimestamp +
                ", successfulTests=" + successfulTests +
                ", skippedTests=" + skippedTests +
                ", failedTests=" + failedTests +
                '}';
    }
}
