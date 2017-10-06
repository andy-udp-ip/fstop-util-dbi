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
