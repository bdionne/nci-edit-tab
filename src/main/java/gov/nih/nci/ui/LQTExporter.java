package gov.nih.nci.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.protege.csv.export.OwlClassExpressionVisitor;

public class LQTExporter {
	
	private static final Logger logger = LoggerFactory.getLogger(LQTExporter.class.getName());
    private final boolean includeHeaders, includeEntityTypes, useCurrentRendering, includeSuperclasses, includeCustomText;
    private final String fileDelimiter, propertyValuesDelimiter, customText;
    private final List<OWLEntity> results, properties;
    private final Map<OWLEntity, List<OWLEntity>> dependentAnnotations;
    private final File outputFile;
    private final OWLEditorKit editorKit;
    private final OWLModelManagerEntityRenderer entityRenderer;
    private final OWLObjectRenderer objectRenderer;
    private final OWLOntology ont;
    private final OwlClassExpressionVisitor visitor = new OwlClassExpressionVisitor();
    private NCIEditTab tab = null;

    /**
     * Package-private constructor. Use {@link LQTExporterBuilder}
     *
     * @param editorKit OWL Editor Kit
     * @param outputFile    Output file for LQT export
     * @param output   List of entities that should be exported
     * @param properties    List of properties whose restrictions on output entities should be exported
     * @param fileDelimiter Primary delimiter for entries
     * @param propertyValuesDelimiter   Delimiter for the (potentially multiple) values of the properties selected
     * @param includeHeaders  true if headers (e.g., property names) should be included in the first row of the file, false otherwise
     * @param includeEntityTypes    true if a column specifying the type of entity in each result row should be included, false otherwise
     * @param useCurrentRendering   true if the currently selected entity rendering should be used instead of IRIs, false otherwise
     * @param includeSuperclasses   true if the superclass(es) of each class in the result set should be included, false otherwise
     * @param includeCustomText true if a row should be added at the end of the file containing custom text, false otherwise
     * @param customText    Custom text to be included in the last row of the file
     */
    public LQTExporter(OWLEditorKit editorKit, File outputFile, List<OWLEntity> output, List<OWLEntity> properties,
    		Map<OWLEntity, List<OWLEntity>> depAnns, String fileDelimiter,
                        String propertyValuesDelimiter, boolean includeHeaders, boolean includeEntityTypes, boolean useCurrentRendering,
                        boolean includeSuperclasses, boolean includeCustomText, String customText) {
        this.editorKit = checkNotNull(editorKit);
        this.outputFile = checkNotNull(outputFile);
        this.results = checkNotNull(output);
        this.properties = checkNotNull(properties);
        this.dependentAnnotations = checkNotNull(depAnns);
        this.fileDelimiter = checkNotNull(fileDelimiter);
        this.propertyValuesDelimiter = checkNotNull(propertyValuesDelimiter);
        this.includeHeaders = checkNotNull(includeHeaders);
        this.includeEntityTypes = checkNotNull(includeEntityTypes);
        this.useCurrentRendering = checkNotNull(useCurrentRendering);
        this.includeSuperclasses = checkNotNull(includeSuperclasses);
        this.includeCustomText = checkNotNull(includeCustomText);
        this.customText = checkNotNull(customText);

        tab = NCIEditTab.currentTab();
        OWLModelManager manager = editorKit.getModelManager();
        entityRenderer = manager.getOWLEntityRenderer();
        objectRenderer = manager.getOWLObjectRenderer();
        ont = manager.getActiveOntology();
    }

    public void export() throws IOException {
        logger.info("Exporting Lucene search results to: " + outputFile.getAbsolutePath());
        FileWriter fw = new FileWriter(outputFile);
        String header = getHeader();
        OWLReasoner reasoner = null;
        if(isIncludingSuperclasses()) {
            reasoner = new StructuralReasoner(editorKit.getModelManager().getActiveOntology(), new SimpleConfiguration(), BufferingMode.BUFFERING);
        }
        List<String> rows = new ArrayList<>();
        for(OWLEntity e : results) {
        	OWLClass superCls = (OWLClass) e;
        	addToList(rows, reasoner, superCls);
        }
        if(includeHeaders) {
            rows.add(0, header);
        }
        if(includeCustomText) {
            rows.add("\n\n" + customText);
        }
        for (String row : rows) { // write results to file
            fw.write(row + "\n");
        }
        fw.flush();
        fw.close();
        logger.info(" ... done exporting");
    }

