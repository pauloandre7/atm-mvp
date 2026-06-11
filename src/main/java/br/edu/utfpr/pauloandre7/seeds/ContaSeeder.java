package br.edu.utfpr.pauloandre7.seeds;

import br.edu.utfpr.pauloandre7.models.Conta;
import br.edu.utfpr.pauloandre7.repositories.IContaRepository;

public class ContaSeeder {

    public void seed(IContaRepository contaRepository) {
        if (contaRepository == null) {
            return;
        }

        seedConta(contaRepository, new Conta("0001-0", "Paulo", "123.456.789-00", "1234", 1500.00f));
        seedConta(contaRepository, new Conta("0002-0", "Luana", "987.654.321-00", "4321", 250.50f));
        seedConta(contaRepository, new Conta("0003-0", "Ricardo", "111.222.333-44", "9999", 980.75f));
    }

    private void seedConta(IContaRepository contaRepository, Conta conta) {
        if (contaRepository.findByNumConta(conta.getNumeroConta()) == null) {
            contaRepository.create(conta);
        }
    }
}