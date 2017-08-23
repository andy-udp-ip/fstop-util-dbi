package tw.com.fstop.util.dbi;

import java.sql.Connection;
import javax.sql.DataSource;

public class DbConnectionInfo 
{
	String jdbcDriver = null;
	String jdbcUrl = null;
	String dbUser = null;
	String dbPassword = null;
	String jndiName = null;
	Boolean useJndi = false;
	DataSource dataSource = null;
	Connection connection = null;
	
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public Connection getConnection()
	{
		return connection;
	}
	public void setConnection(Connection connection) 
	{
		this.connection = connection;
	}
	public String getJdbcDriver() {
		return jdbcDriver;
	}
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getJndiName() {
		return jndiName;
	}
	public void setJndiName(String jndiName) 
	{
		if (jndiName != null && jndiName.isEmpty() == false)
		{
			useJndi = true;
		}
		this.jndiName = jndiName;
	}
	public Boolean isUseJndi() {
		return useJndi;
	}
	public void setUseJndi(Boolean useJndi) {
		this.useJndi = useJndi;
	}
	
	
	
}