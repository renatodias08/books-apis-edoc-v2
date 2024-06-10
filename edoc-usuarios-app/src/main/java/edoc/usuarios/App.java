package edoc.usuarios;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import edoc.usuarios.autenticacao.api.model.SignUpDTO;
import edoc.usuarios.autenticacao.api.model.SingInDTO;
import edoc.usuarios.autenticacao.api.model.UpdateUserDTO;
import edoc.usuarios.autenticacao.api.model.UsuarioCadastroDTO;
import edoc.usuarios.model.Privilege;
import edoc.usuarios.model.Role;
import edoc.usuarios.model.User;
import edoc.usuarios.repositories.UserRepository;
import edoc.usuarios.services.PrivilegioService;
import edoc.usuarios.services.RoleService;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class App implements CommandLineRunner {
	@Autowired
	RoleService serviceRole;
	
	@Autowired
	PrivilegioService servicePrivilege;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	private SignUpDTO signupDTO;
	
	private UpdateUserDTO updateUserDTO;
	
	private User user;
	private UsuarioCadastroDTO userDTO;
	
	@Autowired
	protected UserRepository repositoryUser;
	protected SingInDTO singInDTO;
	
	  public static void main(String[] args) {
	    SpringApplication.run(App.class, args);
	  }

	  @Bean
	  public ModelMapper modelMapper() {
	    return new ModelMapper();
	  }

	  @Override
	  public void run(String... params) throws Exception {
		    user = new User();
		    user.setUsername("edoc");
		    user.setEmail("edoc@email.com.br");
			user.setPassword(encoder.encode("123456"));
			Role roleUser = serviceRole.findOrInsertByName("ROLE_USER");
			Privilege caregoryRead = servicePrivilege.findOrInsertByName("USER_READ_PRIVILEGE");
			Privilege caregoryWrite = servicePrivilege.findOrInsertByName("USER_WRITE_PRIVILEGE");
			Privilege caregoryDelete = servicePrivilege.findOrInsertByName("USER_DELETE_PRIVILEGE");
			
			user.addRole(roleUser);
			user.addPrivilege(caregoryRead);
			user.addPrivilege(caregoryWrite);
			user.addPrivilege(caregoryDelete);
			
			user =	repositoryUser.saveAndFlush(user);
			ObjectMapper objectMapper = new ObjectMapper();
	        System.out.println(objectMapper.writeValueAsString(user));
			
//			this.singInDTO = ObjectMapperUtil.map(this.user,SingInDTO.class);
//			this.signupDTO = ObjectMapperUtil.map(this.user,SignUpDTO.class);
//			this.userDTO = ObjectMapperUtil.map(this.user,UserDTO.class);
//			this.updateUserDTO = ObjectMapperUtil.map(this.user,UpdateUserDTO.class);
//			
//			this.signupDTO.setPassword("123456");
//			this.singInDTO.setPassword("123456");
//			this.updateUserDTO.setPassword("123456");
	  }

}
