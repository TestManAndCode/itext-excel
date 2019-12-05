package com.study.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.google.zxing.WriterException;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.study.listener.DemoDataListener;
import com.study.model.Demo;
import com.study.util.PrintUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
public class TestController {
    @GetMapping("/pdf")
    public void getPdf(@RequestParam(defaultValue = "0",required = false) float x ,
                       @RequestParam(defaultValue = "0",required = false)  float y,
                       HttpServletResponse response) throws IOException, WriterException {
        OutputStream outputStream = response.getOutputStream();
        Document document= PrintUtil.getDocument(outputStream,300,300);
        byte[] bytes= PrintUtil.getQRToByte("131231",100,100);
        Image image = PrintUtil.getImage(bytes);
        image.setFixedPosition(x,y);
        for (int i=0;i<=3;i++){
            document.add(image);
            document.add(new Paragraph("deo"));
            document.add(new Paragraph("deo"));
            document.add(new Paragraph("deo"));
            //下一页
            document.add(PrintUtil.nextPage());
        }
        document.close();
    }
    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), Demo.class,new DemoDataListener()).sheet().doRead();
        List<Demo> list= EasyExcel.read(file.getInputStream()).head(Demo.class).sheet().doReadSync();
        System.out.println(JSON.toJSONString(list));
        //List<Demo> list = EasyExcel.read(file.getInputStream()).head(Demo.class).sheet().doReadSync();
    }

}

