package org.evaporatoronline.mazeapi;

import mazeGenerator.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        ImageTypeSpecifier typeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);

        IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);

        String nativeFormat = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(nativeFormat);

        addMetadata(root, "mazeSeed", String.valueOf(seed));
        addMetadata(root, "mazeAlgorithm", Algorithm);
        addMetadata(root, "mazeWidth", String.valueOf(Length));
        addMetadata(root, "mazeHeight", String.valueOf(Height));
        addMetadata(root, "mazeWeight", String.valueOf(Weight));

        metadata.setFromTree(nativeFormat, root);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(output);
        writer.setOutput(ios);

        IIOImage image = new IIOImage(mazeImage, null, metadata);
        writer.write(null, image, writeParam);

        ios.close();
        writer.dispose();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(output.toByteArray());
    }

    @CrossOrigin(origins = {"https://evaporatoronline.org/solve",
            "http://docker:808/solve",
            "http://localhost:8080/solve"})
    @GetMapping("/solve")
    public ResponseEntity<?> MazeSolve(@RequestParam String ID, @RequestParam String Start,
                                       @RequestParam String End, @RequestParam String Algorithm) throws IOException {
        String[] splitID = ID.split("/[-]");
        String[] splitStartCoords = Start.split("/[-]");
        String[] splitEndCoords = End.split("/[-]");

        int Length;
        int Height;
        int Weight;
        String genAlgorithm;
        long seed;
        int[] StartCoords;
        int[] EndCoords;


        try {
            Length = Integer.parseInt(splitID[0]);
            Height = Integer.parseInt(splitID[1]);
            Weight = Integer.parseInt(splitID[2]);
            genAlgorithm = splitID[3];
            seed = Long.parseLong(splitID[4]);
            StartCoords = new int[]{Integer.parseInt(splitStartCoords[0]),
                    Integer.parseInt(splitStartCoords[1])};
            EndCoords = new int[]{Integer.parseInt(splitEndCoords[0]),
                    Integer.parseInt(splitEndCoords[1])};

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        if (splitID.length != 5) {
            ResponseEntity.badRequest().body(Map.of("message", "Invalid ID parameter"));
        } else if (Length < 5 || Length > 1000) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Length parameter"));
        } else if (Height < 5 || Height > 1000) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Height parameter"));
        } else if (Weight < 0 || Weight > 100) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Weight parameter"));
        } else if (StartCoords[0] < Length - 1 || StartCoords[1] < Height - 1) {

        }

        Grid grid = new Grid(Length, Height);

        switch (genAlgorithm) {
            case "Eller's" -> Generator.Ellers(grid, seed);
            case "GrowingTree" -> Generator.growingTree(grid, Weight, seed);
            default -> {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid Algorithm parameter"));
            }
        }

        ArrayList<Cell> path;
        Cell start = grid.getCell(StartCoords);
        Cell end = grid.getCell(EndCoords);


        switch (Algorithm) {
            case "Dijkstra's" -> path = Solver.Dijkstras(start, end, grid);
            case "A*" -> path = Solver.A(start, end, grid);
            default -> {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid Algorithm parameter"));
            }
        }

        BufferedImage solveImage = Draw.solveDraw(grid, path);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(solveImage, "png", output);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(output.toByteArray());
    }

    private void addMetadata(IIOMetadataNode root, String key, String value) {
        IIOMetadataNode textNode = new IIOMetadataNode("tEXt");
        IIOMetadataNode entry = new IIOMetadataNode("tEXtEntry");
        entry.setAttribute("keyword", key);
        entry.setAttribute("value", value);
        textNode.appendChild(entry);
        root.appendChild(textNode);
    }
}