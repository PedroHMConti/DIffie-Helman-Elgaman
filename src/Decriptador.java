import java.math.BigInteger;

public class Decriptador {
    //receber x_B,y_A,g,p,C
    //calcular K usando k = y_A**x_B mod p
    //calcular k-1
    //calcular mensagem m = C*(k-1) mod p
    public BigInteger decifrar(BigInteger C,BigInteger x_B,BigInteger y_A, BigInteger p){
        //calcular K = y_A^(x_B) mod p ou y_B^(x_A)
        BigInteger k = calcula_K(y_A,x_B,p);
        BigInteger kInverso = euclidesExtendido(k,C,p);
        return C.multiply(kInverso).mod(p);

    }
    public BigInteger calcula_K(BigInteger chavePublica,BigInteger chavePrivada,BigInteger primo){
        return chavePublica.modPow(chavePrivada,primo);
    }

    public BigInteger euclidesExtendido(BigInteger k,BigInteger C, BigInteger p){
            BigInteger p0 = p;
            BigInteger x0 = BigInteger.ZERO;
            BigInteger x1 = BigInteger.ONE;

            if (p.equals(BigInteger.ONE)) return BigInteger.ZERO;

            while (C.compareTo(BigInteger.ONE) > 0) {
                    BigInteger q = C.divide(p);
                    BigInteger t = p;

                    p = C.mod(p);
                    C = t;

                    t  = x0;
                    x0 = x1.subtract(q.multiply(x0));
                    x1 = t;
                }

            return x1.add(p0).mod(p0);
        }

}