    private void addToList(List<String> rows, OWLReasoner reasoner, OWLClass e) {
    	String row = getRendering(e) + fileDelimiter;
        if(includeEntityTypes) {
            row += e.getEntityType().getName() + fileDelimiter;
        }
        if(includeSuperclasses && e.isOWLClass()) {
            row += getSuperclasses(e, reasoner) + fileDelimiter;
        }
        if(!properties.isEmpty()) {
            for (OWLEntity property : properties) {
                row += getPropertyValues(e, property);
            }
        }
        rows.add(row);
        List<OWLClass> subClasses = tab.getDirectSubClasses(e);
        Collections.sort(subClasses, new Comparator<OWLClass>() {

			public int compare(OWLClass o1, OWLClass o2) {
				// single quotes are used by Protege-OWL when the browser
				// text has space in it, but they are displayed without the
				// quotes
				// this messes up the sort order
				String s1 = o1.getIRI().getShortForm();
				String s2 = o2.getIRI().getShortForm();
				if (s1.startsWith("'")) {
					s1 = s1.substring(1, s1.length() - 1);
				}
				if (s2.startsWith("'")) {
					s2 = s2.substring(1, s2.length() - 1);
				}
				return s1.compareTo(s2);
			}

		});
        for (OWLClass sub : subClasses) {
        	addToList(rows, reasoner, sub);
        }

    }
    private String getHeader() {
        String header = "Entity" + fileDelimiter;
        if(includeEntityTypes) {
            header += "Type" + fileDelimiter;
        }
        if(includeSuperclasses) {
            header += "Superclass(es)" + fileDelimiter;
        }
        if(!properties.isEmpty()) {
            for (OWLEntity property : properties) {
                header += getRendering(property) + fileDelimiter;
            }
        }
        return header;
    }

    private String getPropertyValues(OWLEntity entity, OWLEntity property) {
        List<String> values = new ArrayList<>();
        if(property.isOWLAnnotationProperty()) {
            values = getAnnotationPropertyValues(entity, property);
        } else if(property.isOWLDataProperty()) {
            values = getPropertyValuesForEntity(entity, property);
        } else if(property.isOWLObjectProperty()) {
            values = getPropertyValuesForEntity(entity, property);
        }
        String output = "";
        if(!values.isEmpty()) {
            Iterator<String> iter = values.iterator();
            output += "\"";
            while (iter.hasNext()) {
                output += iter.next();
                if (iter.hasNext()) {
                    output += propertyValuesDelimiter;
                } else {
                    output += "\"" + fileDelimiter;
                }
            }
        } else {
            output += fileDelimiter;
        }
        return output;
    }

    private List<String> getAnnotationPropertyValues(OWLEntity entity, OWLEntity property) {
        List<String> values = new ArrayList<>();
        Set<OWLAnnotationAssertionAxiom> axioms = ont.getAnnotationAssertionAxioms(entity.getIRI());
        for(OWLAnnotationAssertionAxiom ax : axioms) {
            if(ax.getProperty().equals(property)) {
            	String next_val = "";
                OWLAnnotationValue annValue = ax.getValue();
                if(annValue instanceof IRI) {
                    next_val = annValue.toString();
                } else if(annValue instanceof OWLLiteral) {
                    String literalStr = ((OWLLiteral) annValue).getLiteral();
                    literalStr = literalStr.replaceAll("\"", "'");
                    next_val = literalStr;
                } else if(annValue instanceof OWLAnonymousIndividual) {
                    next_val = "AnonymousIndividual-" + ((OWLAnonymousIndividual)annValue).getID().getID();
                }
                // now check if user selected annotations on this annotation
                List<OWLEntity> deps = dependentAnnotations.get(property); 
                if (deps != null) {
                	if (!ax.getAnnotations().isEmpty()) {
                		next_val += " " + getJsonObject(deps, ax.getAnnotations());
                	}
                }
                values.add(next_val);
            }
        }
        return values;
    }
    
    private String getJsonObject(List<OWLEntity> deps, Set<OWLAnnotation> anns) {
    	String res = "{";
    	for (OWLEntity ent : deps) {
    		for (OWLAnnotation ann : anns) {
    			if (ann.getProperty().equals(ent)) {
    				String val = "";
    				if (ann.getValue() instanceof OWLLiteral) {    					
    					String literalStr = ((OWLLiteral) ann.getValue()).getLiteral();
    					literalStr = literalStr.replaceAll("\"", "'");
    					//literalStr = literalStr.replaceAll(fileDelimiter, "\\" + fileDelimiter);
    					val = literalStr;
    				} else {
    					val = ann.getValue().toString();
    					val = val.replaceAll("\"", "'");
    					//val = val.replaceAll(fileDelimiter, "\\" + fileDelimiter);

    				}
    				res += "\"\"" + ann.getProperty().getIRI().getShortForm() + "\"\"" + " : " + "\"\"" + val + "\"\"" + ",";
    			}
    		}

    	}
    	// check, perhaps annotation had no annotations of interest
    	if (res.length() > 1) {
    		return res.substring(0, res.length() - 1) + "}";
    	} else {
    		return "";
    	}
    }

