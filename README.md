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

8.Eighth day task
1. Entity-DTO Conversion Rules
a. Basic Guidelines

Separate layers: Entities represent the database, DTOs represent API payloads. Never expose JPA entities directly in responses.

Immutable DTOs: Prefer immutable DTOs (final fields, constructors) for thread-safety.

Explicit mapping: Map only the fields needed; avoid automatic full-copy to prevent leaking sensitive data.

Custom field transformations: If fields have different types (e.g., LocalDate → String), handle them explicitly.

b. Using MapStruct

MapStruct
 is a compile-time mapper generator, which is fast and avoids reflection.

Basic Mapper Example
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(UserEntity entity);
    UserEntity toEntity(UserDTO dto);
}

componentModel = "spring" allows Spring to inject the mapper.

MapStruct automatically maps fields with the same name.

Deep Mapping (Nested Objects)
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user", target = "userDto")
    @Mapping(source = "items", target = "itemsDto")
    OrderDTO toDto(OrderEntity order);

    @Mapping(source = "userDto", target = "user")
    @Mapping(source = "itemsDto", target = "items")
    OrderEntity toEntity(OrderDTO orderDto);

    List<ItemDTO> toItemDtoList(List<ItemEntity> items);
    List<ItemEntity> toItemEntityList(List<ItemDTO> itemsDto);
}

Nested objects (User, Item) are mapped recursively.

Use @Mapping to handle fields with different names or types.

Advanced: Custom Type Conversion
default String mapDate(LocalDate date) {
    return date != null ? date.format(DateTimeFormatter.ISO_DATE) : null;
}
2. Nested DTO Validation

Spring’s validation annotations work with nested objects using @Valid.

public class OrderDTO {
    @NotNull
    private Long id;

    @Valid
    private UserDTO user;

    @Valid
    @NotEmpty
    private List<ItemDTO> items;
}

public class ItemDTO {
    @NotNull
    private String name;

    @Min(1)
    private Integer quantity;
}

@Valid ensures that nested DTOs and lists are validated.

Combine with @RequestBody in controllers:

@PostMapping("/orders")
public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDto) {
    ...
}
3. Avoiding Cyclic Dependencies in Response Models

Cyclic dependencies usually occur when entities reference each other (e.g., User → Orders → User).

Strategies:

Use DTOs for response (never expose bidirectional entities)

Break cycles with selective mapping

public class UserDTO {
    private Long id;
    private String name;
    // Do NOT include orders here to avoid cycle
}

public class OrderDTO {
    private Long id;
    private String description;
    private UserDTO user; // Include user info but without orders
}

MapStruct context to prevent recursion

@Context
CycleAvoidingMappingContext context;

Advanced MapStruct setup can avoid infinite recursion in deep object graphs.

Jackson annotations (for serialization only)

@JsonManagedReference
private User user;

@JsonBackReference
private List<Order> orders;

This only works for JSON serialization, not DTO mapping.

9.Nineth day task
1. Normalization & Denormalization
Normalization

Normalization is the process of organizing database tables to reduce redundancy and improve data integrity.

Goals

Eliminate duplicate data

Ensure logical data storage

Maintain consistency

Common Normal Forms

1NF (First Normal Form)

No repeating groups

Each column has atomic values

2NF (Second Normal Form)

Must be in 1NF

Remove partial dependency on composite keys

3NF (Third Normal Form)

Must be in 2NF

Remove transitive dependency

Example (Not Normalized)
OrderID	CustomerName	Product	Price
1	Rahul	Phone	20000
1	Rahul	Charger	500
Normalized Tables

Customers

CustomerID	Name
1	Rahul

Orders

OrderID	CustomerID
1	1

OrderItems

| OrderID | Product | Price |

Denormalization

Denormalization intentionally adds redundancy to improve read performance.

Example:

| OrderID | CustomerName | Product | Price |

Advantages

Faster reads

Fewer joins

Disadvantages

Data duplication

Update anomalies

Used in:

analytics

reporting systems

large-scale applications

2. Indexing Strategy

Indexes improve query performance by avoiding full table scans.

BTREE Index

Most common index type used in databases like MySQL.

Structure: Balanced Tree

Best for:

equality search (=)

range queries (> < BETWEEN)

sorting (ORDER BY)

Example:

CREATE INDEX idx_user_email
ON users(email);
Composite Index

Index built on multiple columns.

Example:

CREATE INDEX idx_user_name_age
ON users(name, age);

Works best when query follows leftmost prefix rule.

Good query:

SELECT * FROM users WHERE name='Rahul';

Also good:

SELECT * FROM users WHERE name='Rahul' AND age=25;

Bad usage:

SELECT * FROM users WHERE age=25;
3. Joins & Their Performance Costs

Joins combine rows from multiple tables.

Types of Joins
Join Type	Description
INNER JOIN	matching records only
LEFT JOIN	all left table rows
RIGHT JOIN	all right table rows
FULL JOIN	all rows from both

Example:

SELECT orders.id, customers.name
FROM orders
JOIN customers
ON orders.customer_id = customers.id;
Performance Costs

Joins can be expensive because they may require:

Nested Loop Join

Hash Join

Merge Join

Performance depends on:

indexes

table size

join condition

query plan

Large joins without indexes → full table scans

4. Query Execution Plan (EXPLAIN)

EXPLAIN shows how the database will execute a query.

Example:

EXPLAIN SELECT * FROM users WHERE email='test@mail.com';

It reveals:

index usage

join method

rows scanned

cost estimate

Example output fields:

Column	Meaning
type	join type
key	index used
rows	estimated rows scanned
Extra	additional operations

Important performance indicators:

ALL → full table scan (bad)

ref / range → indexed lookup (good)

5. ACID Properties

ACID ensures reliable database transactions.

A — Atomicity

Transaction is all or nothing.

Example:
Money transfer:

debit

credit

If one fails → rollback.

C — Consistency

Database moves from one valid state to another.

Constraints remain valid:

primary keys

foreign keys

checks

I — Isolation

Transactions should not interfere with each other.

Isolation levels:

Read Uncommitted

Read Committed

Repeatable Read

Serializable

Higher isolation → more consistency but slower performance.

D — Durability

Once transaction commits → data is permanently stored.

Even after:

system crash

power failure


