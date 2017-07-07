package fr.insalyon.creatis.vip.cli.vue;

import static java.lang.System.exit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.model.InfoExecution;
import fr.insalyon.creatis.vip.cli.model.PropertyCli;
import fr.insalyon.creatis.vip.cli.model.PropertyException;
import fr.insalyon.creatis.vip.java_client.model.Execution;
import fr.insalyon.creatis.vip.java_client.model.Pipeline;
import fr.insalyon.creatis.vip.java_client.model.PlatformProperties;

public class UtilIO {
    private final static String FLAGAPIKEY = "APIKEY";
    private final static String FLAGDATABASE = "DATABASE";
    private final static String FLAGBASEPATH = "BASEPATH";
    private final static String FLAGREFRESHTIME = "REFRESHTIME";


    public static PropertyCli GetPropertyCli(File propertyFile) throws PropertyException {
        try {
            InputStream is = new FileInputStream(propertyFile);
            Properties prop = new Properties();
            prop.load(is);
            //TODO:properties elements not found exception
            String apikey = (String) prop.get(FLAGAPIKEY);
            String database = (String) prop.get(FLAGDATABASE);
            String basepath = (String) prop.get(FLAGBASEPATH);
            String refreshTime = (String) prop.get(FLAGREFRESHTIME);
            is.close();
            if (database==null) {
                throw new PropertyException("Database property not found");
            }
            if (basepath==null) {
                throw new PropertyException("Base Path property not found");
            }
            if (refreshTime==null) {
                throw new PropertyException("refresh time property not found");
            }
            return new PropertyCli(apikey, database, basepath, refreshTime);


        } catch (IOException ex) {
            // Logger.getLogger(Vue.class.getName()).log(Level.SEVERE, null,
            // ex);
            System.err.println("Properties file not found.");
            exit(0);
        }
        return null;

    }

    public static void printExecuteResult(Execution execution, String directoryOnVip) {


        System.out.println("identifier: " + execution.getIdentifier());
        System.out.println("directory: " + directoryOnVip);


    }

    public static void printExecutionStatus(Execution execution) {


        System.out.println(execution.getStatus());


    }

    public static void printListInfoExecutions(List<InfoExecution> listExecution) {
        for (InfoExecution info : listExecution) {
            System.out.println(info);
        }

    }
    public static void printListExecutions (List<Execution> executionList) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (Execution e:executionList) {
            System.out.println (e.getIdentifier()+","+e.getPipelineIdentifier()+","+df.format(new Date(e.getStartDate()))+","+e.getStatus());
        }
    }

    public static void printPlatformProperties(PlatformProperties platformproperty) {
        System.out.println("Platform name: " + platformproperty.getPlatformName());
        System.out.println("Platform description: " + platformproperty.getPlatformDescription());
        System.out.println("Api version supported: " + platformproperty.getSupportedAPIVersion());
        System.out.println("Contact email: " + platformproperty.getEmail());
    }

    public static void printPipelinesList(List<Pipeline> pipelinesList) {
        for (Pipeline pipeline : pipelinesList) {

            System.out.println("Name: " + pipeline.getName());
            System.out.println("Version: " + pipeline.getVersion());
            System.out.println("Can execute: " + pipeline.getCanExecute() + "\r\n");
        }
    }

    public static void downloadFile(List<String> urls, String dest) {

        try {
            for (String url : urls) {
                URL fileUrl = new URL(url);
                System.out.println(url);
                HttpURLConnection httpConnection = (HttpURLConnection) fileUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("apiKey", Controller.apiKeyValue);
                InputStream inputStream = httpConnection.getInputStream();

                InputStream decodedInputStream = Base64.getDecoder().wrap(inputStream);
                File file = new File(dest + url.substring(url.lastIndexOf('/')));
                file.createNewFile();
                OutputStream outputStream =
                        new FileOutputStream(file);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = decodedInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                inputStream.close();
                decodedInputStream.close();
                outputStream.close();

                System.out.println("Done!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
