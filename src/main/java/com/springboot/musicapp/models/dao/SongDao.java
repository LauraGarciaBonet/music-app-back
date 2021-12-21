package com.springboot.musicapp.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.springboot.musicapp.models.entity.Song;

public interface SongDao extends CrudRepository<Song, Long>{

}
