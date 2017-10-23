
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

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceServiceImpl implements DataSourceService
{
    private static Logger log = LoggerFactory.getLogger(DataSourceServiceImpl.class);
            
    //for Singleton
    private static DataSourceService service;
    
    //service loader since JDK 1.6
    private ServiceLoader<DataSourceProvider> loader;
    
    private DataSourceServiceImpl()
    {
        loader = ServiceLoader.load(DataSourceProvider.class);
    }

    /**
     * Get DataSourceService instance.
     * 
     * @return DataSourceService
     */
    public static synchronized DataSourceService getInstance()
    {
        if (service == null)
        {
            service = new DataSourceServiceImpl();
        }
        return service;
    }
    
    @Override
    public DataSource getDataSource(String providerName, String dbName)
    {        
        try
        {
            Iterator<DataSourceProvider> providers = loader.iterator();

            String name;
            while (providers.hasNext())
            {
                DataSourceProvider p = providers.next();
                name = p.getProviderName();
                if (providerName.equalsIgnoreCase(name))
                {
                    return p.getDataSource(dbName);
                }
            }
        }
        catch (ServiceConfigurationError serviceError)
        {
            log.error("DataSourceServiceImpl error=", serviceError);
        }
        return null;
    }

    
}
