package br.edu.ifpb.biblioteca.service;

import br.edu.ifpb.biblioteca.dto.AutorDTO;
import br.edu.ifpb.biblioteca.entity.Autor;
import br.edu.ifpb.biblioteca.repository.AutorRepository;
import br.edu.ifpb.biblioteca.service.exception.AutorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;

    public Autor find(Long id) {
        return autorRepository.findById(id).orElseThrow(
                () -> new AutorException(String.format("Não existe autor com Id %d.", id)));
    }

    public void delete(Long id) {
        find(id);
        autorRepository.deleteById(id);
    }

    public Autor insert(AutorDTO dto) {
        Autor autor = new Autor(dto);
        autor.setId(null);
        return autorRepository.save(autor);
    }

    public void update(AutorDTO dto, Long id) {
        Autor autor = new Autor(dto);
        autor.setId(id);
        autorRepository.save(autor);
    }

    public List<Autor> findAll() {
        return autorRepository.findAll();
    }
}
