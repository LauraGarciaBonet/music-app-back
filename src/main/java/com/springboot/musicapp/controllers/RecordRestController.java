package com.springboot.musicapp.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.springboot.musicapp.models.entity.Record;
import com.springboot.musicapp.models.service.RecordService;


@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api/v2")
public class RecordRestController {
	
	@Autowired
	private RecordService recordService;
	
	@GetMapping("/records")
	public List<Record> index() {
		return recordService.findAll();
	}

	@GetMapping("/records/{id}")
	public ResponseEntity<?>show(@PathVariable Long id){
		Record record= null;
		Map<String,Object> response= new HashMap<>();
	
	try {
		record= recordService.findById(id);
	} catch (DataAccessException e) {
		response.put("mensaje","Error al realizar consulta en base de datos");
		response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	if(record==null) {
		response.put("mensaje", "El disco ID: ".concat(id.toString().concat(" no existe en la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
	}
	return new ResponseEntity<Record>(record,HttpStatus.OK);
}
	
	@GetMapping("/uploads/img/{nameFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nameFoto){
		Path rutaArchivo= Paths.get("uploads").resolve(nameFoto).toAbsolutePath();
		
		Resource recurso = null;
		
		try {
			recurso=new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if(!recurso.exists() && !recurso.isReadable()) {
			throw new RuntimeException("Error no se puede cargar la imagen " + nameFoto);
		}
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+recurso.getFilename()+"\"");
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
	
	@PostMapping("/records")
	public ResponseEntity<?>create(@RequestBody Record record){
		Record recordNew=null;
		Map<String, Object> response= new HashMap<>();
		
		try {
			recordNew= recordService.save(record);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar insert en base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El disco ha sido creado con éxito!");
		response.put("record", recordNew);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PostMapping("/records/upload")
	public ResponseEntity<?>upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		Map<String, Object> response= new HashMap<>();
		
		Record record= recordService.findById(id);
		
		if(!archivo.isEmpty()) {
			String nombreArchivo= UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ","");
			Path rutaArchivo= Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(),rutaArchivo);
			} catch (IOException e) {
		
				response.put("mensaje","Error al subir la imagen del disco");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreFotoAnterior= record.getImage();
			
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
			Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
			File archivoFotoAnterior= rutaFotoAnterior.toFile();
			
			if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
			}
		}
			record.setImage(nombreArchivo);
			
			recordService.save(record);
			
			response.put("record", record);
			response.put("mensaje","Has subido correctamente la imagen: " + nombreArchivo);
		}
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/records/{id}")
	public ResponseEntity<?> update(@RequestBody Record record, @PathVariable Long id){
		Record recordActual=recordService.findById(id);
		
		Record recordUpdate=null;
		Map<String,Object> response= new HashMap<>();
		
		if(recordActual==null) {
			response.put("mensaje","Error: no se pudo editar, el disco ID: ".concat(id.toString().concat("no existe el id en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		try {
			recordActual.setName(record.getName());
			recordActual.setGender(record.getGender());
			recordActual.setSinger(record.getSinger());
			recordActual.setPrice(record.getPrice());
		
			if(record.getCreatedAt()!= null) {
				recordActual.setCreatedAt(record.getCreatedAt());
			}else {
				recordActual.setCreatedAt(recordActual.getCreatedAt());
			}
			
			recordUpdate=recordService.save(recordActual);
		} catch (DataAccessException e){
			response.put("mensaje","Error al actualizar el disco en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El disco ha sido creado con éxito!");
		response.put("record", recordUpdate);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("records/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		Map<String,Object> response= new HashMap<>();
		
		try {
			recordService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al eliminar el disco de la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El disco ha sido eliminado con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
