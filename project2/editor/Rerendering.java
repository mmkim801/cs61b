package editor;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.ListIterator;
import java.util.ArrayList;

public class Rerendering {
    private static ArrayList<Node> firstNodes;
    private static Node pointer;

    public static void wordWrapping(TextLinkedList typed, String font, int size, double width, int y) {
        firstNodes = new ArrayList<Node>();
        pointer = typed.sentinel.next;
        Text character = pointer.text;
        int textXPos = 5;
        int textYPos = 0;
        int numSpace = 0;
        int height = y;
        firstNodes.add(pointer);
        while (pointer != typed.sentinel) {
            character = pointer.text;
            character.setTextOrigin(VPos.TOP);
            character.setFont(Font.font(font, size));
            if (character.getText().equals("\r")) {
                textXPos = 5;
                textYPos += height;
            } else if (character.getText().equals(" ")) {
                numSpace += 1;
            } else if (textXPos + (int) Math.round(character.getLayoutBounds().getWidth()) >= width - 5) {
                if (numSpace == 0) { 
                    textXPos = 5;
                    textYPos += height;
                } else if (character.getText().equals(" ")) {
                    textXPos += (int) Math.round(character.getLayoutBounds().getWidth());
                    continue;
                } else {
                    while (!pointer.prev.text.getText().equals(" ")) {
                        pointer = pointer.prev;
                    }
                    textXPos = 5;
                    textYPos += height;
                    numSpace = 0;
                    continue;
                }
            }
            character.setX(textXPos);
            character.setY(textYPos);
            if (textXPos == 5 && pointer.prev != typed.sentinel && pointer.text.getY() != pointer.prev.text.getY()) {
                firstNodes.add(pointer);
            }
            pointer = pointer.next;
            textXPos += (int) Math.round(character.getLayoutBounds().getWidth());
        }
    }
    public static ArrayList firstChar() {
        return firstNodes;
    }
}
