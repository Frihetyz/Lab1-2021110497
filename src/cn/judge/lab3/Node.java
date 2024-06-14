package cn.judge.lab3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Node类表示图中的一个节点.
 */
class Node {
    /**
     * 节点的标签.
     */
    private String label;

    /**
     * 邻接表，用于存储与该节点相邻的其他节点及其相邻次数.
     * 键是相邻的Node对象，值是相邻的次数.
     */
    private Map<Node, Integer> adjacencies = new HashMap<>();

    /**
     * 构造一个带有给定标签的新节点.
     *
     * @param nodelabel 节点的标签.
     */
    Node(final String nodelabel) {
        this.label = nodelabel;
    }

    /**
     * 获取节点的标签.
     *
     * @return 节点的标签.
     */
    public String getLabel() {
        return label;
    }

    /**
     * 设置节点的标签.
     *
     * @param newLabel 节点的新标签.
     */
    public void setLabel(final String newLabel) {
        this.label = newLabel;
    }

    /**
     * 获取节点的邻接表.
     *
     * @return 节点的邻接表.
     */
    public Map<Node, Integer> getAdjacencies() {
        return adjacencies;
    }

    /**
     * 设置节点的邻接表.
     *
     * @param newAdjacencies 节点的新邻接表.
     */
    public void setAdjacencies(final Map<Node, Integer> newAdjacencies) {
        this.adjacencies = newAdjacencies;
    }

    /**
     * 更新与另一个节点的相邻次数.
     * 如果该节点在邻接表中不存在，则添加它并设置相邻次数为1.
     * 如果已经存在，则将其相邻次数加1.
     *
     * @param to 目标节点.
     */
    public void incrementAdjacency(final Node to) {
        adjacencies.put(to, adjacencies.getOrDefault(to, 0) + 1);
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}
