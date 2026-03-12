1.Spring Security Filter Chain (Deep Architecture)

Spring Security works using a chain of servlet filters. Each filter has a specific responsibility, and requests flow through the chain before reaching the application.

Key Points:

Filters are configured in SecurityFilterChain.

Typical order for web authentication:

Filter	Purpose
ChannelProcessingFilter	Enforces HTTPS if configured.
SecurityContextPersistenceFilter	Loads SecurityContext from SecurityContextHolder at the start of the request, stores it at the end.
ConcurrentSessionFilter	Controls session concurrency.
LogoutFilter	Handles /logout requests.
UsernamePasswordAuthenticationFilter	Processes login forms (/login).
BasicAuthenticationFilter	Processes HTTP Basic Auth headers.
RequestCacheAwareFilter	Redirects after login.
ExceptionTranslationFilter	Handles AccessDeniedException and authentication exceptions.
FilterSecurityInterceptor	Protects URL patterns based on roles and permissions.

Flow:

Request enters Servlet Container → Filter Chain.

Filters inspect headers, session, and authentication.

If authentication is needed, UsernamePasswordAuthenticationFilter is triggered.

Once authenticated, SecurityContext is stored in SecurityContextHolder.

Request continues to your controllers.

The chain is highly extensible—you can insert custom filters at any point.

2. UsernamePasswordAuthenticationFilter Internals

Triggered by a login POST request, usually at /login.

Steps:

Extract username and password from request (request.getParameter()).

Create a UsernamePasswordAuthenticationToken (unauthenticated).

Pass token to AuthenticationManager (usually ProviderManager).

If authentication succeeds:

Stores authentication in SecurityContextHolder.

Calls successHandler (redirect or JSON response).

If fails, calls failureHandler.

Important fields:

authenticationManager → delegates authentication.

authenticationSuccessHandler → handles post-login success.

authenticationFailureHandler → handles login failure.

3. ProviderManager & AuthenticationProvider

ProviderManager is the central AuthenticationManager in Spring Security.

How it works:

It maintains a list of AuthenticationProviders.

When a token (e.g., UsernamePasswordAuthenticationToken) comes in:

Iterates over providers.

Calls authenticate() on each provider.

First provider to succeed returns an authenticated token.

If none succeed → throws AuthenticationException.

Common AuthenticationProviders:

DaoAuthenticationProvider → Uses UserDetailsService + password encoder.

LdapAuthenticationProvider → Authenticates against LDAP.

JwtAuthenticationProvider → For JWT-based authentication.

4. Custom UserDetailsService Implementation

UserDetailsService loads user data from any source (DB, API, LDAP).

Example:

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}

You can also return List<GrantedAuthority> for fine-grained permissions.

This integrates with DaoAuthenticationProvider.

5. SecurityContextHolder Strategies

Holds authentication info during the request lifecycle.

Modes:

Mode	Description
MODE_THREADLOCAL	Default. Stores context in ThreadLocal.
MODE_INHERITABLETHREADLOCAL	Context propagates to child threads.
MODE_GLOBAL	Single global context (rarely used).

Accessing authentication:

Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();

Important for REST APIs, background tasks, and async requests.

6. Password Hashing Algorithms

Spring Security supports multiple hashing algorithms.

Algorithm	Pros	Cons
BCrypt	Adaptive, widely used, salt built-in	Slower than plain hash
SCrypt	Resistant to hardware attacks (memory-hard)	Slightly more resource intensive
Argon2	Winner of Password Hashing Competition, highly secure	Requires recent JVM, slightly complex setup

Example in Spring Boot:

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
7. Role Hierarchy & Permissions

Spring Security supports hierarchical roles:

@Bean
public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_DOCTOR \n ROLE_DOCTOR > ROLE_PATIENT");
    return hierarchy;
}

Permissions can be mapped via @PreAuthorize or URL security:

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/users")
public List<User> getAllUsers() { ... }
8. Securing REST APIs vs MVC
Feature	REST API	MVC App
Session	Stateless (JWT recommended)	Stateful (HTTP session)
CSRF	Usually disabled for APIs	Enabled by default
Security Context	ThreadLocal via filters	ThreadLocal via filters
Access control	@PreAuthorize / JWT claims	@Secured / role-based URLs
Response	JSON	Redirect / View

Example: Stateless REST Config:

http
    .csrf().disable()
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    .authorizeHttpRequests()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
    .and()
    .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);



Access granted based on roles & permissions.

Filters continue to co
