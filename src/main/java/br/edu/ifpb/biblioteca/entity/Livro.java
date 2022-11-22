package br.edu.ifpb.biblioteca.entity;

import br.edu.ifpb.biblioteca.dto.LivroDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    @JsonIgnore
    private Autor autor;

    public Livro(LivroDTO dto) {
        this.nome = dto.getNome();
        this.publicacao = dto.getPublicacao();
        this.descricao = dto.getDescricao();
        this.autor = dto.getAutor();
    }
}
