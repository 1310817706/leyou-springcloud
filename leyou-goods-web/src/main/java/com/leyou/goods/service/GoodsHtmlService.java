package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsService goodsService;

    public  void createHtml(Long spuId){
        Context context=new Context();

        PrintWriter printWriter=null;
        File file = new File("C:\\lsl\\legou\\tools\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
        try {
             printWriter = new PrintWriter(file);
            context.setVariables(this.goodsService.loadData(spuId));
            this.engine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(printWriter!=null){
                printWriter.close();
            }
        }

    }

    public void deleteHtml(Long id) {

        File file = new File("C:\\lsl\\legou\\tools\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
