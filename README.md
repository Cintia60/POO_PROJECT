# POO Financial Services (POOFS)

## Descrição do Projeto
O projeto implementado é uma aplicação de gestão financeira denominada **POO Financial Services (POOFS)**, desenvolvida para facilitar o registo e o controlo de faturas emitidas por uma empresa, com funcionalidades que permitem a exportação para o portal das Finanças.

Esta aplicação é projetada para lidar com diferentes tipos de produtos, como alimentares e farmacêuticos, e gerenciar as diversas taxas de IVA e condições fiscais associadas, que variam de acordo com a localização do cliente e as características específicas de cada produto.

A aplicação foi desenvolvida utilizando a linguagem **Java**, seguindo uma abordagem de **Programação Orientada a Objetos (POO)**, com ênfase em conceitos fundamentais como **herança, polimorfismo e encapsulamento**. Isso permitiu a criação de uma estrutura modular e flexível, onde diferentes tipos de produtos (alimentares e farmacêuticos) podem ser tratados de forma especializada, mas ainda mantendo a uniformidade na gestão de dados e operações.

Além disso, o projeto incorpora a **persistência de dados**, garantindo que as informações sobre clientes, faturas e produtos sejam armazenadas permanentemente. Para isso, utilizamos dois métodos principais de armazenamento:
- **Ficheiros de texto** (para leitura e escrita legível).
- **Ficheiros de objeto** (para salvar dados de forma eficiente em formato binário).

Esse processo de persistência assegura que os dados não sejam perdidos entre as execuções do programa, permitindo que sejam recuperados sempre que o sistema for reiniciado.

---

## Diagrama UML
*(Inserir o diagrama UML do sistema aqui)*

---

## Desenvolvimento das Classes

### 1. Classe Cliente
A classe `Cliente` é utilizada para representar um cliente de forma segura e validada em sistemas que requerem manipulação de dados pessoais ou fiscais.

#### **Atributos**
- `nome`: Armazena o nome do cliente.
- `tipoLocalizacao`: Indica a localização do cliente, que deve ser "Continente", "Madeira" ou "Açores".
- `numeroContribuinte`: Representa o número fiscal do cliente.

#### **Métodos**
- `Construtor`: Inicializa os atributos e realiza validações nos setters.
- `getNome()` / `setNome(String nome)`: Obtém ou define o nome do cliente.
- `getTipoLocalizacao()` / `setTipoLocalizacao(String tipo)`: Valida e define a localização.
- `getNumeroContribuinte()` / `setNumeroContribuinte(int num)`: Valida e define o número de contribuinte.
- `toString()`: Retorna uma representação textual dos atributos do cliente.

#### **Validações**
- `tipoLocalizacao` deve ser "Continente", "Madeira" ou "Açores".
- `numeroContribuinte` deve ser um número positivo.

#### **Notas Adicionais**
- A classe implementa `Serializable` para permitir a serialização.
- Definição do `serialVersionUID` para garantir compatibilidade.

---

### 2. Classe Produto
A classe `Produto` é **abstrata** e define a estrutura base de um produto no sistema. Deve ser estendida por subclasses que implementam métodos específicos para cada tipo de produto.

#### **Atributos**
- `codigo`: Identificador único.
- `nome`: Nome do produto.
- `descricao`: Descrição detalhada.
- `valorUnitario`: Preço sem IVA.
- `tipoTaxa`: Pode ser "Taxa reduzida", "Taxa intermédia" ou "Taxa normal".
- `quantidade`: Quantidade disponível.

#### **Métodos**
- Métodos `get` e `set` para os atributos com validações.
- `calcularTotalIVA(String tipoLocalizacao)`: Calcula o IVA com base na localização.
- `toString()`: Representação textual do produto.

#### **Métodos Abstratos**
- `isTipoTaxaObrigatorio()`: Determina se a taxa é obrigatória.
- `calcularImposto(String tipoLocalizacao)`: Calcula o imposto aplicável.
- `getCategoria()`: Retorna a categoria do produto.
- `getDetalhesFatura(String tipoLocalizacao)`: Retorna detalhes do produto para faturação.
- `getTipo()`: Retorna o tipo do produto.

#### **Validações**
- `valorUnitario` deve ser positivo.
- `tipoTaxa` deve ser "Taxa reduzida", "Taxa intermédia" ou "Taxa normal".
- `quantidade` não pode ser negativa.

---

### 3. Classe ProdutoAlimentar
Subclasse de `Produto`, representa produtos alimentares.

#### **Atributos**
- `categoria`: Exemplo: "congelados", "enlatados" ou "vinho".
- `biologico`: Se é um produto biológico.
- `certificacoes`: Lista de certificações válidas.

#### **Métodos**
- Validações específicas para certificações e categorias.
- `calcularImposto(String tipoLocalizacao)`: Ajusta impostos para produtos biológicos ou com certificações.
- `toString()`: Representação textual do produto.

#### **Regras de Cálculo**
- **Biológico**: 10% de desconto no IVA.
- **4 Certificações**: Redução de 1% no IVA.
- **Categoria "Vinho"**: Aumento de 1% no IVA.

---

### 4. Classe ProdutoFarmacia
Subclasse de `Produto`, representa produtos farmacêuticos.

#### **Atributos**
- `comPrescricao`: Indica se requer receita médica.
- `categoria`: Apenas para produtos sem prescrição.
- `medico`: Nome do médico para produtos com prescrição.

#### **Métodos**
- `getTipo()`: Retorna "Farmacia".
- `calcularImposto(String tipoLocalizacao)`: Taxas reduzidas para medicamentos com prescrição.
- `toString()`: Representação textual do produto.

#### **Regras de Cálculo**
- **Com prescrição**: IVA reduzido (6% no Continente, 5% na Madeira, 4% nos Açores).
- **Sem prescrição**: IVA normal (23%), exceto "Animais", que recebe redução de 1%.

---

### 5. Classe Fatura
Representa um registo de compra associando um cliente aos produtos adquiridos.

#### **Atributos**
- `numero`: Identificador único.
- `cliente`: Cliente da compra.
- `produtos`: Lista de produtos comprados.
- `valorTotalSemIVA`: Total sem IVA.
- `valorTotalIva`: Valor total do IVA.
- `valorTotalComIVA`: Total com IVA.
- `data`: Data da fatura.

#### **Métodos**
- `adicionarProduto(Produto produto)`: Adiciona um produto e recalcula os totais.
- `recalcularValores()`: Atualiza os valores totais.
- `listarProdutos()`: Lista os produtos na fatura.
- `toString()`: Representação textual da fatura.

---

## Conclusão
O **POOFS** foi desenvolvido seguindo boas práticas de **POO**, incluindo **herança, polimorfismo, encapsulamento e persistência de dados**, garantindo modularidade, flexibilidade e integridade das informações fiscais.
