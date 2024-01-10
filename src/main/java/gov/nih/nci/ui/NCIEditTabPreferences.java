package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.protege.search.lucene.tab.ui.OwlEntityComboBox;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;

public class NCIEditTabPreferences {
	private static final Logger logger = LoggerFactory.getLogger(NCIEditTabPreferences.class);

    private static final int ONTOLOGY_IRI_ATTRIBUTE = 0;
    private static final int INDEX_DIRECTORY_ATTRIBUTE = 1;

    public static final String NCIEDITTAB_PREFERENCES_KEY = "NCIEditTabPreferences";

    public static final String BASE_DIR = "BASE_DIR";
    public static final String INDEX_RECORD_KEYS = "INDEX_RECORD_KEYS";
    

    public static final String PREFIX_INDEX_DIR = "ProtegeIndex";

    public static final String FN_SPLIT = "FnSplit";
    public static final String FN_COPY = "FnCopy";
    public static final String FN_MERGE = "FnMerge";
    public static final String FN_DUALEDITS = "FnDualEdits";
    public static final String FN_RETIRE = "FnRetire";
    
    public static final String IMMUTABLE_PROPERTYLIST = "ImmutPropList";
    public static final String COMPLEX_PROPERTYLIST = "ComplexPropList";
    public static final String RETIRE_CONCEPTROOT = "RetireConceptRoot";
    public static final String RETIRE_DESIGNNOTE = "RetireDesignNote";
    public static final String RETIRE_EDITORNOTE = "RetireEditorNote";
    public static final String RETIRE_CONCEPTSTATUS = "RetireConceptStatus";
    public static final String RETIRE_PARENT = "RetireParent";
    public static final String RETIRE_CHILD = "RetireChild";
    public static final String RETIRE_ROLE = "RetireRole";
    public static final String RETIRE_INROLE = "RetireInRole";
    public static final String RETIRE_ASSOC = "RetireAssoc";
    public static final String RETIRE_INASSOC = "RetireInAssoc";
	
    public static final String SPLIT_FROM = "SplitFrom";
    public static final String MERGE_SOURCE = "MergeSource";
    public static final String MERGE_TARGET = "MergeTarget";
    public static final String MERGE_DESIGNNOTE = "MergeDesignNote";
    public static final String MERGE_EDITORNOTE = "MergeEditorNote";

    public static final String PROTEGE_DIR = ".protege";

    public static final String COLLECTOR_DIR = "nciedittab";

    private static String fileSystemSeparator = System.getProperty("file.separator");

