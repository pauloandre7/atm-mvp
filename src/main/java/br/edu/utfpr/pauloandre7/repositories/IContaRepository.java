package br.edu.utfpr.pauloandre7.repositories;

import br.edu.utfpr.pauloandre7.models.Conta;

public interface IContaRepository {

    public boolean create(Conta conta);
    public boolean delete(Conta conta);
    public Conta findById(Long id);
    public Conta findByNumConta(String numConta);
}
