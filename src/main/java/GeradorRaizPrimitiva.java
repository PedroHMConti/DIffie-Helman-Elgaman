import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeradorRaizPrimitiva {

    public BigInteger achadorDeRaiz(BigInteger primo) {
        BigInteger pm1 = primo.subtract(BigInteger.ONE); // p-1
        List<BigInteger> fatores = fatoresPrimos(pm1);

        // Testa candidatos g = 2, 3, 4, ...
        for (BigInteger g = BigInteger.TWO; g.compareTo(primo) < 0; g = g.add(BigInteger.ONE)) {
            if (ehRaizPrimitiva(g, primo, pm1, fatores)) {
                return g;
            }
        }
        throw new RuntimeException("Nenhuma raiz primitiva encontrada");
    }

    // Verifica se g é raiz primitiva módulo p
// Condição: g^((p-1)/q) ≢ 1 (mod p) para todo fator primo q de p-1
    private boolean ehRaizPrimitiva(BigInteger g, BigInteger primo,
                                    BigInteger pm1, List<BigInteger> fatores) {
        for (BigInteger q : fatores) {
            BigInteger exp = pm1.divide(q);
            if (g.modPow(exp, primo).equals(BigInteger.ONE)) {
                return false; // falhou: g^((p-1)/q) ≡ 1, descarta esse g
            }
        }
        return true;
    }

    // Fatores primos distintos de n
    private List<BigInteger> fatoresPrimos(BigInteger n) {
        List<BigInteger> fatores = new ArrayList<>();
        BigInteger dois = BigInteger.TWO;

        while (n.mod(dois).equals(BigInteger.ZERO)) {
            if (!fatores.contains(dois)) fatores.add(dois);
            n = n.divide(dois);
        }

        for (BigInteger i = BigInteger.valueOf(3);
             i.multiply(i).compareTo(n) <= 0;
             i = i.add(BigInteger.TWO)) {
            while (n.mod(i).equals(BigInteger.ZERO)) {
                if (!fatores.contains(i)) fatores.add(i);
                n = n.divide(i);
            }
        }

        if (n.compareTo(BigInteger.ONE) > 0) fatores.add(n);
        return fatores;
    }
}

