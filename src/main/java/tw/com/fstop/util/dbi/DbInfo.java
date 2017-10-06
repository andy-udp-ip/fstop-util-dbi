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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 目前在測試階段，未實際使用
 * 取得資料庫的表格資訊
 * @author Administrator
 *
 */
public class DbInfo 
{
	public static void main(String [] args)
	{
		String url = "jdbc:mysql://127.0.0.1:3306/goservice";
		String user = "goservice";
		String pass = "25082201";
		String driver = "com.mysql.jdbc.Driver";
        DatabaseMetaInfo info = getDbInfo(driver, url, user, pass);
        displayMetaInfo(info, 0);		
	}
	
	public static void displayMetaInfo(DatabaseMetaInfo info, int level)
	{
		if (info == null)
		{
			return;
		}
		
		DatabaseMetaInfo.InfoType infoType = info.getInfoType();
		
		String indent = "";

		for(int i=0; i < level; i++)
		{
			indent += "-";
		}

		if (infoType != DatabaseMetaInfo.InfoType.Column &&
			 infoType != DatabaseMetaInfo.InfoType.Key	
			)
		{
			System.out.println(indent + info.getName());
			
			if (infoType == DatabaseMetaInfo.InfoType.Table)
			{
				for(DatabaseMetaInfo f : info.getKeyList())
				{
					displayMetaInfo(f, level + 1);
				}			
			}
			
			for(DatabaseMetaInfo f : info.getDataList())
			{
				displayMetaInfo(f, level + 1);
			}			
		}
		else
		{
			if (infoType == DatabaseMetaInfo.InfoType.Column)
			{
				System.out.println(indent + info.getName() + " " + info.getValueType() + " " + info.getPrecision() + " " + info.getScale() + " " + info.isNullable());				
			}
			else
			{
				System.out.println(indent + "KEY=" + info.getName() + " " + info.getKeyName() + " " + info.getKeySeq());				
			}
			
		}
	}
	
	public static DatabaseMetaInfo getDbInfo(String jdbcDriver, String jdbcUrl, String user, String password)
	{
		DatabaseMetaInfo dataInfo = null;
		
		try
		{
			Class.forName(jdbcDriver);
			Connection connection = null;
			if (user == null || user.isEmpty() || password == null || password.isEmpty())
			{
				connection=DriverManager.getConnection(jdbcUrl);
			}
			else
			{
				connection=DriverManager.getConnection(jdbcUrl,user,password);				
			}
			
			dataInfo = new DatabaseMetaInfo(jdbcDriver, jdbcUrl, user, password);
			dataInfo.setInfoType(DatabaseMetaInfo.InfoType.Database);
			
			DatabaseMetaData dbMetaData = connection.getMetaData();			
			ResultSet cataLogResult = dbMetaData.getCatalogs();
			List<DatabaseMetaInfo> list = getCatalogList(dbMetaData, cataLogResult);
			dataInfo.setDataList(list);
			
			connection.close();
		}
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return dataInfo;
	}  //getDbInfo
	

	static List<DatabaseMetaInfo> getCatalogList(DatabaseMetaData meta, ResultSet r) throws SQLException 
    {
		List<DatabaseMetaInfo> list = null;
		
		if (r == null)
		{
			return list;
		}
		
        ResultSetMetaData resultMeta = r.getMetaData();
        
        list = new ArrayList<DatabaseMetaInfo>();
        
        boolean foundrec = false;
        int numColumns=resultMeta.getColumnCount();
        
        System.out.println(numColumns);
        String catalogName = "";
        while(r.next())
        {
        	if (r.getString(1) == null)
        	{
        		catalogName = null;
        	}
        	else
        	{
        		catalogName = r.getString(1).trim();
        	}
            if (catalogName != null)
            {
                System.out.print(catalogName); System.out.print(" ");
                DatabaseMetaInfo info = new DatabaseMetaInfo();
                info.setInfoType(DatabaseMetaInfo.InfoType.Catalog);
                info.setName(catalogName);
                
                ResultSet result = meta.getSchemas(catalogName, null);
                if (result == null || result.getFetchSize() == 0)
                {
                    result = meta.getTables(catalogName, null, null, new String [] {"TABLE"});
                    
                    List<DatabaseMetaInfo> tableList = getTableList(meta, catalogName, null, result); 
                    
                    info.setDataList(tableList);
                                        
                    list.add(info);                	
                }
                else
                {
                	// get schema
                	List<DatabaseMetaInfo> schemaList = getSchemaList(meta, catalogName, result);
                    info.setDataList(schemaList);                    
                    list.add(info);                	
                }
                
            }
            else
            {
                System.out.print("NULL"); System.out.print(" ");       
            }
            System.out.println();    
            foundrec = true;
        }       
        if(foundrec==false)
        {
            System.out.println("No records found");
        }   
    	return list;	
    }  //getCatalogList
	
