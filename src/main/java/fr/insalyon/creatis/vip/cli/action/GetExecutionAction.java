package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.dao.InfoExecutionDAO;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import java.util.List;

public class GetExecutionAction implements Action<Execution> {
	private String executionId;
	private Arguments args;
	private DefaultApi api;

	public GetExecutionAction(DefaultApi api,Arguments args) {
		this.args=args;
		this.api=api;
		setExecutionId();
	}
	private void setExecutionId(){
		try{
			executionId=args.getArgsWithoutFlag().get(0);
		} catch (IndexOutOfBoundsException e) {
			InfoExecutionDAO infoDao=new InfoExecutionDAO();
			executionId=infoDao.getLastExecution().getExecutionIdentifier();
		}
	}

	
	public Execution execute() throws ApiException{
		return api.getExecution(executionId);
		
	}
}
