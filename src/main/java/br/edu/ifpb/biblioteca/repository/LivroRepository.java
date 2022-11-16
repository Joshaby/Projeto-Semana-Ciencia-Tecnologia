package br.edu.ifpb.biblioteca.repository;

import br.edu.ifpb.biblioteca.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