	static List<DatabaseMetaInfo> getSchemaList(DatabaseMetaData meta, String catalogName, ResultSet r) throws SQLException
	{
		List<DatabaseMetaInfo> list = null;
		
		if (r == null)
		{
			return list;
		}
		
        ResultSetMetaData resultMeta = r.getMetaData();
        
        list = new ArrayList<DatabaseMetaInfo>();
        
        int numColumns=resultMeta.getColumnCount();
        
        System.out.println(numColumns);
        String schemaName = "";
        while(r.next())
        {
        	if (r.getString(1) == null)
        	{
        		schemaName = null;
        	}
        	else
        	{
        		schemaName = r.getString(1).trim();
        	}
        	
            if (schemaName != null)
            {
                System.out.print(schemaName); System.out.print(" ");
                DatabaseMetaInfo info = new DatabaseMetaInfo();
                info.setInfoType(DatabaseMetaInfo.InfoType.Schema);
                info.setName(schemaName);
                
                ResultSet  result = meta.getTables(catalogName, schemaName, null, new String [] {"TABLE"});                
                List<DatabaseMetaInfo> tableList = getTableList(meta, catalogName, schemaName, result);                 
                info.setDataList(tableList);                                    
                list.add(info);                	                
            }
            else
            {
                System.out.print("NULL"); System.out.print(" ");       
            }        	
        }
        return list;
	}
	
	static List<DatabaseMetaInfo> getTableList(DatabaseMetaData meta, String catalogName, String schemaName, ResultSet r) throws SQLException
	{
		List<DatabaseMetaInfo> list = null;
		
		if (r == null)
		{
			return list;
		}
		
        ResultSetMetaData resultMeta = r.getMetaData();
        
        list = new ArrayList<DatabaseMetaInfo>();
        
        int numColumns=resultMeta.getColumnCount();
        
        System.out.println(numColumns);
        String tableName = "";
        while(r.next())
        {
        	if (r.getString(3) == null)
        	{
        		tableName = null;
        	}
        	else
        	{
        		tableName = r.getString(3).trim();
        	}
        	
            if (tableName != null)
            {
                System.out.print(tableName); System.out.print(" ");
                DatabaseMetaInfo info = new DatabaseMetaInfo();
                info.setInfoType(DatabaseMetaInfo.InfoType.Table);
                info.setName(tableName);
                
                ResultSet result =  meta.getColumns(catalogName, schemaName, tableName, null);
                
                List<DatabaseMetaInfo> columnList = getColumnList(meta, catalogName, schemaName, result);                 
                info.setDataList(columnList);
                                
                result = meta.getPrimaryKeys(catalogName, schemaName, tableName);
                List<DatabaseMetaInfo> keyList = getKeyList(meta, catalogName, schemaName, result);                 
                info.setKeyList(keyList);
                                
                list.add(info);
            }
            else
            {
                System.out.print("NULL"); System.out.print(" ");       
            }
        }  //while		
		
		return list;
	}

	/*
	 *
			System.out.println(rs.getString("COLUMN_NAME"));
			
			int t = rs.getInt("DATA_TYPE");
			System.out.println(DatabaseInfo.getDataTypes(t));
			
			System.out.println(rs.getInt("COLUMN_SIZE"));
			System.out.println(rs.getInt("DECIMAL_DIGITS"));
			System.out.println(rs.getInt("NULLABLE"));

	 */	
	static List<DatabaseMetaInfo> getColumnList(DatabaseMetaData meta, String catalogName, String schemaName, ResultSet r) throws SQLException
	{
		List<DatabaseMetaInfo> list = null;
		
		if (r == null)
		{
			return list;
		}
		        
        list = new ArrayList<DatabaseMetaInfo>();
                
        String columnName = "";
        String typeName = "";
        int columnSize = 0;
        int scale = 0;
        int precision = 0;
        boolean isNullable = true;
        
        //4, 6, 7, 11
        while(r.next())
        {
        	// Column Name
        	if (r.getString(4) == null)
        	{
        		columnName = null;
        	}
        	else
        	{
        		columnName =  r.getString(4).trim();
        	}

        	// Data Type Name
        	if (r.getString(6) == null)
        	{
        		typeName = null;
        	}
        	else
        	{
        		typeName =  r.getString(6).trim();
        	}
        	
        	columnSize = r.getInt(7);
        	precision = columnSize;
        	scale = r.getInt("DECIMAL_DIGITS");
        	
        	if (r.getInt(11) == 0)
        	{
        		isNullable = false;
        	}
        	else
        	{
        		isNullable = true;        		
        	}
        	
            DatabaseMetaInfo info = new DatabaseMetaInfo();
            info.setInfoType(DatabaseMetaInfo.InfoType.Column);
            info.setName(columnName);
            info.setValueType(typeName);
            info.setSize(columnSize);
            info.setPrecision(precision);
            info.setScale(scale);
            info.setNullable(isNullable);
            
            list.add(info);
        }
        
        
        return list;
	}
	
