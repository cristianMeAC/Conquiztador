public class VonNach {
    private Territorium von;
    private Territorium nach;

    public Territorium getVon() {
        return von;
    }

    public Territorium getNach() {
        return nach;
    }

    public VonNach(Territorium von, Territorium nach) {
        this.von = von;
        this.nach = nach;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VonNach vonNach = (VonNach) o;

        if (nach != null ? !nach.equals(vonNach.nach) : vonNach.nach != null) return false;
        if (von != null ? !von.equals(vonNach.von) : vonNach.von != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = von != null ? von.hashCode() : 0;
        result = 31 * result + (nach != null ? nach.hashCode() : 0);
        return result;
    }
}
