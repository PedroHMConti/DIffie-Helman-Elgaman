import java.math.BigInteger;

public class Encriptador {
    GeraSegredo secreto = new GeraSegredo();

    public BigInteger [] Encriptar(BigInteger textoPlano,BigInteger primo, BigInteger raiz,int tamanhoX_A,BigInteger x_B){
        //escolher x_A
        BigInteger x_A = secreto.criaSegredo(tamanhoX_A);
        //calcular chave publica y_A = g^(x_A)mod p
        BigInteger y_A = calculaChavePublica(x_A,raiz,primo);
        //calcular K = y_A^(x_B) mod p ou y_B^(x_A)
        BigInteger k = calcula_K(y_A,x_B,primo);
        //calcular cifra C => C = m*K mod p
        BigInteger c = cifra(textoPlano,k,primo);
        //envia cifra e chave publica de A
        return new BigInteger[] {c,y_A};
    }
    public BigInteger calculaChavePublica(BigInteger chavePrivada,BigInteger raiz,BigInteger primo ){
        return raiz.modPow(chavePrivada,primo);
    }
    public BigInteger calcula_K(BigInteger chavePublica,BigInteger chavePrivada,BigInteger primo){
        return chavePublica.modPow(chavePrivada,primo);
    }
    private BigInteger cifra(BigInteger mensagem,BigInteger K,BigInteger primo){
        return mensagem.multiply(K).mod(primo);
    }
}
