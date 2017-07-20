package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.dao.InfoExecutionDAO;
import fr.insalyon.creatis.vip.cli.model.GetGateInputException;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import static fr.insalyon.creatis.vip.cli.control.Controller.apiKeyValue;
import static fr.insalyon.creatis.vip.cli.control.Controller.vipUriPrefix;

/**
 * Created by qifan on 2017/7/13.
 */
public class GetGateInputAction {
    private DefaultApi api;
    private Arguments args;
    private String executionId;
    public GetGateInputAction(DefaultApi api, Arguments args)  {
        this.api = api;
        this.args = args;
        try{
            executionId=args.getArgsWithoutFlag().get(0);
        } catch (IndexOutOfBoundsException e) {
            InfoExecutionDAO infoDao=new InfoExecutionDAO();
            executionId=infoDao.getLastExecution().getExecutionIdentifier();
        }
    }

    public Map<String,Object> execute () throws ApiException, GetGateInputException, IOException {

        Execution execution=api.getExecution(executionId);
        Map<String,Object> inputs=execution.getInputValues();
        String gateInput=(String)inputs.get("GateInput");
        if (gateInput==null) {
            throw new GetGateInputException("no gate input found");
        } else {
            URL verifyExist = new URL(vipUriPrefix + "/path/exists?uri=vip://vip.creatis.insa-lyon.fr" + gateInput);
            HttpsURLConnection httpConnection1 = (HttpsURLConnection) verifyExist.openConnection();
            httpConnection1.setRequestMethod("GET");
            httpConnection1.setRequestProperty("apiKey", apiKeyValue);
            InputStream is = httpConnection1.getInputStream();
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String line;
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String resp=new String(sb);
            if (resp.equals("false")) {
                throw new GetGateInputException("the file gate input exists no more");
            }
        }
        return inputs;





    }
}
