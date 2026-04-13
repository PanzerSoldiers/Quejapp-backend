package com.quejapp.quejapi.configuration;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import com.quejapp.quejapi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.filter.OncePerRequestFilter;

/** JwtAuthenticationFilter Este filtro se ejecuta en cada petición HTTP para verificar si el usuario envió
 un token JWT válido. Si el token es válido, el usuario se autentica automáticamente en el contexto de
 seguridad de Spring.*/

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Servicio encargado de manejar los tokens JWT
    private final JwtService jwtService;

    // Servicio que carga los datos del usuario desde la base de datos
    private final UserDetailsService userDetailsService;

    /** metodo principal del filtro. Se ejecuta una vez por cada solicitud HTTP. */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔥 EXCLUSIÓN DEL CHAT
        if (path.startsWith("/chat")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtiene el header Authorization de la petición
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        /** Si el header está vacío o no comienza con "Bearer ", significa que no hay
         token, por lo que la petición continúa sin autenticación. */

        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrae el token eliminando la palabra "Bearer "
        jwt = authHeader.substring(7);

        // Extrae el email del usuario almacenado dentro del token
        userEmail = jwtService.extractUserName(jwt);

        /** Si se obtuvo el usuario del token y todavía no hay autenticación en Spring Security,
         se procede a validar el token. */

        if (StringUtils.isNotEmpty(userEmail) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Se cargan los datos del usuario desde la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Se verifica si el token es válido
            if (jwtService.isTokenValid(jwt, userDetails)) {

                /** Se crea un objeto de autenticación con:
                    el usuario
                    sus roles o permisos */

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Se agregan detalles de la petición actual
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                /** Se guarda la autenticación en el contexto de seguridad.
                 Esto indica que el usuario ya está autenticado. */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continúa con el resto de filtros de Spring Security
        filterChain.doFilter(request, response);
    }
}