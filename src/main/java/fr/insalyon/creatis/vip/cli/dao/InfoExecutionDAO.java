package fr.insalyon.creatis.vip.cli.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.insalyon.creatis.vip.cli.dao.HibernateUtil;
import fr.insalyon.creatis.vip.cli.model.InfoExecution;
import org.hibernate.Query;

public class InfoExecutionDAO {
	public void persist(InfoExecution infoExecution) {
		HibernateUtil.openSession();
		HibernateUtil.beginTransaction();
		HibernateUtil.getSession().save(infoExecution);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	public void upadteStatusByExecutionId(String executionIdentifier, String status) {
		HibernateUtil.openSession();
		HibernateUtil.beginTransaction();

		Query query = HibernateUtil.getSession()
				.createQuery("Select e from InfoExecution e where executionIdentifier=:id");
		query.setParameter("id", executionIdentifier);

		List<InfoExecution> listInfos = new ArrayList<>();
		for (Object l : query.list()) {
			listInfos.add((InfoExecution) l);
		}
		for (InfoExecution info : listInfos) {
			info.setStatus(status);
		}
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	public void deleteExecution (String executionIdentifier ) {
		HibernateUtil.openSession();
		HibernateUtil.beginTransaction();
		Query query = HibernateUtil.getSession()
				.createQuery("Select e from InfoExecution e where executionIdentifier=:id");
		query.setParameter("id", executionIdentifier);
		InfoExecution toDelete=(InfoExecution)query.list().get(0);
		HibernateUtil.getSession().delete(toDelete);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

	}

	public void deleteExecution (Date date) {
		HibernateUtil.openSession();
		HibernateUtil.beginTransaction();
		Query query = HibernateUtil.getSession()
				.createQuery("Select e from InfoExecution e where startdate<:date");
		query.setTimestamp("date",date);
		//query.setParameter("now",new Date());
		List<InfoExecution> toDelete=(List<InfoExecution>)query.list();
		for (InfoExecution info:toDelete) {
			HibernateUtil.getSession().delete(info);
		}
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	public InfoExecution getLastExecution() {

		HibernateUtil.openSession();
		Query query = HibernateUtil.getSession().createQuery("Select e from InfoExecution e Order By Id desc");
		List<InfoExecution> list = query.list();
		return list.get(0);

	}

	public List<InfoExecution> getAllExecutions() {
		HibernateUtil.openSession();
		Query query = HibernateUtil.getSession().createQuery("Select e from InfoExecution e");
		List<InfoExecution> res = query.list();
		HibernateUtil.closeSession();
		return res;
	}

}
