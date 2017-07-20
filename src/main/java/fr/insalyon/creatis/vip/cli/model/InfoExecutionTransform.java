package fr.insalyon.creatis.vip.cli.model;

import fr.insalyon.creatis.vip.java_client.model.Execution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qifan on 2017/7/19.
 */
public class InfoExecutionTransform {

    public static InfoExecution executionToInfo (Execution execution) {
        InfoExecution infoExecution=new InfoExecution();
        infoExecution.setExecutionIdentifier(execution.getIdentifier());
        infoExecution.setPipelineIdentifier(execution.getPipelineIdentifier());
        infoExecution.setExecutionName(execution.getName());
        infoExecution.setStartdate(new Date(execution.getStartDate()));
        infoExecution.setStatus(execution.getStatus().toString());
        return infoExecution;
    }

    public static List<InfoExecution> executionListToInfoList (List<Execution> executionList) {
        List<InfoExecution> infoExecutionList=new ArrayList<>();
        for (Execution execution:executionList) {
            infoExecutionList.add(executionToInfo(execution));
        }
        return infoExecutionList;
    }
}
