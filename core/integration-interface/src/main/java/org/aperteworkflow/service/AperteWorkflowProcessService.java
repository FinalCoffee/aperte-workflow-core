package org.aperteworkflow.service;

import pl.net.bluesoft.rnd.processtool.model.BpmTask;
import pl.net.bluesoft.rnd.processtool.model.ProcessInstance;
import pl.net.bluesoft.rnd.processtool.model.UserData;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessDefinitionConfig;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessQueueConfig;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessStateAction;
import pl.net.bluesoft.rnd.processtool.model.nonpersistent.ProcessQueue;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;


import org.aperteworkflow.bpm.graph.GraphElement;
import org.aperteworkflow.service.fault.AperteWebServiceError;

/**
 * @author tlipski@bluesoft.net.pl
 * @author kkolodziej@bluesoft.net.pl
 * 
 */
public interface AperteWorkflowProcessService {

    ProcessInstance createProcessInstance(ProcessDefinitionConfig config,
                                          String externalKey,
                                          UserData user,
                                          String description,
                                          String keyword,
                                          String source,
										  String internalId);
    ProcessInstance getProcessData(String internalId) throws AperteWebServiceError;
    boolean isProcessRunning(String internalId);
    void saveProcessInstance(ProcessInstance processInstance);
    BpmTask assignTaskFromQueue(ProcessQueue q, UserData user);
    BpmTask assignSpecificTaskFromQueue(ProcessQueue q, BpmTask task, UserData user);
    BpmTask getTaskDataForProcessInstance(String taskExecutionId, String taskName);
    BpmTask getTaskData(String taskId);
    List<BpmTask> findProcessTasksByNames(ProcessInstance pi, UserData user, Set<String> taskNames);
    Integer getRecentTasksCount(Calendar minDate, UserData user);
    Collection<BpmTask> getAllTasks(UserData user);
    List<String> getOutgoingTransitionNames(String executionId) throws AperteWebServiceError;
    List<String> getOutgoingTransitionDestinationNames(String executionId) throws AperteWebServiceError;
    void adminReassignProcessTask(ProcessInstance pi, BpmTask bpmTask, UserData user);
 //   void adminCompleteTask(ProcessInstance pi, BpmTask bpmTask, ProcessStateAction action);

    void deployProcessDefinitionBytes(
            ProcessDefinitionConfig cfg,
            ProcessQueueConfig[] queues,
            byte[] processMapDefinition,
            byte[] processMapImageStream,
            byte[] logo); 
         
     
     
     
    void deployProcessDefinition(byte[] cfgXmlFile, byte[] queueXmlFile,
                                 byte[] processMapDefinition, byte[] processMapImageStream, byte[] logo);
	Collection<ProcessQueue> getUserAvailableQueues(String userLogin) throws AperteWebServiceError;
	boolean isProcessOwnedByUser(String internalId, String userLogin) throws AperteWebServiceError;
	void assignTaskToUser(String taskId, String userLogin) throws AperteWebServiceError;
	void adminCancelProcessInstance(String internalId) throws AperteWebServiceError;
	
	
	 
	ProcessInstance startProcessInstance(String bpmnkey, String userLogin) throws AperteWebServiceError;
	List<BpmTask> findProcessTasks(String internalId, String userLogin) throws AperteWebServiceError;
	UserData getSubstitutingUser(String userLogin) throws AperteWebServiceError;


	void performAction(String internalId, String actionName,
			String bpmTaskName, String userLogin) throws AperteWebServiceError;
	List<BpmTask> findUserTasksPaging(Integer offset, Integer limit,
			String userLogin)throws AperteWebServiceError;
	List<BpmTask> findUserTasks(String internalId, String userLogin)
			throws AperteWebServiceError;
	void adminCompleteTask(String internalId, String actionName,
			String bpmTaskName) throws AperteWebServiceError;
}