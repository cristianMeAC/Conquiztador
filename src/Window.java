import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window extends JFrame{

    public static void main(String[] args) {

        Spielfeld feld = new Spielfeld();

        String filename;
        if (args.length < 1) {
            System.err.println("Fehlendes Argument!");
            return;
        }
        filename = args[0];
        if(!feld.load(filename)) {
            return;
        }
        Window w = new Window(feld);
        w.setVisible(true);
        w.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 27) { // 27 = escape :P
                    System.exit(0); // 0 = erfolgreich beendet :)
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

    }

    private Spielfeld feld;

    public Window(Spielfeld feld) {
        this.feld = feld;

        this.add(new Drawing(this.feld));
        this.pack();

        setResizable(false); // fenstergroesse nicht mehr aenderrbar
        this.setTitle("All Those Territories");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
