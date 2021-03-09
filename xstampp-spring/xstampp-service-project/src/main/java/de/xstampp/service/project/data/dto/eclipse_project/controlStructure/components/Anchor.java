package de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "anchor")
@XmlAccessorType(XmlAccessType.NONE)
public class Anchor {

  @XmlElement(name = "isFlying")
  private Boolean isFlying;

  @XmlElement(name = "xOrientation")
  private int xOrientation;

  @XmlElement(name = "yOrientation")
  private int yOrientation;

  @XmlElement(name = "xOrientationWithPm")
  private Integer xOrientationWithPm;

  @XmlElement(name = "yOrientationWithPm")
  private Integer yOrientationWithPm;

  @XmlElement(name = "ownerId")
  private UUID ownerId;

  @XmlElement(name = "id")
  private UUID id;

  public Boolean getFlying() {
    return isFlying;
  }

  public void setFlying(Boolean flying) {
    isFlying = flying;
  }

  public int getxOrientation() {
    return xOrientation;
  }

  public void setxOrientation(int xOrientation) {
    this.xOrientation = xOrientation;
  }

  public int getyOrientation() {
    return yOrientation;
  }

  public void setyOrientation(int yOrientation) {
    this.yOrientation = yOrientation;
  }

  public Integer getxOrientationWithPm() {
    return xOrientationWithPm;
  }

  public void setxOrientationWithPm(Integer xOrientationWithPm) {
    this.xOrientationWithPm = xOrientationWithPm;
  }

  public Integer getyOrientationWithPm() {
    return yOrientationWithPm;
  }

  public void setyOrientationWithPm(Integer yOrientationWithPm) {
    this.yOrientationWithPm = yOrientationWithPm;
  }

  public UUID getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(UUID ownerId) {
    this.ownerId = ownerId;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
