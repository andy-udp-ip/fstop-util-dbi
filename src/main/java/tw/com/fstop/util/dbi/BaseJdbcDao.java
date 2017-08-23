package tw.com.fstop.util.dbi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.com.fstop.util.dbi.DataConvertMap;
import tw.com.fstop.util.dbi.DbTable;
import tw.com.fstop.util.dbi.DbTableFieldInfo;
import tw.com.fstop.util.dbi.JdbcNamedParameterStatement;
import tw.com.fstop.util.dbi.ObjectConverter;
import tw.com.fstop.util.StrUtil;


/**
 * <pre>
 * JdbcDao 的抽象類別，萬用 DBI，功能非常強大。
 * 用法就是繼承這個類別，然後子類別就基本功能都有了。
 * 支援 JNDI, jdbc 雙模式, 並具備 cache 多組 meta-data 的功能。
 * </pre>
 * @author andy
 *
 */
abstract public class BaseJdbcDao 
{
    //Log4j
	//private final static Logger log = Logger.getLogger(BaseJdbcDao.class.getName());
    //Slf4j
    private final static Logger log = LoggerFactory.getLogger(BaseJdbcDao.class);
	
	static String TABLE_NAME = "";

	static String DEF_JNDI_NAME = "java:comp/env/jdbc/goservice";
	static String DEF_JDBC_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	static String DEF_DB_URL = "jdbc:jtds:sqlserver://192.168.139.130:1433/goservice";
	static String DEF_DB_USER = "goxxxxx";
	static String DEF_DB_PASSWORD = "xxxxxx";
	static Boolean IS_USE_JNDI = false;
	
	static String PRODUCT_NAME_SQLSERVER = "Microsoft SQL Server";
	
	public static final String ENCODING="utf8";
	
	//static Map<String, DbTableFieldInfo> keyFields = new LinkedHashMap<String, DbTableFieldInfo>();
	//static Map<String, DbTableFieldInfo> fields = new HashMap<String, DbTableFieldInfo>();
	static Map<String, DbTable> tables = Collections.synchronizedMap(new LinkedHashMap<String, DbTable>());
	
	void loadTableMetaInfo(Connection connection) throws SQLException
	{		
		//keyFields.clear();
		//fields.clear();		
		tables.get(getTableName()).setName(getTableName());
		tables.get(getTableName()).getKeyFields().clear();
		tables.get(getTableName()).getFields().clear();
		
		DatabaseMetaData dbMetaData = connection.getMetaData();	
	
		tables.get(getTableName()).setProductName(dbMetaData.getDatabaseProductName());
		log.debug("ProductName=" + tables.get(getTableName()).getProductName());
		
		ResultSet result = null;
		result = dbMetaData.getTables(null, null, getTableName(), new String [] {"TABLE"});
		while(result.next())
        {
			String tableName = "";
			tableName = result.getString(3).trim();
			log.debug("metadata table name =" + tableName);
			
			ResultSet result2 =  dbMetaData.getColumns(null, null, tableName, null);
			while(result2.next())
			{
				String columnName =  result2.getString(4).trim();
				String typeName =  result2.getString(6).trim();
				Integer columnSize = result2.getInt(7);
				Integer scale = result2.getInt("DECIMAL_DIGITS");

				Boolean isNullable = false;
	        	if (result2.getInt(11) == 0)
	        	{
	        		isNullable = false;
	        	}
	        	else
	        	{
	        		isNullable = true;        		
	        	}

	        	log.debug(columnName + " " + typeName + " " + columnSize + " " + scale + " " + isNullable);
	        	DbTableFieldInfo fieldInfo = new DbTableFieldInfo(columnName, typeName, columnSize, scale, isNullable);
	        	//fields.put(columnName, fieldInfo);
	        	tables.get(getTableName()).getFields().put(columnName, fieldInfo);
			}
			
			log.debug("load keys");
			result2 = dbMetaData.getPrimaryKeys(null, null, tableName);
			while(result2.next())
			{
				String columnName =  result2.getString(4).trim();
				
				DbTableFieldInfo columnInfo = tables.get(getTableName()).getFields().get(columnName);
				String typeName =  columnInfo.getValueType();
				Integer keySeq = result2.getInt(5);

	        	log.debug(columnName + " " + typeName + " " + keySeq);
	        	DbTableFieldInfo fieldInfo = new DbTableFieldInfo(columnName, typeName, columnInfo.getSize(), columnInfo.getScale(), columnInfo.getNullable());
	        	fieldInfo.setKeySeq(keySeq);
	        	//keyFields.put(columnName, fieldInfo);
	        	tables.get(getTableName()).getKeyFields().put(columnName, fieldInfo);
	        	
	        	tables.get(getTableName()).getFields().get(columnName).setKeySeq(keySeq);
			}
        }//while
	}//loadMetaInfo
	
