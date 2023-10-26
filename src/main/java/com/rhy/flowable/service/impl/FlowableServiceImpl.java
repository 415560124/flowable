package com.rhy.flowable.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rhy.flowable.entity.ApproveInfo;
import com.rhy.flowable.service.IFlowableService;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class FlowableServiceImpl implements IFlowableService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Override
    public String deployHoliday(String path) {
        Deployment deployment = repositoryService.createDeployment()
                //文件名和前缀中间必须有协议名
                .addClasspathResource(path)
                .deploy();
        //通过API查询部署的业务流
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        return processDefinition.getName();
    }

    @Override
    public List<ApproveInfo> historyHoliday(String candidateGroup) throws IllegalAccessException {
        List<ApproveInfo> approveInfos = new ArrayList<>();
        //查询已完成的流程
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery()
                .taskCandidateGroup(candidateGroup)
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();
        Set<String> instanceIds = taskInstances.stream().map(HistoricTaskInstance::getProcessInstanceId).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(instanceIds)){
            return approveInfos;
        }
        //通过instanceId查实例记录
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceIds(instanceIds)
                .finished()
                .orderByProcessInstanceEndTime().desc().list();
        for (int i = 0; i < historicProcessInstances.size(); i++) {
            HistoricProcessInstance historicProcessInstance = historicProcessInstances.get(i);
            ApproveInfo approveInfo = new ApproveInfo();
            //查询该流程的参数
            List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(historicProcessInstance.getId())
                    .list();
            for (HistoricVariableInstance variableInstance : variableInstances) {
                String variableName = variableInstance.getVariableName();
                Object value = variableInstance.getValue();
                Field[] declaredFields = ApproveInfo.class.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.getName().equals(variableName)) {
                        declaredField.setAccessible(true);
                        declaredField.set(approveInfo, value);
                    }
                }
            }
            approveInfos.add(approveInfo);
        }
        return approveInfos;
    }

    @Override
    public String runHoliday(String employee, Integer nrOfHolidays, String description) {
        Map<String,Object> variables = new HashMap<>();
        variables.put("employee",employee);
        variables.put("nrOfHolidays",nrOfHolidays);
        variables.put("description",description);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest",variables);
        return processInstance.getId();
    }

    @Override
    public List<String> taskListHoliday(String candidateGroup) {
        //获取当前managers用户组的任务列表
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(candidateGroup).list();
        List<String> res= new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Map<String, Object> variables = taskService.getVariables(tasks.get(i).getId());
            res.add(task.getId()+"）" + variables.get("employee") + "想要请假" +
                    variables.get("nrOfHolidays") + "天。理由是"+variables.get("description")+"。");
        }
        return res;
    }

    @Override
    public String completeHoliday(String taskId,boolean approved) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //获得任务的相关信息
        Map<String, Object> processVariables = taskService.getVariables(taskId);
        Map<String,Object> variables = new HashMap<>();
        variables.put("approved",approved);
        taskService.complete(taskId,variables);
        if(approved){
            Task next = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            taskService.complete(next.getId());
            return next.getId();
        }
        return task.getId();
    }
}
