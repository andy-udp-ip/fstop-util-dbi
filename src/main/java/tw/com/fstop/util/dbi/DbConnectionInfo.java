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
import javax.sql.DataSource;

/**
 * Stores db connection information.
 * If jndiName is setting then useJndi is true.
 *
 * @since 1.0.0
 */
public class DbConnectionInfo 
{
	String jdbcDriver = null;
	String jdbcUrl = null;
	String dbUser = null;
	String dbPassword = null;
	String jndiName = null;
	String dbName = null;
	String poolName = null;
	Boolean useJndi = false;
	DataSource dataSource = null;
	Connection connection = null;
	
	
    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource)
    {
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

    public String getJdbcDriver()
    {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver)
    {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl)
    {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDbUser()
    {
        return dbUser;
    }

    public void setDbUser(String dbUser)
    {
        this.dbUser = dbUser;
    }

    public String getDbPassword()
    {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword)
    {
        this.dbPassword = dbPassword;
    }

    public String getJndiName()
    {
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

    public Boolean isUseJndi()
    {
        return useJndi;
    }

    public void setUseJndi(Boolean useJndi)
    {
        this.useJndi = useJndi;
    }

    public String getDbName()
    {
        return dbName;
    }

    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }

    public String getPoolName()
    {
        return poolName;
    }

    public void setPoolName(String poolName)
    {
        this.poolName = poolName;
    }	
	
	
}
