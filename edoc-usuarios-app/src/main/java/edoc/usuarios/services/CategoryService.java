package edoc.usuarios.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import edoc.usuarios.exceptions.ObjectNotFoundException;
import edoc.usuarios.model.Category;
import edoc.usuarios.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repo;
	
	public Page<Category> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}
	
	/**
	 * Find Category by id
	 * @param Integer id of the object
	 * @return object found or null if the object were not found
	 * */
	public Category findById(Integer id) {
		Optional<Category> obj = this.repo.findById(id);
		return obj.orElse(null);
	}
	
	/**
	 * Insert a new category
	 * @param Category category to be inserted
	 * @return Category object inserted 
	 * */
	public Category insert(Category obj) {
		obj.setId(null);
		return this.repo.save(obj);
	}
	
	/**
	 * Update a category object
	 * @param Category category to be updated
	 * @return Category object updated 
	 * */
	public Category update(Category obj) {
		if(this.findById(obj.getId()) == null) {
			throw new ObjectNotFoundException("Obeject "+Category.class.getName()+" no found! ID "+obj.getId());
		}
	    return this.repo.save(obj);
	}
	
	/**
	 * Delete a category object by id
	 * @param category id
	 * */
	public void delete(Integer id) {
		if(this.findById(id) == null) {
			throw new ObjectNotFoundException("Obeject "+Category.class.getName()+" no found! ID "+id);
		}
		repo.deleteById(id);
	}
}
