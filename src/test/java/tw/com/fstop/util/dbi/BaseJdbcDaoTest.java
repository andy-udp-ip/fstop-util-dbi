
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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BaseJdbcDaoTest
{
    @Before    
    public void setup() 
    {
    }
    
    @After
    public void tearDown() 
    {
    }

    
    @Test
    public void testBaseJdbcDao()
    {
        CommonDao dao = new CommonDao();
        String tableName = "test1";
        Map<String, Object> map = null;
        int cnt = 0;
        dao.setTableName(tableName);
        
        dao.deleteByWhere("", null);
        
        map = new HashMap<String, Object>();
        map.put("t1", 7);
        
        cnt = dao.insert(map);
        
        System.out.println(cnt);
        //--
        dao = new CommonDao();
        tableName = "test2";
        dao.setDbName("test");
        dao.setTableName(tableName);
        dao.deleteByWhere("", null);
        
        map = new HashMap<String, Object>();
        map.put("t1", 4);
        cnt = dao.insert(map);
        
        System.out.println(cnt);
    }
    
}
