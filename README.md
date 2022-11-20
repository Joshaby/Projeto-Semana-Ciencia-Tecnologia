# Projeto-Semana-Ciencia-Tecnologia

Criação de uma API REST de uma biblioteca!

## Passos

Após feito a configuração do inicial projeto, é hora de programar! Siga os passos abaixo de acordo com o deseronlar do curso.

### Passo 1: Criação da camada das entidades

Primeiramente, vamos criar as classes que iriam ser nossas entidades de banco, que ficaram na pasta entity. Também precisamos criar um enum(usado para um conter um grupo de constantes), que será usado como gênero no Autor. O enum ficará na pasta enumeration, dentro de entity. Nas entidades e enum, precisamos por algumas "anotações", que é uma forma de programação usada bastante em projeto Spring. Vamos usar as anotações __@Getter__, __@Setter__ e __@NoArgsConstructor__, que são anotações da lib Lombok, que servem respectvamente para criar os getters, setter e um construtor sem argumentos, isso tudo em tempo de compilação. Abaixo, segue as classes e o enum:

```java
@Getter
@Setter
@NoArgsConstructor
public class Livro {

    private String nome;

    private LocalDate publicacao;
    
    private String descricao;
}
```

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
@Getter
@Setter
@NoArgsConstructor
public class Autor {

    private String nome;

    private String sobrenome;

    private LocalDate dataNascimento;
    
    private String email;
    
