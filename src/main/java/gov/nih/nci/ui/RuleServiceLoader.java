package gov.nih.nci.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

import gov.nih.nci.api.RuleService;

public class RuleServiceLoader {

	private static final Map<String, RuleService> ruleServiceMap = new LinkedHashMap<>();
	
	/**
	 * Read in configuration data that maps names of interfaces to names of
	 * corresponding concrete implementation classes. Called early upon startup,
	 * before any implementations are needed by the rest of the program.
	 * 
	 * <P>
	 * Example of a possible entry in such a config file (where package names have
	 * been added): myapp.TimeSource = myapp.TimeSourceOneDayAdvance
	 * 
	 * @param config map-key is the fully-qualified interface name, map-value is the
	 *               fully-qualified name of a corresponding concrete implementation
	 *               class, having a no-argument constructor. The caller decides
	 *               where this data comes from. It may be a simple text file, a
	 *               database, etc.
	 */
	public static void init() {

		ServiceLoader<RuleService> loader = ServiceLoader.load(RuleService.class);
		for (RuleService plugin : loader) {
		    ruleServiceMap.put(RuleService.class.getName(), plugin);
		}
	}

	/**
	 * Return an object that implements the given interface. If the given interface
	 * has no known mapping defined by the config, or if the instance cannot be
	 * created, then an exception is thrown.
	 * 
	 * Example of getting an instance that implements the TimeSoure interface:
	 * <code>TimeSource ts = PluginFactory.instanceOf(TimeSource.class);</code>
	 * 
	 * @param aInterface the class object representing the interface.
	 */
	public static <T> T instanceOf(Class<T> aService)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {
		T result = null;
		for (String serviceName : ruleServiceMap.keySet()) {
			if (serviceName.equals(aService.getName())) {
				Class<? extends T> ruleServiceImplClass = (Class<? extends T>) ruleServiceMap.get(serviceName).getClass();
				result = ruleServiceImplClass.getDeclaredConstructor().newInstance();
			}
		}
		if (result == null) {
			throw new InstantiationException("The interface " + aService.getName() + " has no mapping to an impl.");
		}
		return result;
	}
}
