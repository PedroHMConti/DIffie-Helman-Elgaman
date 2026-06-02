import java.math.BigInteger;

public class Decriptador {
    public BigInteger decifrar(BigInteger C, BigInteger x_B, BigInteger y_A, BigInteger p){
        BigInteger k = calcula_K(y_A, x_B, p);
        BigInteger kInverso = k.modInverse(p);
        return C.multiply(kInverso).mod(p);
    }

    public BigInteger calcula_K(BigInteger chavePublica, BigInteger chavePrivada, BigInteger primo){
        return chavePublica.modPow(chavePrivada, primo);
    }
}
