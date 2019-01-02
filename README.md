Server Notifications for Bamboo
==============================

This plugin sends [Bamboo](https://www.atlassian.com/software/bamboo) notifications to any server URL.

It allows any Bamboo notification to be posted to a specific server URL.

This plugin allows you to take advantage of Bamboo's:

-	flexible notification system (ie tell me when this build fails more than 5 times!)

Requests can be validated by checking the secret, which is both included in the request body and sent as "Authorization"-Header.

Notifications Supported
-----------------------

-	all

Setup
-----

1.	Go to the *Notifications* tab of the *Configure Plan* screen.
2.	Choose a *Recipient Type* of *Server*
3.	Configure your *Server URL*
4.  Create a global variable named *SERVER_PLUGIN_SECRET_PASSWORD*, the value if this variable will be used as secret.
5.	You're done! Go and get building.

Compiling from source
---------------------

You first need to [Set up the Atlassian Plugin SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project). Or you could just do a `brew tap atlassian/tap; brew install atlassian/tap/atlassian-plugin-sdk` on a mac is you use HomeBrew... At the project top level (where the pom.xml is) :

1.	Compile : `atlas-mvn compile`
2.	Run : `atlas-run`
3.	Debug : `atlas-debug`

Feedback? Questions?
--------------------

Email: krusche@in.tum.de


Sample request
--------------
```json
{
          "build":{
             "artifact":false,
             "number":35,
             "reason":"Code has changed",
             "buildCompletedDate":"2018-12-01T12:36:26.078Z[Zulu]",
             "testSummary":{
                "duration":26,
                "ignoredCount":0,
                "failedCount":2,
                "existingFailedCount":1,
                "quarantineCount":0,
                "successfulCount":11,
                "description":"2 of 13 failed",
                "skippedCount":0,
                "fixedCount":0,
                "totalCount":13,
                "newFailedCount":1
             },
             "vcs":[
                {
                   "commits":[
                      {
                         "comment":"Break stuff.",
                         "id":"13c43a1e26e5c4635a0ac24e775fed615e069b20"
                      }
                   ],
                   "id":"13c43a1e26e5c4635a0ac24e775fed615e069b20",
                   "repositoryName":"Assignment"
                },
                {
                   "commits":[
       
                   ],
                   "id":"dcf2dba3846620a89f6c3f63cd9dedfa4336f650",
                   "repositoryName":"tests"
                }
             ],
             "failedJobs":[
                {
                   "failedTests":[
                      {
                         "name":"testBubbleSort",
                         "methodName":"Bubble sort",
                         "className":"ac1.de.BehaviorTest",
                         "errors":[
                            "java.lang.AssertionError: Problem: BubbleSort does not sort correctly expected:<[Mon Feb 15 00:00:00 GMT 2016, Sat Apr 15 00:00:00 GMT 2017, Fri Sep 15 00:00:00 GMT 2017, Thu Nov 08 00:00:00 GMT 2018]> but was:<[Mon Feb 15 00:00:00 GMT 2016, Sat Apr 15 00:00:00 GMT 2017, Thu Nov 08 00:00:00 GMT 2018, Fri Sep 15 00:00:00 GMT 2017]>\n\tat org.junit.Assert.fail(Assert.java:88)\n\tat org.junit.Assert.failNotEquals(Assert.java:834)\n\tat org.junit.Assert.assertEquals(Assert.java:118)\n\tat ac1.de.BehaviorTest.testBubbleSort(BehaviorTest.java:40)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:498)\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n\tat org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:298)\n\tat org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:292)\n\tat java.util.concurrent.FutureTask.run(FutureTask.java:266)\n\tat java.lang.Thread.run(Thread.java:748)\n"
                         ]
                      },
                      {
                         "name":"testMergeSort",
                         "methodName":"Merge sort",
                         "className":"ac1.de.BehaviorTest",
                         "errors":[
                            "java.lang.NullPointerException\n\tat java.util.Date.getMillisOf(Date.java:958)\n\tat java.util.Date.compareTo(Date.java:978)\n\tat ac1.de.MergeSort.merge(MergeSort.java:30)\n\tat ac1.de.MergeSort.mergesort(MergeSort.java:19)\n\tat ac1.de.MergeSort.performSort(MergeSort.java:10)\n\tat ac1.de.BehaviorTest.testMergeSort(BehaviorTest.java:46)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:498)\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n\tat org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:298)\n\tat org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:292)\n\tat java.util.concurrent.FutureTask.run(FutureTask.java:266)\n\tat java.lang.Thread.run(Thread.java:748)\n"
                         ]
                      }
                   ],
                   "id":11403400
                }
             ],
             "successful":false
          },
          "secret":"SomeVerySecretToken",
          "notificationType":"Completed Plan Notification",
          "plan":{
             "key":"SC1AC4ASD-BASE"
          }
       }

```