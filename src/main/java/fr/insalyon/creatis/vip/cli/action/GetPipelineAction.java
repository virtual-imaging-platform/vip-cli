/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;
import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Pipeline;
import java.util.List;

/**
 *
 * @author qzhang
 */
public class GetPipelineAction implements Action<Pipeline>{
    private String pipelineName;
    private String pipelineIdentifier;
    private DefaultApi api;
    

    public GetPipelineAction(DefaultApi api,Arguments args) {

        this.api=api;
        pipelineIdentifier=args.getArgsWithoutFlag().get(0);
    }

    
    public Pipeline execute() throws ApiException{

       
        return api.getPipeline(pipelineIdentifier.replaceAll("/" ,"%2F"));
    }
    
}
