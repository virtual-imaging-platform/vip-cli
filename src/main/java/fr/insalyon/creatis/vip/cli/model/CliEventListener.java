package fr.insalyon.creatis.vip.cli.model;

import java.util.List;

/**
 * Created by qifan on 2017/7/19.
 */
public interface CliEventListener {
    void onNewEvent (CliEvent cliEvent);
    boolean doStoreLocalExecutions();
    List<InfoExecution> getLocalExecutions();
    public InfoExecution getLastLocalExecution ();
}
