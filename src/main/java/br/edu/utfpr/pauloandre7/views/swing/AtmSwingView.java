package br.edu.utfpr.pauloandre7.views.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import br.edu.utfpr.pauloandre7.dtos.DadosTransacaoDto;
import br.edu.utfpr.pauloandre7.dtos.DadosUsuarioDto;
import br.edu.utfpr.pauloandre7.models.Conta;
import br.edu.utfpr.pauloandre7.presenters.IAtmPresenter;
import br.edu.utfpr.pauloandre7.repositories.IContaRepository;
import br.edu.utfpr.pauloandre7.views.IAtmView;

public class AtmSwingView implements IAtmView {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IContaRepository contaRepository;

    private IAtmPresenter presenter;

    private final JFrame frame;
    private final JComboBox<String> comboContaBase;
    private final JComboBox<String> comboContaDestino;
    private final JPasswordField campoSenha;
    private final JTextField campoValor;
    private final JLabel nomeValor;
    private final JLabel contaValor;
    private final JLabel saldoValor;
    private final JLabel mensagemValor;
    private final JTextArea extratoArea;

    public AtmSwingView(IContaRepository contaRepository) {
        this.contaRepository = contaRepository;

        this.frame = new JFrame("ATM MVP");
        this.comboContaBase = new JComboBox<>();
        this.comboContaDestino = new JComboBox<>();
        this.campoSenha = new JPasswordField();
        this.campoValor = new JTextField();
        this.nomeValor = new JLabel("-");
        this.contaValor = new JLabel("-");
        this.saldoValor = new JLabel("R$ 0,00");
        this.mensagemValor = new JLabel(" ");
        this.extratoArea = new JTextArea();

        configurarInterface();
        carregarContas();
        atualizarResumoContaSelecionada();
    }

    public void setPresenter(IAtmPresenter presenter) {
        this.presenter = presenter;
    }

