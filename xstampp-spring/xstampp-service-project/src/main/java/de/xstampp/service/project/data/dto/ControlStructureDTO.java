package de.xstampp.service.project.data.dto;

import java.util.ArrayList;
import java.util.List;

import de.xstampp.service.project.data.entity.Arrow;
import de.xstampp.service.project.data.entity.Box;

public class ControlStructureDTO {

	String projectId;

	String svg;
	String blackAndWhiteSVG;

	List<BoxDTO> boxes;
	List<ArrowDTO> arrows;

	public ControlStructureDTO() {
		boxes = new ArrayList<>();
		arrows = new ArrayList<>();
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getBlackAndWhiteSVG() {
		return blackAndWhiteSVG;
	}

	public void setBlackAndWhiteSVG(String blackAndWhiteSVG) {
		this.blackAndWhiteSVG = blackAndWhiteSVG;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<BoxDTO> getBoxes() {
		return boxes;
	}

	public void setBoxes(List<BoxDTO> boxes) {
		this.boxes = boxes;
	}

	public List<ArrowDTO> getArrows() {
		return arrows;
	}

	public void setArrows(List<ArrowDTO> arrows) {
		this.arrows = arrows;
	}

// ++++++++++++++++++++++++++++++++++++++++++

	public static class BoxDTO {
		String id;
		String name;
		String projectId;
		String type;
		int x;
		int y;
		int height;
		int width;
		Integer parentId;

		public BoxDTO() {

		}

		public BoxDTO(Box box) {
			this.id = box.getId();
			this.name = box.getName();
			this.projectId = box.getProjectId().toString();
			this.type = box.getBoxType();
			this.x = box.getX();
			this.y = box.getY();
			this.width = box.getWidth();
			this.height = box.getHeight();
			this.parentId = box.getParent();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getProjectId() {
			return projectId;
		}

		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public Integer getParent() {
			return parentId;
		}

		public void setParent(Integer parent) {
			this.parentId = parent;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

	public static class ArrowDTO {
		String id;
		String source;
		String destination;
		String projectId;
		String label;
		String type;
		String parentId;
		List<PartDTO> parts;

		public ArrowDTO() {

		}

		public ArrowDTO(Arrow arrow) {
			this.id = arrow.getId();
			this.source = arrow.getSource();
			this.destination = arrow.getDestination();
			this.projectId = arrow.getProjectId().toString();
			this.type = arrow.getType();
			this.parentId = arrow.getParents();
			this.label = arrow.getLabel();
			this.parts = new ArrayList<>();
			int[] arrowParts = arrow.getParts();
			if (arrowParts != null) {
				for (int i = 0; i < arrowParts.length / 2; i++) {
					int index = i * 2;
					parts.add(new PartDTO(arrowParts[index], arrowParts[index + 1]));
				}
			}
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public String getProjectId() {
			return projectId;
		}

		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getParent() {
			return parentId;
		}

		public void setParent(String parent) {
			this.parentId = parent;
		}

		public List<PartDTO> getParts() {
			return parts;
		}

		public void setParts(List<PartDTO> parts) {
			this.parts = parts;
		}
	}

	public static class PartDTO {
		private int x;
		private int y;

		public PartDTO() {

		}

		public PartDTO(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}

}
