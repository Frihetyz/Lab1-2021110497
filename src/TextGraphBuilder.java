import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TextGraphBuilder {

    private static class Node {
        String word;

        public Node(String word) {
            this.word = word;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return word.equalsIgnoreCase(node.word);
        }

        @Override
        public int hashCode() {
            return word.toLowerCase().hashCode();
        }
    }

    private static class Edge {
        Node from;
        Node to;
        int weight;

        public Edge(Node from, Node to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private Map<Node, Set<Edge>> graph = new HashMap<>();

    public void buildGraph(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            // 预处理文本
            String preprocessedLine = preprocess(line);

            // 分割单词并构建图
            String[] words = preprocessedLine.split("\\s+");
            for (int i = 0; i < words.length - 1; i++) {
                Node from = newNode(words[i]);
                Node to = newNode(words[i + 1]);
                addEdge(from, to);
            }
        }

        reader.close();
    }

    private String preprocess(String line) {
        // 替换标点符号和非字母字符为空格，并转换为小写
        // ... 实现预处理逻辑
        return line.replaceAll("[^a-zA-Z\\s]", " ").toLowerCase();
    }

    private Node newNode(String word) {
        Node node = new Node(word);
        graph.putIfAbsent(node, new LinkedHashSet<>());
        return node;
    }

    private void addEdge(Node from, Node to) {
        graph.get(from).add(new Edge(from, to, 1)); // 初始权重为1
        // 可以添加逻辑来检查是否已存在边并更新权重
    }

    // ... 其他方法，如打印图或将其写入文件

    public static void main(String[] args) {
        String filePath = "./src/textfile.txt"; // 替换为你的文件路径
        TextGraphBuilder builder = new TextGraphBuilder();
        try {
            builder.buildGraph(filePath);
            // ... 处理构建后的图
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
