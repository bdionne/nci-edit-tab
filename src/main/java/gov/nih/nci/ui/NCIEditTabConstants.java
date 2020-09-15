package gov.nih.nci.ui;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;

import edu.stanford.protege.metaproject.ConfigurationManager;
import edu.stanford.protege.metaproject.api.Operation;
import edu.stanford.protege.metaproject.api.OperationType;
import edu.stanford.protege.metaproject.api.PolicyFactory;

public class NCIEditTabConstants {
	
	private static PolicyFactory factory = ConfigurationManager.getFactory();
	
	public static final String ADD = "Add";
	public static final String EDIT = "Edit";
	public static final String DELETE = "Delete";
	
	public static final String COMPLEX_PROPS = "complex_properties";
	public static final String IMMUTABLE_PROPS = "immutable_properties";
	
	public static final String PROPTABLE_VALUE_COLUMN = "Value";
	
	public static OWLAnnotationProperty DEP_PARENT;
	public static OWLAnnotationProperty DEP_CHILD;
	public static OWLAnnotationProperty DEP_ROLE;
	public static OWLAnnotationProperty DEP_ASSOC;
	public static OWLAnnotationProperty DEP_IN_ROLE;
	public static OWLAnnotationProperty DEP_IN_ASSOC;
	
	public static OWLAnnotationProperty MERGE_SOURCE;
	public static OWLAnnotationProperty MERGE_TARGET;
	public static OWLAnnotationProperty SPLIT_FROM;
	
	public static OWLAnnotationProperty DESIGN_NOTE;
	public static OWLAnnotationProperty EDITOR_NOTE;
	
	public static OWLAnnotationProperty SYN_TYPE;
	public static OWLAnnotationProperty SYN_SOURCE;
	
	public static OWLAnnotationProperty DEF_SOURCE;
	
	public static OWLAnnotationProperty REVIEWER_NAME;
	public static OWLAnnotationProperty REVIEW_DATE;
	
	public static OWLAnnotationProperty CODE_PROP;
	public static OWLAnnotationProperty LABEL_PROP;
	public static OWLAnnotationProperty FULL_SYN;
	public static OWLAnnotationProperty PREF_NAME;
	public static OWLAnnotationProperty DEFINITION;
	
	public static OWLAnnotationProperty DEPR_CONCEPT_STATUS_PROP = null;
	public static String DEPR_CONCEPT_STATUS_VALUE;
	
	public static OWLAnnotationProperty SEMANTIC_TYPE;
	
	public static OWLClass PRE_RETIRE_ROOT;
	public static OWLClass PRE_MERGE_ROOT;

	public static OWLClass RETIRE_ROOT;
	public static OWLClass RETIRE_CONCEPTS_ROOT;
	
	public static final String DEFAULT_SOURCE_NEW_CLASS = "NEWCLASS";
	public static final String DEFAULT_SOURCE_NEW_PROPERTY = "NEWPROPERTY";
	public static final String DEFAULT = "default";
	public static final String DEFAULT_ON_CREATE_CLASS = "default_on_create_class";
	public static final String DEFAULT_ON_EDIT_CLASS = "default_on_edit_class";
	
	// from metaproject, needed to check if user can retire
    public static final Operation RETIRE = factory.getSystemOperation(
            factory.getOperationId("retire"), factory.getName("Retire"),
            factory.getDescription("Accept a proposed retirement of a class"), OperationType.WRITE, Operation.Scope.POLICY);

}
