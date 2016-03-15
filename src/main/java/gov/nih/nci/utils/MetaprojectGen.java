package gov.nih.nci.utils;
/** This is a simple program that does two things:
 *  1. exercises the metaproject API to generate a simple json file
 *  2. deserializes and load the json file for use with EditTab
 *  
 *  This gives us a stub of the metaproject to work with whilst waiting for the
 *  client/server integration
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;

import edu.stanford.protege.metaproject.Manager;
import edu.stanford.protege.metaproject.Utils;
import edu.stanford.protege.metaproject.api.Metaproject;
import edu.stanford.protege.metaproject.api.Operation;
import edu.stanford.protege.metaproject.api.OperationId;
import edu.stanford.protege.metaproject.api.OperationType;
import edu.stanford.protege.metaproject.api.Project;
import edu.stanford.protege.metaproject.api.Role;
import edu.stanford.protege.metaproject.api.User;
import edu.stanford.protege.metaproject.api.UserId;
import edu.stanford.protege.metaproject.api.exception.ObjectConversionException;
import edu.stanford.protege.metaproject.impl.MetaprojectImpl;
import edu.stanford.protege.metaproject.serialization.DefaultJsonSerializer;

public class MetaprojectGen {
	
	private static String METAPROJECT_FILE_NAME = "nci-metaproject.txt";
	
	public MetaprojectGen() {}
	
	public static Metaproject getMetaproject() {
		Metaproject proj = null;
		try {
			proj = Manager.loadMetaproject(new File(MetaprojectGen.METAPROJECT_FILE_NAME));
			/*
			FileReader in = new FileReader(MetaprojectGen.METAPROJECT_FILE_NAME);
			BufferedReader reader = new BufferedReader(in);
			String line;
			StringBuilder  stringBuilder = new StringBuilder();
			
			while( ( line = reader.readLine() ) != null ) {
	            stringBuilder.append( line );
	        }
			
			String js = stringBuilder.toString();
			
			Gson gson = new DefaultJsonSerializer().getInstance();
						
			proj = gson.fromJson(js, Metaproject.class);
			
			reader.close();
			*/
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ObjectConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proj;
		
	}

	public static void main(String[] args) {
		
		MetaprojectGen gen = new MetaprojectGen();	
		
		try {		
			
			Metaproject metaproject = new MetaprojectImpl.Builder().createMetaproject();	
			
			User bob = gen.genUser("001", "Bob Dionne", "robert.dionne@nih.gov");
			User alice = gen.genUser("002", "Alice Please", "alice.please@nih.gov");
			User ted = gen.genUser("003", "Ted Tester", "teddy.bear@nih.gov");
			
			
			
			Set<UserId> admins = new HashSet<>();
			admins.add(bob.getId());
			
			Project proj = Utils.getProject(Utils.getProjectId("001"),
					Utils.getName("thesaurus"),
					Utils.getDescription("NCI ontology"),
					Utils.getAddress("parts unknown"),
					bob.getId(), admins);
			
			
			
			metaproject.getUserRegistry().add(bob, alice, ted);
			
			Operation pre_merge = gen.genOp("001", "pre-merge", "can propose two classes for merging");
			Operation merge = gen.genOp("002", "merge", "can merge two classes that have been pre-merged");
			Operation pre_retire = gen.genOp("003", "pre-retire", "can propose a class for retiremet");
			Operation retire = gen.genOp("004", "retire",  "can retire a class that is pre-retired");
			Operation split = gen.genOp("005", "split",  "can split a class into two distinct classes");
			Operation clone = gen.genOp("006", "clone",  "can make a copy of a class with a new name");
			Operation edit = gen.genOp("007", "edit",  "can edit a class");
			
			Set<OperationId> admin_ops = new HashSet<>();
			admin_ops.addAll(Arrays.asList(pre_merge.getId(), 
					merge.getId(), pre_retire.getId(), 
					retire.getId(), split.getId(), clone.getId(),
					edit.getId()));
				
			Role admin = Utils.getRole(Utils.getRoleId("001"),
					Utils.getName("admin"),
					Utils.getDescription("The big cheese"),
					admin_ops);
			
			Set<OperationId> modeler_ops = new HashSet<>();
			modeler_ops.addAll(Arrays.asList(pre_merge.getId(),
					pre_retire.getId(), split.getId(), clone.getId(),
					edit.getId()));
			
			Role workflow = Utils.getRole(Utils.getRoleId("002"),
					Utils.getName("modeler"),
					Utils.getDescription("A workflow modeler"),
					modeler_ops);
			
			Set<OperationId> reviewer_ops = new HashSet<>();
						
			Role reviewer = Utils.getRole(Utils.getRoleId("003"),
					Utils.getName("reviewer"),
					Utils.getDescription("Read only reviewer of content"),
					reviewer_ops);
			
			metaproject.getOperationRegistry().add(pre_merge, merge, pre_retire,
					retire, split, clone, edit);
			
			metaproject.getRoleRegistry().add(admin, workflow, reviewer);
			
			metaproject.getProjectRegistry().add(proj);	
			
			metaproject.getPolicy().add(admin.getId(), proj.getId(), bob.getId());
			metaproject.getPolicy().add(workflow.getId(), proj.getId(), alice.getId());
			metaproject.getPolicy().add(reviewer.getId(), proj.getId(),  ted.getId());
			
			
			
			PrintWriter out = new PrintWriter(MetaprojectGen.METAPROJECT_FILE_NAME);
			Gson gson = new DefaultJsonSerializer().getInstance();
			out.println(gson.toJson(metaproject, Metaproject.class));
			out.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}


	}
	
	User genUser(String id, String name, String email) {
		return Utils.getUser(Utils.getUserId(id),
				Utils.getName(name),
				Utils.getEmailAddress(email));
	}

	Operation genOp(String id, String name, String desc) {
		return Utils.getOperation(Utils.getOperationId(id),
				Utils.getName(name), 
				Utils.getDescription(desc),
				OperationType.ONTOLOGY,
				Optional.empty());
	}

}
