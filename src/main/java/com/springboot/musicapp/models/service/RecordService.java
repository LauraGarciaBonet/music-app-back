package com.springboot.musicapp.models.service;

import java.util.List;
import com.springboot.musicapp.models.entity.Record;

public interface RecordService {

public List<Record> findAll();
	
	public Record findById(Long id);
	
	public Record save(Record record);
	
	public void delete(Long id);
}
