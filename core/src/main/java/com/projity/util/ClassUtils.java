/*
The contents of this file are subject to the Common Public Attribution License
Version 1.0 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at
http://www.projity.com/license . The License is based on the Mozilla Public
License Version 1.1 but Sections 14 and 15 have been added to cover use of
software over a computer network and provide for limited attribution for the
Original Developer. In addition, Exhibit A has been modified to be consistent
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
specific language governing rights and limitations under the License. The
Original Code is OpenProj. The Original Developer is the Initial Developer and
is Projity, Inc. All portions of the code written by Projity are Copyright (c)
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the
Projity End-User License Agreeement (the Projity License), in which case the
provisions of the Projity License are applicable instead of those above. If you
wish to allow use of your version of this file only under the terms of the
Projity License and not to allow others to use your version of this file under
the CPAL, indicate your decision by deleting the provisions above and replace
them with the notice and other provisions required by the Projity  License. If
you do not delete the provisions above, a recipient may use your version of this
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices
in Exhibits A and B of the license at http://www.projity.com/license. You should
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright � 2006, 2007
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj,
an open source solution from Projity. Attribution URL: http://www.projity.com
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined
in the CPAL as a work which combines Covered Code or portions thereof with code
not governed by the terms of the CPAL. However, in addition to the other notice
obligations, all copies of the Covered Code in Executable and Source Code form
distributed must, as a form of attribution of the original author, include on
each user interface screen the "OpenProj" logo visible to all users.  The
OpenProj logo should be located horizontally aligned with the menu bar and left
justified on the top left of the screen adjacent to the File menu.  The logo
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it
must direct them back to http://www.projity.com.
*/
package com.projity.util;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.projity.datatype.Duration;
import com.projity.datatype.Money;
import com.projity.datatype.Rate;
import com.projity.datatype.TimeUnit;
import com.projity.datatype.Work;
import com.projity.field.Field;
import com.projity.strings.Messages;


/**
 * Utility functions for manipulating primitive types
 */
public class ClassUtils {
	private static Log log = LogFactory.getLog(ClassUtils.class);
	public static final Long defaultLong = new Long(0L);
	public static final Double defaultDouble = new Double(0.0);
	public static final Integer defaultInteger = new Integer(0);
	public static final Float defaultFloat = new Float(0.0);
	public static final Boolean defaultBoolean = new Boolean(false);
	public static final String defaultString = new String();
	public static final Rate defaultRate = new Rate(1.0D);
	public static final Rate defaultUnitlessRate = new Rate(1, TimeUnit.NON_TEMPORAL);
	public static final long NULL_LONG = -999999L;
	public static final double NULL_DOUBLE = -99999.0D;

