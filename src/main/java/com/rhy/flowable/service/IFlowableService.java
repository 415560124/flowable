package com.rhy.flowable.service;

import com.rhy.flowable.entity.ApproveInfo;
import liquibase.pro.packaged.P;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.List;

public interface IFlowableService {
    /**
     * 部署工作流
     * @param path
     * @return
     */
    String deployHoliday(String path);

    /**
     * 历史记录
     * @param candidateGroup
     * @return
     */
    List<ApproveInfo> historyHoliday(String candidateGroup) throws IllegalAccessException;

    /**
     * 执行工作流
     * @param employee
     * @param nrOfHolidays
     * @param description
     * @return
     */
    String runHoliday(String employee,Integer nrOfHolidays,String description);

    /**
     * 查询用户组待审批任务
     * @param candidateGroup
     * @return
     */
    List<String> taskListHoliday(String candidateGroup);
    /**
     * 查询用户组待审批任务
     * @param candidateGroup
     * @return
     */
    String completeHoliday(String taskId,boolean approved);
}
