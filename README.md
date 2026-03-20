18. 18 th day task
    1. Docker Image Layering & Caching

Layering:
Docker images are built in layers, each layer representing a step in the Dockerfile. Layers are immutable, so changes in one layer can invalidate all layers above it.

Caching:
Docker uses a build cache to avoid rebuilding unchanged layers. Example:

FROM python:3.11
WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
CMD ["python", "app.py"]

If requirements.txt hasn’t changed, Docker reuses the cached pip install layer.

Modifying any line in the Dockerfile after a layer invalidates the cache for subsequent layers.

Best practices:

Put frequently changing commands (like COPY . .) after commands that rarely change.

Combine commands with && to reduce layers.

2. Multi-Stage Docker Build Optimization

Purpose: Reduce final image size by separating build-time and runtime dependencies.

# Build stage
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

Only the necessary runtime files are included in the final image.

Reduces attack surface and improves deployment efficiency.

3. Docker Networking Modes

Docker containers can communicate in different ways:

Bridge (default) – Containers on the same host can communicate via internal network.

Host – Container shares the host network stack. Useful for low-latency apps.

Overlay – Multi-host networking, often used with Docker Swarm/Kubernetes.

Macvlan – Assigns a unique MAC address to the container, making it appear as a physical device on the network.

Example:

docker network create --driver bridge mynet
docker run --network=mynet mycontainer
4. Environment Variables & Secrets Security

Environment Variables: Passed via -e or --env-file. Easy but visible in docker inspect.

Secrets Management: For sensitive data (DB passwords, API keys), use:

Docker Secrets (Swarm)

Kubernetes Secrets

External secret managers like HashiCorp Vault

Example (Docker Secret):

echo "mypassword" | docker secret create db_password -
docker service create --name myapp --secret db_password myimage

Inside the container, secrets appear in /run/secrets/db_password, not as environment variables.

5. Volume Persistence for MySQL

Containers are ephemeral; data must persist via volumes.

docker volume create mysql_data
docker run -d \
  --name mysql \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -v mysql_data:/var/lib/mysql \
  mysql:8

Best practices:

Use named volumes (mysql_data) instead of host paths for portability.

Backup volumes regularly.

6. Container Orchestration Basics

Why orchestration: Manage multiple containers, scaling, networking, and high availability.

Docker Swarm:

Native to Docker.

Simple to set up (docker swarm init).

Handles load balancing, scaling, and secrets.

19. 19 th day task
    . Reverse Proxy Benefits (NGINX)

A reverse proxy sits between users and your backend services.

 Key Benefits:

Security
Hides internal services (e.g., your app runs on port 5000, but users only see port 80/443).

Load Balancing
Distributes traffic across multiple backend servers.

SSL Termination
Handles HTTPS so your app doesn’t need to.

Caching
Speeds up responses for static or repeated requests.

Routing
Directs requests:

/api → backend service

/ → frontend

Example:
Client → NGINX → Node.js / Python / Java app
2. SSL Termination using Let's Encrypt

SSL termination means HTTPS is handled at the proxy level.

 Why it matters:

Encrypts user data (HTTPS)

Improves trust (no browser warnings)

Required for modern apps (cookies, APIs, etc.)

How it works:

Use Certbot to get free certificates from Let’s Encrypt

Configure NGINX:

Port 80 → redirect to 443

Port 443 → uses SSL cert

Auto-renewal:

Let’s Encrypt certs expire every 90 days, so auto-renew via cron/systemd timer.

3. Firewall Rules & Port Hardening
Goal:

Expose only what’s necessary.

 Best Practices:

Allow:

22 → SSH (restrict to your IP ideally)

80 → HTTP

443 → HTTPS

Block everything else

 Tools:

Linux firewall: ufw, iptables, or firewalld

Extra Hardening:

Disable root login over SSH

Use SSH keys instead of passwords

Change default SSH port (optional)

Principle:

“Deny by default, allow only what’s required”

4. Using systemd to Manage Backend Service

systemd is used to run your app as a background service.

Why use systemd:

Auto-start on boot

Restart on failure

Centralized logging (journalctl)

Easy control (start/stop/restart)

📄 Example service file:
/etc/systemd/system/myapp.service

[Unit]
Description=My Backend App
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/var/www/app
ExecStart=/usr/bin/node server.js
Restart=always

[Install]
WantedBy=multi-user.target
🔧 Commands:
sudo systemctl daemon-reexec
sudo systemctl enable myapp
sudo systemctl start myapp
sudo systemctl status myapp
5. Zero Downtime Deployment Strategies

Goal: Deploy updates without users noticing interruptions.

Common Strategies:
1. Rolling Deployment

Update servers one by one

No full outage

2. Blue-Green Deployment

Two environments:

Blue (current)

Green (new)

Switch traffic instantly

3. Canary Deployment

Release to small % of users first

Monitor before full rollout

4. Process Reload (NGINX + app)

Restart app gracefully:

pm2 reload all

or systemd restart with minimal disruption

5. Load Balancer Approach

Remove instance → update → re-add

🔗 How Everything Fits Together

Typical production flow:

User (HTTPS)
   ↓
NGINX (Reverse Proxy + SSL Termination)
   ↓
Backend Service (systemd managed)
   ↓
Database







If you want, I can also create a diagram showing Docker layers, multi-stage builds, and volumes for a visual understanding — it makes these concepts click instantly.

Do you want me to make that diagram?
