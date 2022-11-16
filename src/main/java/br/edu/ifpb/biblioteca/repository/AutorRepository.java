package br.edu.ifpb.biblioteca.repository;

import br.edu.ifpb.biblioteca.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
}
