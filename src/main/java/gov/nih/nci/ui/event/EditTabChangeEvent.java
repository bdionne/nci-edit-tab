package gov.nih.nci.ui.event;

import gov.nih.nci.ui.NCIEditTab;

public class EditTabChangeEvent {

    private NCIEditTab source;

    private ComplexEditType type;


    public EditTabChangeEvent(NCIEditTab source, ComplexEditType type) {
        this.source = source;
        this.type = type;
    }


    public NCIEditTab getSource() {
        return source;
    }


    public ComplexEditType getType() {
        return type;
    }

    public boolean isType(ComplexEditType type) {
        return this.type.equals(type);
    }
}