	public List<String> getTableNameList(Connection connection) throws SQLException
	{
		List<String> ret = null;
		DatabaseMetaData dbMetaData = connection.getMetaData();
		
		ResultSet result = null;
		result = dbMetaData.getTables(null, null, null, new String [] {"TABLE"});

		ret = new ArrayList<String>();
		while(result.next())
		{
			String tableName = result.getString(3).trim();
			ret.add(tableName);
			log.debug("getTableNameList=" + tableName);
		}
		return ret;
	}
	
	
	public Map<String, DbTableFieldInfo> getKeyFields()
	{
		initMetaData();
		//return keyFields;
		return tables.get(getTableName()).getKeyFields();
	}
	
	public Map<String, DbTableFieldInfo> getFields()
	{
		initMetaData();
		//return fields;
		return tables.get(getTableName()).getFields();
	}
	
	/**
	 * 由子類別實作，取得 table 名稱
	 * @return		實際物件代表的 table name
	 */
	protected abstract String getTableName();
	
	
	String schema = "";
	String jdbcDriver = null;
	String jdbcUrl = null;
	String dbUser = null;
	String dbPassword = null;
	Connection dbConnection = null;
	DataSource dataSource = null;
	Boolean useJNDI = false;
	String jndiName = null;
	String queryHint1 = "";
	String queryHint2 = "";	
	String updateHint1 = "";
	String updateHint2 = "";	
	Boolean useHint = true;
	Boolean useCoordinator = true;
	
	public BaseJdbcDao()
	{
//		this.jdbcDriver = DEF_JDBC_DRIVER;
//		this.jndiName = DEF_JNDI_NAME;
//		this.useJNDI = IS_USE_JNDI;
//		this.jdbcUrl = DEF_DB_URL;
//		this.dbUser = DEF_DB_USER;
//		this.dbPassword = DEF_DB_PASSWORD;	

	    //此時 getTableName 不會有值
//		DbConnectionInfo dbInfo = ConnectionCoordinator.getDbConnectionInfo(getTableName());
//		this.jdbcDriver = dbInfo.getJdbcDriver();
//		this.jndiName = dbInfo.getJndiName();
//		this.dbUser = dbInfo.getDbUser();
//		this.dbPassword = dbInfo.getDbPassword();
//		if (this.jndiName.isEmpty())
//		{
//			this.useJNDI = false;
//		}
//		else
//		{
//			this.useJNDI = true;			
//		}
	}
	
	public BaseJdbcDao(String jdbcDriver, String jdbcUrl, String dbUser, String dbPassword)
	{
		this.jdbcDriver = jdbcDriver;
		this.jdbcUrl = jdbcUrl;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.useJNDI = false;
		this.useCoordinator = false;
	}
	
	/**
	 * 為什麼不用只有一個參數的方法？例如 BaseJdbcDao(String jndiName)
	 * 因為這個方法先留給子類別實作 xxxDao(String tableName)讓子類別好用一點
	 * @param useJNDI
	 * @param jndiName
	 */
	public BaseJdbcDao(Boolean useJNDI, String jndiName)
	{
		this.jndiName = jndiName;
		this.useJNDI = useJNDI;
		this.useJNDI = true;
		this.useCoordinator = false;
	}
	