	static List<DatabaseMetaInfo> getKeyList(DatabaseMetaData meta, String catalogName, String schemaName, ResultSet r) throws SQLException
	{
		List<DatabaseMetaInfo> list = null;
		
		if (r == null)
		{
			return list;
		}
		
        ResultSetMetaData resultMeta = r.getMetaData();
        
        list = new ArrayList<DatabaseMetaInfo>();
        
        int numColumns=resultMeta.getColumnCount();
        
        System.out.println(numColumns);
        String keyName = "";
        String columnName = "";
        int keySeq = 0;
        
        while(r.next())
        {
        	// Column Name
        	if (r.getString(4) == null)
        	{
        		columnName = null;
        	}
        	else
        	{
        		columnName =  r.getString(4).trim();
        	}

        	keySeq = r.getInt(5);
        	
        	// Key Name
        	if (r.getString(6) == null)
        	{
        		keyName = null;
        	}
        	else
        	{
        		keyName =  r.getString(6).trim();
        	}
        	
            DatabaseMetaInfo info = new DatabaseMetaInfo();
            info.setInfoType(DatabaseMetaInfo.InfoType.Key);
            info.setName(columnName);
            info.setKeyName(keyName);
            info.setKeySeq(keySeq);
            
            list.add(info);
        	
        }        
        return list;
    }	

	
	public static DatabaseMetaData getDBMetaData(Connection conn) throws SQLException
	{
		return conn.getMetaData();
	}
	
	public static int getDBMajorVersion(DatabaseMetaData meta) throws SQLException
	{
		return meta.getDatabaseMajorVersion();
	}
	
	public static int getDBMinorVersion(DatabaseMetaData meta) throws SQLException
	{
		return meta.getDatabaseMinorVersion();
	}
	
	public static int getDriverMajorVersion(DatabaseMetaData meta)
	{
		return meta.getDriverMajorVersion();
	}
	
	public static int getDriverMinorVersion(DatabaseMetaData meta)
	{
		return meta.getDriverMinorVersion();
	}
	
	public static int getMaxConnection(DatabaseMetaData meta) throws SQLException
	{
		return meta.getMaxConnections();
	}
	
	public static String getDataTypes(int type)
	{
		String ty = "";
		switch(type)
		{
			case java.sql.Types.CHAR :
				ty = "CHAR";
				break;
				
			case java.sql.Types.BOOLEAN:
				ty = "BOOLEAN";
				break;

			case java.sql.Types.BLOB:
				ty = "BLOB";
				break;

			case java.sql.Types.CLOB:
				ty = "CLOB";
				break;
			case java.sql.Types.DATE:
				ty = "DATE";
				break;
			case java.sql.Types.DECIMAL:
				ty = "DECIMAL";
				break;
			case java.sql.Types.DOUBLE:
				ty = "DOUBLE";
				break;
			case java.sql.Types.FLOAT:
				ty = "FLOAT";
				break;
			case java.sql.Types.INTEGER:
				ty = "INTEGER";
				break;
			case java.sql.Types.NUMERIC:
				ty = "NUMERIC";
				break;
			case java.sql.Types.NCHAR:
				ty = "NCHAR";
				break;
			case java.sql.Types.NVARCHAR:
				ty = "NVARCHAR";
				break;
			case java.sql.Types.REAL:
				ty = "REAL";
				break;
			case java.sql.Types.SMALLINT:
				ty = "SMALLINT";
				break;
			case java.sql.Types.SQLXML:
				ty = "SQLXML";
				break;
			case java.sql.Types.TIME:
				ty = "TIME";
				break;
			case java.sql.Types.TIMESTAMP:
				ty = "TIMESTAMP";
				break;
			case java.sql.Types.TINYINT:
				ty = "TINYINT";
				break;
			case java.sql.Types.VARCHAR:
				ty = "VARCHAR";
				break;
				
			default:
				ty = "";
				break;
		}

		return ty;
	}

}
