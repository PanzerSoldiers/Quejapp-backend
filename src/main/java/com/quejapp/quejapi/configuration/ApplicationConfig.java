package com.quejapp.quejapi.configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.quejapp.quejapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Clase de configuración de seguridad encargada de definir
 * los componentes necesarios para la autenticación de usuarios.
 * Aquí se configuran los Beans que permiten:
 * 1. Buscar usuarios en la base de datos
 * 2. Validar sus credenciales
 * 3. Gestionar el proceso de autenticación
 * 4. Encriptar las contraseñas
 */

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    /** Repositorio que permite acceder a los usuarios almacenados en la base de datos. Y @RequiredArgsConstructor
     crea automáticamente un constructor para inyectar esta dependencia. */

    private final UserRepository userRepository;

    /**
     UserDetailsService: busca el usuario en la base de datos por email usando UserRepository.
     PasswordEncoder: encripta las contraseñas con BCrypt. (algoritmo de encriptación de contraseñas.)
     DaoAuthenticationProvider: valida las credenciales usando el usuario y el password encriptado.
     AuthenticationManager: gestiona todo el proceso de autenticación cuando un usuario inicia sesión.
     */

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    /**
     PasswordEncoder Define el algoritmo utilizado para encriptar contraseñas. Se utiliza BCrypt porque es uno de los
     algoritmos más seguros para almacenar passwords.*/

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    /**AuthenticationProvider: Este componente es el encargado de validar las credenciales del
     usuario durante el login. Utilizando 2 elementos

     UserDetailsService -> para obtener el usuario desde la base de datos
     PasswordEncoder -> para comparar la contraseña ingresada con la contraseña encriptada
     almacenada en la base de datos*/

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Servicio que carga el usuario desde la base de datos
        authProvider.setUserDetailsService(userDetailsService);

        // Codificador utilizado para verificar la contraseña
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }


    /** AuthenticationManager Es el componente principal que coordina todo el proceso
     de autenticación en Spring Security. Recibe las credenciales del usuario (email y contraseña)
     y se encarga de la validación al AuthenticationProvider.*/

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}