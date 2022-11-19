package br.edu.ifpb.biblioteca.repository;

import br.edu.ifpb.biblioteca.entity.Autor;
import br.edu.ifpb.biblioteca.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByAutor(Autor autor);
}
