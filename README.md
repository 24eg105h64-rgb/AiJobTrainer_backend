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

For the Express web service, configure `JWT_SECRET` (at least 32 bytes), `MONGODB_URI`, and `FRONTEND_URL` in Render's environment settings. `FRONTEND_URL` must be the deployed frontend origin, without a path; multiple origins can be comma-separated. Configure the frontend Render service's `VITE_API_BASE_URL` to this backend service URL ending in `/api`, then redeploy both services.
