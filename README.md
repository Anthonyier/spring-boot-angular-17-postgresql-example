# Spring Boot + Angular 17 + PostgreSQL CRUD example

Full-stack Angular 17 + Spring Boot + PostgreSQL CRUD Aplicacion Tutorial en el que:
- Cada Tutorial tiene id, title, description, published status.
- Podemos crear, obtener, actualizar, borrar Tutorials.
- Tambien podemos encontrar Tutorials por titulo.

![spring-boot-angular-17-postgresql-example-crud.png](spring-boot-angular-17-postgresql-example-crud.png)

Para mas detalle, porfavor visite:
> [Spring Boot + Angular 17 + PostgreSQL: CRUD example](https://www.bezkoder.com/spring-boot-angular-17-postgresql/)

Corra ambos Back-end & Front-end en un solo lugar:
> [Integrate Angular with Spring Boot Rest API](https://www.bezkoder.com/integrate-angular-spring-boot/)

Mas Practica:
> [Angular 17 + Spring Boot: File upload example](https://www.bezkoder.com/angular-17-spring-boot-file-upload/)

> [Angular 17 + Spring Boot: JWT Authentication and Authorization example](https://www.bezkoder.com/angular-17-spring-boot-jwt-auth/)

## Run Spring Boot application
```
mvn spring-boot:run
```
The Spring Boot Server will export API at port `8081`.

## Run Angular Client
```
npm install
ng serve --port 8081
```
Es necesario crear una base de datos llamada testdb, la aplicacion se encarga de crear la tabla