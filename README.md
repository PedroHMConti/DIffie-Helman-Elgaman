# Relatório de Implementação: Diffie-Hellman e ElGamal

- **Disciplina:** Segurança da Informação  
- **Linguagem:** Java 21  
- **Repositório GitHub:** https://github.com/PedroHMConti/DIffie-Helman-Elgaman
- **Imagem Docker Hub:** `pedroconti/diffie-helman`


> **Observação:** O arquivo `diffie-helman.tar` não pôde ser enviado via Aprender3 devido a limitações da plataforma. A imagem Docker está disponível publicamente no Docker Hub e o código-fonte no GitHub, conforme links acima.

---

## 0. Instruções para compilar e executar a aplicação

**Pré-requisito:** Docker instalado na máquina.

```bash
# Ubuntu/Debian
sudo apt install docker.io
sudo systemctl start docker
```

**Passos para executar:**

1. Baixe a imagem do Docker Hub:
   ```bash
   docker pull pedroconti/diffie-helman
   ```

2. Execute a aplicação (a flag `-it` é obrigatória):
   ```bash
   docker run -it pedroconti/diffie-helman
   ```

---


## 1. Descrição da Solução Desenvolvida

O projeto implementa versões simplificadas do protocolo Diffie-Hellman e do esquema de criptografia ElGamal, aplicados em um sistema interativo de envio de mensagens criptografadas.

O sistema simula a comunicação entre dois participantes, A e B, sem o uso de sockets ou programação de rede. A troca de informações é feita por meio de um menu interativo implementado com a classe `Scanner`, operando sobre a entrada e saída padrão do Java. O fluxo é controlado por um laço `while` com flag booleana, garantindo execução contínua até que o usuário escolha encerrar.

O menu oferece as seguintes opções:

- **Opção 1** — Gerar o primo `p` e a raiz primitiva `g`
- **Opção 2** — Gerar a chave secreta `x_B` e a chave pública `y_B` do participante B
- **Opção 3** — Criptografar uma mensagem usando ElGamal
- **Opção 4** — Descriptografar uma mensagem
- **Opção 0** — Encerrar o programa

Todas as implementações foram feitas manualmente, sem bibliotecas externas de criptografia. A única exceção foi a geração de números pseudoaleatórios, para a qual foi utilizada a classe `SecureRandom` da biblioteca padrão do Java, que obtém entropia do sistema operacional, tornando os valores gerados computacionalmente imprevisíveis.

---

## 2. Implementação do Diffie-Hellman

O protocolo Diffie-Hellman permite que dois participantes estabeleçam um segredo compartilhado `K` por meio de um canal público inseguro, sem jamais transmiti-lo diretamente. O protocolo depende de dois parâmetros públicos: um primo grande `p` e um gerador `g` (raiz primitiva módulo `p`).

### 2.1 Geração do Primo p

Implementada na classe `GeradorPrimo`. O usuário informa o tamanho desejado em bits, e a função gera candidatos aleatórios com `SecureRandom` de forma iterativa, testando a primalidade de cada um com o algoritmo de Miller-Rabin até encontrar um primo válido.

### 2.2 Geração da Raiz Primitiva g

Implementada na classe `GeradorRaizPrimitiva`. O algoritmo utiliza o método baseado na fatoração de `p-1`:

1. Calcula `p-1` e obtém seus fatores primos distintos `q₁, q₂, ..., qₖ`
2. Para cada candidato `g` a partir de 2, verifica se `g` é raiz primitiva testando a condição:

```
g^((p-1)/q) ≢ 1 (mod p)  para todo fator primo q de p-1
```

Se algum fator `q` fizer `g^((p-1)/q) ≡ 1 (mod p)`, o candidato é descartado. O primeiro `g` que passar em todos os testes é retornado como raiz primitiva. Esse método é eficiente porque evita calcular todas as `p-1` potências, trabalhando apenas com os fatores primos distintos de `p-1`.

### 2.3 Geração das Chaves e Segredo Compartilhado

Cada participante gera sua chave secreta `x` escolhendo um número aleatório no intervalo `[2, p-2]`. A chave pública é calculada como:

```
y = g^x mod p
```

O segredo compartilhado `K` é derivado por cada participante a partir da chave pública do outro:

```
K = y_B^x_A mod p  (calculado por A)
K = y_A^x_B mod p  (calculado por B)
```

Pelo Teorema de Diffie-Hellman, ambos chegam ao mesmo valor de `K`.

---

## 3. Implementação do ElGamal

O ElGamal é um esquema de criptografia assimétrica construído sobre o protocolo Diffie-Hellman. A criptografia é realizada pela classe `Encriptador`.

### 3.1 Geração de Chaves

O participante B gera sua chave secreta `x_B` (número aleatório de tamanho configurável em bits) e calcula sua chave pública:

```
y_B = g^x_B mod p
```

A chave pública `(p, g, y_B)` é compartilhada. A chave secreta `x_B` é mantida em segredo.

### 3.2 Criptografia

Para cifrar, o participante A:

