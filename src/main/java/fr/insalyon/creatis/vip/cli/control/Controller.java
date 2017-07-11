/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.cli.control;

import fr.insalyon.creatis.vip.cli.action.*;
import fr.insalyon.creatis.vip.cli.dao.HibernateUtil;
import fr.insalyon.creatis.vip.cli.model.ArgumentException;
import fr.insalyon.creatis.vip.cli.model.InfoExecution;
import fr.insalyon.creatis.vip.cli.dao.InfoExecutionDAO;
import fr.insalyon.creatis.vip.cli.model.PropertyCli;
import fr.insalyon.creatis.vip.cli.model.PropertyException;
import fr.insalyon.creatis.vip.cli.vue.UtilIO;
import fr.insalyon.creatis.vip.java_client.ApiClient;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;
import org.hibernate.HibernateException;

import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

import static fr.insalyon.creatis.vip.cli.control.ArgType.SETAPIKEY;
import static java.lang.System.exit;

/**
 * @author qzhang
 */
public class Controller {
    public final static String PROPERTIESPATH = "cli.properties";
    public static String apiKeyValue;
    public static String base;
    public static String databasePosition;
    public static int refreshTime;
    public final static String TRUSTSTOREFILE= "./truststore";
    public static void main(String args[]) {

        //set truststore
        System.setProperty("javax.net.ssl.keyStore", TRUSTSTOREFILE);
        System.setProperty("javax.net.ssl.trustStore", TRUSTSTOREFILE);
        System.setProperty("javax.net.ssl.keyStorePassword", "creatis");

        //parse argument
        Arguments arguments = null;
        try {
            arguments = new Arguments(args);
        } catch (ArgumentException e) {
            System.err.println("Error while parsing arguments:");
            System.err.println(e.getMessage());
            exit(0);
        }
        //read from properties
        PropertyCli property = null;
        DefaultApi api = null;
        try {
            property = UtilIO.GetPropertyCli(new File(PROPERTIESPATH));
            apiKeyValue = property.getApiKey();
            base = property.getBasePath();
            databasePosition = property.getDataBasePosition();
            refreshTime = property.getRefreshTime();

            ApiClient client = new ApiClient();
            client.setBasePath(base);
            client.setApiKey(apiKeyValue);
            // client.setConnectTimeout();
            api = new DefaultApi(client);
        } catch (PropertyException e) {
            System.out.println(e.getMessage());
            exit(0);
        }

        //set hibernate
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        InfoExecutionDAO infoDAO = new InfoExecutionDAO();

        try {
            HibernateUtil.init(databasePosition);


            if (apiKeyValue != null) {


                switch ((arguments.getAction())) {
                    //TODO: put the dao actions into Action Classes.
                    case EXECUTE: {
                        //TODO: delete automatically too old executions in the database after launched an execution.
                        if (arguments.getArgsWithFlag().get("results") == null) {
                            InitAndExecuteAction initAndExecuteAction = new InitAndExecuteAction(api, arguments);
                            Execution execution = null;
                            try {
                                execution = initAndExecuteAction.execute();
                                UtilIO.printExecuteResult(execution);
                                InfoExecution infoExecution = new InfoExecution(execution.getIdentifier(), execution.getPipelineIdentifier(),
                                        execution.getStatus().toString(), initAndExecuteAction.getDirectoryOnVip(), new Date(execution.getStartDate()));
                                infoDAO.persist(infoExecution);
                            } catch (ApiException e) {
                                System.err.println(e.getMessage());
                            }

                        } else {
                            DoAllAction doAllAction = new DoAllAction(api, arguments);
                            try {
                                UtilIO.downloadFile(doAllAction.execute(), doAllAction.getDirectory());
                            } catch (ApiException e) {
                                System.err.println(e.getMessage());
                            }
                        }

                        break;
                    }
                    case STATUS: {
                        GetExecutionAction getExecutionAction = new GetExecutionAction(api, arguments);
                        Execution execution = null;
                        try {
                            execution = getExecutionAction.execute();
                            UtilIO.printExecutionStatus(execution);
                            infoDAO.upadteStatusByExecutionId(execution.getIdentifier(), execution.getStatus().toString());
                        } catch (ApiException e) {
                            System.err.println(e.getMessage());
                        }

                        break;
                    }
                    case EXECTUIONS:
                        if (arguments.getOptions().contains("all")) {
                            Get10LastExecutions get10LastExecutions = new Get10LastExecutions(api);
                            try {
                                UtilIO.printListExecutions(get10LastExecutions.execute());
                            } catch (ApiException e) {
                                System.err.println(e.getMessage());
                            }
                        } else {
                            UtilIO.printListInfoExecutions(infoDAO.getAllExecutions());
                        }

                        break;

                    case RESULT:
                        GetResultAction getResultAction = new GetResultAction(api, arguments);
                        try {
                            UtilIO.downloadFile(getResultAction.execute(), getResultAction.getDirectory());
                        } catch (ApiException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    case DELETE:
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -(refreshTime));
                        infoDAO.deleteExecution(cal.getTime());
                        break;

                    case PIPELINE:
                        GetPipelineAction getPipelineAction = new GetPipelineAction(api, arguments);
                        try {
                            System.out.println(getPipelineAction.execute());
                        } catch (ApiException e) {
                            System.err.println(e.getMessage());
                        }
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
                    case GETAPIKEY:
                        System.out.println(apiKeyValue);
                        break;
                    case SETAPIKEY:
                        SetApiKeyAction setApiKeyAction = new SetApiKeyAction(arguments);
                        setApiKeyAction.execute();
                        break;

                }


            } else if (arguments.getAction() == SETAPIKEY) {
                SetApiKeyAction setApiKeyAction = new SetApiKeyAction(arguments);
                setApiKeyAction.execute();
            } else {
                System.err.println("Api key not found");
                exit(0);
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }  finally {
            HibernateUtil.close();
        }


    }


}
