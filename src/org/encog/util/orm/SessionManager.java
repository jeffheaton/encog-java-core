package org.encog.util.orm;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class SessionManager {
	
	private SessionFactory sessionFactory;
	private AnnotationConfiguration config;
	
	public SessionManager()
	{
		this(new AnnotationConfiguration().configure());
	}
	
	public SessionManager(String path)
	{
		this("org.hsqldb.jdbcDriver",
			"jdbc:hsqldb:file:"+path+";type=cached;shutdown=true",
			"sa",
			"",
			"org.hibernate.dialect.HSQLDialect");
	}
	
	public SessionManager(String driver,String url,String uid,String pwd,String dialect)
	{
		AnnotationConfiguration config = new AnnotationConfiguration();
		config.setProperty("hibernate.connection.driver_class",driver);
		config.setProperty("hibernate.connection.url",url);
		config.setProperty("hibernate.connection.username",uid);
		config.setProperty("hibernate.connection.password",pwd);
		config.setProperty("hibernate.dialect",dialect);
		
		config.setProperty("hibernate.connection.pool_size","1");
		config.setProperty("hibernate.current_session_context_class","thread");
		config.setProperty("hibernate.cache.provider_class","org.hibernate.cache.NoCacheProvider");
		config.setProperty("hibernate.show_sql","false");
		init(config);
	}
	
	public SessionManager(AnnotationConfiguration config)
	{
		init(config);	
	}
	
	private void init(AnnotationConfiguration config)
	{
		try {
			this.config = config;
			
			
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.Fix.class);
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.Alias.class);
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.Word.class);
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.Lemma.class);
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.Fix.class);
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.WordType.class);
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.WordTypePossibility.class);
			this.config.addAnnotatedClass(org.encog.bot.spider.WorkloadItem.class);
			
			
            sessionFactory = config.buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ORMError(ex);
        }	
	}
	
	public ORMSession openSession()
	{
		org.hibernate.Session hibernateSession = sessionFactory.openSession(new DataObjectInterceptor());
		ORMSession result = new ORMSession(hibernateSession);
		
		return result;
		
	}
	
	public void export()
	{
		SchemaExport export = new SchemaExport(this.config);
		export.create(true,true);
	}
	
}
