package com.study.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PrintUtil {

    public static Document getDocument(OutputStream os, float width, float height) {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, new PageSize(width, height));
        document.setMargins(0, 0, 0, 0);
        return document;
    }

    public static Image getImage(byte[] bytes) {
        ImageData imageData = ImageDataFactory.create(bytes);
        return new Image(imageData);
    }

    public static AreaBreak nextPage() {
        return new AreaBreak(AreaBreakType.NEXT_PAGE);
    }

    public static Text getText(String font, Color color, String fontProgram) throws IOException {
        return new Text(font)
                .setFontColor(color)
                .setFont(PdfFontFactory.createFont(fontProgram));
    }


    /**
     * 生成二维码
     */
    public static BufferedImage getQR(String content, int width, int height) throws WriterException, IOException {
        File file = new File(System.getProperty("user.dir") + "/temp", content + ".png");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }


        Map<EncodeHintType, Object> encodeHints = new HashMap<>();
        encodeHints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        encodeHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        encodeHints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, encodeHints);
        bitMatrix = deleteWhite(bitMatrix);
        FileOutputStream fos = new FileOutputStream(new File(System.getProperty("user.dir") + "/temp", content + ".png"));
        MatrixToImageWriter.writeToStream(bitMatrix, "png", fos);
        return ImageIO.read(file);
    }

    public static byte[] getQRToByte(String content, int width, int height) throws IOException, WriterException {

        Map<EncodeHintType, Object> encodeHints = new HashMap<>();
        encodeHints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        encodeHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        encodeHints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, encodeHints);
        bitMatrix = deleteWhite(bitMatrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", os);
        return os.toByteArray();
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * 生成39格式条码
     */
    public static BufferedImage get39Code(String content, int width, int height) throws IOException, WriterException {
        File file = new File(System.getProperty("user.dir") + "/temp", content + ".png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }


        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 容错级别 这里选择最高H级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.CODABAR, width, height, hints);

        MatrixToImageWriter.writeToStream(bitMatrix, "png",
                new FileOutputStream(file));


        return ImageIO.read(file);
    }
}
