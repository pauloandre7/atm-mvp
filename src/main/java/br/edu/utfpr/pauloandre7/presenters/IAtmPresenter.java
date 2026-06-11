package br.edu.utfpr.pauloandre7.presenters;

public interface IAtmPresenter {

    public void onSacarClicked(String numConta, float valor, String senha);
    public void onDepositarClicked(String numConta, float valor, String senha);
    public void onTransferirClicked(String numContaOrigem, String numContaDestino, float valor, String senha);
    public void onExtratoClicked(String numConta);
}
