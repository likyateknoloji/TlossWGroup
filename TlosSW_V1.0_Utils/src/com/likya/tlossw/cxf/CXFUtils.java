package com.likya.tlossw.cxf;

import java.util.HashMap;
import java.util.Map;

public class CXFUtils {
	
	private static final Map<String, Class<?>> HOLDER_TYPES_MAP;
	
	static {
		HOLDER_TYPES_MAP = new HashMap<String, Class<?>>();
		HOLDER_TYPES_MAP.put("int", java.lang.Integer.class);
		HOLDER_TYPES_MAP.put("long", java.lang.Long.class);
		HOLDER_TYPES_MAP.put("short", java.lang.Short.class);
		HOLDER_TYPES_MAP.put("float", java.lang.Float.class);
		HOLDER_TYPES_MAP.put("double", java.lang.Double.class);
		HOLDER_TYPES_MAP.put("boolean", java.lang.Boolean.class);
		HOLDER_TYPES_MAP.put("byte", java.lang.Byte.class);
		HOLDER_TYPES_MAP.put("[B", byte[].class);
	}
	
	public static Object newInstance(Class<?> type, Object value){
	    if (!(HOLDER_TYPES_MAP.containsKey(type.getName()) || HOLDER_TYPES_MAP.containsValue(type))) {
	        try {
				Object o = type.newInstance();
				o = value;
				return o;
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	    } else {
	        if (type.equals(Integer.class) || type.equals(int.class)) {
	            return new Integer(Integer.parseInt(value.toString()));
	            
	        } else if (type.equals(Float.class) || type.equals(float.class)) {
	        	return new Float(Float.parseFloat(value.toString())); 
	        	
	        } else if (type.equals(Double.class) || type.equals(double.class)) {
	        	return new Double(Double.parseDouble(value.toString())); 
	        	
	        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
	        	return new Byte(Byte.parseByte(value.toString())); 
	        
	        } else if (type.equals(Short.class) || type.equals(short.class)) {
	        	return new Short(Short.parseShort(value.toString())); 
	        	
	        } else if (type.equals(Long.class) || type.equals(long.class)) {
	        	return new Long(Long.parseLong(value.toString())); 
	        	
	        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
	        	return new Boolean(Boolean.parseBoolean(value.toString())); 
	        	
	        } else if (type.equals(Character.class) || type.equals(char.class)) {
	        	return new Character((Character) value); 
	        	
	        } else if (type.equals(Byte[].class) || type.equals(byte[].class)) {
	        	return value.toString().getBytes(); 
	        	
	        } else {
	        	return null;
	        }
	    }
	    return null;
	}
}
