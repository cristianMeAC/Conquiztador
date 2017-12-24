import java.util.List;
import java.util.LinkedList;

// kontinent besteht aus territorium
public class Kontinent {
    private List<Territorium> continent = new LinkedList<Territorium>();
    private String name;
    private  int bonus;

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public Kontinent(String name, int bonus, List<Territorium> continent){
        this.name = name;
        this.bonus = bonus;
        this.continent = continent;
    }


    // ueberprueft ob alle territorien b gehoeren
    // true: alle (territorien) gehören b
    public boolean allesmein(Besitz b){
        for (Territorium t: continent){
            if (t.getBesitz()!=b){
                return false;
            }
        }
        return true;
    }
}
