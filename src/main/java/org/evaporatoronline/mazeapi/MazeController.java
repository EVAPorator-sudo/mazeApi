package org.evaporatoronline.mazeapi;

import jakarta.servlet.http.HttpServletResponse;
import mazeGenerator.Draw;
import mazeGenerator.Generator;
import mazeGenerator.Grid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@CrossOrigin(origins = {"https://www.evaporatoronline.org",
        "http://localhost:3000",
        "http://docker:808"})
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

        Grid grid = new Grid(Length, Width);

        if (Objects.equals(Algorithm, "Eller's")) {
            Generator.Ellers(grid);
        } else {
            Generator.growingTree(grid, Weight);
        }

        BufferedImage mazeImage = Draw.gridDraw(grid);
        httpServletResponse.setContentType("image/png");
        ImageIO.write(mazeImage, "png", httpServletResponse.getOutputStream());
    }

}