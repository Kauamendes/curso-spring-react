package com.dsousa.minhasfinancas.model.repository;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.dsousa.minhasfinancas.model.entity.Usuario;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(SpringExtension.class)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//acao/execucao
		boolean result = repository.existsByEmail("usuario@gmail.com");
		
		// verificacao
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		boolean result = repository.existsByEmail("usuario@gmail.com");
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		Usuario usuario = criarUsuario();
		
		Usuario usuarioSalvo = repository.save(usuario);
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();		
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Optional<Usuario> result = repository.findByEmail("usuario@gmail.com");
		Assertions.assertThat(result.isPresent()).isTrue();		
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase() {		
		Optional<Usuario> result = repository.findByEmail("usuario@gmail.com");
		Assertions.assertThat(result.isPresent()).isFalse();		
	}
	
	public static Usuario criarUsuario() {
		Usuario usuario = Usuario.builder()
				.nome("usuario")
				.email("usuario@gmail.com")
				.senha("usuario")
				.build();
		
		return usuario;
	}
	
}
