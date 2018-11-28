package de.tum.in.www1.bamboo.artemis;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class ArtemisMessageParsingTest {

    private final String INPUT = "<img src='http://bamboo.int/bamboo/images/iconsv4/icon-build-successful.png' height='16' width='16' align='absmiddle' />&nbsp;<a href='http://bamboo.int/bamboo/browse/TEST-TEST-8'>test &rsaquo; test &rsaquo; #8</a> passed. Manual run by <a href=\"http://bamboo.int/bamboo/browse/user/admin\">admin</a>";
    private final String OUTPUT_FALLBACK = "test › test › #8 passed. Manual run by admin";
    private final String OUTPUT_TEXT = "<http://bamboo.int/bamboo/browse/TEST-TEST-8|test › test › #8> passed. Manual run by <http://bamboo.int/bamboo/browse/user/admin|admin>";

    private final String BRANCH_INPUT = "<img src='http://bamboo.int/bamboo/images/iconsv4/icon-build-failed.png' height='16' width='16' align='absmiddle' />&nbsp;<a href='http://bamboo.int/bamboo/browse/AA-SLAP0-5'>Atlassian Anarchy &rsaquo; Sounds like a plan &rsaquo; <img src='http://bamboo.int/bamboo/images/icons/branch.png' height='16' width='16' align='absmiddle' />&nbsp;test-branch &rsaquo; #5</a> failed. Manual run by <a href=\"http://bamboo.int/bamboo/browse/user/admin\">Admin</a>\n";
    private final String BRANCH_OUTPUT_FALLBACK = "Atlassian Anarchy › Sounds like a plan › test-branch › #5 failed. Manual run by Admin";
    private final String BRANCH_OUTPUT_TEXT = "<http://bamboo.int/bamboo/browse/AA-SLAP0-5|Atlassian Anarchy › Sounds like a plan › test-branch › #5> failed. Manual run by <http://bamboo.int/bamboo/browse/user/admin|Admin>";

    private final String MAIL_INPUT = "<img src='http://bamboo.int/bamboo/images/iconsv4/icon-build-successful.png' height='16' width='16' align='absmiddle' />&nbsp;<a href='http://bamboo.int/bamboo/browse/DEMO-DP-8'>DEMO &rsaquo; DEMO PLAN &rsaquo; #8</a> passed. Manual run by <a href=\"http://bamboo.int/bamboo/browse/user/mattias\">Räksmörgås RÄKSMÖRGÅS  &lt;raksmargas@gmail.com&gt;</a>";
    private final String MAIL_OUTPUT_FALLBACK = "DEMO › DEMO PLAN › #8 passed. Manual run by Räksmörgås RÄKSMÖRGÅS &lt;raksmargas@gmail.com&gt;";
    private final String MAIL_OUTPUT_TEXT = "<http://bamboo.int/bamboo/browse/DEMO-DP-8|DEMO › DEMO PLAN › #8> passed. Manual run by <http://bamboo.int/bamboo/browse/user/mattias|Räksmörgås RÄKSMÖRGÅS &lt;raksmargas@gmail.com&gt;>";

    private final String UNKNOWN_USERNAME_INPUT = "<img src='http://bamboo.int/bamboo/images/iconsv4/icon-build-successful.png' height='16' width='16' align='absmiddle' />&nbsp;<a href='http://bamboo.int/bamboo/browse/DEMO-DP-8'>DEMO &rsaquo; DEMO PLAN &rsaquo; #8</a> passed. Manual run by <a href=\"http://bamboo.int/bamboo/browse/user/unknown\">[unknown]</a>";
    private final String UNKNOWN_USERNAME_OUTPUT_FALLBACK = "DEMO › DEMO PLAN › #8 passed. Manual run by [unknown]";
    private final String UNKNOWN_USERNAME_OUTPUT_TEXT = "<http://bamboo.int/bamboo/browse/DEMO-DP-8|DEMO › DEMO PLAN › #8> passed. Manual run by <http://bamboo.int/bamboo/browse/user/unknown|[unknown]>";

    private final String DEPLOY_INPUT = "<img src='http://bamboo.int/bamboo/images/iconsv4/icon-build-queued.png'/>&nbsp;<a href=\"http://bamboo.int/bamboo/deploy/viewDeploymentProjectEnvironments.action?id=3309569\">demo</a>  <a href=\"http://bamboo.int/bamboo/deploy/viewDeploymentVersion.action?versionId=3571714\">demo-release-2</a> has started deploying to <a href=\"http://bamboo.int/bamboo/deploy/viewEnvironment.action?id=3375105\">Development</a>. <a href=\"http://bamboo.int/bamboo/deploy/viewDeploymentResult.action?deploymentResultId=3833861\">See details</a>.";
    private final String DEPLOY_OUTPUT = "<http://bamboo.int/bamboo/deploy/viewDeploymentProjectEnvironments.action?id=3309569|demo> <http://bamboo.int/bamboo/deploy/viewDeploymentVersion.action?versionId=3571714|demo-release-2> has started deploying to <http://bamboo.int/bamboo/deploy/viewEnvironment.action?id=3375105|Development>. <http://bamboo.int/bamboo/deploy/viewDeploymentResult.action?deploymentResultId=3833861|See details>.";

    private final String DEPLOY_SPECIAL_INPUT = "<img src='http://bamboo.int/bamboo/images/iconsv4/icon-build-queued.png'/>&nbsp;<a href=\"http://bamboo.int/bamboo/deploy/viewDeploymentProjectEnvironments.action?id=3309569\">demo</a>  <a href=\"http://bamboo.int/bamboo/deploy/viewDeploymentVersion.action?versionId=3571714\">demo-release-2</a> has started deploying to <a href=\"http://bamboo.int/bamboo/deploy/viewEnvironment.action?id=3375105\">DEV - 2) Trigger Solr Backup</a>. <a href=\"http://bamboo.int/bamboo/deploy/viewDeploymentResult.action?deploymentResultId=3833861\">See details</a>.";
    private final String DEPLOY_SPECIAL_OUTPUT = "<http://bamboo.int/bamboo/deploy/viewDeploymentProjectEnvironments.action?id=3309569|demo> <http://bamboo.int/bamboo/deploy/viewDeploymentVersion.action?versionId=3571714|demo-release-2> has started deploying to <http://bamboo.int/bamboo/deploy/viewEnvironment.action?id=3375105|DEV - 2) Trigger Solr Backup>. <http://bamboo.int/bamboo/deploy/viewDeploymentResult.action?deploymentResultId=3833861|See details>.";

    @Test
    public void testFallbackMessage() {
        String branch_output = ArtemisNotificationTransport.fallbackMessage(BRANCH_INPUT);
        String output = ArtemisNotificationTransport.fallbackMessage(INPUT);
        assertEquals(OUTPUT_FALLBACK, output);
        assertEquals(BRANCH_OUTPUT_FALLBACK, branch_output);
    }

    @Test
    public void testTextMessage() {
        String output = ArtemisNotificationTransport.textMessage(INPUT);
        String branch_output = ArtemisNotificationTransport.textMessage(BRANCH_INPUT);
        assertEquals(OUTPUT_TEXT, output);
        assertEquals(BRANCH_OUTPUT_TEXT, branch_output);
    }

    /**
     * Test that we can properly handle messages for users that has an email adress as part of their username.
     * This typically appears if users in Bamboo is not a 100% match with users in the repo's use, for example:
     * - bitbucket
     * - github   
     * - etc.
     * The same issue appears if someone adds <foo@bar.com> to their users real name.
     */
    @Test
    public void testExternalUser() {
        final String mailOutput = ArtemisNotificationTransport.textMessage(MAIL_INPUT);
        final String mailFallback = ArtemisNotificationTransport.fallbackMessage(MAIL_INPUT);
        assertEquals(MAIL_OUTPUT_TEXT, mailOutput);
        assertEquals(MAIL_OUTPUT_FALLBACK, mailFallback);
    }

    /**
     * Test that we can properly handle messages where the user is [unknown].
     * This seems to happen when a plan has been triggered by repository changes but Bamboo fails to
     * correctly parse the revision history and cannot assign a user.
     */
    @Test
    public void testUnknownUser() {
        final String unknownUserOutput = ArtemisNotificationTransport.textMessage(UNKNOWN_USERNAME_INPUT);
        final String unknownUserFallback = ArtemisNotificationTransport.fallbackMessage(UNKNOWN_USERNAME_INPUT);
        assertEquals(UNKNOWN_USERNAME_OUTPUT_TEXT, unknownUserOutput);
        assertEquals(UNKNOWN_USERNAME_OUTPUT_FALLBACK, unknownUserFallback);
    }

    /**
     * Test that we can handle deployment messages even if the the Bamboo build plan is a subset of the release name.
     */
    @Test
    public void testDeploymentMessage() {
        assertEquals(DEPLOY_OUTPUT, ArtemisNotificationTransport.textMessage(DEPLOY_INPUT));
    }

    /**
     * Test that we can handle deployments to environments with special characters in their name.
     */
    @Test
    public void testDeploymentMessageSpecialChars() {
        assertEquals(DEPLOY_SPECIAL_OUTPUT, ArtemisNotificationTransport.textMessage(DEPLOY_SPECIAL_INPUT));
    }
}
