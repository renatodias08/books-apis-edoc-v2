package edoc.usuarios.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edoc.usuarios.autenticacao.api.model.ResetPasswordDTO;
import edoc.usuarios.resources.exceptions.FieldMessage;

public class ChangePasswordSaveValidator implements ConstraintValidator<ChagePasswordSave, ResetPasswordDTO> {
	
	@Override
	public boolean isValid(ResetPasswordDTO objDto, ConstraintValidatorContext context) {
		List<FieldMessage> erros = new ArrayList<>();
		
		//Make custom validations here and add that in que erros list 
		if(!objDto.getPassword().equals(objDto.getPasswordConfirm())) {
			erros.add(new FieldMessage("password","passord and password confirm not match!"));
		}
		
		for (FieldMessage e : erros) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return erros.isEmpty();
	}
}
