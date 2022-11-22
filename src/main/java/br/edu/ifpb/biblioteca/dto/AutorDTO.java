package br.edu.ifpb.biblioteca.dto;

import javax.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AutorDTO {

    @Length(min = 4, max = 10, message = "Informa um nome de no mínimo 4 letras e 10 letras")
    private String nome;

    @Length(min = 6, max = 12, message = "Informa um sobrenome de no mínimo 4 letras e 10 letras")
    private String sobrenome;

    private LocalDate dataNascimento;

    @Email(message = "Informa um email válido")
    private String email;

    private Integer genero;
}
