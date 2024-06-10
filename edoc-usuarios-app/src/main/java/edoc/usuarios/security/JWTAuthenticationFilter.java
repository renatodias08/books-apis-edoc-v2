package edoc.usuarios.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import edoc.usuarios.autentiacao.service.AutenticacaoService;
import edoc.usuarios.autenticacao.api.model.SingInDTO;
import edoc.usuarios.model.User;
import edoc.usuarios.repositories.UserRepository;
import edoc.usuarios.services.UsuarioService;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	@Autowired
	private UsuarioService usuarioService;
    @Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenProvider jwtUtil;   
	@Autowired
	private	AutenticacaoService autenticacaoService;
	@Autowired
	private UserRepository repositoryUser;
	
	
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtUtil) {
    	setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
        this.authenticationManager = authenticationManager;
       // this.jwtUtil = jwtUtil;
    }
	
	@Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) {
		Authentication auth = null;
		try {
			SingInDTO creds = new ObjectMapper()
	                .readValue(req.getInputStream(), SingInDTO.class);
	
	        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>());
	        
	        auth = authenticationManager.authenticate(authToken);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return auth;
	}
	
	@Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
	
		String username = ((MyUserDatails)auth.getPrincipal()).getUsername();
		
		User user = repositoryUser.findByUsername(username);		
        String token = jwtUtil.createToken(username);
        res.addHeader("Authorization", "Bearer " + token);
        res.addHeader("access-control-expose-headers", "Authorization");
	}
	
	private class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {
		 
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
        	ObjectMapper objectMapper = new ObjectMapper();
        	
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json"); 
            response.getWriter().append(
            						objectMapper.writeValueAsString(
            								autenticacaoService.addMessage(
            											   "Not authorized",
            											   HttpStatus.UNAUTHORIZED.value())
            								)
            						);
        }
        
    }
}
