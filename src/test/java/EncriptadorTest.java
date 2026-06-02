import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class EncriptadorTest {

    Encriptador encriptador = new Encriptador();

    // --- calculaChavePublica ---

    @ParameterizedTest
    @CsvSource({
        "3, 5, 23, 10",
        "2, 3, 7,  2",
        "5, 7, 23, 17"
    })
    public void calculaChavePublica_deveRetornarValorCorretoComValoresConhecidos(
            int chavePrivada, int raiz, int primo, int esperado) {
        BigInteger resultado = encriptador.calculaChavePublica(
                BigInteger.valueOf(chavePrivada),
                BigInteger.valueOf(raiz),
                BigInteger.valueOf(primo)
        );
        assertEquals(BigInteger.valueOf(esperado), resultado);
    }

    @Test
    public void calculaChavePublica_naoDeveRetornarNulo() {
        assertNotNull(encriptador.calculaChavePublica(
                BigInteger.valueOf(3),
                BigInteger.valueOf(5),
                BigInteger.valueOf(23)
        ));
    }

    @Test
    public void calculaChavePublica_deveRetornarResultadoNoIntervaloValido() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger resultado = encriptador.calculaChavePublica(
                BigInteger.valueOf(3),
                BigInteger.valueOf(5),
                p
        );
        assertTrue(resultado.compareTo(BigInteger.ONE) >= 0, "y_A deve ser >= 1");
        assertTrue(resultado.compareTo(p) < 0, "y_A deve ser < p");
    }

    // --- calcula_K ---

    @ParameterizedTest
    @CsvSource({
        "10, 6, 23, 6",
        "17, 3, 23, 14",
        "3,  2,  5, 4"
    })
    public void calcula_K_deveRetornarValorCorretoComValoresConhecidos(
            int chavePublica, int chavePrivada, int primo, int esperado) {
        BigInteger resultado = encriptador.calcula_K(
                BigInteger.valueOf(chavePublica),
                BigInteger.valueOf(chavePrivada),
                BigInteger.valueOf(primo)
        );
        assertEquals(BigInteger.valueOf(esperado), resultado);
    }

    @Test
    public void calcula_K_naoDeveRetornarNulo() {
        assertNotNull(encriptador.calcula_K(
                BigInteger.valueOf(10),
                BigInteger.valueOf(6),
                BigInteger.valueOf(23)
        ));
    }

    @Test
    public void calcula_K_deveExibirSimetriaDiffieHellman() {
        // y_A^x_B mod p == y_B^x_A mod p
        // p=23, g=5, x_A=3, x_B=6 => y_A=10, y_B=g^x_B mod p
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        BigInteger x_A = BigInteger.valueOf(3);
        BigInteger x_B = BigInteger.valueOf(6);
        BigInteger y_A = g.modPow(x_A, p);
        BigInteger y_B = g.modPow(x_B, p);

        BigInteger k_B = encriptador.calcula_K(y_A, x_B, p);
        BigInteger k_A = encriptador.calcula_K(y_B, x_A, p);

        assertEquals(k_B, k_A, "K deve ser igual para ambos os lados (propriedade Diffie-Hellman)");
    }

    // --- Encriptar ---

    @Test
    public void encriptar_deveRetornarArrayComDoisElementos() {
        BigInteger[] resultado = encriptador.Encriptar(
                BigInteger.valueOf(7),
                BigInteger.valueOf(23),
                BigInteger.valueOf(5),
                8,
                BigInteger.valueOf(6)
        );
        assertNotNull(resultado);
        assertEquals(2, resultado.length, "Encriptar deve retornar [C, y_A]");
    }

    @Test
    public void encriptar_cifraDeveEstarNoIntervaloValido() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger[] resultado = encriptador.Encriptar(
                BigInteger.valueOf(7), p, BigInteger.valueOf(5), 8, BigInteger.valueOf(6)
        );
        BigInteger C = resultado[0];
        assertTrue(C.compareTo(BigInteger.ZERO) >= 0, "C deve ser >= 0");
        assertTrue(C.compareTo(p) < 0, "C deve ser < p");
    }

    @Test
    public void encriptar_chavePublicaDeveEstarNoIntervaloValido() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger[] resultado = encriptador.Encriptar(
                BigInteger.valueOf(7), p, BigInteger.valueOf(5), 8, BigInteger.valueOf(6)
        );
        BigInteger y_A = resultado[1];
        assertTrue(y_A.compareTo(BigInteger.ONE) >= 0, "y_A deve ser >= 1");
        assertTrue(y_A.compareTo(p) < 0, "y_A deve ser < p");
    }

    @Test
    public void encriptar_deveSerDecifravelComDecriptador() {
        // Round-trip: Encriptar e depois Decifrar deve retornar a mensagem original
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        BigInteger x_B = BigInteger.valueOf(6);
        BigInteger mensagemOriginal = BigInteger.valueOf(7);

        BigInteger[] resultado = encriptador.Encriptar(mensagemOriginal, p, g, 8, x_B);
        BigInteger C = resultado[0];
        BigInteger y_A = resultado[1];

        Decriptador decriptador = new Decriptador();
        BigInteger mensagemDecifrada = decriptador.decifrar(C, x_B, y_A, p);

        assertEquals(mensagemOriginal, mensagemDecifrada,
                "Decifrar(Encriptar(m)) deve retornar a mensagem original");
    }

    @Test
    public void encriptar_deveProduizirCifrasDiferentesParaMesmaEntrada() {
        // x_A é aleatório a cada chamada, então C e y_A variam
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        BigInteger x_B = BigInteger.valueOf(6);
        BigInteger m = BigInteger.valueOf(7);

        BigInteger[] r1 = encriptador.Encriptar(m, p, g, 64, x_B);
        BigInteger[] r2 = encriptador.Encriptar(m, p, g, 64, x_B);

        // Probabilidade de x_A idêntico em 64 bits é astronomicamente baixa
        assertFalse(r1[0].equals(r2[0]) && r1[1].equals(r2[1]),
                "Encriptações da mesma mensagem dificilmente produzem C e y_A iguais");
    }
}