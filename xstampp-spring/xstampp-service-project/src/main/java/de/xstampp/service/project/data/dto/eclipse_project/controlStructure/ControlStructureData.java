package de.xstampp.service.project.data.dto.eclipse_project.controlStructure;

import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components.CSConnection;
import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components.Component;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class ControlStructureData {

    @XmlElement(name = "component")
    private Component root;

    @XmlElementWrapper(name = "rootComponents")
    @XmlElement(name = "root")
    private List<Component> rootComponents;

    @XmlElementWrapper(name = "connections")
    @XmlElement(name = "connection")
    private List<CSConnection> connections;

    @XmlAttribute
    private Boolean useMultiRoots;

    public Component getRoot() {
        return root;
    }

    public void setRoot(Component root) {
        this.root = root;
    }

    public List<Component> getRootComponents() {
        return rootComponents;
    }

    public void setRootComponents(List<Component> rootComponents) {
        this.rootComponents = rootComponents;
    }

    public List<CSConnection> getConnections() {
        return connections;
    }

    public void setConnections(List<CSConnection> connections) {
        this.connections = connections;
    }

    public Boolean getUseMultiRoots() {
        return useMultiRoots;
    }

    public void setUseMultiRoots(Boolean useMultiRoots) {
        this.useMultiRoots = useMultiRoots;
    }
}
