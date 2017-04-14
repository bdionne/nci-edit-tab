package gov.nih.nci.utils.prefs;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;


import org.apache.log4j.Logger;




public class FileBackingStorePrefsFactory implements PreferencesFactory {
	
	private static final Logger log = Logger.getLogger(FileBackingStorePrefsFactory.class);
	 
	  Preferences rootPreferences;
	  public static final String SYSTEM_PROPERTY_FILE =
	    "gov.nih.nci.utils.prefs.PrefsFactory.file";
	 
	  public Preferences systemRoot()
	  {
	    return userRoot();
	  }
	 
	  public synchronized Preferences userRoot()
	  {
	    if (rootPreferences == null) {
	      log.info("Instantiating root preferences");
	 
	      rootPreferences = new FilePrefs(null, "");
	    }
	    return rootPreferences;
	  }
	 
	  private static File preferencesFile;
	 
	  public static synchronized File getPreferencesFile()
	  {
	    if (preferencesFile == null) {
	      String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);
	      if (prefsFile == null || prefsFile.length() == 0) {
	        prefsFile = ".fileprefs";
	      }
	      preferencesFile = new File(prefsFile).getAbsoluteFile();
	      log.info("Preferences file is " + preferencesFile);
	    }
	    return preferencesFile;
	  }


}
