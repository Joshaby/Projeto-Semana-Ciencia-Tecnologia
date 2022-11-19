package br.edu.ifpb.biblioteca.resource;

import br.edu.ifpb.biblioteca.dto.LivroDTO;
import br.edu.ifpb.biblioteca.entity.Livro;
import br.edu.ifpb.biblioteca.service.LivroService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroResource {

    private final LivroService livroService;

    @GetMapping("/{id}")
    public Livro find(@PathVariable Long id) {
        return livroService.find(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        livroService.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insert(@RequestBody LivroDTO dto, HttpServletResponse response) {
        Livro livro = livroService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(livro.getId()).toUri();
        response.addHeader("location", uri.toString());
    }

    @PutMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody LivroDTO dto, @PathVariable Long id) {
        livroService.update(dto, id);
    }

    @GetMapping
    public List<Livro> findAll(@RequestParam(value = "autor_id", defaultValue = "") String id) {
        if (id.isEmpty()) return livroService.findAll();
        else return livroService.findByAutor(Long.valueOf(id));
    }
}
