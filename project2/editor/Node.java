package editor;

import javafx.scene.text.Text;

public class Node {
        protected Node prev;
        protected Text text;
        protected Node next;

        public Node(Node p, Text t, Node n) {
            prev = p;
            text = t;
            next = n;
    }
}