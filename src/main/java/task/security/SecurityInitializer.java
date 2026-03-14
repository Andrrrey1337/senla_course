package task.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

// нужен для перехвата запроса и отправки их в SecurityConfig
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer { }