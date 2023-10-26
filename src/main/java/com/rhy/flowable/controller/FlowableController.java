package com.rhy.flowable.controller;

import com.rhy.flowable.entity.ApproveInfo;
import com.rhy.flowable.entity.CommonRes;
import com.rhy.flowable.service.IFlowableService;
import liquibase.pro.packaged.F;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("flowable")
public class FlowableController {
    @Autowired
    private IFlowableService flowableService;
    /**
     * 部署工作流
     * @param path
     * @return
     */
    @GetMapping("/deploy")
    public CommonRes<String> deployHoliday(@RequestParam String path) {
        return CommonRes.ok(flowableService.deployHoliday(path));
    }

    /**
     * 历史记录
     * @param candidateGroup
     * @return
     */
    @GetMapping("/history")
    public List<ApproveInfo> historyHoliday(@RequestParam String candidateGroup) throws IllegalAccessException{
        return flowableService.historyHoliday(candidateGroup);
    }

    /**
     * 执行工作流
     * @param employee
     * @param nrOfHolidays
     * @param description
     * @return
     */
    @GetMapping("/run")
    public CommonRes<String> runHoliday(@RequestParam String employee, @RequestParam Integer nrOfHolidays, @RequestParam String description){
        return CommonRes.ok(flowableService.runHoliday(employee, nrOfHolidays, description));
    }

    /**
     * 查询用户组待审批任务
     * @param candidateGroup
     * @return
     */
    @GetMapping("/taskList")
    public CommonRes<List<String>> taskListHoliday(@RequestParam String candidateGroup){
        return CommonRes.ok(flowableService.taskListHoliday(candidateGroup));
    }
    /**
     * 审批任务
     * @param candidateGroup
     * @return
     */
    @GetMapping("/complete")
    public CommonRes<String> completeHoliday(@RequestParam String taskId,@RequestParam boolean approved){
        return CommonRes.ok(flowableService.completeHoliday(taskId,approved));
    }
}
