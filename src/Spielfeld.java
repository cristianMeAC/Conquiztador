import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

// map territorien: alle territorium
// map kontinente : alle kontinent
public class Spielfeld {
    public static final int WIDTH = 1250;
    public static final int HEIGHT = 650;

    private Map<String, Territorium> territorien = new HashMap<String, Territorium>();  // alle territorien
    private Map<String, Kontinent> kontinente = new HashMap<String, Kontinent>();   // alle kontinente

    // liest die datei ein
    public boolean load(String filename){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String s = br.readLine();
            while  (s!=null){
                if (s.equals("")){
                    s = br.readLine();
                    continue;
                }

                Scanner scr = new Scanner(s);
                String kommando = scr.next();
                if (kommando.equals("patch-of")){
                    patch_of(scr);
                } else if( kommando.equals("capital-of")){
                    capitalof(scr);
                } else if(kommando.equals("neighbors-of")){
                    neighbors(scr);
                } else if(kommando.equals("continent")){
                    continent(scr);
                } else {
                    System.err.println("Fehler: unbekannter Befehl. " + kommando);
                    return false;
                }


                s = br.readLine();
            }
        } catch (IOException e){
            System.err.println("Datei konnte nicht geladen werden.");
            System.err.println(e);
            return false;
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("Datei konnte nicht geschlossen werden.");
                    System.err.println(e);
                    return false;
                }
            }
        }
        return true;
    }


    // fuegt eine neue landflaeche mit eingelesenen punkten hinzu
    public void patch_of(Scanner scr){
        String nameterr = nameOfTerritorium(scr);
        List<Point>  punkte = new LinkedList<Point>();

        while(scr.hasNextInt()){
            int x = scr.nextInt();
            int y = scr.nextInt();
            punkte.add(new Point(x,y));
        }

        Territorium a = createOrGet(nameterr);
        a.add(new Landflaeche(punkte));
    }

    // setzt bei einem territorium capital von territorium
    public void capitalof(Scanner scr){
        String nameterr = nameOfTerritorium(scr);
        Point p = null;

        if (scr.hasNextInt()) {
            int x = scr.nextInt();
            int y = scr.nextInt();
            p = new Point(x, y);
        }

        Territorium a = createOrGet(nameterr);
        a.setCapital(p);
    }

    // setzt nachbaren beim territorum
    public void neighbors(Scanner scr){
        String nameterr = liestBis(scr, ":");
        List<Territorium> nachbar = new LinkedList<Territorium>();
        while (scr.hasNext()){
            String a = liestBis(scr, "-");
            nachbar.add(createOrGet(a));
        }
        Territorium tmp = createOrGet(nameterr);
        tmp.setNeighbors(nachbar);

    }

    // setzt einen neuen Kontinent ind die kontinente Liste fass es sie noch nicht gibt
    public void continent(Scanner scr){
        String nameterr = nameOfTerritorium(scr);
        int bonus = scr.nextInt();
        scr.next(); // doppelpunkt, brauch ich nicht

        List<Territorium> terr = new LinkedList<Territorium>();
        while (scr.hasNext()){
            String a = liestBis(scr, "-");
            terr.add(createOrGet(a));
        }

        Kontinent k = new Kontinent(nameterr, bonus, terr);
        kontinente.put(nameterr, k);
    }

    //returned name des territoriums
    // bekommt vom scanner etwas eingelesen
    public String nameOfTerritorium (Scanner scr){
        String nameterr = null;

        while(!scr.hasNextInt()){
            if (nameterr == null){
                nameterr = scr.next();
            } else {
                nameterr += " " + scr.next();
            }
        }
        return nameterr;
    }



    public String liestBis(Scanner scr, String bis){
        String nameterr = null;

        while(scr.hasNext()){
            String s = scr.next();
            if (s.equals(bis)){
                return nameterr;
            }
            if (nameterr == null){
                nameterr = s;
            } else {
                nameterr += " " + s;
            }
        }
        return nameterr;
    }

    public void draw(Graphics2D g) {
        //weise linien zwischen capitals von nachbarn
        for (Territorium t: territorien.values()){
            for (Territorium n : t.neighbors()) {
                g.setStroke(new BasicStroke(3));
                g.setPaint(new Color(0xcf, 0xe8, 0xf7));
                int x1 = t.getCaptial_of().x;
                int y1 = t.getCaptial_of().y;
                int x2 = n.getCaptial_of().x;
                int y2 = n.getCaptial_of().y;
                //wenns laenger als die breite ist nach aussen zeichnen
                if(Spielfeld.WIDTH/2 < Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2))) {
                    g.drawLine(x2,y2, 2*x2-x1, 2*y2-y1);
                    g.drawLine(x1, y1, 2*x1-x2, 2*y1-y2);
                } else {
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }

        for(Territorium t : territorien.values()) {
            t.drawBackground(g);
        }

        for(Territorium t : territorien.values()) {
            t.drawForeground(g);
        }
    }

    // wenn es den namen in territorien noch nicht gibt, wird ein neues territorium hinzugefuegt und zuruek gegeben
    // namen gibt es in territorien, return ich das territorium
    public Territorium createOrGet(String name){
        if (territorien.containsKey(name)){
            return territorien.get(name);
        } else {
            Territorium neueTerr = new Territorium(name);
            territorien.put(name, neueTerr);
            return neueTerr;
        }
    }

    public void checkMousePosition(Point point) {
        for (Territorium t: territorien.values()){
            t.setAusgewaehlt(false);
        }
        Territorium t = getTerritorium(point);
        if (t != null) {
            t.setAusgewaehlt(true);
        }
    }

    public Territorium getTerritorium(Point point) {
        for (Territorium t: territorien.values()){
            if(t.isMouseInside(point)) {
                return t;
            }
        }
        return null;
    }

    public void computerAuswaehlen(){
        List <Territorium> keinBesitz = new LinkedList<Territorium>();
        for (Territorium t: territorien.values()){
            if (t.getBesitz() == Besitz.Niemand) {
                keinBesitz.add(t);
            }
        }
        int index = (int) (Math.random()*keinBesitz.size());
        if (!keinBesitz.isEmpty()) {
            Territorium pcland = keinBesitz.get(index);//liefert territorium von keinBesitz beim index
            pcland.setBesitz(Besitz.Computergegner);
            pcland.setBesetzung(1);
        }

    }

    public boolean alleFelderAufgeteilt(){
        for (Territorium t: territorien.values()){
            if (t.getBesitz() == Besitz.Niemand){
                return false;
            }
        }
        return true;
    }


    public void computerVerteilt(){
        int anzahl = verstaerkung(Besitz.Computergegner); //anzahl wie viel der pc verteiln kann
        List<Territorium> a = new LinkedList<Territorium>();
        for (Territorium t : territorien.values()){
            if (t.getBesitz() == Besitz.Computergegner){
                a.add(t);
            }
        }
        for (int i = 0; i < anzahl; i++) {
            int index = (int) (Math.random()*a.size());
                Territorium pcland = a.get(index); //liefert territorium von keinBesitz beim index
                pcland.setBesetzung(pcland.getBesetzung() + 1);

        }
    }

    // rechnet die verstaerkung aus die gestzt werden darf
    //
    public int verstaerkung(Besitz b){
        int bonus = 0;
        int countTerr = 0; // anzahl der territorien
        for (Territorium t: territorien.values()){
            if (t.getBesitz() == b){
                countTerr++;
            }
        }

        for (Kontinent k : kontinente.values()){
            if (k.allesmein(b)) {
                bonus += k.getBonus();
            }
        }
        return countTerr/3 + bonus;
    }

    public void pcAngriff(){
        boolean pcGezogen = false;
        while(true){
            List<VonNach> angreifendeLaender = new LinkedList<VonNach>();
            for (Territorium t : territorien.values()){
                if (t.getBesitz() == Besitz.Spieler){ // ich besitz es
                    for (Territorium a : t.neighbors()){
                        if (a.getBesitz() == Besitz.Computergegner && a.getBesetzung()>1){
                            angreifendeLaender.add(new VonNach(a,t)); // pc (a) greift mich (t) an
                        }
                    }
                }  else if(t.getBesitz() == Besitz.Computergegner && t.getBesetzung()>1){
                    for (Territorium a : t.neighbors()){
                        if (a.getBesitz() == Besitz.Spieler){
                            angreifendeLaender.add(new VonNach(t,a)); // pc (t) greift mich (a) an
                        }
                    }
                }
            }
            // ausrechnen

            if(angreifendeLaender.isEmpty()){
                break;
            }

            int anzhahl = angreifendeLaender.size();
            int index = (int) (Math.random()*angreifendeLaender.size());
            VonNach pcland = angreifendeLaender.get(index);
            pcland.getVon().attacke(pcland.getNach());

            //pc kann rueber ziehen wenn es ihm gehoert
            if (pcland.getNach().getBesitz() == Besitz.Computergegner) {
                int MaxRueberZiehen = pcland.getVon().getBesetzung() - 1;
                int move = (int) (Math.random() * (MaxRueberZiehen + 1));
                pcland.getNach().setBesetzung(pcland.getNach().getBesetzung() + move);
                pcland.getVon().setBesetzung(pcland.getVon().getBesetzung() - move);
            }
        }
    }

    // true: wenn alle territorien im besitz von b sind
    public boolean gewonnen(Besitz b) {
        for (Territorium t: territorien.values()){
            if (t.getBesitz() != b){
                return false;
            }
        }
        return true;
    }
}