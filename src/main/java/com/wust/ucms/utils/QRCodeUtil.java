package com.wust.ucms.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtil {

    private static final int WIDTH = 140;
    private static final int HEIGHT = 140;
    private static final String FILE_FORMAT = "png";
    private static final Map<EncodeHintType, Object> HINTS = new HashMap<EncodeHintType, Object>();

    static {
        HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
        HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        HINTS.put(EncodeHintType.MARGIN, 0);
    }

    public static BufferedImage toBufferedImage(String content) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, HINTS);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static BufferedImage toBufferedImage(String content, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, HINTS);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static void writeToStream(String content, OutputStream stream) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, HINTS);
        MatrixToImageWriter.writeToStream(bitMatrix, FILE_FORMAT, stream);
    }

    public static void writeToStream(String content, OutputStream stream, int width, int height)
            throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, HINTS);
        MatrixToImageWriter.writeToStream(bitMatrix, FILE_FORMAT, stream);
    }

    public static void createQRCodeFile(String content, String path) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, HINTS);
        MatrixToImageWriter.writeToPath(bitMatrix, FILE_FORMAT, new File(path).toPath());
    }

    public static void createQRCodeFile(String content, String path, int width, int height)
            throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, HINTS);
        MatrixToImageWriter.writeToPath(bitMatrix, FILE_FORMAT, new File(path).toPath());
    }
}
