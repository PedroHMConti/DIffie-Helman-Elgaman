import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeradorRaizPrimitivaTest {

    GeradorRaizPrimitiva geradorRaizPrimitiva = new GeradorRaizPrimitiva();

    // --- achadorDeRaiz ---

    @ParameterizedTest
    @CsvSource({
        "5,  2",
        "7,  3",
        "11, 2",
        "23, 5"
    })
    public void achadorDeRaiz_deveRetornarMenorRaizPrimitivaParaPrimosConhecidos(int primo, int raizEsperada) {
        BigInteger resultado = geradorRaizPrimitiva.achadorDeRaiz(BigInteger.valueOf(primo));
        assertEquals(BigInteger.valueOf(raizEsperada), resultado);
    }

    @Test
    public void achadorDeRaiz_naoDeveRetornarNulo() {
        assertNotNull(geradorRaizPrimitiva.achadorDeRaiz(BigInteger.valueOf(7)));
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 7, 11, 13, 17, 19, 23})
    public void achadorDeRaiz_deveRetornarValorNoIntervaloValido(int primo) {
        BigInteger p = BigInteger.valueOf(primo);
        BigInteger resultado = geradorRaizPrimitiva.achadorDeRaiz(p);

        assertTrue(resultado.compareTo(BigInteger.TWO) >= 0,
                "Raiz primitiva deve ser >= 2");
        assertTrue(resultado.compareTo(p.subtract(BigInteger.ONE)) <= 0,
                "Raiz primitiva deve ser <= p-1");
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 7, 11, 13, 17, 19, 23})
    public void achadorDeRaiz_deveRetornarVerdadeiraRaizPrimitiva(int primo) {
        BigInteger p = BigInteger.valueOf(primo);
        BigInteger g = geradorRaizPrimitiva.achadorDeRaiz(p);

        assertTrue(ehRaizPrimitivaValida(g, p),
                "g=" + g + " deve ser raiz primitiva de p=" + p);
    }

    @Test
    public void achadorDeRaiz_deveRetornarOrdemMaximaParaPrimoPequeno() {
        // Para p=7, a raiz primitiva tem ordem exatamente p-1=6:
        // g^k mod p != 1 para todo 1 <= k < p-1
        BigInteger p = BigInteger.valueOf(7);
        BigInteger g = geradorRaizPrimitiva.achadorDeRaiz(p);
        BigInteger pm1 = p.subtract(BigInteger.ONE);

        assertEquals(BigInteger.ONE, g.modPow(pm1, p),
                "g^(p-1) mod p deve ser 1 (Pequeno Teorema de Fermat)");

        for (int k = 1; k < pm1.intValue(); k++) {
            assertNotEquals(BigInteger.ONE, g.modPow(BigInteger.valueOf(k), p),
                    "g^" + k + " mod p não deve ser 1 — ordem deve ser exatamente p-1");
        }
    }

    @Test
    public void achadorDeRaiz_satisfazPequenoTeoremaDeFermat() {
        // g^(p-1) mod p = 1 para qualquer g coprimo com p
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = geradorRaizPrimitiva.achadorDeRaiz(p);

        assertEquals(BigInteger.ONE, g.modPow(p.subtract(BigInteger.ONE), p));
    }

    @Test
    public void achadorDeRaiz_deveRetornarRaizPrimitivaParaPrimoGrande() {
        GeradorPrimo geradorPrimo = new GeradorPrimo();
        BigInteger p = geradorPrimo.gerarPrimo(32);
        BigInteger g = geradorRaizPrimitiva.achadorDeRaiz(p);

        assertNotNull(g);
        assertTrue(g.compareTo(BigInteger.TWO) >= 0, "Raiz primitiva deve ser >= 2");
        assertTrue(g.compareTo(p) < 0, "Raiz primitiva deve ser < p");
        assertTrue(ehRaizPrimitivaValida(g, p), "Deve ser uma raiz primitiva válida");
    }

    // --- helper: verifica propriedade de raiz primitiva ---

    private boolean ehRaizPrimitivaValida(BigInteger g, BigInteger p) {
        BigInteger pm1 = p.subtract(BigInteger.ONE);
        for (BigInteger q : fatoresPrimos(pm1)) {
            if (g.modPow(pm1.divide(q), p).equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    private List<BigInteger> fatoresPrimos(BigInteger n) {
        List<BigInteger> fatores = new ArrayList<>();
        BigInteger dois = BigInteger.TWO;
        while (n.mod(dois).equals(BigInteger.ZERO)) {
            if (!fatores.contains(dois)) fatores.add(dois);
            n = n.divide(dois);
        }
        for (BigInteger i = BigInteger.valueOf(3); i.multiply(i).compareTo(n) <= 0; i = i.add(BigInteger.TWO)) {
            while (n.mod(i).equals(BigInteger.ZERO)) {
                if (!fatores.contains(i)) fatores.add(i);
                n = n.divide(i);
            }
        }
        if (n.compareTo(BigInteger.ONE) > 0) fatores.add(n);
        return fatores;
    }
}