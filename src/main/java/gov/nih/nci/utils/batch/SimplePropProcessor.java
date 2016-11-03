package gov.nih.nci.utils.batch;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;

import gov.nih.nci.ui.NCIEditTab;

public class SimplePropProcessor extends EditProcessor {
	
	Vector<String> supportedRoles = null;
	Vector<String> supportedProperties = null;
	Vector<String> supportedAssociations = null;
	
	NCIEditTab tab;
	
	public SimplePropProcessor(NCIEditTab t) {
		super();
		
		tab = t;
		
		supportedRoles = tab.getSupportedRoles();
		supportedAssociations = tab.getSupportedAssociations();
	}
	
	public Vector<String> validateData(Vector<String> v) {
		

		// keep a vector or error messages, may be more than one
		Vector<String> w = new Vector<String>();

		try {

			String cls_name = (String) v.elementAt(0);
			String operation = (String) v.elementAt(1);
			String attribute = (String) v.elementAt(2);
			String attributename = (String) v.elementAt(3);
			String attributevalue_1 = (String) v.elementAt(4);
			String attributevalue_2 = (String) v.elementAt(5);

			OWLClass hostClass = tab.getClass(cls_name);

			if (hostClass == null) {
				String error_msg = " -- concept " + cls_name
						+ " does not exist.";
				w.add(error_msg);

			} else if (tab.isRetired(hostClass)) {
				w.add(" -- concept " + cls_name + " is retired, cannot edit");
			}
			

			Vector superclasses = new Vector();
			
			if (operation.compareToIgnoreCase("new") != 0
					&& operation.compareToIgnoreCase("edit") != 0
					&& operation.compareToIgnoreCase("delete") != 0) {
				String error_msg = " -- action " + operation
						+ " is not supported.";
				w.add(error_msg);
			}

			if (attribute.compareToIgnoreCase("parent") != 0
					&& attribute.compareToIgnoreCase("role") != 0
					&& attribute.compareToIgnoreCase("property") != 0
					&& attribute.compareToIgnoreCase("association") != 0) {
				String error_msg = " -- attribute " + attribute
						+ " is not supported.";
				w.add(error_msg);
			}

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

					else if (attribute.compareToIgnoreCase("property") == 0) {
						if (!tab.supportsProperty(attributename)) {
							String error_msg = " -- property " + attributename
									+ " is not identifiable.";
							w.add(error_msg);
						} else {
							
							if (tab.hasPropertyValue(hostClass, attributename,
									attributevalue_1)) {
								String error_msg = " -- property already exists.";
								w.add(error_msg);
							}
							

						}
						
						if (checkBatchProperty(
								attributename, attributevalue_1)
								&& checkBatchPropertyNotFullSynPT(
												attributename, attributevalue_1)) {

						} else {
							// TODO: add some error messages here
							//w.add(tab.getFilter().getErrorMessage());
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
					} else if (attribute.compareToIgnoreCase("property") == 0) {
						if (!supportedProperties.contains(attributename)) {
							String error_msg = " -- property " + attributename
									+ " is not identifiable.";
							w.add(error_msg);
						} else {
							
							Boolean editable = tab.isReadOnlyProperty(attributename);
							if (editable.equals(Boolean.TRUE)) {
								String error_msg = " -- property "
										+ attributename + ", it is read-only.";
								w.add(error_msg);
							}

							if (!hasProperty(hostClass, attributename,
									attributevalue_1)) {

								String error_msg = " -- property " + "("
										+ attributename + ", "
										+ attributevalue_1
										+ ") does not exist.";
								w.add(error_msg);

							}
							

							if (operation.compareToIgnoreCase("edit") == 0) {
								
								if (hasProperty(hostClass,
										attributename, attributevalue_2)) {
									String error_msg = " -- property " + "("
											+ attributename + ", "
											+ attributevalue_2
											+ ") already exists.";
									w.add(error_msg);
								} else if (attributevalue_2
										.equalsIgnoreCase("NA")) {
									String error_msg = " -- property " + "("
											+ attributename
											+ ") new value is not specified.";
									w.add(error_msg);
								} else if (checkBatchProperty(
										attributename, attributevalue_2)
										&& checkBatchPropertyNotFullSynPT(
														attributename, attributevalue_2)) {

								} else {
									// TODO: sme eror messages here
									//w.add(tab.getFilter().getErrorMessage());
								}
								
							} else {
								
								if (checkBatchProperty(
										attributename, attributevalue_1)) {

								} else {
									// TODO: error messages here
									//w.add(tab.getFilter().getErrorMessage());
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
	
	public boolean processData(Vector<String> w) {
		
		
		String className = (String) w.elementAt(0);

		String operation = (String) w.elementAt(1);

		String attribute = (String) w.elementAt(2);

		String attributeName = (String) w.elementAt(3);

		String attributeValue = (String) w.elementAt(4);

		String newAttributeValue = (String) w.elementAt(5);

		boolean retval = false;
		if (operation.compareToIgnoreCase("new") == 0) {
			if (attribute.compareToIgnoreCase("property") == 0) {
				
				OWLAnnotationProperty ap = tab.lookUpShort(attributeName);
				
				if (!tab.getComplexProperties().contains(ap)) {
					tab.addAnnotationToClass(className, ap, attributeValue);						
				} else {
					// complex property
					
				}
				
				
				/**
				if (attributeName.compareToIgnoreCase(NCIEditTabConstants.ALTLABEL) == 0) {
					OWLNamedClass hostClass = wrapper.getOWLNamedClass(name);
					retval = wrapper.addAnnotationProperty(hostClass, NCIEditTab.ALTLABEL, 
							owlModel.createRDFSLiteral(attributeValue, owlModel.getSystemFrames().getXmlLiteralType()));
				} else {
					retval = wrapper.addAnnotationProperty(name, attributeName,
							attributeValue);
					
				}	
				*/			

			} else if (attribute.compareToIgnoreCase("parent") == 0) {
				/**
				OWLNamedClass hostClass = wrapper.getOWLNamedClass(name);
				OWLNamedClass targetClass = wrapper
						.getOWLNamedClass(attributeName);
				retval = wrapper
						.addDirectSuperclass(hostClass, targetClass);
						*/	
			}

			else if (attribute.compareToIgnoreCase("association") == 0) {
				/**
				OWLNamedClass hostClass = wrapper.getOWLNamedClass(name);
				retval = wrapper.addObjectProperty(hostClass,
						attributeName, attributeValue);
						*/
			}

			else if (attribute.compareToIgnoreCase("role") == 0) {
				int pos = attributeValue.indexOf('|');
				String modifier = attributeValue.substring(0, pos);
				String value = attributeValue.substring(pos + 1);
				/**
				retval = wrapper.addRestriction(name, attributeName, value,
						modifier);
						*/
			}
		} else if (operation.compareToIgnoreCase("delete") == 0) {
			if (attribute.compareToIgnoreCase("property") == 0) {
				/**
				retval = wrapper.removeAnnotationProperty(name,
						attributeName, attributeValue);
						*/
			} else if (attribute.compareToIgnoreCase("parent") == 0) {
				/**
				OWLNamedClass hostClass = wrapper.getOWLNamedClass(name);
				OWLNamedClass targetClass = wrapper
						.getOWLNamedClass(attributeName);
				
				RDFSClass definition = hostClass.getDefinition();
				if (definition == null) {
					retval = wrapper.removeDirectSuperclass(hostClass,
							targetClass);
				} else {
					retval = wrapper.removeEquivalentDefinitionNew(hostClass, targetClass);
				}
				*/
				
			}

			else if (attribute.compareToIgnoreCase("association") == 0) {
				/**
				OWLNamedClass hostClass = wrapper.getOWLNamedClass(name);
				retval = wrapper.removeObjectProperty(hostClass,
						attributeName, attributeValue);
						*/
			}

			else if (attribute.compareToIgnoreCase("role") == 0) {
				int pos = attributeValue.indexOf('|');
				String modifier = attributeValue.substring(0, pos);
				String value = attributeValue.substring(pos + 1);
				/**
				retval = wrapper.removeRestriction(name, attributeName,
						value, modifier);
						*/
			}
		} else if (operation.compareToIgnoreCase("edit") == 0) {
			if (attribute.compareToIgnoreCase("property") == 0) {

				/**
				retval = wrapper.modifyAnnotationProperty(name,
						attributeName, attributeValue, newAttributeValue);
						*/
				possiblySyncPreferredTerm(className, attributeName,
						newAttributeValue);

			} else if (attribute.compareToIgnoreCase("role") == 0) {
				int pos = attributeValue.indexOf('|');
				String modifier = attributeValue.substring(0, pos);
				String value = attributeValue.substring(pos + 1);

				pos = newAttributeValue.indexOf('|');
				String newmodifier = newAttributeValue.substring(0, pos);
				String newvalue = newAttributeValue.substring(pos + 1);
				/**
				retval = wrapper.modifyRestriction(name, attributeName,
						value, modifier, newvalue, newmodifier);
						*/
			} else if (attribute.compareToIgnoreCase("association") == 0) {
				/**
				OWLNamedClass hostClass = wrapper.getOWLNamedClass(name);

				retval = wrapper.removeObjectProperty(hostClass,
						attributeName, attributeValue);
				retval = wrapper.addObjectProperty(hostClass,
						attributeName, newAttributeValue);
						*/
			}
		}

		// to be implemented
		/*
		 * else if (edit.compareToIgnoreCase("delete-all") == 0) { }
		 */
		
		return retval;
	}
	
	

	private boolean hasRole(OWLClass cls, String roleName, OWLClass filler) {
		return true;
	}
	
	private boolean hasAssociation(OWLClass cls, String assocName, OWLClass value) {
		return true;
	}
	
	private boolean hasProperty(OWLClass cls, String propName, String value) {
		return true;
	}
	
	private boolean checkBatchProperty(String propName, String value) {
		return true;
	}
	
	private boolean  checkBatchPropertyNotFullSynPT(String propName, String value) {
		return true;
	}
	
	public void possiblySyncPreferredTerm(String cls_name, String name,
			String value) {
		/**
		if (name.compareTo(NCIEditTab.ALTLABEL) == 0) {

			String tn = ComplexPropertyParser.getPtNciTermName(value);
			if (tn != null) {
				// need to mod preferred name and rdfs:label
				OWLNamedClass cls = wrapper.getOWLNamedClass(cls_name);
				String pn = wrapper.getPropertyValue(cls, NCIEditTab.PREFLABEL);
				String rdl = wrapper.getPropertyValue(cls, "rdfs:label");
				wrapper.modifyAnnotationProperty(cls_name,
						NCIEditTab.PREFLABEL, pn, tn);
				wrapper.modifyAnnotationProperty(cls_name, "rdfs:label", rdl,
						tn);

			}
		} else if (name.compareTo(NCIEditTab.PREFLABEL) == 0) {

			OWLNamedClass cls = wrapper.getOWLNamedClass(cls_name);
			ArrayList<String> pvals = wrapper.getPropertyValues(cls,
					NCIEditTab.ALTLABEL);
			for (String s : pvals) {
				String tn = ComplexPropertyParser.getPtNciTermName(s);
				if (tn != null) {
					HashMap<String, String> hm = ComplexPropertyParser
							.parseXML(s);
					String newfspt = ComplexPropertyParser.replaceFullSynValue(
							hm, "term-name", value);
					wrapper.modifyAnnotationProperty(cls_name,
							NCIEditTab.ALTLABEL, s, newfspt);
				}
			}

			String rdl = wrapper.getPropertyValue(cls, "rdfs:label");
			wrapper
					.modifyAnnotationProperty(cls_name, "rdfs:label", rdl,
							value);

		}
		*/

	}

}
