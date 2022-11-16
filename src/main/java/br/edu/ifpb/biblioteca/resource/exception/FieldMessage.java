package br.edu.ifpb.biblioteca.resource.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldMessage {

    private String field;

    private String message;
}
