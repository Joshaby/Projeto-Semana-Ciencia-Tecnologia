package br.edu.ifpb.biblioteca.entity;

import br.edu.ifpb.biblioteca.dto.AutorDTO;
import br.edu.ifpb.biblioteca.entity.enumeration.Genero;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    public Autor(AutorDTO dto) {
        this.nome = dto.getNome();
        this.sobrenome = dto.getSobrenome();
        this.dataNascimento = dto.getDataNascimento();
        this.email = dto.getEmail();
        this.genero = Genero.toEnum(dto.getGenero());
    }

    @JsonIgnore
    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}
