import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class ConversorDeMensagem {
    public BigInteger stringToInt(String text){
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8); //transfoma cada caractere em um byte e faz um array de bytes
        return new BigInteger(1,bytes);
    }

    public String intToString(BigInteger numero){
        byte[] bytes = numero.toByteArray();
        // Remove o byte de sinal (0x00) que o BigInteger pode inserir no início
        if (bytes[0] == 0x00) {
            byte[] trimmed = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, trimmed, 0, trimmed.length);
            bytes = trimmed;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
