/*
 * Copyright (c) 2017, FSTOP, Inc. All Rights Reserved.
 *
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import tw.com.fstop.util.PropUtil;


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
	static final String DB_SETTING_PROP = "dbi.properties";
	
	static Properties prop = null;
	static DataSourceService service = null;
	
	static 
	{
	    try
        {
            prop =PropUtil.loadProperties(DB_SETTING_PROP);
            
            //initialize data source service 
            service = DataSourceServiceImpl.getInstance();
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
            
        }
	}
	
	//index DbConnectionInfo by dbName
	static Map<String, DbConnectionInfo> info = Collections.synchronizedMap(new HashMap<String, DbConnectionInfo>());
	
	public static DbConnectionInfo setParam(String dbName,
	                                        String jndi, 
											String url, 
											String driver, 
											String user, 
											String password,
											String poolName,
											DataSource ds,
											Connection connection
											)
	{
		DbConnectionInfo dbInfo = new DbConnectionInfo();
		dbInfo.setDbName(dbName);
		dbInfo.setDbPassword(password);
		dbInfo.setDbUser(user);
		dbInfo.setJdbcDriver(driver);
		dbInfo.setJdbcUrl(url);
		dbInfo.setJndiName(jndi);
		dbInfo.setPoolName(poolName);
		dbInfo.setDataSource(ds);
		dbInfo.setConnection(connection);
		return dbInfo;
	}
	
	
	public static DbConnectionInfo getDbConnectionInfo(String dbName, String tableName)
	{
		String url = null; 
		String jndi = null;	
		String driver = null;
		String user = null;
		String password = null;
		String poolName = null;
		DataSource ds = null;
		Connection conn = null;
		DbConnectionInfo dbInfo = null;
		
        if (info.get(dbName) == null)
        {
            jndi = prop.getProperty(dbName + ".db.jndiname", "");
            driver = prop.getProperty(dbName + ".db.driver", "");
            url = prop.getProperty(dbName + ".db.url", "");
            user = prop.getProperty(dbName + ".db.user", "");
            password = prop.getProperty(dbName + ".db.password", "");
            poolName = prop.getProperty(dbName + ".db.pool", "");
            dbInfo = setParam(dbName, jndi, driver, url, user, password, poolName, ds, conn);

            info.put(dbName, dbInfo);
        }
        else
        {
            dbInfo = info.get(dbName);
        }
		
		return dbInfo;
	}//getDbConnectionInfo
	
	
	public static Connection getPooledDbConnection(String dbName, String tableName) throws SQLException
	{
		DbConnectionInfo dbInfo = null;
		dbInfo = getPooledDbConnectionInfo(dbName, tableName);	
		return dbInfo.getDataSource().getConnection();
	}
	
	public static Connection getJndiDbConnection(String dbName, String tableName) throws NamingException, SQLException, ClassNotFoundException
	{
		DbConnectionInfo dbInfo = null;
		dbInfo = getDbConnectionInfo(dbName, tableName);	
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
	
	public static DbConnectionInfo getJdbcDbConnection(String dbName, String tableName) throws SQLException, ClassNotFoundException
	{
		DbConnectionInfo dbInfo = getDbConnectionInfo(dbName, tableName);
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
	
	
	
	public static DbConnectionInfo getPooledDbConnectionInfo(String dbName, String tableName)
	{
		DataSource ds = null;
		DbConnectionInfo dbInfo = null;		
		
		dbInfo = getDbConnectionInfo(dbName, tableName);
		
		/* for JDK 1.5 and blow
        if (dbInfo.getDataSource() == null)
        {
            String poolName = dbInfo.getPoolName();
            if ("c3p0".equalsIgnoreCase(poolName))
            {
                ds = getC3p0DataSource(dbInfo);
                dbInfo.setDataSource(ds);
            }
            else if ("hikari".equalsIgnoreCase(poolName))
            {
                ds = getHikariDataSource(dbInfo);
                dbInfo.setDataSource(ds);
            }
        }
        */
		
		// for JDK 1.6 and above
		if (dbInfo.getDataSource() == null)
		{
		    String poolName = dbInfo.getPoolName();
		    ds = service.getDataSource(poolName, dbName);
		    dbInfo.setDataSource(ds);
		}
		

		return dbInfo;
	}//getPooledDbConnectionInfo
	
	static DataSource getC3p0DataSource(DbConnectionInfo dbInfo)
	{
	    DataSource ds = null;

	    // ds = new ComboPooledDataSource(); //c3p0 use default setting
        ds = new ComboPooledDataSource(dbInfo.getDbName());

        ComboPooledDataSource dump = (ComboPooledDataSource) ds;
        log.debug(String.format("getMinPoolSize=%d", dump.getMinPoolSize()));
        log.debug(String.format("getMaxPoolSize=%d", dump.getMaxPoolSize()));
        log.debug(String.format("getMaxStatements=%d", dump.getMaxStatements()));
        log.debug(String.format("getMaxStatementsPerConnection=%d", dump.getMaxStatementsPerConnection()));                

        return ds;
	}
	
	static DataSource getHikariDataSource(DbConnectionInfo dbInfo)
	{
        DataSource ds = null;
        //Hikari config 以 dbName.hikari.properties 來命名
        String cfg = "/" + dbInfo.getDbName() + ".hikari.properties";

        HikariConfig config = new HikariConfig(cfg);
//        config.setMaximumPoolSize(10);
//        config.setDataSourceClassName(dbInfo.jdbcDriver);
//        config.setJdbcUrl(dbInfo.jdbcUrl);
//        config.addDataSourceProperty("user", dbInfo.getDbUser());
//        config.addDataSourceProperty("password", dbInfo.getDbPassword());

        ds = new HikariDataSource(config);  //pass in HikariConfig to HikariDataSource
        HikariDataSource dump = (HikariDataSource) ds;
        log.debug(String.format("getMaximumPoolSize=%d", dump.getMaximumPoolSize()));
        log.debug(String.format("getMinimumIdle=%d", dump.getMinimumIdle()));
        log.debug(String.format("getIdleTimeout=%d", dump.getIdleTimeout()));
        log.debug(String.format("getMaxLifetime=%d", dump.getMaxLifetime()));                

        return ds;
	}
	
	
	
}
