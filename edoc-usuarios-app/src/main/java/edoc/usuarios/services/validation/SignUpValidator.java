package edoc.usuarios.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import edoc.usuarios.autenticacao.api.model.SignUpDTO;
import edoc.usuarios.model.User;
import edoc.usuarios.resources.exceptions.FieldMessage;
import edoc.usuarios.services.UsuarioService;

public class SignUpValidator implements ConstraintValidator<SignUpSave, SignUpDTO> {
	
	@Autowired
	private UsuarioService userService;
	
	@Override
	public boolean isValid(SignUpDTO objDto, ConstraintValidatorContext context) {
		List<FieldMessage> erros = new ArrayList<>();
		
		//Make custom validations here and add that in que erros list 
		if(objDto.getEmail() == null) {
			erros.add(new FieldMessage("email","can not be null"));
		}
		
		User userFound = userService.findByEmail(objDto.getEmail());
		
		if(userFound != null && !userFound.getId().equals(objDto.getId())) {
			erros.add(new FieldMessage("email","User already registered!"));
		}
		
		for (FieldMessage e : erros) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return erros.isEmpty();
	}
}
