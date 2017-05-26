package editor;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ScrollBar;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import java.util.ListIterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.beans.value.ObservableValue;

/**
 * A JavaFX application that displays the letter the user has typed most recently in the center of
 * the window. Pressing the up and down arrows causes the font size to increase and decrease,
 * respectively.
 */
public class Editor extends Application {
    private static int WINDOW_WIDTH = 500;
    private static int WINDOW_HEIGHT = 500;
    private TextLinkedList typed = new TextLinkedList();
    private double textXPos;
    private double textYPos;
    private Rectangle rectangleCursor;
    private String filename;
    private int height;
    private ArrayList firstNodes;
    private ScrollBar scrollBar = new ScrollBar();
    private int fontSize;
    private int scrollBarWidth;

    /** An EventHandler to handle keys that get pressed. */
    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        private Text positionText;
        private int mousePressedX;
        private int mousePressedY;

        @Override
        public void handle(MouseEvent mouseEvent) {
            Node pointer;
            firstNodes = Rerendering.firstChar();
            mousePressedX = (int) Math.round(mouseEvent.getX());
            mousePressedY = (int) Math.round(mouseEvent.getY());
            int index = (int) Math.floor(mousePressedY / height);
            if (index >= firstNodes.size()) {
                pointer = (Node) firstNodes.get(firstNodes.size() - 1);
                rectangleCursor.setY((int) Math.round(pointer.text.getY()));
            } else {
                pointer = (Node) firstNodes.get(index);
                rectangleCursor.setY((int) pointer.text.getY());
            } if (mousePressedX <= 5) {
                rectangleCursor.setX(5);
                typed.cursor.next = pointer.prev;
            } else {
                int y = (int) pointer.text.getY();
                while (pointer != typed.sentinel) {
                    int xPrev = Math.abs((int) (mousePressedX - pointer.text.getX()));
                    int xNext = Math.abs((int) (mousePressedX - (pointer.text.getX() + pointer.text.getLayoutBounds().getWidth())));
                    if (pointer.text.getY() != y) {
                        pointer = pointer.prev;
                        typed.cursor.next = pointer;
                        while ((int) Math.round(pointer.text.getX() + pointer.text.getLayoutBounds().getWidth()) > WINDOW_WIDTH - scrollBarWidth - 5) {
                            pointer = pointer.prev;
                        }
                        rectangleCursor.setX((int) Math.round(pointer.text.getX() + pointer.text.getLayoutBounds().getWidth()));
                        typed.cursor.next = pointer;
                        return;
                    } else if (xPrev == Math.min(xPrev, xNext) && !pointer.text.getText().equals("\r")) {
                        rectangleCursor.setX(pointer.text.getX());
                        typed.cursor.next = pointer.prev;
                        return;
                    } 
                    pointer = pointer.next;
                }
                pointer = pointer.prev;
                    rectangleCursor.setX((int) Math.round(pointer.text.getX() + pointer.text.getLayoutBounds().getWidth()));
                    typed.cursor.next = pointer;
            }
        }
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {
        private static final int STARTING_FONT_SIZE = 12;
        private static final int STARTING_TEXT_POSITION_X = 5;
        private static final int STARTING_TEXT_POSITION_Y = 0;

        /** The Text to display on the screen. */
        private Text displayText = new Text(STARTING_TEXT_POSITION_X, STARTING_TEXT_POSITION_Y, "");
        Group textroot;
        

        private String fontName = "Verdana";

        KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            fontSize = STARTING_FONT_SIZE;
            this.textroot = root;
            textXPos = STARTING_TEXT_POSITION_X;
            textYPos = 0;
            Text displayText = new Text(STARTING_TEXT_POSITION_X, STARTING_TEXT_POSITION_Y, "");
            displayText.setFont(Font.font(fontName, fontSize));
            height = (int) Math.round(displayText.getLayoutBounds().getHeight());
            // All new Nodes need to be added to the root in order to be displayed.s
            rectangleCursor = new Rectangle(5, 0, 1, height);
        }
        
