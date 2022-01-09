import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * HuffmanEncode class for compression
 */
public class HuffmanEncode {

  /**
   * Constructor Function for this HuffmanEncode class.
   * <p>
   * Creates the huffman binary tree and saves it as Node class in treeSerFilePath. Creates the
   * huffman binary codes and saves it as a byte[] in huffmanSerFilePath.
   *
   * @param text               String to do the huffman coding on
   * @param treeSerFilePath    String of the path to tree serialization file
   * @param huffmanSerFilePath String of the path to huffman serialization file
   */
  public HuffmanEncode(String text, String treeSerFilePath, String huffmanSerFilePath) {

    /* Array of the characters in text */
    char[] textCharArray = text.toCharArray();

    Hashtable<Character, Integer> freqTable = new Hashtable<>();
    Hashtable<Character, String> huffmanCode = new Hashtable<>();

    /* I used a Priority Queue as it eliminates sorting the nodes after all the nodes are
     * added. I referred to https://en.wikipedia.org/wiki/Huffman_coding#Compression for
     * the huffman algorithm with Priority Queues and for help with PriorityQueues in Java
     * https://www.freecodecamp.org/news/priority-queue-implementation-in-java/. */
    PriorityQueue<Node> nodes = new PriorityQueue<>();

    for (char c : textCharArray) {

      /* Initializes the frequency for each character if empty or gets it value in freqTable */
      int f = freqTable.getOrDefault(c, 0);

      freqTable.put(c, f + 1);
    }

    for (Entry<Character, Integer> e : freqTable.entrySet()) {
      char value = e.getKey();
      int freq = e.getValue();
      nodes.add(new Node(value, freq));
    }

    /* Node with the lowest frequency is always removed first from the Priority Queue,
     * as I added the compareTo function in the Node Class */
    while (nodes.size() > 1) {
      Node f = new Node();
      Node d = nodes.poll();
      Node e = nodes.poll();
      if (e != null) {
        f.freq = d.freq + e.freq;
        f.leftNode = d;
        f.rightNode = e;
        nodes.add(f);
      }
    }

    Node rootNode = nodes.peek();
    encodeEachChar(rootNode, "", huffmanCode);

    FileOutputStream treeFile;
    ObjectOutputStream treeObj;
    try {
      treeFile = new FileOutputStream(treeSerFilePath);
      treeObj = new ObjectOutputStream(treeFile);
      treeObj.writeObject(rootNode);
      treeObj.close();
      treeFile.close();
    } catch (IOException e) {
      System.out.println("IO Exception");
      e.printStackTrace();
      return;
    }

    ByteArrayOutputStream huffmanBytes = new ByteArrayOutputStream();
    byte b = 0;
    int count = 0;
    for (char c : textCharArray) {
      String code = huffmanCode.get(c);
      for (char d : code.toCharArray()) {
        if (d == '1') {
          b += 1;
        }
        if (count == 7) {
          huffmanBytes.write(b);
          count = 0;
          b = 0;
        } else {
          b *= 2;
          count += 1;
        }
      }
    }

    /* zeroNum is the number of zeros used for padding, I also save zeroNum in
     * huffmanSerFilePath so the padding zeros are not used when decoding */
    int zeroNum;
    if (count != 0) {
      zeroNum = 7 - count;
      for (int i = 0; i < zeroNum; i++) {
        b *= 2;
      }
      huffmanBytes.write(b);
      huffmanBytes.write((byte) zeroNum);
    }

    byte[] huffmanBytesArray = huffmanBytes.toByteArray();
    FileOutputStream huffmanFile;
    ObjectOutputStream huffmanObj;
    try {
      huffmanFile = new FileOutputStream(huffmanSerFilePath);
      huffmanObj = new ObjectOutputStream(huffmanFile);
      huffmanObj.writeObject(huffmanBytesArray);
      huffmanObj.close();
      huffmanFile.close();
    } catch (IOException e) {
      System.out.println("IO Exception");
      e.printStackTrace();
    }
  }

  /**
   * Creates the huffman code for each Character based on the huffman binary tree.
   *
   * @param n    Root Node of the huffman binary tree
   * @param bits String of the Cumulative huffman code
   * @param h    Hashtable that maps each char to it's huffman code
   */
  private void encodeEachChar(Node n, String bits, Hashtable<Character, String> h) {
    if (n == null) {
      return;
    } else if (n.leftNode == null && n.rightNode == null) {
      if (bits.length() > 0) {
        h.put(n.value, bits);
      } else {
        h.put(n.value, "1");
      }
    }
    if (n.leftNode != null) {
      encodeEachChar(n.leftNode, bits + "0", h);
    }
    if (n.rightNode != null) {
      encodeEachChar(n.rightNode, bits + "1", h);
    }
  }
}