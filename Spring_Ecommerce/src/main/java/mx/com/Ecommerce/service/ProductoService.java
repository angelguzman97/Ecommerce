package mx.com.Ecommerce.service;

import java.util.List;
import java.util.Optional;

import mx.com.Ecommerce.model.Producto;

public interface ProductoService {

	public Producto save(Producto producto);
	
	//El optional sirve para validar si el objeto que mandamos a llamar de la base de datos existe o no
	public Optional<Producto> get(Long id);
	
	public void update(Producto producto);
	
	public void delete(Long id);
	
	public List<Producto> findAll();
}
