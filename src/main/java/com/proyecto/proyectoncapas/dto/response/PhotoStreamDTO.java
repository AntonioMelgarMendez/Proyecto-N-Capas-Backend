package com.proyecto.proyectoncapas.dto.response;

import java.io.InputStream;

public record PhotoStreamDTO(InputStream inputStream, String contentType, long contentLength) {}
