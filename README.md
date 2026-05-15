# Relatório de Implementação: Diffie-Hellman e ElGamal

**Disciplina:** Segurança da Informação  
**Linguagem:** Java 21  

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

Implementada na classe `GeradorRaizPrimitiva`. Para cada candidato a partir de 2, o algoritmo calcula todas as potências `g^i mod p` para `i` de `1` até `p-1`. Um valor é raiz primitiva se o conjunto de resultados contiver todos os inteiros de `1` a `p-1` sem repetição, ou seja, se gerar o grupo multiplicativo completo módulo `p`. O primeiro candidato que satisfizer essa condição é retornado como `g`.

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

O algoritmo de Miller-Rabin é um teste de primalidade probabilístico usado para verificar se um candidato gerado é primo. Como não é determinístico, foi adotado `k = 10` rodadas, o que reduz a probabilidade de erro (falso positivo) para no máximo `4^(-10) ≈ 0,000001%`.

### 4.1 Funcionamento

Dado um candidato `n`, o algoritmo:

1. Escreve `n - 1` na forma `2^r * d` (com `d` ímpar), fatorando os 2s
2. Escolhe uma base `a` aleatória no intervalo `[2, n-2]`
3. Calcula `x = a^d mod n`
4. Se `x == 1` ou `x == n-1`, o candidato passa essa rodada
5. Eleva `x` ao quadrado repetidamente até `r-1` vezes; se `x == n-1` em algum passo, passa
6. Se nenhuma condição for satisfeita, `n` é composto — descartado

Após `k = 10` rodadas com bases diferentes, o candidato é considerado primo com alta confiança.

### 4.2 Uso do BigInteger

Todas as operações foram implementadas com `BigInteger`, usando o método `modPow` para exponenciação modular eficiente — indispensável dado o tamanho dos números envolvidos.

---

## 5. Principais Dificuldades Encontradas

**Limitação do tipo Long para números grandes**  
Inicialmente, `p` era lido com `Long.parseLong`, limitando o valor a 64 bits (~9,2 × 10¹⁸). A solução foi ler `p` diretamente como `new BigInteger(sc.nextLine())`.

**Mensagem maior que p**  
A conversão da string inteira para um único `BigInteger` (via `getBytes`) produzia números maiores que `p`, tornando a mensagem inválida para o ElGamal. A solução foi cifrar a mensagem **caractere por caractere**, onde cada bloco é o valor ASCII do caractere, sempre menor que `p`.

**Operadores relacionais com BigInteger**  
O Java não suporta os operadores `>=`, `<=`, `==` diretamente em `BigInteger`. Todas as comparações precisaram ser reescritas com `.compareTo()`, o que exigiu atenção redobrada na lógica das condições.

**break dentro de switch não encerra o while**  
O `break` de um `case` encerra apenas o `switch`, não o laço externo. Para a opção de saída, foi necessário introduzir uma flag booleana `running = false` para controlar o encerramento do loop.

**Geração da raiz primitiva para primos grandes**  
O algoritmo de busca por força bruta para encontrar `g` é computacionalmente inviável para primos de muitos bits, pois requer calcular `p-1` potências para cada candidato. O sistema funcionou bem para primos de até 32 bits, mas torna-se lento para tamanhos maiores.
