package gov.nih.nci.utils.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLAnnotation;

import gov.nih.nci.ui.NCIEditTab;

public class ComplexPropProcessor extends EditProcessor {
	
	private List<OWLAnnotation> qualifiers = new ArrayList<OWLAnnotation>();
	private List<OWLAnnotation> new_qualifiers = new ArrayList<OWLAnnotation>();

	public ComplexPropProcessor(NCIEditTab t) {
		super(t);
	}
	
	public Vector<String> validateData(Vector<String> v) {
		
		Vector<String> w = super.validateData(v);
		
		if (classToEdit != null) {
			try {
				
				// in all cases we need the prop id
				String prop_iri = (String) v.elementAt(2);
				String prop_value = null;
				String new_prop_value = null;
				
				if (!tab.supportsProperty(prop_iri)) {
					String error_msg = " -- property " + prop_iri
							+ " is not identifiable.";
					w.add(error_msg);
					return w;
				} else if (tab.isReadOnlyProperty(prop_iri)) {
					String error_msg = " -- property "
							+ prop_iri + ", it is read-only.";
					w.add(error_msg);
					return w;
				}
				
				// with the exception of DEL_ALL, all other operations will require
				// at least a prop value and a set of annotations				
				if (!this.operation.equals(EditOp.DEL_ALL)) {
					
					prop_value = (String) v.elementAt(3);
					
					qualifiers = new ArrayList<OWLAnnotation>();
					
					int pairs = 4;
					while ((v.elementAt(pairs) != null) &&
							(v.elementAt(pairs) != prop_iri)) {
						String ann = v.elementAt(pairs++);
						if (v.elementAt(pairs) != null) {
							// ok, we have two more
							String ann_val = v.elementAt(pairs++);
							qualifiers.add(tab.createAnnotation(ann, ann_val));
						} else {
							// error, qualifiers come in pairs
						}
					}
					if (operation.equals(EditOp.MODIFY)) {
						new_prop_value = v.elementAt(++pairs);
						
						while ((v.elementAt(pairs) != null) &&
								(v.elementAt(pairs) != prop_iri)) {
							String ann = v.elementAt(pairs++);
							if (v.elementAt(pairs) != null) {
								// ok, we have two more
								String ann_val = v.elementAt(pairs++);
								new_qualifiers.add(tab.createAnnotation(ann, ann_val));
							} else {
								// error, qualifiers come in pairs
							}
						}
						
						
					}
					
				}

				switch (operation) {
				case DELETE:
			         NEW:								

					if (!tab.hasComplexPropertyValue(classToEdit, prop_iri,
							prop_value, qualifiers)) {
						String error_msg = " -- complex property " + "("
								+ prop_iri + ", "
								+ prop_value
								+ ") does not exist.";
						w.add(error_msg);
						return w;
					}
					break;
				case DEL_ALL:
					// TODO: Is there anything to validate here?
					break;
				case MODIFY:
					
					
					if (!tab.hasComplexPropertyValue(classToEdit, prop_iri,
							new_prop_value, new_qualifiers)) {
						String error_msg = " -- complex property " + "("
								+ prop_iri + ", "
								+ prop_value
								+ ") does not exist.";
						w.add(error_msg);
						return w;
					}
					
					break;
				
				default:
					break;
				}
			} catch (Exception e) {
				w.add("Exception caught" + e.toString());
			}
		}

		return w;
	}
	
	public boolean processData(Vector<String> w) {
		return true;
	}

		
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
