package editor;
import javafx.scene.text.Text;
import java.util.ListIterator;

public class TextLinkedList {

    protected Node sentinel;
    protected Node cursor;
    protected int size;

    public TextLinkedList() {
        sentinel = new Node(null, null, null);
        cursor = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        cursor.next = sentinel;
        cursor.prev = cursor;
        size = 0;
    }

    public void addText(Text input) {
        cursor.next.next = new Node(cursor.next, input, cursor.next.next);
        cursor.next.next.next.prev = cursor.next.next;
        cursor.next = cursor.next.next;
        size += 1;
    }

    public Text delete() {
        if (cursor.next == sentinel) {
            return null;
        }
        Node removed = cursor.next;
        cursor.next = cursor.next.prev;
        cursor.next.next = cursor.next.next.next;
        cursor.next.next.prev = cursor.next;
        size -= 1;
        return removed.text;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public ListIterator<Text> iterator() {
        return new TextListIterator();
    }

    public ListIterator<Text> iterator(Node start) {
        return new TextListIterator(start);
    }


    public class TextListIterator implements ListIterator<Text> {
        public Node pointer;
        private Node front;

        TextListIterator() {
            pointer = sentinel.next;
            front = sentinel;
        }

        TextListIterator(Node start) {
            pointer = start.next;
            front = sentinel;
        }

        public boolean hasNext() {
            if (pointer == front) {
                return false;
            }
            return true; 
        }

        public Text next() {
            Text nextText = pointer.text;
            pointer = pointer.next;
            return nextText;
        }

        public boolean hasPrevious() {
            if (pointer == front) {
                return false;
            }
            return true;
        }

        public Text previous() {
            Text prevText = pointer.text;
            pointer = pointer.prev;
            return prevText;
        }

        public void add(Text t) {
        }

        public int nextIndex() {
            return -100;
        }

        public int previousIndex() {
            return -100;
        } 

        public void remove() {
        }

        public void set(Text text) {
        }
    }
}
