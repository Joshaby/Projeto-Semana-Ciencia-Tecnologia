# Projeto-Semana-Ciencia-Tecnologia

Criação de uma API REST de uma biblioteca!

Após feito a configuração do inicial projeto, é hora de programar! Siga os passos abaixo de acordo com o deseronlar do curso. 

## Passos

### Passo 1: Criação das entidades e entendedo o JPA e Hibernate

Primeiramente, vamos criar as classes que iriam ser nossas entidades de banco(lembrando que elas ficaram na pasta entity). Abaixo, segue as classes:

```java
// Livro

public class Livro {

    private String nome;

    private LocalDate publicacao;
    
    private String descricao;
    
    private Autor autor;
}
```

Classe Autor

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

```java
// Livro

@Entity
public class Livro {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private LocalDate publicacao;
    
    private String descricao;
    
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
    
    private Genero genero;
}
```

