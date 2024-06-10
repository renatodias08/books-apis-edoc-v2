package edoc.usuarios.autorizacoes.service.validacao;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edoc.usuarios.autenticacao.api.model.ResetPasswordDTO;
import edoc.usuarios.exceptions.FieldMessage;

public class ChangePasswordSaveValidator implements ConstraintValidator<ChagePasswordSave, ResetPasswordDTO> {
	
	@Override
	public boolean isValid(ResetPasswordDTO objDto, ConstraintValidatorContext context) {
		List<FieldMessage> erros = new ArrayList<>();
		if(!objDto.getPassword().equals(objDto.getPasswordConfirm())) {
			erros.add(new FieldMessage("senha","senha e confirmação de senha não coincidem!"));
		}
		
		for (FieldMessage e : erros) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return erros.isEmpty();
	}
}
