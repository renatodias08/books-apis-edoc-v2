package edoc.usuarios.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edoc.usuarios.autenticacao.api.model.PrivilegeDTO;
import edoc.usuarios.autenticacao.api.model.RoleDTO;
import edoc.usuarios.autenticacao.api.model.UserDetailDTO;
import edoc.usuarios.autenticacao.api.model.UsuarioCadastroDTO;
import edoc.usuarios.autenticacao.api.model.UsuarioDataDTO;
import edoc.usuarios.autenticacao.api.model.UsuarioResponseDTO;
import edoc.usuarios.model.User;
import edoc.usuarios.services.EmailService;
import edoc.usuarios.services.PrivilegioService;
import edoc.usuarios.services.RoleService;
import edoc.usuarios.services.UsuarioService;
import edoc.usuarios.util.ObjectMapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/usuarios")
@Api(tags = "usuarios", description = "Dados de cadastro")
@RequiredArgsConstructor
public class UsuariosController {

	@Value("${auth.reset-password-token-expiration-miliseg}")
	private Long resetPasswordTokenExpirationMisiseg;
	@Autowired
	EmailService emailService;
	@Autowired
	private UsuarioService usuarioService;	
	@Autowired
	private  PrivilegioService privilegioService;	
	@Autowired
	private RoleService serviceRole;
	
	@Autowired
	private ModelMapper modelMapper;

    @ApiOperation(value = "${UsuariosController.cadastrar} Cadastro usuarios ", nickname = "cadastrar", notes = "", response = String.class, authorizations = {
            @Authorization(value = "Authorization")
        }, tags={ "usuarios", })
        @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 400, message = "Algo deu errado"),
            @ApiResponse(code = 403, message = "Acesso negado"),
            @ApiResponse(code = 422, message = "O nome de usuário já está em uso") })
        @RequestMapping(value = "",
            produces = { "application/json" }, 
            consumes = { "application/json" },
            method = RequestMethod.POST)
	@PostAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> cadastrar(@ApiParam(value = "Cadastro usuarios"  )  @Valid @RequestBody  UsuarioDataDTO usuarioDataDTO) {
      String uriStr = usuarioService.cadastrar(modelMapper.map(usuarioDataDTO, User.class));		
      return ResponseEntity.ok(uriStr);
	}

    
    
	@ApiOperation(value = "${UsuariosController.token} Obter token ", nickname = "login", notes = "", response = String.class, authorizations = {
	    @Authorization(value = "Authorization")
	}, tags={ "usuarios", })
	@ApiResponses(value = { 
	    @ApiResponse(code = 200, message = "OK", response = String.class),
	    @ApiResponse(code = 400, message = "Algo deu errado"),
	    @ApiResponse(code = 422, message = "Nome de usuário/senha inválidos fornecidos") })
	@RequestMapping(value = "/token",
	    produces = { "application/json" }, 
	    consumes = { "application/json" },
	    method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestParam String usuario, @RequestParam String senha) {
		return usuarioService.login(usuario, senha);
	}	
	
//	
    @ApiOperation(value = "${UsuariosController.pesquisar} Pesquisar id", nickname = "pesquisarPorId", notes = "", response = UserDetailDTO.class, authorizations = {
            @Authorization(value = "Authorization")
        }, tags={ "usuarios", })
        @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK", response = UsuarioResponseDTO.class),
            @ApiResponse(code = 400, message = "Algo deu errado"),
            @ApiResponse(code = 403, message = "Acesso negado"),
            @ApiResponse(code = 404, message = "O usuário não existe"),
            @ApiResponse(code = 500, message = "Token JWT expirado ou inválido") })
        @RequestMapping(value = "/{id}",
        	    produces = { "application/json" }, 
        	    consumes = { "application/json" },
            method = RequestMethod.GET)
    @PostAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<UserDetailDTO> pesquisarPorId(@PathVariable(value="id") Integer id) throws ObjectNotFoundException {		
		User user = usuarioService.findById(id);		
		if(user == null) {
			throw new ObjectNotFoundException("Object "+User.class.getName()+" not found! id "+id);
		}		
		UserDetailDTO userDTO = ObjectMapperUtil.map(user,UserDetailDTO.class);		
		return ResponseEntity.ok().body(userDTO);
	}
	
    @ApiOperation(value = "${UsuariosController.pesquisarUsuario} Pesquisar meu usuario", nickname = "pesquisarUsuario", notes = "", response = UsuarioResponseDTO.class, authorizations = {
            @Authorization(value = "Authorization")
        }, tags={ "usuarios", })
        @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK", response = UsuarioResponseDTO.class),
            @ApiResponse(code = 400, message = "Algo deu errado"),
            @ApiResponse(code = 403, message = "Acesso negado"),
            @ApiResponse(code = 404, message = "O usuário não existe"),
            @ApiResponse(code = 500, message = "Token JWT expirado ou inválido") })
        @RequestMapping(value = "/meu",
            produces = { "application/json" },
            method = RequestMethod.GET)
    @PostAuthorize("hasAuthority('ROLE_USER')")
    public  ResponseEntity<UsuarioCadastroDTO> pesquisarUsuario(@RequestParam(value="usuario") String usuario){
    	UsuarioCadastroDTO userDTO = modelMapper.map(usuarioService.search(usuario), UsuarioCadastroDTO.class);
	    return ResponseEntity.ok().body(userDTO);    	
    }
   
    
    @ApiOperation(value = "${UsuariosController.atualizarToken} Atualizar usuario", nickname = "atualizarToken", notes = "", response = String.class, authorizations = {
            @Authorization(value = "Authorization")
        }, tags={ "usuarios", })
        @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "OK", response = UsuarioResponseDTO.class),
            @ApiResponse(code = 400, message = "Algo deu errado"),
            @ApiResponse(code = 403, message = "Acesso negado"),
            @ApiResponse(code = 404, message = "O usuário não existe"),
            @ApiResponse(code = 500, message = "Token JWT expirado ou inválido") })
        @RequestMapping(value = "/atualiza/token",
         	    produces = { "application/json" },         	   
            method = RequestMethod.GET)
    @PostAuthorize("hasAuthority('ROLE_USER')")
    public String atualizarToken(HttpServletRequest req) {
      return usuarioService.refresh(req.getRemoteUser());
    }
    
    
    
    
    
    
	@GetMapping("/todos-privilegios")
	@PostAuthorize("hasAuthority('USER_READ_PRIVILEGE')")
	public ResponseEntity<List<PrivilegeDTO>> getAllPrivileges() {
		
		List<PrivilegeDTO> dto = ObjectMapperUtil.mapAll(privilegioService.findAll(),PrivilegeDTO.class);
		
		return ResponseEntity.ok().body(dto);
	}
	
	@GetMapping("/todos-roles")
	@PostAuthorize("hasAuthority('USER_READ_PRIVILEGE')")
	public ResponseEntity<List<RoleDTO>> getAllRoles() {
		
		List<RoleDTO> dto = ObjectMapperUtil.mapAll(serviceRole.findAll(),RoleDTO.class);
		
		return ResponseEntity.ok().body(dto);
	}
	
	
	
	@DeleteMapping("/{id}")
	@PostAuthorize("hasAuthority('USER_DELETE_PRIVILEGE')")
	public ResponseEntity<Void> delete(@PathVariable(value="id") Integer id) {
		usuarioService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
