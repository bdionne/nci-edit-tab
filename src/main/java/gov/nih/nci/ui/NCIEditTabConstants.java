package gov.nih.nci.ui;

import edu.stanford.protege.metaproject.Manager;
import edu.stanford.protege.metaproject.api.MetaprojectFactory;
import edu.stanford.protege.metaproject.api.Operation;
import edu.stanford.protege.metaproject.api.OperationType;

public class NCIEditTabConstants {
	
	private static MetaprojectFactory factory = Manager.getFactory();
	
	public static final String ADD = "Add";
	public static final String EDIT = "Edit";
	public static final String DELETE = "Delete";
	
	public static final Operation MERGE = factory.getSystemOperation(
            factory.getOperationId("merge"), factory.getName("Merge"),
            factory.getDescription("Accept a proposed merge of two classes"), OperationType.WRITE, Operation.Scope.METAPROJECT);
    
    public static final Operation SPLIT = factory.getSystemOperation(
            factory.getOperationId("split"), factory.getName("Split"),
            factory.getDescription("Split two classes"), OperationType.WRITE, Operation.Scope.METAPROJECT);
    
    public static final Operation CLONE = factory.getSystemOperation(
            factory.getOperationId("clone"), factory.getName("Clone"),
            factory.getDescription("Clone an existing class"), OperationType.WRITE, Operation.Scope.METAPROJECT);
    
    public static final Operation PRE_RETIRE = factory.getSystemOperation(
            factory.getOperationId("pre-retire"), factory.getName("Pre-retire"),
            factory.getDescription("Propose the retirement of a class"), OperationType.WRITE, Operation.Scope.METAPROJECT);
    
    public static final Operation RETIRE = factory.getSystemOperation(
            factory.getOperationId("retire"), factory.getName("Retire"),
            factory.getDescription("Accept a proposed retirement of a class"), OperationType.WRITE, Operation.Scope.METAPROJECT);

}
