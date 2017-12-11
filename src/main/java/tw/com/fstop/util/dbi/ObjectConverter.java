/*
 * net/balusc/util/ObjectConverter.java
 * 
 * Copyright (C) 2007 BalusC
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the
 * GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package tw.com.fstop.util.dbi;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic object converter.
 * <h3>Use examples</h3>
 * 
 * <pre>
 * Object o1 = Boolean.TRUE;
 * Integer i = ObjectConverter.convert(o1, Integer.class);
 * System.out.println(i); // 1
 * 
 * Object o2 = "false";
 * Boolean b = ObjectConverter.convert(o2, Boolean.class);
 * System.out.println(b); // false
 * 
 * Object o3 = new Integer(123);
 * String s = ObjectConverter.convert(o3, String.class);
 * System.out.println(s); // 123
 * </pre>
 * 
 * Not all possible conversions are implemented. You can extend the
 * <tt>ObjectConverter</tt>
 * easily by just adding a new method to it, with the appropriate logic. For
 * example:
 * 
 * <pre>
 * public static ToObject fromObjectToObject(FromObject fromObject)
 * {
 *     // Implement.
 * }
 * </pre>
 * 
 * The method name doesn't matter. It's all about the parameter type and the
 * return type.
 * 
 */
public final class ObjectConverter
{

    // Init
    // ---------------------------------------------------------------------------------------

    private static final Map<String, Method> CONVERTERS = new HashMap<String, Method>();

    static
    {
        // Preload converters.
        Method[] methods = ObjectConverter.class.getDeclaredMethods();
        for (Method method : methods)
        {
            if (method.getParameterTypes().length == 1)
            {
                // Converter should accept 1 argument. This skips the convert()
                // method.
                CONVERTERS.put(method.getParameterTypes()[0].getName() + "_" + method.getReturnType().getName(),
                        method);
            }
        }
    }

    private ObjectConverter()
    {
        // Utility class, hide the constructor.
    }

    // Action
    // -------------------------------------------------------------------------------------

