package com.dsousa.minhasfinancas.model.repository;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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

import com.dsousa.minhasfinancas.model.entity.Lancamento;
import com.dsousa.minhasfinancas.model.enumered.StatusLancamento;
import com.dsousa.minhasfinancas.model.enumered.TipoLancamento;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(SpringExtension.class)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save(lancamento);
		
		assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoInexistente).isNull();
		
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirLancamento();
		
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirLancamento();
		
		lancamento.setAno(2022);
		lancamento.setDescricao("teste atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2022);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("teste atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
		
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
						.ano(2019)
						.mes(1)
						.descricao("lancamento qualquer")
						.valor(BigDecimal.valueOf(10))
						.tipo(TipoLancamento.RECEITA)
						.status(StatusLancamento.PENDENTE)
						.dataCadastro(LocalDate.now()).build();
	}
	
	private Lancamento criarEPersistirLancamento() {
		Lancamento lancamento = criarLancamento();
		
		entityManager.persist(lancamento);
		return lancamento;
	}
	
}
