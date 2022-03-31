package gov.nih.nci.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.usage.UsageFilter;
import org.protege.editor.owl.ui.usage.UsagePreferences;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nih.nci.curator.CuratorReasonerPreferences;
import gov.nih.nci.curator.utils.RolesVisitor;
import gov.nih.nci.curator.utils.StatedParentVisitor;
import gov.nih.nci.ui.UsagePanel;

public class CuratorChecks {
	
	private final Logger logger = LoggerFactory.getLogger(CuratorChecks.class);

	private OWLOntology ont;
	
	private OWLModelManager owlModelManager;
	
	private StatedParentVisitor spar_visitor;
	
	private RolesVisitor roles_visitor;
	
	private boolean checkRedundantParents = false;
	
	private boolean checkDisjointParents = false;
	
	private boolean checkUnsupportedConstructs = false;
	
	private boolean checkBadRetree = false;
	
	private boolean is_retree = false;
	
	private UsagePanel usage_panel = null;
	
	private OWLEditorKit owlEditorKit;
	
	protected static final String LAST_USED_FOLDER = "";
	
	private Map<OWLEntity, Set<OWLAxiom>> axiomsByEntityMap;
	
	private AxiomSorter axiomSorter;
	
	private Set<OWLAxiom> additionalAxioms = new HashSet<>();

	private OWLEntity entity;
	
