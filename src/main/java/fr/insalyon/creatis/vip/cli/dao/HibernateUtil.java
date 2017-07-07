package fr.insalyon.creatis.vip.cli.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static StandardServiceRegistry serviceRegistry;
	private static SessionFactory sessionFactory;
	private static Session session;

	public static void init(String databasePosition) throws HibernateException {
		Configuration configuration = new Configuration().configure();
		configuration.setProperty("hibernate.connection.url",databasePosition);
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	public static void close() {
		sessionFactory.close();
		StandardServiceRegistryBuilder.destroy(serviceRegistry);
	}

	public static void openSession() {

		session = sessionFactory.openSession();
	}

	public static void closeSession() {

		session.close();
	}

	public static void beginTransaction() {

		session.beginTransaction();
	}

	public static void commitTransaction() {

		session.getTransaction().commit();
	}

	public static void cancelTransaction() {
		if (session.getTransaction().isActive()) session.getTransaction().rollback();
	}

	public static Session getSession() {

		return session;
	}

}
