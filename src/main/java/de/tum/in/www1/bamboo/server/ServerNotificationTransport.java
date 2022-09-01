package de.tum.in.www1.bamboo.server;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atlassian.bamboo.artifact.MutableArtifact;
import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.BuildOutputLogEntry;
import com.atlassian.bamboo.build.ErrorLogEntry;
import com.atlassian.bamboo.build.LogEntry;
import com.atlassian.bamboo.build.artifact.ArtifactLink;
import com.atlassian.bamboo.build.artifact.ArtifactLinkDataProvider;
import com.atlassian.bamboo.build.artifact.ArtifactLinkManager;
import com.atlassian.bamboo.build.artifact.FileSystemArtifactLinkDataProvider;
import com.atlassian.bamboo.build.logger.BuildLogFileAccessor;
import com.atlassian.bamboo.build.logger.BuildLogFileAccessorFactory;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.chains.ChainStageResult;
import com.atlassian.bamboo.commit.Commit;
import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.plan.cache.ImmutablePlan;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.tests.TestCaseResultError;
import com.atlassian.bamboo.resultsummary.tests.TestResultsSummary;
import com.atlassian.bamboo.resultsummary.vcs.RepositoryChangeset;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.utils.HttpUtils;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.bamboo.variable.VariableDefinition;
import com.atlassian.bamboo.variable.VariableDefinitionManager;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.ImmutableList;

import de.tum.in.ase.parser.ReportParser;
import de.tum.in.ase.parser.exception.ParserException;

public class ServerNotificationTransport implements NotificationTransport {

    private static final Logger log = Logger.getLogger(ServerNotificationTransport.class);

    private final String webhookUrl;

    private CloseableHttpClient client;

    @Nullable
    private final ImmutablePlan plan;

    @Nullable
    private final ResultsSummary resultsSummary;

    @Nullable
    private final BuildLoggerManager buildLoggerManager;

    @Nullable
    private final BuildLogFileAccessorFactory buildLogFileAccessorFactory;

    @Nullable
    private final CustomVariableContext customVariableContext;

    // Will be injected by Bamboo
    private final VariableDefinitionManager variableDefinitionManager = (VariableDefinitionManager) ContainerManager.getComponent("variableDefinitionManager");

    private final ArtifactLinkManager artifactLinkManager = (ArtifactLinkManager) ContainerManager.getComponent("artifactLinkManager");

    // Maximum length for the feedback text. The feedback will be truncated afterwards
    private static final int FEEDBACK_DETAIL_TEXT_MAX_CHARACTERS = 5000;

    // Maximum number of lines of log per job. The last lines will be taken.
    private static final int JOB_LOG_MAX_LINES = 5000;

    // We are only interested in logs coming from the build, not in logs from Bamboo
    final List<Class<?>> logEntryTypes = ImmutableList.of(BuildOutputLogEntry.class, ErrorLogEntry.class);

    public ServerNotificationTransport(String webhookUrl, @Nullable ImmutablePlan plan, @Nullable ResultsSummary resultsSummary, CustomVariableContext customVariableContext,
            @Nullable BuildLoggerManager buildLoggerManager, @Nullable BuildLogFileAccessorFactory buildLogFileAccessorFactory) {
        this.webhookUrl = customVariableContext.substituteString(webhookUrl);
        this.plan = plan;
        this.resultsSummary = resultsSummary;
        this.buildLoggerManager = buildLoggerManager;
        this.buildLogFileAccessorFactory = buildLogFileAccessorFactory;
        this.customVariableContext = customVariableContext;

        URI uri;
        try {
            uri = new URI(webhookUrl);
        }
        catch (URISyntaxException e) {
            LoggingUtils.logError("Unable to set up proxy settings, invalid URI encountered: " + e, buildLoggerManager, plan.getPlanKey(), log, e);
            return;
        }

        HttpUtils.EndpointSpec proxyForScheme = HttpUtils.getProxyForScheme(uri.getScheme());
        if (proxyForScheme != null) {
            HttpHost proxy = new HttpHost(proxyForScheme.host, proxyForScheme.port);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            this.client = HttpClients.custom().setRoutePlanner(routePlanner).build();
        }
        else {
            this.client = HttpClients.createDefault();
        }
    }