    public void mostrar() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private void configurarInterface() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 620));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(12, 12));

        JPanel topo = criarPainelResumo();
        JPanel centro = criarPainelCentral();
        JPanel rodape = criarPainelMensagem();

        frame.add(topo, BorderLayout.NORTH);
        frame.add(centro, BorderLayout.CENTER);
        frame.add(rodape, BorderLayout.SOUTH);
    }

    private JPanel criarPainelResumo() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));

        JLabel titulo = new JLabel("ATM Simples");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 10, 0);
        painel.add(titulo, constraints);

        adicionarRotuloValor(painel, "Conta base:", contaValor, 1, 0);
        adicionarRotuloValor(painel, "Nome:", nomeValor, 1, 1);
        adicionarRotuloValor(painel, "Saldo:", saldoValor, 1, 2);

        JLabel dica = new JLabel("A conta base é a primeira conta carregada do repositório.");
        dica.setForeground(new Color(90, 90, 90));
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 4;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(8, 0, 0, 0);
        painel.add(dica, constraints);

        return painel;
    }

    private void adicionarRotuloValor(JPanel painel, String rotulo, JLabel valor, int linha, int coluna) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 0, 4, 12);
        c.gridy = linha;
        c.gridx = coluna * 2;
        c.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(rotulo);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        painel.add(label, c);

        c = new GridBagConstraints();
        c.insets = new Insets(4, 0, 4, 16);
        c.gridy = linha;
        c.gridx = coluna * 2 + 1;
        c.anchor = GridBagConstraints.WEST;
        valor.setFont(new Font("SansSerif", Font.PLAIN, 13));
        painel.add(valor, c);
    }

    private JPanel criarPainelCentral() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        comboContaBase.setPreferredSize(new Dimension(220, 28));
        comboContaDestino.setPreferredSize(new Dimension(220, 28));
        campoSenha.setPreferredSize(new Dimension(220, 28));
        campoValor.setPreferredSize(new Dimension(220, 28));

        comboContaBase.addActionListener(evento -> atualizarResumoContaSelecionada());

        JButton botaoSacar = new JButton("Sacar");
        JButton botaoDepositar = new JButton("Depositar");
        JButton botaoTransferir = new JButton("Transferir");
        JButton botaoExtrato = new JButton("Extrato");

        botaoSacar.addActionListener(evento -> executarSaque());
        botaoDepositar.addActionListener(evento -> executarDeposito());
        botaoTransferir.addActionListener(evento -> executarTransferencia());
        botaoExtrato.addActionListener(evento -> executarExtrato());

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Operações", criarPainelOperacoes(botaoSacar, botaoDepositar, botaoTransferir, botaoExtrato));
        abas.addTab("Extrato", criarPainelExtrato());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        painel.add(abas, constraints);

        return painel;
    }

    private JPanel criarPainelOperacoes(JButton botaoSacar, JButton botaoDepositar, JButton botaoTransferir, JButton botaoExtrato) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        adicionarCampo(painel, 0, "Conta base", comboContaBase);
        adicionarCampo(painel, 1, "Senha da operação", campoSenha);
        adicionarCampo(painel, 2, "Valor", campoValor);
        adicionarCampo(painel, 3, "Conta destino", comboContaDestino);

        JPanel botoes = new JPanel();
        botoes.add(botaoSacar);
        botoes.add(botaoDepositar);
        botoes.add(botaoTransferir);
        botoes.add(botaoExtrato);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.insets = new Insets(14, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        painel.add(botoes, c);

        return painel;
    }

    private void adicionarCampo(JPanel painel, int linha, String rotulo, javax.swing.JComponent componente) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = linha;
        labelConstraints.insets = new Insets(4, 6, 4, 12);
        labelConstraints.anchor = GridBagConstraints.WEST;
        painel.add(new JLabel(rotulo + ":"), labelConstraints);

        GridBagConstraints campoConstraints = new GridBagConstraints();
        campoConstraints.gridx = 1;
        campoConstraints.gridy = linha;
        campoConstraints.weightx = 1.0;
        campoConstraints.fill = GridBagConstraints.HORIZONTAL;
        campoConstraints.insets = new Insets(4, 0, 4, 6);
        painel.add(componente, campoConstraints);
    }

    private JPanel criarPainelExtrato() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        extratoArea.setEditable(false);
        extratoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        extratoArea.setText("Clique em Extrato para carregar as transações da conta base.");

        painel.add(new JScrollPane(extratoArea), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelMensagem() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        mensagemValor.setHorizontalAlignment(SwingConstants.LEFT);
        mensagemValor.setOpaque(true);
        mensagemValor.setBackground(new Color(245, 245, 245));
        mensagemValor.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        painel.add(mensagemValor, BorderLayout.CENTER);
        return painel;
    }

    private void carregarContas() {
        List<Conta> contas = contaRepository.findAll();
        DefaultComboBoxModel<String> modeloBase = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modeloDestino = new DefaultComboBoxModel<>();

        for (Conta conta : contas) {
            modeloBase.addElement(conta.getNumeroConta());
            modeloDestino.addElement(conta.getNumeroConta());
        }

        comboContaBase.setModel(modeloBase);
        comboContaDestino.setModel(modeloDestino);

        if (modeloBase.getSize() > 0) {
            comboContaBase.setSelectedIndex(0);
        }

        if (modeloDestino.getSize() > 1) {
            comboContaDestino.setSelectedIndex(1);
        }
    }

    private void atualizarResumoContaSelecionada() {
        Conta conta = getContaSelecionada();
        if (conta == null) {
            return;
        }

        contaValor.setText(conta.getNumeroConta());
        nomeValor.setText(conta.getNomeUsuario());
        saldoValor.setText(formatarMoeda(conta.getSaldo()));
    }

    private Conta getContaSelecionada() {
        Object selecionado = comboContaBase.getSelectedItem();
        if (selecionado == null) {
            return null;
        }

        return contaRepository.findByNumConta(selecionado.toString());
    }

    private Conta getContaDestinoSelecionada() {
        Object selecionado = comboContaDestino.getSelectedItem();
        if (selecionado == null) {
            return null;
        }

        return contaRepository.findByNumConta(selecionado.toString());
    }

    private void executarSaque() {
        Conta conta = getContaSelecionada();
        if (conta == null) {
            exibirMensagemErro("Selecione uma conta base.");
            return;
        }

        Float valor = lerValor();
        if (valor == null) {
            return;
        }

        presenter.onSacarClicked(conta.getNumeroConta(), valor, lerSenha());
    }

    private void executarDeposito() {
        Conta conta = getContaSelecionada();
        if (conta == null) {
            exibirMensagemErro("Selecione uma conta base.");
            return;
        }

        Float valor = lerValor();
        if (valor == null) {
            return;
        }

        presenter.onDepositarClicked(conta.getNumeroConta(), valor, lerSenha());
    }

    private void executarTransferencia() {
        Conta contaOrigem = getContaSelecionada();
        Conta contaDestino = getContaDestinoSelecionada();

        if (contaOrigem == null || contaDestino == null) {
            exibirMensagemErro("Selecione as contas de origem e destino.");
            return;
        }

        if (contaOrigem.getNumeroConta().equals(contaDestino.getNumeroConta())) {
            exibirMensagemErro("A conta de origem e destino devem ser diferentes.");
            return;
        }

        Float valor = lerValor();
        if (valor == null) {
            return;
        }

        presenter.onTransferirClicked(contaOrigem.getNumeroConta(), contaDestino.getNumeroConta(), valor, lerSenha());
    }

    private void executarExtrato() {
        Conta conta = getContaSelecionada();
        if (conta == null) {
            exibirMensagemErro("Selecione uma conta base.");
            return;
        }

        presenter.onExtratoClicked(conta.getNumeroConta());
    }

    private Float lerValor() {
        String texto = campoValor.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            exibirMensagemErro("Informe um valor.");
            return null;
        }

        try {
            float valor = Float.parseFloat(texto);
            if (valor <= 0) {
                exibirMensagemErro("O valor precisa ser maior que zero.");
                return null;
            }
            return valor;
        } catch (NumberFormatException exception) {
            exibirMensagemErro("Valor inválido.");
            return null;
        }
    }

    private String lerSenha() {
        return new String(campoSenha.getPassword());
    }

    private String formatarMoeda(float valor) {
        return String.format("R$ %.2f", valor);
    }

    @Override
    public void atualizarSaldo(float novoSaldo) {
        saldoValor.setText(formatarMoeda(novoSaldo));
    }

    @Override
    public void exibirDadosUsuario(DadosUsuarioDto dadosUsuario) {
        if (dadosUsuario == null) {
            return;
        }

        nomeValor.setText(dadosUsuario.nome());
        contaValor.setText(dadosUsuario.numConta());
        saldoValor.setText(formatarMoeda(dadosUsuario.saldo()));
    }

    @Override
    public void exibirMensagemSucesso(String mensagem) {
        mensagemValor.setForeground(new Color(0, 128, 0));
        mensagemValor.setText(mensagem);
    }

    @Override
    public void exibirMensagemErro(String erro) {
        mensagemValor.setForeground(new Color(180, 0, 0));
        mensagemValor.setText(erro);
    }

    @Override
    public void limparCampos() {
        campoSenha.setText("");
        campoValor.setText("");
    }

    @Override
    public void exibirExtrato(List<DadosTransacaoDto> transacoes) {
        if (transacoes == null || transacoes.isEmpty()) {
            extratoArea.setText("Nenhuma transação encontrada para a conta selecionada.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-18s %-15s %-12s %-15s%n", "Data", "Tipo", "Valor", "Destino"));
        builder.append("-------------------------------------------------------------------------------\n");

        for (DadosTransacaoDto transacao : transacoes) {
            builder.append(String.format(
                "%-18s %-15s %-12s %-15d%n",
                transacao.data().format(FORMATO_DATA),
                transacao.tipoTransacao(),
                formatarMoeda(transacao.valor()),
                transacao.idContaDestino()));
        }

        extratoArea.setText(builder.toString());
        extratoArea.setCaretPosition(0);
    }
}