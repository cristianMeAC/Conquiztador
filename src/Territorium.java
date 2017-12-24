import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.util.LinkedList;

// besteht aus landfaechen
public class Territorium {
    private String name;
    private List<Territorium> neighbors = new LinkedList<Territorium>();
    private boolean ausgewaehlt = false; // Maus drueber fahren
    private boolean angeklickt = false; // Maus anklicken
    private List<Landflaeche> landflaechen =  new LinkedList<Landflaeche>(); // landflaechen von diesen territorium
    private int besetzung = 0;
    private Point captial_of; //hauptstadt vom territorium, Besetzung wird dort hingezeichnet
    private Besitz besitz = Besitz.Niemand;

    public int getBesetzung() {
        return besetzung;
    }

    public void setBesetzung(int besetzung) {
        this.besetzung = besetzung;
    }

    public Point getCaptial_of() {
        return captial_of;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAngeklickt() {
        return angeklickt;
    }

    public void setAngeklickt(boolean angeklickt) {
        this.angeklickt = angeklickt;
    }

    public Besitz getBesitz() {
        return besitz;
    }

    public void setBesitz(Besitz besitz) {
        this.besitz = besitz;
    }

    public Territorium(String name){
        this.name = name;
    }

    public void setCapital(Point a){
        this.captial_of = a;
    }

    public void setNeighbors(List<Territorium> a){
        this.neighbors = a;
    }

    // zeichne flaeche; wems gehoert farbe
    public void drawBackground(Graphics2D g){
        for (Landflaeche l : landflaechen) {
            switch(besitz){
                case Spieler:
                    if (angeklickt){
                        g.setPaint(Color.yellow.darker());
                    } else {
                        g.setPaint(Color.yellow);
                    }
                    break;
                case Computergegner: g.setPaint(Color.red);
                    break;
                case Niemand: g.setPaint(Color.gray);
                    break;
            }

            l.drawBackground(g);
        }
    }

    // zeichne linie
    public void drawForeground(Graphics2D g){
        for (Landflaeche l : landflaechen) {
            l.drawForeground(g, ausgewaehlt);
        }
        Hilfsmethoden.drawCenteredString(g, besetzung + "", this.getCaptial_of());
    }

    public void add (Landflaeche lf){
        landflaechen.add(lf);
    }

    // liefert ture wenn ich in irgendeiner landflaeche bin
    public boolean isMouseInside(Point point) {
        for (Landflaeche l : landflaechen){
            if (l.isMouseInside(point)){
                return true;
            }
        }
        return false;
    }

    public void setAusgewaehlt(boolean ausgewaehlt) {
        this.ausgewaehlt = ausgewaehlt;
    }

    public List<Territorium> neighbors() {
        return neighbors;
    }

    // ich bin nachbar von t oder t ist nachbar von mir
    public boolean isneighbor(Territorium t){
        return neighbors.contains(t) || t.neighbors.contains(this);
    }


    // this greift verteidiger an (wenn ich angreife)
    public void attacke(Territorium verteidiger){
        int verlustAngreifer = 0; //mein verlust
        int verlustVerteidiger = 0; // sein verlust
        int anzahlAngreifer = Math.min(3, this.getBesetzung()-1);
        int anzahlVerteidiger = Math.min(2, verteidiger.getBesetzung());

        int[] angreiferWuerfel = new int[anzahlAngreifer];
        int[] verteidigerWuerfel = new int[anzahlVerteidiger];

        //befuelle array mit zufallszahl 1-6
        for (int i = 0; i < anzahlAngreifer ; i++) {
            angreiferWuerfel[i] =(int)((Math.random() * 6) +1);
        }
        //befuelle array mit zufallszahl 1-6
        for (int i = 0; i < anzahlVerteidiger; i++) {
            verteidigerWuerfel[i] = (int) ((Math.random()*6)+1);
        }

        Arrays.sort(angreiferWuerfel);
        Arrays.sort(verteidigerWuerfel);

        //vergleichen der wuerfel und zaehle mit wer gewinnt/verliert
        for (int i = 0; i < Math.min(anzahlAngreifer, anzahlVerteidiger); i++) {
            if (angreiferWuerfel[anzahlAngreifer-1-i] > verteidigerWuerfel[anzahlVerteidiger-1-i]){
                // angreifer gewinnt
                verlustVerteidiger++;
            } else {
                //verteidiger gewinnt
                verlustAngreifer++;
            }
        }

        //verringere meine besetzung um meinen verlsut
        this.setBesetzung(this.getBesetzung()-verlustAngreifer);
        //verringere verteidiger um seinen verlust
        verteidiger.setBesetzung(verteidiger.getBesetzung()-verlustVerteidiger);

        // Besetzung vom verteidiger ist 0  -> dann besetzte ich sein land
        if (verteidiger.getBesetzung() == 0){
            verteidiger.setBesitz(this.getBesitz());
            verteidiger.setBesetzung(1);
            this.setBesetzung(this.getBesetzung()-1);
        }
    }
}