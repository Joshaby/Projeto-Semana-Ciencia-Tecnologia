# Projeto-Semana-Ciencia-Tecnologia

Criação de uma API REST de uma biblioteca!

## Passos

Após feito a configuração do inicial projeto, é hora de programar! Siga os passos abaixo de acordo com o deseronlar do curso.

### Passo 1: Criação das entidades

Primeiramente, vamos criar as classes que iriam ser nossas entidades de banco(lembrando que elas ficaram na pasta entity). Também precisamos criar um enum(usado para um conter um grupo de constantes), que será usado como gênero no Autor. O enum ficará na pasta enumeration, dentro de entity. Abaixo, segue as classes e o enum:

```java
// Livro

public class Livro {

    private String nome;

    private LocalDate publicacao;
    
    private String descricao;
}
```

Aqui no enum, nos deparamos com as anotações __@Getter__ e __AllArgsConstructor__ que criam os getters dos atributos do enum e construtor do enum com todos os atributos em tempo de compilação, respectivamente.
```java
@Getter
@AllArgsConstructor
public enum Genero {

    MASCULINO(1),
    FEMININO(2),
    OUTRO(3);

    private Integer tipo;

    public static Genero toEnum(Integer tipo) {
        if (tipo == null) {
            return null;
        }
//        for (Genero genero : values()) {
//            if (genero.getTipo() == tipo) return genero;
//        }
//        throw new RuntimeException();
        Optional<Genero> genero = Arrays.stream(values()).filter(g -> g.getTipo().equals(tipo)).findFirst();
        return genero.orElseThrow(RuntimeException::new);
    }
}

```

```java
// Autor

public class Autor {

    private String nome;

    private String sobrenome;

    private LocalDate dataNascimento;
    
    private String email;
    
    private Genero genero;
}
```

Para que essas classes sejam entidades de banco, precisamos "anotar-lá" com __@Entity__(programação com em projetos Spring usam bastante anotações). Além disso, precisamos definir o id dessas classes/entidades, isso para garantir unicidade a nível de banco. Para isso, usamos a anotação __@Id__ em cima de um atributo de classe que deva ser o id, seu nome deve ser id, o tipo, tanto faz(usaremos Long)! Tendo o id, precisamos dizer como ele vai ser gerado, usamos a anotação __@GeneratedValue(strategy = GenerationType.IDENTITY)__, essa anotação quer dizer que a geração do id será feita pelo SGBD em uso. Logo, as classes ficaram da seguinte forma:

Um livro poderá ter uma descrição muito grande por exemplo, e sem anotação __@Length(max = 9999)__ no atributo descrição, teriamos um exceção que indica que estrapolamos o tamanho padrão da descrição no banco
```java
// Livro

@Entity
public class Livro {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private LocalDate publicacao;
    
    @Length(max = 9999)
    private String descricao;
}
```

Em Autor, no genero, precisamos por a anotação @Enumerated(EnumType.STRING), para indicarmos ao Spring Data que sera usado a String do nome do gênero, que são MASCULINO, FEMININO ou OUTRO
```java
// Autor

@Entity
public class Autor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String sobrenome;

    private LocalDate dataNascimento;
    
    private String email;

    @Enumerated(EnumType.STRING)
    private Genero genero;
}
```

Agora vamos configurar o banco H2, ele já funciona normalmente, mas vamos configurar o nosso acesso a ele. Essas configuração ficaram no arquivo application.yml(renomei o arquivo e mude sua extenção), na pasta resource. Segue abaixo a configuração:

```yaml
spring:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console
```

Nesse arquivo configuramos o dialéto SQL aceito pelo SGBD, url de acesso, usuário, senha, endpoint de acesso ao SGBD e outros. Feito essas configuração, precisamos acessar o link http://localhost:8080/h2-console, mas antes disso, inicie o projeto. Após abrir o link, em JDBC URL coloque `jdbc:h2:mem:testdb`, em User Name coloque `sa` e deixe o password em branco. Feito isso, só abrir o SGBD. Vejam que as tabelas de Livro e Autor foram criadas, isso foi feito graças a anotação __@Entity__ das classes.

