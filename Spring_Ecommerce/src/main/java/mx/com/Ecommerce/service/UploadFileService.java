package mx.com.Ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//Esto es para las imágenes

@Service
public class UploadFileService {
	// Esto apunta a la carpeta creada, donde almacenará las imágenes
	private String folder = "images//";

	// Como parámetro se coloca un objeto Multipart File que es la imagen para
	// asegurar.
	private String saveImage(MultipartFile file) throws IOException {
		// Si viene algo (imagen) entonces hacer pasar esa imagen a byte. Esto es para
		// que pueda enviarse desde el cliente al servidor.
		if (!file.isEmpty()) {
			// Colocar un arreglo de tipo byte
			byte[] bytes = file.getBytes();
			// Variable de tipo Path. Y dentro se coloca la URL donde se crea la carpeta, en
			// este caso la tiene la variable folder. Se concatena con el parametro que es lo que se trae la imagen
			Path path = Paths.get(folder + file.getOriginalFilename());
			//Como parametro es el Path(ruta) y byte(imagen transformada en bytes) y así enviarla al servidor
			Files.write(path, bytes);
			
			//Si viene una imagen, que me la retorne
			
			return file.getOriginalFilename();
		}
		//Si viene vacío, entonces que retorne una imagen por default.
		
		return "default.jpg";
	}

	public void deleteImage(String nombre) {
		String ruta = "images//";
		File file = new File(ruta + nombre);
		file.delete();
		
	}
}
