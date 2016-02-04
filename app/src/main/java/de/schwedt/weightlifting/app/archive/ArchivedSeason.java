package de.schwedt.weightlifting.app.archive;

import java.util.ArrayList;

public class ArchivedSeason {
    private String seasonName;
    private ArrayList<ArchivedRelay> archivedRelays;

    public ArchivedSeason(String seasonName, ArrayList<ArchivedRelay> archivedRelays) {
        this.seasonName = seasonName;
        this.archivedRelays = archivedRelays;
    }

    public ArrayList<ArchivedRelay> getArchivedRelays() {
        return archivedRelays;
    }

    public void setArchivedRelays(ArrayList<ArchivedRelay> archivedRelays) {
        this.archivedRelays = archivedRelays;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }


}
