package com.alvaroagea.elk.practica5;

public class OlympicAthletes {
    private final String country;
    private final String athlete;
    private final int medals;

    public OlympicAthletes(String country, String athlete, int medals) {
        this.country = country;
        this.athlete = athlete;
        this.medals = medals;
    }

    public String getCountry() {
        return country;
    }

    public String getAthlete() {
        return athlete;
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
        if (!(o instanceof OlympicAthletes)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        OlympicAthletes c = (OlympicAthletes) o;

        // Compare the data members and return accordingly
        return this.country.compareTo(c.country) == 0 &&
                this.athlete.compareTo(c.athlete) == 0 &&
                this.medals == c.medals;

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.country.hashCode();
        hash = 53 * hash + this.athlete.hashCode();
        hash = 53 * hash + this.medals;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("country: %s\tathlete: %s\tmedals: %d", this.country, this.athlete,
                this.medals);

    }
}
