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

You first need to [Set up the Atlassian Plugin SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project). Or you could just do a `brew tap atlassian/tap; brew install atlassian/tap/atlassian-plugin-sdk` on macOS if you use HomeBrew... At the project top level (where the pom.xml is):

1.	Compile : `atlas-compile`
2.	Run : `atlas-run`
3.	Test: `atlas-unit-test`
4.	Debug : `atlas-debug`

Feedback? Questions?
--------------------

Email: krusche@in.tum.de

Debug
-----

You can reach the debug log under `<bamboo-host>/chain/viewChainActivityLog.action?planKey=XXX`.

Sample request
--------------
```json
{
   "build":{
      "artifact":false,
      "number":86,
      "reason":"Manual build",
      "buildCompletedDate":"2019-05-05T09:51:30.960Z[Zulu]",
      "testSummary":{
         "duration":62,
         "ignoredCount":0,
         "failedCount":1,
         "existingFailedCount":1,
         "quarantineCount":0,
         "successfulCount":14,
         "description":"1 of 15 failed",
         "skippedCount":0,
         "fixedCount":0,
         "totalCount":15,
         "newFailedCount":0
      },
      "vcs":[
         {
            "commits":[

            ],
            "id":"f4158f583a02dc3b9bda47e64749fdbf7e9bdcef",
            "repositoryName":"Assignment"
         },
         {
            "commits":[

            ],
            "id":"9be1bb278e08d03ab2519b746702196cead996be",
            "repositoryName":"tests"
         }
      ],
      "jobs":[
         {
            "skippedTests":[

            ],
            "failedTests":[
               {
                  "name":"testBubbleSort",
                  "methodName":"Bubble sort",
                  "className":"de.de.SortingExampleBehaviorTest",
                  "errors":[
                     "java.lang.AssertionError: Problem: BubbleSort does not sort correctly expected:<[Mon Feb 15 00:00:00 GMT 2016, Sat Apr 15 00:00:00 GMT 2017, Fri Sep 15 00:00:00 GMT 2017, Thu Nov 08 00:00:00 GMT 2018]> but was:<[Thu Nov 08 00:00:00 GMT 2018, Sat Apr 15 00:00:00 GMT 2017, Mon Feb 15 00:00:00 GMT 2016, Fri Sep 15 00:00:00 GMT 2017]>\n\tat org.junit.Assert.fail(Assert.java:88)\n\tat org.junit.Assert.failNotEquals(Assert.java:834)\n\tat org.junit.Assert.assertEquals(Assert.java:118)\n\tat de.de.SortingExampleBehaviorTest.testBubbleSort(SortingExampleBehaviorTest.java:37)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:498)\n\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)\n\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n\tat org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:298)\n\tat org.junit.internal.runners.statements.FailOnTimeout$CallableStatement.call(FailOnTimeout.java:292)\n\tat java.util.concurrent.FutureTask.run(FutureTask.java:266)\n\tat java.lang.Thread.run(Thread.java:748)\n"
                  ]
               }
            ],
            "successfulTests":[
               {
                  "name":"testUseBubbleSortForSmallList",
                  "methodName":"Use bubble sort for small list",
                  "className":"de.de.SortingExampleBehaviorTest"
               },
               {
                  "name":"testConstructors[Policy]",
                  "methodName":"Constructors[policy]",
                  "className":"de.de.ConstructorTest"
               },
               {
                  "name":"testClass[Policy]",
                  "methodName":"Class[policy]",
                  "className":"de.de.ClassTest"
               },
               {
                  "name":"testClass[Context]",
                  "methodName":"Class[context]",
                  "className":"de.de.ClassTest"
               },
               {
                  "name":"testMethods[SortStrategy]",
                  "methodName":"Methods[sort strategy]",
                  "className":"de.de.MethodTest"
               },
               {
                  "name":"testMethods[Policy]",
                  "methodName":"Methods[policy]",
                  "className":"de.de.MethodTest"
               },
               {
                  "name":"testClass[BubbleSort]",
                  "methodName":"Class[bubble sort]",
                  "className":"de.de.ClassTest"
               },
               {
                  "name":"testMergeSort",
                  "methodName":"Merge sort",
                  "className":"de.de.SortingExampleBehaviorTest"
               },
               {
                  "name":"testAttributes[Context]",
                  "methodName":"Attributes[context]",
                  "className":"de.de.AttributeTest"
               },
               {
                  "name":"testUseMergeSortForBigList",
                  "methodName":"Use merge sort for big list",
                  "className":"de.de.SortingExampleBehaviorTest"
               },
               {
                  "name":"testAttributes[Policy]",
                  "methodName":"Attributes[policy]",
                  "className":"de.de.AttributeTest"
               },
               {
                  "name":"testMethods[Context]",
                  "methodName":"Methods[context]",
                  "className":"de.de.MethodTest"
               },
               {
                  "name":"testClass[SortStrategy]",
                  "methodName":"Class[sort strategy]",
                  "className":"de.de.ClassTest"
               },
               {
                  "name":"testClass[MergeSort]",
                  "methodName":"Class[merge sort]",
                  "className":"de.de.ClassTest"
               }
            ],
            "tasks":[
               {
                  "description":"checkout",
                  "pluginKey":"com.atlassian.bamboo.plugins.vcs:task.vcs.checkout",
                  "isEnabled":true,
                  "isFinal":false,
                  "state":"SUCCESS"
               },
               {
                  "description":"compile",
                  "pluginKey":"com.atlassian.bamboo.plugins.maven:task.builder.mvn3",
                  "isEnabled":true,
                  "isFinal":false,
                  "state":"SUCCESS"
               },
               {
                  "description":"test-compile",
                  "pluginKey":"com.atlassian.bamboo.plugins.maven:task.builder.mvn3",
                  "isEnabled":true,
                  "isFinal":false,
                  "state":"SUCCESS"
               },
               {
                  "description":"tests",
                  "pluginKey":"com.atlassian.bamboo.plugins.maven:task.builder.mvn3",
                  "isEnabled":true,
                  "isFinal":false,
                  "state":"FAILED"
               }           
            ],
            "staticCodeAnalysisReports":[
               {
                  "tool":"SPOTBUGS",
                  "issues":[
                     {
                         "filePath":"/buildDir/testExercise/assignment/src/com/ls1/staticCodeAnalysis/App.java",
                         "startLine":16,
                         "endLine":16,
                         "rule":"ES_COMPARING_PARAMETER_STRING_WITH_EQ",
                         "category":"BAD_PRACTICE",
                         "message":"Comparison of String parameter using == or != in com.stefan.staticCodeAnalysis.App.equalString(String)",
                         "priority":"1"
                     }
                  ]
               },
               {
                  "tool":"CHECKSTYLE",
                  "issues":[
                     {
                         "filePath":"/buildDir/testExercise/assignment/src/com/ls1/staticCodeAnalysis/App.java",
                         "startLine":7,
                         "endLine":7,
                         "startColumn":1,
                         "endColumn":1,
                         "rule":"HideUtilityClassConstructorCheck",
                         "category":"design",
                         "message":"Utility classes should not have a public or default constructor.",
                         "priority":"error"
                     }
                  ]
               },
               {
                  "tool":"PMD",
                  "issues":[
                     {
                         "filePath":"/buildDir/testExercise/assignment/src/com/ls1/staticCodeAnalysis/App.java",
                         "startLine":10,
                         "endLine":10,
                         "startColumn":16,
                         "endColumn":16,
                         "rule":"UnusedLocalVariable",
                         "category":"Best Practices",
                         "message":"Avoid unused local variables such as 'b'.",
                         "priority":"3"
                     }
                  ]
               }
            ],
            "logs": [
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile) on project BSN-2-Tests: Compilation failure"
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] /home/bamboo/bamboo-agent-home/xml-data/build-dir/BSNBSN2-ADMIN-JOB1/assignment/src/de/tum/in/ase/BubbleSort.java:[9,2] ';' expected"
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] -&gt; [Help 1]"
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] "
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch."
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] Re-run Maven using the -X switch to enable full debug logging."
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] "
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] For more information about the errors and possible solutions, please read the following articles:"
               },
               {
                  "date":"2019-05-05T09:51:30.960Z[Zulu]",
                  "log":"[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException"
               }
            ],
            "id":14581874
         }
      ],
      "successful":false
   },
   "secret":"SomeVerySecretToken",
   "notificationType":"Completed Plan Notification",
   "plan":{
      "key":"CO1BRD3-SOLUTION"
   }
}

```
