/*
 * The MIT License
 *
 * Copyright (c) 2017, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.jenkins.docker.pipeline;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.RestartableJenkinsRule;

public class DockerNodeStepTest {
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Rule
    public RestartableJenkinsRule story = new RestartableJenkinsRule();

    @Test
    public void simpleProvision() throws Exception {
        story.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                WorkflowJob j = story.j.jenkins.createProject(WorkflowJob.class, "simpleProvision");
                j.setDefinition(new CpsFlowDefinition("node {\n" +
                        "  dockerNode('localhost', 'maven:3-alpine', '/home/jenkins') {\n" +
                        "    echo 'NODE_NAME: ' + env.NODE_NAME\n" +
                        "    sh 'mvn -version'\n" +
                        "  }\n" +
                        "}\n", true));
                WorkflowRun r = story.j.buildAndAssertSuccess(j);
                story.j.assertLogContains("hi", r);
            }
        });
    }
}
