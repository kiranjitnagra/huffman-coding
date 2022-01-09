import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * HuffmanDecode Class for decompression
 */
public class HuffmanDecode {

  /**
   * The decoded original text
   */
  private StringBuilder origString;

  /**
   * Constructor Function for this HuffmanDecode class.
   * <p>
   * Decompresses the compressed files in treeSerFilePath and huffmanSerFilePath and outputs the
   * decoded text to the output file in outputFilePath.
   *
   * @param outputFilePath     : String of the path to output file
   * @param treeSerFilePath    : String of the path to tree serialization file
   * @param huffmanSerFilePath : String of the path to huffman serialization file
   */
  public HuffmanDecode(String outputFilePath, String treeSerFilePath, String huffmanSerFilePath) {

    Node tree;
    try {
      FileInputStream treeFile = new FileInputStream(treeSerFilePath);
      ObjectInputStream treeObj = new ObjectInputStream(treeFile);
      tree = (Node) treeObj.readObject();
      treeObj.close();
      treeFile.close();
    } catch (IOException i) {
      System.out.println("IO Exception");
      i.printStackTrace();
      return;
    } catch (ClassNotFoundException c) {
      System.out.println("ClassNotFound Exception");
      c.printStackTrace();
      return;
    }

    byte[] huffmanBytesArray;
    try {
      FileInputStream huffmanFile = new FileInputStream(huffmanSerFilePath);
      ObjectInputStream huffmanObj = new ObjectInputStream(huffmanFile);
      huffmanBytesArray = (byte[]) huffmanObj.readObject();
      huffmanObj.close();
      huffmanFile.close();
    } catch (IOException i) {
      System.out.println("IO Exception");
      i.printStackTrace();
      return;
    } catch (ClassNotFoundException c) {
      System.out.println("ClassNotFound Exception");
      c.printStackTrace();
      return;
    }

    StringBuilder huffmanCode = new StringBuilder();
    for (int i = 0; i < huffmanBytesArray.length - 1; i++) {
      byte b = huffmanBytesArray[i];
      StringBuilder bv = new StringBuilder(Integer.toBinaryString(Byte.toUnsignedInt(b)));
      while (bv.length() < 8) {
        bv.insert(0, "0");
      }
      huffmanCode.append(bv);
    }

    int zeroNum = 0;
    byte lastByte = huffmanBytesArray[huffmanBytesArray.length - 1];
    int lastByteValue = Byte.toUnsignedInt(lastByte);

    /* Special Case when lastByteValue does not represents number of zeros used for padding */
    if (lastByteValue > 7) {
      /* Add the last byte */
      StringBuilder bv = new StringBuilder(
          Integer.toBinaryString(Byte.toUnsignedInt(lastByte)));
      while (bv.length() < 8) {
        bv.insert(0, "0");
      }
      huffmanCode.append(bv);
    } else {
      zeroNum = lastByteValue + 1;
    }

    if (huffmanCode.length() != 0) {
      origString = new StringBuilder();
      int i = -1;
      int huffmanCodeLimit = huffmanCode.length() - zeroNum - 1;
      if (tree != null) {
        if (tree.leftNode == null && tree.rightNode == null) {
          /* Special Case when tree is only one node */
          int f = tree.freq;
          while (f > 0) {
            origString.append(tree.value);
            f -= 1;
          }
        } else {
          while (i < huffmanCodeLimit) {
            i = decodeChar(tree, i, huffmanCode);
          }
        }
      }

      BufferedWriter outputFile;
      try {
        outputFile = new BufferedWriter(new FileWriter(outputFilePath));
        outputFile.write(origString.toString());
        outputFile.close();
      } catch (IOException e) {
        System.out.println("IO Exception");
        e.printStackTrace();
      }
    } else {
      System.out.println("huffmanCode is empty");
    }
  }

  /**
   * Return the current index in the huffmanCode when one character decoded (leaf node reached in n)
   * or decoding complete.
   *
   * @param n           Root Node of the huffman binary tree
   * @param i           Integer of the current index in huffmanCode
   * @param huffmanCode StringBuilder of the complete huffman code
   * @return Integer of the current index in the huffmanCode
   */
  public int decodeChar(Node n, int i, StringBuilder huffmanCode) {
    if (n == null) {
      return i;
    } else if (n.leftNode == null && n.rightNode == null) {
      origString.append(n.value);
      return i;
    }
    i += 1;
    if (huffmanCode.charAt(i) == '0') {
      n = n.leftNode;
    } else {
      n = n.rightNode;
    }
    i = decodeChar(n, i, huffmanCode);
    return i;
  }
}
