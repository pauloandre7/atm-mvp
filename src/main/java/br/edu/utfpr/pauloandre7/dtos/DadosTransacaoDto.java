package br.edu.utfpr.pauloandre7.dtos;

import java.time.LocalDateTime;

import br.edu.utfpr.pauloandre7.models.TiposTransacao;

public record DadosTransacaoDto(LocalDateTime data, TiposTransacao tipoTransacao, float valor, long idContaDestino) {
}