	private Set<UsageFilter> filters = new HashSet<>();
	
	
	public CuratorChecks(OWLOntology o, OWLEditorKit oek) {
		ont = o;
		owlModelManager = oek.getOWLModelManager();
		axiomSorter = new AxiomSorter();
		axiomsByEntityMap = new TreeMap<>(owlModelManager.getOWLObjectComparator());
		spar_visitor = new StatedParentVisitor(null, ont);
		roles_visitor = new RolesVisitor(null, ont, null);
		
		checkRedundantParents =
				CuratorReasonerPreferences.getInstance().
				isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_REDUNDANT_PARENT);
		checkDisjointParents =
				CuratorReasonerPreferences.getInstance().
				isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_DISJOINT_CLASSES);
		checkUnsupportedConstructs =
				CuratorReasonerPreferences.getInstance().
				isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_UNSUPPORTED_CONSTRUCTS);
		CuratorReasonerPreferences.getInstance().
		isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_SUBCLASS_FULL_DEFINED);
		checkBadRetree =
				CuratorReasonerPreferences.getInstance().
				isEnabled(CuratorReasonerPreferences.OptionalEditChecksTask.CHECK_BAD_RETREE);
		
		this.owlEditorKit = oek;
		
		usage_panel = new UsagePanel(owlEditorKit);
	}
	
	public Set<OWLClass> getStatedParents(OWLClass c) {
		spar_visitor.setEntity(c);
		c.accept(spar_visitor);
		return spar_visitor.parents;
		
	}
	
	private boolean checkRetreeOk(List<? extends OWLOntologyChange> latest_changes) {
		
		OWLClass f_cls = 
	    		((OWLSubClassOfAxiom) latest_changes.get(0).getAxiom()).getSuperClass().asOWLClass();
		
	    OWLClass cls = 
	    		((OWLSubClassOfAxiom) latest_changes.get(1).getAxiom()).getSubClass().asOWLClass();
	    
	    
	    
	    Set<OWLClass> rts = findRoots(cls, new HashSet<OWLClass>());
	    Set<OWLClass> o_rts = findRoots(f_cls, new HashSet<OWLClass>());
	    
	    if ((rts.size() == 1) && (o_rts.size() == 1)) {
	    	if (rts.iterator().next().equals(o_rts.iterator().next())) {
	    		
	    	} else {
	    		if (checkClassNotModeled(cls)) {
	    			
	    			usage_panel.setOWLEntity(cls, true);	    	    
	    			entity = cls;
	    			
	    			if (usage_panel.getCount() > 1) {		
	    				displayUsages(cls);
	    				return false;
	    			} else {
	    				return true;
	    			}
	    		} else {
	    			// class is modeled but parent is disjoint
	    			return false;
	    		}
	    	}
	    }
	    return true;
	    
	    
	}
	
	private boolean allSubClassAxioms(List<? extends OWLOntologyChange> latest_changes) {
		
		boolean ok = true;
		for (OWLOntologyChange change : latest_changes) {
			OWLAxiom ax = change.getAxiom();
			if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
				if (((OWLSubClassOfAxiom) ax).getSuperClass().isOWLClass()) {
					
				} else {
					ok = false;
					break;
				}
			} else {
				ok = false;
				break;
			}
		}
		return ok;
	}
	
	private boolean isRetree(List<? extends OWLOntologyChange> latest_changes) {

		if ((latest_changes.size() == 2) && (latest_changes.get(0).isRemoveAxiom() &&
				latest_changes.get(1).isAddAxiom())) {
			return allSubClassAxioms(latest_changes);
		} else {
			return false;
		}
	}
	
	public boolean checkOkChanges(List<? extends OWLOntologyChange> latest_changes) {

		OWLOntologyChange latest_change = null;

		if ((latest_changes.size() == 1) && (latest_changes.get(0).isAddAxiom())) {
			latest_change = latest_changes.get(0);
		} else if ((latest_changes.size() == 2) && (latest_changes.get(0).isRemoveAxiom() &&
				latest_changes.get(1).isAddAxiom())) {
			
			latest_change = latest_changes.get(1);
			is_retree = isRetree(latest_changes);
		}
		
		if (is_retree && checkBadRetree) {
			return checkRetreeOk(latest_changes);
		} else if (latest_change != null) {
			return checkOkChange(latest_change);
		} else {
			return true;
		}
	}
	
	public boolean checkOkChange(OWLOntologyChange change) {

		boolean ok = true;

		

		if (change.isAddAxiom()) {
			
			if (checkUnsupportedConstructs) {
				ok = ok && checkUnsupportedConstruct(change);
			}

			OWLAxiom ax = change.getAxiom();
			ok = ok && checkOkAxiom(ax, ont);

		}

		return ok;

	}	
	
	private boolean checkOkAxiom(OWLAxiom ax, OWLOntology ont) {
		if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
			OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) ax;
			if (subax.getSuperClass().isOWLClass()) {
				OWLClass cls = subax.getSubClass().asOWLClass(); 
				boolean redundantParentsOk = true;
				boolean disjointRootsOk = true;

				if (!subax.getSuperClass().isAnonymous()) {
					OWLClass newParent = subax.getSuperClass().asOWLClass();



					if (checkRedundantParents) {
						redundantParentsOk = checkRedundantParents(cls, newParent);

					}

					if (checkDisjointParents) {
						disjointRootsOk = checkDisjointRoots(cls);

					}

				}

				return redundantParentsOk &&
						disjointRootsOk;
			}
		}
		return true;
    }
	
	private boolean checkUnsupportedConstruct(OWLOntologyChange change) {
		OWLClass cls = null;

		if (change.isAddAxiom()) {
			OWLAxiom ax = change.getAxiom();
			if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) ax;
				if (!subax.getSuperClass().isAnonymous()) {
					return true;
				}
				cls = subax.getSubClass().asOWLClass(); 
			} else if (ax.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
				OWLEquivalentClassesAxiom eax = (OWLEquivalentClassesAxiom) ax;
				cls = eax.getClassExpressionsAsList().get(0).asOWLClass();
			}
		}
		
		
		if (cls != null) {
			roles_visitor.setEntity(cls, true, null);
			cls.accept(roles_visitor);
			if (!roles_visitor.bad_constructs.isEmpty()) {
				if (JOptionPane.showConfirmDialog(null,
						"NCI Curator does not support these language constructs!",
						"Ontology Project Changed",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE) ==
						JOptionPane.OK_OPTION) {
					return true;
				} else {
					return false;
				}

			}
		}
		return true;
	}

	private Set<OWLClass> getUniqueClasses(List<OWLOntologyChange> changes) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		for (OWLOntologyChange change : changes) {
			if (change.isAddAxiom()) {
				OWLAxiom ax = change.getAxiom();
				if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
					OWLSubClassOfAxiom subax = (OWLSubClassOfAxiom) ax;
					result.add(subax.getSubClass().asOWLClass()); 
				} else if (ax.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
					OWLEquivalentClassesAxiom eax = (OWLEquivalentClassesAxiom) ax;
					result.add(eax.getClassExpressionsAsList().get(0).asOWLClass());
				}
			}
		}
		return result;

	}
	
	private boolean checkRedundantParents(OWLClass cls, OWLClass newParent) {
		boolean found = false;
		try {
			found = findPath(cls, newParent, false);
		} catch (Exception ex) {
			found = true;    				
		}
		if (found) {
			// ask user if redundant parent ok or NOT
			if (JOptionPane.showConfirmDialog(null,
					"Asserted parent: " +
			         newParent.getIRI().getFragment() +
			         "\nis already parent of existing parent! ",
					"Ontology Project changed",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) ==
					JOptionPane.OK_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		
		Set<OWLClass> assertedParents = getStatedParents(cls);
    	
		for (OWLClass ap : assertedParents) {
			if (!ap.equals(newParent)) {
				try {
					found = findPath(newParent, ap, true);
				} catch (Exception ex) {
					found = true;    				
				}
				if (found) {
					if (JOptionPane.showConfirmDialog(null,
							"Asserted parent: " + newParent.getIRI().getFragment() +
							"\nis already a child of existing parent: "
							+ ap.getIRI().getFragment() +
							"\nconsider deleting ancestor",
							"Ontology Project changed",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE) ==
							JOptionPane.OK_OPTION) {
						return true;
					} else {
						return false;
					}
				}
			}
		}		
		
		// not found then it's ok
		return !found;
		
	}
	
	private boolean checkDisjointRoots(OWLClass cls) {
		
		boolean found = false;
		if (findRoots(cls, new HashSet<OWLClass>()).size() > 1 ) {
			if (JOptionPane.showConfirmDialog(null,
					"Adding this parent creates multiple disjoint roots!",
					"Ontology Project changed",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) ==
					JOptionPane.OK_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		return !found;
	}
	
	private boolean checkClassNotModeled(OWLClass cls) {
		boolean not_modeled = true;
		for (OWLSubClassOfAxiom subax : ont.getSubClassAxiomsForSubClass(cls)) {
			if (subax.getSuperClass().isOWLClass()) {
				
			} else {
				JOptionPane.showMessageDialog(null,
						"Changing parent to disjoint parent creates issues with role domains",
						"Class Parent Changed",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
		};
		
		return not_modeled;
	}
    
    private Set<OWLClass> findRoots(OWLClass cls, Set<OWLClass> res) {
    	
    	Set<OWLClass> roots = new HashSet<OWLClass>();
    	    	
        Set<OWLClass> assertedParents = getStatedParents(cls);
        
        if (assertedParents.isEmpty()) {
        	roots.add(cls);
        	return roots;        	
        } else {
        	for (OWLClass ap : assertedParents) {
        		Set<OWLClass> rps = findRoots(ap, res);
        		for (OWLClass r : rps) {
        			if (res.contains(r)) {

        			} else {
        				res.add(r);
        			}
        		}
    		}
        	
        }
        return res;
    }
    
    
    
    private boolean findPath(OWLClass src, OWLClass target, boolean checkBase) throws Exception {
    	Set<OWLClass> assertedParents = getStatedParents(src);
    	
    	if (checkBase && assertedParents.contains(target)) {
    		throw new Exception();
    		
    	}
    	
    	
		for (OWLClass ap : assertedParents) {
			
			Set<OWLClass> app = getStatedParents(ap);
			if (app.contains(target)) {
				throw new Exception();				
			} else {
				findPath(ap, target, checkBase);
			}
		}
		return false;
    	
    }
    
    private void displayUsages(OWLEntity entity) {
    	
    	Object[] options = { "Usages..", "CANCEL"};
    	int res = JOptionPane.showOptionDialog(null, "This retreeing is complex, click cancel to backout, usages to see why", "Warning",
    	JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
    	null, options, options[1]);
    	
    	if (res == 1) {
    		return;
    	}
    	
    	
    	
    	
    	
    	WorkspaceFrame frame = ProtegeManager.getInstance().getFrame(owlEditorKit.getOWLWorkspace());
    	
    	    	
        frame.getSize();
        Point loc = frame.getLocation();
        SwingUtilities.convertPointToScreen(loc, frame);
        
        //final JDialog dlg = new JDialog(ProtegeManager.getInstance().getFrame(owlEditorKit.getOWLWorkspace()));
       
        
        JPanel holder = new JPanel(new BorderLayout(3, 3));
        
        JScrollPane lowerComp = new JScrollPane(usage_panel);
        lowerComp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        holder.add(lowerComp);
       
        
        Object[] options2 = { "Export report", "CANCEL"};
       
        int ret = JOptionPaneEx.showConfirmDialog(owlEditorKit.getWorkspace(), "Usages", holder, 
        		JOptionPane.PLAIN_MESSAGE, 
        		JOptionPane.OK_CANCEL_OPTION, null,
        		options2,
        		options2[1]);
        
        if (ret == 0) {
        	Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass());   
			  JFileChooser fc = new JFileChooser(prefs.getString(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
			  
			  //to do - add file filter
			  int select = fc.showSaveDialog(this.owlEditorKit.getWorkspace());
			  if (select == JFileChooser.APPROVE_OPTION) {
				  prefs.putString(LAST_USED_FOLDER, fc.getSelectedFile().getParent());

				  File file = fc.getSelectedFile();
				  String outfileName = file.getAbsolutePath();


				  //String filename = file.getName();
				  
				  UsagePreferences p = UsagePreferences.getInstance();
				  
				  filters.addAll(p.getActiveFilters());

				  try {
					  PrintWriter writer = new PrintWriter(new BufferedWriter(
							  new FileWriter(outfileName)));
					  
					  Set<OWLAxiom> axioms = ont.getReferencingAxioms(entity);
					  for (OWLAxiom ax : axioms) {
						  addUsage(ax);
					  }
					  for (OWLAxiom ax : ont.getReferencingAxioms(entity.getIRI())) {
						  addUsage(ax);
					  }
					  // This is terribly inefficient but there are no indexes in the OWL API to do this.
					  for (OWLAnnotationAssertionAxiom ax : ont.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
						  java.util.Optional<IRI> valueIRI = ax.getValue().asIRI();
						  if (valueIRI.isPresent()) {
							  if (valueIRI.get().equals(entity.getIRI())) {
								  addUsage(ax);
							  }
						  }
					  }
					  
					  for (OWLEntity ent : axiomsByEntityMap.keySet()) {
				            for (OWLAxiom ax : axiomsByEntityMap.get(ent)) {
				            	writer.println(ax.toString());
				            }
				        }
					  
					  
					  writer.close();
					  
					  JOptionPane.showMessageDialog(owlEditorKit.getWorkspace(), "Report written to: " +
					  file.getName(), "Usages report", JOptionPane.INFORMATION_MESSAGE);
				  } catch (IOException e) {
					  // TODO Auto-generated catch block
					  e.printStackTrace();
				  }
			  }
        	
        }
        
        
        
    }
    
    private class AxiomSorter implements OWLAxiomVisitor, OWLEntityVisitor, OWLPropertyExpressionVisitor {

        private OWLAxiom currentAxiom;


        public void setAxiom(OWLAxiom axiom) {
            currentAxiom = axiom;
        }


        private void add(OWLEntity ent) {

            if (isFilterSet(UsageFilter.filterSelf) && entity.equals(ent)) {
                return;
            }
            Set<OWLAxiom> axioms = axiomsByEntityMap.get(ent);
            if (axioms == null) {
                axioms = new HashSet<>();
                axiomsByEntityMap.put(ent, axioms);
            }
            axioms.add(currentAxiom);
        }


        public void visit(OWLClass cls) {
            add(cls);
        }


        public void visit(OWLDatatype dataType) {
            add(dataType);
        }


        public void visit(OWLNamedIndividual individual) {
            add(individual);
        }


        public void visit(OWLDataProperty property) {
            add(property);
        }


        public void visit(OWLObjectProperty property) {
            add(property);
        }


        public void visit(OWLAnnotationProperty property) {
            add(property);
        }


        public void visit(OWLObjectInverseOf property) {
            property.getInverse().accept(this);
        }


        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLAnnotationAssertionAxiom axiom) {
            if (axiom.getSubject() instanceof IRI) {
                IRI subjectIRI = (IRI) axiom.getSubject();
                
                    if (ont.containsClassInSignature(subjectIRI)) {
                        add(owlModelManager.getOWLDataFactory().getOWLClass(subjectIRI));
                    }
                    if (ont.containsObjectPropertyInSignature(subjectIRI)) {
                        add(owlModelManager.getOWLDataFactory().getOWLObjectProperty(subjectIRI));
                    }
                    if (ont.containsDataPropertyInSignature(subjectIRI)) {
                        add(owlModelManager.getOWLDataFactory().getOWLDataProperty(subjectIRI));
                    }
                    if (ont.containsIndividualInSignature(subjectIRI)) {
                        add(owlModelManager.getOWLDataFactory().getOWLNamedIndividual(subjectIRI));
                    }
                    if (ont.containsAnnotationPropertyInSignature(subjectIRI)) {
                        add(owlModelManager.getOWLDataFactory().getOWLAnnotationProperty(subjectIRI));
                    }
                    if (ont.containsDatatypeInSignature(subjectIRI)) {
                        add(owlModelManager.getOWLDataFactory().getOWLDatatype(subjectIRI));
                    }
                
            }
        }


        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            ((OWLEntity) axiom.getSubProperty()).accept(this);
        }


        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
            ((OWLEntity) axiom.getProperty()).accept(this);
        }


        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
            ((OWLEntity) axiom.getProperty()).accept(this);
        }


        public void visit(OWLClassAssertionAxiom axiom) {
            if (!axiom.getIndividual().isAnonymous()) {
                axiom.getIndividual().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()) {
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLDataPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLDataPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLSubDataPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
        }


        public void visit(OWLDeclarationAxiom axiom) {
            axiom.getEntity().accept(this);
        }


        public void visit(OWLDifferentIndividualsAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                if (!ind.isAnonymous()) {
                    ind.asOWLNamedIndividual().accept(this);
                }
            }
        }


        public void visit(OWLDisjointClassesAxiom axiom) {
            boolean hasBeenIndexed = false;
            if (!isFilterSet(UsageFilter.filterDisjoints)) {
                for (OWLClassExpression desc : axiom.getClassExpressions()) {
                    if (!desc.isAnonymous()) {
                        desc.asOWLClass().accept(this);
                        hasBeenIndexed = true;
                    }
                }
            }
            if (!hasBeenIndexed) {
                additionalAxioms.add(axiom);
            }
        }


        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            if (!isFilterSet(UsageFilter.filterDisjoints)) {
                for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                    prop.accept(this);
                }
            }
        }


        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            if (!isFilterSet(UsageFilter.filterDisjoints)) {
                for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                    prop.accept(this);
                }
            }
        }


        public void visit(OWLDisjointUnionAxiom axiom) {
            if (!isFilterSet(UsageFilter.filterDisjoints)) {
                axiom.getOWLClass().accept(this);
            }
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            boolean hasBeenIndexed = false;
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    desc.asOWLClass().accept(this);
                    hasBeenIndexed = true;
                }
            }
            if (!hasBeenIndexed) {
                additionalAxioms.add(axiom);
            }
        }


        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLHasKeyAxiom axiom) {
            //@@TODO implement
        }


        public void visit(OWLDatatypeDefinitionAxiom axiom) {
            axiom.getDatatype().accept(this);
        }


        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()) {
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()) {
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()) {
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            axiom.getSuperProperty().accept(this);
        }


        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
        }


        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLSameIndividualAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                if (!ind.isAnonymous()) {
                    ind.asOWLNamedIndividual().accept(this);
                }
            }
        }


        public void visit(OWLSubClassOfAxiom axiom) {
            if (!axiom.getSubClass().isAnonymous()) {
                if (!isFilterSet(UsageFilter.filterNamedSubsSupers) ||
                        (!axiom.getSubClass().equals(entity) && !axiom.getSuperClass().equals(entity))) {
                    axiom.getSubClass().asOWLClass().accept(this);
                }
            }
            else {
                additionalAxioms.add(axiom);
            }
        }


        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(SWRLRule rule) {
        }
    }
    
    private boolean isFilterSet(UsageFilter filter) {
        return filters.contains(filter);
    }
    
    private void addUsage(OWLAxiom ax) {
        axiomSorter.setAxiom(ax);
        ax.accept(axiomSorter);
    }

    
    

}
