package gov.nih.nci.utils.batch;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public class ComplexPropProcessor extends EditProcessor {

	public ComplexPropProcessor(NCIEditTab t) {
		super(t);
		// TODO Auto-generated constructor stub
	}
	
	public Vector<String> validateData(Vector<String> v) {
		return super.validateData(v);
		
/**
		try {

			String attribute = (String) v.elementAt(2);
			String attributename = (String) v.elementAt(3);
			String attributevalue_1 = (String) v.elementAt(4);
			String attributevalue_2 = (String) v.elementAt(5);

			

			Vector superclasses = new Vector();
			
			


			if (operation.compareToIgnoreCase("new") == 0) {
				
				if (hostClass != null) {
					if (attribute.compareToIgnoreCase("role") == 0) {
					
						if (!supportedRoles.contains(attributename)) {
							String error_msg = " -- role " + attributename
									+ " is not identifiable.";
							w.add(error_msg);
						} else {
							int pos = attributevalue_1.indexOf("|");
							if (pos == -1) {
								String error_msg = " -- missing modifier or filler.";
								w.add(error_msg);
							} else {
								String filler = attributevalue_1.substring(
										pos + 1, attributevalue_1.length());
								
								OWLClass targetClass = tab.getClass(filler);
										
								if (targetClass == null) {
									String error_msg = " -- concept " + filler
											+ " does not exist.";
									w.add(error_msg);
								} else {
									
									if (hasRole(hostClass,
											attributename, targetClass)) {
										String error_msg = " -- role already exists.";
										w.add(error_msg);
									}
									

								}
								
							}
						}
						
					}

					else if (attribute.compareToIgnoreCase("parent") == 0) {
						
						OWLClass superClass = tab.getClass(attributename);
						if (superClass == null) {
							String error_msg = " -- superconcept does not exist.";
							w.add(error_msg);

						} else {

							if (tab.isPreMerged(superClass)
									|| tab.isPreRetired(superClass)
									|| tab.isRetired(superClass)) {
								String error_msg = "superconcept cannot be premerged, preretired, or retired.";
								w.add(error_msg);

							}

						}
						

					}

					
						
					} else if (attribute.compareToIgnoreCase("association") == 0) {
						if (!supportedAssociations.contains(attributename)) {
							String error_msg = " -- association "
									+ attributename + " is not identifiable.";
							w.add(error_msg);
						} else {
							
							OWLClass targetClass = tab.getClass(attributevalue_1);
							
							if (targetClass == null) {
								
								String error_msg = " -- concept "
										+ attributevalue_1 + " does not exist.";
								w.add(error_msg);
								
							} else {
								if (hasAssociation(hostClass,
										attributename, targetClass)) {
									String error_msg = " -- association already exists.";
									w.add(error_msg);
								}
							}
							
						}
					}
				}
				
			}

			else if (operation.compareToIgnoreCase("edit") == 0
					|| operation.compareToIgnoreCase("delete") == 0) {
				if (hostClass != null) {
					if (attribute.compareToIgnoreCase("parent") == 0) {
						if (operation.compareToIgnoreCase("delete") == 0) {
							
							OWLClass superClass = tab.getClass(attributename);
							if (superClass == null) {
								String error_msg = " -- superconcept "
										+ attributename + " does not exist.";
								w.add(error_msg);
							} else if (tab.getDirectSuperClasses(
									hostClass).size() == 1) {
								String error_msg = " -- can't delete last superconcept "
										+ attributename;
								w.add(error_msg);

							}
							

						} else {
							String error_msg = " -- edit parent action is not supported. Use delete and add actions instead.";
							w.add(error_msg);
						}
					}

					else if (attribute.compareTo("role") == 0) {
						if (!supportedRoles.contains(attributename)) {
							String error_msg = " -- role " + attributename
									+ " is not identifiable.";
							w.add(error_msg);
						} else {
							int pos = attributevalue_1.indexOf("|");
							if (pos == -1) {
								String error_msg = " -- missing modifier or filler.";
								w.add(error_msg);
							} else {
								String filler = attributevalue_1.substring(
										pos + 1, attributevalue_1.length());
								
								OWLClass targetClass = tab.getClass(filler);
								
								if (targetClass == null) {
									String error_msg = " -- concept " + filler
											+ " does not exist.";
									w.add(error_msg);
								} else {
									if (!hasRole(hostClass,
											attributename, targetClass)) {
										String error_msg = " -- role does not exist.";
										w.add(error_msg);

									}

									if (operation.compareTo("edit") == 0) {
										pos = attributevalue_2.indexOf("|");
										if (pos == -1) {
											String error_msg = " -- missing modifier or filler.";
											w.add(error_msg);
										} else {
											filler = attributevalue_2
													.substring(pos + 1,
															attributevalue_2
																	.length());

											targetClass = tab.getClass(filler);
											if (targetClass == null) {
												String error_msg = " -- concept "
														+ filler
														+ " does not exist.";
												w.add(error_msg);
											}
										}
									}
								}
								
							}
						}
					} 
					} else if (attribute.compareTo("association") == 0) {
						if (!supportedAssociations.contains(attributename)) {
							String error_msg = " -- association "
									+ attributename + " is not identifiable.";
							w.add(error_msg);
						}

						if (operation.compareToIgnoreCase("delete") == 0) {
							
							OWLClass targetClass = tab.getClass(attributevalue_1);
							if (targetClass == null) {
								String error_msg = " -- concept "
										+ attributevalue_1 + " does not exist.";
								w.add(error_msg);
							} else {
								if (!hasAssociation(hostClass,
										attributename, targetClass)) {
									String error_msg = " -- association does not exist.";
									w.add(error_msg);
								}
							}
							
						} else {
							String error_msg = " -- edit association action is not supported. Use delete and add actions instead.";
							w.add(error_msg);
						}
					}
				}				
			}
		} catch (Exception e) {
			w.add("Exception caught" + e.toString());
		}

		return w;
	}
	**/
	}
	
	public boolean processData(Vector<String> w) {
		return true;
	}

}