	public void setHint()
	{
		clearHint();
		if (tables.get(getTableName()) != null && useHint == true)
		{
			String productName = tables.get(getTableName()).getProductName();
			if (productName.equalsIgnoreCase(PRODUCT_NAME_SQLSERVER))
			{
				queryHint1 = "";
				queryHint2 = " with (nolock) ";
				updateHint1 = "";
				updateHint2 = " with (rowlock) ";
			}
		}
	}
	public void clearHint()
	{
		queryHint1 = "";
		queryHint2 = "";
		updateHint1 = "";
		updateHint2 = "";		
	}
	
	void initMetaData()
	{
		//若是不存在則先新增
		if (tables.get(getTableName()) == null)
		{
		    log.debug("loading table info...");
			Map<String, DbTableFieldInfo> fields = Collections.synchronizedMap(new LinkedHashMap<String, DbTableFieldInfo>());
			Map<String, DbTableFieldInfo> keyFields = Collections.synchronizedMap(new HashMap<String, DbTableFieldInfo>());
			DbTable table = new DbTable();
			table.setFields(fields);
			table.setKeyFields(keyFields);
			tables.put(getTableName(), table);			
		}
		
		//table 不一定有 key 但一定要有欄位
		//如果欄位 cache 為空，則載入
		//if (fields.isEmpty())
		if (tables.get(getTableName()).getFields().isEmpty())
		{
			try 
			{
				this.getDbConnection();
				loadTableMetaInfo(dbConnection);
				this.closeConnection();		
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}					
		}
	}
	
	public Connection getDbConnection(boolean useJNDI, 
									String jndiName, 
									String jdbcDriver,
									String dbUrl,
									String dbUser,
									String dbPassword
									) 
	{
		try
		{
			if (dbConnection == null || dbConnection.isClosed())
			{				
				if (useJNDI)
				{
					log.debug("use jndi data source");
					Context ctx = new InitialContext();
					dataSource = (DataSource) ctx.lookup(jndiName);				
					dbConnection = dataSource.getConnection();
					
					for( SQLWarning warn = dbConnection.getWarnings(); warn != null; warn = warn.getNextWarning() )
			        {
						log.warn( "SQL Warning:" ) ;
						log.warn( "State  : " + warn.getSQLState()  ) ;
						log.warn( "Message: " + warn.getMessage()   ) ;
						log.warn( "Error  : " + warn.getErrorCode() ) ;
			        }					
				}//if useJNDI
				else
				{
					log.debug("use jdbc driver");
					Class.forName(jdbcDriver);
					dbConnection = DriverManager.getConnection(
							dbUrl,
							dbUser,
							dbPassword
							);		
				}
				
			}//if invalid dbConnection
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.error(e);
			log.error(e.getMessage(), e);
		}
		
		return dbConnection;
	}//getDbConnection
	
	public void closeConnection()
	{
		try
		{			
			if(dbConnection != null)
			{
				//如果有設定 transaction 則不自動 close
				//必需在 commit 或 rollback 之後 setAutoCommit(true)
				//再呼叫 closeConnection 才可以
				if (dbConnection.getAutoCommit() == false)
				{
					log.debug("auto commit=" + dbConnection.getAutoCommit());
					return;
				}
				
				dbConnection.close();
				dbConnection = null;
			}
		}
		catch(Exception e)
		{			
		}
		
	}//closeConnection
	
	void printSQLException(SQLException se)
	{
		// Loop through the SQL Exceptions
		while( se != null )
	    {
	        log.error( "State  : " + se.getSQLState());
	        log.error( "Message: " + se.getMessage());
	        log.error( "Error  : " + se.getErrorCode());
	        se = se.getNextException();
	    }
	}

	/**
	 * 組成簡單的只有等於的 where 條件，且map key與 欄位名稱 必需相同
	 * 不考慮 is null 的情形
	 * @param map
	 * @return
	 */
    String getEqualCondition(Map<String, Object> map)
	{
		String ret = "";
		
		if (map == null || map.isEmpty())
		{
			return ret;
		}
		
		ret = " where ";
		Set<String> ks = map.keySet();
		int cnt = 0;
		for(String key : ks)
		{
			cnt++;
			if (cnt < ks.size())
			{
				ret = ret + key + " = :" + key + " and "; 				
			}
			else
			{
				ret = ret + key + " = :" + key + " "; 								
			}
		}
		
		return ret;
	}

