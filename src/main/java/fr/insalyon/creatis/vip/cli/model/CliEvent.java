package fr.insalyon.creatis.vip.cli.model;

import fr.insalyon.creatis.vip.java_client.model.Execution;

/**
 * Created by qifan on 2017/7/19.
 */
public class CliEvent {
    public enum CliEventEnum {
        INIT,EXECUTION_CREATED,EXECUTION_UPDATED,EXECUTION_DELETED,TERMINATE
    }
    private Execution execution;
    private CliEventEnum cliEventEnum;

    public CliEvent(Execution execution, CliEventEnum cliEventEnum) {
        this.execution = execution;
        this.cliEventEnum = cliEventEnum;
    }

    public CliEventEnum getCliEventEnum() {
        return cliEventEnum;
    }

    public Execution getExecution() {
        return execution;
    }
}
