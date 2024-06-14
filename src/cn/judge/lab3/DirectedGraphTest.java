package cn.judge.lab3;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectedGraphTest {
    private DirectedGraph graph;

    @Before
    public void setUp() {
        graph = new DirectedGraph();
    }

    @Test
    public void testQueryBridgeWords() {
        graph.buildGraphFromText("hello world java hello java world zyt hello world tyz hello");

        printTestResult("python", "ruby", "No \"python\" and \"ruby\" in the graph!", graph.queryBridgeWords("python", "ruby"));
        printTestResult("python", "world", "No \"python\" in the graph!", graph.queryBridgeWords("python", "world"));
        printTestResult("hello", "python", "No \"python\" in the graph!", graph.queryBridgeWords("hello", "python"));
        printTestResult("hello", "world", "The bridge words from \"hello\" to \"world\" are: java.", graph.queryBridgeWords("hello", "world"));
        printTestResult("world", "hello", "The bridge words from \"world\" to \"hello\" are: java, zyt and tyz.", graph.queryBridgeWords("world", "hello"));
        printTestResult("java", "hello", "No bridge words from \"java\" to \"hello\"!", graph.queryBridgeWords("java", "hello"));
        printTestResult("java", "java", "The bridge words from \"java\" to \"java\" are: hello and world.", graph.queryBridgeWords("java", "java"));
    }

    private void printTestResult(String word1, String word2, String expected, String actual) {
        boolean passed = expected.equals(actual);
        System.out.println("Input1: " + word1);
        System.out.println("Input2: " + word2);
        System.out.println("Expected Output: '" + expected + "'");
        System.out.println("Actual Output: " + actual);
        System.out.println("Test " + (passed ? "PASSED" : "FAILED"));
        System.out.println();
        assertEquals(expected, actual);
    }
}
