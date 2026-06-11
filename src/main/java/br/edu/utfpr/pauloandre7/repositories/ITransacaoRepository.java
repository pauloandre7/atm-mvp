package br.edu.utfpr.pauloandre7.repositories;

import java.util.List;

import br.edu.utfpr.pauloandre7.models.Transacao;

public interface ITransacaoRepository {

    public boolean create(Transacao transacao);
    public boolean delete(Transacao transacao);
    public Transacao findById(Long id);
    public List<Transacao> findAll();
    public List<Transacao> findByContaId(Long contaId);
}
