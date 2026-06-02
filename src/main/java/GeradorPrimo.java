import java.math.BigInteger;
import java.security.SecureRandom;

public class GeradorPrimo {

    private static final SecureRandom RANDOM = new SecureRandom();

    public BigInteger GerarAleatorio(int bits) {
        // limites
        BigInteger menorValor = BigInteger.TWO.pow(bits - 1);
        BigInteger maiorValor = BigInteger.TWO.pow(bits).subtract(BigInteger.ONE);
        BigInteger range = maiorValor.subtract(menorValor);

        while(true) {
            //Gera um n aleatorio
            BigInteger n = new BigInteger(range.bitLength(), RANDOM);

            //garante que n esteja no intervalo estipulado
            if (n.compareTo(range) > 0) continue;

            n = n.add(menorValor);
            //ja adianta a verificar se e primo
            int ultimoDigito = n.mod(BigInteger.TEN).intValue();
            if (ultimoDigito == 1 || ultimoDigito == 3
                    || ultimoDigito == 7 || ultimoDigito == 9) {
                return n;
            }
        }
    }

    //Gera numero aleatorio A que vai ser elevado a uma potência módulo n para testar se n é primo
    private BigInteger gerarBase(BigInteger n) {
        BigInteger limiteSuperior = n.subtract(BigInteger.TWO);  // n - 2
        BigInteger intervalo = n.subtract(BigInteger.valueOf(3)); // (n-2) - 2 + 1 = n-3

        BigInteger a;
        do {
            // Sorteia número aleatório com o mesmo bitLength de n
            a = new BigInteger(n.bitLength(), RANDOM);
        } while (a.compareTo(BigInteger.TWO) < 0
                || a.compareTo(limiteSuperior) > 0);

        return a;
    }
    private Boolean millerRabin(BigInteger n) {
        // Casos base
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (!n.testBit(0)) return false; // par > 2

        // Fatora n-1 = 2^r · d (UMA VEZ, fora do loop)
        int r = 0;
        BigInteger d = n.subtract(BigInteger.ONE);
        while (!d.testBit(0)) {
            d = d.shiftRight(1);  // d = d / 2
            r++;
        }

        // 10 rodadas com testemunhas diferentes
        for (int i = 0; i < 10; i++) {
            BigInteger a = gerarBase(n);
            BigInteger x = a.modPow(d, n);  // x = a^d mod n

            // Condição A: x == 1 → passa nessa rodada
            // Condição B inicial: x == n-1 → passa nessa rodada
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                continue; // testa próxima testemunha
            }

            // Eleva ao quadrado r-1 vezes procurando n-1
            boolean encontrouMenosUm = false;
            for (int j = 0; j < r - 1; j++) {
                x = x.modPow(BigInteger.TWO, n);  // x = x² mod n
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    encontrouMenosUm = true;
                    break;
                }
            }

            if (!encontrouMenosUm) {
                return false; // composto confirmado
            }
        }

        return true; // sobreviveu a todas as rodadas → provavelmente primo
    }
    public BigInteger gerarPrimo(int bits) {
        while (true) {
            BigInteger candidato = GerarAleatorio(bits);
            if (millerRabin(candidato)) {
                return candidato;
            }
        }
    }
}
