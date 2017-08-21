package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.client.data.ApiException;
import fr.insalyon.creatis.vip.client.data.api.DefaultApi;
import fr.insalyon.creatis.vip.client.data.model.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static fr.insalyon.creatis.vip.cli.control.Controller.*;

/**
 * Created by qifan on 2017/7/24.
 */
public class GetGateReleaseAction {

    private Arguments args;
    private DefaultApi api;

    public GetGateReleaseAction(DefaultApi api, Arguments args) {
        this.args = args;
        this.api = api;
    }

    public void execute() throws ApiException {
        List<Path> paths = api.listDirectory(GATERELEASEPATH);
        for (Path p : paths) {
            if (!p.getIsDirectory()) {
                System.out.println(p.getPlatformURI());
            }
        }

    }


    /** version manuelle
     public void execute () throws IOException {
     String path =vipUriPrefix + "/path/directory?uri=" + GATERELEASEPATH;
     HttpURLConnection httpConnection = (HttpURLConnection) new URL(path).openConnection();
     httpConnection.setRequestMethod("GET");
     httpConnection.setRequestProperty("apiKey", apiKeyValue);
     InputStream response = null;
     response = httpConnection.getInputStream();
     BufferedReader br = null;
     StringBuilder sb = new StringBuilder();
     String line;
     br = new BufferedReader(new InputStreamReader(response));
     while ((line = br.readLine()) != null) {
     sb.append(line);
     }
     br.close();
     System.out.println(sb.toString());
     }
     **/
}
