package org.evaporatoronline.mazeapi;

import jakarta.servlet.http.HttpServletResponse;
import mazeGenerator.Draw;
import mazeGenerator.Generator;
import mazeGenerator.Grid;
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
            HttpServletResponse httpServletResponse,
            @RequestParam int Length,
            @RequestParam int Height,
            @RequestParam int Weight,
            @RequestParam String Algorithm
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

    private void addMetadata(IIOMetadataNode root, String key, String value) {
        IIOMetadataNode textNode = new IIOMetadataNode("tEXt");
        IIOMetadataNode entry = new IIOMetadataNode("tEXtEntry");
        entry.setAttribute("keyword", key);
        entry.setAttribute("value", value);
        textNode.appendChild(entry);
        root.appendChild(textNode);
    }
}