/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.spring.test.servicetask;

import static org.activiti.engine.impl.test.JobTestHelper.areJobsAvailable;
import static org.activiti.engine.impl.test.JobTestHelper.waitForJobExecutorToProcessAllJobs;
import static org.assertj.core.api.Assertions.assertThat;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 */
@ContextConfiguration("classpath:org/activiti/spring/test/servicetask/servicetaskSpringTest-context.xml")
public class ServiceTaskSpringDelegationTest extends SpringActivitiTestCase {

    @Test
    @Deployment
    public void testDelegateExpression() {
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("delegateExpressionToSpringBean");
        assertThat(runtimeService.getVariable(procInst.getId(), "myVar")).isEqualTo("Activiti BPMN 2.0 process engine");
        assertThat(runtimeService.getVariable(procInst.getId(), "fieldInjection")).isEqualTo("fieldInjectionWorking");
    }

    @Test
    @Deployment
    public void testAsyncDelegateExpression() throws Exception {
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("delegateExpressionToSpringBean");
        assertThat(areJobsAvailable(processEngine)).isTrue();
        waitForJobExecutorToProcessAllJobs(processEngine, 5000, 500);
        Thread.sleep(1000);
        assertThat(runtimeService.getVariable(procInst.getId(), "myVar")).isEqualTo("Activiti BPMN 2.0 process engine");
        assertThat(runtimeService.getVariable(procInst.getId(), "fieldInjection")).isEqualTo("fieldInjectionWorking");
    }

    @Test
    @Deployment
    public void testMethodExpressionOnSpringBean() {
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("methodExpressionOnSpringBean");
        assertThat(runtimeService.getVariable(procInst.getId(), "myVar")).isEqualTo("ACTIVITI BPMN 2.0 PROCESS ENGINE");
    }

    @Test
    @Deployment
    public void testAsyncMethodExpressionOnSpringBean() {
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("methodExpressionOnSpringBean");
        assertThat(areJobsAvailable(processEngine)).isTrue();
        waitForJobExecutorToProcessAllJobs(processEngine, 5000, 500);
        assertThat(runtimeService.getVariable(procInst.getId(), "myVar")).isEqualTo("ACTIVITI BPMN 2.0 PROCESS ENGINE");
    }

    @Test
    @Deployment
    public void testExecutionAndTaskListenerDelegationExpression() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("executionAndTaskListenerDelegation");
        assertThat(runtimeService.getVariable(processInstance.getId(), "executionListenerVar")).isEqualTo("working");
        assertThat(runtimeService.getVariable(processInstance.getId(), "taskListenerVar")).isEqualTo("working");

        assertThat(runtimeService.getVariable(processInstance.getId(), "executionListenerField")).isEqualTo("executionListenerInjection");
        assertThat(runtimeService.getVariable(processInstance.getId(), "taskListenerField")).isEqualTo("taskListenerInjection");
    }

}