    private Genero genero;
}
```

Para que essas classes sejam entidades de banco, precisamos anotar-lá com __@Entity__. Além disso, precisamos definir o id dessas classes/entidades, isso para garantir unicidade a nível de banco. Para isso, usamos a anotação __@Id__ em cima de um atributo de classe que deva ser o id, seu nome deve ser id, o tipo, tanto faz(usaremos Long)! Tendo o id, precisamos dizer como ele vai ser gerado, usamos a anotação __@GeneratedValue(strategy = GenerationType.IDENTITY)__, essa anotação quer dizer que a geração do id será feita pelo SGBD em uso. Logo, as classes ficaram da seguinte forma:

Um livro poderá ter uma descrição muito grande por exemplo, e sem anotação __@Length(max = 9999)__ no atributo descrição, teriamos um exceção que indica que estrapolamos o tamanho padrão da descrição no banco
```java
@Entity
...
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
@Entity
...
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
...
public class Livro {
    ...
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;
}
```
Ainda nessa parte de relacionamento, podemos ter referências dos Livros em um Autor, e isso só faz sentido no mundo orientado a objetos. Precisamos fazer isso pelo seguinte motivo: caso queiramos remover um Autor, os seus Livros precisam ser removidos. Fazemos isso colocando um List de Livro no Autor, e em cima desse atributo, colocamos a anotação __@OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)__ que indica que existe uma relação "um" para "muitos" entre Autor e Livro, e que essa relação já foi mapeada, nesse caso, foi mapeada no Livro sobre o atributo Autor. Sobre o cascade, ele indica que qualquer operação no Autor, essa operação sera refletida em Livro, que nesse caso é a operação de remoção. Segue o código atualizado:
```java
...
public class Autor {
    ...
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

### Passo 2: Criação da camada de interfaces de persistência

Após finalizar o passo 1, para podemos ter um CRUD dessas entidades criadas, precisamos criar uma interface para cada entidade, onde essas interfaces extenderam a interface generic _JpaRepository_, que é uma interface do Spring Data JPA que contêm assinaturas de métodos CRUD. Mas como isso funciona internamente???... Essas interfaces ficaram na pasta repository. Segue abaixo as interfaces:

```java
public interface LivroRepository extends JpaRepository<Livro, Long> {
}
```

```java
public interface AutorRepository extends JpaRepository<Autor, Long> {
}
```

### Passo 3: Criação da camada de serviço

Com a finalização do passo 2, precisamos criar as classes de serviços, que vão fica na pasta service. Essas classes são necessárias para ter as regras de negócio do produto, ter serviços propriamente ditos e conectar a camada de repository e resource. Neste primeiro momento, vamos criar apenas um método de recuperar todos os Livros e Autores. Para uma classes ser considerada como serviço, ela precisa ser anotada com __@Service__ que é uma anotação de esteriótipo do Spring. Para que podemos ter acesso a camada repository, precisamos injetar o repository no service, isso feito com a anotação __@Autowired__(atualmente é um mal uso usar essa anotação, existem outros meios para indicar a injeção, mas para um primeiro entendimento do assunto, essa anotação se faz necessária), usada em cima do atributo contendo o tipo do repository. Segue os códigos:

```java
@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }
}
```

```java
@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> findAll() {
        return autorRepository.findAll();
    }
}
```

### Passo 4: Criação da camada de endpoints

Agora vamos criar uma parte da nossa API, que seguira os padrões REST(estilo arquitetural para APIs com algumas restrições). Relembrando que as classes criadas aqui, ficaram no pacote resource. Vamos criar dois endpoints(links, URLs para algum contéudo diante de algum método HTTP), que retornaram todos os Livros e Autores cadastrados. Para isso, precisamos criar duas classes que representaram as APIs de Autor e Livro. Ambas as classes precisam ser anotadas com __@RestController__, e depois serem anotadas com __@RequestMapping("/endpoint")__. Essa última anotação indica a parte principal do endpoint, que é passada como String, e nosso caso é "/livros" e "/autores". Precisamos injetar os services necessários, isso para realizar alguma operação, como a recuperação de Livros. Feito isso, precisamos criar métodos que vão retornar todos os Livros e Autores, e esses métodos precisam ser anotados com __@GetMapping__, que indica que endpoint será acessado com o método HTTP GET, que é usado na operação de leitura, recuperação de dados. Sem essa anotação, o endpoint não irá funcionar. Segue abaixo os códigos:

```java
@RestController
@RequestMapping("/livros")
public class LivroResource {

    @Autowired
    private LivroService livroService;

    @GetMapping
    public List<Livro> findAll() {
        return livroService.findAll();
    }
}
```

```java
@RestController
@RequestMapping("/autores")
public class AutorResource {
    
    @Autowired
    private AutorService autorService;

    @GetMapping
    public List<Autor> findAll() {
        return autorService.findAll();
    }
}
```

Como você viu, aconteceu um erro ao tentar recuperar os Livros e Autores, qual o motivo? Quando recuperamos um Livro, recuperamos todos os seus atributos, inclusive o Autor, e o Autor tem seus atributos e contêm Livros, como pode percerber temos um problema de referência cíclcica, e resolvemos isso com a anotação __@JsonIgnore__. Vamos por essa anotação em cima do atributo Autor em Livro, e em cima do atributo Livros em Autor. Segue os códigos:

```java
...
public class Livro {
    ...
    @JsonIgnore
    private Autor autor;
}
```

```java
...
public class Autor {
    ...
    @JsonIgnore
    private List<Livro> livros = new ArrayList<>();
}
```

### Passo 5: Recuperação de Livro e Autor

Agora, vamos recuperar Livro e Autor por um ID, um identificador único. Para isso, vamos precisar editar os services de Autor e Livro, adicionando os métodos de recuperação. E nos resouces, vamos adicionar os endpoints para essa funcionalidade. Segue os códigos dos service:

Caso não exista um Livro ou Autor com ID passado pelo usuário, será jogada uma RuntimeException. Tratamento de exceções será abordado mais na frente
```java
...
public class LivroService {
    ...
    public Livro find(Long id) {
        return livroRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
```

```java
...
public class AutorService {
    ...
    public Autor find(Long id) {
        return autorRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
```

Agora vamos criar os endpoints. Precisamos anotar os métodos dos endpoints com __@GetMapping("/{id}")__, isso indica que vamos usar o método HTTP GET, e que no endpoint será passado o id do Autor ou Livro, isso é representado por "/{id}". O {id} representa qualquer valor numérico. Os endpoints completos desse métodos seram /livros/{id} e /autores/{id}. A anotação __@PathVariable__ irá pegar o id passado no link e por no parâmetro id do método.

```java
...
public class LivroResource {
    ...
    @GetMapping("/{id}")
    public Livro find(@PathVariable Long id) {
        return livroService.find(id);
    }
}
```

```java
...
public class AutorResource {
    ...
    @GetMapping("/{id}")
    public Autor find(@PathVariable Long id) {
        return autorService.find(id);
    }
}
```

### Passo 5: Inserção de Livro e Autor

Agora vamos aprender como inserir um Livro e Autor. Primeiramente, só podemos inserir um Livro caso o Autor desse Livro exista, isso na lógica do software é claro, mas na prática, por enquanto não o mesmo. Precisamos criar DTOs, DTOs vão representar os dados passado pelo usuário, esses dados inicialmente são passados como JSON, mas seram convertidos em objetos assim que o back receber a requisição do usuário. Para isso, precisamos criar a pasta dto e dentro dela, criar o LivroDTO e AutorDTO. Segue os códidos:

```java
@Getter
@Setter
@NoArgsConstructor
public class LivroDTO {

    private String nome;

    private LocalDate publicacao;

    private String descricao;

    private Autor autor;
}
```

```java
@Getter
@Setter
@NoArgsConstructor
public class AutorDTO {
    
    private String nome;
    
    private String sobrenome;

    private LocalDate dataNascimento;
    
    private String email;

    private Integer genero;
}
```

Antes do momento de persistência, precisamos "converter" os DTOs nas entidades. Para fazer isso, basta criarmos novos construtores nas entidades, como mostrado abaixo:

```java
...
public class Livro {
    ...
    public Livro(LivroDTO dto) {
        this.nome = dto.getNome();
        this.publicacao = dto.getPublicacao();
        this.descricao = dto.getDescricao();
        this.autor = dto.getAutor();
    }
}
```

```java
...
public class Autor {
    ...
    public Autor(AutorDTO dto) {
        this.nome = dto.getNome();
        this.sobrenome = dto.getSobrenome();
        this.dataNascimento = dto.getDataNascimento();
        this.email = dto.getEmail();
        this.genero = Genero.toEnum(dto.getGenero());
    }
}
```

Após isso, vamos criar os métodos nos services para a persistência:

Aqui precisamos recuperar o Autor do banco com o seu service, qual o motivo? No LivroDTO, o objeto Autor possuí apenas o Id, e para que o Spring Data persista nosso objeto da maneira correta, precisamos recuperar o Autor com todos os seus atributos, e setar no Livro antes do seu salvamento. Isso faz garantir o relacionamento entre Livro e Autor.
```java
...
public class LivroService {
    
    @Autowired
    private AutorService autorService;
    
    ...
    public Livro insert(LivroDTO dto) {
        Livro livro = new Livro(dto);
        livro.setId(null);
        livro.setAutor(autorService.find(livro.getAutor().getId()));
        return livroRepository.save(livro);
    }
}
```

```java
...
public class AutorService {
    ...
    public Autor insert(AutorDTO dto) {
        Autor autor = new Autor(dto);
        autor.setId(null);
        return autorRepository.save(autor);
    }
}
```

Nos resources, usaremos o método HTTP POST com a anotação __@PostMapping__, que indica que vamos inserir um novo dado no back. A anotação __@ResponseStatus(HttpStatus.CREATED)__ indica o status HTTP do retorno da requisição, que nesse caso é 201, que mostra que algo foi criado. O retorno da requisição não possuí corpo, mas possuí o status como falado e, um endpoint de acesso do novo Livro ou Autor adicionado, esse endpoint se encontra no cabeçalho do retorno. A anotação __@RequesBody__ "converterar" o JSON no DTO correspondente. Logo, teremos:

```java
...
public class LivroResource {
    ...
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insert(@RequestBody LivroDTO dto, HttpServletResponse response) {
        Livro livro = livroService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(livro.getId()).toUri();
        response.addHeader("location", uri.toString());
    }
}
```

```java
...
public class AutorResource {
    ...
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insert(@RequestBody AutorDTO dto, HttpServletResponse response) {
        Autor autor = autorService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(autor.getId()).toUri();
        response.addHeader("location", uri.toString());
    }
}
```

### Passo 6: Remoção de Livro e Autor

Agora vamos implementar a remoção de Livro e Autor. Nos services, antes de fazer a remoção, precisamos saber se existe algum Livro ou Autor com o ID passado, caso não exista, será lançada uma exceção.

```java
...
public class LivroService {
    ...
    public void delete(Long id) {
        find(id);
        livroRepository.deleteById(id);
    }
}
```

```java
...
public class AutorService {
    ...
    public void delete(Long id) {
        find(id);
        autorRepository.deleteById(id);
    }
}
```

Nos resources, precisamos criar métodos com a anotação __@DeleteMapping("/{id}")__ que é um verbo HTTP que indica que algo vai ser removido. o __@ResponseStatus(HttpStatus.NO_CONTENT)__ inidica que a resposta do back conterá o status No Content 204, ou seja, nada será retornado além das informação de headers.

```java
...
public class LivroResource {
    ...
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        livroService.delete(id);
    }
}
```

```java
...
public class AutorResource {
    ...
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        autorService.delete(id);
    }
}
```

### Passo 7: Atualização de Livro e Autor

Nesse passo, vamos aprender a como atualizar um Livro ou Autor. Antes de começar, precisamos saber de uma característica falha dessa forma de atualização. Caso queiramos atualizar um Livro por exemplo, precisamos ter em um JSON contendo todas a informações desse Livro, e atualizar a informação de desejo e persistir, sem isso, o Livro perderá algumas informações. Na atualização, vamos precisar dos DTOs como na inserção que foi feita anteriormente. Na camada de service, precisamos fazer:

```java
...
public class LivroService {
    ...
    public void update(LivroDTO dto, Long id) {
        Livro livro = new Livro(dto);
        livro.setId(id);
        livroRepository.save(livro);
    }
}
```

```java
...
public class AutorService {
    ...
    public void update(AutorDTO dto, Long id) {
        Autor autor = new Autor(dto);
        autor.setId(id);
        autorRepository.save(autor);
    }
}
```

No resource, precisamos criar métodos com a anotação __@PutMapping ("/{id}")__, que indica uma operação de atualização, aqui usamos novamente a anotação __@ResponseStatus(HttpStatus.NO_CONTENT)__ que indica o status de resposta do servirdor, que não conterá nada.

```java
...
public class LivroResource {
    ...
    @PutMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody LivroDTO dto, @PathVariable Long id) {
        livroService.update(dto, id);
    }
}
```

```java
...
public class AutorResource {
    ...
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody AutorDTO dto, @PathVariable Long id) {
        autorService.update(dto, id);
    }
}
```

### Passo 8: Busca de Livros por ID de Autor

Basicamente terminamos o CRUD de Livro e Autor, mas, ficou faltando uma coisa, a buscas de Livros por ID de algum Autor. Vamos começar pelo repository do Livro. O JpaRepository como comentando anteriormente, faz mágias mirabolantes com acesso ao banco, mas, existe uma outra coisa legal. Seguindo um certo padrão, podemos assinar métodos em uma interface que extende o JpaRepository, e esse método virar uma query. Veja abaixo:

Essa assinatura "findByAutor" criará uma query onde será buscado Livros, e o seu retorno será em um List de Livro
```java
public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByAutor(Autor autor);
}
```

No service, precisamos buscar o Autor no banco com o ID passado, e fazer a busca usando esse objeto. Precisamos fazer isso porque o hibernate abstrai o banco com JPQL, que é basicamente SQL com objetos.

```java
...
public class LivroService {
    ...
    public List<Livro> findByAutor(Long id) {
        Autor autor = autorRepository.findById(id).orElseThrow(RuntimeException::new);
        return livroRepository.findByAutor(autor);
    }
}
```

No resource, vamos precisar atualizar o método findAll, nele vamos por um parâmetro de URL que será o "autor_id", fazemos isso com a anotação __@RequestParam(value = "autor_id", defaultValue = "")__. Temos:
```java
...
public class LivroResource {
    ...
    @GetMapping
    public List<Livro> findAll(@RequestParam(value = "autor_id", defaultValue = "") String id) {
        if (id.isEmpty()) return livroService.findAll();
        else return livroService.findByAutor(Long.valueOf(id));
    }
}
```

### Passo 9: Envio de email de confirmação de inserção de Livro

Uma funcionalidade dessa aplicação, é o envio de um email para o Autor dizendo que o Livro foi adicionado a biblioteca. Primeiramente, vamos usar o SMTP do Google para fazer o envio, e precisamos configurar isso no `applicatiom.yml` como mostrado abaixo.

```yaml
...
spring:
  mail:
    host: smtp.gmail.com
    username: ${default.sender}
    password: # Sua senha
    properties:
      mail:
        smtp:
          auth: true
          socketFactory:
            port: 465
            class: javax.net.ssl.SSLSocketFactory
            fallback: false
          starttls:
            enable: true
          ssl:
            enable: true
            
default:
  sender: # Sua senha
```

Vamos usar o email criado para testes, email: biblioteca.teste.2022@gmail.com e a senha: biblioteca1029@. Caso queria usar seu email pessoal, você precisa permitir o uso dele em aplicações que não sejam Google, e você faz isso por esses dois links https://www.google.com/settings/security/lesssecureapps e https://accounts.google.com/b/0/DisplayUnlockCaptcha.

Após isso, precisamos criar o EmailService, nele montar o corpo do email com SimpleMailMessage e enviar com JavaMailSender, que será injetado nesse service. No service, a anotação __@Value("${default.sender}")__ serve para pegar o default.sender definido no `applicatiom.yml`.

```java
@Service
public class EmailService {

    @Value("${default.sender}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String email, String message) {
        SimpleMailMessage mailMessage = prepareMailMessage(email, message);
        javaMailSender.send(mailMessage);
    }

    private SimpleMailMessage prepareMailMessage(String email, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom(sender);
        mailMessage.setSubject("Adição de novo livro");
        mailMessage.setSentDate(new Date(System.currentTimeMillis()));
        mailMessage.setText(text);
        return mailMessage;
    }
}
```

Precisamos criar o método em Autor que retorne seu nome completo. Nesse método, vamos por o __@JsonIgnore__ para fazer com que o nome completo não apareça nas consultas de Autor.

```java
...
public class Autor {
    ...
    @JsonIgnore
    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}
```

Por fim, vamos editar o método insert no LivroService. Nesse service, precisamos injetar o EmailService

```java
...
public class LivroService {
    ...
    @Autowired
    private EmailService emailService;
    ...
    public Livro insert(LivroDTO dto) {
        Livro livro = new Livro(dto);
        livro.setId(null);
        livro.setAutor(autorService.find(livro.getAutor().getId()));
        Autor autor = livro.getAutor();
        String text = prepareMailText(autor.getNomeCompleto(), livro.getNome());
        emailService.sendEmail(autor.getEmail(), text);
        return livroRepository.save(livro);
    }
}
```

Antes de enviar os emails, mudem os emails dos Autores no `import.sql`, de preferência, coloque seu email pessoal

## Extras

### Extra 1: Validação de requisições

### Extra 2: Documentação com Swagger UI