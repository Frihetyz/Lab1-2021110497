import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class Node {
    String label;
    // 邻接表，用于存储与该节点相邻的其他节点及其相邻次数: 键是相邻的Node对象，值是相邻的次数
    Map<Node, Integer> adjacencies = new HashMap<>();
    Node(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }

    // 更新与另一个节点的相邻次数: 如果该节点在邻接表中不存在，则添加它并设置相邻次数为1; 如果已经存在，则将其相邻次数加1
    public void incrementAdjacency(Node to) {
        adjacencies.put(to, adjacencies.getOrDefault(to, 0) + 1);
    }

    @Override
    public String toString() {
        return label;
    }

    // 节点的标签是唯一的，通过比较标签来判断两个节点是否相等
    @Override
    public boolean equals(Object o) {
        // 如果两个引用指向同一个对象，则认为它们相等
        if (this == o) return true;
        // 如果o为null或o的类型与当前对象不同，则认为它们不相等
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}

class DirectedGraph {
    Map<Node, List<Node>> adjacencyList = new HashMap<>();
    private Map<String, Node> nodeMap = new HashMap<>();

    public void addEdge(Node from, Node to) {
        // 更新边的权重（相邻次数）
        from.incrementAdjacency(to);

        // 确保from的邻接列表存在，然后检查to是否已经在列表中
        List<Node> toNodes = adjacencyList.computeIfAbsent(from, k -> new ArrayList<>());
        if (!toNodes.contains(to)) {
            toNodes.add(to);
        }
    }

    public void buildGraphFromText(String text) {
        // 使用空格替换标点符号和换行符，并转换为小写（可选）
        text = text.replaceAll("[^a-zA-Z\\s]", " ").toLowerCase();
        // 按空格分割文本为单词数组
        String[] words = text.split("\\s+");
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
        List<Node> neighbors = new ArrayList<>();
        adjacencyList.put(from, neighbors);
    }

    public Node createNode(String label) {
        Node newNode = new Node(label);
        nodeMap.put(label, newNode);
        return newNode;
    }

    // 功能需求2：展示有向图
    void showDirectedGraph(String dotFilePath,String outputImagePath) {
        // 构造Graphviz dot命令
        String cmd = "dot -Tpng " + dotFilePath + " -o " + outputImagePath;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            // 读取并处理输出（如果需要）
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
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

    // 功能需求3：查询桥接词
    String queryBridgeWords(String word1, String word2) {
        StringBuilder result = new StringBuilder();
        List<Node> bridgeWords = new ArrayList<>();
        Node node1 = nodeMap.get(word1);
        Node node2 = nodeMap.get(word2);
        if (!adjacencyList.containsKey(node1) && !adjacencyList.containsKey(node2)) {
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
            result.append("The bridge words from ").append("\"").append(word1).append("\"").append(" to ").append("\"").append(word2).append("\"").append(" are: ");
            for (int i = 0; i < bridgeWords.size(); i++) {
                result.append(bridgeWords.get(i).getLabel());
                if (i < bridgeWords.size() - 2) {
                    result.append(", ");
                } else if (bridgeWords.size() > 1 && i == bridgeWords.size() - 2) {
                    result.append(" and ");
                }
            }
            result.append(".");
        } else {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }
        return result.toString();
    }

    // 功能需求4：根据bridge word生成新文本
    String generateNewText(String inputText) {
        // 将文本分割成单词列表
        String[] words = inputText.split("\\s+");

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            List<Node> bridgeWords = new ArrayList<>();
            Node node1 = nodeMap.get(words[i]);
            Node node2 = nodeMap.get(words[i + 1]);
            if(node1 == null||node2 == null) {
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
                Node bridgeWord = bridgeWords.get(random.nextInt(bridgeWords.size()));
                result.append(words[i]).append(" ").append(bridgeWord).append(" ");
            } else {
                // 没有桥接词
                result.append(words[i]).append(" ");
            }
        }
        result.append(words[words.length - 1]).append(" ");
        return result.toString();
    }

    // 功能需求5：计算两个单词之间的最短路径
    String calcShortestPath(String word1, String word2) {
        StringBuilder result = new StringBuilder();
        Node startNode = nodeMap.get((word1));
        if (startNode == null) {
            return "\"" + word1 + "\"" + " is not exist!";
        }

        if(!word1.equals("") && word2.equals("")) {
            // 遍历图中所有节点，并计算从startNode到每个节点的最短路径
            int i = 1;
            Map<Integer, List<Node>> shortestPathsMap = new HashMap<>(); // 存储i值到List<Node>的映射
            for (Node endNode : nodeMap.values()) {
                if (!endNode.getLabel().equals(word1)) { // 避免计算到自身的路径
                    Pair<List<Node>, Integer> shortestPath = dijkstra(startNode, endNode);
                    if (shortestPath != null) {
                        // 将最短路径添加到map中，使用i作为键
                        shortestPathsMap.put(i, shortestPath.getFirst());
                        // 打印包含路径和长度的信息
                        System.out.println(i + ":" + "From \"" + word1 + "\" to \"" + endNode.getLabel() + "\": " +
                                shortestPath.getFirst().stream().map(Node::getLabel).collect(Collectors.joining(" -> ")) +
                                " (length: " + shortestPath.getSecond() + ")");
                        i++;
                    } else {
                        // 打印没有路径的信息
                        System.out.println("No path from " + word1 + " to " + endNode.getLabel());
                    }
                }
            }
            Scanner scanner = new Scanner(System.in);

            System.out.println("Do you want to see a path in the graph: Y/N");
            String choice1 = scanner.nextLine();
            while(!choice1.equals("Y") && !choice1.equals("N")) {
                System.out.println("Please input Y/N");
                choice1 = scanner.nextLine();
            }
            while(choice1.equals("Y")) {
                System.out.println("Choose a path: ");
                String choice2 = scanner.nextLine();
                int choice2_int = Integer.parseInt(choice2);
                List<Node> nodeInPath = shortestPathsMap.get(choice2_int);
                printGraphWithShortestPath("graph_shortest_path_user_choose.dot", nodeInPath, "graph_shortest_path_user_choose.png");

                System.out.println("Do you want to see a path in the graph: Y/N");
                choice1 = scanner.nextLine();
                while(!choice1.equals("Y") && !choice1.equals("N")) {
                    System.out.println("Please input Y/N");
                    choice1 = scanner.nextLine();
                }
            }
            return result.toString();
        }

        Node endNode = nodeMap.get((word2));

        if (endNode == null) {
            return "\"" + word2 + "\"" + " is not exist!";
        }

        Pair<List<Node>, Integer> shortestPath = dijkstra(startNode, endNode);
        if (shortestPath != null) {
            result.append("The shortest path is: ").append(shortestPath.getFirst().stream().map(Node::getLabel).collect(Collectors.joining(" -> ")));
            result.append("\n");
        } else {
            result.append("No road!");
        }
        printGraphWithShortestPath("graph_with_shortest_path.dot", shortestPath.getFirst(), "graph_with_shortest_path.png");
        String len = shortestPath.getSecond().toString();
        result.append("The shortest path's len is: ").append(len);
        return result.toString();
    }

    public Pair<List<Node>, Integer> dijkstra(Node start, Node end) {
        // 初始化距离和已访问节点
        Map<Node, Integer> distances = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        Map<Node, Node> previous = new HashMap<>(); // 用于记录每个节点的父节点

        // 初始化距离
        for (Node node : adjacencyList.keySet()) {
            distances.put(node, node.equals(start) ? 0 : Integer.MAX_VALUE);
        }

        // 使用优先队列来存储待处理的节点
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Integer.compare(distances.get(a), distances.get(b)));
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            visited.add(current);

            // 遍历当前节点的所有邻居
            for (Node neighbor : adjacencyList.get(current)) {
                // 如果邻居节点未被访问过，并且通过当前节点到达邻居节点的距离更短
                if (!visited.contains(neighbor) && distances.get(current) + current.adjacencies.get(neighbor) < distances.get(neighbor)) {
                    distances.put(neighbor, distances.get(current) + current.adjacencies.get(neighbor));
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

    private boolean isInPath(Node from, Node to, List<Node> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).equals(from) && path.get(i + 1).equals(to)) {
                return true;
            }
        }
        return false;
    }

    public void printGraphWithShortestPath(String dotFileName, List<Node> shortestPath, String outImageFileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFileName))) {
            writer.write("digraph G {\n");

            for (Map.Entry<Node, List<Node>> entry : adjacencyList.entrySet()) {
                Node from = entry.getKey();
                List<Node> toNodes = entry.getValue();

                for (Node to : toNodes) {
                    int weight = from.adjacencies.getOrDefault(to, 1); // 如果没有权重，默认为1
                    String edgeColor = ""; // 默认边颜色

                    // 检查这条边是否在最短路径上，并设置不同的颜色
                    if (isInPath(from, to, shortestPath)) {
                        edgeColor = ", color=\"red\", penwidth=2"; // 假设用红色高亮显示
                    }

                    writer.write("\t\"" + from.label + "\" -> \"" + to.label + "\" [label=\"" + weight + "\"" + edgeColor + "];\n");
                }
            }

            writer.write("}");
            System.out.println("Graph with shortest path has been written to " + dotFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        showDirectedGraph(dotFileName,outImageFileName);
    }

    // 功能需求6：随机游走
    String randomWalk() {
        StringBuilder result = new StringBuilder();
        List<Node> visitedNodes = new ArrayList<>();
        Set<Node> visitedSet = new HashSet<>();

        List<Node> nodes = new ArrayList<>(adjacencyList.keySet());
        Node beginNode = nodes.get(ThreadLocalRandom.current().nextInt(nodes.size()));
        Node currentNode = beginNode;

        while (!adjacencyList.getOrDefault(currentNode, Collections.emptyList()).isEmpty()) {
            // 将当前节点添加到已访问节点列表中
            visitedNodes.add(currentNode);
            visitedSet.add(currentNode);
            // 输出已访问的节点
            System.out.println("Visited nodes:");
            System.out.println(currentNode.getLabel());
            // 随机选择一个相邻节点
            List<Node> neighbors = new ArrayList<>(currentNode.adjacencies.keySet());
            if (neighbors.isEmpty()) {
                // 如果没有相邻节点，则跳出循环
                break;
            }
            Node nextNode = neighbors.get(ThreadLocalRandom.current().nextInt(neighbors.size()));

            // 检查是否进入了一个已经访问过的节点（即出现了重复的边）
            if (visitedSet.contains(nextNode)) {
                break;
            }
            // 移动到下一个节点
            currentNode = nextNode;

            Scanner scanner = new Scanner(System.in);
            System.out.println("continue or stop?(1 or 0)");
            String choice = scanner.nextLine();
            while(!choice.equals("0")&&!choice.equals("1")) {
                System.out.println("Please input 1 or 0?");
                choice = scanner.nextLine();
            }
            if (choice.equals("0")) {
                System.out.println("bye");
                break;
            }
        }
        // 将已访问的节点写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("random_walk_result.txt"))) {
            for (Node node : visitedNodes) {
                writer.write(node.getLabel());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}

class Pair<T, U> {
    private T first;
    private U second;
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
    public T getFirst() {
        return first;
    }
    public U getSecond() {
        return second;
    }

}

public class Main {
    public static void main(String[] args) {
        String filePath = "./src/textfile.txt";
        DirectedGraph graph = new DirectedGraph();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

        }catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to choose a task: Y/N");
        String choice1 = scanner.nextLine();
        while(!choice1.equals("Y")&&!choice1.equals("N")) {
            System.out.println("Please input Y/N");
            choice1 = scanner.nextLine();
        }
        while(choice1.equals("Y")){
            System.out.println("Please choose a task: ");
            System.out.println("1.展示有向图");
            System.out.println("2.查询桥接词（bridge words）");
            System.out.println("3.根据bridge word生成新文本");
            System.out.println("4.计算两个单词之间的最短路径");
            System.out.println("5.随机游走");
            String choice2 = scanner.nextLine();
            if(choice2.equals("1")) {
                // 功能需求2：展示有向图
                String dotFileName = "graph.dot";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFileName))) {
                    writer.write("digraph G {\n");

                    for (Map.Entry<Node, List<Node>> entry : graph.adjacencyList.entrySet()) {
                        Node from = entry.getKey();
                        List<Node> toNodes = entry.getValue();

                        for (Node to : toNodes) {
                            // 访问from节点的adjacencies映射以获取与to节点的权重
                            int weight = from.adjacencies.getOrDefault(to, 1); // 如果没有权重，默认为1
                            writer.write("\t\"" + from.label + "\" -> \"" + to.label + "\" [label=\"" + weight + "\"];\n");
                        }
                    }

                    writer.write("}");
                    System.out.println("Graph has been written to " + dotFileName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                graph.showDirectedGraph("graph.dot","graph.png");
            }

            if(choice2.equals("2")) {
                // 功能需求3：查询桥接词（bridge words）
                System.out.println("Enter word1:");
                String word1Str = scanner.nextLine().toLowerCase();
                System.out.println("Enter word2:");
                String word2Str = scanner.nextLine().toLowerCase();
                System.out.println(graph.queryBridgeWords(word1Str, word2Str));
            }

            if(choice2.equals("3")) {
                // 功能需求4：根据bridge word生成新文本
                System.out.println("Enter a new text:");
                String newText = scanner.nextLine().toLowerCase();
                System.out.println(graph.generateNewText(newText));
            }

            if(choice2.equals("4")) {
                // 功能需求5：计算两个单词之间的最短路径
                System.out.print("Enter startNode: ");
                String startLabel = scanner.nextLine().toLowerCase();
                System.out.print("Enter endNode: (or press Enter for empty string) ");
                String endLabel = scanner.nextLine().toLowerCase();
                System.out.println(graph.calcShortestPath(startLabel, endLabel));
            }

            if(choice2.equals("5")) {
                // 功能需求6：随机游走
                System.out.println(graph.randomWalk());
            }

            System.out.println("Do you want to choose a task: Y/N");
            choice1 = scanner.nextLine();
            while(!choice1.equals("Y")&&!choice1.equals("N")) {
                System.out.println("Please input Y/N");
                choice1 = scanner.nextLine();
            }
        }
    }
}