Seventh day task

1. Richardson Maturity Model

The Richardson Maturity Model (RMM) is a way to evaluate how RESTful an API is. It has four levels:

Level 0 – The Swamp of POX: Single endpoint, usually just POSTs to /api. No real use of HTTP verbs.

Level 1 – Resources: Introduces multiple endpoints for different resources, e.g., /users, /orders.

Level 2 – HTTP Verbs: Uses HTTP methods properly (GET, POST, PUT, DELETE) for CRUD operations.

Level 3 – Hypermedia (HATEOAS): Responses include links to related resources for navigation.

Goal: Moving up the levels improves RESTfulness, clarity, and client-server decoupling.

2. Designing Idempotent Operations

Idempotency means performing an operation multiple times has the same effect as doing it once. This is crucial for reliability.

GET – Always idempotent (reading data doesn’t change state).

PUT – Idempotent (replaces a resource; repeated calls produce the same result).

DELETE – Idempotent (deleting a resource repeatedly has the same effect).

POST – Usually not idempotent (creates a new resource each time).

Best practice: Ensure PUT and DELETE can safely be retried in case of network errors.

3. Pagination with Spring Data

When returning large datasets, avoid sending everything at once. Use pagination:

Spring Data JPA provides Pageable and Page for easy pagination:

@GetMapping("/users")
public Page<User> getUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
}

Query parameters:

?page=0&size=20 – first 20 items.

?sort=name,asc – sort by name ascending.

Best practice: Include total pages, current page, and total elements in the response for client navigation.

4. Sorting and Filtering Patterns

Sorting: Use query parameters.

Example: /users?sort=lastName,asc&sort=age,desc

Filtering: Use query parameters for flexibility.

Example: /users?status=active&role=admin

Advanced patterns:

Use Specification pattern or Querydsl with Spring Data for complex dynamic filtering.

Support multiple filters and ranges: /orders?amountGt=100&amountLt=500&dateAfter=2026-01-01

5. API Versioning Strategies

Versioning avoids breaking clients when the API evolves:

URI versioning: /api/v1/users

Clear, simple, widely used.

Request parameter versioning: /users?version=1

Less obvious, can be hidden.

Header versioning: Accept: application/vnd.myapi.v1+json

Clean URL, allows content negotiation.

Media type versioning: Similar to header versioning, used in HATEOAS APIs.

Best practice: Prefer URI or header versioning for clarity and long-term maintainability.

6. Best Error Response Structure

A consistent error response improves client experience and debuggability. Use a structured format:

Example JSON:

{
  "timestamp": "2026-03-04T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "User with ID 123 not found",
  "path": "/users/123",
  "code": "USER_NOT_FOUND"
}


