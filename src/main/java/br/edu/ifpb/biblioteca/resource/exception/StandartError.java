package br.edu.ifpb.biblioteca.resource.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StandartError {

    private Integer status;

    private String message;
    
    private Long time;
}