    private List<String> getPropertyValuesForEntity(OWLEntity entity, OWLEntity property) {
        List<String> values = new ArrayList<>();
        if(entity.isOWLClass()) {
            for (OWLAxiom axiom : ont.getAxioms((OWLClass) entity, Imports.INCLUDED)) {
                if(axiom.getSignature().contains(property)) {
                    if(axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
                        Optional<String> filler = getFillerForAxiom((OWLSubClassOfAxiom)axiom, entity, property);
                        if(filler.isPresent()) {
                            values.add(filler.get());
                        }
                    } else if (axiom.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES)) {
                        OWLSubClassOfAxiom subClassOfAxiom = ((OWLEquivalentClassesAxiom) axiom).asOWLSubClassOfAxioms().iterator().next();
                        Optional<String> filler = getFillerForAxiom(subClassOfAxiom, entity, property);
                        if(filler.isPresent()) {
                            values.add(filler.get());
                        }
                    }
                }
            }
        } else if(entity.isOWLNamedIndividual()) {
            for(OWLAxiom axiom : ont.getAxioms((OWLNamedIndividual) entity, Imports.INCLUDED)) {
                if (axiom.getSignature().contains(property)) {
                    if (axiom.getAxiomType().equals(AxiomType.DATA_PROPERTY_ASSERTION)) {
                        OWLDataPropertyAssertionAxiom dataAssertionAxiom = (OWLDataPropertyAssertionAxiom) axiom;
                        if (dataAssertionAxiom.getProperty().equals(property)) {
                            OWLLiteral literal = dataAssertionAxiom.getObject();
                            String literalStr = literal.getLiteral();
                            literalStr = literalStr.replaceAll("\"", "'");
                            values.add(literalStr);
                        }
                    } else if(axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
                        OWLObjectPropertyAssertionAxiom objAssertionAxiom = (OWLObjectPropertyAssertionAxiom) axiom;
                        if(objAssertionAxiom.getProperty().equals(property)) {
                            OWLIndividual individual = objAssertionAxiom.getObject();
                            values.add(getRendering(individual));
                        }
                    }
                }
            }
        }
        return values;
    }

    private Optional<String> getFillerForAxiom(OWLSubClassOfAxiom axiom, OWLEntity entity, OWLEntity property) {
        String filler = null;
        OWLClassExpression ce;
        if(axiom.getSubClass().equals(entity)) {
            ce = axiom.getSuperClass();
        } else {
            ce = axiom.getSubClass();
        }
        ce.accept(visitor);
        Optional<OWLEntity> optProp = visitor.getProperty();
        if(optProp.isPresent() && optProp.get().equals(property)) {
            Optional<OWLObject> optFiller = visitor.getFiller();
            if(optFiller.isPresent()) {
                filler = getRendering(optFiller.get());
            }
        }
        return Optional.ofNullable(filler);
    }

    private String getSuperclasses(OWLEntity e, OWLReasoner reasoner) {
        Set<OWLClass> superclasses = reasoner.getSuperClasses(e.asOWLClass(), true).getFlattened();
        String output = "";
        Iterator<OWLClass> iter = superclasses.iterator();
        while (iter.hasNext()) {
            OWLClass c = iter.next();
            output += getRendering(c);
            if(iter.hasNext()) {
                output += propertyValuesDelimiter;
            }
        }
        return output;
    }

    private String getRendering(OWLEntity e) {
        String rendering;
        if(useCurrentRendering) {
            rendering = entityRenderer.render(e);
        } else {
            //rendering = e.getIRI().toString();
        	rendering = e.getIRI().getShortForm();
        }
        return rendering;
    }

    private String getRendering(OWLObject obj) {
        return objectRenderer.render(obj);
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }

    public String getPropertyValuesDelimiter() {
        return propertyValuesDelimiter;
    }

    public String getCustomText() {
        return customText;
    }

    public List<OWLEntity> getResults() {
        return results;
    }

    public List<OWLEntity> getProperties() {
        return properties;
    }

    public boolean isIncludingHeaders() {
        return includeHeaders;
    }

    public boolean isIncludingEntityTypes() {
        return includeEntityTypes;
    }

    public boolean isUsingCurrentRendering() {
        return useCurrentRendering;
    }

    public boolean isIncludingSuperclasses() {
        return includeSuperclasses;
    }

    public boolean isIncludingCustomText() {
        return includeCustomText;
    }

    public File getOutputFile() {
        return outputFile;
    }
}
