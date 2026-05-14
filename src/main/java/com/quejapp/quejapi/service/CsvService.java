package com.quejapp.quejapi.service;

import com.opencsv.CSVWriter;
import com.quejapp.quejapi.model.User;
import com.quejapp.quejapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Service
public class CsvService {

    @Autowired
    private UserRepository usuarioRepository;

    private final String PATH = "storage/csv/";

    // CREAR CSV VACÍO
    public String crearCsv(
            String fileName,
            List<String> headers
    ) {

        try {

            File directory = new File(PATH);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fullPath =
                    PATH + fileName + ".csv";

            CSVWriter writer =
                    new CSVWriter(
                            new FileWriter(fullPath)
                    );

            writer.writeNext(
                    headers.toArray(new String[0])
            );

            writer.close();

            return "CSV creado correctamente";

        } catch (Exception e) {

            e.printStackTrace();

            return "Error creando CSV";
        }
    }

    // EXPORTAR USUARIOS REALES
    public String exportarUsuariosCSV() {

        try {

            List<User> usuarios =
                    usuarioRepository.findAll();

            File directory = new File(PATH);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fullPath =
                    PATH + "usuarios.csv";

            CSVWriter writer =
                    new CSVWriter(
                            new FileWriter(fullPath)
                    );

            // ENCABEZADOS
            String[] headers = {
                    "Nombre",
                    "Apellido",
                    "Correo",
                    "Rol"
            };

            writer.writeNext(headers);

            // DATOS
            for(User usuario : usuarios) {

                String[] data = {
                        usuario.getFirstname(),
                        usuario.getLastname(),
                        usuario.getEmail(),
                        usuario.getRole().name()
                };

                writer.writeNext(data);
            }

            writer.close();

            return "CSV usuarios generado correctamente";

        } catch (Exception e) {

            e.printStackTrace();

            return "Error exportando usuarios";
        }
    }
}