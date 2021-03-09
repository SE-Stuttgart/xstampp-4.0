package de.xstampp.service.project.service.data.report.xmlProcessor;

import java.util.Set;
import java.util.Stack;

import static de.xstampp.service.project.service.data.report.xmlProcessor.XmlProcessor.NodeConstructionState.*;

public class XmlProcessor {

    enum NodeConstructionState {
        BLOCK,
        TAG,
        START_TAG,
        END_TAG,
        ATTRIBUTES,
        ATTRIBUTE_NAME,
        ATTRIBUTE_START,
        ATTRIBUTE_VALUE
    }

    public static String convertSystemDescription(String html) {
        XmlNode rootNode = constructXmlNodes(html);
        rootNode.toXslFo();
        return nodesToString(rootNode);
    }

    public static String convertSvg(String svg) {
        XmlNode rootNode = constructXmlNodes(svg);
        rootNode.prependToTags("svg:");
        return nodesToString(rootNode);
    }

    public static void svgSecurityCheck(String svg) throws SecurityException {
        XmlNode rootNode = constructXmlNodes(svg);
        if (rootNode.hasTag("script")) {
            throw new SecurityException("SVG contains 'script' tag. Possible XSS hazard.");
        }
    }

    public static XmlNode constructXmlNodes(String html) {

        NodeConstructionState state = BLOCK;
        XmlNode rootNode = new XmlNode();
        XmlNode currentNode = rootNode;
        String currentAttributeName = null;
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder escapeBuilder = new StringBuilder();
        int stringIndex = 0;

        for (int fs = 0; fs < html.length() * 2; fs++) {
            if (stringIndex >= html.length()) {
                break;
            }
            char c = html.charAt(stringIndex);
            switch (state) {
                case BLOCK:
                    if (c == '<') {
                        escapeBuilder = new StringBuilder();
                        String lastString = stringBuilder.toString();
                        stringBuilder = new StringBuilder();
                        currentNode.contents.add(lastString);
                        state = TAG;
                        stringIndex++;
                    } else if (escapeBuilder.length() > 0) {
                        escapeBuilder.append(c);
                        if (c == ';') {
                            String escape = escapeBuilder.toString();
                            escapeBuilder = new StringBuilder();
                            switch (escape) {
                                case "&amp;":
                                    stringBuilder.append('&');
                                    break;
                                case "&lt;":
                                    stringBuilder.append('<');
                                    break;
                                case "&gt;":
                                    stringBuilder.append('>');
                                    break;
                                case "&quot;":
                                    stringBuilder.append('"');
                                    break;
                                case "&apos;":
                                    stringBuilder.append('\'');
                                    break;
                            }
                        }
                        stringIndex++;
                    } else if (c == '&') {
                        escapeBuilder.append(c);
                        stringIndex++;
                    } else if (c == '\uFEFF') {
                        stringIndex++;
                    } else {
                        stringBuilder.append(c);
                        stringIndex++;
                    }
                    break;
                case TAG:
                    if (c == '/') {
                        state = END_TAG;
                        stringIndex++;
                    } else {
                        XmlNode newNode = new XmlNode();
                        newNode.parent = currentNode;
                        currentNode.contents.add(newNode);
                        currentNode = newNode;
                        state = START_TAG;
                    }
                    break;
                case START_TAG:
                    if (c == '>') {
                        currentNode.name = stringBuilder.toString();
                        stringBuilder = new StringBuilder();
                        if (currentNode.name.equals("br")) {
                            currentNode = currentNode.parent;
                        }
                        state = BLOCK;
                        stringIndex++;
                    } else if (c == '/') {
                        currentNode.name = stringBuilder.toString();
                        stringBuilder = new StringBuilder();
                        currentNode = currentNode.parent;
                        stringIndex += 2;
                    } else if (Character.isWhitespace(c)) {
                        currentNode.name = stringBuilder.toString();
                        stringBuilder = new StringBuilder();
                        state = ATTRIBUTES;
                        stringIndex++;
                    } else {
                        stringBuilder.append(c);
                        stringIndex++;
                    }
                    break;
                case END_TAG:
                    if (c == '>') {
                        currentNode = currentNode.parent;
                        state = BLOCK;
                    }
                    stringIndex++;
                    break;
                case ATTRIBUTES:
                    if (Character.isWhitespace(c)) {
                        stringIndex++;
                    } else if (c == '>') {
                        state = BLOCK;
                        stringIndex++;
                    } else if (c == '/') {
                        currentNode = currentNode.parent;
                        stringIndex += 2;
                    } else {
                        state = ATTRIBUTE_NAME;
                    }
                    break;
                case ATTRIBUTE_NAME:
                    if (c == '=') {
                        currentAttributeName = stringBuilder.toString();
                        stringBuilder = new StringBuilder();
                        state = ATTRIBUTE_START;
                        stringIndex++;
                    } else {
                        stringBuilder.append(c);
                        stringIndex++;
                    }
                    break;
                case ATTRIBUTE_START:
                    if (c == '"') {
                        state = ATTRIBUTE_VALUE;
                    }
                    stringIndex++;
                    break;
                case ATTRIBUTE_VALUE:
                    if (c == '"') {
                        currentNode.attributes.put(currentAttributeName, stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        state = ATTRIBUTES;
                        stringIndex++;
                    } else {
                        stringBuilder.append(c);
                        stringIndex++;
                    }
                    break;
            }
        }

        return rootNode;
    }

    public static String nodesToString(XmlNode rootNode) {
        Stack<Object> stack = new Stack<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = rootNode.contents.size() - 1; i >= 0; i--) {
            stack.push(rootNode.contents.get(i));
        }
        while (!stack.empty()) {
            Object node = stack.pop();
            if (node instanceof EscapedString) {
                stringBuilder.append(((EscapedString) node).getString());
            } else if (node instanceof String) {
                stack.push(new EscapedString((String) node, false));
            } else if (node instanceof XmlNode) {
                XmlNode xmlNode = (XmlNode) node;
                stack.push(new EscapedString("</" + xmlNode.name + ">", true));
                for (int i = xmlNode.contents.size() - 1; i >= 0; i--) {
                    stack.push(xmlNode.contents.get(i));
                }
                stack.push(new EscapedString(">", true));
                Set<String> keySet = xmlNode.attributes.keySet();
                for (String attributeName : keySet) {
                    stack.push(new EscapedString(" " + attributeName + "=\"" + xmlNode.attributes.get(attributeName) + "\"", true));
                }
                stack.push(new EscapedString("<" + xmlNode.name, true));
            }
        }
        return stringBuilder.toString();
    }

    static class EscapedString {
        private String string;

        EscapedString(String string, boolean alreadyEscaped) {
            if (alreadyEscaped) {
                this.string = string;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < string.length(); i++) {
                    char c = string.charAt(i);
                    switch (c) {
                        case '&':
                            stringBuilder.append("&amp;");
                            break;
                        case '<':
                            stringBuilder.append("&lt;");
                            break;
                        case '>':
                            stringBuilder.append("&gt;");
                            break;
                        case '"':
                            stringBuilder.append("&quot;");
                            break;
                        case '\'':
                            stringBuilder.append("&apos;");
                            break;
                            default:
                                stringBuilder.append(c);
                    }
                }
                this.string = stringBuilder.toString();
            }
        }

        public String getString() {
            return string;
        }
    }

}
