package com.springboot.musicapp.models.service;

import java.util.List;

import com.springboot.musicapp.models.entity.Song;


public interface SongService {

public List<Song> findAll();
	
	public Song findById(Long id);
	
	public Song save(Song song);
	
	public void delete(Long id);
}
