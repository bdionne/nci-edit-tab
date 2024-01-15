package gov.nih.nci.utils;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.protege.metaproject.ProjectOptionsManager;
import edu.stanford.protege.metaproject.api.ProjectOptions;

public class ProjectOptionsConfigManager {
	private static final Logger logger = LoggerFactory.getLogger(ProjectOptionsConfigManager.class);
	
	public static void saveProjectOptionsFile(ProjectOptions projOptions, String filePath) {
        try {
            //String projOptionLocation = "project-options.json";
            /*if (Strings.isNullOrEmpty(projOptionLocation)) {
            	throw new RuntimeException("Config property " + HTTPServer.SERVER_CONFIGURATION_PROPERTY + " isn't set");
						}*/
            File projOptionsFile = new File(filePath);
            //ConfigurationManager.getConfigurationWriter().saveConfiguration(configuration, configurationFile);
            ProjectOptionsManager.getProjectOptionsWriter().saveProjectOptions(projOptions, projOptionsFile);
        }
        catch (IOException e) {
            String message = "Unable to save project options";
            logger.error("Save project options error: ", e);
            //throw new Exception(message, e);
        }
    }
}
