package com.diegoacosta127.handicapp;

public class Team {

    public int teamId;
    public String name;
    public int teamSeason;

    Team (){
    }
    
    public Team(int teamId, String name) {
        this.teamId = teamId;
        this.name = name;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeamSeason() {
        return teamSeason;
    }

    public void setTeamSeason(int teamSeason) {
        this.teamSeason = teamSeason;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
