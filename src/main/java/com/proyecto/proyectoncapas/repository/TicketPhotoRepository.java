package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.TicketPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketPhotoRepository extends JpaRepository<TicketPhoto, Long> {
    List<TicketPhoto> findByTicketId(Long ticketId);
}