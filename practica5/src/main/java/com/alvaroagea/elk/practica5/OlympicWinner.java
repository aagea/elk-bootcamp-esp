package com.alvaroagea.elk.practica5;

public final class OlympicWinner {
    private final String year;
    private final String country;
    private final int medals;

    public OlympicWinner(String year, String country, int medals) {
        this.year = year;
        this.country = country;
        this.medals = medals;
    }

    public String getYear() {
        return year;
    }

    public String getCountry() {
        return country;
    }

    public int getMedals() {
        return medals;
    }

    // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof OlympicWinner)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        OlympicWinner c = (OlympicWinner) o;

        // Compare the data members and return accordingly
        return this.country.compareTo(c.country) == 0 &&
                this.year.compareTo(c.year) == 0 &&
                this.medals == c.medals;

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.country.hashCode();
        hash = 53 * hash + this.year.hashCode();
        hash = 53 * hash + this.medals;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("year: %s\t country: %s\tmedals: %d", this.year, this.country,
                this.medals);

    }
}
