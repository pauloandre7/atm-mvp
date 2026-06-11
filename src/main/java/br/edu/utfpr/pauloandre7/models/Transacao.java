package br.edu.utfpr.pauloandre7.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Transacao {

    private final Long id;
    private final TiposTransacao tipoTransacao;
    private final float valor;
    private final long idContaOrigem;
    private final long idContaDestino;
    private final float antigoSaldo;
    private final float novoSaldo;
    private final LocalDateTime data;
}
