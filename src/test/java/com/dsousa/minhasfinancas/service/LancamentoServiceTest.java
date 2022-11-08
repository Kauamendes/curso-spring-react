package com.dsousa.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Lancamento;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.model.enumered.StatusLancamento;
import com.dsousa.minhasfinancas.model.enumered.TipoLancamento;
import com.dsousa.minhasfinancas.model.repository.LancamentoRepository;
import com.dsousa.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.dsousa.minhasfinancas.service.impl.LancamentoServiceImpl;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	
	@Test
	public void deveSalvarUmLancamento() {
		
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validarLancamento(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		Assertions.assertThat(lancamentoSalvo.getId()).isEqualTo(lancamento.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandHouverErroDeValidacao() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarLancamento(lancamentoASalvar);
		
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validarLancamento(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		service.atualizar(lancamentoSalvo);
		
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	
	@Test
	public void deveLancarErroAoTentarAtualizarLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		service.deletar(lancamento);
		
		Mockito.verify(repository).delete(lancamento);
		
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	
	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> resultado = service.buscar(lancamento);
		
		Assertions.assertThat(resultado)
							.isNotEmpty()
							.hasSize(1)
							.contains(lancamento);
	}
	
	@Test
	public void deveAtualizaOStatusDeUmLancamento() {
	
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		Long id = 1L;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		Long id = 1L;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void DeveLancarErrosAoValidarUmLancamento() {
		
	 Lancamento lancamento;
	 String erro;

	lancamento = Lancamento.builder()
				.descricao("")
				.ano(0)
				.mes(0)
				.usuario(null)
				.valor(BigDecimal.ZERO)
				.tipo(null)
				.build();
	
	

		erro = catchThrowable(() -> service.validarLancamento(lancamento)).getMessage();
		assertEquals("Informe uma descrição válida.", erro);
		
		lancamento.setDescricao("teste teste");
	
		erro = catchThrowable(() -> service.validarLancamento(lancamento)).getMessage();
		assertEquals("Informe um Mês válido.", erro);
		
		lancamento.setMes(2);
	
		erro = catchThrowable(() -> service.validarLancamento(lancamento)).getMessage();
		assertEquals("Informe um ano válido.", erro);
	
		lancamento.setAno(2022);
	
		erro = catchThrowable(() -> service.validarLancamento(lancamento)).getMessage();
		assertEquals("Informe um Usuário", erro);
	
		lancamento.setUsuario(Usuario.builder().id(2L).build());
		
		erro = catchThrowable(() -> service.validarLancamento(lancamento)).getMessage();
		assertEquals("Informe um valor válido.", erro);
	
		lancamento.setValor(BigDecimal.ONE);
	
		erro = catchThrowable(() -> service.validarLancamento(lancamento)).getMessage();
		assertEquals("Informe um tipo de lançamento.", erro);
	
		
  }
	
}
