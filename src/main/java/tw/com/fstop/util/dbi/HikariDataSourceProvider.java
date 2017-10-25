
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

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Hikari connection pool DataSource provider.
 * 
 * Hikari config by dbName.hikari.properties 
 *
 * @since 1.0.2
 */
public class HikariDataSourceProvider implements DataSourceProvider
{
    private static Logger log = LoggerFactory.getLogger(HikariDataSourceProvider.class);
                      
    static final String PROVIDER_NAME = "Hikari";
    
    @Override
    public String getProviderName()
    {        
        return PROVIDER_NAME;
    }

    @Override
    public DataSource getDataSource(String dbName)
    {
        DataSource ds = null;
        
        //Hikari config 以 dbName.hikari.properties 來命名
        String cfg = "/" + dbName + ".hikari.properties";

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
