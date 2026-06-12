package br.edu.utfpr.pauloandre7.presenters;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.utfpr.pauloandre7.dtos.DadosTransacaoDto;
import br.edu.utfpr.pauloandre7.dtos.DadosUsuarioDto;
import br.edu.utfpr.pauloandre7.models.Conta;
import br.edu.utfpr.pauloandre7.models.TiposTransacao;
import br.edu.utfpr.pauloandre7.models.Transacao;
import br.edu.utfpr.pauloandre7.repositories.IContaRepository;
import br.edu.utfpr.pauloandre7.repositories.ITransacaoRepository;
import br.edu.utfpr.pauloandre7.views.IAtmView;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AtmPresenter implements IAtmPresenter {

    private final IAtmView view;
    private final IContaRepository contaRepository;
    private final ITransacaoRepository transacaoRepository;
    

    @Override
    public void onSacarClicked(String numConta, float valor, String senha) {
        Conta conta = localizarContaValida(numConta, senha);
        if (conta == null) {
            return;
        }

        float saldoAnterior = conta.getSaldo();

        try {
            conta.diminuirSaldo(valor);
            registrarTransacao(TiposTransacao.SAQUE, valor, conta.getId(), 0L, saldoAnterior, conta.getSaldo());
            atualizarViewConta(conta);
            view.exibirMensagemSucesso("Saque realizado com sucesso.");
            view.limparCampos();
        } catch (Exception exception) {
            view.exibirMensagemErro(exception.getMessage());
        }
    }

    @Override
    public void onDepositarClicked(String numConta, float valor, String senha) {
        Conta conta = localizarContaValida(numConta, senha);
        if (conta == null) {
            return;
        }

        float saldoAnterior = conta.getSaldo();
        conta.aumentarSaldo(valor);

        registrarTransacao(TiposTransacao.DEPOSITO, valor, 0L, conta.getId(), saldoAnterior, conta.getSaldo());
        atualizarViewConta(conta);
        view.exibirMensagemSucesso("Depósito realizado com sucesso.");
        view.limparCampos();
    }

    @Override
    public void onTransferirClicked(String numContaOrigem, String numContaDestino, float valor, String senha) {
        Conta contaOrigem = localizarContaValida(numContaOrigem, senha);
        if (contaOrigem == null) {
            return;
        }

        Conta contaDestino = contaRepository.findByNumConta(numContaDestino);
        if (contaDestino == null) {
            view.exibirMensagemErro("Conta de destino não encontrada.");
            return;
        }

        float saldoOrigemAnterior = contaOrigem.getSaldo();
        try {
            contaOrigem.diminuirSaldo(valor);
            contaDestino.aumentarSaldo(valor);

            registrarTransacao(TiposTransacao.TRANSFERENCIA, valor, contaOrigem.getId(), contaDestino.getId(), saldoOrigemAnterior, contaOrigem.getSaldo());
            atualizarViewConta(contaOrigem);
            view.exibirMensagemSucesso("Transferência realizada com sucesso.");
            view.limparCampos();
        } catch (Exception exception) {
            view.exibirMensagemErro(exception.getMessage());
        }
    }

    @Override
    public void onExtratoClicked(String numConta) {
        exibirExtrato(numConta);
    }

    public void exibirDadosUsuario(String numConta) {
        Conta conta = contaRepository.findByNumConta(numConta);
        if (conta == null) {
            view.exibirMensagemErro("Conta não encontrada.");
            return;
        }

        view.exibirDadosUsuario(new DadosUsuarioDto(conta.getNomeUsuario(), conta.getNumeroConta(), conta.getSaldo()));
    }

    public void exibirExtrato(String numConta) {
        Conta conta = contaRepository.findByNumConta(numConta);
        if (conta == null) {
            view.exibirMensagemErro("Conta não encontrada.");
            return;
        }

        List<DadosTransacaoDto> transacoes = transacaoRepository.findByContaId(conta.getId())
            .stream()
            .map(transacao -> new DadosTransacaoDto(
                transacao.getData(),
                transacao.getTipoTransacao(),
                transacao.getValor(),
                transacao.getIdContaDestino()))
            .toList();

        view.exibirExtrato(transacoes);
    }

    private Conta localizarContaValida(String numConta, String senha) {
        Conta conta = contaRepository.findByNumConta(numConta);
        if (conta == null) {
            view.exibirMensagemErro("Conta não encontrada.");
            return null;
        }

        if (senha == null || !senha.equals(conta.getSenha())) {
            view.exibirMensagemErro("Senha inválida.");
            return null;
        }

        return conta;
    }

    private void registrarTransacao(TiposTransacao tipoTransacao, float valor, Long idContaOrigem, Long idContaDestino, float antigoSaldo, float novoSaldo) {
        Transacao transacao = new Transacao(
            null,
            tipoTransacao,
            valor,
            idContaOrigem == null ? 0L : idContaOrigem,
            idContaDestino == null ? 0L : idContaDestino,
            antigoSaldo,
            novoSaldo,
            LocalDateTime.now()
        );
        transacaoRepository.create(transacao);
    }

    private void atualizarViewConta(Conta conta) {
        view.atualizarSaldo(conta.getSaldo());
        view.exibirDadosUsuario(new DadosUsuarioDto(conta.getNomeUsuario(), conta.getNumeroConta(), conta.getSaldo()));
    }

    @Override
    public void inicializarView() {
        List<String> contas = contaRepository.findAll().stream()
            .map(Conta::getNumeroConta).toList();
        
        view.popularCombosDeConta(contas);
        
        if(!contas.isEmpty()) {
            exibirDadosUsuario(contas.get(0)); // Carrega os dados da primeira conta no painel Resumo
        }
    }

    @Override
    public void onContaSelecionada(String numConta) {
        // Reutilizando seu método que já busca a conta e chama view.exibirDadosUsuario()
        exibirDadosUsuario(numConta); 
    }
}