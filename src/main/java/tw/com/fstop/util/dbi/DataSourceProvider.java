
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

/**
 * Interface for data source provider (SPI).
 * 
 * @since 1.0.1
 */
public interface DataSourceProvider
{
    /**
     * Get provider name.
     * 
     * @return provider name
     */
    String getProviderName();
    
    /**
     * Get data source.
     * 
     * @param dbName database name
     * @return data source or null for ServiceConfigurationError
     */
    DataSource getDataSource(String dbName);
}
