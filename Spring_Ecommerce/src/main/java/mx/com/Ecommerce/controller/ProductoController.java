package mx.com.Ecommerce.controller;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mx.com.Ecommerce.model.Producto;
import mx.com.Ecommerce.model.Usuario;
import mx.com.Ecommerce.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	//Hacer pruebas y mostrar por consola
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
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
	public String save(Producto producto) {
		LOGGER.info("Este es el objeto del producto {}",producto);
		Usuario u= new Usuario(1, "", "", "", "", "", "", "");
		producto.setUsuario(u);
		productoService.save(producto);
		return "redirect:/productos";
	}
}
