package com.dbs.data.spreadsheet;

import com.dbs.data.spreadsheet.Exception.CircleExistException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author erpu.yang
 * @date 2019/04/16
 */
public class HelperTest {

    @Test
    public void testTopSort() throws CircleExistException {
        // 2,4,1,=A0+A1*A2
        // =A3*(A0+1),=B2,0,=A0+1
        // arrange
        List<Node> graph = new ArrayList<Node>();
        Node a0 = new Node("A0","2");
        graph.add(a0);
        Node a1 = new Node("A1", "4");
        graph.add(a1);
        Node a2 = new Node("A2", "1");
        graph.add(a2);
        Node a3 = new Node("A3", "=A0+A1*A2");
        graph.add(a3);

        Node b0 = new Node("B0", "=A3*(A0+1)");
        graph.add(b0);
        Node b1 = new Node("B1", "=B2");
        graph.add(b1);
        Node b2 = new Node("B2", "0");
        graph.add(b2);
        Node b3 = new Node("B3", "=A0+1");
        graph.add(b3);

        a0.getNeighbors().add(a3);
        a1.getNeighbors().add(a3);
        a2.getNeighbors().add(a3);

        a0.getNeighbors().add(b0);
        a3.getNeighbors().add(b0);

        b2.getNeighbors().add(b1);

        a0.getNeighbors().add(b3);

        // act
        List<Node> nodes = Helper.topSort(graph);

        // assert
        int b1Index = -1, b2Index = -1;
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if (n.getLabel().equals("B1")) {
                b1Index = i;
            }

            if (n.getLabel().equals("B2")) {
                b2Index = i;
            }
        }

        assertTrue(b2Index < b1Index);
    }

    @Test(expected = CircleExistException.class)
    public void testTopSortWithCircleException() throws CircleExistException {
        // 2,4,1,=A0+A1*A2
        // =A3*(B1+1),=B0,0,=A0+1
        // arrange
        List<Node> graph = new ArrayList<Node>();
        Node a0 = new Node("A0","2");
        graph.add(a0);
        Node a1 = new Node("A1", "4");
        graph.add(a1);
        Node a2 = new Node("A2", "1");
        graph.add(a2);
        Node a3 = new Node("A3", "=A0+A1*A2");
        graph.add(a3);

        Node b0 = new Node("B0", "=A3*(B1+1)");
        graph.add(b0);
        Node b1 = new Node("B1", "=B0");
        graph.add(b1);
        Node b2 = new Node("B2", "0");
        graph.add(b2);
        Node b3 = new Node("B3", "=A0+1");
        graph.add(b3);

        a0.getNeighbors().add(a3);
        a1.getNeighbors().add(a3);
        a2.getNeighbors().add(a3);

        b1.getNeighbors().add(b0);
        a3.getNeighbors().add(b0);

        b0.getNeighbors().add(b1);

        a0.getNeighbors().add(b3);

        // act
        Helper.topSort(graph);

        // assert
    }

    @Test
    public void testEval() {

        // arrange
        String ex = "A0+A1*A2";
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("A0", Helper.newValueWithScale("2.0"));
        map.put("A1", Helper.newValueWithScale("2.0"));
        map.put("A2", Helper.newValueWithScale("2.0"));

        // act
        BigDecimal result = Helper.eval(ex, map);

        // assert
        assertTrue(result.compareTo(new BigDecimal("6.0")) == 0);

    }

    @Test
    public void testParseLine() {

        // arrange
        String st = "2,4,1,=A0+A1*A2";
        char row = 'A';
        Map<String, Node> nodes = new HashMap<>();

        // act
        Helper.parseLine(st, row, nodes);

        // assert
        assertTrue(nodes.size() == 4);
        assertTrue(nodes.get("A0").getNeighbors().size() == 1);
        assertTrue(nodes.get("A1").getNeighbors().size() == 1);
        assertTrue(nodes.get("A2").getNeighbors().size() == 1);

    }

}
