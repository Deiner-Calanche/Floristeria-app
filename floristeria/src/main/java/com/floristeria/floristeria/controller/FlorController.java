package com.floristeria.floristeria.controller;

import com.floristeria.floristeria.model.Flor;
import com.floristeria.floristeria.service.FlorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/flores")
public class FlorController {

    private static final String UPLOAD_FOLDER = "C:\\Users\\Deiner Caanche Villa\\Desktop\\floristeria-main\\floristeria-main\\uploads\\"; // Carpeta de subida de archivos

    @Autowired
    private FlorService florService;

    @Operation(summary = "Mostrar formulario para capturar una nueva flor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario mostrado correctamente")
    })
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("flor", new Flor());
        return "florForm"; // Nombre de la plantilla Thymeleaf
    }

    @Operation(summary = "Guardar los datos de una nueva flor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección a la lista de flores"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/guardar")
    public String saveFlor(@ModelAttribute Flor flor, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Por favor, seleccione un archivo para cargar.");
            return "redirect:/flores/form";
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_FOLDER);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            byte[] bytes = file.getBytes();
            Path path = uploadPath.resolve(file.getOriginalFilename());
            Files.write(path, bytes);

            flor.setImagePath("/uploads/" + file.getOriginalFilename());
            florService.save(flor);
            redirectAttributes.addFlashAttribute("message", "Archivo cargado exitosamente: '" + file.getOriginalFilename() + "'");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error al cargar el archivo: " + e.getMessage());
            return "redirect:/flores/form";
        }

        return "redirect:/flores/listar";
    }

    @Operation(summary = "Mostrar lista de flores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de flores mostrada correctamente")
    })
    @GetMapping("/listar")
    public String listFlores(Model model) {
        List<Flor> flores = florService.findAll();
        model.addAttribute("flores", flores);
        return "flores"; // Nombre de la plantilla Thymeleaf para la lista de flores
    }
}
