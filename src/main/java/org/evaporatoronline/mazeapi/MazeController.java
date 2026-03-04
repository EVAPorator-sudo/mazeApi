package org.evaporatoronline.mazeapi;

import mazeGenerator.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class MazeController {

    @GetMapping("/")
    public String index() {
        return "Welcome to the MazeApi !";
    }

    @CrossOrigin(originPatterns = {
            "https://evaporatoronline.org",
            "http://docker:808",
            "http://localhost:8080"
    })
    @GetMapping(value = "/generate")
    public ResponseEntity<?> MazeImage(
            @RequestParam int Length,
            @RequestParam int Height,
            @RequestParam int Weight,
            @RequestParam String Algorithm,
            @RequestParam(name = "_", required = false) String cacheFiller
    ) throws IOException {

        if (Length < 5 || Length > 1000) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Length parameter"));
        }

        if (Height < 5 || Height > 1000) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Height parameter"));
        }

        if (Weight < 0 || Weight > 100) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Weight parameter"));
        }

        Random random = new Random();
        Grid grid = new Grid(Length, Height);
        long seed = random.nextLong();

        switch (Algorithm) {
            case "Eller's" -> Generator.Ellers(grid, seed);
            case "GrowingTree" -> Generator.growingTree(grid, Weight, seed);
            default -> {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid Algorithm parameter"));
            }
        }

        BufferedImage mazeImage = Draw.gridDraw(grid);


        HashMap<String, String> MetaData = new HashMap<>();
        MetaData.put("mazeSeed", String.valueOf(seed));
        MetaData.put("mazeAlgorithm", Algorithm);
        MetaData.put("mazeLength", String.valueOf(Length));
        MetaData.put("mazeHeight", String.valueOf(Height));
        MetaData.put("mazeWeight", String.valueOf(Weight));

        ByteArrayOutputStream output = imageManipulator.addMetaData(MetaData, mazeImage);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(output.toByteArray());
    }

    @CrossOrigin(origins = {"https://evaporatoronline.org",
            "http://docker:808",
            "http://localhost:8080"})
    @PostMapping(value = "/solve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> MazeSolve(@RequestParam("image") MultipartFile image, @RequestParam String Start,
                                       @RequestParam String End, @RequestParam String Algorithm) throws IOException {

        HashMap<String, String> metadata;

        try {
            metadata = imageManipulator.readMetaData(image);
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid Image Metadata"));
        }

        String[] requiredData = {
                "mazeLength", "mazeHeight", "mazeWeight",
                "mazeAlgorithm", "mazeSeed"
        };

        for (String key : requiredData) {
            if (!metadata.containsKey(key)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Missing Metadata key: " + key));
            }
        }

        String[] splitStartCoords = Start.split("-");
        String[] splitEndCoords = End.split("-");

        int Length;
        int Height;
        int Weight;
        String genAlgorithm;
        long seed;
        int[] StartCoords;
        int[] EndCoords;

        try {
            Length = Integer.parseInt(metadata.get("mazeLength"));
            Height = Integer.parseInt(metadata.get("mazeHeight"));
            Weight = Integer.parseInt(metadata.get("mazeWeight"));
            genAlgorithm = metadata.get("mazeAlgorithm");
            seed = Long.parseLong(metadata.get("mazeSeed"));
            StartCoords = new int[]{Integer.parseInt(splitStartCoords[0]),
                    Integer.parseInt(splitStartCoords[1])};
            EndCoords = new int[]{Integer.parseInt(splitEndCoords[0]),
                    Integer.parseInt(splitEndCoords[1])};

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid numeric parameter format"));
        }

        if (Length < 5 || Length > 1000) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid Length parameter"));
        } else if (Height < 5 || Height > 1000) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid Height parameter"));
        } else if (Weight < 0 || Weight > 100) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid Weight parameter"));
        } else if (StartCoords[0] > Length - 1 || StartCoords[1] > Height - 1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid Coordinate parameter"));
        }

        Grid grid = new Grid(Length, Height);

        switch (genAlgorithm) {
            case "Eller's" -> Generator.Ellers(grid, seed);
            case "GrowingTree" -> Generator.growingTree(grid, Weight, seed);
            default -> {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid Maze Algorithm parameter"));
            }
        }

        ArrayList<Cell> path;
        Cell start = grid.getCell(StartCoords);
        Cell end = grid.getCell(EndCoords);


        switch (Algorithm) {
            case "Dijkstra's" -> path = Solver.Dijkstras(start, end, grid);
            case "A*" -> path = Solver.A(start, end, grid);
            default -> {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid Solve Algorithm parameter"));
            }
        }

        BufferedImage solveImage = Draw.solveDraw(grid, path);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(solveImage, "png", output);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(output.toByteArray());
    }
}