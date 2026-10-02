# Онлайн-аптека — практическая работа №8

Готовое Spring Boot приложение, объединяющее MVC, PostgreSQL/JPA, связи сущностей, валидацию, Spring Security с тремя ролями, REST API, Swagger/JWT и основной сценарий интернет-аптеки: каталог → корзина → оформление заказа.

## Запуск

Требуется Java 17+ и Docker (либо локальный PostgreSQL).

```bash
docker compose up -d
./mvnw spring-boot:run
```

Сайт: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html

При первом запуске создаются роли и демонстрационный администратор:

- логин: `admin`
- пароль: `Admin123!`

Сразу после проверки смените пароль или создайте нового администратора.

## Роли

- `ROLE_USER`: каталог, корзина и собственные заказы;
- `ROLE_PHARMACIST`: управление каталогом и заказами;
- `ROLE_ADMIN`: полный доступ и изменение ролей пользователей.

## JWT в Swagger

1. Выполнить `POST /api/auth/login`.
2. Скопировать `token`.
3. Нажать **Authorize** и вставить токен.

## Настройки окружения

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET` — не менее 32 байт
- `JWT_EXPIRATION` — срок действия в секундах

## Тесты

```bash
./mvnw test
```

Тесты используют H2 в режиме совместимости с PostgreSQL.
