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

20.20 th day task

1️. Refactoring Strategies (SOLID, DRY, KISS)

Purpose: Make your code easier to maintain, extend, and test.

SOLID principles:
S – Single Responsibility: Each class should have one reason to change.
O – Open/Closed: Classes should be open for extension, closed for modification.
L – Liskov Substitution: Subclasses should replace base classes without breaking behavior.
I – Interface Segregation: Use small, focused interfaces instead of one big interface.
D – Dependency Inversion: Depend on abstractions, not concrete classes.

Example in Spring Boot:

// BAD: Single class doing everything
public class OrderService {
    public void fetchOrders() { ... }
    public void saveOrders() { ... }
    public void notifyUser() { ... }
}

// GOOD: Separate responsibilities
@Service
public class OrderFetcher { ... }

@Service
public class OrderSaver { ... }

@Service
public class NotificationService { ... }
DRY (Don’t Repeat Yourself):
Avoid duplicate code. Extract reusable methods or utilities.
KISS (Keep It Simple, Stupid):
Avoid overengineering. Simple and readable code is better than clever but confusing code.
2️. SQL Performance Tuning for Slow Queries

Purpose: Make database operations faster.

Use indexes on frequently queried columns:
CREATE INDEX idx_orders_user_id ON orders(user_id);
Avoid SELECT *; specify only the columns you need:
SELECT id, amount FROM orders WHERE user_id = ?;
Use query analysis tools:
MySQL: EXPLAIN SELECT ...
Workbench → Query Performance Analyzer
Optimize joins, batch inserts, and caching.
3️. Logging Improvements (Structured Logging)

Purpose: Make logs easier to read, analyze, and monitor in production.

Use Logback or Log4j2 JSON format:
<encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
    <providers>
        <timestamp />
        <loggerName />
        <logLevel />
        <threadName />
        <message />
    </providers>
</encoder>
Include request IDs, user IDs, and service names.
Avoid printing sensitive data in logs.
4️. Exception Consistency & Documentation

Purpose: Make error handling predictable and easy to debug.

Create custom exceptions:
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) { super(message); }
}
Use @ControllerAdvice for global exception handling:
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
Document possible exceptions in your API or README.
5️.API Change Management

Purpose: Safely change APIs without breaking clients.

Use versioning:
GET /api/v1/orders
GET /api/v2/orders
Maintain backward compatibility.
Communicate changes in README or API documentation (Swagger/OpenAPI).
6️. Preparing Project for Production

Checklist:

 Externalize configuration (application.properties → environment variables)
 Enable connection pooling (HikariCP)
 Enable structured logging & monitoring
 Secure endpoints (Spring Security, JWT, HTTPS)
 Validate DB migrations & seed data
 Build fat jar for deployment:
mvn clean package









If you want, I can also create a diagram showing Docker layers, multi-stage builds, and volumes for a visual understanding — it makes these concepts click instantly.

Do you want me to make that diagram?
