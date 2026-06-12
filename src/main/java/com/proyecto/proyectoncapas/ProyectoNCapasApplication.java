package com.proyecto.proyectoncapas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProyectoNCapasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoNCapasApplication.class, args);
    }

}
