package tw.com.fstop.util.dbi;

import java.util.Map;

/**
 * Store db table information.
 * 
 * @since 1.0.0
 */
public class DbTable 
{
	String productName;
	String name;
	String dbName;
	Map<String, DbTableFieldInfo> keyFields;
	Map<String, DbTableFieldInfo> fields;
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, DbTableFieldInfo> getKeyFields() {
		return keyFields;
	}
	public void setKeyFields(Map<String, DbTableFieldInfo> keyFields) {
		this.keyFields = keyFields;
	}
	public Map<String, DbTableFieldInfo> getFields() {
		return fields;
	}
	public void setFields(Map<String, DbTableFieldInfo> fields) {
		this.fields = fields;
	}
    public String getDbName()
    {
        return dbName;
    }
    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }
	
	
	
}
