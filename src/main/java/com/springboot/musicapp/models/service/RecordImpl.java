package com.springboot.musicapp.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.musicapp.models.dao.RecordDao;
import com.springboot.musicapp.models.entity.Record;

@Service
public class RecordImpl implements RecordService {
	
	@Autowired
	private RecordDao recordDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Record> findAll(){
	
		return (List<Record>) recordDao.findAll();
	}
	@Override
	@Transactional(readOnly = true)
	public Record findById(Long id){
	
		return recordDao.findById(id).orElse(null);
	}
	

	@Override
	@Transactional
	public Record save(Record record) {
		return recordDao.save(record);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		recordDao.deleteById(id);
	}

}
