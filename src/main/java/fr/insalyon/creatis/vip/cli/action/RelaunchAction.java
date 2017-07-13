package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

/**
 * Created by qifan on 2017/7/12.
 */
public class RelaunchAction {
    private String ExecutionIdentifier;
    private Execution toRelaunch;
    private DefaultApi api;
    private Arguments args;
    private String directoryOnVip;

    public RelaunchAction( DefaultApi api, Arguments args) {

        this.api = api;
        this.args = args;
        try {
            ExecutionIdentifier = args.getArgsWithoutFlag().get(0);
            System.out.println("identifier to relaunch is "+ExecutionIdentifier);
        }catch (IndexOutOfBoundsException ie) {
            System.err.println("execution identifier not indicated");
        }
        toRelaunch=new Execution();
    }
    public void execute () throws ApiException {
        Execution execution=api.getExecution(ExecutionIdentifier);
        System.out.println (execution);
        toRelaunch.setInputValues(execution.getInputValues());
        toRelaunch.setName(execution.getName());
        toRelaunch.setPipelineIdentifier(execution.getPipelineIdentifier());
        api.initAndStartExecution(execution);

    }
}
