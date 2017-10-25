
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

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * C3p0 connection pool DataSource provider.
 * 
 * C3p0 config by c3p0-config.xml
 *
 * @since 1.0.1
 */
public class C3p0DataSourceProvider implements DataSourceProvider
{
    private static Logger log = LoggerFactory.getLogger(C3p0DataSourceProvider.class);
    
    static final String PROVIDER_NAME = "C3p0";

    @Override
    public String getProviderName()
    {
        return PROVIDER_NAME;
    }

    @Override
    public DataSource getDataSource(String dbName)
    {
        DataSource ds = null;

        // ds = new ComboPooledDataSource(); //c3p0 use default setting
        ds = new ComboPooledDataSource(dbName);

        ComboPooledDataSource dump = (ComboPooledDataSource) ds;
        log.debug(String.format("getMinPoolSize=%d", dump.getMinPoolSize()));
        log.debug(String.format("getMaxPoolSize=%d", dump.getMaxPoolSize()));
        log.debug(String.format("getMaxStatements=%d", dump.getMaxStatements()));
        log.debug(String.format("getMaxStatementsPerConnection=%d", dump.getMaxStatementsPerConnection()));                

        return ds;
    }

}
