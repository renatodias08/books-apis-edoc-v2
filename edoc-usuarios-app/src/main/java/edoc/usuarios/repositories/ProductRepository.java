package edoc.usuarios.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edoc.usuarios.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{
	
}