    /**
     * 產生以主鍵值為主的 where 條件
     * @return
     */
    String getEqualByKeyCondition()
    {
    	Set<String> set = getKeyFields().keySet();
    	if (set.isEmpty()) return "";
    	
    	String ret = " where ";
    	int cnt = 0;
    	for(String key : set)
    	{
    		cnt++;
    		if (cnt < set.size())
    		{
    			ret = ret + key + " = :" + key + " and ";     			
    		}
    		else
    		{
    			ret = ret + key + " = :" + key + " ";     			    			
    		}
    	}

    	return ret;
    }

    /**
     * 產生無條件的 update SQL 語法
     * @param map
     * @return
     */
    String getUpdateStatement(Map<String, Object> map)
	{
		String ret = "";
		
		if (map == null || map.isEmpty())
		{
			return ret;
		}
		setHint();		
		ret = " update " + updateHint1 + getTableName() + updateHint2 + " set ";
		Set<String> ks = map.keySet();
		for(String key : ks)
		{
			//避開 key 欄位
			if (getKeyFields().containsKey(key))
			{
				continue;
			}
			
			//因為 hashmap 出現位置不固定，所以無法正確判斷，只能後置處理
			//且 update 有既有欄位 與 條件欄位 
			if (getFields().containsKey(key))
			{
				ret = ret + key + " = :" + key + ", "; 								
			}
			
		}

		ret = ret.substring(0, ret.lastIndexOf(",")) + " ";
		
		return ret;
	}
    
    /**
     * 產生以主鍵為主要條件的 update SQL
     * @param map
     * @return
     */
    String getUpdateByKeyStatement(Map<String, Object> map)
	{
		String ret = "";
		
		if (map == null || map.isEmpty())
		{
			return ret;
		}
		
		
		//有可能沒有 key
		String cond = getEqualByKeyCondition();
		ret = this.getUpdateStatement(map);
		if (StrUtil.isNotEmpty(cond))
		{
			ret = ret + cond; //getEqualByKeyCondition(); 			
		}
		else
		{
			ret = ret + this.getEqualCondition(map);
		}
		
		return ret;
	}

    /**
     * 產生 insert SQL 語法
     * @return
     */
    String getInsertStatement()
    {
		String cols = "insert into " + getTableName() + " ( ";
		String ret = " values (";
		Set<String> ks = getFields().keySet();
		int cnt = 0;
		for(String key : ks)
		{
			cnt++;
			if (cnt < ks.size())
			{
				cols = cols + key + ", ";
				ret = ret + ":" + key + ", "; 				
			}
			else
			{
				cols = cols + key + " ";
				ret = ret + ":" + key + " "; 								
			}
		}
		cols = cols + ") ";
		ret = ret + ") ";
		
		ret = cols + ret;
		return ret;    	
    }
    
    /**
     * 依照給定的 map 內容產生 insert SQL 語法
     * @param map
     * @return
     */
    String getInsertStatement(Map<String, Object> map)
	{
		String ret = "";
		
		if (map == null || map.isEmpty())
		{
			return ret;
		}
		
		String fields = "insert into " + getTableName() + " ( ";
		ret = " values (";
		Set<String> ks = map.keySet();
		int cnt = 0;
		for(String key : ks)
		{
			cnt++;
			if (cnt < ks.size())
			{
				fields = fields + key + ", ";
				ret = ret + ":" + key + ", "; 				
			}
			else
			{
				fields = fields + key + " ";
				ret = ret + ":" + key + " "; 								
			}
		}
		fields = fields + ") ";
		ret = ret + ") ";
		
		ret = fields + ret;
		return ret;
	}
    
    void setParam(JdbcNamedParameterStatement stmt, Map<String, Object> map) throws SQLException
    {
    	if (map == null || map.isEmpty()) return;
    	
		Set<String> ks = map.keySet();
		for(String key : ks)
		{
			stmt.setObject(key, map.get(key)); 
		}    	
    }
    
