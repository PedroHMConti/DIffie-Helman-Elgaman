import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DecriptadorTest {

    Decriptador decriptador = new Decriptador();

    // --- calcula_K ---

    @Test
    public void calcula_K_deveRetornarValorCorretoComValoresConhecidos() {
        // 10^6 mod 23 = 6
        BigInteger resultado = decriptador.calcula_K(
                BigInteger.valueOf(10),
                BigInteger.valueOf(6),
                BigInteger.valueOf(23)
        );
        assertEquals(BigInteger.valueOf(6), resultado);
    }

    @Test
    public void calcula_K_deveCalcularModPowCorretamente() {
        // 3^2 mod 5 = 9 mod 5 = 4
        BigInteger resultado = decriptador.calcula_K(
                BigInteger.valueOf(3),
                BigInteger.valueOf(2),
                BigInteger.valueOf(5)
        );
        assertEquals(BigInteger.valueOf(4), resultado);
    }

    @Test
    public void calcula_K_naoDeveRetornarNulo() {
        assertNotNull(decriptador.calcula_K(
                BigInteger.valueOf(7),
                BigInteger.valueOf(4),
                BigInteger.valueOf(23)
        ));
    }

    @Test
    public void calcula_K_deveExibirSimetriaDiffieHellman() {
        // y_A^x_B mod p == y_B^x_A mod p (chave compartilhada é a mesma para ambos os lados)
        // p=23, g=7, x_A=5, x_B=3 => y_A=17, y_B=21, K=14
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(7);
        BigInteger x_A = BigInteger.valueOf(5);
        BigInteger x_B = BigInteger.valueOf(3);
        BigInteger y_A = g.modPow(x_A, p);
        BigInteger y_B = g.modPow(x_B, p);

        BigInteger k_B = decriptador.calcula_K(y_A, x_B, p);
        BigInteger k_A = decriptador.calcula_K(y_B, x_A, p);

        assertEquals(k_B, k_A, "K deve ser igual para ambos os lados (propriedade Diffie-Hellman)");
    }

    // --- decifrar ---

    @Test
    public void decifrar_deveRetornarMensagemOriginal() {
        // p=23, x_B=6, y_A=10 (=5^3 mod 23), K=6
        // C = 7 * 6 mod 23 = 19  =>  decifrar deve retornar 7
        BigInteger mensagemDecifrada = decriptador.decifrar(
                BigInteger.valueOf(19),
                BigInteger.valueOf(6),
                BigInteger.valueOf(10),
                BigInteger.valueOf(23)
        );
        assertEquals(BigInteger.valueOf(7), mensagemDecifrada);
    }

    @Test
    public void decifrar_deveRetornarMensagemOriginalEmSegundoCenario() {
        // p=23, g=7, x_A=5, x_B=3, y_A=17, K=14
        // C = 10 * 14 mod 23 = 2  =>  decifrar deve retornar 10
        BigInteger mensagemDecifrada = decriptador.decifrar(
                BigInteger.valueOf(2),
                BigInteger.valueOf(3),
                BigInteger.valueOf(17),
                BigInteger.valueOf(23)
        );
        assertEquals(BigInteger.valueOf(10), mensagemDecifrada);
    }

    @Test
    public void decifrar_comMensagemUm_deveRetornarUm() {
        // m=1: C = 1 * K mod p = K; decifrar(K) deve retornar 1
        // p=23, x_B=6, y_A=10, K=6, C=6
        BigInteger mensagemDecifrada = decriptador.decifrar(
                BigInteger.valueOf(6),
                BigInteger.valueOf(6),
                BigInteger.valueOf(10),
                BigInteger.valueOf(23)
        );
        assertEquals(BigInteger.ONE, mensagemDecifrada);
    }

    @Test
    public void decifrar_deveSerInversoDaEncriptacao() {
        // Fluxo completo com parâmetros fixos (sem aleatoriedade):
        // p=23, g=5, x_A=3, x_B=6, y_A=10, K=6, m=7, C=19
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        BigInteger x_A = BigInteger.valueOf(3);
        BigInteger x_B = BigInteger.valueOf(6);
        BigInteger y_A = g.modPow(x_A, p);
        BigInteger k = y_A.modPow(x_B, p);

        BigInteger mensagemOriginal = BigInteger.valueOf(7);
        BigInteger C = mensagemOriginal.multiply(k).mod(p);

        BigInteger mensagemDecifrada = decriptador.decifrar(C, x_B, y_A, p);

        assertEquals(mensagemOriginal, mensagemDecifrada);
    }

    @Test
    public void decifrar_naoDeveRetornarNulo() {
        assertNotNull(decriptador.decifrar(
                BigInteger.valueOf(19),
                BigInteger.valueOf(6),
                BigInteger.valueOf(10),
                BigInteger.valueOf(23)
        ));
    }
}