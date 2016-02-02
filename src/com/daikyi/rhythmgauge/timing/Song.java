package com.daikyi.rhythmgauge.timing;

public abstract class Song {

	String title;
	String songArtist;
	
	public Song(){
		title = "";
		songArtist = "";
	}
	
	public Song(String title, String songArtist){
		
		this.title = title;
		this.songArtist = songArtist;
	}
	
	public String getTitle(){
		
		return title;
	}
	
	public void setTitle(String title){
		
		this.title = title;
	}
	
	public String getSongArtist(){
		
		return songArtist;
	}
	
	public void setSongArtist(String songArtist){
		
		this.songArtist = songArtist;
	}
}
