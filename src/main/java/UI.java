import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class UI implements Runnable {
    private String outputText = "";
    private String inputText = "";

    private Label netComp = null;
    private Label header = null;
    private Window window = null;
    private Screen screen = null;
    private TextBox textBox = null;
    private Label errorComp = null;

    UI () throws IOException {
        TerminalSize terminalSize = new TerminalSize(100,100);
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        window = new BasicWindow();

        TextBox textBox = new TextBox();
        netComp = new Label("");
        errorComp = new Label("Scanning for networks...");
        header = new Label("");

        textBox.setPreferredSize(terminalSize.withRows(1));
        netComp.setPreferredSize(terminalSize.withRows(1));
        errorComp.setPreferredSize(terminalSize.withRows(1));

        // Setup terminal and screen layers
        screen.startScreen();

        // Create panel to hold components
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new GridLayout(1));
        mainPanel.addComponent(header);
        mainPanel.addComponent(netComp.withBorder(Borders.doubleLine()), GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL,true,true));

        Panel textPanel = new Panel();
        textPanel.setLayoutManager(new GridLayout(1));
        textPanel.addComponent(errorComp, GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL,true,false));
        textPanel.addComponent(textBox, GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL,true,false));
        mainPanel.addComponent(textPanel.withBorder(Borders.doubleLine()), GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL,true,false));

        // Create window to hold the panel
        window.setTheme(new SimpleTheme(TextColor.ANSI.GREEN,TextColor.ANSI.BLACK));
        window.setComponent(mainPanel);

        }

    @Override
    public void run() {
        class KeyStrokeListener implements WindowListener {
            @Override
            public void onResized(Window window, TerminalSize terminalSize, TerminalSize terminalSize1) {
            }

            @Override
            public void onMoved(Window window, TerminalPosition terminalPosition, TerminalPosition terminalPosition1) {
            }

            @Override
            public void onInput(Window window, KeyStroke keyStroke, AtomicBoolean atomicBoolean) {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    inputText = textBox.getText();
                }
            }

            @Override
            public void onUnhandledInput(Window window, KeyStroke keyStroke, AtomicBoolean atomicBoolean) {
            }
        }

        window.addWindowListener(new KeyStrokeListener());

        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.WHITE));
        gui.addWindowAndWait(window);
    }

    public void printLine(String line) {
        this.outputText += line + '\n';
        this.netComp.setText(outputText);
    }

    public void clear() {
        this.outputText = "";
        this.netComp.setText(outputText);
    }

    public String getUserInput() {
        String toReturn = inputText;
        inputText = "";
        return toReturn;
    }

    public void setDefault(String text) {
        //this.defaultText = text;
        header.setText(text);
    }

    public void setStatus(String text) {
        this.errorComp.setText(text);
    }
}
