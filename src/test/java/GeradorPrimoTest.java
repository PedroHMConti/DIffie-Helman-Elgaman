
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class GeradorPrimoTest {

    GeradorPrimo geradorPrimo = new GeradorPrimo();

    // --- GerarAleatorio ---

    @ParameterizedTest
    @ValueSource(ints = {8, 16, 32, 64, 128})
    public void gerarAleatorioDeveRetornarNumeroDentroDoIntervalo(int bits) {
        BigInteger resultado = geradorPrimo.GerarAleatorio(bits);
        BigInteger menorValor = BigInteger.TWO.pow(bits - 1);
        BigInteger maiorValor = BigInteger.TWO.pow(bits).subtract(BigInteger.ONE);

        assertTrue(resultado.compareTo(menorValor) >= 0,
                "Resultado deve ser >= 2^(bits-1)");
        assertTrue(resultado.compareTo(maiorValor) <= 0,
                "Resultado deve ser <= 2^bits - 1");
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 16, 32})
    public void gerarAleatorioDeveTerUltimoDigitoCompativelComPrimo(int bits) {
        for (int i = 0; i < 20; i++) {
            BigInteger resultado = geradorPrimo.GerarAleatorio(bits);
            int ultimoDigito = resultado.mod(BigInteger.TEN).intValue();
            assertTrue(ultimoDigito == 1 || ultimoDigito == 3
                            || ultimoDigito == 7 || ultimoDigito == 9,
                    "Último dígito deve ser 1, 3, 7 ou 9, mas foi: " + ultimoDigito);
        }
    }

    @Test
    public void gerarAleatorioNaoDeveRetornarNulo() {
        assertNotNull(geradorPrimo.GerarAleatorio(16));
    }

    @Test
    public void gerarAleatorioDeveProduzirValoresDiferentes() {
        BigInteger a = geradorPrimo.GerarAleatorio(64);
        BigInteger b = geradorPrimo.GerarAleatorio(64);
        // Probabilidade de colisão em 64 bits é astronomicamente baixa
        assertNotEquals(a, b, "Dois números aleatórios de 64 bits dificilmente serão iguais");
    }

    // --- gerarPrimo ---

    @ParameterizedTest
    @ValueSource(ints = {8, 16, 32, 64})
    public void gerarPrimoDeveRetornarNumeroProvavelmentePrimo(int bits) {
        BigInteger primo = geradorPrimo.gerarPrimo(bits);
        // isProbablePrime(100) tem certeza de ~1 - 2^(-100)
        assertTrue(primo.isProbablePrime(100),
                "O número gerado deve ser primo: " + primo);
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 16, 32, 64})
    public void gerarPrimoDeveRetornarNumeroDentroDoIntervalo(int bits) {
        BigInteger primo = geradorPrimo.gerarPrimo(bits);
        BigInteger menorValor = BigInteger.TWO.pow(bits - 1);
        BigInteger maiorValor = BigInteger.TWO.pow(bits).subtract(BigInteger.ONE);

        assertTrue(primo.compareTo(menorValor) >= 0,
                "Primo deve ser >= 2^(bits-1)");
        assertTrue(primo.compareTo(maiorValor) <= 0,
                "Primo deve ser <= 2^bits - 1");
    }

    @Test
    public void gerarPrimoNaoDeveRetornarNulo() {
        assertNotNull(geradorPrimo.gerarPrimo(16));
    }

    @Test
    public void gerarPrimoDeveRetornarNumeroImpar() {
        BigInteger primo = geradorPrimo.gerarPrimo(32);
        assertEquals(1, primo.mod(BigInteger.TWO).intValue(),
                "Primos gerados devem ser ímpares (exceto 2)");
    }

    @Test
    public void gerarPrimoDeveProduzirResultadosDistintos() {
        BigInteger p1 = geradorPrimo.gerarPrimo(64);
        BigInteger p2 = geradorPrimo.gerarPrimo(64);
        assertNotEquals(p1, p2, "Dois primos de 64 bits dificilmente serão iguais");
    }
}