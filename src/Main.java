import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConversorDeMensagem conversorDeMensagem = new ConversorDeMensagem();
        System.out.println(conversorDeMensagem.stringToInt("puta que pariu , é o melhor goleiro do Brasil , Everson!"));
        BigInteger numero = new BigInteger("81739415858377148502270687223028346061075343870847382750714917648891230035909483473449197051515466159958372693804389561960961234145603105");


        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while(running){
            System.out.println("-------- MENU ELGAMAL --------");
            System.out.println("1 - Gerar p e q ");
            System.out.println("2 - Gerar chave secreta de B ");
            System.out.println("3 - Criptografar texto ");
            System.out.println("4 - Descriptografar texto ");
            System.out.println("0 - sair");
            System.out.printf("Opção: ");
            switch (sc.nextLine()){
                case "1":
                    System.out.println("informe o tamanho (ex: 128): ");
                    int tamanho = Integer.parseInt(sc.nextLine());
                    GeradorPrimo gerador = new GeradorPrimo();
                    BigInteger primo;
                    primo = gerador.gerarPrimo(tamanho);
                    GeradorRaizPrimitiva raizPrimitiva = new GeradorRaizPrimitiva();
                    BigInteger raizprima = raizPrimitiva.achadorDeRaiz(primo);
                    System.out.println("p: "+ primo);
                    System.out.println("g: "+ raizprima);
                    break;

                case "2":
                    System.out.println("informe o tamanho da chave de B: ");
                    int tamanhoChaveB = Integer.parseInt(sc.nextLine());
                    GeraSegredo geraSegredo = new GeraSegredo();
                    System.out.println(geraSegredo.criaSegredo(tamanhoChaveB));
                    break;

                case "3":
                    System.out.println("Informe o p: ");
                    BigInteger p = new BigInteger(sc.nextLine());
                    System.out.println("Informe o g: ");
                    BigInteger g = new BigInteger(sc.nextLine());
                    System.out.println("Informe o x_B: ");
                    BigInteger x_B = new BigInteger(sc.nextLine());
                    System.out.println("Informe o tamanho de x_A(entre 2 e p-2): ");
                    int tamanhoX_A = Integer.parseInt((sc.nextLine()));
                    System.out.println("informe a mensagem: ");
                    String mensagem = sc.nextLine();
                    BigInteger mensagemConvertida = conversorDeMensagem.stringToInt(mensagem);
                    if (mensagemConvertida.compareTo(p) >= 0 || mensagemConvertida.compareTo(BigInteger.ZERO) < 0) {
                        System.out.println("Mensagem inválida");
                        break;
                    }
                    Encriptador encriptador = new Encriptador();
                    BigInteger[] resultado = encriptador.Encriptar(mensagemConvertida, p, g, tamanhoX_A,x_B);
                    System.out.println("Cifra   (C): " + resultado[0]);
                    System.out.println("Chave pública y_A: " + resultado[1]);
                    break;

                case "4":
                    System.out.println("em construção");
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    System.out.println("opção inválida");
                    break;
            }
        }
    }

}