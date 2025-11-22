# Excusas S.A. – Sistema de Gestión de Excusas
Segundo Parcial – Diseño de Sistemas – Escuela Da Vinci

## Integrantes
- Kevin Flores — GitHub: @usuario
- Lucas Simone — GitHub: @usuario
- Laura Yachelini — GitHub: @yachela



## Descripción del Proyecto
Excusas S.A. es un sistema desarrollado para gestionar excusas laborales siguiendo principios de Diseño Orientado a Objetos, SOLID y el patrón Chain of Responsibility.

El sistema permite registrar excusas, procesarlas a través de una cadena de encargados, generar prontuarios e interactuar mediante una API REST implementada con Spring Boot.

Este repositorio corresponde al Segundo Parcial de Diseño de Sistemas y sigue estrictamente la metodología TDD (Test-Driven Development).



## Metodología: TDD
El desarrollo del sistema debe realizarse siguiendo el ciclo TDD:

1. Escribir un test que falle (Rojo)
2. Implementar el mínimo código necesario (Verde)
3. Refactorizar manteniendo los tests en verde (Refactor)

Se requiere entregar:
- Tests unitarios del modelo (mínimo 10 por entidad)
- Tests de servicios (con uso de Mockito)
- Tests de repositorios (con H2 y profile test)
- Tests de integración de la API (MockMvc o similar)



## Tecnologías utilizadas
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- MySQL (desarrollo/producción)
- H2 (tests)
- Spring Boot Test (JUnit 5)
- Mockito



## Flujo de trabajo y ramas
Se utilizan las siguientes ramas:

- `main` – rama estable
- `dev` – rama de desarrollo
- `test` – rama para el trabajo inicial y TDD

Flujo de trabajo obligatorio:
Branch local → test → dev → Pull Request a main



## Milestones del proyecto
El desarrollo se organiza según los siguientes hitos:

1. Diseño del sistema
2. Implementación de la cadena de encargados
3. Lógica de emails y comunicación
4. Registro de prontuarios
5. Tests del sistema
6. Diagramas y documentación
7. Exposición del sistema mediante API REST


## Estructura del Proyecto (Spring Boot)

![img.png](img.png)



## Documentación
La documentación completa se encuentra en la Wiki del repositorio e incluye:

- Enunciado
- Diagrama UML del modelo
- Diagrama DER
- Arquitectura del sistema
- Justificación de los patrones de diseño 

--- 
# Pendientes equipo

## Fase 1: Persistencia y Modelado (JPA)
- [ ] Configurar `application.properties` 
- [ ] Mapear `Employee` como `@Entity`
- [ ] Mapear `Excuse` y `TypeExcuse` como `@Entity`
- [ ] Mapear `Prontuario` como `@Entity`
- [ ] Crear Repositorios (`ExcuseRepository`, `EmployeeRepository`)
- [ ] **TDD:** Tests de integración para Repositorios (H2)

## Fase 2: Lógica de Negocio (Service Layer)
- [ ] Crear `ExcuseService`
- [ ] Migrar lógica de construcción de cadena (`Builder`) al servicio
- [ ] Implementar método `registrarExcusa(ExcuseDTO)`
- [ ] **TDD:** Tests unitarios de `ExcuseService` con Mockito

## Fase 3: API REST (Controllers)
- [ ] Crear `EmployeeController` (GET / POST)
- [ ] Crear `ExcuseController` (POST /excusas - Chain Integration)
- [ ] Crear Endpoints de consulta (GET /excusas, GET /rechazadas)
- [ ] **TDD:** Tests de integración con `MockMvc` para cada endpoint

## Fase 4: Requisitos Avanzados
- [ ] Implementar filtros de búsqueda (Fecha, Legajo)
- [ ] Implementar `PUT /encargados/modo` (Cambio de estrategia en tiempo real)
- [ ] Implementar `DELETE /excusas` (Validación de fecha)

## Fase 5: Documentación y Entrega
- [ ] Generar Diagrama DER
- [ ] Actualizar Diagrama UML (Incluyendo capas MVC)
- [ ] Escribir README con justificación de patrones
- [ ] Verificar Coverage de Tests (>80%)

