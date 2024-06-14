package cn.judge.lab3;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * DirectedGraph类表示一个有向图.
 */
public class DirectedGraph {
    /**
     * 邻接列表，用于存储节点及其邻接的其他节点.
     */
    private final Map<Node, List<Node>> adjacencyList = new HashMap<>();

    /**
     * 获取邻接列表.
     * @return 邻接列表
     */
    public Map<Node, List<Node>> getAdjacencyList() {
        return adjacencyList;
    }
    /**
     * 节点映射，用于存储标签与节点对象的对应关系.
     */
    private final Map<String, Node> nodeMap = new HashMap<>();

    /**
     * 添加一条边到有向图.
     *
     * @param from 源节点
     * @param to   目标节点
     */
    public void addEdge(final Node from, final Node to) {
        // 更新边的权重（相邻次数）
        from.incrementAdjacency(to);

        // 确保from的邻接列表存在，然后检查to是否已经在列表中
        List<Node> toNodes = adjacencyList.computeIfAbsent(
                from, k -> new ArrayList<>());
        if (!toNodes.contains(to)) {
            toNodes.add(to);
        }
    }
    /**
     * 从文本构建有向图.
     *
     * @param text 输入的文本
     */
    public void buildGraphFromText(final String text) {
        // 使用空格替换标点符号和换行符，并转换为小写
        String processedText = text.replaceAll(
                "[^a-zA-Z\\s]", " ").toLowerCase();
        // 按空格分割文本为单词数组
        String[] words = processedText.split("\\s+");
        // 遍历单词数组，添加节点和边
        for (int i = 0; i < words.length - 1; i++) {
            String fromLabel = words[i];
            String toLabel = words[i + 1];

            Node from = nodeMap.get((fromLabel));
            if (from == null) {
                from = createNode(fromLabel);
            }
            Node to = nodeMap.get((toLabel));
            if (to == null) {
                to = createNode(toLabel);
            }
            addEdge(from, to);
        }
        String fromLabel = words[words.length - 1];
        Node from = nodeMap.get(fromLabel);
        if (from == null) {
            List<Node> neighbors = new ArrayList<>();
            adjacencyList.put(from, neighbors);
        }
    }
    /**
     * 创建一个新节点.
     *
     * @param label 节点标签
     * @return 新创建的节点
     */
    public Node createNode(final String label) {
        Node newNode = new Node(label);
        nodeMap.put(label, newNode);
        return newNode;
    }

    /**
     * 查询两个单词之间的桥接词.
     *
     * @param word1 第一个单词
     * @param word2 第二个单词
     * @return 桥接词的字符串表示
     */
    String queryBridgeWords(final String word1, final String word2) {
        StringBuilder result = new StringBuilder();
        List<Node> bridgeWords = new ArrayList<>();
        Node node1 = nodeMap.get(word1);
        Node node2 = nodeMap.get(word2);
        if (!adjacencyList.containsKey(node1)
                && !adjacencyList.containsKey(node2)) {
            return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
        } else if (!adjacencyList.containsKey(node1)) {
            return "No \"" + word1 + "\" in the graph!";
        } else if (!adjacencyList.containsKey(node2)) {
            return "No \"" + word2 + "\" in the graph!";
        } else {
            for (Node neighbor : adjacencyList.get(node1)) {
                if (adjacencyList.get(neighbor).contains(node2)) {
                    bridgeWords.add(neighbor);
                }
            }
        }
        // 遍历word1的邻居，检查它们是否连接到word2
        if (!bridgeWords.isEmpty()) {
            result.append("The bridge words from ")
                    .append("\"").append(word1).append("\"")
                    .append(" to ").append("\"").append(word2).append("\"")
                    .append(" are: ");
            for (int i = 0; i < bridgeWords.size(); i++) {
                result.append(bridgeWords.get(i).getLabel());
                if (i < bridgeWords.size() - 2) {
                    result.append(", ");
                } else if (bridgeWords.size() > 1
                        && i == bridgeWords.size() - 2) {
                    result.append(" and ");
                }
            }
            result.append(".");
        } else {
            return "No bridge words from \""
                    + word1 + "\" to \"" + word2 + "\"!";
        }
        return result.toString();
    }

