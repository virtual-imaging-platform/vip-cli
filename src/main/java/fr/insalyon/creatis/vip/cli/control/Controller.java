/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.cli.control;

import fr.insalyon.creatis.vip.cli.action.*;
import fr.insalyon.creatis.vip.cli.dao.HibernateEventListener;
import fr.insalyon.creatis.vip.cli.model.*;
import fr.insalyon.creatis.vip.cli.vue.UtilIO;
import fr.insalyon.creatis.vip.java_client.ApiClient;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import java.io.*;
import java.util.*;

import static fr.insalyon.creatis.vip.cli.control.ArgType.SETAPIKEY;
import static java.lang.System.exit;

/**
 * @author qzhang
 */
public class Controller {
    public final static String PROPERTIESPATH = "cli.properties";
    public final static String TRUSTSTOREFILE = "./truststore";

    public static String apiKeyValue;
    public static String vipUriPrefix;
    public static String databaseLocation;
    public static int refreshTime;
    public static boolean enableDatabase;

    private Arguments arguments;
    private DefaultApi api;
    private UtilIO utilIO = UtilIO.getInstance();
    private List<CliEventListener> cliEventListeners;

    public static void main(String args[]) {
        new Controller().run(args);
    }

    private void run(String[] args) {

        initializeSslConnection();
        arguments = parseArguments(args);
        api = readPropertiesSetApi();
        cliEventListeners = new ArrayList<>();
        if (enableDatabase)
            cliEventListeners.add(new HibernateEventListener());
        try {

            informListeners(CliEvent.CliEventEnum.INIT, null);

            if (apiKeyValue == null) {
                if (arguments.getAction() == SETAPIKEY) {
                    SetApiKeyAction setApiKeyAction = new SetApiKeyAction(arguments);
                    setApiKeyAction.execute();
                    exit(0);
                } else {
                    System.err.println("Api key not found");
                    exit(0);
                }
            }

            switch ((arguments.getAction())) {
                case EXECUTE:
                    doExecute();
                    break;
                case STATUS:
                    doStatus();
                    break;
                case EXECTUIONS:
                    doExecutions();
                    break;
                case RESULT:
                    GetResultAction getResultAction = new GetResultAction(new CliContext(api,arguments,cliEventListeners));
                    utilIO.downloadFile(getResultAction.execute(), getResultAction.getDirectory());
                    break;
                case DELETE:
                    informListeners(CliEvent.CliEventEnum.EXECUTION_DELETED, null);
                    break;
                case PIPELINE:
                    GetPipelineAction getPipelineAction = new GetPipelineAction(api, arguments);
                    System.out.println(getPipelineAction.execute());
                    break;
                case GETAPIKEY:
                    System.out.println(apiKeyValue);
                    break;
                case SETAPIKEY:
                    SetApiKeyAction setApiKeyAction = new SetApiKeyAction(arguments);
                    setApiKeyAction.execute();
                    break;
                case RELAUNCH:
                    RelaunchAction relaunchAction = new RelaunchAction(api, arguments);
                    relaunchAction.execute();
                case GETGATEINPUT:
                    doGetGateInput();
                    break;
                //this case is used to test args
                case TESTARGS:
                    System.out.println("**args without flags are**");
                    for (String argwithoutflag : arguments.getArgsWithoutFlag()) {
                        System.out.println(argwithoutflag);
                    }
                    System.out.println("**args with flags are**");
                    for (Map.Entry<String, String> entry : arguments.getArgsWithFlag().entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                    System.out.println("**options are**");
                    for (String opt : arguments.getOptions()) {
                        System.out.println(opt);
                    }
                    break;
            }

        } catch (ApiException ae) {
            System.err.println(ae.getMessage());
        } finally {
            informListeners(CliEvent.CliEventEnum.TERMINATE, null);
        }


    }

    private void initializeSslConnection() {
        //set truststore
        System.setProperty("javax.net.ssl.keyStore", TRUSTSTOREFILE);
        System.setProperty("javax.net.ssl.trustStore", TRUSTSTOREFILE);
        System.setProperty("javax.net.ssl.keyStorePassword", "creatis");
    }

    private Arguments parseArguments(String[] args) {
        //parse argument
        Arguments arguments = null;
        try {
            arguments = new Arguments(args);
        } catch (ArgumentException e) {
            System.err.println("Error while parsing arguments:");
            System.err.println(e.getMessage());
            exit(0);
        }
        return arguments;
    }

    private DefaultApi readPropertiesSetApi() {
        //read from properties
        PropertyCli property = null;
        DefaultApi api = null;
        try {
            property = utilIO.getPropertyCli(new File(PROPERTIESPATH));
            apiKeyValue = property.getApiKey();
            vipUriPrefix = property.getBasePath();
            databaseLocation = property.getDataBasePosition();
            refreshTime = property.getRefreshTime();
            switch (property.getEnableDatabase()) {
                case "true":
                    enableDatabase = true;
                    break;
                case "false":
                    enableDatabase = false;
                    break;
                default:
                    enableDatabase = false;
            }

            ApiClient client = new ApiClient();
            client.setConnectTimeout(3000);
            client.setBasePath(vipUriPrefix);
            client.setApiKey(apiKeyValue);
            // client.setConnectTimeout();
            api = new DefaultApi(client);
        } catch (PropertyException e) {
            System.out.println(e.getMessage());
            exit(0);
        }
        return api;
    }

    private void doExecute() throws ApiException {
        //TODO: delete automatically too old executions in the database after launched an execution.
        if (arguments.getArgsWithFlag().get("results") == null) {
            InitAndExecuteAction initAndExecuteAction = new InitAndExecuteAction(api, arguments);
            Execution execution = initAndExecuteAction.execute();
            utilIO.printInitExecuteResult(execution);
            informListeners(CliEvent.CliEventEnum.EXECUTION_CREATED, execution);
        } else {
            DoAllAction doAllAction = new DoAllAction(new CliContext(api,arguments,cliEventListeners));
            try {
                utilIO.downloadFile(doAllAction.execute(), doAllAction.getDirectory());
            } catch (ApiException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void doStatus() throws ApiException {
        GetExecutionAction getExecutionAction = new GetExecutionAction(new CliContext(api,arguments,cliEventListeners));
        Execution execution = getExecutionAction.execute();
        utilIO.printExecutionStatus(execution);
        informListeners(CliEvent.CliEventEnum.EXECUTION_UPDATED, execution);
    }

    private void doExecutions() throws ApiException {
        // use new option "local"
        // if "local" option is present then :
        //   * get executions from listeners that store them
        //   * if there qre none or more than 2, raise exception

        if (arguments.hasOption("local")) {

            utilIO.printListInfoExecutions(getListenerWithStorage().getLocalExecutions(), arguments.hasOption("formatted"));

        } else {

            Get10LastExecutions get10LastExecutions = new Get10LastExecutions(api);
            utilIO.printListExecutions(get10LastExecutions.execute(), arguments.hasOption("formatted"));
        }
    }

    private void doGetGateInput() throws ApiException {
        GetGateInputAction getGateInputAction = new GetGateInputAction(api, arguments);
        try {
            utilIO.printGateInputs(getGateInputAction.execute());
        } catch (GetGateInputException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void informListeners(CliEvent.CliEventEnum eventEnum, Execution execution) {
        CliEvent cliEvent = new CliEvent(execution, eventEnum);
        for (CliEventListener cliEventListener : cliEventListeners) {
            cliEventListener.onNewEvent(cliEvent);
        }
    }

    private CliEventListener getListenerWithStorage() {
        CliEventListener cliEventListenerWithStorage = null;
        for (CliEventListener cliEventListener : cliEventListeners) {
            if (cliEventListener.doStoreLocalExecutions()) {
                if (cliEventListenerWithStorage == null) {
                    cliEventListenerWithStorage = cliEventListener;
                } else {
                    System.err.println("Error: too many event listener with storage");
                }
            }
        }

        return cliEventListenerWithStorage;
    }

    public class CliContext {
        public DefaultApi api;
        public Arguments arguments;
        public List<CliEventListener> cliEventListenerList;

        public CliContext(DefaultApi api, Arguments arguments, List<CliEventListener> cliEventListenerList) {
            this.api = api;
            this.arguments = arguments;
            this.cliEventListenerList = cliEventListenerList;
        }
        public CliEventListener getListenerWithStorage() {
            CliEventListener cliEventListenerWithStorage = null;
            for (CliEventListener cliEventListener : cliEventListeners) {
                if (cliEventListener.doStoreLocalExecutions()) {
                    if (cliEventListenerWithStorage == null) {
                        cliEventListenerWithStorage = cliEventListener;
                    } else {
                        System.err.println("Error: too many event listener with storage");
                    }
                }
            }
            return cliEventListenerWithStorage;
        }



    }


}
