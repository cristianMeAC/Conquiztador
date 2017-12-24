import java.awt.*;

public class Hilfsmethoden {

    // text in die mitte schrieben
    public static void drawCenteredString(Graphics2D g, String s, Point p){
        FontMetrics m = g.getFontMetrics(); // klasse die uebers aussehen mir infos gibt

        float breite = m.stringWidth(s); // breite des Strings
        float hoehe = m.getHeight(); // hoehe des Strings

        g.drawString(s, p.x-(breite/2), p.y+(hoehe/2));
    }
}
