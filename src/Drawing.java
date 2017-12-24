import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class Drawing extends JPanel{
    private Spielzug zug = Spielzug.Landerwerb; // land wird gerade erworben
    private Spielfeld feld;
    private int verfuegbar = 0; // wie viele armen ich
    private Territorium angeklickt = null;
    private boolean schonGezogen = false;
    private List<VonNach> gueltigeZuege = new LinkedList<VonNach>();
    private Polygon button;
    private boolean inButton = false;

    private MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            switch (zug) {
                case Landerwerb:{
                    landerwerbClick(e);
                    break;
                }
                case Verstaerkung:{
                    verstaerkungClick(e);
                    break;
                }
                case AngreifenBewegen:{
                    angreifenBewegenClick(e);
                    break;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    };

    private MouseMotionListener mml = new MouseMotionListener() {

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            feld.checkMousePosition(e.getPoint());
            if (button.contains(e.getPoint())){
                inButton = true;
            } else {
                inButton = false;
            }
            repaint(); // neu zeichnen
        }
    };

    // konstruktor
    // setzte spielfeld, füge MouseListener/MouseMotionListener hinzu, hingergrundfarbe, button zuweisung
    public Drawing(Spielfeld feld){

        this.feld = feld;
        this.addMouseListener(ml);
        this.addMouseMotionListener(mml);
        setBackground(new Color(0x00, 0x86, 0xd3));

        int w = Spielfeld.WIDTH;
        int h = Spielfeld.HEIGHT;
        this.setPreferredSize(new Dimension(w, h)); // spielfeld möchte die größe haben -> in class window durch pack geamcht
        this.button = new Polygon(new int[] {w-10,w-10,w-130,w-130}, new int[]{h-10, h-40, h-40, h-10}, 4);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        feld.draw(g2d);

        switch (zug) {
            case Verstaerkung:
                Hilfsmethoden.drawCenteredString(g2d, "Verstaerkung: " + verfuegbar, new Point(getWidth()/2, getHeight() - 20));
                break;
            case AngreifenBewegen:


                Hilfsmethoden.drawCenteredString(g2d, angeklickt != null? "angeklickt: " + angeklickt.getName() : "Bitte ein Land anklicken", new Point(getWidth()/2, getHeight() - 20));
                g2d.setPaint(Color.ORANGE);
                g2d.fillPolygon(button);
                g2d.setStroke(new BasicStroke(inButton?4:1));
                g2d.setPaint(Color.black);
                g2d.drawPolygon(button);
                Hilfsmethoden.drawCenteredString(g2d,"end this round :)", new Point(Spielfeld.WIDTH-70, Spielfeld.HEIGHT-27));
                break;
        }
    }


    public void landerwerbClick(MouseEvent e){
        Territorium a = feld.getTerritorium(e.getPoint());
        if (a != null && a.getBesitz() == Besitz.Niemand) {
            a.setBesitz(Besitz.Spieler);
            a.setBesetzung(1);
            feld.computerAuswaehlen();
            if (feld.alleFelderAufgeteilt() == true) {
                verfuegbar = feld.verstaerkung(Besitz.Spieler);
                zug = Spielzug.Verstaerkung;
            }
        }
        repaint();
    }

    public void verstaerkungClick(MouseEvent e){
        Territorium a = feld.getTerritorium(e.getPoint());
        if (a != null && a.getBesitz() == Besitz.Spieler && verfuegbar > 0) {
            verfuegbar -= 1;
            a.setBesetzung(a.getBesetzung() + 1);
            repaint();
        }

        if (verfuegbar <= 0) {
            feld.computerVerteilt();
            zug = Spielzug.AngreifenBewegen;
        }
        repaint();
    }

    public void angreifenBewegenClick(MouseEvent e){
        if (button.contains(e.getPoint())){
            //computer ist dran
            feld.pcAngriff();
            if (feld.gewonnen(Besitz.Computergegner)){
                // spiel ende
                repaint();
                JOptionPane.showMessageDialog(null, "Computer won!");
                System.exit(0);
            }
            //ich
            verfuegbar = feld.verstaerkung(Besitz.Spieler);
            schonGezogen = false;
            gueltigeZuege.clear();
            if (verfuegbar <= 0) {
                feld.computerVerteilt();
                zug = Spielzug.AngreifenBewegen;
            } else {
                zug = Spielzug.Verstaerkung;
            }
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (angeklickt != null) {
                angeklickt.setAngeklickt(false);
            }
            Territorium a = feld.getTerritorium(e.getPoint());
            if (a != null && a.getBesitz() == Besitz.Spieler) {
                if (a == angeklickt){
                    angeklickt.setAngeklickt(false);
                    angeklickt = null;
                }else {
                    angeklickt = a;
                    angeklickt.setAngeklickt(true);
                }
            } else {
                angeklickt = null;
            }
            repaint();
        } else if(angeklickt != null && e.getButton() == MouseEvent.BUTTON3) {
            // Wenn es ein angeklicktes gibt und man rechts klickt dann
            Territorium a = feld.getTerritorium(e.getPoint());
            if( a!=null && a.isneighbor(angeklickt) && angeklickt.getBesetzung() > 1) {
                if (a != null && a.getBesitz() == Besitz.Spieler) {
                    // Rueberschicken

                    boolean gueltig = false;
                    if(gueltigeZuege.contains(new VonNach(angeklickt, a))) {
                        gueltig = true;
                    }
                    if (!gueltig && !schonGezogen) {
                        gueltig = true;
                        schonGezogen = true;
                        gueltigeZuege.add(new VonNach(angeklickt, a));
                        gueltigeZuege.add(new VonNach(a, angeklickt));
                    }
                    if (gueltig) {
                        a.setBesetzung(a.getBesetzung() + 1);
                        angeklickt.setBesetzung(angeklickt.getBesetzung() - 1);
                    }
                    repaint();
                } else if (a != null && a.getBesitz() == Besitz.Computergegner) {
                    // Angriff: ich greife gegner (pc) an
                    angeklickt.attacke(a);
                    if (a.getBesitz() == Besitz.Spieler) {
                        gueltigeZuege.add(new VonNach(angeklickt, a));
                    }
                    repaint();

                    //ich gewinne
                    if (feld.gewonnen(Besitz.Spieler)){
                        // spiel ende
                        repaint();
                        JOptionPane.showMessageDialog(null, "Player won!");
                        System.exit(0);
                    }
                }
            }
        }
    }


}
