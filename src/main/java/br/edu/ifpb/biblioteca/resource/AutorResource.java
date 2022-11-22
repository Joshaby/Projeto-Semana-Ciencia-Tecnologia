package br.edu.ifpb.biblioteca.resource;

import br.edu.ifpb.biblioteca.dto.AutorDTO;
import br.edu.ifpb.biblioteca.entity.Autor;
import br.edu.ifpb.biblioteca.service.AutorService;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/autores")
@RequiredArgsConstructor
public class AutorResource {

    private final AutorService autorService;

    @GetMapping("/{id}")
    public Autor find(@PathVariable Long id) {
        return autorService.find(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        autorService.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insert(@Valid @RequestBody AutorDTO dto, HttpServletResponse response) {
        Autor autor = autorService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(autor.getId()).toUri();
        response.addHeader("location", uri.toString());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody AutorDTO dto, @PathVariable Long id) {
        autorService.update(dto, id);
    }

    @GetMapping
    public List<Autor> findAll() {
        return autorService.findAll();
    }
}
