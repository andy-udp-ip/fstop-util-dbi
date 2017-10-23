
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BaseJdbcDaoTest
{
    
    Server server1;
    Server server2;
    
    /**
     * Setup and start db servers
     *  
     * @throws IOException io exception
     * @throws AclFormatException acl exception
     */
    @Before    
    public void setup() throws IOException, AclFormatException 
    {
        
        Properties prop = new Properties();
        InputStream input = null;
        String fileName;
        HsqlProperties configProps;
        
        fileName = "hsql_server.properties";
        input = new FileInputStream(fileName);        
        prop.load(input);
        server1 = new Server();        
        configProps = new HsqlProperties(prop);
        server1.setProperties(configProps);        
        server1.setNoSystemExit(true);
        server1.start();

        prop.clear();

        fileName = "hsql_server2.properties";
        input = new FileInputStream(fileName);     
        prop.load(input);
        server2 = new Server();        
        configProps = new HsqlProperties(prop);
        server2.setProperties(configProps);        
        server2.setNoSystemExit(true);
        server2.start();
        
        System.out.println("====================[DB Server Start Complete]=======================");
        
    }
    
    /**
     * Shutdown db servers.
     * 
     */
    @After
    public void tearDown() 
    {
        server1.stop();
        server2.stop();
        System.out.println("====================[DB Server Shutdown Complete]=======================");
    }

    @Test
    public void testBaseJdbcDao() throws SQLException
    {
        CommonDao dao = new CommonDao();
        String tableName = "";
        String sql;
        Map<String, Object> map = null;
        Map<String, Object> data = null;
        List<Map<String, Object>> list = null;
        int cnt = 0;
        boolean isOK;
        
        //-- use default db name 
        tableName = "TEST1";
        dao.setTableName(tableName);        
        dao.deleteByWhere("", null);  //delete all
        
        map = new HashMap<String, Object>();
        map.put("T1", 7);        
        cnt = dao.insert(map);        
        assertThat(cnt).isEqualTo(1);
        
        //-- custom db name
        dao = new CommonDao();
        dao.setDbName("test");
        
        tableName = "TEST2";        
        dao.setTableName(tableName);
        dao.deleteByWhere("", null); //delete all
        
        map = new HashMap<String, Object>();
        map.put("T1", 4);
        cnt = dao.insert(map);        
        assertThat(cnt).isEqualTo(1);
        
        
        //-- custom insert sql
        dao = new CommonDao();
        tableName = "TEST1";
        dao.setTableName(tableName);        
        dao.deleteByWhere("", null);  //delete all
        
        sql = "insert into TEST1 (T1) values (:T1) ";
        map = new HashMap<String, Object>();
        map.put("T1", 1);
        cnt = dao.insert(sql, map);        
        assertThat(cnt).isEqualTo(1);
        
        //-- batch insert        
        dao = new CommonDao();
        dao.setDbName("test");        
        tableName = "TEST2";             
        dao.setTableName(tableName);        
        dao.deleteByWhere("", null);  //delete all
        
        sql = "insert into TEST2 (T1) values (:T1) ";
        
        dao.startBatch(sql);
        for(int i=0; i < 10; i++)
        {
            map = new HashMap<String, Object>();
            map.put("T1", i);
            dao.addBatch(map);
        }
        int [] r = dao.endBatch();
        for(int i=0; i < r.length; i++)
        {
            System.out.println("batch=" + i + " =" + r[i]);
            assertThat(r[i]).isEqualTo(1);
        }
        
        //-- batch insert abort
        dao = new CommonDao();
        dao.setDbName("test");        
        tableName = "TEST2";             
        dao.setTableName(tableName);               
        sql = "insert into TEST2 (T1) values (:T1) ";
        
        dao.startBatch(sql);
        for(int i=0; i < 10; i++)
        {
            map = new HashMap<String, Object>();
            map.put("T1", i);
            dao.addBatch(map);
        }
        dao.abortBatch();
        
        //-- batch update
        dao = new CommonDao();
        dao.setDbName("test");        
        tableName = "TEST2";             
        dao.setTableName(tableName);               
        sql = "update TEST2 set T2=:T2 where T1=:T1";
        
        dao.startBatch(sql);
        for(int i=0; i < 10; i++)
        {
            map = new HashMap<String, Object>();
            map.put("T1", i);
            map.put("T2", i);
            dao.addBatch(map);
        }
        r = dao.endBatch();
        for(int i=0; i < r.length; i++)
        {
            System.out.println("batch=" + i + " =" + r[i]);
            assertThat(r[i]).isEqualTo(1);
        }        
        
        
        //-- use default db name 
        tableName = "TEST1";        
        dao = new CommonDao();
        dao.setTableName(tableName);  
        //dao.setDbName("test");      
        dao.deleteByWhere("", null);  //delete all
        
        map = new HashMap<String, Object>();
        map.put("T1", 1);        
        map.put("T2", 1); 
        dao.insert(map);        
        
        map.put("T2", 2); 
        cnt = dao.update(null, map);
        assertThat(cnt).isEqualTo(1);
        
        data = dao.findByKey(map);
        assertThat(data.get("T2").toString()).isEqualTo(map.get("T2").toString());
        
        sql = " where T2 = 2";
        cnt = dao.deleteByWhere(sql, null);
        assertThat(cnt).isEqualTo(1);

        map.clear();
        map.put("T1", 1);     
        sql = " where T1 = :T1";
        list = dao.find(sql, map);
        assertThat(list.size()).isEqualTo(0);

        
        //find by sql
        map.clear();
        map.put("T1", 1);        
        map.put("T2", 1); 
        dao.insert(map);        

        map.clear();
        map.put("T1", 1);     
        sql = "select T2 from TEST1 where T1 = :T1";
        list = dao.findBySQL(sql, map);
        assertThat(list.size()).isEqualTo(1);
        data = list.get(0);
        assertThat(data.get("T2").toString()).isEqualTo("1");
        
        //get record count
        cnt = (int) dao.getRecordCount(null, null);
        assertThat(cnt).isEqualTo(1);
        
        map.put("T1", 1);     
        sql = " where T1 = :T1";
        cnt = (int) dao.getRecordCount(sql, map);
        assertThat(cnt).isEqualTo(1);
        
        //get record count by sql
        map.clear();
        map.put("T1", 1);     
        sql = "select count(*) from TEST1 where T1 = :T1";
        cnt = (int) dao.getRecordCountBySQL(sql, map);
        assertThat(cnt).isEqualTo(1);
        
        //insert key
        map.clear();
        map.put("T1", 2);
        cnt = dao.insertKey(map);
        assertThat(cnt).isEqualTo(1);
        
        //insert by sql        
        map.clear();
        map.put("T1", 3);
        sql = " insert into TEST1 (T1) values (:T1) ";
        cnt = dao.insert(sql, map);
        assertThat(cnt).isEqualTo(1);
        
        //save
        map.clear();
        map.put("T1", 4);
        cnt = dao.save(map);
        assertThat(cnt).isEqualTo(1);

        map.put("T2", 4);
        cnt = dao.save(map);
        assertThat(cnt).isEqualTo(1);
        
        data = dao.findByKey(map);
        assertThat(data.get("T2").toString()).isEqualTo(map.get("T2").toString());
        
        //delete by key
        cnt = dao.deleteByKey(map);
        assertThat(cnt).isEqualTo(1);
        
        //delete by sql
        sql = "delete from TEST1 where T1 = :T1";
        map.clear();
        map.put("T1", 3);
        cnt = dao.deleteBySQL(sql, map);
        assertThat(cnt).isEqualTo(1);
        
        //update ex
        sql = "update TEST1 set T1 = :T1 where T1 = :T1_old";
        map.clear();
        map.put("T1_old", 2);
        map.put("T1", 3);
        cnt = dao.updateEx(sql, map);
        assertThat(cnt).isEqualTo(1);

        data = dao.findByKey(map);
        assertThat(data.get("T1").toString()).isEqualTo(map.get("T1").toString());
        
        //is key exist
        isOK = dao.isKeyExist(map);
        assertTrue(isOK);
        
        
        //exec proc        
        sql = "DROP FUNCTION SP_TEST IF EXISTS;";
        dao.updateEx(sql, null);
        
        sql = "CREATE FUNCTION SP_TEST (t int) "
            + "RETURNS int "
            + "RETURN t - 1  "        
        ;
        cnt = dao.updateEx(sql, null);
        System.out.println(cnt);
        
        sql = "SP_TEST(123) ";
        list = dao.execProc(sql, null);
        data = list.get(0);
        System.out.println(data.toString());
        assertNotNull(data);
        assertThat(data.get("@p0").toString()).isEqualTo("122");
        
    }
    
}
