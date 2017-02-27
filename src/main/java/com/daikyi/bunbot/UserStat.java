package com.daikyi.bunbot;

import java.util.Comparator;

public class UserStat {

	private String username;
	private String id;
	
	private int maxBombStreak;
	private int curBombStreak;
	
	private int bombDeaths;
	private int missileDeaths;
	private int tntDeaths;
	
	private int defuses;
	private int dodgeStreak;
	private int plungeStreak;
	
	private int monies;
	private int lostMonies;


	public UserStat(String userName, String id) {
		maxBombStreak = 0;
		curBombStreak = 0;
		monies = 0;
		lostMonies = 0;
		bombDeaths = 0;
		missileDeaths = 0;
		tntDeaths = 0;
		defuses = 0;
		dodgeStreak = 0;
		plungeStreak = 0;

		username = userName;
		this.id = id;
	}
	
	public boolean newDodgeStreak(int newStreak){
		if(newStreak > dodgeStreak){
			dodgeStreak = newStreak;
			return true;
		}
		return false;
	}
	
	public boolean newPlungeStreak(int newStreak){
		if(newStreak > plungeStreak){
			plungeStreak = newStreak;
			return true;
		}
		return false;
	}
	
	public String getID(){
		return id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setNewUserName(String newName) {
		username = newName;
	}

	public int getMaxBombStreak() {
		return maxBombStreak;
	}

	public void setMaxBombStreak(int mbs) {
		maxBombStreak = mbs;
	}

	public void setCurBombStreak(int newStreak) {
		if (newStreak > maxBombStreak)
			maxBombStreak = newStreak;
		curBombStreak = newStreak;
	}

	public int getCurBombStreak() {
		return curBombStreak;
	}

	public int getMonies() {
		return monies-lostMonies;
	}

	public void setMonies(int money){
		monies = money;
	}
	
	public void setLosses(int money){
		lostMonies = money;
	}
	
	public int getMoniesGained(){
		return monies;
	}
	
	public int getMoniesLost(){
		return lostMonies;
	}
	
	public void gainMonies(int money) {
		monies += money;
	}
	
	public void loseMonies(int money){
		lostMonies+=money;
	}

	public int getTotalDeaths() {
		return bombDeaths+missileDeaths+tntDeaths;
	}
	
	public int getBombDeaths(){
		return bombDeaths;
	}
	
	public int getMissileDeaths(){
		return missileDeaths;
	}
	
	public int getTNTDeaths(){
		return tntDeaths;
	}
	
	public void setBombDeaths(int deaths){
		bombDeaths = deaths;
	}
	
	public void setMissileDeaths(int deaths){
		missileDeaths = deaths;
	}
	
	public void setTNTDeaths(int deaths){
		tntDeaths = deaths;
	}
	
	public void bombDeath(){
		bombDeaths++;
	}
	
	public void missileDeath(){
		missileDeaths++;
	}
	
	public void tntDeath(){
		tntDeaths++;
	}

	public void setDefuses(int defuseCount) {
		defuses = defuseCount;
	}

	public int getDefuses() {
		return defuses;
	}
	
	public int getDodgeStreak(){
		return dodgeStreak;
	}
	
	public int getPlungeStreak(){
		return plungeStreak;
	}
	
	//deaths, defuses, monies, streak, dodges
	enum UserComp implements Comparator<UserStat> {
		Deaths {
			@Override
			public int compare(UserStat c1, UserStat c2) {

				return c2.getTotalDeaths() - c1.getTotalDeaths();
			}
		},
		Defuses {
			@Override
			public int compare(UserStat c1, UserStat c2) {

				return c2.getDefuses() - c1.getDefuses();
			}
		},
		Monies {
			@Override
			public int compare(UserStat c1, UserStat c2) {

				return c2.getMonies() - c1.getMonies();
			}
		},
		Streak {
			@Override
			public int compare(UserStat c1, UserStat c2) {

				return c2.getMaxBombStreak() - c1.getMaxBombStreak();
			}
		},
		Dodges {
			@Override
			public int compare(UserStat c1, UserStat c2) {

				return c2.getDodgeStreak() - c1.getDodgeStreak();
			}
		},
		Plunges {
			@Override
			public int compare(UserStat c1, UserStat c2) {

				return c2.getPlungeStreak() - c1.getPlungeStreak();
			}
		};

		public static Comparator<UserStat> getComparator(UserComp comp) {
			return new Comparator<UserStat>() {
				@Override
				public int compare(UserStat c1, UserStat c2) {
					return comp.compare(c1, c2);
				}
			};
		}
	}
}
