package com.quejapp.quejapi.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PendingActionService {

    private final Map<String, Runnable> accionesPendientes =
            new ConcurrentHashMap<>();

    public void guardarAccion(
            String usuario,
            Runnable accion
    ) {
        accionesPendientes.put(usuario, accion);
    }

    public boolean tieneAccion(String usuario) {
        return accionesPendientes.containsKey(usuario);
    }

    public String confirmar(String usuario) {

        Runnable accion =
                accionesPendientes.get(usuario);

        if(accion == null) {
            return "No hay acciones pendientes.";
        }

        accion.run();

        accionesPendientes.remove(usuario);

        return "Acción ejecutada correctamente.";
    }
}