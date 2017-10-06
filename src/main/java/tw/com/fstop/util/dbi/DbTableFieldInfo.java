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

/**
 * Store db table field information.
 *
 * @since 1.0.0
 */
public class DbTableFieldInfo 
{
    private String keyName;   //KEY
    private Integer  keySeq;  //KEY
    private String fieldName; //KEY
    private String valueType;
    private Integer size;
    private Integer precision;
    private Integer scale;
    private Boolean nullable;
    
    public DbTableFieldInfo()
    {    	
    }
    
    public DbTableFieldInfo(Integer keySeq, String keyName, String fieldName)
    {
    	this.keySeq = keySeq;
    	this.keyName = keyName;
    	this.fieldName = fieldName;
    }
    
    public DbTableFieldInfo(String fieldName, String valueType, Integer size, Integer scale, Boolean nullable)
    {
    	this.fieldName = fieldName;
    	this.valueType = valueType;
    	this.size = size;
    	this.precision = size;
    	this.scale = scale;
    	this.nullable = nullable;
    }
    
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Integer getKeySeq() {
		return keySeq;
	}
	public void setKeySeq(Integer keySeq) {
		this.keySeq = keySeq;
	}
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getPrecision() {
		return precision;
	}
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	public Integer getScale() {
		return scale;
	}
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	public Boolean getNullable() {
		return nullable;
	}
	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

}
