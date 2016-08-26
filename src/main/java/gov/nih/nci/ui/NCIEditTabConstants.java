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
	
	public static OWLAnnotationProperty CODE_PROP;
	public static OWLAnnotationProperty LABEL_PROP;
	public static OWLAnnotationProperty PREF_NAME;
	
	public static OWLClass PRE_RETIRE_ROOT;
	public static OWLClass PRE_MERGE_ROOT;
	public static OWLClass RETIRE_ROOT;
	
	
	
	public static final Operation MERGE = factory.getSystemOperation(
            factory.getOperationId("merge"), factory.getName("Merge"),
            factory.getDescription("Accept a proposed merge of two classes"), OperationType.WRITE, Operation.Scope.POLICY);
    
    public static final Operation SPLIT = factory.getSystemOperation(
            factory.getOperationId("split"), factory.getName("Split"),
            factory.getDescription("Split two classes"), OperationType.WRITE, Operation.Scope.POLICY);
    
    public static final Operation CLONE = factory.getSystemOperation(
            factory.getOperationId("clone"), factory.getName("Clone"),
            factory.getDescription("Clone an existing class"), OperationType.WRITE, Operation.Scope.POLICY);
    
    public static final Operation PRE_RETIRE = factory.getSystemOperation(
            factory.getOperationId("pre-retire"), factory.getName("Pre-retire"),
            factory.getDescription("Propose the retirement of a class"), OperationType.WRITE, Operation.Scope.POLICY);
    
    public static final Operation RETIRE = factory.getSystemOperation(
            factory.getOperationId("retire"), factory.getName("Retire"),
            factory.getDescription("Accept a proposed retirement of a class"), OperationType.WRITE, Operation.Scope.POLICY);

}
