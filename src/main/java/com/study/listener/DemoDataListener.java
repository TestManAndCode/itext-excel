package com.study.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.study.model.Demo;

import java.util.ArrayList;
import java.util.List;

public class DemoDataListener extends AnalysisEventListener<Demo> {
    List<Demo> list = new ArrayList<Demo>();
    @Override
    public void invoke(Demo demo, AnalysisContext analysisContext) {
        list.add(demo);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println(JSON.toJSONString(list));
    }
}
