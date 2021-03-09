package de.xstampp.service.project.data.dto.eclipse_project.causalFactor;

import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components.ComponentType;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "causalComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class CausalCSComponent {

    @XmlElement(name = "title")
    private String text;

    @XmlElementWrapper(name = "causalFactors")
    @XmlElement(name = "factor")
    private List<CausalFactor> factors;

    @XmlElement(name = "type")
    private ComponentType type;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<CausalFactor> getFactors() {
        return factors;
    }

    public void setFactors(List<CausalFactor> factors) {
        this.factors = factors;
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }
}
