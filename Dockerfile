FROM eclipse-temurin:21-jre-alpine

# Crear un directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo .jar compilado por GitHub Actions desde la carpeta build/libs hacia el contenedor
COPY build/libs/*.jar app.jar

# Exponer el puerto estándar que espera Cloud Run
EXPOSE 8080

# Comando para arrancar la aplicación inyectando las variables de optimización de memoria
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]