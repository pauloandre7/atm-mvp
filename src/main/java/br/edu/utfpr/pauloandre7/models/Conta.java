package br.edu.utfpr.pauloandre7.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Conta {

    @Getter
    private Long id;

    @Getter
    private String numeroConta;
    
    @Getter @Setter
    private String nomeUsuario;
    
    @Getter @Setter
    private String cpf;
    
    @Getter @Setter
    private String senha;

    @Getter 
    private float saldo;
    
    public Conta(String numeroConta, String nomeUsuario, String cpf, String senha, float saldo) {
        this.numeroConta = numeroConta;
        this.nomeUsuario = nomeUsuario;
        this.cpf = cpf;
        this.senha = senha;
        this.saldo = saldo;
    }

    public void diminuirSaldo(float valor) throws Exception{
        
        if(saldo < valor){
            throw new Exception("Saldo insuficiente");
        }

        saldo = saldo - valor;
    }

    public void aumentarSaldo(float valor ){
        
        saldo = saldo + valor;
    }
}
