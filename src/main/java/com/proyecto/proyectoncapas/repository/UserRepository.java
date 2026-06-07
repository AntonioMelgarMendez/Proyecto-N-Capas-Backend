package com.proyecto.proyectoncapas.repository;

import com.proyecto.proyectoncapas.entities.Reservation;
import com.proyecto.proyectoncapas.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}