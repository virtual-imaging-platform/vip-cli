package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.dao.InfoExecutionDAO;
import fr.insalyon.creatis.vip.cli.model.CliEventListener;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import java.util.List;

import static fr.insalyon.creatis.vip.cli.control.Controller.enableDatabase;
import static java.lang.System.exit;

/**
 * get the information of a specified execution from VIP
 * if no execution identifier indicated, the last execution in the local database will be used by default
 */

public class GetExecutionAction implements Action<Execution> {
	private String executionId;
	private Arguments args;
	private DefaultApi api;
	private CliEventListener cliEventListenerWithStorage;

	public GetExecutionAction(Controller.CliContext cliContext) {
		this.args=cliContext.arguments;
		this.api=cliContext.api;
		cliEventListenerWithStorage=cliContext.getListenerWithStorage();
		setExecutionId();
	}
	private void setExecutionId(){
		try{
			executionId=args.getArgsWithoutFlag().get(0);
		} catch (IndexOutOfBoundsException e) {
			if (cliEventListenerWithStorage!=null) {
				executionId = cliEventListenerWithStorage.getLastLocalExecution().getExecutionIdentifier();
			} else {
				System.err.println("please enter execution identifier");
				exit(0);
			}
		}
	}

	
	public Execution execute() throws ApiException{
		return api.getExecution(executionId);
		
	}
}
