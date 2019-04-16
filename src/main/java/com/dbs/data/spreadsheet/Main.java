package com.dbs.data.spreadsheet;

import com.dbs.data.log.Logger;
import com.dbs.data.spreadsheet.Exception.CircleExistException;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author erpu.yang
 * @date 2019/04/15
 */
public class Main {

    public static void main(String[] args) {

        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            String inputFilePath = cmd.getOptionValue("input");
            String outputFilePath = cmd.getOptionValue("output");

            String dir = System.getProperty("user.dir");
            TreeMap<String, Node> nodes = readFile(dir + File.separator + inputFilePath);
            List<Node> sortedNodes = Helper.topSort(new ArrayList<>(nodes.values()));
            parseSheet(sortedNodes, nodes);
            output(nodes, dir + File.separator + outputFilePath);
        } catch (ParseException e) {
            Logger.error(e.getMessage());
            formatter.printHelp("java -jar spreasheet.jar", options);

            System.exit(1);
        } catch (CircleExistException e) {
            Logger.error(e.getMessage());
            System.exit(2);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            System.exit(3);
        }
    }

    private static TreeMap<String, Node> readFile(String file) throws IOException {
        TreeMap<String, Node> nodes = new TreeMap<>();

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        char row = 'A';
        while ((st = br.readLine()) != null) {
            Helper.parseLine(st, row, nodes);
            row++;
        }

        br.close();

        return nodes;
    }

    private static void parseSheet(List<Node> nodes, Map<String, Node> nodesMap) {
        for (Node node : nodes) {
            if (StringUtils.startsWith(node.getExpression(), "=")) {
                String ex = node.getExpression().substring(1);

                Map<String, BigDecimal> valueMap = new HashMap<>();
                String[] colValExs = StringUtils.split(node.getExpression().substring(1), Helper.SUPPORTED_OPERATORS);

                for (String item : colValExs) {
                    // ref
                    if (Character.isLetter(item.charAt(0))) {
                        Node n = nodesMap.get(item);
                        valueMap.put(n.getLabel(), n.getValue());
                    }
                }

                BigDecimal val = Helper.eval(ex, valueMap);
                node.setValue(val);
            }

        }
    }

    private static void output(TreeMap<String, Node> nodes, String file) throws IOException {
        char curr = nodes.firstKey().charAt(0);
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(nodes.get(nodes.firstKey()).getValue().toString());
        for (String key : nodes.keySet()) {

            if (StringUtils.equals(key,  nodes.firstKey())) {
                continue;
            }

            if (key.charAt(0) != curr) {
                bw.newLine();
                bw.write(nodes.get(key).getValue().toString());
                curr = key.charAt(0);
            } else {
                bw.write(",");
                bw.write(nodes.get(key).getValue().toString());
            }
        }
        bw.close();
    }
}
