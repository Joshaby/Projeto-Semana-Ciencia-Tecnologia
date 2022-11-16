package br.edu.ifpb.biblioteca.dto;

import br.edu.ifpb.biblioteca.entity.Autor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LivroDTO {

    private String nome;

    private LocalDate publicacao;

    private String descricao;

    private Autor autor;
}
