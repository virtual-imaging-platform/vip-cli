package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.vue.UtilIO;
import fr.insalyon.creatis.vip.client.data.ApiException;
import fr.insalyon.creatis.vip.client.data.api.DefaultApi;
import fr.insalyon.creatis.vip.client.data.model.UploadData;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.sql.rowset.serial.SerialRef;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

import static fr.insalyon.creatis.vip.cli.control.Controller.*;

/**
 * Created by qifan on 2017/7/24.
 */
public class UploadAction {
    private DefaultApi api;
    private Arguments args;
    private String[] uploadPath;
    private String localPath;
    private String encodedFile;


    public UploadAction(DefaultApi api, Arguments args) throws IOException {
        this.api = api;
        this.args = args;
        setPath();
    }

    private void setPath () throws IOException {
        try {
            localPath = args.getArgsWithoutFlag().get(0);
        } catch (ArrayIndexOutOfBoundsException ae) {
            System.err.println("Please enter the file path");
        }
        try {
            uploadPath=args.getArgsWithoutFlag().get(1).split("/");
        } catch (ArrayIndexOutOfBoundsException ae2) {
            System.err.println("Please enter the upload destination");
        }
        try {
            encodedFile = encodeFileToBase64Binary(new File(localPath));
        } catch (IOException e) {
            System.err.println("Failed to open file");
            throw new IOException("failed to open file");
        }
    }


    public void execute() throws ApiException {
        String toVerify="";
        for (int i=0;i<uploadPath.length;i++) {
            toVerify += uploadPath[i];
            if (!api.doesPathExists(URIPREFIX + INPUTPREFIX + toVerify)) {
                api.createPath(URIPREFIX + INPUTPREFIX + toVerify);
            }
            if (i + 1 < uploadPath.length) {
                toVerify += "/";
            }
        }
        String fileDirOnVip = INPUTPREFIX+toVerify + "/" + localPath.substring(localPath.lastIndexOf(File.separator)+1);
        UploadData uploadData=new UploadData();
        uploadData.setUri(URIPREFIX + fileDirOnVip);
        uploadData.setPathContent(encodedFile);
        api.uploadFile(uploadData);
    }

    /** Version manuelle
    public void execute () throws IOException {
        String toVerify="";
        for (int i=0;i<uploadPath.length;i++) {
            toVerify+=uploadPath[i];
            URL checkExist = new URL(vipUriPrefix + "/path/exists?uri=vip://vip.creatis.insa-lyon.fr" + INPUTPREFIX+toVerify);
            HttpsURLConnection checkDirBaseConn = (HttpsURLConnection) checkExist.openConnection();
            checkDirBaseConn.setRequestMethod("GET");
            checkDirBaseConn.setRequestProperty("apiKey", apiKeyValue);
            String msg = UtilIO.getStringFromInputStream(checkDirBaseConn.getInputStream());
            System.out.println(msg);
            System.out.println(checkDirBaseConn.getResponseCode());
            System.out.println(checkDirBaseConn.getResponseMessage());
            checkDirBaseConn.disconnect();


            if (msg.equals("false")) {
                URL create=new URL (vipUriPrefix+"/path/directory?uri=vip://vip.creatis.insa-lyon.fr"+INPUTPREFIX+toVerify);
                HttpsURLConnection CreateDirBaseConn = (HttpsURLConnection) create.openConnection();
                CreateDirBaseConn.setRequestMethod("POST");
                CreateDirBaseConn.setRequestProperty("apiKey", apiKeyValue);
                System.out.println(CreateDirBaseConn.getResponseCode());
                System.out.println(CreateDirBaseConn.getResponseMessage());
                CreateDirBaseConn.disconnect();

            }
            if (i+1<uploadPath.length) {
                toVerify+="/";
            }
        }

        URL url = new URL(vipUriPrefix + "/path/content");
        HttpsURLConnection UploadConn = (HttpsURLConnection) url.openConnection();
        UploadConn.setDoOutput(true);
        UploadConn.setRequestMethod("PUT");
        UploadConn.setRequestProperty("apiKey", apiKeyValue);
        UploadConn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = UploadConn.getOutputStream();
        String fileDirOnVip = INPUTPREFIX+toVerify + "/" + localPath.substring(localPath.lastIndexOf(File.separator)+1);
        System.out.println("file dir on vip: "+fileDirOnVip);
        String jsonString = new JSONObject()
                .put("uri", "vip://vip.creatis.insa-lyon.fr" + fileDirOnVip)
                .put("pathContent", encodedFile).toString();
        os.write(jsonString.getBytes());
        os.flush();
        os.close();
        int responseCode=UploadConn.getResponseCode();
        System.out.println(responseCode);
        System.out.println(UploadConn.getResponseMessage());
        if(responseCode==200) {
            System.out.println("success");
        } else {
            System.err.println("failed");
        }
        UploadConn.disconnect();

    }
    **/
    private String encodeFileToBase64Binary(File file) throws IOException {

        byte[] bytes = loadFile(file);
        String encoded = Base64.getEncoder().encodeToString(bytes);
        return encoded;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read chosenFile " + file.getName());
        }
        is.close();
        return bytes;

    }


}
