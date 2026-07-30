# ── Stage 1: Build ─────────────────────────────
FROM node:22-alpine AS build

WORKDIR /app

# install dependencies
COPY package.json package-lock.json* ./
RUN npm install

# copy source
COPY . .

# build vite app (OUTPUT = dist/)
RUN npm run build


# ── Stage 2: Serve with Nginx ──────────────────
FROM nginx:1.27-alpine

# copy built files (VITE uses dist/)
COPY --from=build /app/dist /usr/share/nginx/html

# nginx config (for SPA + websockets)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]