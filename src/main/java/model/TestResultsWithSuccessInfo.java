package model;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.bamboo.results.tests.TestResults;

public class TestResultsWithSuccessInfo extends TestResults {

    private String successMessage;

    public TestResultsWithSuccessInfo(String className, String methodName) {
        super(className, methodName, 0L);
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public boolean hasSuccessMessage() {
        return StringUtils.isNotBlank(successMessage);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestResultsWithSuccessInfo)) {
            return false;
        }
        TestResultsWithSuccessInfo other = (TestResultsWithSuccessInfo) o;
        return super.equals(o) && Objects.equals(successMessage, other.successMessage);
    }
}
