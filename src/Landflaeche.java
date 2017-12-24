import java.awt.*;
import java.util.List;

// besteht aus koordinaten x und y (punkten)
public class Landflaeche {
    private int[] x;
    private int[] y;

    public Landflaeche(List<Point> punkte){
        this.x = new int [punkte.size()];
        this.y = new int [punkte.size()];

        int i = 0;
        for(Point a : punkte) {
            x[i] = a.x;
            y[i] = a.y;
            i++;
        }
    }

    // fuellt den hintergrund von meiner landflaeche
    public void drawBackground(Graphics2D g){
        g.fillPolygon(x, y, x.length);
    }

    // zeichnet die linien wenn es ausgewaehlt ist dicker
    // zeichne mein objekt g (2d objekt)
    public void drawForeground(Graphics2D g, boolean ausgewaehlt){
        g.setPaint(Color.black);
        g.setStroke(new BasicStroke(ausgewaehlt ? 5 : 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10.0f, null, 0.0f));
        g.drawPolygon(x,y,x.length);
    }

    // schaut ob in der landflaeche meine maus ist
    // mauspunkt wird mir als p übergeben
    public boolean isMouseInside(Point p) {
        Polygon pol = new Polygon(x, y, x.length);
        return pol.contains(p);
    }
}
