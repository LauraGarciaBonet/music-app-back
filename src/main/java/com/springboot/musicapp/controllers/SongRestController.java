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
import com.springboot.musicapp.models.entity.Song;
import com.springboot.musicapp.models.service.SongService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class SongRestController {

	@Autowired
	private SongService songService;
	
	@GetMapping("/songs")
	public List<Song> index() {
		return songService.findAll();
	}
	
	@GetMapping("/songs/{id}")
	public ResponseEntity<?>show(@PathVariable Long id){
		Song song= null;
		Map<String,Object> response= new HashMap<>();
	
	try {
		song= songService.findById(id);
	} catch (DataAccessException e) {
		response.put("mensaje","Error al realizar consulta en base de datos");
		response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	if(song==null) {
		response.put("mensaje", "La canción ID: ".concat(id.toString().concat(" no existe en la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
	}
	return new ResponseEntity<Song>(song,HttpStatus.OK);
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
	
	@PostMapping("/songs")
	public ResponseEntity<?>create(@RequestBody Song song){
		Song songNew=null;
		Map<String, Object> response= new HashMap<>();
		
		try {
			songNew= songService.save(song);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar insert en base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","La canción ha sido creada con éxito!");
		response.put("song", songNew);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PostMapping("/songs/upload")
	public ResponseEntity<?>upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		Map<String, Object> response= new HashMap<>();
		
		Song song= songService.findById(id);
		
		if(!archivo.isEmpty()) {
			String nombreArchivo= UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ","");
			Path rutaArchivo= Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(),rutaArchivo);
			} catch (IOException e) {
		
				response.put("mensaje","Error al subir la imagen de la canción");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreFotoAnterior= song.getImage();
			
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0) {
			Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
			File archivoFotoAnterior= rutaFotoAnterior.toFile();
			
			if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
			}
		}
			song.setImage(nombreArchivo);
			
			songService.save(song);
			
			response.put("song", song);
			response.put("mensaje","Has subido correctamente la imagen: " + nombreArchivo);
		}
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/songs/{id}")
	public ResponseEntity<?> update(@RequestBody Song song, @PathVariable Long id){
		Song songActual=songService.findById(id);
		
		Song songUpdate=null;
		Map<String,Object> response= new HashMap<>();
		
		if(songActual==null) {
			response.put("mensaje","Error: no se pudo editar, la canción ID: ".concat(id.toString().concat("no existe el id en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		try {
			songActual.setName(song.getName());
			songActual.setGender(song.getGender());
			songActual.setDuration(song.getDuration());
		
			if(song.getCreatedAt()!= null) {
				songActual.setCreatedAt(song.getCreatedAt());
			}else {
				songActual.setCreatedAt(songActual.getCreatedAt());
			}
			
			songUpdate=songService.save(songActual);
		} catch (DataAccessException e){
			response.put("mensaje","Error al actualizar la canción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","La canción ha sido creado con éxito!");
		response.put("song", songUpdate);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("songs/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		Map<String,Object> response= new HashMap<>();
		
		try {
			songService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al eliminar la canción de la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La canción ha sido eliminada con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}