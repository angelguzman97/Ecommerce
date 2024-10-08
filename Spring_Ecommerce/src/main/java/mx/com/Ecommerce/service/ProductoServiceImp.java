package mx.com.Ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.com.Ecommerce.model.Producto;
import mx.com.Ecommerce.repository.ProductoRepository;

@Service
public class ProductoServiceImp implements ProductoService{

	@Autowired
	private ProductoRepository productoRepository;
	
	
	@Override
	public Producto save(Producto producto) {
		// TODO Auto-generated method stub
		
		return productoRepository.save(producto);
	}

	@Override
	public Optional<Producto> get(Long id) {
		// TODO Auto-generated method stub
		return productoRepository.findById(id);
	}

	@Override
	public void update(Producto producto) {
		// TODO Auto-generated method stub
		productoRepository.save(producto);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		productoRepository.deleteById(id);
	}

	@Override
	public List<Producto> findAll() {
		// TODO Auto-generated method stub
		return productoRepository.findAll();
	}

}