	public static boolean isBasicType(Class clazz) {
		return clazz == String.class
				|| clazz == Double.class
				|| clazz == Float.class
				|| clazz == Integer.class
				|| clazz == Long.class
				|| clazz == Short.class
				|| clazz == Boolean.class;
	}
	/**
	 * Given a type, return its default value.  If type is unknown, a new one is constructed
	 * @param clazz
	 * @return
	 */
	public static Object getDefaultValueForType(Class clazz) {
		if (clazz == String.class)
			return defaultString;
		else if (clazz == Double.class || clazz == Double.TYPE)
			return defaultDouble;
		else if (clazz == Integer.class || clazz == Integer.TYPE)
			return defaultInteger;
		else if (clazz == Long.class || clazz == Long.TYPE)
			return defaultLong;
		else if (clazz == Float.class || clazz == Float.TYPE)
			return defaultFloat;
		else if (clazz == Boolean.class)
			return defaultBoolean;
		else if (clazz == Rate.class)
			return defaultRate;
		else {
			try {
				System.out.println("making default for class" + clazz);
				return clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static boolean isDefaultValue(Object value) {
		return (value == defaultLong
			|| value == defaultDouble
			|| value == defaultInteger
			|| value == defaultFloat
			|| value == defaultString
			|| value == Duration.ZERO
			|| value == defaultRate
			|| value == DateTime.getZeroDate());
	}

	public static final Long LONG_MULTIPLE_VALUES = new Long(0L);
	public static final Double DOUBLE_MULTIPLE_VALUES = new Double(0.0);
	public static final Integer INTEGER_MULTIPLE_VALUES = new Integer(0);
	public static final Float FLOAT_MULTIPLE_VALUES = new Float(0.0);
	public static final Boolean BOOLEAN_MULTIPLE_VALUES = new Boolean(false);
	public static final String STRING_MULTIPLE_VALUES = new String();
	public static final Double PERCENT_MULTIPLE_VALUES = new Double(-9876543.21); // a never used value used as flag to indicate multiple values
	public static final Rate RATE_MULTIPLE_VALUES = new Rate();
	/**
	 * Given a type, return a value that signifies that there are multiple values.  This can occur in a dialog which works on multile objects at once.  If type is unknown, a new one is constructed
	 * @param clazz
	 * @return
	 */
	public static Object getMultipleValueForType(Class clazz) {
		if (clazz == String.class)
			return STRING_MULTIPLE_VALUES;
		else if (clazz == Double.class || clazz == Double.TYPE)
			return DOUBLE_MULTIPLE_VALUES;
		else if (clazz == Integer.class || clazz == Integer.TYPE)
			return INTEGER_MULTIPLE_VALUES;
		else if (clazz == Long.class || clazz == Long.TYPE)
			return LONG_MULTIPLE_VALUES;
		else if (clazz == Float.class || clazz == Float.TYPE)
			return FLOAT_MULTIPLE_VALUES;
		else if (clazz == Boolean.class)
			return BOOLEAN_MULTIPLE_VALUES;
		else if (clazz == Rate.class)
			return RATE_MULTIPLE_VALUES;
		else {
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			return null;
		}
	}

	public static boolean isMultipleValue(Object value) {
		if (value == null)
			return false;
		return (value == LONG_MULTIPLE_VALUES
			|| value == DOUBLE_MULTIPLE_VALUES
			|| value == INTEGER_MULTIPLE_VALUES
			|| value == FLOAT_MULTIPLE_VALUES
			|| value == STRING_MULTIPLE_VALUES
			|| value.equals(PERCENT_MULTIPLE_VALUES)
			|| value == RATE_MULTIPLE_VALUES
			|| value == Duration.ZERO
			|| value == DateTime.getZeroDate());
	}



	/**
	 * Get the corresponding object class from a primitive class
	 * @param clazz primitive class
	 * @return Object class.
	 * @throws ClassCastException if class is unknown primitive
	 */
	public static Class primitiveToObjectClass(Class clazz) {
//		return MethodUtils.toNonPrimitiveClass(clazz);
		if (clazz == Boolean.TYPE)
			return Boolean.class;
		else if (clazz == Character.TYPE)
			return Character.class;
		else if (clazz == Byte.TYPE)
			return Byte.class;
		else if (clazz == Short.TYPE)
			return Short.class;
		else if (clazz == Integer.TYPE)
			return Integer.class;
		else if (clazz == Long.TYPE)
			return Long.class;
		else if (clazz == Float.TYPE)
			return Float.class;
		else if (clazz == Double.TYPE)
			return Double.class;
		throw new ClassCastException("Cannot convert class" + clazz + " to an object class");
	}

/**
 * Convert a Double to an Object of a given class
 * @param value Double value to convert
 * @param clazz Class the class to convert to
 * @return new object of the given class
 * @throws IllegalArgumentException if the value is not convertible to the class
 */	public static Object doubleToObject(Double value, Class clazz) {
		if (clazz == Boolean.class)
			return new Boolean(value.doubleValue() != 0.0);
		else if (clazz == Byte.class)
			return new Byte(value.byteValue());
		else if (clazz == Short.class)
			return new Short(value.shortValue());
		else if (clazz == Integer.class)
			return new Integer(value.intValue());
		else if (clazz == Long.class)
			return new Long(value.longValue());
		else if (clazz == Float.class)
			return new Float(value.floatValue());
		else if (clazz == Double.class)
			return value;
		else if (clazz == Money.class)
			return Money.getInstance(value.doubleValue());
		else if (clazz == Duration.class)
			return Duration.getInstanceFromDouble(value);
		else if (clazz == Work.class)
			return Work.getWorkInstanceFromDouble(value);



		throw new IllegalArgumentException("Class " + clazz + " cannot be converted from a Double");
	}

	public static java.lang.reflect.Field staticFieldFromFullName(String nameAndField) {
		int lastDot = nameAndField.lastIndexOf(".");
		String className = nameAndField.substring(0,lastDot);
		String fieldName = nameAndField.substring(lastDot+1);
		try {
			return ClassUtils.forName(className).getDeclaredField(fieldName);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Method staticVoidMethodFromFullName(String nameAndField) {
		return staticMethodFromFullName(nameAndField,null);
	}

	public static Method staticMethodFromFullName(String nameAndField, Class[] args) {
		int lastDot = nameAndField.lastIndexOf(".");
		String className = nameAndField.substring(0,lastDot);
		String methodName = nameAndField.substring(lastDot+1);
		try {
			return ClassUtils.forName(className).getDeclaredMethod(methodName, args);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Object invokeVoidStaticMethodFromFullName(String nameAndField) {
		if (nameAndField == null)
			return null;
		try {
			return ClassUtils.staticVoidMethodFromFullName(nameAndField).invoke(null,(Object[])null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	/**
	 * Set the array size of the custom field this applies to
	 * @param boundsField
	 */
		public static void setStaticField(String field, int value) {
			try {
				staticFieldFromFullName(field).setInt(null,value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static void setStaticField(String field, String value) {
			try {
				staticFieldFromFullName(field).set(null,value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Safe Class.forName.  See http://radio.weblogs.com/0112098/stories/2003/02/12/classfornameIsEvil.html
		 * @param className
		 * @return
		 * @throws ClassNotFoundException
		 */
		public static Class forName(String className) throws ClassNotFoundException {
			Class theClass = null;
			log.debug("Load class :"+className);
			try {
			    theClass = Class.forName( className, true, Thread.currentThread().getContextClassLoader() );
			}
			catch (ClassNotFoundException e) {
				log.fatal("Class not found("+className+"): ",e);
			    theClass = Class.forName( className );
			}
			return theClass;
		}

		public static boolean setSimpleProperty(Object bean, String name, Object value) {
			try {
				PropertyUtils.setSimpleProperty(bean,name,value);
				return true;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		private static Class[] getterParams = new Class[] {};
		public static boolean isObjectReadOnly(Object object){
			if (object==null) return false;
			Boolean value=null;
			try {
				Method m=object.getClass().getMethod("isReadOnly", getterParams);
				if (m!=null) value=(Boolean)m.invoke(object,null);
			}
			catch (IllegalArgumentException e) {}
			catch (IllegalAccessException e) {}
			catch (InvocationTargetException e) {}
			catch (NoSuchMethodException e) {}
			return value != null&&value.booleanValue();

		}
		private static Class[] fieldGetterParams = new Class[] {Field.class};
		public static boolean isObjectFieldReadOnly(Object object,Field field){
			if (object==null) return false;
			Boolean value=null;
			try {
				Method m=object.getClass().getMethod("isReadOnly", fieldGetterParams);
				if (m!=null)
					value=(Boolean)m.invoke(object,new Object[] {field});
			}
			catch (IllegalArgumentException e) {}
			catch (IllegalAccessException e) {}
			catch (InvocationTargetException e) {}
			catch (NoSuchMethodException e) {}
			return value != null&&value.booleanValue();

		}

		private static HashMap<Class,Comparator> comparatorMap = null;
		private static final Comparator defaultTextComparator=
				new Comparator() {
					public int compare(Object o1, Object o2) {
						return ("" + o1).compareTo("" +o2);
					}};

		public static Comparator getComparator(Class clazz) {
			if (comparatorMap == null) {
				comparatorMap = new HashMap<Class,Comparator>();
				comparatorMap.put(String.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((String)o1).compareTo((String) o2);
							}});
				comparatorMap.put(Date.class,
					new Comparator() {
						public int compare(Object o1, Object o2) {
							if (o1 == null)
								return (o2 == null ? 0 : -1);
							else if (o2 == null)
								return 1;
							return ((Date)o1).compareTo((Date) o2);
						}});
				comparatorMap.put(Integer.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Integer)o1).compareTo((Integer) o2);
							}});
				comparatorMap.put(Long.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Long)o1).compareTo((Long) o2);
							}});
				comparatorMap.put(Short.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Short)o1).compareTo((Short) o2);
							}});
				comparatorMap.put(Float.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Float)o1).compareTo((Float) o2);
							}});
				comparatorMap.put(Double.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Double)o1).compareTo((Double) o2);
							}});
				comparatorMap.put(Byte.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Byte)o1).compareTo((Byte) o2);
							}});
				comparatorMap.put(Boolean.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Boolean)o1).compareTo((Boolean) o2);
							}});
				comparatorMap.put(Money.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Money)o1).compareTo((Money) o2);
							}});
				comparatorMap.put(Duration.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Duration)o1).compareTo(o2);
							}});
				comparatorMap.put(Work.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Work)o1).compareTo(o2);
							}});
				comparatorMap.put(Rate.class,
						new Comparator() {
							public int compare(Object o1, Object o2) {
								if (o1 == null)
									return (o2 == null ? 0 : -1);
								else if (o2 == null)
									return 1;
								return ((Rate)o1).compareTo(o2);
							}});
			}
			Comparator result = comparatorMap.get(clazz);
			if (result == null) { // in case none found, try comparing if the class is comparable
				if (Comparable.class.isAssignableFrom(clazz))
					return ComparableComparator.getInstance();
				else
					return defaultTextComparator;
			}
			return comparatorMap.get(clazz);

		}
		public static boolean isNull(Object value) {
			if (value instanceof Long)
				return (((Long) value).longValue() == ClassUtils.NULL_LONG);
			else if (value instanceof Duration)
				return Duration.millis(((Duration)value).getEncodedMillis()) == ClassUtils.NULL_LONG;
			else if (value instanceof Number) // for other number types
				return (((Number) value).doubleValue() == ClassUtils.NULL_DOUBLE);
			return false;
		}





		/**
		 * ByteArrayInputStream implementation that does not synchronize methods.
		 */
		public static class FastByteArrayInputStream extends InputStream {
		    /**
		     * Our byte buffer
		     */
		    protected byte[] buf = null;

		    /**
		     * Number of bytes that we can read from the buffer
		     */
		    protected int count = 0;

		    /**
		     * Number of bytes that have been read from the buffer
		     */
		    protected int pos = 0;

		    public FastByteArrayInputStream(byte[] buf, int count) {
		        this.buf = buf;
		        this.count = count;
		    }

		    public final int available() {
		        return count - pos;
		    }

		    public final int read() {
		        return (pos < count) ? (buf[pos++] & 0xff) : -1;
		    }

		    public final int read(byte[] b, int off, int len) {
		        if (pos >= count)
		            return -1;

		        if ((pos + len) > count)
		            len = (count - pos);

		        System.arraycopy(buf, pos, b, off, len);
		        pos += len;
		        return len;
		    }

		    public final long skip(long n) {
		        if ((pos + n) > count)
		            n = count - pos;
		        if (n < 0)
		            return 0;
		        pos += n;
		        return n;
		    }

		}

		/**
		 * ByteArrayOutputStream implementation that doesn't synchronize methods
		 * and doesn't copy the data on toByteArray().
		 */
		public static class FastByteArrayOutputStream extends OutputStream {
		    /**
		     * Buffer and size
		     */
		    protected byte[] buf = null;
		    protected int size = 0;

		    /**
		     * Constructs a stream with buffer capacity size 5K
		     */
		    public FastByteArrayOutputStream() {
		        this(5 * 1024);
		    }

		    /**
		     * Constructs a stream with the given initial size
		     */
		    public FastByteArrayOutputStream(int initSize) {
		        this.size = 0;
		        this.buf = new byte[initSize];
		    }

		    /**
		     * Ensures that we have a large enough buffer for the given size.
		     */
		    private void verifyBufferSize(int sz) {
		        if (sz > buf.length) {
		            byte[] old = buf;
		            buf = new byte[Math.max(sz, 2 * buf.length )];
		            System.arraycopy(old, 0, buf, 0, old.length);
		            old = null;
		        }
		    }

		    public int getSize() {
		        return size;
		    }

		    /**
		     * Returns the byte array containing the written data. Note that this
		     * array will almost always be larger than the amount of data actually
		     * written.
		     */
		    public byte[] getByteArray() {
		        return buf;
		    }

		    public final void write(byte b[]) {
		        verifyBufferSize(size + b.length);
		        System.arraycopy(b, 0, buf, size, b.length);
		        size += b.length;
		    }

		    public final void write(byte b[], int off, int len) {
		        verifyBufferSize(size + len);
		        System.arraycopy(b, off, buf, size, len);
		        size += len;
		    }

		    public final void write(int b) {
		        verifyBufferSize(size + 1);
		        buf[size++] = (byte) b;
		    }

		    public void reset() {
		        size = 0;
		    }

		    /**
		     * Returns a ByteArrayInputStream for reading back the written data
		     */
		    public InputStream getInputStream() {
		        return new FastByteArrayInputStream(buf, size);
		    }

		}

		/**
		 * See http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
		 * Utility for making deep copies (vs. clone()'s shallow copies) of
		 * objects. Objects are first serialized and then deserialized. Error
		 * checking is fairly minimal in this implementation. If an object is
		 * encountered that cannot be serialized (or that references an object
		 * that cannot be serialized) an error is printed to System.err and
		 * null is returned. Depending on your specific application, it might
		 * make more sense to have copy(...) re-throw the exception.
		 */
	    /**
	     * Returns a copy of the object, or null if the object cannot
	     * be serialized.
	     */
	    public static Object deepCopy(Object orig) {
	        Object obj = null;
	        try {
	            // Write the object out to a byte array
	            FastByteArrayOutputStream fbos =
	                    new FastByteArrayOutputStream();
	            ObjectOutputStream out = new ObjectOutputStream(fbos);
	            out.writeObject(orig);
	            out.flush();
	            out.close();

	            // Retrieve an input stream from the byte array and read
	            // a copy of the object back in.
	            ObjectInputStream in =
	                new ObjectInputStream(fbos.getInputStream());
	            obj = in.readObject();
	        }
	        catch(IOException e) {
	            e.printStackTrace();
	        }
	        catch(ClassNotFoundException cnfe) {
	            cnfe.printStackTrace();
	        }
	        return obj;
	    }



}
