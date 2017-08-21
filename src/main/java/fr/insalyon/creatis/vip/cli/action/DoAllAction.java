package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.dao.InfoExecutionDAO;
import fr.insalyon.creatis.vip.cli.model.CliEventListener;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

/**
 * Created by qifan on 2017/6/26.
 */

/**
 * Action which test the status of the execution and download the result when its ready
 */

public class DoAllAction implements Action<List<String>> {
    public final static String DOWNLOADBASEPATH= Controller.vipUriPrefix + "/path/content?uri=vip://vip.creatis.insa-lyon.fr/vip/Home";
    private DefaultApi api;
    private Arguments args;
    private String directory;
    private CliEventListener cliEventListenerWithStorage;


    public DoAllAction (Controller.CliContext cliContext) {
        this.api=cliContext.api;
        this.args=cliContext.arguments;
        cliEventListenerWithStorage=cliContext.getListenerWithStorage();
        this.directory=args.getArgsWithFlag().get("results");
        args.getArgsWithFlag().remove("results");
    }


    public String getDirectory() {
        return directory;
    }


    @Override
    public List<String> execute() throws ApiException {
        //init execution
        InitAndExecuteAction initAndExecuteAction = new InitAndExecuteAction(api,args);
        Execution execution=initAndExecuteAction.execute();

        //persist in the local database

        // test the status of execution
        while (execution.getStatus()!= Execution.StatusEnum.FINISHED) {
            if (execution.getStatus()== Execution.StatusEnum.KILLED) {
                System.out.println("Killed");
                exit(0);
            }
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            execution=api.getExecution(execution.getIdentifier());

            //infoDao.upadteStatusByExecutionId(execution.getIdentifier(),execution.getStatus().toString());

        }
        //download the returned file
        Map<String, List<String>> returnedFiles = api.getExecution(execution.getIdentifier()).getReturnedFiles();
        List<String> urls = returnedFiles.get("output_file");
        List<String> usableUrls = new ArrayList<>();
        for (String url : urls) {
            int pos = url.indexOf('/', 13);
            usableUrls.add(DOWNLOADBASEPATH + url.substring(pos));
        }
        return usableUrls;
    }
}
