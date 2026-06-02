import java.math.BigInteger;
import java.security.SecureRandom;

public class GeraSegredo {
    SecureRandom random = new SecureRandom();
    //ambas chaves secretas devem estar no intervalo[2,p-2]
    public BigInteger criaSegredo(int tamanho){
        return new BigInteger(tamanho,random);
    }
}
