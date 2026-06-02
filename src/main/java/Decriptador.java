import java.math.BigInteger;

public class Decriptador {
    public BigInteger decifrar(BigInteger C, BigInteger x_B, BigInteger y_A, BigInteger p){
        BigInteger k = calcula_K(y_A, x_B, p);
        BigInteger kInverso = modInverso(k,p);
        return C.multiply(kInverso).mod(p);
    }

    public BigInteger calcula_K(BigInteger chavePublica, BigInteger chavePrivada, BigInteger primo){
        return chavePublica.modPow(chavePrivada, primo);
    }

    public BigInteger modInverso(BigInteger k, BigInteger p) {
        BigInteger t = BigInteger.ZERO;
        BigInteger newT = BigInteger.ONE;
        BigInteger r = p;
        BigInteger newR = k;

        while (!newR.equals(BigInteger.ZERO)) {
            BigInteger quociente = r.divide(newR);

            // atualiza os coeficientes: t = newT, newT = t - quociente * newT
            BigInteger tempT = t.subtract(quociente.multiply(newT));
            t = newT;
            newT = tempT;

            // atualiza os restos: r = newR, newR = r - quociente * newR
            BigInteger tempR = r.subtract(quociente.multiply(newR));
            r = newR;
            newR = tempR;
        }

        // garante que o resultado esteja no intervalo [0, p)
        if (t.compareTo(BigInteger.ZERO) < 0) {
            t = t.add(p);
        }

        return t;
    }
}
