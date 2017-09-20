package tw.com.fstop.util.dbi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import tw.com.fstop.util.PropUtil;
import tw.com.fstop.util.StrUtil;

/**
 * Data base connection coordinator.
 * <pre>
 * Provide data base connections by custom property. 
 * Some property key is hard code and can be modified if required.
 * for Example:
 *   default.db.jndiname
 *   .db.jndiname
 *   
 * Load configurations from dbi.properties.
 * </pre>
 * 
 * @since 1.0.0
 */
public class ConnectionCoordinator 
{
	//private static Logger logger = Logger.getLogger(ConnectionCoordinator.class.getName());
	private static Logger log = LoggerFactory.getLogger(ConnectionCoordinator.class);
	static String DB_SETTING_PROP = "dbi.properties";
	
	static Properties prop = null;
	
	static 
	{
	    try
        {
            prop =PropUtil.loadProperties(DB_SETTING_PROP);
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
            
        }
	}
	
	static Map<String, DbConnectionInfo> info = Collections.synchronizedMap(new HashMap<String, DbConnectionInfo>());
	
	public static DbConnectionInfo setParam(String jndi, 
											String url, 
											String driver, 
											String user, 
											String password,
											DataSource ds,
											Connection connection
											)
	{
		DbConnectionInfo dbInfo = new DbConnectionInfo();
		dbInfo.setDbPassword(password);
		dbInfo.setDbUser(user);
		dbInfo.setJdbcDriver(driver);
		dbInfo.setJdbcUrl(url);
		dbInfo.setJndiName(jndi);
		dbInfo.setDataSource(ds);
		dbInfo.setConnection(connection);
		return dbInfo;
	}
	
	
	public static DbConnectionInfo getDbConnectionInfo(String tableName)
	{
		//先試著以 tableName 來取設定，若是有值，則表示特殊設定
		String url = null; //prop.getProperty(tableName + ".db.url", "");
		String jndi = null; //prop.getProperty(tableName + ".db.jndiname", "");	
		String driver = null;
		String user = null;
		String password = null;
		DataSource ds = null;
		Connection conn = null;
		DbConnectionInfo dbInfo = null;
		
		//當  tableName 為空字串，表示使用預設值
		if (StrUtil.isEmpty(tableName))
		{
		    if (info.get("_default") == null)
		    {
		        jndi = prop.getProperty("default.db.jndiname", "");
		        driver = prop.getProperty("default.db.driver", "");
		        url = prop.getProperty("default.db.url", "");
		        user = prop.getProperty("default.db.user", "");
		        password = prop.getProperty("default.db.password", "");	
		        dbInfo = setParam(jndi, driver, url, user, password, ds, conn);
		        info.put("_default", dbInfo);
		    }
		    else
		    {
		        dbInfo = info.get("_default");					
		    }
		}
		else
		{
		    if (info.get(tableName) == null)
		    {
		        jndi = prop.getProperty(tableName + ".db.jndiname", "");
		        driver = prop.getProperty(tableName + ".db.driver", "");
		        url = prop.getProperty(tableName + ".db.url", "");
		        user = prop.getProperty(tableName + ".db.user", "");
		        password = prop.getProperty(tableName + ".db.password", "");
		        dbInfo = setParam(jndi, driver, url, user, password, ds, conn);
		        info.put(tableName, dbInfo);
		    }
		    else
		    {
		        dbInfo = info.get(tableName);					
		    }
		}
		
		return dbInfo;
	}//getDbConnectionInfo
	
	
	public static Connection getPooledDbConnection(String tableName) throws SQLException
	{
		DbConnectionInfo dbInfo = null;
		dbInfo = getPooledDbConnectionInfo(tableName);	
		return dbInfo.getDataSource().getConnection();
	}
	
	public static Connection getJndiDbConnection(String tableName) throws NamingException, SQLException, ClassNotFoundException
	{
		DbConnectionInfo dbInfo = null;
		dbInfo = getDbConnectionInfo(tableName);	
		if (dbInfo.isUseJndi())
		{
			if (dbInfo.getDataSource() == null)
			{
				Context ctx = new InitialContext();
				DataSource ds = (DataSource) ctx.lookup(dbInfo.getJndiName());				
				dbInfo.setDataSource(ds);
			}
			return dbInfo.getDataSource().getConnection();								
		}
		return null;
	}
	
	public static DbConnectionInfo getJdbcDbConnection(String tableName) throws SQLException, ClassNotFoundException
	{
		DbConnectionInfo dbInfo = getDbConnectionInfo(tableName);
		Connection conn = null;
		Class.forName(dbInfo.getJdbcDriver());
		conn = DriverManager.getConnection(
				dbInfo.getJdbcUrl(),
				dbInfo.getDbUser(),
				dbInfo.getDbPassword()
				);
		dbInfo.setConnection(conn);
		return dbInfo;
	}//getJdbcDbConnection
	
	
	
	public static DbConnectionInfo getPooledDbConnectionInfo(String tableName)
	{
		DataSource ds = null;
		DbConnectionInfo dbInfo = null;		
		
		//以預設值來處理
		tableName = "";
		dbInfo = getDbConnectionInfo(tableName);
		if (StrUtil.isEmpty(tableName))
		{
		    if (dbInfo.getDataSource() == null)
		    {
		        ds = new ComboPooledDataSource();										
		        dbInfo.setDataSource(ds);

		        ComboPooledDataSource dump = (ComboPooledDataSource) ds;
		        log.debug(String.format("getMinPoolSize=%d", dump.getMinPoolSize()));
		        log.debug(String.format("getMaxPoolSize=%d", dump.getMaxPoolSize()));
		        log.debug(String.format("getMaxStatements=%d", dump.getMaxStatements()));
		        log.debug(String.format("getMaxStatementsPerConnection=%d", dump.getMaxStatementsPerConnection()));	
		    }
		}
		else
		{
		    //若是 tableName 未設定，則會自動使用 default pool
		    if (dbInfo.getDataSource() == null)
		    {
		        ds = new ComboPooledDataSource(tableName);					
		        dbInfo.setDataSource(ds);	

		        ComboPooledDataSource dump = (ComboPooledDataSource) ds;
		        log.debug(String.format("getMinPoolSize=%d", dump.getMinPoolSize()));
		        log.debug(String.format("getMaxPoolSize=%d", dump.getMaxPoolSize()));
		        log.debug(String.format("getMaxStatements=%d", dump.getMaxStatements()));
		        log.debug(String.format("getMaxStatementsPerConnection=%d", dump.getMaxStatementsPerConnection()));
		    }
		}
		
		return dbInfo;
	}//getPooledDbConnectionInfo
	
}
