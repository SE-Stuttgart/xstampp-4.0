package de.xstampp.service.auth.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.xstampp.common.service.ConfigurationService;

/**
 * <p>
 * Configuration for the database access with the Hibernate framework. This
 * configuration enables transaction and session management with the
 * {@link org.springframework.transaction.annotation.Transactional} annotation.
 * </p>
 * <p>
 * This configuration defines the database to use, configures the database
 * connection, sets Hibernate-specific properties and defines classes which are
 * mapped to the database tables.
 * </p>
 * <p>
 * This implementation depends on the libraries spring-orm and tomcat-dbcp.
 * </p>
 * <p>
 * Reference: <a href="https://www.baeldung.com/hibernate-5-spring">M. Gulden:
 * Bootstrapping Hibernate 5 with Spring.</a>
 * </p>
 */
@Configuration
@EnableTransactionManagement
public class PersistenceHibernateConfiguration {

	@Autowired
	private ConfigurationService configService;

	private static final String CONFIG_BASE = "database-auth.";
	private static final String DATA_PACKAGE = "de.xstampp.service.auth.data";

	/**
	 * Defines container-wide transaction and session manager for the database
	 * access. You activate it e.g. with the
	 * {@link org.springframework.transaction.annotation.Transactional} annotation
	 * in your code.
	 * 
	 * @return transaction and session manager.
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new HibernateTransactionManager(sessionFactory().getObject());
	}

	/**
	 * Defines and configures container-wide session factory access for the database
	 * access. The session factory instance ({@link org.hibernate.SessionFactory})
	 * is injected in services which perform CRUD-operations on entities.
	 * 
	 * @return factory bean instance which creates a Hibernate session factory. This
	 *         factory bean is not used directly. You inject and call methods
	 *         directly on the {@link org.hibernate.SessionFactory} instance in your
	 *         code.
	 */
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
		bean.setDataSource(dataSource());
		bean.setPackagesToScan(DATA_PACKAGE);
		bean.setHibernateProperties(hibernateProperties());
		return bean;
	}

	/**
	 * Defines and configures container-wide database connection management.
	 * 
	 * @return an instance which defines the database connection. It is used e.g. by
	 *         the
	 *         {@link org.springframework.orm.hibernate5.LocalSessionFactoryBean}
	 *         instance.
	 */
	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(configService.getStringProperty(CONFIG_BASE + "driver"));
		dataSource.setUrl(configService.getStringProperty(CONFIG_BASE + "url"));
		dataSource.setUsername(configService.getStringProperty(CONFIG_BASE + "user"));
		dataSource.setPassword(configService.getStringProperty(CONFIG_BASE + "password"));
		dataSource.setInitialSize(Integer.parseInt(configService.getStringProperty(CONFIG_BASE + "pool.min-size")));
		dataSource.setMinIdle(dataSource.getInitialSize());
		dataSource.setMaxIdle(dataSource.getInitialSize());
		dataSource.setMaxTotal(Integer.parseInt(configService.getStringProperty(CONFIG_BASE + "pool.max-size")));
		return dataSource;
	}

	/**
	 * Defines container-wide exception translation for the services of the
	 * persistence layer. This bean translates each Hibernate-specific exception
	 * into a Spring exception. You activate the exception translation using the
	 * {@link org.springframework.stereotype.Repository} as annotation for your
	 * service.
	 * 
	 * @return a bean for the exception translation.
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	private Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty(AvailableSettings.DIALECT,
				configService.getStringProperty(CONFIG_BASE + "dialect"));
		hibernateProperties.setProperty("hibernate.temp.use_jdbc_metadata_defaults",
				configService.getStringProperty(CONFIG_BASE + "defaultsJdbcMetadata"));
		hibernateProperties.setProperty(AvailableSettings.SHOW_SQL,
				configService.getStringProperty(CONFIG_BASE + "showSQL"));
		return hibernateProperties;
	}
}
