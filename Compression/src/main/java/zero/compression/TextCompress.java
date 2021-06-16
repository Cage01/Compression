/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zero.compression;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author Mason
 */
class Node {

    Character ch;
    Integer freq;

    Node left, right = null;

    public Node(Character ch, Integer freq) {
        this.freq = freq;
        this.ch = ch;
    }

    public Node(Character ch, Integer freq, Node left, Node right) {
        this.freq = freq;
        this.ch = ch;
        this.left = left;
        this.right = right;
    }
}

public class TextCompress {

    public static void main(String[] args) {
        String text = "Testing compression for this string here.";

        buildTree(text);
    }

    private static void buildTree(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        //Map each character and how often it appears
        Map<Character, Integer> charFreq = new HashMap<>();

        for (int i = 0; i < text.toCharArray().length; i++) {
            charFreq.put(text.charAt(i), charFreq.getOrDefault(text.charAt(i), 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>(charFreq.size(), Comparator.comparingInt(l -> l.freq));

        for (Map.Entry<Character, Integer> entry : charFreq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() != 1) {
            Node left = pq.poll();
            Node right = pq.poll();

            int sum = left.freq + right.freq;
            pq.add(new Node(null, sum, left, right));
        }

        Node root = pq.peek();

        //Traversing the tree and storing the codes
        Map<Character, String> huffmanCode = new HashMap<>();
        encode(root, "", huffmanCode);

        // Print the Huffman codes
        System.out.println("Huffman Codes are: " + huffmanCode);
        System.out.println("The original string is: " + text);

        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(huffmanCode.get(c));
        }
        System.out.println("The encoded string is: " + sb);

        System.out.println("The decoded string is: ");
        if (isLeaf(root)) {
            // Special case: For input like a, aa, aaa, etc.
            while (root.freq-- > 0) {
                System.out.print(root.ch);
            }
        } else {
            // Traverse the Huffman Tree again and this time,
            // decode the encoded string
            int index = -1;
            while (index < sb.length() - 1) {
                index = decode(root, index, sb);
            }
        }
    }

    private static boolean isLeaf(Node root) {
        return root.left == null && root.right == null;
    }

    private static void encode(Node root, String str, Map<Character, String> huffmanCode) {
        if (root == null) {
            return;
        }
 
        // Found a leaf node
        if (isLeaf(root)) {
            huffmanCode.put(root.ch, str.length() > 0 ? str : "1");
        }
 
        encode(root.left, str + '0', huffmanCode);
        encode(root.right, str + '1', huffmanCode);
    }

    private static int decode(Node root, int index, StringBuilder sb) {
        if (root == null) {
            return index;
        }

        // Found a leaf node
        if (isLeaf(root)) {
            System.out.print(root.ch);
            return index;
        }

        index++;

        root = (sb.charAt(index) == '0') ? root.left : root.right;
        index = decode(root, index, sb);
        return index;
    }
}
