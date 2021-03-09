package de.xstampp.service.project.service.data.report.xmlProcessor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(value = { "parent" })
public class XmlNode {

    private static final int INDENT_SIZE = 8;

    public XmlNode parent;
    public String name;
    public Map<String, String> attributes = new HashMap<>();
    public List contents = new ArrayList();

    public void toXslFo() {
        Stack<Object> stack = new Stack<>();
        for (int i = contents.size()-1; i >= 0; i--) {
            stack.push(this.contents.get(i));
        }
        while (!stack.empty()) {
            Object node = stack.pop();
            if (!(node instanceof XmlNode)) {
                continue;
            }
            XmlNode xmlNode = (XmlNode) node;
            xmlNode.applyHtmlAttributes();
            switch (xmlNode.name) {
                case "p":
                    xmlNode.name = "fo:block";
                    xmlNode.attributes.put("space-before", "0.5em");
                    break;
                case "br":
                    xmlNode.name = "fo:block";
                    break;
                case "ul":
                    xmlNode.name = "fo:block";
                    for (Object subnode : xmlNode.contents) {
                        if (subnode instanceof XmlNode && ((XmlNode) subnode).name.equals("li")) {
                            ((XmlNode) subnode).name = "li-u";
                        }
                    }
                    break;
                case "ol":
                    xmlNode.name = "fo:block";
                    int elNo = 1;
                    for (Object subnode : xmlNode.contents) {
                        if (subnode instanceof XmlNode && ((XmlNode) subnode).name.equals("li")) {
                            ((XmlNode) subnode).name = "li-" + elNo;
                            elNo++;
                        }
                    }
                    break;
                case "li-u":
                    xmlNode.name = "fo:block";
                    xmlNode.contents.add(0, "* ");
                    break;
                case "span":
                    xmlNode.name = "fo:inline";
                    break;
                case "strong":
                    xmlNode.name = "fo:inline";
                    xmlNode.attributes.put("font-weight", "bold");
                    break;
                case "em":
                    xmlNode.name = "fo:inline";
                    xmlNode.attributes.put("font-style", "italic");
                    break;
                case "u":
                    xmlNode.name = "fo:inline";
                    xmlNode.attributes.put("text-decoration", "underline");
                    break;
                case "s":
                    xmlNode.name = "fo:inline";
                    xmlNode.attributes.put("text-decoration", "line-through");
                    break;
                default:
                    if (xmlNode.name.startsWith("li-")) {
                        int number = Integer.valueOf(xmlNode.name.split("li-")[1]);
                        xmlNode.contents.add(0, number + ". ");
                        xmlNode.name = "fo:block";
                    }
            }
            for (int i = xmlNode.contents.size() - 1; i >= 0; i--) {
                stack.push(xmlNode.contents.get(i));
            }
        }
    }

    public void prependToTags(String prefix) {
        Stack<Object> stack = new Stack<>();
        boolean firstNode = true;
        for (int i = contents.size()-1; i >= 0; i--) {
            stack.push(this.contents.get(i));
        }
        while (!stack.empty()) {
            Object node = stack.pop();
            if (!(node instanceof XmlNode)) {
                continue;
            }
            XmlNode xmlNode = (XmlNode) node;
            xmlNode.name = prefix + xmlNode.name;
            if (firstNode) {
                xmlNode.attributes.put("xmlns:svg", "http://www.w3.org/2000/svg");
                firstNode = false;
            }
            for (int i = xmlNode.contents.size() - 1; i >= 0; i--) {
                stack.push(xmlNode.contents.get(i));
            }
        }
    }

    public boolean hasTag(String tagName) {
        tagName = tagName.toLowerCase();
        Stack<Object> stack = new Stack<>();
        for (int i = contents.size()-1; i >= 0; i--) {
            stack.push(this.contents.get(i));
        }
        while (!stack.empty()) {
            Object node = stack.pop();
            if (!(node instanceof XmlNode)) {
                continue;
            }
            XmlNode xmlNode = (XmlNode) node;
            if (xmlNode.name.trim().toLowerCase().equals(tagName)) {
                return true;
            }
            for (int i = xmlNode.contents.size() - 1; i >= 0; i--) {
                stack.push(xmlNode.contents.get(i));
            }
        }
        return false;
    }

    private void applyHtmlAttributes() {
        if (this.name.startsWith("li")) {
            this.attributes.put("margin-left", INDENT_SIZE + "px");
        }
        if (!this.attributes.containsKey("class")) {
            return;
        }
        String[] attributes = this.attributes.get("class").split(" ");
        for (String attribute : attributes) {
            switch (attribute) {
                case "ql-size-small":
                    this.attributes.put("font-size", "0.75em");
                    break;
                case "ql-size-large":
                    this.attributes.put("font-size", "1.5em");
                    break;
                case "ql-size-huge":
                    this.attributes.put("font-size", "2.2em");
                    break;

                case "ql-font-serif":
                    this.attributes.put("font-family", "serif");
                    break;
                case "ql-font-monospace":
                    this.attributes.put("font-family", "monospace");
                    break;

                case "ql-align-center":
                    this.attributes.put("text-align", "center");
                    break;
                case "ql-align-right":
                    this.attributes.put("text-align", "right");
                    break;
                case "ql-align-justify":
                    this.attributes.put("text-align", "justify");
                    break;

                    default:
                        if (attribute.startsWith("ql-indent-")) {
                            int indent = Integer.valueOf(attribute.split("ql-indent-")[1]);
                            if (this.name.startsWith("li")) {
                                indent += 1;
                            }
                            this.attributes.put("margin-left", (indent * INDENT_SIZE) + "px");
                        }
            }
        }
        this.attributes.remove("class");
    }

}
