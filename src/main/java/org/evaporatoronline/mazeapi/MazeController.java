package org.evaporatoronline.mazeapi;

import jakarta.servlet.http.HttpServletResponse;
import mazeGenerator.Draw;
import mazeGenerator.Generator;
import mazeGenerator.Grid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
public class MazeController {

    @GetMapping("/")
    public String index() {
        return "Welcome to the MazeApi !";
    }

    @GetMapping(value = "/maze")
    public ResponseEntity<?> MazeImage(HttpServletResponse httpServletResponse,
                          @RequestParam int Length, @RequestParam int Width,
                          @RequestParam int Weight, @RequestParam String Algorithm) throws IOException {


        if (Length < 5 || Length > 1000){
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Length parameter"));
        }

        if (Width < 5 || Width > 1000){
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Width parameter"));
        }

        if (Weight < 0 || Weight > 100){
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Weight parameter"));
        }

        Grid grid = new Grid(Length, Width);

        switch (Algorithm) {
            case "Eller's" -> Generator.Ellers(grid);
            case "GrowingTree" -> Generator.growingTree(grid, Weight);
            default -> {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid Algorithm parameter"));
            }
        }
        BufferedImage mazeImage = Draw.gridDraw(grid);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(mazeImage, "png", output);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(output.toByteArray());
    }

}