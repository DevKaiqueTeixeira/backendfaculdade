package com.cafe.projeto.repository;

import com.cafe.projeto.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
}
