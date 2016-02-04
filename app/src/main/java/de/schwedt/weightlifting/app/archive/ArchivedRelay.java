package de.schwedt.weightlifting.app.archive;

import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.Table;

public class ArchivedRelay {
    private String relayName;
    private Competitions archivedCompetitions;
    private Table archivedTable;

    public ArchivedRelay(String relayName, Competitions archivedCompetitions, Table archivedTable) {
        this.relayName = relayName;
        this.archivedCompetitions = archivedCompetitions;
        this.archivedTable = archivedTable;
    }

    public String getRelayName() {
        return relayName;
    }

    public void setRelayName(String relayName) {
        this.relayName = relayName;
    }

    public Competitions getArchivedCompetitions() {
        return archivedCompetitions;
    }

    public void setArchivedCompetitions(Competitions archivedCompetitions) {
        this.archivedCompetitions = archivedCompetitions;
    }

    public Table getArchivedTable() {
        return archivedTable;
    }

    public void setArchivedTable(Table archivedTable) {
        this.archivedTable = archivedTable;
    }
}
