import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ConversorDeMensagemTest {

    ConversorDeMensagem conversorDeMensagem = new ConversorDeMensagem();

    @Test
    public void deveConnverterCorretamente() {
        String original = "Hello";
        BigInteger numero = conversorDeMensagem.stringToInt(original);
        String resultado = conversorDeMensagem.intToString(numero);
        assertEquals(original, resultado);
    }

    @Test
    public void deveConverterStringParaBigIntegerPositivo() {
        BigInteger resultado = conversorDeMensagem.stringToInt("A");
        assertTrue(resultado.signum() > 0);
    }

    @Test
    public void deveConverterStringDiferentesParaValoresDiferentes() {
        BigInteger a = conversorDeMensagem.stringToInt("abc");
        BigInteger b = conversorDeMensagem.stringToInt("xyz");
        assertNotEquals(a, b);
    }

    @Test
    public void deveRealizarRoundTripComCaracteresUTF8() {
        String original = "Olá, mundo!";
        BigInteger numero = conversorDeMensagem.stringToInt(original);
        String resultado = conversorDeMensagem.intToString(numero);
        assertEquals(original, resultado);
    }

    @Test
    public void deveRealizarRoundTripComStringLonga() {
        String original = "Diffie-Hellman é um protocolo de troca de chaves criptográficas.";
        BigInteger numero = conversorDeMensagem.stringToInt(original);
        String resultado = conversorDeMensagem.intToString(numero);
        assertEquals(original, resultado);
    }

    @Test
    public void deveRealizarRoundTripComUmUnicoCaractere() {
        String original = "Z";
        BigInteger numero = conversorDeMensagem.stringToInt(original);
        String resultado = conversorDeMensagem.intToString(numero);
        assertEquals(original, resultado);
    }

    @Test
    public void deveRealizarRoundTripComNumeros() {
        String original = "1234567890";
        BigInteger numero = conversorDeMensagem.stringToInt(original);
        String resultado = conversorDeMensagem.intToString(numero);
        assertEquals(original, resultado);
    }

    @Test
    public void deveConverterBigIntegerConhecidoParaString() {
        // 'A' em ASCII é 65 (0x41)
        BigInteger numero = BigInteger.valueOf(65);
        String resultado = conversorDeMensagem.intToString(numero);
        assertEquals("A", resultado);
    }

    @Test
    public void stringToIntDeveSerDeterministico() {
        String texto = "teste";
        assertEquals(conversorDeMensagem.stringToInt(texto), conversorDeMensagem.stringToInt(texto));
    }
}