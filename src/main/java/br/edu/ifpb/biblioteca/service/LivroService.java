package br.edu.ifpb.biblioteca.service;

import br.edu.ifpb.biblioteca.dto.LivroDTO;
import br.edu.ifpb.biblioteca.entity.Autor;
import br.edu.ifpb.biblioteca.entity.Livro;
import br.edu.ifpb.biblioteca.repository.AutorRepository;
import br.edu.ifpb.biblioteca.repository.LivroRepository;
import br.edu.ifpb.biblioteca.service.exception.AutorException;
import br.edu.ifpb.biblioteca.service.exception.LivroException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    private final AutorRepository autorRepository;

    private final AutorService autorService;

    private final EmailService emailService;

    public Livro find(Long id) {
        return livroRepository.findById(id).orElseThrow(
                () -> new LivroException(String.format("Não existe livro com Id %d.", id)));
    }

    public List<Livro> findByAutor(Long id) {
        Autor autor = autorRepository.findById(id).orElseThrow(
                () -> new AutorException(String.format("Não existe autor com Id %d", id)));
        return livroRepository.findByAutor(autor);
    }

    public void delete(Long id) {
        find(id);
        livroRepository.deleteById(id);
    }

    public Livro insert(LivroDTO dto) {
        Livro livro = new Livro(dto);
        livro.setId(null);
        livro.setAutor(autorService.find(livro.getAutor().getId()));
        Autor autor = livro.getAutor();
        String text = prepareMailText(autor.getNomeCompleto(), livro.getNome());
        //emailService.sendEmail(autor.getEmail(), text);
        return livroRepository.save(livro);
    }

    public void update(LivroDTO dto, Long id) {
        Livro livro = new Livro(dto);
        livro.setId(id);
        livroRepository.save(livro);
    }

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    private String prepareMailText(String autor, String livro) {
        return String.format("Olá %s! Informamos que o seu livro \"%s\" foi adicionado a biblioteca!", autor, livro);
    }
}
