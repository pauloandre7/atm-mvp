package br.edu.utfpr.pauloandre7.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import br.edu.utfpr.pauloandre7.models.Transacao;

public class TransacaoRepository implements ITransacaoRepository {

    private final List<Transacao> transacoes = new ArrayList<>();
    private final AtomicLong sequenciaId = new AtomicLong(1L);

    @Override
    public synchronized boolean create(Transacao transacao) {
        if (transacao == null) {
            return false;
        }

        Long id = transacao.getId();
        Transacao transacaoPersistida = transacao;

        if (id == null) {
            id = sequenciaId.getAndIncrement();
            transacaoPersistida = new Transacao(
                id,
                transacao.getTipoTransacao(),
                transacao.getValor(),
                transacao.getIdContaOrigem(),
                transacao.getIdContaDestino(),
                transacao.getAntigoSaldo(),
                transacao.getNovoSaldo(),
                transacao.getData()
            );
        } else {
            atualizarSequencia(id);
        }

        if (findById(id) != null) {
            return false;
        }

        transacoes.add(transacaoPersistida);
        return true;
    }

    @Override
    public synchronized boolean delete(Transacao transacao) {
        if (transacao == null || transacao.getId() == null) {
            return false;
        }

        for (int indice = 0; indice < transacoes.size(); indice++) {
            Transacao atual = transacoes.get(indice);
            if (transacao.getId().equals(atual.getId())) {
                transacoes.remove(indice);
                return true;
            }
        }

        return false;
    }

    @Override
    public synchronized Transacao findById(Long id) {
        if (id == null) {
            return null;
        }

        for (Transacao transacao : transacoes) {
            if (id.equals(transacao.getId())) {
                return transacao;
            }
        }

        return null;
    }

    @Override
    public synchronized List<Transacao> findAll() {
        return new ArrayList<>(transacoes);
    }

    @Override
    public synchronized List<Transacao> findByContaId(Long contaId) {
        List<Transacao> resultado = new ArrayList<>();

        if (contaId == null) {
            return resultado;
        }

        for (Transacao transacao : transacoes) {
            if (transacao.getIdContaOrigem() == contaId || transacao.getIdContaDestino() == contaId) {
                resultado.add(transacao);
            }
        }

        return resultado;
    }

    private void atualizarSequencia(Long id) {
        sequenciaId.updateAndGet(atual -> Math.max(atual, id + 1));
    }
}