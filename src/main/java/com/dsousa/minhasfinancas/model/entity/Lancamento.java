package com.dsousa.minhasfinancas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.dsousa.minhasfinancas.model.enumered.StatusLancamento;
import com.dsousa.minhasfinancas.model.enumered.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lancamento", schema = "financas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lancamento {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String descricao;
	
	@Column
	private Integer mes;
	
	@Column
	private Integer ano;
	
	@JoinColumn(name = "id_usuario")
	@ManyToOne
	private Usuario usuario;
	
	@Column
	private BigDecimal valor;
	
	@Column(name = "data_cadastro")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate dataCadastro;
	
	@Column(name = "tipo")
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipo;
	
	@Column(name = "status", columnDefinition = "default 'PENDENTE'",length = 255)
	@Enumerated(EnumType.STRING)
	private StatusLancamento status;

		
}
