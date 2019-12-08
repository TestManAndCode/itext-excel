package com.study.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.google.zxing.WriterException;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextChunkLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextChunk;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.study.listener.DemoDataListener;
import com.study.model.Demo;
import com.study.util.PrintUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
public class TestController {
    @GetMapping("/pdf")
    public void getPdf(@RequestParam(defaultValue = "0", required = false) float x,
                       @RequestParam(defaultValue = "0", required = false) float y,
                       HttpServletResponse response) throws IOException, WriterException {
        OutputStream outputStream = response.getOutputStream();
        Document document = getWmsPrintDoc(outputStream);
        Text a = new Text("");
        a.setFontSize(15);
        a.setUnderline(0, 30);
        for (int i = 0; i <= 3; i++) {
            createWmsPrintInfo(document);
            //下一页
            document.add(PrintUtil.nextPage());
        }
        document.close();
    }

    private void createWmsPrintInfo(Document document) throws IOException, WriterException {
        Image image = PrintUtil.getImage(getQrCode("测试内容", 400, 400));
        image.setFixedPosition(100, 300);
        Paragraph paragraph = new Paragraph();
        Paragraph paragraphLine= new Paragraph();
        int fontSize = 20;
        int lineNumber=2;
        Text client = PrintUtil.getText("客户:", fontSize, false);
        Text quantity = PrintUtil.getText("\t数量:", fontSize, false);
        Text version = PrintUtil.getText("版本:", fontSize, false);

        Text clientValue = PrintUtil.getText("文中文中文", fontSize, false);
        Text quantityValue = PrintUtil.getText("2000", fontSize, false);
        Text versionValue = PrintUtil.getText("v1.0.0", fontSize, false);
        SolidLine line = new SolidLine(1f);

        paragraph.add(client);
        paragraph.add(clientValue);
        paragraph.add(quantity);
        paragraph.add(quantityValue);
        paragraph.add(PrintUtil.newLine());
        paragraph.add(version);
        paragraph.add(versionValue);

        LineSeparator ls = new LineSeparator(line);
        ls.setWidth(160);
        ls.setHeight(1f);
        ls.setMargin(0);
        paragraphLine.add(ls);
        paragraphLine.setFixedPosition(140, 300-lineNumber*fontSize-50, 400);
        paragraph.setFixedPosition(100, 300-lineNumber*fontSize-50, 400);
        paragraph.setFont(PrintUtil.getPdfFont());
        document.add(image);
        document.add(paragraph);
        document.add(paragraphLine);

    }

    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), Demo.class, new DemoDataListener()).sheet().doRead();
        List<Demo> list = EasyExcel.read(file.getInputStream()).head(Demo.class).sheet().doReadSync();
        System.out.println(JSON.toJSONString(list));
        //List<Demo> list = EasyExcel.read(file.getInputStream()).head(Demo.class).sheet().doReadSync();
    }

    private Document getWmsPrintDoc(OutputStream os) {
        return PrintUtil.getDocument(os, 600, 750);
    }

    public byte[] getQrCode(String msg, int width, int height) throws IOException, WriterException {
        byte[] qrCode = PrintUtil.getQRToByte(msg, width, height);
        byte[] bytes = PrintUtil.setQrCodeWatermark(new ByteArrayInputStream(qrCode), width, height);
        return bytes;
    }

}