    public void sendNotification(@NotNull Notification notification) {
        LoggingUtils.logInfo("Start sending notification", buildLoggerManager, plan.getPlanKey(), log);
        try {
            HttpPost method = setupPostMethod();
            JSONObject jsonObject = createJSONObject(notification);
            try {
                String secret = (String) jsonObject.get("secret");
                method.addHeader("Authorization", secret);
            }
            catch (JSONException e) {
                LoggingUtils.logError("Error while getting secret from JSONObject: " + e.getMessage(), buildLoggerManager, plan.getPlanKey(), log, e);
            }

            method.setEntity(new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));

            try {
                LoggingUtils.logInfo("Executing call to " + method.getURI().toString(), buildLoggerManager, plan.getPlanKey(), log);
                LoggingUtils.logDebug(method.getURI().toString(), plan.getPlanKey(), log);
                LoggingUtils.logDebug(method.getEntity().toString(), plan.getPlanKey(), log);
                CloseableHttpResponse closeableHttpResponse = client.execute(method);

                LoggingUtils.logInfo("Call executed", buildLoggerManager, plan.getPlanKey(), log);
                if (closeableHttpResponse != null) {
                    LoggingUtils.logInfo("Response is not null: " + closeableHttpResponse, buildLoggerManager, plan.getPlanKey(), log);

                    StatusLine statusLine = closeableHttpResponse.getStatusLine();
                    if (statusLine != null) {
                        LoggingUtils.logInfo("StatusLine is not null: " + statusLine, buildLoggerManager, plan.getPlanKey(), log);
                        LoggingUtils.logInfo("StatusCode is: " + statusLine.getStatusCode(), buildLoggerManager, plan.getPlanKey(), log);
                    }
                    else {
                        LoggingUtils.logInfo("Statusline is null" + closeableHttpResponse, buildLoggerManager, plan.getPlanKey(), log);
                    }

                    HttpEntity httpEntity = closeableHttpResponse.getEntity();
                    if (httpEntity != null) {
                        String response = EntityUtils.toString(httpEntity);
                        LoggingUtils.logInfo("Response from entity is: " + response, buildLoggerManager, plan.getPlanKey(), log);
                        EntityUtils.consume(httpEntity);
                    }
                    else {
                        LoggingUtils.logError("Httpentity is null", buildLoggerManager, plan.getPlanKey(), log, null);
                    }
                }
                else {
                    LoggingUtils.logError("Response is null", buildLoggerManager, plan.getPlanKey(), log, null);
                }
            }
            catch (Exception e) {
                LoggingUtils.logError("Error while sending payload: " + e.getMessage(), buildLoggerManager, plan.getPlanKey(), log, e);
            }
        }
        catch (Exception e) {
            LoggingUtils.logError("Error during sendNotification: " + e.getMessage(), buildLoggerManager, plan.getPlanKey(), log, e);
        }
        LoggingUtils.logInfo("finish send notification for plan", buildLoggerManager, plan.getPlanKey(), log);
    }

    private HttpPost setupPostMethod() throws URISyntaxException {
        HttpPost post = new HttpPost((new URI(webhookUrl)));
        post.setHeader("Content-Type", "application/json");
        return post;
    }

    private JSONObject createJSONObject(Notification notification) {
        LoggingUtils.logInfo("Creating JSON object", buildLoggerManager, plan.getPlanKey(), log);

        JSONObject jsonObject = new JSONObject();
        try {
            List<VariableDefinition> variableDefinitions = variableDefinitionManager.getGlobalVariables();
            if (!variableDefinitions.isEmpty()) {
                // Variable name contains "password" to ensure that the secret is hidden in the UI
                Optional<VariableDefinition> optionalVariableDefinition = variableDefinitions.stream().filter(vd -> vd.getKey().equals("SERVER_PLUGIN_SECRET_PASSWORD"))
                        .findFirst();
                if (optionalVariableDefinition.isPresent()) {
                    jsonObject.put("secret", optionalVariableDefinition.get().getValue()); // Used to verify that the request is coming from a legitimate server
                }
                else {
                    jsonObject.put("secret", "SERVER_PLUGIN_SECRET_PASSWORD-NOT-DEFINED");
                    LoggingUtils.logError("Variable SERVER_PLUGIN_SECRET_PASSWORD is not defined", buildLoggerManager, plan.getPlanKey(), log, null);
                }

            }
            else {
                jsonObject.put("secret", "NO-GLOBAL-VARIABLES-ARE-DEFINED");
                LoggingUtils.logError("No global variables are defined", buildLoggerManager, plan.getPlanKey(), log, null);
            }

            jsonObject.put("notificationType", notification.getDescription());

            if (plan != null) {
                JSONObject planDetails = new JSONObject();
                planDetails.put("key", plan.getPlanKey());
                // LoggingUtils.logError(, buildLoggerManager, plan.getPlanKey(), log, null);
                // defaultRepositoryDef = BuildContextHelper.getDefaultPlanRepositoryDefinition();

                jsonObject.put("plan", planDetails);
            }

            if (resultsSummary != null) {
                JSONObject buildDetails = new JSONObject();
                buildDetails.put("number", resultsSummary.getBuildNumber());
                buildDetails.put("reason", resultsSummary.getShortReasonSummary());
                buildDetails.put("successful", resultsSummary.isSuccessful());
                buildDetails.put("buildCompletedDate", ZonedDateTime.ofInstant(resultsSummary.getBuildCompletedDate().toInstant(), ZoneId.systemDefault()));

                // ResultsSummary only contains shared artifacts. Job level artifacts are not available here
                buildDetails.put("artifact", !resultsSummary.getArtifactLinks().isEmpty());

                TestResultsSummary testResultsSummary = resultsSummary.getTestResultsSummary();
                JSONObject testResultOverview = new JSONObject();
                testResultOverview.put("description", testResultsSummary.getTestSummaryDescription());
                testResultOverview.put("totalCount", testResultsSummary.getTotalTestCaseCount());
                testResultOverview.put("failedCount", testResultsSummary.getFailedTestCaseCount());
                testResultOverview.put("existingFailedCount", testResultsSummary.getExistingFailedTestCount());
                testResultOverview.put("fixedCount", testResultsSummary.getFixedTestCaseCount());
                testResultOverview.put("newFailedCount", testResultsSummary.getNewFailedTestCaseCount());
                testResultOverview.put("ignoredCount", testResultsSummary.getIgnoredTestCaseCount());
                testResultOverview.put("quarantineCount", testResultsSummary.getQuarantinedTestCaseCount());
                testResultOverview.put("skippedCount", testResultsSummary.getSkippedTestCaseCount());
                testResultOverview.put("successfulCount", testResultsSummary.getSuccessfulTestCaseCount());
                testResultOverview.put("duration", testResultsSummary.getTotalTestDuration());

                buildDetails.put("testSummary", testResultOverview);

                // The branch name can only be accessed from the ResultsContainer -> We can only add it later to the changesetDetails JSONObject.
                // As we can not access it easily from the vcsDetails JSONArray, we keep a reference to the changesetDetails JSONObject, which will later be used to
                // get the correct changesetDetails JSONObject based on the repository name
                Map<String, JSONObject> repositoryToVcsDetails = new HashMap<>();

                JSONArray vcsDetails = new JSONArray();
                for (RepositoryChangeset changeset : resultsSummary.getRepositoryChangesets()) {
                    JSONObject changesetDetails = new JSONObject();
                    changesetDetails.put("id", changeset.getChangesetId());
                    changesetDetails.put("repositoryName", changeset.getRepositoryData().getName());

                    JSONArray commits = new JSONArray();
                    for (Commit commit : changeset.getCommits()) {
                        JSONObject commitDetails = new JSONObject();
                        commitDetails.put("id", commit.getChangeSetId());
                        commitDetails.put("comment", commit.getComment());

                        commits.put(commitDetails);
                    }

                    changesetDetails.put("commits", commits);

                    vcsDetails.put(changesetDetails);
                    // Put a reference to later access it when adding the branch name
                    repositoryToVcsDetails.put(changeset.getRepositoryData().getName(), changesetDetails);
                }
                buildDetails.put("vcs", vcsDetails);

                if (resultsSummary instanceof ChainResultsSummary) {
                    ChainResultsSummary chainResultsSummary = (ChainResultsSummary) resultsSummary;
                    JSONArray jobs = new JSONArray();
                    for (ChainStageResult chainStageResult : chainResultsSummary.getStageResults()) {
                        for (BuildResultsSummary buildResultsSummary : chainStageResult.getBuildResults()) {
                            JSONObject jobDetails = new JSONObject();

                            jobDetails.put("id", buildResultsSummary.getId());

                            LoggingUtils.logInfo("Loading cached test results for job " + buildResultsSummary.getId(), buildLoggerManager, plan.getPlanKey(), log);
                            ResultsContainer resultsContainer = ServerNotificationRecipient.getCachedTestResults().get(buildResultsSummary.getPlanResultKey().toString());
                            if (resultsContainer != null) {
                                // We just extracted the ResultsContainer, so we can clear it from the map now.
                                ServerNotificationRecipient.getCachedTestResults().remove(buildResultsSummary.getPlanResultKey().toString());
                                long secondsInMap = (System.currentTimeMillis() - resultsContainer.getInitTimestamp()) / 1000;

                                LoggingUtils.logInfo("Tests results found - was in map for " + secondsInMap + " seconds", buildLoggerManager, plan.getPlanKey(), log);
                                JSONArray successfulTestDetails = createTestsResultsJSONArray(resultsContainer.getSuccessfulTests(), false);
                                jobDetails.put("successfulTests", successfulTestDetails);

                                JSONArray skippedTestDetails = createTestsResultsJSONArray(resultsContainer.getSkippedTests(), false);
                                jobDetails.put("skippedTests", skippedTestDetails);

                                JSONArray failedTestDetails = createTestsResultsJSONArray(resultsContainer.getFailedTests(), true);
                                jobDetails.put("failedTests", failedTestDetails);

                                JSONArray taskResults = createTasksJSONArray(resultsContainer.getTaskResults());
                                jobDetails.put("tasks", taskResults);

                                // Put the branch name in the referenced changesetDetails JSONObject
                                for (Map.Entry<String, String> repositoryToBranchEntry : resultsContainer.getRepositoryToBranchMap().entrySet()) {
                                    JSONObject changesetDetails = repositoryToVcsDetails.get(repositoryToBranchEntry.getKey());
                                    if (changesetDetails != null) {
                                        changesetDetails.put("branchName", repositoryToBranchEntry.getValue());
                                    }
                                }
                            }
                            else {
                                LoggingUtils.logError("Could not load cached test results!", buildLoggerManager, plan.getPlanKey(), log, null);
                            }
                            LoggingUtils.logInfo("Loading artifacts for job " + buildResultsSummary.getId(), buildLoggerManager, plan.getPlanKey(), log);
                            try {
                                // Note: we cannot directly access buildResultsSummary.getProducedArtifactLinks() because it is a lazy Hibernate collection
                                Collection<ArtifactLink> artifactLinks = artifactLinkManager.getArtifactLinks(buildResultsSummary, null);
                                try {
                                    JSONArray staticCodeAnalysisReports = createStaticCodeAnalysisReportArray(artifactLinks, buildResultsSummary.getId());
                                    jobDetails.put("staticCodeAnalysisReports", staticCodeAnalysisReports);
                                }
                                catch (Exception e) {
                                    LoggingUtils.logError("Error during parsing static code analysis reports :" + e.getMessage(), buildLoggerManager, plan.getPlanKey(), log, e);
                                }
                                try {
                                    JSONArray testwiseCoverageReport = createTestwiseCoverageJSONObject(artifactLinks, buildResultsSummary.getId());
                                    if (testwiseCoverageReport != null) {
                                        jobDetails.put("testwiseCoverageReport", testwiseCoverageReport);
                                    }
                                }
                                catch (Exception e) {
                                    LoggingUtils.logError("Error during parsing testwise coverage report :" + e.getMessage(), buildLoggerManager, plan.getPlanKey(), log, e);
                                }
                            }
                            catch (Exception ex) {
                                LoggingUtils.logError("Error during loading artifacts :" + ex.getMessage(), buildLoggerManager, plan.getPlanKey(), log, ex);
                            }
                            List<LogEntry> logEntries = Collections.emptyList();

                            // Only add log if no tests are found (indicates a build error)
                            if (testResultsSummary.getTotalTestCaseCount() == 0) {
                                // Loading logs for job
                                try {
                                    final BuildLogFileAccessor fileAccessor = this.buildLogFileAccessorFactory.createBuildLogFileAccessor(buildResultsSummary.getPlanResultKey());
                                    logEntries = fileAccessor.getLastNLogsOfType(JOB_LOG_MAX_LINES, logEntryTypes);
                                    LoggingUtils.logInfo("Found: " + logEntries.size() + " LogEntries", buildLoggerManager, plan.getPlanKey(), log);
                                }
                                catch (IOException ex) {
                                    LoggingUtils.logError("Error while loading build log: " + ex.getMessage(), buildLoggerManager, plan.getPlanKey(), log, ex);
                                }
                            }
                            JSONArray logEntriesArray = new JSONArray();
                            for (LogEntry logEntry : logEntries) {
                                // A lambda here would require us to catch the JSONException inside the lambda, so we use a loop.
                                logEntriesArray.put(createLogEntryJSONObject(logEntry));
                            }
                            jobDetails.put("logs", logEntriesArray); // We add an empty array here in case tests are found to prevent errors while parsing in the client

                            jobs.put(jobDetails);
                        }
                    }
                    buildDetails.put("jobs", jobs);

                    // TODO: This ensures outdated versions of Artemis can still process the new request. Will be removed without further notice in the future
                    buildDetails.put("failedJobs", jobs);
                }
                jsonObject.put("build", buildDetails);
            }

        }
        catch (Exception e) {
            LoggingUtils.logError("Error during createJSONObject :" + e.getMessage(), buildLoggerManager, plan.getPlanKey(), log, e);
        }

        LoggingUtils.logInfo("JSON object created", buildLoggerManager, plan.getPlanKey(), log);
        return jsonObject;
    }

    private Optional<JSONObject> createStaticCodeAnalysisReportJSONObject(File rootFile, String label) {
        /*
         * The rootFile is a directory if the copy pattern matches multiple files, otherwise it is a regular file.
         * Ignore artifact definitions matching multiple files.
         * Also, ignore empty (0-byte) files since they cause parsing errors since they are invalid XML.
         */
        // TODO: Support artifact definitions matching multiple files
        if (rootFile == null || rootFile.length() == 0 || rootFile.isDirectory()) {
            return Optional.empty();
        }

        try {
            LoggingUtils.logInfo("Creating artifact JSON object for artifact definition: " + label, buildLoggerManager, plan != null ? plan.getPlanKey() : null, log);
            // The report parser is able to identify the tool to which the report belongs
            ReportParser reportParser = new ReportParser();

            String reportJSON = reportParser.transformToJSONReport(rootFile);
            return Optional.of(new JSONObject(reportJSON));
        }
        catch (JSONException e) {
            LoggingUtils.logError("Error constructing artifact JSON for artifact definition " + label + ": " + e.getMessage(), buildLoggerManager,
                    plan != null ? plan.getPlanKey() : null, log, e);

        }
        catch (ParserException e) {
            LoggingUtils.logError("Error parsing static code analysis report " + label + ": " + e.getMessage(), buildLoggerManager, plan != null ? plan.getPlanKey() : null, log,
                    e);
        }
        return Optional.empty();
    }

    /**
     * Find and returns a JSONArray from the testwise coverage reports if exists
     * @param artifactLinks all artifact links from the build to search in
     * @param jobId the job id
     * @return a JSONArray containing all testwise coverage reports if this artifact exists, otherwise null
     */
    private JSONArray createTestwiseCoverageJSONObject(Collection<ArtifactLink> artifactLinks, long jobId) {
        Optional<ArtifactLink> optionalArtifactLink = artifactLinks.stream().filter(artifact -> "testwiseCoverageReport".equals(artifact.getArtifact().getLabel())).findFirst();

        if (!optionalArtifactLink.isPresent()) {
            return null;
        }

        MutableArtifact artifact = optionalArtifactLink.get().getArtifact();
        ArtifactLinkDataProvider dataProvider = artifactLinkManager.getArtifactLinkDataProvider(artifact);

        if (dataProvider == null) {
            LoggingUtils.logInfo("ArtifactLinkDataProvider is null for " + artifact.getLabel() + " in job " + jobId, buildLoggerManager, plan != null ? plan.getPlanKey() : null,
                    log);
            LoggingUtils.logInfo("Could not retrieve data for artifact " + artifact.getLabel() + " in job " + jobId, buildLoggerManager, plan != null ? plan.getPlanKey() : null,
                    log);
            return null;
        }

        try {
            if (dataProvider instanceof FileSystemArtifactLinkDataProvider) {
                FileSystemArtifactLinkDataProvider fileDataProvider = (FileSystemArtifactLinkDataProvider) dataProvider;
                File artifactFile = fileDataProvider.getFile();
                InputStream inputStream = new FileInputStream(artifactFile.getAbsolutePath());
                String fileContent = IOUtils.toString(inputStream, "UTF-8");
                return new JSONObject(fileContent).getJSONArray("tests");
            }
        }
        catch (IOException exception) {
            LoggingUtils.logInfo("Could not read from artifact file for " + artifact.getLabel() + " in job " + jobId, buildLoggerManager, plan != null ? plan.getPlanKey() : null,
                    log);
        }
        catch (JSONException exception) {
            LoggingUtils.logInfo("Could not read parse Testwise Coverage Report for in job " + jobId, buildLoggerManager, plan != null ? plan.getPlanKey() : null,
                    log);
            LoggingUtils.logInfo(exception.getMessage(), buildLoggerManager, plan != null ? plan.getPlanKey() : null, log);
        }
        return null;
    }

    private JSONArray createStaticCodeAnalysisReportArray(Collection<ArtifactLink> artifactLinks, long jobId) {
        JSONArray artifactsArray = new JSONArray();
        Collection<JSONObject> artifactJSONObjects = new ArrayList<>();
        // ArtifactLink refers to a single artifact definition configured on job level
        for (ArtifactLink artifactLink : artifactLinks) {
            MutableArtifact artifact = artifactLink.getArtifact();

            /*
             * The interface ArtifactLinkDataProvider generalizes access to the resulting artifact files.
             * Artifact handler configurations, which define how the results are stored, determine the concrete
             * implementation of the interface.
             */
            ArtifactLinkDataProvider dataProvider = artifactLinkManager.getArtifactLinkDataProvider(artifact);

            if (dataProvider == null) {
                LoggingUtils.logInfo("ArtifactLinkDataProvider is null for " + artifact.getLabel() + " in job " + jobId, buildLoggerManager,
                        plan != null ? plan.getPlanKey() : null, log);
                LoggingUtils.logInfo("Could not retrieve data for artifact " + artifact.getLabel() + " in job " + jobId, buildLoggerManager,
                        plan != null ? plan.getPlanKey() : null, log);
                continue;
            }

            /*
             * FileSystemArtifactLinkDataProvider provides access to artifacts stored on the local server.
             * Has to be extended to support other artifact handling configurations.
             */
            if (dataProvider instanceof FileSystemArtifactLinkDataProvider) {
                FileSystemArtifactLinkDataProvider fileDataProvider = (FileSystemArtifactLinkDataProvider) dataProvider;
                File artifactFile = fileDataProvider.getFile();

                if (StaticCodeAnalysisUtils.isStaticCodeAnalysisArtifact(artifact.getLabel(), artifactFile.getName())) {
                    Optional<JSONObject> optionalReport = createStaticCodeAnalysisReportJSONObject(artifactFile, artifact.getLabel());
                    optionalReport.ifPresent(artifactJSONObjects::add);
                }
            }
            else {
                LoggingUtils.logError("Unsupported artifact handler configuration encountered for artifact "
                        + artifact.getLabel() + " in job " + jobId, buildLoggerManager, plan != null ? plan.getPlanKey() : null, log, null);
            }
        }
        artifactJSONObjects.forEach(artifactsArray::put);
        return artifactsArray;
    }

    private JSONObject createTestsResultsJSONObject(TestResults testResults, boolean addErrors) throws JSONException {
        LoggingUtils.logInfo("Creating test results JSON object for " + testResults.getActualMethodName(), buildLoggerManager, plan != null ? plan.getPlanKey() : null, log);
        JSONObject testResultsJSON = new JSONObject();
        testResultsJSON.put("name", testResults.getActualMethodName());
        testResultsJSON.put("methodName", testResults.getMethodName());
        testResultsJSON.put("className", testResults.getClassName());

        if (addErrors) {
            JSONArray testCaseErrorDetails = new JSONArray();
            for (TestCaseResultError testCaseResultError : testResults.getErrors()) {
                String errorMessageString = testCaseResultError.getContent();
                if (errorMessageString != null && errorMessageString.length() > FEEDBACK_DETAIL_TEXT_MAX_CHARACTERS) {
                    errorMessageString = errorMessageString.substring(0, FEEDBACK_DETAIL_TEXT_MAX_CHARACTERS);
                }
                testCaseErrorDetails.put(errorMessageString);
            }
            testResultsJSON.put("errors", testCaseErrorDetails);
        }

        return testResultsJSON;
    }

    private JSONArray createTestsResultsJSONArray(Collection<TestResults> testResultsCollection, boolean addErrors) throws JSONException {
        LoggingUtils.logInfo("Creating test results JSON array", buildLoggerManager, plan != null ? plan.getPlanKey() : null, log);
        JSONArray testResultsArray = new JSONArray();
        for (TestResults testResults : testResultsCollection) {
            testResultsArray.put(createTestsResultsJSONObject(testResults, addErrors));
        }

        return testResultsArray;
    }

    /**
     * Creates an JSONArray containing task name, plugin key, whether the task is final or enabled and the
     * state (SUCCESS, FAILED, ERROR) for each defined task.
     *
     * @param taskResults Collection of all defined tasks with details
     * @return JSONArray containing the name and state
     * @throws JSONException if JSONObject can't be created
     */
    private JSONArray createTasksJSONArray(Collection<TaskResult> taskResults) throws JSONException {
        LoggingUtils.logInfo("Creating tasks JSON array", buildLoggerManager, plan != null ? plan.getPlanKey() : null, log);
        JSONArray tasksArray = new JSONArray();
        for (TaskResult taskResult : taskResults) {
            JSONObject taskJSON = new JSONObject();
            taskJSON.put("description", taskResult.getTaskIdentifier().getUserDescription());
            taskJSON.put("pluginKey", taskResult.getTaskIdentifier().getPluginKey());
            taskJSON.put("isEnabled", taskResult.getTaskIdentifier().isEnabled());
            taskJSON.put("isFinal", taskResult.getTaskIdentifier().isFinalising());
            taskJSON.put("state", taskResult.getTaskState().name());
            tasksArray.put(taskJSON);
        }
        return tasksArray;
    }

    private JSONObject createLogEntryJSONObject(LogEntry logEntry) throws JSONException {
        JSONObject logEntryObject = new JSONObject();
        logEntryObject.put("log", logEntry.getLog());
        logEntryObject.put("date", ZonedDateTime.ofInstant(logEntry.getDate().toInstant(), ZoneId.systemDefault()));

        return logEntryObject;
    }
}
