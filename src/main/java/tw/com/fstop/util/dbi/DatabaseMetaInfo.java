package tw.com.fstop.util.dbi;

import java.util.List;


public class DatabaseMetaInfo 
{
	public enum InfoType
	{
		Root,
		Database,
		Catalog,
		Schema,
		Table,
		Column,
		Key
	}
	
	private InfoType infoType;
	private List<DatabaseMetaInfo> dataList = null;
	private List<DatabaseMetaInfo> keyList = null;  //只有 Table 才會有
	private String jdbcUrl;
	private String jdbcDriverClass;
	private String dbUserName;
	private String dbPassword;
    private String name;
    private Object value = null;
    private String valueType;
    private int size = 0;
    private int precision;
    private int scale;
    private boolean nullable = true;
    
    private String keyName;
    private int  keySeq;
    
    public DatabaseMetaInfo()
    {
    	
    }
    
    public DatabaseMetaInfo(String driver, String url, String user, String password)
    {
    	this.jdbcDriverClass = driver;
    	this.jdbcUrl = url;
    	this.dbUserName = user;
    	this.dbPassword = password;
    }
    
	public InfoType getInfoType() {
		return infoType;
	}
	public void setInfoType(InfoType infoType) {
		this.infoType = infoType;
	}
	public List<DatabaseMetaInfo> getDataList() {
		return dataList;
	}
	public void setDataList(List<DatabaseMetaInfo> dataList) {
		this.dataList = dataList;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getJdbcDriverClass() {
		return jdbcDriverClass;
	}
	public void setJdbcDriverClass(String jdbcDriverClass) {
		this.jdbcDriverClass = jdbcDriverClass;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean isNullable) {
		this.nullable = isNullable;
	}

	public List<DatabaseMetaInfo> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<DatabaseMetaInfo> keyList) {
		this.keyList = keyList;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public int getKeySeq() {
		return keySeq;
	}

	public void setKeySeq(int keySeq) {
		this.keySeq = keySeq;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	
	
}
