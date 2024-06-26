package edoc.usuarios.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edoc.usuarios.exceptions.ObjectNotFoundException;
import edoc.usuarios.model.ResetPasswordToken;
import edoc.usuarios.repositories.ResetPasswordTokenRepository;

@Service
public class ResetPasswordTokenService {
	
	@Autowired
	private ResetPasswordTokenRepository repo;
	
	/**
	 * Find ResetPasswordToken by User e-mail
	 * @param ResetPasswordToken id of the object
	 * @return object found or null if the object were not found
	 * */
	public ResetPasswordToken findByEmail(String email) {
		Optional<ResetPasswordToken> obj = this.repo.findByEmail(email);
		return obj.orElse(null);
	}
	
	/**
	 * Find ResetPasswordToken by token
	 * @param ResetPasswordToken id of the object
	 * @return object found or null if the object were not found
	 * */
	public ResetPasswordToken findByToken(String token) {
		Optional<ResetPasswordToken> obj = this.repo.findByToken(token);
		return obj.orElse(null);
	}
	
	/**
	 * Find ResetPasswordToken by id
	 * @param ResetPasswordToken id of the object
	 * @return object found or null if the object were not found
	 * */
	public ResetPasswordToken findById(Integer id) {
		Optional<ResetPasswordToken> obj = this.repo.findById(id);
		return obj.orElse(null);
	}
	
	/**
	 * Insert a new resetPasswordToken
	 * @param ResetPasswordToken resetPasswordToken to be inserted
	 * @return ResetPasswordToken object inserted 
	 * */
	public ResetPasswordToken insert(ResetPasswordToken obj) {
		obj.setId(null);
		return this.repo.save(obj);
	}
	
	/**
	 * Update an resetPasswordToken
	 * @param resetPasswordToken resetToken to be updated
	 * @return resetPasswordToken object updated 
	 * */
	public ResetPasswordToken update(ResetPasswordToken obj) {
		if(this.findById(obj.getId()) == null) {
			throw new ObjectNotFoundException("Obeject "+ResetPasswordToken.class.getName()+" no found! ID "+obj.getId());
		}
		return this.repo.save(obj);
	}
	
}
