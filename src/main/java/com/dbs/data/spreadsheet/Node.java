package com.dbs.data.spreadsheet;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author erpu.yang
 * @date 2019/04/16
 */
public class Node {
    private String label;
    private String expression;
    private ArrayList<Node> neighbors;
    private BigDecimal value;

    public Node(String label, String expression) {
        this.label = label;
        this.expression = expression;
        neighbors = new ArrayList<Node>();
    }

    public Node(String label) {
        this.label = label;
        neighbors = new ArrayList<Node>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = neighbors;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
