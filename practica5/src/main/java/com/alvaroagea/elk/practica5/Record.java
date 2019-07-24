package com.alvaroagea.elk.practica5;

public final class Record {
    private final String year;
    private final String city;
    private final String sport;
    private final String discipline;
    private final String athlete;
    private final String country;
    private final String gender;
    private final String event;
    private final String medal;

    public Record(String year, String city, String sport, String discipline,
                  String athlete, String country, String gender, String event, String medal) {
        this.year = year;
        this.city = city;
        this.sport = sport;
        this.discipline = discipline;
        this.athlete = athlete;
        this.country = country;
        this.gender = gender;
        this.event = event;
        this.medal = medal;
    }

    public String getYear() {
        return year;
    }

    public String getCity() {
        return city;
    }

    public String getSport() {
        return sport;
    }

    public String getDiscipline() {
        return discipline;
    }

    public String getAthlete() {
        return athlete;
    }

    public String getCountry() {
        return country;
    }

    public String getGender() {
        return gender;
    }

    public String getEvent() {
        return event;
    }

    public String getMedal() {
        return medal;
    }
}
