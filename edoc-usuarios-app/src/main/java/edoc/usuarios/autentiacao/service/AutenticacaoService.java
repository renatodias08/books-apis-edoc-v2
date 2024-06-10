package edoc.usuarios.autentiacao.service;

import org.springframework.stereotype.Service;

import edoc.usuarios.autenticacao.api.model.MessageDTO;
@Service
public class AutenticacaoService {
	  public MessageDTO addMessage(String message, Integer status) {
			MessageDTO messageDTO = new MessageDTO();
			messageDTO.setMessage(message);
			messageDTO.setStatus(status);
			return messageDTO;
		}	  
}
