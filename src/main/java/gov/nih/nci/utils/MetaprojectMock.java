/** This is a simple program that mocks out the metaproject
 *  1. The main method exercises the metaproject API to generate a simple json file
 *  2. deserializes and load the json file for use with EditTab
 *  
 *  This gives us a stub of the metaproject to work with whilst waiting for the
 *  client/server integration
 */

package gov.nih.nci.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetaprojectMock {
	
	
	public MetaprojectMock() {}
	
	public Set<String> getComplexAnnotationProperties() {
		String[] res = new String[] {"DEFINITION", "FULL_SYN", "ALT_DEFINITION", "Go_Annotation", "term"};
		return new HashSet<String>(Arrays.asList(res));
		
	}
	
	public Set<String> getRequiredEntities() {

		String[] res = new String[] { "Premerged_Concepts", "Preretired_Concepts", "Retired_Concept_Current_Year" };
		return new HashSet<String>(Arrays.asList(res));

	}

	public Set<String> getImmutableAnnotationProperties() {
		String[] res = new String[] { "OLD_CHILD", "OLD_PARENT", "OLD_ROLE", "OLD_SOURCE_ROLE", "OLD_ASSOCIATION",
				"OLD_SOURCE_ASSOCIATION", "Concept_Status", "Merge_Source", "Merge_Target", "Merge_Into",
				"Split_From" };
		return new HashSet<String>(Arrays.asList(res));

	}

    

    public Map<String,Set<String>> getRequiredAnnotationsForAnnotation() {
    	HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();
    	// Definition
    	String[] req_deps = new String[] {"provenance", "source"};
    	map.put("DEFINITION", new HashSet<String>(Arrays.asList(req_deps)));
    	// ALT_DEFINITION
    	map.put("ALT_DEFINITION", new HashSet<String>(Arrays.asList(req_deps)));
    	// FULL_SYN
    	req_deps = new String[] {"Term", "term-group", "term-source"};
    	map.put("FULL_SYN", new HashSet<String>(Arrays.asList(req_deps)));
    	// Go_Annotation
    	req_deps = new String[] {"go-term", "xml:lang", "go-id"};
    	map.put("Go_Annotation", new HashSet<String>(Arrays.asList(req_deps)));
    	req_deps = new String[] {"term_type", "term-source"};
    	map.put("term", new HashSet<String>(Arrays.asList(req_deps)));
    	
    	return map;
    	
    }

    public Map<String,Set<String>> getOptionalAnnotationAnnotations() {
    	
    	HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();
    	// DEFINITION
    	String[] opt_deps = new String[] {"Definition_Review_Date", "def-source", "Definition_Reviewer_Name", "attr"};
    	map.put("DEFINITION", new HashSet<String>(Arrays.asList(opt_deps)));
    	// ALT_DEFINITION
    	map.put("ALT_DEFINITION", new HashSet<String>(Arrays.asList(opt_deps)));
    	// FULL_SYN
    	opt_deps = new String[] {"source-code", "xml:lang", "subsource-name"};
    	map.put("FULL_SYN", new HashSet<String>(Arrays.asList(opt_deps)));
    	// Go_Annotation
    	opt_deps = new String[] {"go-evi", "source-date", "go-source"};
    	map.put("Go_Annotation", new HashSet<String>(Arrays.asList(opt_deps)));
    	
    	return map;
    	
    }
	
	

}
