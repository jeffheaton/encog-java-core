package org.encog.util.orm;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class SessionManager {
	
	private static SessionManager instance;
	private SessionFactory sessionFactory;
	private AnnotationConfiguration config;
	
	private SessionManager()
	{	
	}
	
	public static SessionManager getInstance() 
	{
		if( instance==null )
		{
			instance = new SessionManager();
		}
		
		return instance;
	}
	
	public void init()
	{
		init(new AnnotationConfiguration().configure());
	}
	
	public void initHSQL(String path)
	{
		init("org.hsqldb.jdbcDriver",
			"jdbc:hsqldb:file:"+path+";type=cached;shutdown=true",
			"sa",
			"",
			"org.hibernate.dialect.HSQLDialect");
	}
	
	public void init(String driver,String url,String uid,String pwd,String dialect)
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
	
	public void init(AnnotationConfiguration config)
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
			this.config.addAnnotatedClass(org.encog.bot.spider.workload.data.WorkloadHost.class);
			this.config.addAnnotatedClass(org.encog.bot.spider.workload.data.WorkloadLocation.class);
			
			
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
