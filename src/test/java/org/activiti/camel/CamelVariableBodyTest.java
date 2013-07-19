package org.activiti.camel;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineTestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:camel-activiti-context.xml")
public class CamelVariableBodyTest extends ProcessEngineTestCase {
	
	MockEndpoint service1;

    @Autowired
    CamelContext ctx;

  public void setUp() {
    //CamelContext ctx = applicationContext.getBean(CamelContext.class);
    service1 = (MockEndpoint) ctx.getEndpoint("mock:serviceBehavior");
    service1.reset();
  }
	
	@Deployment(resources = {"process/HelloCamelBody.bpmn20.xml"})
	public void testCamelBody() throws Exception {
	  service1.expectedBodiesReceived("hello world");
		Map<String, Object> varMap = new HashMap<String, Object>();
		varMap.put("camelBody", "hello world");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("HelloCamel", varMap);
		//Ensure that the variable is equal to the expected value.
		assertEquals("hello world", runtimeService.getVariable(processInstance.getId(), "camelBody"));
		service1.assertIsSatisfied();
		
		Task task = taskService.createTaskQuery().singleResult();
		
		//Ensure that the name of the task is correct.
		assertEquals("Hello Task", task.getName());
		
		//Complete the task.
		taskService.complete(task.getId());
	}
	
}