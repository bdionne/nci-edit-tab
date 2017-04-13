package gov.nih.nci.utils.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import org.apache.log4j.Logger;

public class FilePrefs extends AbstractPreferences {
	 private static final Logger log = Logger.getLogger(FilePrefs.class);
	 
	  private Map<String, String> root;
	  private Map<String, FilePrefs> children;
	  private List<String> children_names;
	  private boolean isRemoved = false;
	 
	  public FilePrefs(AbstractPreferences parent, String name)
	  {
	    super(parent, name);
	 
	    log.info("Instantiating node " + name);
	 
	    root = new TreeMap<String, String>();
	    children = new TreeMap<String, FilePrefs>();
	    children_names = new ArrayList<String>();
	 
	    try {
	      sync();
	    }
	    catch (BackingStoreException e) {
	      log.error("Unable to sync on creation of node " + name, e);
	    }
	  }
	 
	  protected void putSpi(String key, String value)
	  {
	    root.put(key, value);
	    try {
	      flush();
	    }
	    catch (BackingStoreException e) {
	      log.error("Unable to flush after putting " + key, e);
	    }
	  }
	 
	  protected String getSpi(String key)
	  {
	    return root.get(key);
	  }
	 
	  protected void removeSpi(String key)
	  {
	    root.remove(key);
	    try {
	      flush();
	    }
	    catch (BackingStoreException e) {
	      log.error("Unable to flush after removing " + key, e);
	    }
	  }
	 
	  protected void removeNodeSpi() throws BackingStoreException
	  {
	    isRemoved = true;
	    flush();
	  }
	 
	  protected String[] keysSpi() throws BackingStoreException
	  {
	    return root.keySet().toArray(new String[root.keySet().size()]);
	  }
	 
	  protected String[] childrenNamesSpi() throws BackingStoreException
	  {
		  return children_names.toArray(new String[children_names.size()]);
	    //return children.keySet().toArray(new String[children.keySet().size()]);
	  }
	 
	  protected FilePrefs childSpi(String name)
	  {
	    FilePrefs child = children.get(name);
	    if (child == null || child.isRemoved()) {
	      child = new FilePrefs(this, name);
	      children.put(name, child);
	    }
	    return child;
	  }
	 
	 
	  protected synchronized void syncSpi() throws BackingStoreException
	  {
	    if (isRemoved()) return;
	 
	    final File file = FileBackingStorePrefsFactory.getPreferencesFile();
	 
	    if (!file.exists()) return;
	 
	    //synchronized (file) {
	      Properties p = new Properties();
	      try {
	        p.load(new FileInputStream(file));
	 
	        StringBuilder sb = new StringBuilder();
	        getPath(sb);
	        String path = sb.toString();
	 
	        final Enumeration<?> pnen = p.propertyNames();
	        while (pnen.hasMoreElements()) {
	          String propKey = (String) pnen.nextElement();
	          if (propKey.startsWith(path)) {
	            String subKey = propKey.substring(path.length());
	            // Only load immediate descendants
	            if (subKey.indexOf('/') == -1) {
	              root.put(subKey, p.getProperty(propKey));
	            } else {
	            	children_names.add(subKey.substring(0,subKey.indexOf('/')));
	            }
	          }
	        }
	      }
	      catch (IOException e) {
	        throw new BackingStoreException(e);
	      }
	    }
	  //}
	 
	  private void getPath(StringBuilder sb)
	  {
	    final FilePrefs parent = (FilePrefs) parent();
	    if (parent == null) return;
	 
	    parent.getPath(sb);
	    sb.append(name()).append('/');
	  }
	 
	  protected synchronized void flushSpi() throws BackingStoreException
	  {
	    final File file = FileBackingStorePrefsFactory.getPreferencesFile();
	 
	    //synchronized (file) {
	      Properties p = new Properties();
	      try {
	 
	        StringBuilder sb = new StringBuilder();
	        getPath(sb);
	        String path = sb.toString();
	 
	        if (file.exists()) {
	          p.load(new FileInputStream(file));
	 
	          List<String> toRemove = new ArrayList<String>();
	 
	          // Make a list of all direct children of this node to be removed
	          final Enumeration<?> pnen = p.propertyNames();
	          while (pnen.hasMoreElements()) {
	            String propKey = (String) pnen.nextElement();
	            if (propKey.startsWith(path)) {
	              String subKey = propKey.substring(path.length());
	              // Only do immediate descendants
	              if (subKey.indexOf('/') == -1) {
	                toRemove.add(propKey);
	              }
	            }
	          }
	 
	          // Remove them now that the enumeration is done with
	          for (String propKey : toRemove) {
	            p.remove(propKey);
	          }
	        }
	 
	        // If this node hasn't been removed, add back in any values
	        if (!isRemoved) {
	          for (String s : root.keySet()) {
	            p.setProperty(path + s, root.get(s));
	          }
	        }
	 
	        p.store(new FileOutputStream(file), "FilePreferences");
	      }
	      catch (IOException e) {
	        throw new BackingStoreException(e);
	      }
	    //}
	  }

}
