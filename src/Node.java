import java.io.Serializable;

/**
 * Node class
 */
public class Node implements Comparable<Node>, Serializable {

  /**
   * Character Value
   */
  public char value;

  /**
   * Character Frequency
   */
  public int freq;

  /**
   * Left Node of this Node
   */
  public Node leftNode;

  /**
   * Right Node of this Node
   */
  public Node rightNode;

  /**
   * Empty Constructor of this Node class
   */
  public Node() {
  }

  /**
   * Constructor of this Node class
   *
   * @param value Character Value
   * @param freq  Integer of the Character Frequency
   */
  public Node(char value, int freq) {
    this.value = value;
    this.freq = freq;
  }

  /**
   * Comparing another Node class o based on Character Frequency. Used for PriorityQueue in
   * HuffmanEncode.
   *
   * @param o Another Node class
   * @return Integer of the difference between the two Node Character Frequencies
   */
  public int compareTo(Node o) {
    return this.freq - o.freq;
  }
}
