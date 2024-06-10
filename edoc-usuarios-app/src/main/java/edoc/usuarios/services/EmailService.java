package edoc.usuarios.services;

import org.springframework.mail.SimpleMailMessage;

import com.fasterxml.jackson.core.JsonProcessingException;

import edoc.usuarios.model.ResetPasswordToken;

public interface EmailService {
	
	void sendEmail(SimpleMailMessage msg);
	
	public void sendResetPasswordToken(ResetPasswordToken resetPasswordToken) throws JsonProcessingException;
}
