package de.tum.in.www1.bamboo.artemis;

import com.atlassian.bamboo.author.Author;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.builder.LifeCycleState;
import com.atlassian.bamboo.deployments.results.DeploymentResult;
import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.plan.cache.ImmutablePlan;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.utils.HttpUtils;
import com.atlassian.bamboo.variable.CustomVariableContext;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class ArtemisNotificationTransport implements NotificationTransport
{
    private static final Logger log = Logger.getLogger(ArtemisNotificationTransport.class);

    private final String webhookUrl;

    private CloseableHttpClient client;

    @Nullable
    private final ImmutablePlan plan;
    @Nullable
    private final ResultsSummary resultsSummary;
    @Nullable
    private final DeploymentResult deploymentResult;

    public ArtemisNotificationTransport(String webhookUrl,
                                        @Nullable ImmutablePlan plan,
                                        @Nullable ResultsSummary resultsSummary,
                                        @Nullable DeploymentResult deploymentResult,
                                        CustomVariableContext customVariableContext)
    {
        this.webhookUrl = customVariableContext.substituteString(webhookUrl);
        this.plan = plan;
        this.resultsSummary = resultsSummary;
        this.deploymentResult = deploymentResult;

        URI uri;
        try
        {
            uri = new URI(webhookUrl);
        }
        catch (URISyntaxException e)
        {
            log.error("Unable to set up proxy settings, invalid URI encountered: " + e);
            return;
        }

        HttpUtils.EndpointSpec proxyForScheme = HttpUtils.getProxyForScheme(uri.getScheme());
        if (proxyForScheme!=null)
        {
            HttpHost proxy = new HttpHost(proxyForScheme.host, proxyForScheme.port);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            this.client = HttpClients.custom().setRoutePlanner(routePlanner).build();
        }
        else
        {
            this.client = HttpClients.createDefault();
        }
    }

    public void sendNotification(@NotNull Notification notification)
    {

        String message = (notification instanceof Notification.HtmlImContentProvidingNotification)
                ? ((Notification.HtmlImContentProvidingNotification) notification).getHtmlImContent()
                : notification.getIMContent();

        if (!StringUtils.isEmpty(message))
        {
            try
            {
                HttpPost method = setupPostMethod();

                if (resultsSummary != null) {


                    Set<Author> authors = resultsSummary.getUniqueAuthors();
                    if (!authors.isEmpty())
                    {
                        message += " Responsible Users: ";

                        ArrayList<String> usernames = new ArrayList<String>();

                        for (Author author: authors)
                        {
                            usernames.add(author.getFullName());
                        }

                        message += String.join(", ", usernames);
                    }
                }

                JSONObject attachments = new JSONObject();
                JSONObject object = new JSONObject();
                try {
                    //TODO fill this with the information that Artemis needs
                    object.put("result", plan.getLatestResultsSummary());
                    attachments.put("attachments", new JSONArray().put(object));
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("payload", attachments.toString()));
                    method.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

                } catch (JSONException e) {
                    log.error("JSON construction error :" + e.getMessage(), e);
                }
                catch (UnsupportedEncodingException e) {
                    log.error("Unsupported Encoding Exception :" + e.getMessage(), e);
                }
                try {
                    log.debug(method.getURI().toString());
                    log.debug(method.getEntity().toString());
                    client.execute(method);
                } catch (IOException e) {
                    log.error("Error using Slack API: " + e.getMessage(), e);
                }
            }
            catch(URISyntaxException e)
            {
                log.error("Error parsing webhook url: " + e.getMessage(), e);
            }
        }
    }

    private HttpPost setupPostMethod() throws URISyntaxException
    {
        return new HttpPost(new URI(webhookUrl));
    }

}
