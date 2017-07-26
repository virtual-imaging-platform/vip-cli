package fr.insalyon.creatis.vip.cli.vue;

import static java.lang.System.exit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.model.InfoExecution;
import fr.insalyon.creatis.vip.cli.model.InfoExecutionTransform;
import fr.insalyon.creatis.vip.cli.model.PropertyCli;
import fr.insalyon.creatis.vip.cli.model.PropertyException;
import fr.insalyon.creatis.vip.java_client.model.Execution;

public class UtilIO {
    private final static String FLAGAPIKEY = "APIKEY";
    private final static String FLAGDATABASE = "DATABASE";
    private final static String FLAGBASEPATH = "BASEPATH";
    private final static String FLAGREFRESHTIME = "REFRESHTIME";
    private final static String FLAGENABLEDATABASE = "ENABLEDATABASE";


    private static UtilIO instance;

    public static UtilIO getInstance() {
        if (instance == null) {
            instance = new UtilIO();
        }
        return instance;
    }

    private UtilIO() {
    }

    public PropertyCli getPropertyCli(File propertyFile) throws PropertyException {
        try (InputStream is = new FileInputStream(propertyFile)) {

            Properties prop = new Properties();
            prop.load(is);
            //TODO:properties elements not found exception
            String apikey = (String) prop.get(FLAGAPIKEY);
            String database = (String) prop.get(FLAGDATABASE);
            String basepath = (String) prop.get(FLAGBASEPATH);
            String refreshTime = (String) prop.get(FLAGREFRESHTIME);
            String enableDatabase = (String) prop.get(FLAGENABLEDATABASE);
            if (database == null) {
                throw new PropertyException("Database property not found");
            }
            if (basepath == null) {
                throw new PropertyException("Base Path property not found");
            }
            if (refreshTime == null) {
                throw new PropertyException("refresh time property not found");
            }

            if (enableDatabase == null) {
                throw new PropertyException("enable database property not found");
            }
            return new PropertyCli(apikey, database, basepath, refreshTime, enableDatabase);


        } catch (IOException ex) {
            // Logger.getLogger(Vue.class.getName()).log(Level.SEVERE, null,
            // ex);
            System.err.println("Properties file not found.");
            exit(0);
        }
        return null;

    }

    public void printInitExecuteResult(Execution execution) {
        System.out.println("Execution name: " + execution.getName());
        System.out.println("Execution identifier: " + execution.getIdentifier());
        //TODO: see if we print result-directory (gate problem doesnt exist for gate)
    }

    public void printExecutionStatus(Execution execution) {


        System.out.println(execution.getStatus());


    }

    /**
     * used to print local database information
     *
     * @param listExecution
     */
    public void printListInfoExecutions(List<InfoExecution> listExecution, boolean formatted) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (formatted) {
            StringBuilder toPrint = new StringBuilder();
            if (listExecution != null) {

                // TODO : check new line character on windows /Solution: use System.lineSperator() to be adapted to all OS including Windows
                for (InfoExecution e : listExecution) {
                    toPrint.append(e.getExecutionName()).append(",")
                            .append(e.getPipelineIdentifier()).append(",")
                            .append(df.format(e.getStartdate())).append(",")
                            .append(e.getStatus()).append(",")
                            .append(e.getExecutionIdentifier()).append("&&&");
                }
                System.out.println(toPrint.toString());
            }

        } else {
            if (listExecution != null) {
                StringBuilder toPrint = new StringBuilder();
                // TODO : check new line character on windows
                Collections.reverse(listExecution);
                for (InfoExecution e : listExecution) {
                    toPrint.append("Execution Name:   " + e.getExecutionName()).append(System.lineSeparator())
                            .append("Pipeline id:      " + e.getPipelineIdentifier()).append(System.lineSeparator())
                            .append("Start date:       " + df.format(e.getStartdate())).append(System.lineSeparator())
                            .append("Status:           " + e.getStatus()).append(System.lineSeparator())
                            .append("Execution id:     " + e.getExecutionIdentifier()).append(System.lineSeparator())
                            .append("-------------------").append(System.lineSeparator());
                }
                System.out.print(toPrint.toString());
            }
        }

    }

    public void printListExecutions(List<Execution> executionList, boolean formatted) {
        printListInfoExecutions(InfoExecutionTransform.executionListToInfoList(executionList), formatted);
    }

    /*
    public void downloadFile(List<String> urls, String dest) {

        try {
            for (String url : urls) {
                URL fileUrl = new URL(url);
                // System.out.println(url);
                HttpURLConnection httpConnection = (HttpURLConnection) fileUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("apiKey", Controller.apiKeyValue);
                InputStream inputStream = httpConnection.getInputStream();

                InputStream decodedInputStream = Base64.getDecoder().wrap(inputStream);
                File file = new File(dest + url.substring(url.lastIndexOf('/')));
                System.out.println(dest + url.substring(url.lastIndexOf('/')));
                file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);

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
            System.err.println(e.toString());
        }

    }
     **/
    public void downloadFile(Map<String, String> results, String dest) {
        String[] dests = dest.split(File.separator);
        String tmp = "";
        for (int it = 0; it < dests.length; it++) {
            if (!new File(tmp += File.separator + dests[it]).exists()) {
                System.out.println("Trying to create dir " + tmp);
                new File(tmp).mkdir();
            }
        }

        results.forEach((fileName, encodedContent) -> {
                    byte[] decoded = Base64.getDecoder().decode(encodedContent);
                    File file = new File(dest + File.separator + fileName);
                    try {
                        file.createNewFile();
                        OutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(decoded);
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
        );
    }

    public void printGateInputs(Map<String, Object> inputs) {
        StringBuilder sb = new StringBuilder();
        sb.append(inputs.get("CPUestimation")).append("&&&")
                .append(inputs.get("GateInput")).append("&&&")
                .append(inputs.get("GateRelease")).append("&&&")
                .append(inputs.get("NumberOfParticles")).append("&&&")
                .append(inputs.get("ParallelizationType"));
        System.out.println(sb.toString());
        /**
         System.out.print(inputs.get("CPUestimation")+"&&&");
         System.out.print(inputs.get("GateInput")+"&&&");
         System.out.print(inputs.get("GateRelease")+"&&&");
         System.out.print(inputs.get("NumberOfParticles")+"&&&");
         System.out.print(inputs.get("ParallelizationType"));
         **/
    }

    public static String getStringFromInputStream(InputStream is) throws IOException {
/**
 byte b[] = new byte[is.available()];
 is.read(b, 0, b.length);
 is.close();
 return new String(b);
 /**
 Pattern p = Pattern.compile("\\s*|\t|\r|\n");
 Matcher m = p.matcher(new String(b));

 return m.replaceAll("");
 **/
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return new String(sb);
    }
}
