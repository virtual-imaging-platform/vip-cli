package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.model.CliEventListener;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import static java.lang.System.exit;

/**
 * Created by qifan on 2017/7/12.
 */
public class RelaunchAction {
    private String executionId;
    private Execution toRelaunch;
    private DefaultApi api;
    private Arguments args;
    private String directoryOnVip;
    private CliEventListener cliEventListenerWithStorage;

    public RelaunchAction( Controller.CliContext cliContext) {

        this.args=cliContext.arguments;
        this.api=cliContext.api;
        cliEventListenerWithStorage=cliContext.getListenerWithStorage();
        setExecutionId();
    }

    private void setExecutionId(){
        try{
            executionId=args.getArgsWithoutFlag().get(0);
        } catch (IndexOutOfBoundsException e) {
            if (cliEventListenerWithStorage!=null) {
                executionId = cliEventListenerWithStorage.getLastLocalExecution().getExecutionIdentifier();
            } else {
                System.err.println("please enter execution identifier");
                exit(0);
            }
        }
    }
    public void execute () throws ApiException {
        Execution execution=api.getExecution(executionId);
        System.out.println (execution);
        toRelaunch=new Execution();
        toRelaunch.setInputValues(execution.getInputValues());
        toRelaunch.setName(execution.getName());
        toRelaunch.setPipelineIdentifier(execution.getPipelineIdentifier());
        api.initAndStartExecution(execution);

    }
}
