//package edoc.usuarios.springjwt.resources;
//
//import static io.restassured.RestAssured.given;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import javax.transaction.Transactional;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import edoc.usuarios.autenticacao.api.model.PrivilegeDTO;
//import edoc.usuarios.autenticacao.api.model.RoleDTO;
//import edoc.usuarios.autenticacao.api.model.RoleDetailDTO;
//import edoc.usuarios.autenticacao.api.model.RoleSaveDto;
//import edoc.usuarios.autenticacao.api.model.SingInDTO;
//import edoc.usuarios.model.Privilege;
//import edoc.usuarios.model.Role;
//import edoc.usuarios.services.PrivilegioService;
//import edoc.usuarios.services.RoleService;
//import edoc.usuarios.util.ObjectMapperUtil;
//
//class RoleResourceTest extends AbstractApplicationTest{
//	
//	@Autowired
//	RoleService serviceRole;
//	
//	@Autowired
//	PrivilegioService servicePrivilege;
//	
//	@Autowired
//	private BCryptPasswordEncoder encoder;
//	
//	private RoleDTO roleDTO;
//	
//	private RoleDetailDTO roleDetailDTO;
//	
//	private RoleSaveDto roleSaveDTO;
//	
//	@BeforeAll
//	void prepare() {
//		this.prepareParent();
//		this.roleDTO = new RoleDTO();
//		this.roleDetailDTO = new RoleDetailDTO();
//		this.roleSaveDTO = new RoleSaveDto();
//		
//		userMock.setUsername("User test");
//		userMock.setPassword(encoder.encode("123456"));
//		Role roleUser = serviceRole.findOrInsertByName("ROLE_USER");
//		Privilege roleRead = servicePrivilege.findOrInsertByName("ROLE_READ_PRIVILEGE");
//		Privilege roleWrite = servicePrivilege.findOrInsertByName("ROLE_WRITE_PRIVILEGE");
//		Privilege roleDelete = servicePrivilege.findOrInsertByName("ROLE_DELETE_PRIVILEGE");
//		
//		
//		PrivilegeDTO roleDeleteDTO = ObjectMapperUtil.map(roleDelete, PrivilegeDTO.class);
//		PrivilegeDTO roleWriteDTO = ObjectMapperUtil.map(roleWrite, PrivilegeDTO.class);
//		PrivilegeDTO roleReadDTO = ObjectMapperUtil.map(roleRead, PrivilegeDTO.class);
//		
//		userMock.addRole(roleUser);
//		userMock.addPrivilege(roleRead);
//		userMock.addPrivilege(roleWrite);
//		userMock.addPrivilege(roleDelete);
//		
//		userMock =	repositoryUser.saveAndFlush(userMock);
//		
//		this.singInDTO = ObjectMapperUtil.map(this.userMock,SingInDTO.class);
//		this.singInDTO.setPassword("123456");
//		
//		this.roleDTO.setName("ROLE_TEST");
//		this.roleSaveDTO.setName("ROLE_TEST");
//		this.roleSaveDTO.addPrivilegesItem(roleDeleteDTO);
//		this.roleSaveDTO.addPrivilegesItem(roleWriteDTO);
//		this.roleSaveDTO.addPrivilegesItem(roleReadDTO);
//		
//		this.roleDetailDTO = ObjectMapperUtil.map(this.roleDTO,RoleDetailDTO.class);
//		
//	}
//	
//	@Test
//	@DisplayName("Role not authorized [GET]")
//	@Order(1)
//	void notAutorized() {
//		assertThat(this.token).isBlank();
//		given()
//		.contentType("application/json")
//		.port(port)
//		.when().get("/role/1")
//		.then().statusCode(403);
//	}
//	
//	@Test
//	@DisplayName("Sing in -> Get Token [POST]")
//	@Order(2)
//	@Transactional
//	void getToken() {
//		
//		this.token =	given()
//				.contentType("application/json")
//				.body(singInDTO)
//				.port(port)
//				.when().post("/login")
//				.then().statusCode(200).extract().header("Authorization");
//	}
//	
//	private void roleNotFound(Integer id) {
//		given()
//		.header("Authorization", this.token)
//		.contentType("application/json")
//		.port(port)
//		.when().get("/role/"+id)
//		.then().statusCode(404);
//	}
//	
//	@Test
//	@DisplayName("Role not found [GET]")
//	@Order(3)
//	void notFoundRole() {
//		assertThat(this.token).isNotBlank();
//		this.roleNotFound(100);
//	}
//	
//	@Test
//	@DisplayName("Role insert [POST]")
//	@Order(4)
//	void insertRole() {
//		
//		assertThat(this.token).isNotBlank();
//		
//		String userSavedUrl = given()
//								.header("Authorization", this.token)
//								.contentType("application/json")
//								.body(this.roleSaveDTO)
//								.port(port)
//								.when().post("/role")
//								.then().statusCode(201)
//								.extract().header("Location");
//		
//		String splitedUrl[] = userSavedUrl.split("/");
//		this.roleDTO.setId(Integer.parseInt(splitedUrl[splitedUrl.length -1]));
//	}
//	
//	@Test
//	@DisplayName("Role found [GET]")
//	@Order(5)
//	void foundRole() {
//		
//		RoleDetailDTO userDTORetrived =	given()
//				.header("Authorization", this.token)
//				.contentType("application/json")
//				.port(port)
//				.when().get("/role/"+this.roleDTO.getId())
//				.then().statusCode(200)
//				.extract().as(RoleDetailDTO.class);
//		
//		assertEquals(this.roleDetailDTO.getName(), userDTORetrived.getName());
//	}
//	
//	@Test
//	@DisplayName("Role update [PUT]")
//	@Order(6)
//	void updateRole() {
//		
//		this.roleDTO.setName("ROLE_TEST_UPDATED");
//		this.roleDetailDTO.setName(this.roleDTO.getName());
//		 given()
//		.header("Authorization", this.token)
//		.contentType("application/json")
//		.body(this.roleDTO)
//		.port(port)
//		.when().put("/role/"+this.roleDTO.getId())
//		.then().statusCode(204);
//	}
//	
//	@Test
//	@DisplayName("Role check if it was updated [GET]")
//	@Order(7)
//	void updateCheckRole() {
//		RoleDetailDTO roleDTORetrived =	given()
//									.header("Authorization", this.token)
//									.contentType("application/json")
//									.port(port)
//									.when().get("/role/"+this.roleDTO.getId())
//									.then().statusCode(200)
//									.extract().as(RoleDetailDTO.class);
//		
//		assertEquals(this.roleDetailDTO.getName(), roleDTORetrived.getName());
//	}
//	
//	@Test
//	@DisplayName("Role delte [DELETE]")
//	@Order(8)
//	void deleteRole() {
//		
//		 given()
//		.header("Authorization", this.token)
//		.contentType("application/json")
//		.port(port)
//		.when().delete("/role/"+this.roleDTO.getId())
//		.then().statusCode(204);
//	}
//
//	@Test
//	@DisplayName("User check if it was deleted [GET]")
//	@Order(9)
//	void delteCheckRole() {
//		this.roleNotFound(this.roleDTO.getId());
//	}
//
//	@Test
//	@DisplayName("Role list [GET]")
//	@Order(10)
//	void listRole() {
//		
//		 given()
//		.header("Authorization", this.token)
//		.contentType("application/json")
//		.port(port)
//		.when().get("/role/page?name=test")
//		.then().statusCode(200);
//		
//	}
//	
//}