    private static List<OWLEntity> immutablePropList = new ArrayList<OWLEntity>();
    
    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(NCIEDITTAB_PREFERENCES_KEY);
    }

    /**
     * Gets the system temporary directory path.
     *
     * @return Returns the system temporary directory path.
     */
    @Nonnull
    public static String getTempDirectory() {
        return normalizePathName(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Gets the user home directory path.
     *
     * @return Returns the user home directory path.
     */
    @Nonnull
    public static String getNCIEditTabBaseDirectory() {
    	return PROTEGE_DIR + fileSystemSeparator + COLLECTOR_DIR;
    }

    public static boolean getFnSplit() {
        return getPreferences().getBoolean(FN_SPLIT, true);
    }

    public static void setFnSplit(boolean isSelected) {
        getPreferences().putBoolean(FN_SPLIT, isSelected);
    }

    public static boolean getFnCopy() {
        return getPreferences().getBoolean(FN_COPY, true);
    }

    public static void setFnCopy(boolean isSelected) {
        getPreferences().putBoolean(FN_COPY, isSelected);
    }
    
    public static boolean getFnMerge() {
        return getPreferences().getBoolean(FN_MERGE, true);
    }

    public static void setFnMerge(boolean isSelected) {
        getPreferences().putBoolean(FN_MERGE, isSelected);
    }
    
    public static boolean getFnDualEdits() {
        return getPreferences().getBoolean(FN_DUALEDITS, true);
    }

    public static void setFnDualEdits(boolean isSelected) {
        getPreferences().putBoolean(FN_DUALEDITS, isSelected);
    }
    
    public static boolean getFnRetire() {
        return getPreferences().getBoolean(FN_RETIRE, true);
    }

    public static void setFnRetire(boolean isSelected) {
        getPreferences().putBoolean(FN_RETIRE, isSelected);
    }
    
    //
    public static List<String> getImmutPropList() {
    	return getPreferences().getStringList(IMMUTABLE_PROPERTYLIST, new ArrayList<String>());
    	//return getPreferences().getString(IMMUTABLE_PROPERTYLIST, "");
    }

    public static void setImmutPropList(List<String> iplist) {
    	getPreferences().putStringList(IMMUTABLE_PROPERTYLIST, iplist);
        //getPreferences().putString(IMMUTABLE_PROPERTYLIST, ipl);
    }
    
    /*public static void setImmutProps(List<OWLEntity> iplist){
    	immutablePropList = iplist;
        writeImmutProps();
    }
    
    public static List<OWLEntity> getImmutPropList(){
        //return new ArrayList<>(immutablePropIRIs);
    	return immutablePropList;
    }*/
    
    /*private static void loadImmutProps() {
        final List<String> defaultValues = Collections.emptyList();
        List<String> values = getPreferences().getStringList(IMMUTABLE_PROPERTYLIST, defaultValues);

        if (values.equals(defaultValues)){
            
        }
        else{
            for (String value : values){
                try {
                    IRI iri = IRI.create(new URI(value.trim()));
                    immutablePropList.add(new OWLiri);
                }
                catch (URISyntaxException e) {
                    ErrorLogPanel.showErrorDialog(e);
                }
            }
        }
    }*/
    
    /*private static void writeImmutProps() {
        
        //for (String str : immutablePropList){
            //StringBuilder str = new StringBuilder(obj.toString());
            //str.append(langStringBuilder.toString());
            //values.add(str);
        //}
        //getPreferences().putStringList(IMMUTABLE_PROPERTYLIST, values);
        
        List<String> values = new ArrayList<>();
        StringBuilder langStringBuilder = new StringBuilder();
        
        for (IRI iri : immutablePropIRIs){
            StringBuilder str = new StringBuilder(iri.toString());
            str.append(langStringBuilder.toString());
            values.add(str.toString());
        }
        getPreferences().putStringList(IMMUTABLE_PROPERTYLIST, values);
    }*/
    
    public static List<String> getComplexPropList() {
    	return getPreferences().getStringList(COMPLEX_PROPERTYLIST, new ArrayList<String>());
    	//return getPreferences().getString(COMPLEX_PROPERTYLIST, "");
    }

    public static void setComplexPropList(List<String> cplist) {
    	getPreferences().putStringList(COMPLEX_PROPERTYLIST, cplist);
        //getPreferences().putString(COMPLEX_PROPERTYLIST, cpl);
    }
    
    public static List<String> getComplexPropAnnotationList(String annot) {
    	StringBuilder strBld = new StringBuilder();
    	strBld.append(COMPLEX_PROPERTYLIST);
    	strBld.append("/");
    	strBld.append(annot);
    	return getPreferences().getStringList(strBld.toString(), new ArrayList<String>());
    }

    public static void setComplexPropAnnotationList(String annot, List<String> cplist) {
    	StringBuilder strBld = new StringBuilder();
    	strBld.append(COMPLEX_PROPERTYLIST);
    	strBld.append("/");
    	strBld.append(annot);
    	getPreferences().putStringList(strBld.toString(), cplist);
    }
    
    public static String getRetireConceptRoot() {
    	return getPreferences().getString(RETIRE_CONCEPTROOT, "");
    }

    public static void setRetireConceptRoot(String rcr) {
        getPreferences().putString(RETIRE_CONCEPTROOT, rcr);
    }
    
    public static String getRetireDesignNote() {
    	return getPreferences().getString(RETIRE_DESIGNNOTE, "");
    }
    
    public static void setRetireDesignNote(String rdn) {
        getPreferences().putString(RETIRE_DESIGNNOTE, rdn);
    }
    
    public static String getRetireEditorNote() {
    	return getPreferences().getString(RETIRE_EDITORNOTE, "");
    }

    public static void setRetireEditorNote(String ren) {
        getPreferences().putString(RETIRE_EDITORNOTE, ren);
    }
    
    public static String getRetireConceptStatus() {
    	return getPreferences().getString(RETIRE_CONCEPTSTATUS, "");
    }

    public static void setRetireConceptStatus(String rcs) {
        getPreferences().putString(RETIRE_CONCEPTSTATUS, rcs);
    }
    
    public static String getRetireParent() {
    	return getPreferences().getString(RETIRE_PARENT, "");
    }

    public static void setRetireParent(String rp) {
        getPreferences().putString(RETIRE_PARENT, rp);
    }
    
    public static String getRetireChild() {
    	return getPreferences().getString(RETIRE_CHILD, "");
    }

    public static void setRetireChild(String rc) {
        getPreferences().putString(RETIRE_CHILD, rc);
    }
    
    public static String getRetireRole() {
    	return getPreferences().getString(RETIRE_ROLE, "");
    }

    public static void setRetireRole(String rr) {
        getPreferences().putString(RETIRE_ROLE, rr);
    }
    
    public static String getRetireInRole() {
    	return getPreferences().getString(RETIRE_INROLE, "");
    }

    public static void setRetireInRole(String rir) {
        getPreferences().putString(RETIRE_INROLE, rir);
    }
    
    public static String getRetireAssoc() {
    	return getPreferences().getString(RETIRE_ASSOC, "");
    }

    public static void setRetireAssoc(String ra) {
        getPreferences().putString(RETIRE_ASSOC, ra);
    }
    
    public static String getRetireInAssoc() {
    	return getPreferences().getString(RETIRE_INASSOC, "");
    }

    public static void setRetireInAssoc(String ria) {
        getPreferences().putString(RETIRE_INASSOC, ria);
    }
    
    public static String getSplitFrom() {
    	return getPreferences().getString(SPLIT_FROM, "");
    }

    public static void setSplitFrom(String sf) {
        getPreferences().putString(SPLIT_FROM, sf);
    }
    
    public static String getMergeSource() {
    	return getPreferences().getString(MERGE_SOURCE, "");
    }

    public static void setMergeSource(String ms) {
        getPreferences().putString(MERGE_SOURCE, ms);
    }
    
    public static String getMergeTarget() {
    	return getPreferences().getString(MERGE_TARGET, "");
    }

    public static void setMergeTarget(String mt) {
        getPreferences().putString(MERGE_TARGET, mt);
    }
    
    public static String getMergeDesignNote() {
    	return getPreferences().getString(MERGE_DESIGNNOTE, "");
    }

    public static void setMergeDesignNote(String mdn) {
        getPreferences().putString(MERGE_DESIGNNOTE, mdn);
    }
    
    public static String getMergeEditorNote() {
    	return getPreferences().getString(MERGE_EDITORNOTE, "");
    }

    public static void setMergeEditorNote(String men) {
        getPreferences().putString(MERGE_EDITORNOTE, men);
    }
    
    /**
     * Gets the base directory to store the index files. By default, the base
     * directory is the user home directory.
     *
     * @return Returns the base directory location.
     */
    @Nonnull
    public static String getBaseDirectory() {
        return getPreferences().getString(BASE_DIR, getNCIEditTabBaseDirectory());
    }

    /**
     * Sets the base directory to store the index files.
     *
     * @param baseDirectory
     *          A directory location set by the user.
     */
    public static void setBaseDirectory(@Nonnull String baseDirectory) {
        checkNotNull(baseDirectory);
        getPreferences().putString(BASE_DIR, normalizePathName(baseDirectory));
    }

    /**
     * Adds an index record to the index preference given the input ontology.
     * The record will contain the information about the ontology IRI, the index
     * directory location and the index checksum. This method will do nothing if
     * the input is an anonymous ontology.
     *
     * @param ontology
     *            The input ontology to create the index record
     */
    public static void addIndexRecord(String indexDirId) {

    	final String indexRecordKey = createIndexRecordKey(indexDirId);
    	getPreferences().putStringList(indexRecordKey, createIndexAttributes(indexDirId));
    	collectIndexRecordKey(indexRecordKey);

    }

    /**
     * Removes the index record given the ontology IRI. This method will also
     * remove the index directory from the file system.
     *
     * @param indexDirId
     *          The directory id for the index
     */
    public static void removeIndexRecord(String indexDirId) {
        
        final String indexRecordKey = createIndexRecordKey(indexDirId);
        deleteIndexDirectory(indexRecordKey);
        getPreferences().putString(indexRecordKey, null);
        discardIndexRecordKey(indexRecordKey);
    }

    /**
     * Checks if the index preference contains the index record for the given
     * input ontology. When the method finds an index record associated with
     * the input ontology, it will check if the index directory location is
     * still valid. The method will check as well if the index checksum agrees
     * with the input ontology such that the index record is still valid.
     * 
     * This method will always return <code>false</code> if the input is an
     * anonymous ontology.
     *
     * @param indexDirId
     *          The unique id that neames the directory where the index is stored
     * @return Returns <code>true</code> if the index record is still relevant
     * with the given input ontology, or <code>false</code> otherwise.
     */
    public static boolean containsIndexRecord(String indexDirId) {
        
        boolean doesContain = true;
        String indexRecordKey = createIndexRecordKey(indexDirId);
        Optional<List<String>> indexRecord = getIndexRecord(indexRecordKey);
        if (indexRecord.isPresent()) {
            String directoryLocation = getIndexDirectoryLocation(indexDirId);
            if (doesDirectoryExist(directoryLocation)) {
            	
            } else {
                doesContain = false;
            }
        } else {
            doesContain = false;
        }
        return doesContain;
    }

    private static boolean doesDirectoryExist(String directoryLocation) {
        if (directoryLocation == null) {
            return false;
        }
        if (directoryLocation.isEmpty()) {
            return false;
        }
        return new File(directoryLocation).exists();
    }

    /**
     * Returns the index record table. The table consists of the record key, the ontology IRI,
     * the index directory location and the index checksum.
     *
     * @return A index record table.
     */
    @Nonnull
    public static List<List<String>> getIndexRecordTable() {
        List<List<String>> indexRecordTable = new ArrayList<>();
        List<String> indexMapKeys = getPreferences().getStringList(INDEX_RECORD_KEYS, new ArrayList<>());
        for (String indexMapKey : indexMapKeys) {
            List<String> indexRecord = new ArrayList<>();
            indexRecord.add(indexMapKey);
            indexRecord.addAll(getCheckedIndexRecord(indexMapKey));
            indexRecordTable.add(indexRecord);
        }
        return indexRecordTable;
    }

    /**
     * Gets the index directory path given the input ontology IRI.
     *
     * @param indexDirId
     *          The unique integer id that identifies the index directory path.
     * @return A full path location of the index directory
     */
    @Nonnull
    public static String getIndexDirectoryLocation(String indexDirId) {
        String indexMapKey = createIndexRecordKey(indexDirId);
        List<String> indexAttributes = getCheckedIndexRecord(indexMapKey);
        return indexAttributes.get(INDEX_DIRECTORY_ATTRIBUTE);
    }

    /**
     * Clears all ontology versions and the associated index location from the preference.
     * Note that no physical files or folders are deleted from the file system.
     */
    public static void clear() {
        Preferences preferences = getPreferences();
        preferences.clear();
        preferences.putString(BASE_DIR, getBaseDirectory());
    }

    /*
     * Private utility methods
     */

    private static Optional<List<String>> getIndexRecord(String indexRecordKey) {
        return Optional.ofNullable(getPreferences().getStringList(indexRecordKey, null));
    }

    private static List<String> getCheckedIndexRecord(String indexRecordKey) {
        Optional<List<String>> uncheckedIndexRecord = getIndexRecord(indexRecordKey);
        List<String> indexRecord = new ArrayList<>();
        if (!uncheckedIndexRecord.isPresent()) {
            indexRecord.add(ONTOLOGY_IRI_ATTRIBUTE, "");
            indexRecord.add(INDEX_DIRECTORY_ATTRIBUTE, "");
        } else {
            indexRecord.addAll(uncheckedIndexRecord.get());
        }
        return indexRecord;
    }

    private static String createIndexRecordKey(String id) {
        return "KEY:" + id;
    }

    private static List<String> createIndexAttributes(String indexDirId) {
        List<String> indexAttributes = new ArrayList<>();
        indexAttributes.add(ONTOLOGY_IRI_ATTRIBUTE, indexDirId);
        indexAttributes.add(INDEX_DIRECTORY_ATTRIBUTE, createIndexDirectoryLocation(indexDirId));
        return indexAttributes;
    }

    private static String fetchOntologyIri(OWLOntology ontology) {
        return ontology.getOntologyID().getOntologyIRI().get().toString();
    }

    private static String createIndexDirectoryLocation(String indexDirId) {
        StringBuffer directoryLocation = new StringBuffer();
        directoryLocation.append(getBaseDirectory());
        directoryLocation.append(fileSystemSeparator);
        directoryLocation.append(createDirectoryName(indexDirId));
        return directoryLocation.toString();
    }

    private static void collectIndexRecordKey(String indexMapKey) {
        List<String> indexRecordKeys = getPreferences().getStringList(
                INDEX_RECORD_KEYS,
                new ArrayList<>());
        if (!indexRecordKeys.contains(indexMapKey)) { // to prevent duplication in the list
            indexRecordKeys.add(indexMapKey);
        }
        getPreferences().putStringList(INDEX_RECORD_KEYS, indexRecordKeys);
    }

    private static void discardIndexRecordKey(String indexMapKey) {
        List<String> indexMapKeys = getPreferences().getStringList(
                INDEX_RECORD_KEYS,
                new ArrayList<>());
        indexMapKeys.remove(indexMapKey);
        getPreferences().putStringList(INDEX_RECORD_KEYS, indexMapKeys);
    }

    private static String createDirectoryName(String indexDirId) {
        String ontologyIdHex = indexDirId;
        String timestampHex = Integer.toHexString(new Date().hashCode());
        return String.format("%s-%s-%s", PREFIX_INDEX_DIR, ontologyIdHex, timestampHex);
    }

    private static void deleteIndexDirectory(String indexMapKey) {
        List<String> indexAttributes = getCheckedIndexRecord(indexMapKey);
        String directoryLocation = indexAttributes.get(INDEX_DIRECTORY_ATTRIBUTE);
        try {
            File indexDirectory = new File(directoryLocation);
            if (indexDirectory.exists()) {
                FileUtils.deleteDirectory(indexDirectory);
            }
        } catch (IOException e) {
            logger.error("Error while removing index directory: " + directoryLocation, e);
        }
    }

    /**
     * Make sure the path directory contains no file separator at the end of the string
     *
     * @param directory
     *          input path directory
     * @return clean path directory.
     */
    private static String normalizePathName(String directory) {
        if (directory.endsWith(fileSystemSeparator)) {
            directory = directory.substring(0, directory.length()-1);
        }
        return directory;
    }

}
