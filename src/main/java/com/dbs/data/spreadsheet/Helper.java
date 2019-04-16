package com.dbs.data.spreadsheet;

import com.dbs.data.spreadsheet.Exception.CircleExistException;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author erpu.yang
 * @date 2019/04/16
 */
public class Helper {

    public static final String SUPPORTED_OPERATORS = "+-*/()";
    private static final JexlEngine jexl = new JexlBuilder().cache(512).strict(true).silent(false).create();

    public static List<Node> topSort(List<Node> graph) throws CircleExistException {
        // map to save in-degree
        HashMap<Node, Integer> map = new HashMap();
        for (Node node : graph) {
            for (Node neighbor : node.getNeighbors()) {
                if (map.containsKey(neighbor)) {
                    map.put(neighbor, map.get(neighbor) + 1);
                } else {
                    map.put(neighbor, 1);
                }
            }
        }

        // init
        ArrayList<Node> result = new ArrayList<Node>();

        // push the node with 0 in-degree to BFS queue
        Queue<Node> q = new LinkedList<Node>();
        for (Node node : graph) {
            if (!map.containsKey(node)) {
                q.offer(node);
                result.add(node);
            }
        }

        while (!q.isEmpty()) {
            Node node = q.poll();
            for (Node n : node.getNeighbors()) {
                map.put(n, map.get(n) - 1);
                // if in-degree == 0, put it into queue
                if (map.get(n) == 0) {
                    result.add(n);
                    q.offer(n);
                }
            }
        }

        // has circle
        if (result.size() != graph.size()) {
            throw new CircleExistException();
        }

        return result;
    }

    public static BigDecimal eval(String formula, Map<String, BigDecimal> valueMap) {
        JexlExpression e = jexl.createExpression(formula);

        // populate the context
        JexlContext context = new MapContext();
        for (String key : valueMap.keySet()) {
            context.set(key, valueMap.get(key).doubleValue());
        }
        // work it out
        Object result = e.evaluate(context);

        return newValueWithScale(String.valueOf(result));
    }

    public static BigDecimal newValueWithScale(String s) {
        return new BigDecimal(s).setScale(5, RoundingMode.HALF_UP);
    }

    public static void parseLine(String st, char row, Map<String, Node> nodes) {
        String line = StringUtils.trimToEmpty(st);
        String[] columns = StringUtils.split(line, ',');
        for (int i = 0; i < columns.length; i++) {
            String expression = columns[i];
            String label = String.format("%c%d", row, i);
            Node node = nodes.containsKey(label) ? nodes.get(label) : new Node(label, expression);
            node.setExpression(expression);
            if (expression.startsWith("=")) {
                String[] colValExs = StringUtils.split(expression.substring(1), SUPPORTED_OPERATORS);

                for (String item : colValExs) {
                    // ref
                    if (Character.isLetter(item.charAt(0))) {
                        if (nodes.containsKey(item)) {
                            nodes.get(item).getNeighbors().add(node);
                        } else {
                            Node temp = new Node(item);
                            nodes.put(item, temp);

                            temp.getNeighbors().add(node);
                        }
                    }
                }
            } else {
                node.setValue(Helper.newValueWithScale(expression));
            }
            nodes.put(label, node);
        }
    }
}
