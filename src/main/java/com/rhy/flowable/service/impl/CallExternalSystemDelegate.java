package com.rhy.flowable.service.impl;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallExternalSystemDelegate implements JavaDelegate, ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("请假审批扩展程序执行" + execution.getVariable("employee"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext = applicationContext;
    }
}
