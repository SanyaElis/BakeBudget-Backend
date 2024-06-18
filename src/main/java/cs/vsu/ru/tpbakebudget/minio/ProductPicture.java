package cs.vsu.ru.tpbakebudget.minio;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductPicture {
    private final String minioObjectName;

    private final byte[] base64Image;

    private final String extension;

    public ProductPicture(byte[] base64Image, String extension) {
        this.minioObjectName = UUID.randomUUID() + "." + extension;
        this.base64Image = base64Image;
        this.extension = extension;
    }
}
