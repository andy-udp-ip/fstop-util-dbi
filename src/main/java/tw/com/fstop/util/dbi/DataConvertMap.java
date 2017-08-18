package tw.com.fstop.util.dbi;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DataConvertMap<K, V> extends LinkedHashMap<K, V>
{
	private static final long serialVersionUID = -4883158026909924823L;

	public <T> T get(K k, Class<T> clazz)
	{
		return ObjectConverter.convert(this.get(k), clazz);
	}
	
	public void copy (Map<K, V> m)
	{
		for(K key : m.keySet())
		{
			this.put(key, m.get(key));
		}
	}
	
	/**
	 * 將指定內容依指定的物件型別進行轉換
	 * 因為 primitive type 無法直接改變 value，所以必需return後承接
	 * @param k      map key
	 * @param obj    型別物件
	 * @return       轉換後的值
	 */
	@SuppressWarnings("unchecked")
	public <T> T as(K k, Object obj)
	{
	
		if (obj == null) return null;
		
		if(obj.getClass().equals(String.class))
		{
			return (T) ObjectConverter.convert(this.get(k), String.class);
		}
		
		if(obj.getClass().equals(BigDecimal.class))
		{
			return (T) ObjectConverter.convert(this.get(k), BigDecimal.class);
		}

		if(obj.getClass().equals(Boolean.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Boolean.class);
		}
		
		if(obj.getClass().equals(Integer.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Integer.class);
		}

		if(obj.getClass().equals(Long.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Long.class);
		}

		if(obj.getClass().equals(Float.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Float.class);
		}

		if(obj.getClass().equals(Double.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Double.class);
		}

		if(obj.getClass().equals(Byte.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Byte.class);
		}

		if(obj.getClass().equals(Short.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Short.class);
		}

		if(obj.getClass().equals(Character.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Character.class);
		}
		
		if(obj.getClass().equals(Clob.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Clob.class);
		}

		if(obj.getClass().equals(Blob.class))
		{
			return (T) ObjectConverter.convert(this.get(k), Blob.class);
		}
		
		return (T) obj;		
	}
	
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    //public static boolean isWrapperType(Class<?> clazz)
    public static boolean isWrapperType(Object obj)
    {
//    	Object o = obj;
//    	Class<?> clazz = o.getClass();
//      return WRAPPER_TYPES.contains(clazz);
        return WRAPPER_TYPES.contains(obj.getClass());
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
    
    public static void main(String [] args)
    {
    	DataConvertMap<String, Object> m = new DataConvertMap<String, Object>();
    	
    	int i = 123;
    	float f = 456.78f;
    	double d = 456.78d;
    	long l = 321L;
    	BigDecimal bd = new BigDecimal(56789);

    	m.put("int", i);
    	m.put("float", f);
    	m.put("double", d);
    	m.put("long", l);
    	m.put("BigDecimal", bd);
    	
    	String v = "";
    	String s = "";
    	v = m.as("int", s);
    	System.out.println(v);
    	
    	long vl = 0; 
    	vl = m.as("long", vl);
    	System.out.println(vl);
    	
    	Float fl = 0f;
    	fl = m.as("long", fl);
    	System.out.println(fl);
    }
    
}
