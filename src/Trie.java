public class Trie {

    private static final int ALPHABET_SIZE = 26;
    private static final char FIRST_LETTER = 'a';

    private final Node root;
    private int wordCount;

    public Trie() {
        this.root = new Node();
        this.wordCount = 0;
    }

    private static class Node {
        Node[] children;
        boolean isTerminal;
        int passCount;

        Node() {
            children = new Node[ALPHABET_SIZE];
            isTerminal = false;
            passCount = 0;
        }
    }

    private String normalize(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Слово или префикс не должны быть null");
        }
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Слово или префикс не должны быть пустыми");
        }
        String lower = trimmed.toLowerCase();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if (c < 'a' || c > 'z') {
                throw new IllegalArgumentException(
                        "Допустимы только буквы a-z. Недопустимый символ: '" + c + "'."
                );
            }
        }
        return lower;
    }

    public void insert(String word) {
        String w = normalize(word);
        if (contains(w)) {
            return;
        }
        Node current = root;
        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);
            int index = c - FIRST_LETTER;
            if (current.children[index] == null) {
                current.children[index] = new Node();
            }
            current = current.children[index];
            current.passCount++;
        }
        current.isTerminal = true;
        wordCount++;
    }

    public boolean contains(String word) {
        String w = normalize(word);
        Node node = traverseNormalized(w);
        return node != null && node.isTerminal;
    }

    public boolean startsWith(String prefix) {
        String p = normalize(prefix);
        Node node = traverseNormalized(p);
        return node != null;
    }

    private Node traverseNormalized(String normalized) {
        Node current = root;
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            int index = c - FIRST_LETTER;
            Node next = current.children[index];
            if (next == null) {
                return null;
            }
            current = next;
        }
        return current;
    }

    public String[] getByPrefix(String prefix) {
        String p = normalize(prefix);
        Node node = traverseNormalized(p);
        if (node == null) {
            return new String[0];
        }
        int total = countByPrefix(p);
        String[] result = new String[total];
        StringBuilder builder = new StringBuilder();
        builder.append(p);
        int[] indexHolder = new int[1];
        // тут я обхожу поддерево и добавляю найденные слова в массив
        collectWords(node, builder, result, indexHolder);
        return result;
    }

    private void collectWords(Node node, StringBuilder prefix, String[] result, int[] indexHolder) {
        if (node.isTerminal) {
            result[indexHolder[0]] = prefix.toString();
            indexHolder[0]++;
        }
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            Node child = node.children[i];
            if (child != null) {
                char c = (char) (FIRST_LETTER + i);
                prefix.append(c);
                collectWords(child, prefix, result, indexHolder);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    public int size() {
        return wordCount;
    }

    public int countByPrefix(String prefix) {
        String p = normalize(prefix);
        Node node = traverseNormalized(p);
        if (node == null) {
            return 0;
        }
        return node.passCount;
    }

    public boolean remove(String word) {
        String w = normalize(word);
        if (!contains(w)) {
            return false;
        }
        // тут я рекурсивно удаляю слово и решаю, нужно ли очищать узлы
        remove(root, w, 0);
        wordCount--;
        return true;
    }

    private boolean remove(Node node, String word, int depth) {
        if (depth == word.length()) {
            if (!node.isTerminal) {
                return false;
            }
            node.isTerminal = false;
            return isEmptyNode(node);
        }
        char c = word.charAt(depth);
        int index = c - FIRST_LETTER;
        Node child = node.children[index];
        if (child == null) {
            return false;
        }
        boolean shouldDeleteChild = remove(child, word, depth + 1);
        child.passCount--;
        if (shouldDeleteChild) {
            node.children[index] = null;
        }
        return !node.isTerminal && isEmptyNode(node) && node != root;
    }

    private boolean isEmptyNode(Node node) {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (node.children[i] != null) {
                return false;
            }
        }
        return true;
    }

    public String toDot() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph Trie {\n");
        sb.append("  node [shape=circle];\n");
        int[] idCounter = new int[1];
        buildDot(root, 0, sb, idCounter);
        sb.append("}\n");
        return sb.toString();
    }

    private void buildDot(Node node, int nodeId, StringBuilder sb, int[] idCounter) {
        String label;
        if (nodeId == 0) {
            label = "корень";
        } else {
            label = node.isTerminal ? "слово" : "узел";
        }
        sb.append("  n").append(nodeId).append(" [label=\"").append(label).append("\"];\n");
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            Node child = node.children[i];
            if (child != null) {
                int childId = ++idCounter[0];
                char c = (char) (FIRST_LETTER + i);
                sb.append("  n")
                        .append(nodeId)
                        .append(" -> n")
                        .append(childId)
                        .append(" [label=\"")
                        .append(c)
                        .append("\"];\n");
                buildDot(child, childId, sb, idCounter);
            }
        }
    }

    @Override
    public String toString() {
        if (wordCount == 0) {
            return "Дерево пусто";
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder prefix = new StringBuilder();
        printAllWords(root, prefix, sb);
        return sb.toString();
    }

    private void printAllWords(Node node, StringBuilder prefix, StringBuilder out) {
        if (node.isTerminal) {
            out.append(prefix.toString()).append('\n');
        }
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            Node child = node.children[i];
            if (child != null) {
                char c = (char) (FIRST_LETTER + i);
                prefix.append(c);
                printAllWords(child, prefix, out);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }
}
