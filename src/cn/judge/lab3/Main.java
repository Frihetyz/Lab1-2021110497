package cn.judge.lab3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main类包含程序的入口点和用户交互逻辑.
 */
public final class Main {

    /**
     * 隐藏工具类构造器.
     */
    private Main() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 程序入口点.
     *
     * @param args 命令行参数.
     */
    public static void main(final String[] args) {
        String filePath = "./resources/textfile.txt";
        DirectedGraph graph = new DirectedGraph();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
                // 在每行后添加空格以模拟换行符被当作空格处理
                textBuilder.append(" ");
            }
            String text = textBuilder.toString();

            // 功能需求1：读入文本并生成有向图
            graph.buildGraphFromText(text);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to choose a task: Y/N");
        String choice1 = scanner.nextLine();
        while (!choice1.equals("Y") && !choice1.equals("N")) {
            System.out.println("Please input Y/N");
            choice1 = scanner.nextLine();
        }

        while (choice1.equals("Y")) {
            System.out.println("Please choose a task: ");
            System.out.println("1.展示有向图");
            System.out.println("2.查询桥接词（bridge words）");
            System.out.println("3.根据bridge word生成新文本");
            System.out.println("4.计算两个单词之间的最短路径");
            System.out.println("5.随机游走");

            String choice2 = scanner.nextLine();

            if (choice2.equals("1")) {
                // 功能需求2：展示有向图
                String dotFileName = "graph.dot";
                try (BufferedWriter writer =
                             new BufferedWriter(new FileWriter(dotFileName))) {
                    writer.write("digraph G {\n");

                    for (Map.Entry<Node, List<Node>> entry
                            : graph.getAdjacencyList().entrySet()) {
                        Node from = entry.getKey();
                        List<Node> toNodes = entry.getValue();

                        for (Node to : toNodes) {
                            // from节点的adjacencies映射以获取与to节点的权重
                            int weight = from.getAdjacencies()
                                    .getOrDefault(to, 1); // 如果没有权重，默认为1
                            writer.write("\t\"" + from.getLabel()
                                    + "\" -> \"" + to.getLabel()
                                    + "\" [label=\"" + weight + "\"];\n");
                        }
                    }

                    writer.write("}");
                    System.out.println(
                            "Graph has been written to " + dotFileName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                graph.showDirectedGraph("graph.dot", "graph.png");
            }

            if (choice2.equals("2")) {
                // 功能需求3：查询桥接词（bridge words）
                System.out.println("Enter word1:");
                String word1Str = scanner.nextLine().toLowerCase();
                System.out.println("Enter word2:");
                String word2Str = scanner.nextLine().toLowerCase();
                System.out.println(graph.queryBridgeWords(word1Str, word2Str));
            }

            if (choice2.equals("3")) {
                // 功能需求4：根据bridge word生成新文本
                System.out.println("Enter a new text:");
                String newText = scanner.nextLine().toLowerCase();
                System.out.println(graph.generateNewText(newText));
            }

            if (choice2.equals("4")) {
                // 功能需求5：计算两个单词之间的最短路径
                System.out.print("Enter startNode: ");
                String startLabel = scanner.nextLine().toLowerCase();
                System.out.print(
                        "Enter endNode: (or press Enter for empty string) ");
                String endLabel = scanner.nextLine().toLowerCase();
                System.out.println(
                        graph.calcShortestPath(startLabel, endLabel));
            }

            if (choice2.equals("5")) {
                // 功能需求6：随机游走
                System.out.println(graph.randomWalk());
            }

            System.out.println("Do you want to choose a task: Y/N");
            choice1 = scanner.nextLine();
            while (!choice1.equals("Y") && !choice1.equals("N")) {
                System.out.println("Please input Y/N");
                choice1 = scanner.nextLine();
            }
        }
    }
}
