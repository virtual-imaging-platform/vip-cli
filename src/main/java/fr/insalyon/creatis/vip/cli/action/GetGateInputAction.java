package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.model.CliEventListener;
import fr.insalyon.creatis.vip.cli.model.GetGateInputException;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import java.io.IOException;
import java.util.Map;

import static fr.insalyon.creatis.vip.cli.control.Controller.URIPREFIX;
import static java.lang.System.exit;

/**
 * Created by qifan on 2017/7/13.
 */
public class GetGateInputAction {
    private DefaultApi apiProcessing;
    private fr.insalyon.creatis.vip.client.data.api.DefaultApi apiData;
    private Arguments args;
    private String executionId;
    private CliEventListener cliEventListenerWithStorage;

    public GetGateInputAction(Controller.CliContext cliContext, fr.insalyon.creatis.vip.client.data.api.DefaultApi apiData) {
        this.args = cliContext.arguments;
        this.apiProcessing = cliContext.api;
        cliEventListenerWithStorage = cliContext.getListenerWithStorage();
        this.apiData = apiData;
        setExecutionId();
    }

    private void setExecutionId() {
        try {
            executionId = args.getArgsWithoutFlag().get(0);
        } catch (IndexOutOfBoundsException e) {
            if (cliEventListenerWithStorage != null) {
                executionId = cliEventListenerWithStorage.getLastLocalExecution().getExecutionIdentifier();
            } else {
                System.err.println("please enter execution identifier");
                exit(0);
            }
        }
    }


    public Map<String, Object> execute() throws ApiException, GetGateInputException, IOException, fr.insalyon.creatis.vip.client.data.ApiException {

        Execution execution = apiProcessing.getExecution(executionId);
        Map<String, Object> inputs = execution.getInputValues();
        String gateInput = (String) inputs.get("GateInput");
        if (gateInput == null) {
            throw new GetGateInputException("no gate input found");
        } else {
             if (!apiData.doesPathExists(URIPREFIX+gateInput)) {
                 throw new GetGateInputException("the file gate input exists no more");
             }
            /**
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
            String resp = new String(sb);
            if (resp.equals("false")) {
                throw new GetGateInputException("the file gate input exists no more");
            }**/

        }
        return inputs;


    }
}
