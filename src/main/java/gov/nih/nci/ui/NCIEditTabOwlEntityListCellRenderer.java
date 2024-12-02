package gov.nih.nci.ui;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLEntity;

import edu.stanford.protege.csv.export.ui.IncQualsOWLCellRenderer;

public class NCIEditTabOwlEntityListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;
    private IncQualsOWLCellRenderer owlCellRenderer;

    public NCIEditTabOwlEntityListCellRenderer(@Nonnull OWLEditorKit editorKit,
    		Map<OWLEntity, List<OWLEntity>> depMap) {
        owlCellRenderer = new IncQualsOWLCellRenderer(editorKit, depMap);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        //if(value instanceof NCIEditTabPreferencesPanel.OwlEntityListItem) {
            //OWLEntity entity = ((NCIEditTabPreferencesPanel.OwlEntityListItem) value).getEntity();
            //label = owlCellRenderer.getListCellRendererComponent(list, entity, index, isSelected, cellHasFocus);
        //}
        return label;
    }
}
