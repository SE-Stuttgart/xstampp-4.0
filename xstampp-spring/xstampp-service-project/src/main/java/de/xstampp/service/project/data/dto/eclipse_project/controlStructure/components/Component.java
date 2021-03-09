package de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components;

import de.xstampp.service.project.data.dto.eclipse_project.causalFactor.CausalFactor;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "component")
@XmlAccessorType(XmlAccessType.NONE)
public class Component {

  @XmlElement(name = "id")
  private UUID id;

  @XmlElement(name = "controlActionId")
  private UUID controlActionId;

  @XmlElement(name = "text")
  private String text;

  @XmlElement(name = "isSafetyCritical")
  private boolean isSafetyCritical;

  @XmlElement(name = "comment")
  private String comment;

  @XmlElement(name = "layout")
  private Rectangle layout;

  @XmlElement(name = "layoutPM")
  private Rectangle layoutPM;

  @XmlElement(name = "componentType")
  private String componentType;

  @XmlElement(name = "relative")
  private UUID relative;

  @XmlElementWrapper(name = "causalFactors")
  @XmlElement(name = "causalFactor")
  private List<CausalFactor> causalFactors;

  @XmlElementWrapper(name = "children")
  @XmlElement(name = "component")
  private List<Component> children;

  @XmlElementWrapper(name = "unsafeVariables")
  @XmlElement(name = "unsafeVariable")
  private List<UUID> unsafeVariables;

  @XmlElementWrapper(name = "connections")
  @XmlElement(name = "connection")
  private List<CSConnection> connections;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getControlActionId() {
    return controlActionId;
  }

  public void setControlActionId(UUID controlActionId) {
    this.controlActionId = controlActionId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isSafetyCritical() {
    return isSafetyCritical;
  }

  public void setSafetyCritical(boolean safetyCritical) {
    isSafetyCritical = safetyCritical;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Rectangle getLayout() {
    return layout;
  }

  public void setLayout(Rectangle layout) {
    this.layout = layout;
  }

  public Rectangle getLayoutPM() {
    return layoutPM;
  }

  public void setLayoutPM(Rectangle layoutPM) {
    this.layoutPM = layoutPM;
  }

  public String getComponentType() {
    return componentType;
  }

  public void setComponentType(String componentType) {
    this.componentType = componentType;
  }

  public UUID getRelative() {
    return relative;
  }

  public void setRelative(UUID relative) {
    this.relative = relative;
  }

  public List<CausalFactor> getCausalFactors() {
    return causalFactors;
  }

  public void setCausalFactors(List<CausalFactor> causalFactors) {
    this.causalFactors = causalFactors;
  }

  public List<Component> getChildren() {
    return children;
  }

  public void setChildren(List<Component> children) {
    this.children = children;
  }

  public List<UUID> getUnsafeVariables() {
    return unsafeVariables;
  }

  public void setUnsafeVariables(List<UUID> unsafeVariables) {
    this.unsafeVariables = unsafeVariables;
  }

  public List<CSConnection> getConnections() {
    return connections;
  }

  public void setConnections(List<CSConnection> connections) {
    this.connections = connections;
  }
}
