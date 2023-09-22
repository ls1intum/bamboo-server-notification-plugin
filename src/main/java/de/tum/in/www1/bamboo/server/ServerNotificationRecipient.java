package de.tum.in.www1.bamboo.server;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.logger.BuildLogFileAccessorFactory;
import com.atlassian.bamboo.notification.NotificationRecipient;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.cache.ImmutablePlan;
import com.atlassian.bamboo.plugin.descriptor.NotificationRecipientModuleDescriptor;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ServerNotificationRecipient extends AbstractNotificationRecipient
        implements NotificationRecipient.RequiresPlan, NotificationRecipient.RequiresResultSummary {

    private static final String WEBHOOK_URL = "webhookUrl";

    private String webhookUrl = null;

    private I18nResolver i18n;

    private TemplateRenderer templateRenderer;

    private ImmutablePlan plan;

    private ResultsSummary resultsSummary;

    private CustomVariableContext customVariableContext;

    private static BuildLoggerManager buildLoggerManager;

    private static BuildLogFileAccessorFactory buildLogFileAccessorFactory;

    // Time in seconds before removing ResultsContainer
    private static final int TESTRESULTSCONTAINER_REMOVE_TIME = 600;

    /*
     * The TestResults of successful tests can not be loaded from the ChainResultSummary (only failing ones can).
     * The TestResults need a BuildContext and can therefor only be accessed when using an Event that extends BuildContextEvent.
     * The normal BuildCompletedEvent does not extend BuildContextEvent, but the PostBuildCompletedEvent does.
     * We listen for the PostBuildCompletedEvent and save the test cases in this Map with the key of the job as String.
     * The TestResults are stored inside the ResultsContainer class and it can be retrieved using this Map in the ServerNotificationTransport class.
     * A method clearOldTestResultsContainer() has been added, that removes old ResultsContainer from the Map, because we add every build to this Map, even those without
     * Notifications enabled.
     * The time (in seconds) after the ResultsContainer can be specified in the variable TESTRESULTSCONTAINER_REMOVE_TIME.
     */
    private static final Map<String, ResultsContainer> cachedTestResults = new ConcurrentHashMap<>();

    @Override
    public void populate(@NotNull Map<String, String[]> params) {
        if (params.containsKey(WEBHOOK_URL)) {
            this.webhookUrl = this.getParam(WEBHOOK_URL, params);
        }
    }

    private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    @NotNull
    @Override
    public ErrorCollection validate(@NotNull Map<String, String[]> params) {
        ErrorCollection errorCollection = new SimpleErrorCollection();

        webhookUrl = this.getParam(WEBHOOK_URL, params);
        if (webhookUrl != null && (webhookUrl.isEmpty() || !isValidURL(webhookUrl))) {
            errorCollection.addError(WEBHOOK_URL, i18n.getText("server.webhookUrl.invalid"));
        }

        return errorCollection;
    }

    @Override
    public void init(@Nullable String configurationData) {

        if (StringUtils.isNotBlank(configurationData)) {
            String delimiter = "\\|";

            String[] configValues = configurationData.split(delimiter);

            if (configValues.length > 0) {
                webhookUrl = configValues[0];
            }
        }
    }

    @NotNull
    @Override
    public String getRecipientConfig() {
        StringBuilder recipientConfig = new StringBuilder();
        if (StringUtils.isNotBlank(webhookUrl)) {
            recipientConfig.append(webhookUrl);
        }
        return recipientConfig.toString();
    }

    @NotNull
    @Override
    public String getEditHtml() {
        String editTemplateLocation = ((NotificationRecipientModuleDescriptor) getModuleDescriptor()).getEditTemplate();
        return templateRenderer.render(editTemplateLocation, populateContext());
    }

    private Map<String, Object> populateContext() {
        Map<String, Object> context = Maps.newHashMap();

        if (webhookUrl != null) {
            context.put(WEBHOOK_URL, webhookUrl);
        }

        System.out.println("populateContext = " + context);

        return context;
    }

    @NotNull
    @Override
    public String getViewHtml() {
        String editTemplateLocation = ((NotificationRecipientModuleDescriptor) getModuleDescriptor()).getViewTemplate();
        return templateRenderer.render(editTemplateLocation, populateContext());
    }

    @NotNull
    public List<NotificationTransport> getTransports() {
        List<NotificationTransport> list = Lists.newArrayList();
        list.add(new ServerNotificationTransport(webhookUrl, plan, resultsSummary, customVariableContext, buildLoggerManager, buildLogFileAccessorFactory));
        return list;
    }

    public void setPlan(@Nullable final Plan plan) {
        this.plan = plan;
    }

    public void setPlan(@Nullable final ImmutablePlan plan) {
        this.plan = plan;
    }

    public void setResultsSummary(@Nullable final ResultsSummary resultsSummary) {
        this.resultsSummary = resultsSummary;
    }

    public void setBuildLoggerManager(@Nullable final BuildLoggerManager buildLoggerManager) {
        ServerNotificationRecipient.buildLoggerManager = buildLoggerManager;
    }

    public static BuildLoggerManager getBuildLoggerManager() {
        return buildLoggerManager;
    }

    public void setBuildLogFileAccessorFactory(@Nullable final BuildLogFileAccessorFactory buildLogFileAccessorFactory) {
        ServerNotificationRecipient.buildLogFileAccessorFactory = buildLogFileAccessorFactory;
    }

    public static BuildLogFileAccessorFactory getBuildLogFileAccessorFactory() {
        return buildLogFileAccessorFactory;
    }

    public static Map<String, ResultsContainer> getCachedTestResults() {
        return cachedTestResults;
    }

    public static void clearOldTestResultsContainer() {
        getCachedTestResults().entrySet().removeIf(entry -> entry.getValue().getInitTimestamp() < (System.currentTimeMillis() - (1000 * TESTRESULTSCONTAINER_REMOVE_TIME)));
    }

    // -----------------------------------Dependencies
    public void setTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setCustomVariableContext(CustomVariableContext customVariableContext) {
        this.customVariableContext = customVariableContext;
    }

    public void setI18nResolver(I18nResolver i18n) {
        this.i18n = i18n;
    }
}
