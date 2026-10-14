# AI Job Trainer Backend

This folder contains the Spring Boot/Maven API and the retained Express/Mongoose API.

## Spring Boot API

Start MongoDB and set `JWT_SECRET` to a secret with at least 32 bytes. From this folder, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The API listens on port `8080`. Optional initial admin seeding uses `ADMIN_EMAIL` and `ADMIN_PASSWORD`.

## Express API

Configure `JWT_SECRET` (at least 32 bytes) and `MONGODB_URI` in this folder's ignored `.env` file, then run:

```powershell
npm install
npm run start
```

The Express API listens on port `5000` by default. Never commit `.env`, dependency directories, or Maven build output.

## Render configuration

Configure `JWT_SECRET` (at least 32 bytes), `MONGODB_URI`, `FRONTEND_URL`, and `BACKEND_URL` in the backend service's environment settings. `FRONTEND_URL` must contain allowed frontend origins, including scheme and without paths; multiple origins can be comma-separated. Configure the frontend service's `VITE_BACKEND_URL` to the backend origin, including scheme and without a trailing slash, then redeploy both services.
