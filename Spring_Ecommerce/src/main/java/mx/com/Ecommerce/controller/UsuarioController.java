package mx.com.Ecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import mx.com.Ecommerce.model.Orden;
import mx.com.Ecommerce.model.Usuario;
import mx.com.Ecommerce.service.IOrdenService;
import mx.com.Ecommerce.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;

	@GetMapping("/registro")
	public String create() {
		return "usuario/registro";
	}

	@PostMapping("/save")
	public String save(Usuario usuario) {
		// Mostrar en consola
		logger.info("usuario: {}", usuario);

		usuario.setTipo("USER");

		usuarioService.save(usuario);

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {

		return "usuario/login";
	}

	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) { // Se añade un parámetro http para iniciar sesión.
																	// Session se mantiene activo mientras uno inicie
																	// sesión
		// logger.info("Accesos : {}",usuario);

		Optional<Usuario> user = usuarioService.findByEmail(usuario.getEmail());

		//logger.info("Usuario: {}", user.get());

		// Es bueno trabajar con Optional para poder hacer ciertas válidaciones.
		// Si el usuario está presente en la bd.
		if (user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());

			// Si el usuario es igual a admin, se envía a la pagina de admin.
			if (user.get().getTipo().equals("ADMIN")) {

				return "redirect:/administrador";
			} else {
				return "redirect:/";
			}
		} else {
			logger.info("Usuario no existe");
		}

		return "redirect:/";
	}
	
	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		Usuario usuario = usuarioService.findById(Long.parseLong(session.getAttribute("idusuario").toString())).get();
		
		List<Orden> ordenes = ordenService.findByUsuario(usuario);
		
		model.addAttribute("ordenes", ordenes);
		
		return "usuario/compras";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Long id, HttpSession session, Model model) {
		
		logger.info("id de la orden: {}",id);
		
		Optional<Orden> orden = ordenService.findById(id);
		
		model.addAttribute("detalles", orden.get().getDetalles());
		
		//Sesión
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		
		return "usuario/detallecompra";
	}
}
