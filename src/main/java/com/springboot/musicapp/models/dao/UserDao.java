package com.springboot.musicapp.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.springboot.musicapp.models.entity.Usser;

public interface UserDao extends CrudRepository<Usser, Long> {

	public Usser findByUsername(String username);
	
	@Query("select u from Usser u where u.username=?1")
	public Usser findByUsername2(String username);
}
