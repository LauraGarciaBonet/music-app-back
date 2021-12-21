package com.springboot.musicapp.models.dao;

import org.springframework.data.repository.CrudRepository;
import com.springboot.musicapp.models.entity.Record;

public interface RecordDao extends CrudRepository<Record, Long> {

}