    /**
     * 根据桥接词生成新文本.
     *
     * @param inputText 输入的文本
     * @return 生成的新文本
     */
    String generateNewText(final String inputText) {
        // 将文本分割成单词列表
        String[] words = inputText.split("\\s+");

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            List<Node> bridgeWords = new ArrayList<>();
            Node node1 = nodeMap.get(words[i]);
            Node node2 = nodeMap.get(words[i + 1]);
            if (node1 == null || node2 == null) {
                result.append(words[i]).append(" ");
                continue;
            }
            for (Node neighbor : adjacencyList.get(node1)) {
                if (adjacencyList.get(neighbor).contains(node2)) {
                    bridgeWords.add(neighbor);
                }
            }
            if (!bridgeWords.isEmpty()) {
                // 随机选择一个桥接词
                Random random = new Random();
                Node bridgeWord = bridgeWords
                        .get(random.nextInt(bridgeWords.size()));
                result.append(words[i]).append(" ")
                        .append(bridgeWord).append(" ");
            } else {
                // 没有桥接词
                result.append(words[i]).append(" ");
            }
        }
        result.append(words[words.length - 1]);
        return result.toString();
    }

    /**
     * 计算两个单词之间的最短路径.
     *
     * @param word1 起始单词
     * @param word2   结束单词
     * @return 最短路径的字符串表示
     */
    String calcShortestPath(final String word1, final String word2) {
        StringBuilder result = new StringBuilder();
        Node startNode = nodeMap.get((word1));
        if (startNode == null) {
            return "No \"" + word1 + "\" in the graph!";
        }

        if (!word1.equals("") && word2.equals("")) {
            // 遍历图中所有节点，并计算从startNode到每个节点的最短路径
            int i = 1;
            // 存储i值到List<Node>的映射
            Map<Integer, List<Node>> shortestPathsMap = new HashMap<>();
            for (Node endNode : nodeMap.values()) {
                if (!endNode.getLabel().equals(word1)) { // 避免计算到自身的路径
                    Pair<List<Node>, Integer> shortestPath =
                            dijkstra(startNode, endNode);
                    if (shortestPath != null) {
                        // 将最短路径添加到map中，使用i作为键
                        shortestPathsMap.put(i, shortestPath.getFirst());
                        // 打印包含路径和长度的信息
                        System.out.println(i + ":" + "From \""
                                + word1 + "\" to \"" + endNode.getLabel()
                                + "\": "
                                + shortestPath.getFirst()
                                .stream().map(Node::getLabel)
                                .collect(Collectors.joining(" -> "))
                                + " (length: "
                                + shortestPath.getSecond() + ")");
                        i++;
                    } else {
                        // 打印没有路径的信息
                        System.out.println(
                                "No path from " + word1
                                        + " to " + endNode.getLabel());
                    }
                }
            }
            Scanner scanner = new Scanner(System.in);

            System.out.println("Do you want to see a path in the graph: Y/N");
            String choice1 = scanner.nextLine();
            while (!choice1.equals("Y") && !choice1.equals("N")) {
                System.out.println("Please input Y/N");
                choice1 = scanner.nextLine();
            }
            while (choice1.equals("Y")) {
                System.out.println("Choose a path: ");
                String choice2 = scanner.nextLine();
                int choice2int = Integer.parseInt(choice2);
                List<Node> nodeInPath = shortestPathsMap.get(choice2int);
                printGraphWithShortestPath(
                        "graph_shortest_path_user_choose.dot",
                        nodeInPath,
                        "graph_shortest_path_user_choose.png");

                System.out.println(
                        "Do you want to see a path in the graph: Y/N");
                choice1 = scanner.nextLine();
                while (!choice1.equals("Y") && !choice1.equals("N")) {
                    System.out.println("Please input Y/N");
                    choice1 = scanner.nextLine();
                }
            }
            return result.toString();
        }

        Node endNode = nodeMap.get((word2));

        if (endNode == null) {
            return "No \"" + word2 + "\" in the graph!";
        }

        Pair<List<Node>, Integer> shortestPath = dijkstra(startNode, endNode);
        if (shortestPath != null) {
            result.append("The shortest path is: ")
                    .append(shortestPath.getFirst()
                            .stream().map(Node::getLabel)
                            .collect(Collectors.joining(" -> ")));
            result.append("\n");
        } else {
            result.append("No road!");
        }
        printGraphWithShortestPath(
                "graph_with_shortest_path.dot",
                shortestPath.getFirst(),
                "graph_with_shortest_path.png");
        String len = shortestPath.getSecond().toString();
        result.append("The shortest path's len is: ").append(len);
        return result.toString();
    }

    /**
     * dijkstra.
     *
     * @param start 起始单词
     * @param end   结束单词
     * @return 最短路径和最短距离
     */
    public Pair<List<Node>, Integer> dijkstra(
            final Node start, final Node end) {
        // 初始化距离和已访问节点
        Map<Node, Integer> distances = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        Map<Node, Node> previous = new HashMap<>(); // 用于记录每个节点的父节点

        // 初始化距离
        for (Node node : adjacencyList.keySet()) {
            distances.put(node, node.equals(start) ? 0 : Integer.MAX_VALUE);
        }

        // 使用优先队列来存储待处理的节点
        PriorityQueue<Node> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(distances.get(a), distances.get(b)));
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            visited.add(current);

            // 遍历当前节点的所有邻居
            for (Node neighbor : adjacencyList.get(current)) {
                // 如果邻居节点未被访问过，并且通过当前节点到达邻居节点的距离更短
                if (!visited.contains(neighbor) && distances.get(current)
                        + current.getAdjacencies().get(neighbor)
                        < distances.get(neighbor)) {
                    distances.put(neighbor, distances.get(current)
                            + current.getAdjacencies().get(neighbor));
                    previous.put(neighbor, current); // 记录父节点
                    pq.add(neighbor);
                }
            }
        }
        // 构建并返回最短路径
        if (distances.get(end) != Integer.MAX_VALUE) {
            List<Node> path = new ArrayList<>();
            for (Node node = end; node != null; node = previous.get(node)) {
                path.add(node);
            }
            Collections.reverse(path); // 因为从end回溯到start，所以需要反转列表
            return new Pair<>(path, distances.get(end));
        }
        return new Pair<>(null, -1); // 没有找到路径
    }

    /**
     * dijkstra.
     *
     * @param dotFileName dot文件路径
     * @param shortestPath   最短路经
     * @param outImageFileName png文件路径
     */
    public void printGraphWithShortestPath(
            final String dotFileName,
            final List<Node> shortestPath, final String outImageFileName) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(dotFileName))) {
            writer.write("digraph G {\n");

            for (Map.Entry<Node, List<Node>> entry : adjacencyList.entrySet()) {
                Node from = entry.getKey();
                List<Node> toNodes = entry.getValue();

                for (Node to : toNodes) {
                    // 如果没有权重，默认为1
                    int weight = from.getAdjacencies().getOrDefault(to, 1);
                    String edgeColor = ""; // 默认边颜色

                    // 检查这条边是否在最短路径上，并设置不同的颜色
                    if (isInPath(from, to, shortestPath)) {
                        edgeColor = ", color=\"red\", penwidth=2"; // 假设用红色高亮显示
                    }
                    writer.write("\t\"" + from.getLabel()
                            + "\" -> \"" + to.getLabel() + "\" [label=\""
                            + weight + "\"" + edgeColor + "];\n");
                }
            }

            writer.write("}");
            System.out.println(
                    "Graph with shortest path has been written to "
                            + dotFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        showDirectedGraph(dotFileName, outImageFileName);
    }

    private boolean isInPath(final Node from,
                             final Node to,
                             final List<Node> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).equals(from) && path.get(i + 1).equals(to)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 打印路径.
     *
     * @param predecessors 前驱节点映射
     * @param target       目标节点
     * @param path         路径字符串构建器
     */
    private void printPath(final Map<Node, Node> predecessors,
                           final Node target, final StringBuilder path) {
        if (predecessors.get(target) != null) {
            printPath(predecessors, predecessors.get(target), path);
            path.append(" -> ");
        }
        path.append(target.getLabel());
    }

    /**
     * 随机游走.
     *
     * @return 随机游走的路径
     */
    String randomWalk() {
        StringBuilder result = new StringBuilder();
        List<Node> visitedNodes = new ArrayList<>();
        Set<Node> visitedSet = new HashSet<>();

        List<Node> nodes = new ArrayList<>(adjacencyList.keySet());
        Node beginNode = nodes.get(
                ThreadLocalRandom.current().nextInt(nodes.size()));
        Node currentNode = beginNode;

        while (!adjacencyList.getOrDefault(currentNode, Collections.emptyList())
                .isEmpty()) {
            // 将当前节点添加到已访问节点列表中
            visitedNodes.add(currentNode);
            visitedSet.add(currentNode);
            // 输出已访问的节点
            System.out.println("Visited nodes:");
            System.out.println(currentNode.getLabel());
            // 随机选择一个相邻节点
            List<Node> neighbors = new ArrayList<>(
                    currentNode.getAdjacencies().keySet());
            if (neighbors.isEmpty()) {
                // 如果没有相邻节点，则跳出循环
                break;
            }
            Node nextNode = neighbors.get(ThreadLocalRandom.current()
                    .nextInt(neighbors.size()));

            // 检查是否进入了一个已经访问过的节点（即出现了重复的边）
            if (visitedSet.contains(nextNode)) {
                break;
            }
            // 移动到下一个节点
            currentNode = nextNode;

            Scanner scanner = new Scanner(System.in);
            System.out.println("continue or stop?(1 or 0)");
            String choice = scanner.nextLine();
            while (!choice.equals("0") && !choice.equals("1")) {
                System.out.println("Please input 1 or 0?");
                choice = scanner.nextLine();
            }
            if (choice.equals("0")) {
                System.out.println("bye");
                break;
            }
        }
        // 将已访问的节点写入文件
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("random_walk_result.txt"))) {
            for (Node node : visitedNodes) {
                writer.write(node.getLabel());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 显示有向图.
     *
     * @param dotFilePath    dot文件路径
     * @param outputImagePath 输出文件路径
     */

    void showDirectedGraph(final String dotFilePath,
                           final String outputImagePath) {
        // 构造Graphviz dot命令
        String cmd = "dot -Tpng " + dotFilePath + " -o " + outputImagePath;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            // 读取并处理输出
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待命令执行完成
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(outputImagePath + " has been saved!");
            } else {
                System.out.println("Error!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
