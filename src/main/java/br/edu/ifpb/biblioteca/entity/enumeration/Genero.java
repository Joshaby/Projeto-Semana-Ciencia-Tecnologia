package br.edu.ifpb.biblioteca.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

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
        Optional<Genero> genero = Arrays.stream(values()).filter(g -> g.getTipo().equals(tipo)).findFirst();
        return genero.orElseThrow(RuntimeException::new);
    }
}
