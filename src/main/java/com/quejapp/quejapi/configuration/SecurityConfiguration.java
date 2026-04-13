package com.quejapp.quejapi.configuration;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import com.quejapp.quejapi.model.Role;
import lombok.RequiredArgsConstructor;

/** SecurityConfiguration Clase principal donde se define la seguridad de la aplicación.

 Aquí se configuran:
 Qué rutas requieren autenticación
 Qué roles pueden acceder
 El uso de JWT
 La política de sesiones
 La configuración CORS */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    // Filtro que valida el token JWT en cada petición
    private final JwtAuthenticationFilter jwtAuthFilter;

    // Componente que valida las credenciales del usuario
    private final AuthenticationProvider authenticationProvider;

    /** SecurityFilterChain Define cómo Spring Security debe proteger la aplicación. */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // Se desactiva CSRF porque la API usa JWT y no sesiones
                .csrf(AbstractHttpConfigurer::disable)

                // Habilita CORS para permitir comunicación con el frontend
                .cors(Customizer.withDefaults())

                // Configuración de permisos para los endpoints
                .authorizeHttpRequests(request -> request

                        // Permite solicitudes OPTIONS (usadas por CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Endpoints públicos como login o registro
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoints públicos para modelos Weka
                        .requestMatchers("/api/weka/**").permitAll()

                        // Endpoints públicos de estadísticas
                        .requestMatchers("/api/stats/**").permitAll()

                        // Endpoint público para el chatbot.
                        .requestMatchers("/chat/**").permitAll()

                        // Solo accesible para administradores
                        .requestMatchers("/api/admin/**")
                        .hasAuthority(Role.ADMINISTRATOR.name())

                        // Acceso para usuarios y administradores
                        .requestMatchers("/api/user/**")
                        .hasAnyAuthority(Role.USER.name(), Role.ADMINISTRATOR.name())

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().permitAll())

                /** La aplicación usa JWT, por lo que no se guardan sesiones.
                 Cada petición debe enviar su token. */
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Proveedor que valida las credenciales del usuario
                .authenticationProvider(authenticationProvider)

                /** Se agrega el filtro JWT antes del filtro de autenticación
                 de Spring para validar el token en cada petición. */

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Configuración CORS: Permite que el frontend pueda hacer peticiones a la API. */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (frontend)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "https://quejapp-client.vercel.app"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos en las solicitudes
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"));

        // Permite enviar credenciales
        configuration.setAllowCredentials(true);

        // Tiempo de caché de la configuración CORS
        configuration.setMaxAge(3600L);

        // Aplica esta configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}