        public void save() {
            ListIterator<Text> textIter = typed.iterator();
            try {
                BufferedWriter savefile = new BufferedWriter(new FileWriter(filename));
                while (textIter.hasNext()) {
                    String savetext = textIter.next().getText();
                    if (savetext.equals("\r")) {
                        savetext = "\n";
                    }
                    savefile.write(savetext);
                }
                savefile.close();
            } catch (IOException ioException) {
                System.out.println("Error when saving; exception was: " + ioException);
            }
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {
                String charTyped = keyEvent.getCharacter();
                Text newCharac = new Text(rectangleCursor.getX(), rectangleCursor.getY(), charTyped);
                if (charTyped.equals("\r")) {
                    typed.addText(newCharac);
                    Rerendering.wordWrapping(typed, fontName, fontSize, WINDOW_WIDTH - scrollBarWidth, height);
                    rectangleCursor.setX((int) newCharac.getX());
                    rectangleCursor.setY((int) newCharac.getY());
                } else if (charTyped.length() > 0 && charTyped.charAt(0) != 8) {
                    typed.addText(newCharac);
                    textroot.getChildren().add(newCharac);
                    Rerendering.wordWrapping(typed, fontName, fontSize, WINDOW_WIDTH - scrollBarWidth, height);
                    if (typed.cursor.next.next != typed.sentinel && typed.cursor.next.next.text.getX() == 5) {
                        rectangleCursor.setX(STARTING_TEXT_POSITION_X);
                        rectangleCursor.setY(newCharac.getY() + height);
                    } else if ((int) Math.round(newCharac.getX() + newCharac.getLayoutBounds().getWidth()) < WINDOW_WIDTH - scrollBarWidth - 5) {
                        rectangleCursor.setX((int) Math.round(newCharac.getX() + newCharac.getLayoutBounds().getWidth()));
                        rectangleCursor.setY(newCharac.getY());
                    }
                }
                firstNodes = Rerendering.firstChar();
                if (firstNodes.size() != 0) {
                    Node x = (Node) firstNodes.get(firstNodes.size() - 1);
                    if (x.text.getY() + height > WINDOW_HEIGHT) {
                        scrollBar.setMax((int) Math.round((x.text.getY() + height) - WINDOW_HEIGHT));
                        scrollBar.setValue((int) Math.round(rectangleCursor.getY() + rectangleCursor.getHeight()) - WINDOW_HEIGHT);
                        }
                    }
                keyEvent.consume();
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.UP) {
                    Node pointer;
                    firstNodes = Rerendering.firstChar();
                    if (rectangleCursor.getY() != 0) {
                        rectangleCursor.setY((int) Math.round(rectangleCursor.getY() - height));
                        int index = (int) Math.floor(rectangleCursor.getY() / height);
                            if (index > firstNodes.size()) {
                            pointer = (Node) firstNodes.get(index);
                            while (pointer != typed.sentinel) {
                                int xPrev = Math.abs((int) (rectangleCursor.getX() - pointer.text.getX()));
                                int xNext = Math.abs((int) (rectangleCursor.getX() - (pointer.text.getX() + pointer.text.getLayoutBounds().getWidth())));
                                if (Math.min(xPrev, xNext) == xPrev) {
                                    rectangleCursor.setX(pointer.text.getX());
                                    typed.cursor.next = pointer.prev;
                                    return;
                                } else if ((int) Math.round(pointer.text.getY()) != rectangleCursor.getY()) {
                                    pointer = pointer.prev;
                                    typed.cursor.next = pointer;
                                    rectangleCursor.setX((int) Math.round(pointer.text.getX() + pointer.text.getLayoutBounds().getWidth()));
                                    return;
                                }
                                pointer = pointer.next;
                            }
                        }
                    }
                    if (((int) Math.round(rectangleCursor.getY()) == 0)) {
                        scrollBar.setValue(-rectangleCursor.getY());
                        textroot.setLayoutY(rectangleCursor.getHeight());
                    }
                } else if (code == KeyCode.DOWN) {
                    Node pointer;
                    firstNodes = Rerendering.firstChar();
                    int nextHeight = (int) rectangleCursor.getY() + height;
                    int index = (int) Math.floor(nextHeight / height);
                    if (index < firstNodes.size()) {
                        pointer = (Node) firstNodes.get(index);
                        rectangleCursor.setY(nextHeight);
                        while (pointer != typed.sentinel) {
                            int xPrev = Math.abs((int) (rectangleCursor.getX() - pointer.text.getX()));
                            int xNext = Math.abs((int) (rectangleCursor.getX() - (pointer.text.getX() + pointer.text.getLayoutBounds().getWidth())));
                            if (xPrev == Math.min(xPrev, xNext)) {
                                rectangleCursor.setX(pointer.text.getX());
                                typed.cursor.next = pointer.prev;
                                break;
                            } else if (pointer.text.getY() != rectangleCursor.getY()) {
                                pointer = pointer.prev;
                                typed.cursor.next = pointer;
                                rectangleCursor.setX((int) Math.round(pointer.text.getX() + pointer.text.getLayoutBounds().getWidth()));
                                break;
                            }
                            pointer = pointer.next;
                        }
                        pointer = pointer.prev;
                        typed.cursor.next = pointer;
                        rectangleCursor.setX((int) Math.round(pointer.text.getX() + pointer.text.getLayoutBounds().getWidth()));
                    }
                    if ((int) Math.round(rectangleCursor.getY() + rectangleCursor.getHeight()) > WINDOW_HEIGHT) {
                        scrollBar.setValue((int) Math.round(rectangleCursor.getY() + rectangleCursor.getHeight()) - WINDOW_HEIGHT);
                        textroot.setLayoutY(-scrollBar.getValue());
                    }
                } else if (code == KeyCode.RIGHT) {
                    if (typed.cursor.next.next != typed.sentinel) {
                        typed.cursor.next = typed.cursor.next.next;
                        if (typed.cursor.next.next != typed.sentinel && typed.cursor.next.text.getY() != typed.cursor.next.next.text.getY()) {
                            if (typed.cursor.next.next.text.getText().equals("\r")) {
                                rectangleCursor.setX(STARTING_TEXT_POSITION_X);
                                rectangleCursor.setY(typed.cursor.next.text.getY() + height);
                            }
                        }
                        if ((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()) <= WINDOW_WIDTH - scrollBarWidth - 5){
                            rectangleCursor.setX((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()));
                            rectangleCursor.setY(typed.cursor.next.text.getY());
                        }
                    }
                } else if (code == KeyCode.LEFT) {
                    if (typed.cursor.next != typed.sentinel) {
                        typed.cursor.next = typed.cursor.next.prev;
                        if (typed.cursor.next == typed.sentinel || typed.cursor.next.next.text.getX() == STARTING_TEXT_POSITION_X) {
                            if (!typed.cursor.next.next.text.getText().equals("\r")) {
                                rectangleCursor.setX(STARTING_TEXT_POSITION_X);
                            }
                        }
                        if ((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()) < WINDOW_WIDTH - scrollBarWidth - 5) {
                                // rectangleCursor.setX(typed.cursor.next.next.text.getX());
                                // rectangleCursor.setY(typed.cursor.next.next.text.getY());
                            rectangleCursor.setX((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()));
                            rectangleCursor.setY(typed.cursor.next.text.getY());
                        }
                    }
                } else if (code == KeyCode.BACK_SPACE) {
                    Text deletedText = typed.delete();
                    if (deletedText == null) {
                        rectangleCursor.setX(STARTING_TEXT_POSITION_X);
                        rectangleCursor.setY(STARTING_TEXT_POSITION_Y);
                    } else if (deletedText.getText().equals("\r")) {
                        Rerendering.wordWrapping(typed, fontName, fontSize, WINDOW_WIDTH - scrollBarWidth, height);
                        rectangleCursor.setX((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()));
                        rectangleCursor.setY((int) typed.cursor.next.text.getY());
                    } else {
                        Rerendering.wordWrapping(typed, fontName, fontSize, WINDOW_WIDTH - scrollBarWidth, height);
                        if (typed.cursor.next == typed.sentinel) {
                            rectangleCursor.setX(STARTING_TEXT_POSITION_X);
                            rectangleCursor.setY(STARTING_TEXT_POSITION_Y);
                        } else {
                            if ((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()) > WINDOW_WIDTH - scrollBarWidth - 5) {
                                rectangleCursor.setX(5);
                            } else {
                                rectangleCursor.setX((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()));
                                rectangleCursor.setY(typed.cursor.next.text.getY());
                            }
                        }
                        textroot.getChildren().remove(deletedText);
                    }
                    firstNodes = Rerendering.firstChar();
                    if (firstNodes.size() != 0) {
                        Node x = (Node) firstNodes.get(firstNodes.size() - 1);
                        if (x.text.getY() + height > WINDOW_HEIGHT) {
                            scrollBar.setMax((int) Math.round((x.text.getY() + height) - WINDOW_HEIGHT));
                        } else {
                            scrollBar.setMax(0);
                        }
                    }
                } else if (keyEvent.isShortcutDown()) {
                    if (keyEvent.getCode() == KeyCode.P) {
                        System.out.println(rectangleCursor.getX() + ", " + rectangleCursor.getY());
                    } else if (keyEvent.getCode() == KeyCode.S) {
                        save();
                    } else if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                        fontSize += 4;
                        displayText.setFont(Font.font(fontName, fontSize));
                        height = (int) Math.round(displayText.getLayoutBounds().getHeight());
                        Rerendering.wordWrapping(typed, fontName, fontSize, WINDOW_WIDTH - scrollBarWidth, height);
                        firstNodes = Rerendering.firstChar();
                        rectangleCursor.setHeight(height);
                        if (typed.cursor.next != typed.sentinel) {
                            rectangleCursor.setX(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth());
                            rectangleCursor.setY(typed.cursor.next.text.getY());
                        }
                    } else if (code == KeyCode.MINUS) {
                        fontSize = Math.max(4, fontSize - 4);
                        displayText.setFont(Font.font(fontName, fontSize));
                        height = (int) Math.round(displayText.getLayoutBounds().getHeight());
                        Rerendering.wordWrapping(typed, fontName, fontSize, WINDOW_WIDTH - scrollBarWidth, height);
                        firstNodes = Rerendering.firstChar();
                        rectangleCursor.setHeight(height);
                        if (typed.cursor.next != typed.sentinel) {
                            rectangleCursor.setX(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth());
                            rectangleCursor.setY(typed.cursor.next.text.getY());
                        }
                    }
                }
            }
        }
    }

    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors = {Color.BLACK, Color.TRANSPARENT};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            rectangleCursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    public void makeRectangleColorChange() {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
    private int getDimensionInsideMargin(int outsideDimension) {
        return outsideDimension - 2 * 5;
    }

    @Override
    public void start(Stage primaryStage) {
         // Create a Node that will be the parent of all things displayed on the screen.
        Group root = new Group();
        Group textroot = new Group();
        root.getChildren().add(textroot);
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler = 
            new KeyEventHandler(textroot, WINDOW_WIDTH, WINDOW_HEIGHT);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler());

        textroot.getChildren().add(rectangleCursor);
        makeRectangleColorChange();

        // This is boilerplate, necessary to setup the window where things are displayed.

        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        scrollBar.setMin(0);
        scrollBar.setMax(0);
        root.getChildren().add(scrollBar);
        scrollBarWidth = (int) Math.round(scrollBar.getLayoutBounds().getWidth());
        double usableScreenWidth = (int) Math.round(WINDOW_WIDTH - scrollBarWidth);
        scrollBar.setLayoutX(usableScreenWidth);

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                textroot.setLayoutY(-newValue.doubleValue());
            }
        });

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                // Re-compute width.
                int newWidth = getDimensionInsideMargin(newScreenWidth.intValue());
                WINDOW_WIDTH = newWidth;
                Rerendering.wordWrapping(typed, "Verdana", fontSize, WINDOW_WIDTH - scrollBarWidth, height);
                if ((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()) > WINDOW_WIDTH - scrollBarWidth - 5) {
                    rectangleCursor.setX(5);
                    rectangleCursor.setY(typed.cursor.next.text.getY() + height);
                } else {
                    rectangleCursor.setX((int) Math.round(typed.cursor.next.text.getX() + typed.cursor.next.text.getLayoutBounds().getWidth()));
                    rectangleCursor.setY(typed.cursor.next.text.getY());
                }
                scrollBar.setLayoutX((int) Math.round(WINDOW_WIDTH - scrollBarWidth));
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                int newHeight = getDimensionInsideMargin(newScreenHeight.intValue());
                WINDOW_HEIGHT = newHeight;
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        filename = getParameters().getRaw().get(0);
        primaryStage.setTitle(filename);

        if (filename == null) {
            System.out.println("No filename is provided");
            System.exit(1);
        }
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return;
            }
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            int intRead = -1;
            while ((intRead = bufferedReader.read()) != -1) {
                char charRead = (char) intRead;
                Text openText = new Text(Character.toString(charRead));
                typed.addText(openText);
                textroot.getChildren().add(openText);
            }
            Rerendering.wordWrapping(typed, "Verdana", 12, WINDOW_WIDTH - scrollBarWidth, height);
            firstNodes = Rerendering.firstChar();
            if (firstNodes.size() != 0) {
                Node lastChar = (Node) firstNodes.get(firstNodes.size() - 1);
                if (lastChar.text.getY() + height > WINDOW_HEIGHT) {
                        scrollBar.setMax((int) Math.round((lastChar.text.getY() + height) - WINDOW_HEIGHT));
                    }
            }
            typed.cursor.next = typed.sentinel;
            bufferedReader.close();
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("File not found!" + fileNotFound);
        } catch (IOException ioException) {
            System.out.println("Error!" + ioException);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