Agora precisamos criar o relacionamento entre Livro e Autor. A relação entre Livro e Autor é de muitos para um, isso quer dizer que vários livros podem ser feitos por um autor, apenas um, ou, que um autor faz vários livros. Se você estudou bando de dados bem, saberá que nesse relacionamento, colocamos a referência do "um" nos "muitos", que nesse caso será a referência do autor nos Livros. Fazemos isso da seguinte maneira: colocamos um atributo autor do tipo Autor em Livro e anotamos com __@ManyToOne__, e logo abaixo dessa anotação colocamos __@JoinColumn(name = "autor_id")__. Essas duas anotaçãos formam o relacionamento de muitos para um entre livro e autor, e criará uma coluna com o nome autor_id na relação do Livro no banco, ou seja, estamos transformando uma referência de um objeto em uma foreign key na relação. Segue as classes atualizadas:

```java
// Livro

@Entity
public class Livro {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private LocalDate publicacao;
    
    @Length(max = 9999)
    private String descricao;
    
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;
}
```

```java
// Autor

@Entity
public class Autor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String sobrenome;

    private LocalDate dataNascimento;
    
    private String email;

    @Enumerated(EnumType.STRING)
    private Genero genero;
}
```

Ainda nessa parte de relacionamento, podemos ter referências dos Livros em um Autor, e isso só faz sentido no mundo orientado a objetos. Precisamos fazer isso pelo seguinte motivo: caso queiramos remover um Autor, os seus Livros precisam ser removidos. Fazemos isso colocando um List de Livro no Autor, e em cima desse atributo, colocamos a anotação __@OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)__ que indica que existe uma relação "um" para "muitos" entre Autor e Livro, e que essa relação já foi mapeada, nesse caso, foi mapeada no Livro sobre o atributo Autor. Sobre o cascade, ele indica que qualquer operação no Autor, essa operação sera refletida em Livro, que nesse caso é a operação de remoção. Segue o código atualizado:
```java
// Autor

@Entity
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String sobrenome;

    private LocalDate dataNascimento;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Livro> livros = new ArrayList<>();
}
```

Lembrando que as tabelas são criadas a partir das classes de entidades, isso em tempo de compilação, e também em tempo de compilação, podemos inserir dados nessas tabela com inserts SQL normalmente. Para fazer isso, precisamos criar um arquivo `import.sql` dentro da pasta resource do projeto. O nome da tabela e atributos, devem seguir os nomes das classes e seus atributos, caso seja usado camelCase nos nomes, você precisa escrever-los em snake_case no SQL. Segue abaixo as queries:

```sql
INSERT INTO autor(nome, sobrenome, data_nascimento, email, genero) VALUES ('Matt', 'Smith', '1982-11-01', 'jose.azevedo@academico.ifpb.edu.br', 'MASCULINO');
INSERT INTO autor(nome, sobrenome, data_nascimento, email, genero) VALUES ('David', 'Tennant', '1982-11-01', 'josehenriquebrito55@gmail.com', 'MASCULINO');

INSERT INTO livro(nome, publicacao, descricao, autor_id) VALUES ('Doctor Who: Lobo mal', '2022-01-12', 'Lorem ipsum dolor sit amet. Et architecto deleniti sed dicta quas et voluptatem quam. Sed corrupti dolorem ea obcaecati aperiam et doloribus quos ut molestias dolores. Ut obcaecati similique est neque consequatur ut exercitationem ratione sit odio nesciunt.', 1);
INSERT INTO livro(nome, publicacao, descricao, autor_id) VALUES ('Doctor Who: O fim dos tempos', '2022-01-13', 'Lorem ipsum dolor sit amet. Ut ipsum ratione quo itaque deserunt et cumque delectus 33 autem quas et perspiciatis assumenda. Ut aliquam mollitia 33 quia provident et magnam earum 33 nesciunt nostrum et velit consectetur aut galisum dolore qui sunt asperiores. At iste magnam aut modi doloribus sed expedita pariatur. Aut deleniti sint ex voluptas velit sit incidunt expedita.', 2);
```

Chegamos ao fim do passo 1!




