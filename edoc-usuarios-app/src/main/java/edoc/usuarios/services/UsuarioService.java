package edoc.usuarios.services;

import java.net.URI;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edoc.usuarios.autenticacao.api.model.MessageDTO;
import edoc.usuarios.autenticacao.api.model.TokenDataDTO;
import edoc.usuarios.constantes.UsuariosConstantes;
import edoc.usuarios.exceptions.CustomException;
import edoc.usuarios.exceptions.ObjectNotFoundException;
import edoc.usuarios.model.Privilege;
import edoc.usuarios.model.ResetPasswordToken;
import edoc.usuarios.model.Role;
import edoc.usuarios.model.User;
import edoc.usuarios.repositories.UserRepository;
import edoc.usuarios.security.JwtTokenProvider;
import edoc.usuarios.util.ObjectMapperUtil;

@Service
public class UsuarioService {
	
	@Autowired
	private BCryptPasswordEncoder encoder;	
	@Autowired
	ResetPasswordTokenService resetPasswordService;	
	@Autowired
	private UserRepository userRepository;	
	@Autowired
	private RoleService serviceRole;	
	@Autowired
	private PrivilegioService servicePrivilege;	
	@Autowired
	private ResetPasswordTokenService serviceResetPassword;	
	@Autowired
	private EmailService emailService;	
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private  AuthenticationManager authenticationManager; 
	@Autowired
	private JwtTokenProvider jwtTokenProvider;  
	
	@Value("${auth.reset-password-token-expiration-miliseg}")
	private Long resetPasswordTokenExpirationMisiseg;
	
	
	  @Value("${security.jwt.token.expire-length:3600000}")
	  private long validityInMilliseconds = 3600000; // 1h
	
		public String cadastrar(User dto) {
			User user = null;
			String uriStr = null;
			try {
				user = ObjectMapperUtil.map(dto, User.class);

				Role roleUser = serviceRole.findOrInsertByName("ROLE_USER");
				Privilege caregoryRead = servicePrivilege.findOrInsertByName("CATEGORY_READ_PRIVILEGE");
				Privilege caregoryWrite = servicePrivilege.findOrInsertByName("CATEGORY_WRITE_PRIVILEGE");
				Privilege caregoryDelete = servicePrivilege.findOrInsertByName("CATEGORY_DELETE_PRIVILEGE");

				user.addRole(roleUser);
				user.addPrivilege(caregoryRead);
				user.addPrivilege(caregoryWrite);
				user.addPrivilege(caregoryDelete);

				user = insert(user);
//				uri = ServletUriComponentsBuilder
//				  .fromCurrentContextPath().path("user/{id}")
//				  .buildAndExpand(user.getId())
//				  .toUri();
				uriStr = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(user.getId())
						.toUriString();

			} catch (AuthenticationException e) {
				throw new CustomException("O nome de usuario ja esta em uso", HttpStatus.UNPROCESSABLE_ENTITY);
			}
			return uriStr;

		}
	  
	  
		 public ResponseEntity<String> login(String username, String password) {
			    try {
			        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			        TokenDataDTO response = new TokenDataDTO();				    	
					//UsuarioDataDTO usuarioDataDTO = ObjectMapperUtil.map(user, UsuarioDataDTO.class);		
			        String token = jwtTokenProvider.createToken(username);
			        response.setToken(token);
			        addParametrosUsuario(response);	        
			        // Converter o objeto para JSON usando o Jackson
			        ObjectMapper objectMapper = new ObjectMapper();
			         String json = objectMapper.writeValueAsString(response);
			        return ResponseEntity.ok(json);	
			    } catch (AuthenticationException e) {
			        throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
			    } catch (JsonProcessingException e) {
			        throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
			    }
			}
	  
		 
		 
	  public User search(String username) {
		    User appUser = userRepository.findByUsername(username);
		    if (appUser == null) {
		      throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
		    }
		    return appUser;
      }	 
	  public User whoami(HttpServletRequest req) {
		  User user =  userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		  return user;
	  }
	  
	  

