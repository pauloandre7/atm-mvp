package br.edu.utfpr.pauloandre7.views;

import java.util.List;

import br.edu.utfpr.pauloandre7.dtos.DadosTransacaoDto;
import br.edu.utfpr.pauloandre7.dtos.DadosUsuarioDto;

public interface IAtmView {

    void atualizarSaldo(float novoSaldo);
    void exibirDadosUsuario(DadosUsuarioDto dadosUsuario);
    void exibirMensagemSucesso(String mensagem);
    void exibirMensagemErro(String erro);
    void limparCampos();
    void exibirExtrato(List<DadosTransacaoDto> transacoes);
    void popularCombosDeConta(List<String> numerosContas);
}
