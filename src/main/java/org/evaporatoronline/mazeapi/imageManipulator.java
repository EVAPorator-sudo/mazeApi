package org.evaporatoronline.mazeapi;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class imageManipulator {

    public static ByteArrayOutputStream addMetaData(HashMap<String, String> metaDataList, BufferedImage Image) throws IOException {

        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        ImageTypeSpecifier typeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);

        IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);

        String nativeFormat = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(nativeFormat);
        IIOMetadataNode textNode = new IIOMetadataNode("tEXt");

        for (String key : metaDataList.keySet()) {
            String value = metaDataList.get(key);
            IIOMetadataNode entry = new IIOMetadataNode("tEXtEntry");
            entry.setAttribute("keyword", key);
            entry.setAttribute("value", value);
            textNode.appendChild(entry);
        }

        root.appendChild(textNode);
        metadata.setFromTree(nativeFormat, root);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(output);
        writer.setOutput(ios);

        IIOImage image = new IIOImage(Image, null, metadata);
        writer.write(null, image, writeParam);

        ios.close();
        writer.dispose();

        return output;
    }

    public static HashMap<String, String> readMetaData(MultipartFile image) throws IOException {

        ImageInputStream inputStream = ImageIO.createImageInputStream(image.getInputStream());
        Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);

        if (!readers.hasNext()) {
            throw new IllegalArgumentException("Unsupported image format");
        }

        ImageReader reader = readers.next();
        reader.setInput(inputStream, true);

        IIOMetadata metadata = reader.getImageMetadata(0);
        String format = metadata.getNativeMetadataFormatName();
        Node root = metadata.getAsTree(format);

        NodeList dataNodes = ((Element) root).getElementsByTagName("tEXtEntry");
        HashMap<String, String> MetaData = new HashMap<>();

        for (int i = 0; i < dataNodes.getLength(); i++) {
            Element element = (Element) dataNodes.item(i);

            String key = element.getAttribute("keyword");
            String value = element.getAttribute("value");

            MetaData.put(key, value);
        }

        reader.dispose();
        inputStream.close();

        return MetaData;

    }
}
