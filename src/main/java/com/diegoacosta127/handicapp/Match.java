package com.diegoacosta127.handicapp;

public class Match {

    public int matchId;
    public int teamHomeId;
    public int teamAwayId;
    public int scoreHome;
    public int scoreAway;

    public Match() {
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getTeamHomeId() {
        return teamHomeId;
    }

    public void setTeamHomeId(int teamHomeId) {
        this.teamHomeId = teamHomeId;
    }

    public int getTeamAwayId() {
        return teamAwayId;
    }

    public void setTeamAwayId(int teamAwayId) {
        this.teamAwayId = teamAwayId;
    }

    public int getScoreHome() {
        return scoreHome;
    }

    public void setScoreHome(int scoreHome) {
        this.scoreHome = scoreHome;
    }

    public int getScoreAway() {
        return scoreAway;
    }

    public void setScoreAway(int scoreAway) {
        this.scoreAway = scoreAway;
    }
}
