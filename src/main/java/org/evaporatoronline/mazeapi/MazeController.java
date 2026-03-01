package org.evaporatoronline.mazeapi;

import jakarta.servlet.http.HttpServletResponse;
import mazeGenerator.Draw;
import mazeGenerator.Generator;
import mazeGenerator.Grid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@RestController
public class MazeController {

    @GetMapping("/")
    public String index() {
        return "Welcome to the MazeApi !";
    }

    @GetMapping(value = "/maze", produces = "image/png")
    public void MazeImage(HttpServletResponse httpServletResponse,
                          @RequestParam int Length, @RequestParam int Width,
                          @RequestParam int Weight, @RequestParam String Algorithm) throws IOException {


        if (Length < 5 || Length > 1000){
            throw new InvalidMazeParameterException("Invalid Length parameter");
        }

        if (Width < 5 || Width > 1000){
            throw new InvalidMazeParameterException("Invalid Width parameter");
        }

        if (Weight < 0 || Weight > 100){
            throw new InvalidMazeParameterException("Invalid Weight parameter");
        }

        Grid grid = new Grid(Length, Width);

        switch (Algorithm){
            case "Eller's" -> Generator.Ellers(grid);
            case "GrowingTree" -> Generator.growingTree(grid, Weight);
            default -> throw new InvalidMazeParameterException("Invalid Algorithm parameter");
        }

        BufferedImage mazeImage = Draw.gridDraw(grid);
        httpServletResponse.setContentType("image/png");
        ImageIO.write(mazeImage, "png", httpServletResponse.getOutputStream());
    }

}