package mx.com.Ecommerce.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import mx.com.Ecommerce.model.Producto;
import mx.com.Ecommerce.model.Usuario;
import mx.com.Ecommerce.service.ProductoService;
import mx.com.Ecommerce.service.UploadFileService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	//Hacer pruebas y mostrar por consola
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private UploadFileService upload;
	
	@GetMapping("")
	public String show(Model model) { //El objeto Model lleva información desde el Back hasta la vista
		//El primer parametro es el nombre con quien voy a recibir la información, en este caso es productos.
		//El segundo parametro es el objeto/variable que mostraremos en la vista
		model.addAttribute("productos", productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("/create")
	public String create() {
		return "productos/create";
	}
	
	@PostMapping("/save")
	public String save(Producto producto,@RequestParam("img") MultipartFile file) throws IOException {
		LOGGER.info("Este es el objeto del producto {}",producto);
		Usuario u= new Usuario(1, "", "", "", "", "", "", "");
		producto.setUsuario(u);
		
		//Se coloca la lógica de la imagen también.
		if(producto.getId() == null) {//Cuando se crea un producto
			String nombreImagen = upload.saveImage(file);//Guardar el nombre de la imagen
			producto.setImagen(nombreImagen);
		}else {
			if(file.isEmpty()) { //editamos el producto, pero no se cambia la imagen
				Producto p = new Producto();
				p = productoService.get(producto.getId()).get();
				producto.setImagen(p.getImagen());
			}else {//Editamos el producto completo (con todo e imagen)
				String nombreImagen = upload.saveImage(file);//Guardar el nombre de la imagen
				producto.setImagen(nombreImagen);
			}
		}
		
		productoService.save(producto);
		return "redirect:/productos";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {
		Producto producto=new Producto();
		Optional<Producto> optionalProducto=productoService.get(id);
		producto = optionalProducto.get();
		
		LOGGER.info("Producto buscado: {}",producto);
		model.addAttribute("producto",producto);
		
		return "productos/edit";
	}
	
	@PostMapping("/update")
	public String update(Producto producto) {
		LOGGER.info("Producto Actualizado {}",producto);
		Usuario u= new Usuario(1, "", "", "", "", "", "", "");
		producto.setUsuario(u);
		productoService.save(producto);
		return "redirect:/productos";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		productoService.delete(id);
		return "redirect:/productos";
	}
}
