package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.java_client.ApiException;
import fr.insalyon.creatis.vip.java_client.api.DefaultApi;
import fr.insalyon.creatis.vip.java_client.model.Execution;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qifan on 2017/7/4.
 */
public class Get10LastExecutions implements Action<List<Execution>> {
    private DefaultApi api;

    public Get10LastExecutions(DefaultApi api) {
        this.api = api;
    }

    @Override
    public List<Execution> execute() throws ApiException {
        List<Execution>result= api.listExecutions();
        List<Execution> tenLast=new ArrayList<>();
        for (int i=0;i<10;i++) {
            try {
            if (result.get(i)!=null) {
                tenLast.add(result.get(i));
            } } catch (IndexOutOfBoundsException e) {
                //do nothing
            }
        }
        return tenLast;
    }
}
