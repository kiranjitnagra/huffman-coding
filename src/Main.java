import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main class for the Huffman Coding Assignment
 */
public class Main {

  /**
   * Main Function for running the Huffman Compression on Text Files.
   *
   * @param args arguments
   */
  public static void main(String[] args) {

    /* Change this line to use another input text file */
    String inputFilePath = "./input.txt";
    String outputFilePath = "./output.txt";
    String treeSerialFilePath = "./tree.ser";
    String huffmanSerialFilePath = "./huffman.ser";

    try {

      String text = new String(Files.readAllBytes(Paths.get(inputFilePath)));

      if (text.length() == 0) {
        System.out.println("Empty File");
        return;
      }

      System.out.println("Encoding...");
      new HuffmanEncode(text, treeSerialFilePath, huffmanSerialFilePath);

      System.out.println("Decoding...");
      new HuffmanDecode(outputFilePath, treeSerialFilePath, huffmanSerialFilePath);

      long huffmanBytes = Files.size(Paths.get(huffmanSerialFilePath));
      long treeBytes = Files.size(Paths.get(treeSerialFilePath));
      long inputBytes = Files.size(Paths.get(inputFilePath));
      long outputBytes = Files.size(Paths.get(outputFilePath));

      long totalCompressed = huffmanBytes + treeBytes;

      System.out.println();
      System.out.printf(huffmanSerialFilePath + " %,d bytes%n", huffmanBytes);
      System.out.printf(treeSerialFilePath + "    %,d bytes%n%n", treeBytes);

      float compressionRatio = (float) inputBytes / (float) totalCompressed;
      System.out.println("Compression Ratio: " + compressionRatio + "\n");

      System.out.printf("  Compressed File Size: %,d bytes%n", totalCompressed);
      System.out.printf("    Original File Size: %,d bytes%n", inputBytes);
      System.out.printf("Decompressed File Size: %,d bytes%n%n", outputBytes);

      boolean checkFilesMatch = true;
      if (inputBytes == outputBytes) {
        System.out.println("Checking contents of original and decompressed files...");

        BufferedInputStream bin = new BufferedInputStream(
            new FileInputStream(new File(inputFilePath)));
        BufferedInputStream bout = new BufferedInputStream(
            new FileInputStream(new File(outputFilePath)));

        int i = bin.read();
        int j = bout.read();

        /* Checking if each byte matches for each file. */
        while (i != -1) {
          if (i != j) {
            checkFilesMatch = false;
            break;
          }
          i = bin.read();
          j = bout.read();
        }
        bin.close();
        bout.close();
      } else {
        checkFilesMatch = false;
      }

      if (checkFilesMatch) {
        System.out.println("Original and Decompressed Contents Match :)");
      } else {
        System.out.println("Original and Decompressed Contents DO NOT Match :(");
      }

    } catch (IOException e) {
      System.out.println("IO Exception");
      e.printStackTrace();
    }
  }
}
