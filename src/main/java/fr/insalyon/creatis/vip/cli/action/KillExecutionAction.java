package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.dao.InfoExecutionDAO;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.DeleteExecutionConfiguration;

/**
 * Created by qifan on 2017/6/15.
 */
public class KillExecutionAction implements Action<Object> {

    private DefaultApi api;
    private Arguments args;
    private String executionId;

    public KillExecutionAction (DefaultApi api, Arguments args) {
        this.api = api;
        this.args = args;
        setExecutionIdentifier();
    }

    private void setExecutionIdentifier() {
        try{

            executionId=args.getArgsWithoutFlag().get(0);
        } catch (IndexOutOfBoundsException e) {
            InfoExecutionDAO infoDao=new InfoExecutionDAO();
            executionId=infoDao.getLastExecution().getExecutionIdentifier();
        }
    }
    @Override
    public Object execute() throws ApiException {
        DeleteExecutionConfiguration d=new DeleteExecutionConfiguration();
        d.setDeleteFiles(true);
        api.deleteExecution(executionId,d);
        return 1;
    }
}