		public String refresh(String username) {
			try {
				TokenDataDTO response = new TokenDataDTO();
				response.setToken(
						jwtTokenProvider.createToken(username));
				response.setMessage("Autenticação bem-sucedida");
				 addParametrosUsuario(response);
				ObjectMapper objectMapper = new ObjectMapper();
				return objectMapper.writeValueAsString(response);
				
			} catch (AuthenticationException e) {
				throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
			} catch (JsonProcessingException e) {
				throw new CustomException("Nome de usuário/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
			}
//			
		}
		

	private void addParametrosUsuario(TokenDataDTO response) {
	   response.setExpiresIn(String.valueOf(validityInMilliseconds));
		response.setMessage(UsuariosConstantes.MENSAGEM);	   
		response.setScope(UsuariosConstantes.EDOC);
		response.tokenType(UsuariosConstantes.Bearer);
	}


  public MessageDTO addMessage(String message, Integer status) {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setMessage(message);
		messageDTO.setStatus(status);
		return messageDTO;
	}	  
	  
	  
	  
	  
	  
	
	/**
	 * Paginate User
	 * @param current page
	 * @param lines per page
	 * @param order by
	 * @param direction 
	 * */
	public Page<User> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return userRepository.findAll(pageRequest);
	}
	
	public Page<User> findPageByName(String name,Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return userRepository.findByName(name, pageRequest);
	}
	
	public Page<User> findPageByEmail(String email,Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return userRepository.findByEmail(email, pageRequest);
	}
	public Page<User> findPageByNameAndEmail(String name,String email,Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return userRepository.findByNameAndEmail(name,email, pageRequest);
	}
	
	
	/**
	 * Find User by id
	 * @param User id of the object
	 * @return object found or null if the object were not found
	 * */
	public User findById(Integer id) {
		Optional<User> obj = this.userRepository.findById(id);
		return obj.orElse(null);
	}
	
	/**
	 * Find User by e-mail
	 * @param User id of the object
	 * @return object found or null if the object were not found
	 * */
	public User findByEmail(String email) {
		Optional<User> obj = this.userRepository.findByEmail(email);
		return obj.orElse(null);
	}
	
	/**
	 * Insert a new user
	 * @param User user to be inserted
	 * @return User object inserted 
	 * */
	public User insert(User obj) {
		obj.setId(null);
		obj.setPassword(encoder.encode(obj.getPassword()));
		return this.userRepository.save(obj);
	}
	
	/**
	 * Update an user
	 * @param User user to be updated
	 * @return User object updated 
	 * */
	public User update(User obj) {
		User userRetrived = this.findById(obj.getId());
		if(userRetrived == null) {
			throw new ObjectNotFoundException("Obeject "+User.class.getName()+" no found! ID "+obj.getId());
		}
		if(obj.getPassword() != null && !obj.getPassword().isEmpty()) {
			obj.setPassword(encoder.encode(obj.getPassword()));
		}else {
			obj.setPassword(userRetrived.getPassword());
		}
		return this.userRepository.save(obj);
	}
	
	/**
	 * Generate an reset password token
	 * @param User user that will be password reseted 
	 * @return ResetPasswordToken object whith token genereted
	 * */
	public ResetPasswordToken generateResetPasswordToken(User obj) {
		User user = this.findById(obj.getId());
		ResetPasswordToken resetToken = new ResetPasswordToken();
		
		if(user != null) {
			resetToken.setUser(user);
			Date createdDate = new Date();
			resetToken.setToken(UUID.randomUUID().toString());
			resetToken.setCreatedDate(createdDate);
			resetPasswordService.insert(resetToken);
		}
		return resetToken;
	}
	
	/**
	 * Delete a user object by id
	 * @param user id
	 * */
	public void delete(Integer id) {
		if(this.findById(id) == null) {
			throw new ObjectNotFoundException("Obeject "+User.class.getName()+" no found! ID "+id);
		}
		userRepository.deleteById(id);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	  public void delete(String username) {
	    userRepository.deleteByUsername(username);
	  }




		

}
