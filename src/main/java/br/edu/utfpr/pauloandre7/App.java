package br.edu.utfpr.pauloandre7;

import javax.swing.SwingUtilities;

import br.edu.utfpr.pauloandre7.presenters.AtmPresenter;
import br.edu.utfpr.pauloandre7.repositories.ContaRepository;
import br.edu.utfpr.pauloandre7.repositories.IContaRepository;
import br.edu.utfpr.pauloandre7.repositories.ITransacaoRepository;
import br.edu.utfpr.pauloandre7.repositories.TransacaoRepository;
import br.edu.utfpr.pauloandre7.seeds.ContaSeeder;
import br.edu.utfpr.pauloandre7.views.swing.AtmSwingView;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IContaRepository contaRepository = new ContaRepository();
            ITransacaoRepository transacaoRepository = new TransacaoRepository();

            new ContaSeeder().seed(contaRepository);

            AtmSwingView view = new AtmSwingView(contaRepository);
            AtmPresenter presenter = new AtmPresenter(view, contaRepository, transacaoRepository);
            view.setPresenter(presenter);
            view.mostrar();
        });
    }
}
