package mx.com.Ecommerce.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mx.com.Ecommerce.model.Producto;
import mx.com.Ecommerce.service.ProductoService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("productos",productoService.findAll());
		
		return "usuario/home";
	}
	
	//Método para ver los productos individualmente
	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable Long id) {
		log.info("id producto enviado como parámetro {}", id);
		
		
		return "usuario/productohome";
	}
	

}