    void setKeyParam(JdbcNamedParameterStatement stmt, Map<String, Object> map) throws SQLException
    {
    	if (map == null || map.isEmpty()) return;
    	
		Set<String> ks = getKeyFields().keySet();
		for(String key : ks)
		{
			stmt.setObject(key, map.get(key)); 
		}    	
    }

    /**
     * 以主鍵值搜尋，輸入的 map 中必需有主鍵的欄位
     * @param map
     * @return
     */
	public Map<String, Object> findByKey(Map<String, Object> map)
	{
		ResultSet rs = null;
		JdbcNamedParameterStatement stmt = null;
		try
		{
			String cond = getEqualByKeyCondition();			
			setHint();
			String sql = "select " + queryHint1 + " * from " + getTableName() + queryHint2;
			//因為 table 有可能沒有 key
			if (StrUtil.isNotEmpty(cond))
			{
				sql = sql + cond;
			}
			else
			{
				sql = sql + getEqualCondition(map);
			}
			
			log.debug("findByKey=" + sql);
			System.out.println(sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			
			if (StrUtil.isNotEmpty(cond))
			{
				setKeyParam(stmt, map);				
			}
			else
			{
				setParam(stmt, map);
			}
						
			rs=stmt.executeQuery();
			
			MapListHandler handler = new MapListHandler();
			List<Map<String, Object>> list = handler.handle(rs);
			DataConvertMap<String, Object> dcMap = new DataConvertMap<String, Object>();
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			
			if (list != null && list.size() > 0)
			{
				dcMap.copy(list.get(0));
				return dcMap;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (rs != null) 
			{
			    try { rs.close(); } catch (SQLException e) { ; }
			    rs = null;
			}
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();
		}
		return null;
	}//findByKey
    
	/**
	 * 依條件搜尋
	 * <pre>
	 * Find by custom condition, 其中 map 只有 organizaiton_id 的值   
	 *   dao.find(" where organization_id=:organization_id ", map);
	 * </pre>
	 * @param where
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> find(String where, Map<String, Object> map)
	{		
		ResultSet rs = null;
		JdbcNamedParameterStatement stmt = null;
		try
		{
			//若是未輸入 where 條件，則再判斷 map 是否可以組條件
			if (StrUtil.isEmpty(where))
			{
				where = getEqualCondition(map);
			}
			setHint();
			String sql = "select " + queryHint1 + " * from " + getTableName() + queryHint2 + where;
			log.debug("find=" + sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			setParam(stmt, map);
			rs=stmt.executeQuery();
			
			//MapListHandler handler = new MapListHandler();
			//return handler.handle(rs);
			List<Map<String, Object>> list =  makeResultList(rs);
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			
			return list;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (rs != null) 
			{
			    try { rs.close(); } catch (SQLException e) { ; }
			    rs = null;
			}
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();
		}
		return null;
	}//find
    
	/**
	 * 採用使用者自行輸入的 SQL 做為查詢依據
	 * @param sql
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> findBySQL(String sql, Map<String, Object> map)
	{		
		ResultSet rs = null;
		JdbcNamedParameterStatement stmt = null;
		try
		{
			log.debug("findBySQL=" + sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			setParam(stmt, map);
			rs=stmt.executeQuery();
			
			//MapListHandler handler = new MapListHandler();
			//List<Map<String, Object>> list = handler.handle(rs);
			List<Map<String, Object>> list =  makeResultList(rs);
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;

			return list;			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (rs != null) 
			{
			    try { rs.close(); } catch (SQLException e) { ; }
			    rs = null;
			}
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();
		}
		return null;
	}//findBySQL
	
	List<Map<String, Object>> makeResultList(ResultSet rs) throws SQLException
	{
		MapListHandler handler = new MapListHandler();
		List<Map<String, Object>> list = handler.handle(rs);
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>(); 
		for(Map<String, Object> m : list)
		{
			DataConvertMap<String, Object> dcMap = new DataConvertMap<String, Object>();
			dcMap.copy(m);
			listData.add(dcMap);
		}
		
		return listData;		
	}
	
	/**
	 * 取得資料庫總筆數
	 * @return
	 */
	public long getRecordCount(String where, Map<String, Object> map)
	{
		ResultSet rs = null;
		JdbcNamedParameterStatement stmt = null;
		long ret = 0;
		try
		{
			//若是未輸入 where 條件，則再判斷 map 是否可以組條件
			if (StrUtil.isEmpty(where))
			{
				where = getEqualCondition(map);
			}
			setHint();
						
			String sql = "select " + queryHint1 + " count(*) from " + getTableName() + queryHint2 + where;

			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			setParam(stmt, map);
			rs=stmt.executeQuery();
			
			ScalarHandler<Long> scalar = new ScalarHandler<Long>();
			ret = ObjectConverter.convert(scalar.handle(rs), Long.class);
			
			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (rs != null) 
			{
			    try { rs.close(); } catch (SQLException e) { ; }
			    rs = null;
			}
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();			
		}
		
		return ret;
	}  //getRecordCount
	
	/**
	 * 若沒有 key，則與 insert 相同
	 * @param map
	 * @return
	 */
	public int insertKey(Map<String, Object> map)
	{
		int ret = 0;
		JdbcNamedParameterStatement stmt = null;
		try
		{
			String sql = "";

			Map<String, Object> keyMap = new HashMap<String, Object>();
			Set<String> set = getKeyFields().keySet();
			for(String k : set)
			{
				keyMap.put(k, k);
			}
			
			if (set.isEmpty())
			{
				sql = getInsertStatement(map);
			}
			else
			{
				sql = getInsertStatement(keyMap);				
			}
			
			log.debug("insertKey=" + sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			if (set.isEmpty())
			{
				setParam(stmt, map);								
			}
			else
			{
				setKeyParam(stmt, map);				
			}
			
			ret = stmt.executeUpdate();
			
			stmt.close();
			stmt = null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();			
		}
		
		return ret;
	}//insertKey
	
	/**
	 * insert into 
	 * @param map
	 * @return int 1為成功 ,0為失敗
	 */
	public int insert(Map<String, Object> map)
	{
		int ret = 0;
		JdbcNamedParameterStatement stmt = null;
		try
		{			
			String sql = getInsertStatement(map);
			log.debug("insert=" + sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			setParam(stmt, map);
			ret = stmt.executeUpdate();
			stmt.close();
			stmt = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();
		}
		return ret;				
	} //insert

	/**
	 * insert into 
	 * 若 Map 欄位不足則會失敗
	 * @param map
	 * @return int 1為成功 ,0為失敗
	 */
	public int insertByAllColumn(Map<String, Object> map)
	{
		int ret = 0;
		JdbcNamedParameterStatement stmt = null;
		try
		{			
			String sql = getInsertStatement();
			log.debug("insert=" + sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			setParam(stmt, map);
			ret = stmt.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();
		}
		return ret;				
	} //insertByAllColumn
	
	/**
	 * 更新資料
	 * <pre>
	 * Update by key, map 中必需有主鍵值欄位
	 *   dao.update(null, map);
	 * Update by custom condition, 其中 unload_time 放置新的值, unload_time_old 放置舊的值
	 * 也就是資料庫欄位名稱放置的均為新的值
	 *   map.put("unload_time", 110);  
	 *   map.put("unload_time_old", 120);
	 *   dao.update(" where unload_time=:unload_time_old ", map);
	 * </pre>
	 * @param where
	 * @param map
	 * @return
	 */
	public int update(String where, Map<String, Object> map)
	{
		int ret = 0;
		JdbcNamedParameterStatement stmt = null;
		try
		{
			String sql = "";
			//若是未輸入 where 條件，則再判斷 map 是否可以組條件
			if (StrUtil.isEmpty(where))
			{
				sql = getUpdateByKeyStatement(map);
			}
			else
			{
				sql = getUpdateStatement(map) + where;
			}

			log.debug("update=" + sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);			
			setParam(stmt, map);
			ret = stmt.executeUpdate();
			stmt.close();
			stmt = null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();
		}
		return ret;		
	}//update
	
	/**
	 * 當資料不存在時新增一筆，若存在時則更新
	 * @param map
	 * @return
	 */
	public int save(Map<String, Object> map)
	{
		Map<String, Object> m = findByKey(map);
		if (m != null && m.isEmpty() == false)
		{
			log.debug("save for update....");
			return update(null, map);
		}
		else if (m == null || m.isEmpty())
		{
			log.debug("save for insert....");
			return insert(map);
		}
		return 0;
	}  //save
	
	public int deleteByKey(Map<String, Object> map)
	{
		int ret = 0;
		JdbcNamedParameterStatement stmt = null;
		try
		{
			String cond = getEqualByKeyCondition();
			String sql = null;
			if (StrUtil.isNotEmpty(cond))
			{
				sql = "delete from " + getTableName() + cond;				
			}
			else
			{
				//既然指定 delete by key ，又沒有key和條件，防止誤刪
				if (map.isEmpty())
				{
					log.error("deleteByKey with empty map!!");
					return ret;
				}
				sql = "delete from " + getTableName() + getEqualCondition(map);								
			}
			log.debug("deleteByKey=" + sql);
			stmt = new JdbcNamedParameterStatement(getDbConnection(), sql);
			if (StrUtil.isNotEmpty(cond))
			{
				setKeyParam(stmt, map);
			}
			else
			{
				setParam(stmt, map);
			}
			
			ret = stmt.executeUpdate();	
			stmt.close();
			stmt = null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//log.debug(e);
	         log.error(e.getMessage(), e);

		}
		finally
		{
			if (stmt != null) 
			{
			    try { stmt.close(); } catch (SQLException e) { ; }
			    stmt = null;
			}
			closeConnection();
		}
		return ret;
	}  //deleteByKey
	
	public boolean isKeyExist(Map<String, Object> map)
	{
		Map<String, Object> m = findByKey(map);
		if (m == null || m.isEmpty())
		{
			return false;
		}
		return true;
	}
	
	
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String dbUrl) {
		this.jdbcUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPasswrod() {
		return dbPassword;
	}

	public void setDbPassword(String dbPass) {
		this.dbPassword = dbPass;
	}
	
	public Boolean getUseJNDI() {
		return useJNDI;
	}

	public void setUseJNDI(Boolean useJNDI) {
		this.useJNDI = useJNDI;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public Connection getDbConnection() 
	{
		System.out.println("getTableName=" + getTableName() + " use coordinator=" + this.useCoordinator);

		//TODO 在此處設定 jdbc 連線資訊，就不用在子類別個設定
		if (this.useCoordinator == false)
        {   
	        return getDbConnection(this.useJNDI, this.jndiName, this.jdbcDriver, this.jdbcUrl, this.dbUser, this.dbPassword);		    
        }
		
		//使用 ConnectionCoordinator的方式 
        DbConnectionInfo dbInfo = null;
        try 
        {
            if (StrUtil.isEmpty(this.dbUser))
            {
                dbInfo = ConnectionCoordinator.getPooledDbConnectionInfo(getTableName());
                //dbInfo = ConnectionCoordinator.getDbConnectionInfo(getTableName());
                this.jdbcDriver = dbInfo.getJdbcDriver();
                this.jndiName = dbInfo.getJndiName();
                this.jdbcUrl = dbInfo.getJdbcUrl();
                this.dbUser = dbInfo.getDbUser();
                this.dbPassword = dbInfo.getDbPassword();
                this.useJNDI = dbInfo.isUseJndi();              
            }
            
            if (dbConnection == null || dbConnection.isClosed())
            {
                dbConnection = ConnectionCoordinator.getPooledDbConnection(getTableName());             
                //dbConnection = ConnectionCoordinator.getJndiDbConnection(getTableName());             
            }
            
            return dbConnection;
        } 
        catch (Exception e) 
        {
            log.error(e.getMessage(), e);
        }
		//--
        
		return null;
	}

	public void setDbConnection(Connection dbConn) {
		this.dbConnection = dbConn;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public Boolean getUseHint() {
		return useHint;
	}

	public void setUseHint(Boolean useHint) {
		this.useHint = useHint;
	}
	
	
}