1. Gera uma chave efêmera secreta `x_A` aleatória
2. Calcula sua chave pública efêmera: `y_A = g^x_A mod p`
3. Calcula a chave de sessão: `K = y_B^x_A mod p`
4. Calcula a cifra: `C = m * K mod p`
5. Envia o par `(C, y_A)` para B

Como a mensagem convertida para inteiro pode ser maior que `p`, a criptografia opera **caractere por caractere**. Cada caractere é convertido para seu valor ASCII (`BigInteger.valueOf((long) ch)`), garantindo que o bloco seja sempre menor que `p` (qualquer `p > 127` é suficiente para ASCII).

### 3.3 Descriptografia

O participante B, de posse de `(C, y_A)` e sua chave secreta `x_B`:

1. Recalcula a chave de sessão: `K = y_A^x_B mod p`
2. Calcula o inverso modular de `K`: `K_inv = K^(-1) mod p`
3. Recupera a mensagem: `m = C * K_inv mod p`
4. Converte cada inteiro de volta ao caractere: `(char) m.intValue()`

---

## 4. Implementação do Miller-Rabin

O algoritmo de Miller-Rabin é um teste de primalidade probabilístico implementado manualmente na classe `GeradorPrimo`, método `millerRabin`. Com `k = 10` rodadas independentes, a probabilidade máxima de um falso positivo (composto classificado como primo) é `4^(-10) ≈ 9,5 × 10⁻⁷`.

### 4.1 Base matemática

O algoritmo parte do Pequeno Teorema de Fermat: se `n` é primo, então para qualquer base `a` com `1 < a < n-1`:

```
a^(n-1) ≡ 1 (mod n)
```

Isso implica que `a^(n-1) - 1 ≡ 0 (mod n)`. Como `n` é primo e portanto ímpar (para `n > 2`), `n-1` é par e pode ser escrito como `n-1 = 2^r · d`, com `d` ímpar. Fatorando a diferença de quadrados repetidamente:

```
a^(n-1) - 1 = (a^d - 1)(a^d + 1)(a^(2d) + 1) ··· (a^(2^(r-1)·d) + 1)
```

Se `n` é primo, ele divide esse produto, logo deve dividir pelo menos um dos fatores. Isso significa que uma das seguintes condições deve ser verdadeira:

```
a^d         ≡  1  (mod n)   ← fator (a^d - 1)
a^(2^j · d) ≡ -1  (mod n)   ← fator (a^(2^j·d) + 1), para algum j ∈ {0, ..., r-1}
```

Nota: `-1 mod n` equivale a `n-1`, por isso o código compara com `n.subtract(BigInteger.ONE)`.

### 4.2 Etapas da implementação

**Pré-processamento — fatoração de `n-1`:**

```java
int r = 0;
BigInteger d = n.subtract(BigInteger.ONE);
while (!d.testBit(0)) {   // enquanto d for par
    d = d.shiftRight(1);  // d = d / 2
    r++;
}
// ao sair: n-1 = 2^r · d, com d ímpar
```

Essa decomposição é feita uma única vez antes do loop das rodadas, pois depende apenas de `n`.

**Rodadas com testemunhas aleatórias:**

Para cada uma das 10 rodadas, sorteia-se uma base `a` aleatória em `[2, n-2]` e calcula-se `x = a^d mod n`. A seguir, o algoritmo testa as condições derivadas acima:

- Se `x == 1` ou `x == n-1`: `n` passa nessa rodada (condições satisfeitas diretamente)
- Caso contrário, eleva `x` ao quadrado até `r-1` vezes buscando `x == n-1`:
  - Se encontrar: `n` passa nessa rodada
  - Se não encontrar após `r-1` elevações: `n` é **composto com certeza** — retorna `false`

Se `n` sobreviver a todas as 10 rodadas, é considerado primo com alta confiança.

### 4.3 Filtragem prévia de candidatos

Antes de aplicar Miller-Rabin, o método `GerarAleatorio` já descarta candidatos cujo último dígito decimal seja `0, 2, 4, 5, 6` ou `8`, pois esses números são divisíveis por 2 ou 5 e obviamente compostos. Isso reduz o número de chamadas ao teste probabilístico.

---

## 5. Principais Dificuldades Encontradas

**Limitação do tipo Long para números grandes**  
Inicialmente, `p` era lido com `Long.parseLong`, limitando o valor a 64 bits (~9,2 × 10¹⁸). A solução foi ler `p` diretamente como `new BigInteger(sc.nextLine())`.


**Operadores relacionais com BigInteger**  
O Java não suporta os operadores `>=`, `<=`, `==` diretamente em `BigInteger`. Todas as comparações precisaram ser reescritas com `.compareTo()`, o que exigiu atenção redobrada na lógica das condições.

**break dentro de switch não encerra o while**  
O `break` de um `case` encerra apenas o `switch`, não o laço externo. Para a opção de saída, foi necessário introduzir uma flag booleana `running = false` para controlar o encerramento do loop.

**Geração da raiz primitiva para primos grandes**  
O algoritmo de busca por força bruta para encontrar `g` é computacionalmente inviável para primos de muitos bits, pois requer calcular `p-1` potências para cada candidato. O sistema funcionou bem para primos de até 32 bits, mas torna-se lento para tamanhos maiores.
