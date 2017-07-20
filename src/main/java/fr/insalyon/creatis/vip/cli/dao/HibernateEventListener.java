package fr.insalyon.creatis.vip.cli.dao;

import fr.insalyon.creatis.vip.cli.control.Controller;
import fr.insalyon.creatis.vip.cli.model.CliEvent;
import fr.insalyon.creatis.vip.cli.model.CliEventListener;
import fr.insalyon.creatis.vip.cli.model.InfoExecution;
import fr.insalyon.creatis.vip.cli.model.InfoExecutionTransform;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by qifan on 2017/7/19.
 */
public class HibernateEventListener implements CliEventListener {

    @Override
    public void onNewEvent(CliEvent cliEvent) {
        InfoExecutionDAO infoExecutionDAO=new InfoExecutionDAO();
        switch ( cliEvent.getCliEventEnum()) {
            case INIT:
                java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
                HibernateUtil.init(Controller.databaseLocation);
                break;
            case EXECUTION_CREATED:
                infoExecutionDAO.persist(InfoExecutionTransform.executionToInfo(cliEvent.getExecution()));
                break;
            case EXECUTION_UPDATED:
                infoExecutionDAO.upadteStatusByExecutionId(cliEvent.getExecution().getIdentifier(),cliEvent.getExecution().getStatus().toString());
                break;
            case EXECUTION_DELETED:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -(Controller.refreshTime));
                infoExecutionDAO.deleteExecution(cal.getTime());
                break;
            case TERMINATE:
                HibernateUtil.close();



        }
    }


    @Override
    public boolean doStoreLocalExecutions() {
        return true;
    }

    @Override
    public List<InfoExecution> getLocalExecutions() {
        return new InfoExecutionDAO().getAllExecutions();
    }
    public InfoExecution getLastLocalExecution () {return new InfoExecutionDAO().getLastExecution();}
}
