package gov.nih.nci.ui;

import java.awt.BorderLayout;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

public class ConceptView extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    private static final Logger log = Logger.getLogger(ConceptView.class);
    
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        log.info("Example View Component initialized");
    }

	@Override
	protected void disposeOWLView() {
	}
}
