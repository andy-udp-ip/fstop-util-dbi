
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
 * Interface for data source service.
 *
 * @since 1.0.1
 */
public interface DataSourceService
{
    /**
     * Get data source.
     * 
     * @param providerName data source provider name
     * @param dbName database name
     * @return data source
     */
    DataSource getDataSource(String providerName, String dbName);
}
