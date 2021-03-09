package de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "connection")
@XmlAccessorType(XmlAccessType.NONE)
public class CSConnection {

  @XmlElement(name = "id")
  private UUID id;

  @XmlElement(name = "connectionType")
  private String connectionType;

  @XmlElement(name = "sourceAnchor")
  private Anchor sourceAnchor;

  @XmlElement(name = "targetAnchor")
  private Anchor targetAnchor;

//  @XmlElement(name = "id")
//  private List<Point> bendPoints;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getConnectionType() {
    return connectionType;
  }

  public void setConnectionType(String connectionType) {
    this.connectionType = connectionType;
  }

  public Anchor getSourceAnchor() {
    return sourceAnchor;
  }

  public void setSourceAnchor(Anchor sourceAnchor) {
    this.sourceAnchor = sourceAnchor;
  }

  public Anchor getTargetAnchor() {
    return targetAnchor;
  }

  public void setTargetAnchor(Anchor targetAnchor) {
    this.targetAnchor = targetAnchor;
  }
}
