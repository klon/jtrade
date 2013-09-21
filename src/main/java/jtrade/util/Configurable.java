package jtrade.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configurable<T> {
	private static final Logger logger = LoggerFactory.getLogger(Configurable.class);
	private static List<Configurable<?>> configurables = new ArrayList<Configurable<?>>();
	private static Properties configuration;

	/**
	 * Declared name of the configurable in container class
	 */
	private String name;

	/**
	 * Holds the current value (never null).
	 */
	private T value;

	/**
	 * Holds the default value (never null).
	 */
	private T defaultValue;

	/**
	 * Holds the class where this configurable is defined.
	 */
	private Class<?> container;

	/**
	 * Holds the min value for optimization.
	 */
	private T min;

	/**
	 * Holds the max value for optimization.
	 */
	private T max;

	/**
	 * Creates a new configurable having the specified default value.
	 * 
	 * @param defaultValue
	 *          the default value.
	 * @throws IllegalArgumentException
	 *           if <code>defaultValue</code> is <code>null</code>.
	 */
	public Configurable(String name, T defaultValue) {
		init(name, defaultValue, null, null);
	}

	/**
	 * Creates a new configurable having the specified default value.
	 * 
	 * @param defaultValue
	 *          the default value.
	 * @throws IllegalArgumentException
	 *           if <code>defaultValue</code> is <code>null</code>.
	 */
	public Configurable(String name, T defaultValue, T min, T max) {
		if (min == null) {
			throw new IllegalArgumentException("Min value cannot be null");
		}
		if (max == null) {
			throw new IllegalArgumentException("Max value cannot be null");
		}
		init(name, defaultValue, min, max);
	}

	private void init(String name, T defaultValue, T min, T max) {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null");
		}
		if (defaultValue == null) {
			throw new IllegalArgumentException("Default value cannot be null");
		}
		this.container = findContainer();
		this.name = new StringBuilder(container.getName()).append('#').append(name.toUpperCase()).toString();
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.min = min;
		this.max = max;

		configurables.add(this);

		Object value = configuration.get(this.name);
		if (value != null) {
			Configurable.configure(this, Util.coerceType(value, defaultValue.getClass()));
		}
	}

	/**
	 * Returns the current value for this configurable.
	 * 
	 * @return the current value (always different from <code>null</code>).
	 */
	public T get() {
		return value;
	}

	/**
	 * Returns the default value for this configurable.
	 * 
	 * @return the default value (always different from <code>null</code>).
	 */
	public T getDefault() {
		return defaultValue;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	/**
	 * Returns the container class of this configurable (the class where this
	 * configurable is defined as a <code>public static</code> field.
	 * 
	 * @return the container class or <code>null</code> if unknown (e.g. J2ME).
	 */
	public Class<?> getContainer() {
		return container;
	}

	/**
	 * Returns the field name of this configurable (for example <code>
	 * "javolution.context.ConcurrentContext#MAXIMUM_CONCURRENCY"</code>) for
	 * {@link javolution.context.ConcurrentContext#MAXIMUM_CONCURRENCY}.
	 * 
	 * @return this configurable name or <code>null</code> if the name of this
	 *         configurable is unknown (e.g. J2ME).
	 */
	public String getName() {
		// if (name == null) {
		// try {
		// Collection<Field> fields = Util.getFields(container);
		// for (Field f : fields) {
		// if (f.get(null) == this) {
		// name = container.getName() + '#' + f.getName();
		// }
		// }
		// } catch (Exception e) {
		// throw new IllegalStateException(e);
		// }
		// }
		return name;
	}

	public boolean isOptimizable() {
		return defaultValue instanceof Number && min != max;
	}

	/**
	 * Notifies this configurable that its runtime value is going to be changed.
	 * The default implementation does nothing.
	 * 
	 * @param oldValue
	 *          the previous value.
	 * @param newValue
	 *          the new value.
	 * @throws UnsupportedOperationException
	 *           if dynamic reconfiguration of this configurable is not allowed
	 *           (regardless of the security context).
	 */
	protected void notifyChange(T oldValue, T newValue) throws java.lang.UnsupportedOperationException {
	}

	/**
	 * Returns the string representation of the value of this configurable.
	 * 
	 * @return <code>String.valueOf(this.get())</code>
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	private static Class<?> findContainer() {
		try {
			StackTraceElement[] stack = new Throwable().getStackTrace();
			String className = stack[3].getClassName();
			int sep = className.indexOf("$");
			if (sep >= 0) { // If inner class, remove suffix.
				className = className.substring(0, sep);
			}
			return Class.forName(className); // We use the caller class loader
			// (and
			// avoid dependency to Reflection
			// utility).

		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	public static Configurable<?> getInstance(String name, Object obj) {
		Class<?> cls = null;
		if (obj == null) {
			int sep = name.lastIndexOf('#');
			if (sep < 0) {
				throw new IllegalArgumentException("Object must be specified when name is not qualified");
			}
			String className = name.substring(0, sep);
			name = name.substring(sep + 1);
			try {
				cls = Class.forName(className);
			} catch (ClassNotFoundException e1) {
				logger.warn("Class {} not found", className);
				return null;
			}
		} else {
			cls = obj.getClass();
		}
		Field field = Util.getField(cls, name);
		if (field == null) {
			logger.warn("Configurable {} not found", name);
			return null;
		}
		try {
			return (Configurable<?>) field.get(obj);
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Sets the run-time value of the specified configurable. If the configurable
	 * value is different from the previous one, then {@link #notifyChange} is
	 * called. This method raises a <code>SecurityException</code> if the
	 * specified configurable cannot be {@link SecurityContext#isConfigurable
	 * reconfigured}.
	 * 
	 * @param configurable
	 *          the configurable being configured.
	 * @param object
	 *          the new run-time value.
	 * @throws IllegalArgumentException
	 *           if <code>value</code> is <code>null</code>.
	 * @throws SecurityException
	 *           if the specified configurable cannot be modified.
	 */
	@SuppressWarnings("unchecked")
	public static <T> void configure(Configurable<T> configurable, Object object) throws SecurityException {
		if (object == null) {
			throw new IllegalArgumentException("New value cannot be null");
		}
		Object oldValue = configurable.value;

		if (!object.equals(oldValue)) {
			logger.info("{} set to {}", configurable.getName(), object);
			configurable.value = (T) object;
			configurable.notifyChange((T) oldValue, (T) object);
		}
	}

	/**
	 * Convenience method to read the specified properties and reconfigure
	 * accordingly.
	 * 
	 * @param map
	 *          the properties.
	 */
	public static void configure(Map<?, ?> map) {
		for (Map.Entry<?, ?> e : map.entrySet()) {
			String name = (String) e.getKey();
			Object value = e.getValue();
			Configurable<?> cfg = getInstance(name, null);
			if (cfg != null) {
				Configurable.configure(cfg, Util.coerceType(value, cfg.getDefault().getClass()));
			}

			for (Configurable<?> c : configurables) {
				if (c != cfg && name.equals(c.getName())) {
					Configurable.configure(c, Util.coerceType(value, c.getDefault().getClass()));
				}
			}
		}
		configuration.putAll(map);
	}

	public static void configure(String name, Object value) {
		Configurable<?> cfg = getInstance(name, null);
		if (cfg != null) {
			Configurable.configure(cfg, Util.coerceType(value, cfg.getDefault().getClass()));
		}
		for (Configurable<?> c : configurables) {
			if (c != cfg && name.equals(c.getName())) {
				Configurable.configure(c, Util.coerceType(value, c.getDefault().getClass()));
			}
		}
		configuration.put(name, value);
	}

	public static Object getValue(Object obj, String name) {
		try {
			Collection<Field> fields = Util.getFields(obj.getClass());
			for (Field f : fields) {
				if (Configurable.class.isAssignableFrom(f.getType())) {
					Configurable<?> c = (Configurable<?>) f.get(obj);
					if (c.getName().endsWith(name)) {
						return c.get();
					}
				}
			}
			return null;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static void copyConfigurables(Object src, Object dst) {
		Map<String, Configurable<?>> dstConfigurables = getConfigurables(dst);
		for (Configurable<?> srcConf : getConfigurables(src).values()) {
			if (srcConf.isOptimizable()) {
				Configurable<?> dstConf = dstConfigurables.get(srcConf.getName());
				if (dstConf != null) {
					Configurable.configure(dstConf, srcConf.get());
				}
			}
		}
	}

	public static List<Configurable<?>> getOptimizableConfigurables(Object obj) {
		List<Configurable<?>> configurables = new ArrayList<Configurable<?>>();
		for (Configurable<?> c : getConfigurables(obj).values()) {
			if (c.isOptimizable()) {
				configurables.add(c);
			}
		}
		return configurables;
	}

	public static List<Configurable<?>> getOptimizableConfigurables(Class<?> cls) {
		List<Configurable<?>> configurables = new ArrayList<Configurable<?>>();
		for (Configurable<?> c : getConfigurables(cls).values()) {
			if (c.isOptimizable()) {
				configurables.add(c);
			}
		}
		return configurables;
	}

	public static Map<String, Configurable<?>> getConfigurables(Object obj) {
		try {
			Collection<Field> fields = Util.getFields(obj.getClass());
			Map<String, Configurable<?>> configurables = new TreeMap<String, Configurable<?>>();
			for (Field f : fields) {
				if (Configurable.class.isAssignableFrom(f.getType())) {
					Configurable<?> c = (Configurable<?>) f.get(obj);
					configurables.put(f.getName(), c);
				}
			}
			return configurables;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Map<String, Configurable<?>> getConfigurables(Class<?> cls) {
		try {
			Collection<Field> fields = Util.getFields(cls);
			Map<String, Configurable<?>> configurables = new TreeMap<String, Configurable<?>>();
			for (Field f : fields) {
				if (Configurable.class.isAssignableFrom(f.getType())) {
					Configurable<?> c = (Configurable<?>) f.get(null);
					configurables.put(f.getName(), c);
				}
			}
			return configurables;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Map<String, Object> getConfiguration(Object obj) {
		try {
			Collection<Field> fields = Util.getFields(obj.getClass());
			Map<String, Object> configurables = new TreeMap<String, Object>();
			for (Field f : fields) {
				if (f.getType().isAssignableFrom(Configurable.class)) {
					Configurable<?> c = (Configurable<?>) f.get(obj);
					configurables.put(f.getName(), c.get());
				}
			}
			return configurables;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Map<String, Object> getConfiguration(Class<?> cls) {
		try {
			Collection<Field> fields = Util.getFields(cls);
			Map<String, Object> configurables = new TreeMap<String, Object>();
			for (Field f : fields) {
				if (f.getType().isAssignableFrom(Configurable.class)) {
					Configurable<?> c = (Configurable<?>) f.get(null);
					configurables.put(f.getName(), c.get());
				}
			}
			return configurables;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Properties getConfiguration() {
		return configuration;
	}

	private static Properties loadProperties(String filename, Properties defaults) throws IOException {
		Properties properties = new Properties(defaults);
		InputStream is = null;
		try {
			is = Configurable.class.getResourceAsStream(filename);
			if (is == null) {
				try {
					is = new FileInputStream(filename);
				} catch (Exception e) {
				}
			}
			if (is != null) {
				properties.load(is);
				logger.info("Configurable properties read from {}", filename);
				if (logger.isDebugEnabled()) {
					logger.debug(properties.toString());
				}
			}
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
		return properties;
	}

	private static final String systemPropertyName = "jtrade.config";
	static {
		String filename = null;
		try {
			filename = System.getProperty(systemPropertyName, null);
			if (filename == null) {
				filename = "/jtrade-" + InetAddress.getLocalHost().getHostName().toLowerCase() + ".properties";
			}
			configuration = loadProperties(filename, loadProperties("/jtrade.properties", null));
			configure(configuration);
		} catch (Throwable t) {
			throw new IllegalStateException(String.format("Could not read Configurable properties file from %s: %s", filename, t.getMessage()), t);
		}
	}
}
