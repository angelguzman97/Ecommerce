package mx.com.Ecommerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mx.com.Ecommerce.model.DetalleOrden;
import mx.com.Ecommerce.model.Orden;
import mx.com.Ecommerce.model.Producto;
import mx.com.Ecommerce.model.Usuario;
import mx.com.Ecommerce.service.IDetalleOrdenService;
import mx.com.Ecommerce.service.IOrdenService;
import mx.com.Ecommerce.service.IUsuarioService;
import mx.com.Ecommerce.service.ProductoService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class HomeController {

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	@Autowired
	private IDetalleOrdenService detalleOrdenService;


	// Para almacenar los detalles de la orden en una lista
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	// Datos de la orden
	Orden orden = new Orden();

	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("productos", productoService.findAll());

		return "usuario/home";
	}

	// Método para ver los productos individualmente
	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable Long id, Model model) {
		log.info("id producto enviado como parámetro {}", id);

		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();

		model.addAttribute("producto", producto);

		return "usuario/productohome";
	}

	@PostMapping("/cart")
	public String addCart(@RequestParam Long id, @RequestParam Integer cantidad, Model model) {
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;

		Optional<Producto> optionalProducto = productoService.get(id);
		log.info("Producto añadido: {}", optionalProducto.get());
		log.info("Cantidad: {}", cantidad);

		producto = optionalProducto.get();

		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio() * cantidad);
		detalleOrden.setProducto(producto);

		// Validar que el producto no se añada n veces
		Long idProducto = producto.getId();
		// función lambda para validar que no sean el mismo id del producto
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId() == idProducto);

		if (!ingresado) {
			// Añadir cada detalleOrden a detalles. Para posteriormente hacer la sumatoria
			detalles.add(detalleOrden);
		}

		// Función lambda. Hacer el conteo total que contenga una lista
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);

		// Mostrar eso en la vista.
		model.addAttribute("cart", detalles);
		// Pasar la orden como tal
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	// Quitar un producto del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Long id, Model model) {

		// Lista nueva de productos
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

		// Recorrer la lista de detalles con un for
		for (DetalleOrden detalleOrden : detalles) {
			// Hacer una validación. Si encuentra un Id diferente lo va a añadir
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNueva.add(detalleOrden);
			}
		}

		// Poner la nueva lista con los productos restantes
		detalles = ordenesNueva;

		double sumaTotal = 0;

		// Función lambda. Hacer el conteo total que contenga una lista
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);

		// Mostrar eso en la vista.
		model.addAttribute("cart", detalles);
		// Pasar la orden como tal
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	@GetMapping("/getCart")
	public String getCart(Model model) {

		// Mostrar eso en la vista.
		model.addAttribute("cart", detalles);
		// Pasar la orden como tal
		model.addAttribute("orden", orden);

		return "/usuario/carrito";
	}

	@GetMapping("/order")
	public String order(Model model) {

		Usuario usuario = usuarioService.findById(1).get();

		// Mostrar eso en la vista.
		model.addAttribute("cart", detalles);
		// Pasar la orden como tal
		model.addAttribute("orden", orden);
		
		model.addAttribute("usuario", usuario);
		
		return "usuario/resumenorden";
	}
	
	//Guardar la orden
	@GetMapping("/saveOrder")
	public String saveOrder() {
		Date fechaCreacion = new Date();//Nos permite obtener la fecha actual
		
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());//Se envía el núm. de orden por medio del servicio y el método
		
		// Usuario que hizo la orden
		Usuario usuario = usuarioService.findById(1).get();
		
		orden.setUsuario(usuario);
		ordenService.save(orden);//Guardar la orden
		
		//Guardar detalles
		for(DetalleOrden dt:detalles) {
			dt.setOrden(orden);//Se envia la orden a detalles
			detalleOrdenService.save(dt);//Se guarda el detalle
		}
		
		//Limpiar lista y orden
		orden = new Orden();
		detalles.clear();
		
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String seacrhProduct(@RequestParam String nombre, Model model) {
		
		log.info("Nombre del producto: {}", nombre);
		
		//La lista lo pasamos a un filtro que es una función lambda que trae el producto de tal nombre del producto y la función o método Contains se le pasa en la secuencia de caracteres que en este caso es el String nombre. Si contienen alguna parte de ese nombre que se le envía, se nos ponga y nos devuelva con una lista.
		List<Producto> productos = productoService.findAll().stream().filter(p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
		
		//Se obtiene la lista y se envía a la vista
		model.addAttribute("productos", productos);
		
		return "usuario/home";
	}
	
	
}
