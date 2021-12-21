package com.springboot.musicapp.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.musicapp.models.dao.SongDao;
import com.springboot.musicapp.models.entity.Song;



@Service
public class SongImpl implements SongService{

	@Autowired
	private SongDao songDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Song> findAll(){
	
		return (List<Song>) songDao.findAll();
	}
	@Override
	@Transactional(readOnly = true)
	public Song findById(Long id){
	
		return songDao.findById(id).orElse(null);
	}
	

	@Override
	@Transactional
	public Song save(Song song) {
		return songDao.save(song);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		songDao.deleteById(id);
	}
}
