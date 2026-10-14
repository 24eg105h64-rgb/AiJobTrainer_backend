# AI Job Trainer Backend

This folder contains the Spring Boot/Maven API and the Express API. Both use MySQL.

## Spring Boot API

Set `SPRING_DATASOURCE_URL` to the Aiven JDBC URL (`jdbc:mysql://<host>:<port>/<database>?sslMode=REQUIRED`), and set `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, and a `JWT_SECRET` of at least 32 bytes. From this folder, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The API listens on port `8080`. Optional initial admin seeding uses `ADMIN_EMAIL` and `ADMIN_PASSWORD`.

## Express API

Configure `JWT_SECRET` (at least 32 bytes), `DATABASE_URL` from Aiven, and optionally `MYSQL_CA_CERT` as the path to the downloaded Aiven CA certificate in this folder's ignored `.env` file, then run:

```powershell
npm install
npm run start
```

The Express API listens on port `5000` by default. Never commit `.env`, dependency directories, or Maven build output.

## Render configuration

For the Express service, configure `JWT_SECRET`, `DATABASE_URL`, `FRONTEND_URL`, and `BACKEND_URL` in Render. Use Aiven's MySQL connection URL with TLS enabled; `MYSQL_CA_CERT` may point to the downloaded Aiven CA certificate. For Spring, configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` using Aiven's JDBC connection details. `FRONTEND_URL` must contain allowed frontend origins, including scheme and without paths; multiple origins can be comma-separated. Configure the frontend service's `VITE_BACKEND_URL` to the backend origin, including scheme and without a trailing slash, then redeploy both services.
