package fr.insalyon.creatis.vip.cli.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.model.CliEventListener;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;

import static fr.insalyon.creatis.vip.cli.control.Controller.URIPREFIX;
import static java.lang.System.exit;

/**
 * action to download the result of execution
 */
public class GetResultAction  {

    private String executionId;
    private String directory;
    private Arguments args;
    private DefaultApi apiProcessing;
    private fr.insalyon.creatis.vip.client.data.api.DefaultApi apiData;
    private CliEventListener cliEventListenerWithStorage;


    public GetResultAction(Controller.CliContext cliContext, fr.insalyon.creatis.vip.client.data.api.DefaultApi apiData) {
        this.args =cliContext.arguments;
        this.apiProcessing = cliContext.api;
        this.cliEventListenerWithStorage=cliContext.getListenerWithStorage();
        this.apiData = apiData;
        setExecutionId();
        setDirectory();
    }

    //set where to put the result
    //result directory is necessary for this action
    private void setDirectory() {
        if (args.getArgsWithoutFlag().size()>=1) {
            directory=args.getArgsWithoutFlag().get(args.getArgsWithoutFlag().size()-1);
        } else {
            System.err.println("Directory missing");
            exit(0);
        }
    }

    private void setExecutionId() {
        if (args.getArgsWithoutFlag().size()>=2) {
            executionId = args.getArgsWithoutFlag().get(0);
        } else if (args.getArgsWithoutFlag().size()==1){
            if (cliEventListenerWithStorage!=null) {
                executionId = cliEventListenerWithStorage.getLastLocalExecution().getExecutionIdentifier();
            } else {
                System.err.println("please enter execution identifier");
                exit(0);
            }
        }
    }

    public String getDirectory() {
        return directory;
    }

    public Map<String,String> execute () throws ApiException, fr.insalyon.creatis.vip.client.data.ApiException {
        Map<String, List<String>> returnedFiles = apiProcessing.getExecution(executionId).getReturnedFiles();

        List<String> urls=null;

        //if the execution is gate, a -gate option is needed
        if (args.hasOption("gate")) {
            urls=returnedFiles.get("merged_result");

        } else {
            urls = returnedFiles.get("output_file");
        }

        if (urls==null) {
            System.err.println("No results are ready");
            System.err.println("If you are trying to download gate lab result, verify -gate option");
            exit(0);
        }

        Map<String,String> results = new HashMap<>();
        for (String url : urls) {
            //transform vip/users/aab_ccd to vip/Home
            int pos = url.indexOf('/', "/vip/Users//".length());
            //add file name and encoded file string to a map
            results.put(url.substring(url.lastIndexOf("/")+1),apiData.downloadFile(URIPREFIX+"/vip/Home"+ url.substring(pos))) ;
        }
        return results;
    }

/**
    public List<String> execute() throws ApiException {
        Map<String, List<String>> returnedFiles = apiProcessing.getExecution(executionId).getReturnedFiles();
        //TODO:check urls not null
        List<String> urls=null;
       // for (String key : returnedFiles.keySet()) {
          //  System.out.println(key + ":" + returnedFiles.get(key));
        //}

        //if the execution is gate, a -gate option is needed
        if (args.hasOption("gate")) {
            urls=returnedFiles.get("merged_result");

        } else {
             urls = returnedFiles.get("output_file");
        }
        //transform the urls of the file to a downloadable apiProcessing request path
        List<String> usableUrls = new ArrayList<>();
        String base = apiProcessing.getApiClient().getBasePath() + "/path/content?uri=vip://vip.creatis.insa-lyon.fr/vip/Home";
        for (String url : urls) {
            System.out.println(url);
            int pos = url.indexOf('/', "/vip/Users//".length());
            usableUrls.add(base + url.substring(pos));


        }
        //System.out.println(apiData.downloadFile());
        return usableUrls;
    }
**/
}