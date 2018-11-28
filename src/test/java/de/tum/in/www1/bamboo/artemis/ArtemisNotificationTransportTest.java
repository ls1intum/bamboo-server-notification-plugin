package de.tum.in.www1.bamboo.artemis;

import com.atlassian.bamboo.notification.buildhung.BuildHungNotification;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.project.Project;
import com.atlassian.bamboo.variable.CustomVariableContext;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class ArtemisNotificationTransportTest
{
    private final String WEBHOOK_URL = "alamakotaakotmaapitoken";

    @Mock
    private Project project;
    @Mock
    private Plan plan;
    @Mock
    private CustomVariableContext customVariableContext;

    @Test
    public void testCorrectUrlsAreHit()
    {
        when(project.getKey()).thenReturn("BAM");
        when(plan.getProject()).thenReturn(project);
        when(plan.getBuildKey()).thenReturn("MAIN");
        when(plan.getName()).thenReturn("Main");
        when(customVariableContext.substituteString("alamakotaakotma")).thenReturn("alamakotaakotma");
        when(customVariableContext.substituteString("alamakotaakotmaapitoken")).thenReturn("alamakotaakotmaapitoken");


        final PlanResultKey planResultKey = PlanKeys.getPlanResultKey("BAM-MAIN", 3);

        BuildHungNotification notification = new BuildHungNotification()
        {
            public String getHtmlImContent()
            {
                return "IM Content";
            }

        };

        ArtemisNotificationTransport hnt = new ArtemisNotificationTransport(WEBHOOK_URL, plan, null, null, customVariableContext);

        //dirty reflections trick to inject mock HttpClient
        try
        {
            Field field = ArtemisNotificationTransport.class.getDeclaredField("client");
            field.setAccessible(true);
            field.set(hnt, new MockHttpClient());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

        hnt.sendNotification(notification);
    }

    public class MockHttpClient extends CloseableHttpClient {

        @Override
        protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
            assertTrue(request instanceof HttpPost);
            HttpPost postMethod = (HttpPost) request;
            assertEquals(WEBHOOK_URL, request.getRequestLine().getUri());

            try {
                assert(postMethod.getEntity() instanceof UrlEncodedFormEntity);
                UrlEncodedFormEntity entity = (UrlEncodedFormEntity) postMethod.getEntity();

                for (NameValuePair vp : URLEncodedUtils.parse(entity))
                {
                    if (vp.getName().compareTo("payload")==0)
                    {
                        JSONObject payload = new JSONObject(vp.getValue());

                        assertEquals("myBamboo", payload.getString("username"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
        }

        public void close() throws IOException {

        }

        public HttpParams getParams() {
            return null;
        }

        public ClientConnectionManager getConnectionManager() {
            return null;
        }
    }
}