    /**
     * Convert the given object value to the given class.
     * 
     * @param from
     *            The object value to be converted.
     * @param to
     *            The type class which the given object should be converted to.
     * @param <T>
     *            target type
     * @return The converted object value.
     * @throws NullPointerException
     *             If 'to' is null.
     * @throws UnsupportedOperationException
     *             If no suitable converter can be found.
     * @throws RuntimeException
     *             If conversion failed somehow. This can be caused by at least
     *             an ExceptionInInitializerError, IllegalAccessException or
     *             InvocationTargetException.
     */
    public static <T> T convert(Object from, Class<T> to)
    {

        // Null is just null.
        if (from == null)
        {
            return null;
        }

        // Can we cast? Then just do it.
        if (to.isAssignableFrom(from.getClass()))
        {
            return to.cast(from);
        }

        // Lookup the suitable converter.
        String converterId = from.getClass().getName() + "_" + to.getName();
        Method converter = CONVERTERS.get(converterId);
        if (converter == null)
        {
            throw new UnsupportedOperationException("Cannot convert from " + from.getClass().getName() + " to "
                    + to.getName() + ". Requested converter does not exist.");
        }

        // Convert the value.
        try
        {
            return to.cast(converter.invoke(to, from));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot convert from " + from.getClass().getName() + " to " + to.getName()
                    + ". Conversion failed with " + e.getMessage(), e);
        }
    }

    // Converters
    // ---------------------------------------------------------------------------------

    /**
     * Converts Boolean to String.
     * 
     * @param value
     *            The Boolean to be converted.
     * @return The converted String value.
     */
    public static String booleanToString(Boolean value)
    {
        return value.toString();
    }

    public static String byteToString(Byte value)
    {
        return value.toString();
    }
    
    public static String shortToString(Short value)
    {
        return value.toString();
    }

    /**
     * Converts Integer to String.
     * 
     * @param value
     *            The Integer to be converted.
     * @return The converted String value.
     */
    public static String integerToString(Integer value)
    {
        return value.toString();
    }

    public static String longToString(Long value)
    {
        return value.toString();
    }

    public static String floatToString(Float value)
    {
        return value.toString();
    }

    public static String doubleToString(Double value)
    {
        return value.toString();
    }

    public static String bigDecimalToString(BigDecimal value)
    {
        return value.toString();
    }
    //--
    
    //Boolean
    public static Boolean byteToBoolean(Byte value)
    {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Boolean shortToBoolean(Short value)
    {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }
    
    /**
     * Converts Integer to Boolean. If integer value is 0, then return FALSE,
     * else return TRUE.
     * 
     * @param value
     *            The Integer to be converted.
     * @return The converted Boolean value.
     */
    public static Boolean integerToBoolean(Integer value)
    {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Boolean longToBoolean(Long value)
    {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }
    
    public static Boolean floatToBoolean(Float value)
    {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Boolean doubleToBoolean(Double value)
    {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Boolean bigdecimalToBoolean(BigDecimal value)
    {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }
    
    /**
     * Converts String to Boolean.
     * 
     * @param value
     *            The String to be converted.
     * @return The converted Boolean value.
     */
    public static Boolean stringToBoolean(String value)
    {
        return Boolean.valueOf(value);
    }
    //--
    
    //Integer
    /**
     * Converts Boolean to Integer. If boolean value is TRUE, then return 1,
     * else return 0.
     * 
     * @param value
     *            The Boolean to be converted.
     * @return The converted Integer value.
     */
    public static Integer booleanToInteger(Boolean value)
    {
        return value.booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
    }
    
    public static Integer byteToInteger(Byte value)
    {
        return new Integer(value.intValue());
    }
    
    public static Integer shortToInteger(Short value)
    {
        return new Integer(value.intValue());
    }

    public static Integer longToInteger(Long value)
    {
        return new Integer(value.intValue());
    }
    
    public static Integer floatToInteger(Float value)
    {
        Long l = value.longValue();
        return new Integer(l.intValue());
    }
        
    public static Integer doubleToInteger(Double value)
    {
        Long l = value.longValue();
        return new Integer(l.intValue());
    }

    public static Integer bigDecimalToInteger(BigDecimal value)
    {
        Long l = value.longValue();
        return new Integer(l.intValue());
    }
    
    /**
     * Converts String to Integer.
     * 
     * @param value
     *            The String to be converted.
     * @return The converted Integer value.
     */
    public static Integer stringToInteger(String value)
    {
        // 因為 value 有可能包含小數點，會造成轉換失敗，所以先使用 Float 來轉成 integer
        // return Integer.valueOf(value);
        return new Integer(Float.valueOf(value).intValue());
    }
    //--
    
    //Long
    public static Long booleanToLong(Boolean value)
    {
        return value.booleanValue() ? Long.valueOf(1) : Long.valueOf(0);
    }
    
    public static Long byteToLong(Byte value)
    {
        return new Long(value.longValue());
    }

    public static Long shortToLong(Short value)
    {
        return new Long(value.longValue());
    }

    public static Long integerToLong(Integer value)
    {
        return new Long(value.longValue());
    }

    public static Long floatToLong(Float value)
    {
        return new Long(value.longValue());
    }

    public static Long doubleToLong(Double value)
    {
        return new Long(value.longValue());
    }
    
    public static Long bigDecimalToLong(BigDecimal value)
    {
        return new Long(value.longValue());
    }

    public static Long stringToLong(String value)
    {
        // 因為 value 有可能包含小數點，會造成轉換失敗，所以先使用 Float 來轉成 long
        // return Long.valueOf(value);
        return new Long(Float.valueOf(value).longValue());
    }
    //--
    
    //Float
    public static Float booleanToFloat(Boolean value)
    {
        return value.booleanValue() ? Float.valueOf(1) : Float.valueOf(0);
    }

    public static Float byteToFloat(Byte value)
    {
        return new Float(value.floatValue());
    }

    public static Float shortToFloat(Short value)
    {
        return new Float(value.floatValue());
    }

    public static Float integerToFloat(Integer value)
    {
        return new Float(value.floatValue());
    }

    public static Float longToFloat(Long value)
    {
        return new Float(value.floatValue());
    }
    
    public static Float doubleToFloat(Double value)
    {
        return new Float(value.floatValue());
    }

    public static Float bigDecimalToFloat(BigDecimal value)
    {
        return new Float(value.floatValue());
    }

    public static Float stringToFloat(String value)
    {
        return Float.parseFloat(value);
    }
    //--

    //Double
    public static Double booleanToDouble(Boolean value)
    {
        return value.booleanValue() ? Double.valueOf(1) : Double.valueOf(0);
    }

    public static Double byteToDouble(Byte value)
    {
        return new Double(value.doubleValue());
    }

    public static Double shortToDouble(Short value)
    {
        return new Double(value.doubleValue());
    }

    public static Double integerToDouble(Integer value)
    {
        return new Double(value.doubleValue());
    }

    public static Double floatToDouble(Float value)
    {
        //This will result in precision problem.
        //return new Double(value.doubleValue());
        
        Double d = new Double(value.toString());
        return d;
    }

    /**
     * Converts BigDecimal to Double.
     * 
     * @param value
     *            The BigDecimal to be converted.
     * @return The converted Double value.
     */
    public static Double bigDecimalToDouble(BigDecimal value)
    {
        return new Double(value.doubleValue());
    }

    /**
     * Converts String to Double.
     * 
     * @param value The String to be converted.
     * @return The converted Double value.
     */
    public static Double stringToDouble(String value)
    {
        return Double.valueOf(value);
    }    
    //--
    
    
    //BigDecimal
    public static BigDecimal booleanToBigDecimal(Boolean value)
    {
        return value.booleanValue() ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0);
    }

    public static BigDecimal byteToBigDecimal(Byte value)
    {
        return new BigDecimal(value.intValue());
    }

    public static BigDecimal shortToBigDecimal(Short value)
    {
        return new BigDecimal(value.intValue());
    }

    public static BigDecimal integerToBigDecimal(Integer value)
    {
        return new BigDecimal(value.intValue());
    }

    public static BigDecimal floatToBigDecimal(Float value)
    {
        //return new BigDecimal(value.doubleValue());
        return new BigDecimal(value.toString());
    }
    
    /**
     * Converts Double to BigDecimal.
     * 
     * @param value
     *            The Double to be converted.
     * @return The converted BigDecimal value.
     */
    public static BigDecimal doubleToBigDecimal(Double value)
    {
        //return new BigDecimal(value.doubleValue());
        return new BigDecimal(value.toString());
    }
    
    public static BigDecimal stringToBigDecimal(String value)
    {
        return new BigDecimal(value);
    }
    //--
    
    //-- primitive --
    //byte
    public static byte booleanTobyte(Boolean value)
    {
        return value.booleanValue() ? (byte) 1 : (byte) 0;
    }

    public static byte byteTobyte(Byte value)
    {        
        return value.byteValue();
    }
    
    public static byte shortTobyte(Short value)
    {        
        return value.byteValue();
    }
    
    public static byte integerTobyte(Integer value)
    {        
        return value.byteValue();
    }

    public static byte longTobyte(Long value)
    {        
        return value.byteValue();
    }
    
    public static byte floatTobyte(Float value)
    {        
        return value.byteValue();
    }

    public static byte doubleTobyte(Double value)
    {        
        return value.byteValue();
    }

    public static byte bigDecimalTobyte(BigDecimal value)
    {        
        return value.byteValue();
    }

    public static byte stringTobyte(String value)
    {        
        return new Byte(value);
    }    
    //--
    
    //short
    public static short booleanToshort(Boolean value)
    {
        return value.booleanValue() ? (byte) 1 : (byte) 0;
    }
    
    public static short byteToshort(Byte value)
    {        
        return value.shortValue();
    }

    public static short shortToshort(Short value)
    {        
        return value.shortValue();
    }

    public static short integerToshort(Integer value)
    {        
        return value.shortValue();
    }

    public static short longToshort(Long value)
    {        
        return value.shortValue();
    }
    
    public static short floatToshort(Float value)
    {        
        return value.shortValue();
    }
    
    public static short doubleToshort(Double value)
    {        
        return value.shortValue();
    }

    public static short bigDecimalToshort(BigDecimal value)
    {        
        return value.shortValue();
    }

    public static short stringToshort(String value)
    {        
        return new Short(value);
    }
    //--
    
    //int
    public static int booleanToInt(Boolean value)
    {
        return value.booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
    }
    
    public static int byteToInt(Byte value)
    {        
        return value.intValue();
    }
    
    public static int shortToInt(Short value)
    {        
        return value.intValue();
    }

    public static int integerToInt(Integer value)
    {        
        return value.intValue();
    }

    public static int longToInt(Long value)
    {        
        return value.intValue();
    }
    
    public static int floatToInt(Float value)
    {        
        return value.intValue();
    }

    public static int doubleToInt(Double value)
    {        
        return value.intValue();
    }

    public static int bigDecimalToInt(BigDecimal value)
    {        
        return value.intValue();
    }

    public static int stringToInt(String value)
    {        
        return new Integer(value);
    }
    //--
    
    //float
    public static float booleanTofloat(Boolean value)
    {
        return value.booleanValue() ? Float.valueOf(1) : Float.valueOf(0);
    }
    
    public static float byteTofloat(Byte value)
    {        
        return value.floatValue();
    }
    
    public static float shortTofloat(Short value)
    {        
        return value.floatValue();
    }
    
    public static float integerTofloat(Integer value)
    {        
        return value.floatValue();
    }
    
    public static float floatTofloat(Float value)
    {        
        return value.floatValue();
    }

    public static float doubleTofloat(Double value)
    {        
        return value.floatValue();
    }
    
    public static float bigDecimalTofloat(BigDecimal value)
    {        
        return value.floatValue();
    }
    
    public static float stringTofloat(String value)
    {        
        return new Float(value);
    }
    //--
    
    //double
    public static double booleanTodouble(Boolean value)
    {
        return value.booleanValue() ? Double.valueOf(1) : Double.valueOf(0);
    }
    
    public static double byteTodouble(Byte value)
    {        
        return value.doubleValue();
    }
    
    public static double shortTodouble(Short value)
    {        
        return value.doubleValue();
    }
    
    public static double integerTodouble(Integer value)
    {        
        return value.doubleValue();
    }
    
    public static double floatTodouble(Float value)
    {        
        //This will result in precision problem.
        //return new Double(value.doubleValue());
        
        //This is OK
        //BigDecimal b = new BigDecimal(value.toString());
        
        Double d = new Double(value.toString());
        return d;
    }

    public static double doubleTodouble(Double value)
    {        
        return value.doubleValue();
    }
    
    public static double bigDecimalTodouble(BigDecimal value)
    {        
        return value.doubleValue();
    }
    
    public static double stringTodouble(String value)
    {        
        return new Double(value);
    }
    //--

}
