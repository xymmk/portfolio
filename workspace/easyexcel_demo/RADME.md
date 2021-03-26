# 概要

EasyExcelというJavaライブラリーApache poi、jxlなどライブラリーより、使うメモリは少ないらしいので、今回使ってみました。もっと詳しい情報はEasyExcelのgithubを見てください。

[EasyExcelのgithub](https://github.com/alibaba/easyexcel)

# 環境

## OS
 macOS Catalina

## Java
1.8

## Gradle
5.2.1

## easyexcel
2.1.1

# ライブラリーをインストール

```kotlin

plugins {
    id 'java'
    id 'application'
}

sourceCompatibility = 1.8

repositories {
    mavenCentral()
}

mainClassName = 'demo'

// exayexcelライブラリー追加
dependencies {
    implementation group: 'com.alibaba', name: 'easyexcel', version: '2.1.1'
}

```

# テスト用のExcelファイルを用意

今回はEasyExcelのリポジトリ中の[cellDataDemo.xlsx](https://github.com/alibaba/easyexcel/blob/19fcc3f4f66a45b1e1a9d0cef4b6edf63dbd7f03/src/test/resources/demo/cellDataDemo.xlsx)ファイルを使う

## celDataDemo.xlsxファイルの内容

|文字列|日付|ID|関数|
|--|--|--|--|
|字符串0|2020/1/1 1:01|1|=CONCAT(A9,C9)|

# cellDataDemoを定義するBeanを作成

[参考](https://github.com/alibaba/easyexcel/blob/19fcc3f4f66a45b1e1a9d0cef4b6edf63dbd7f03/src/test/java/com/alibaba/easyexcel/test/demo/read/DemoData.java)

```Java
import java.util.Date;

public class DemoData {
    private String string;
    private Date date;
    private Double doubleData;

    // getter、setterを設定する
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getDoubleData() {
        return doubleData;
    }

    public void setDoubleData(Double doubleData) {
        this.doubleData = doubleData;
    }
}

```

# DemoDataListener用意

[参考](https://github.com/alibaba/easyexcel/blob/master/src/test/java/com/alibaba/easyexcel/test/demo/read/DemoDataListener.java)

```Java
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;


public class DemoDataListener extends AnalysisEventListener<DemoData> {

    // メモリ不足にならないように、5行毎にListをクリアする（実際は3000行毎にクリアしても良いそうです。）
    private static final int BATCH_COUNT = 5;
    List<DemoData> list = new ArrayList<DemoData>();


    //Excelファイル1行ずつ処理するので、毎行も下記の処理を行う
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        list.add(data);
        if (list.size() >= BATCH_COUNT) {
            saveData(list);
            // 5行毎にデータベースへ保存し、listをクリアする
            list.clear();
        }
    }

    //読み込みが終わったら、下記の処理を行う
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("処理完了");
    }

    private void saveData(List<DemoData> list){
        list.forEach(demo -> {
            System.out.println("文字列: " +  demo.getString() + "日付: " + demo.getDate() + "doubleData: " + demo.getDoubleData());
        });
        System.out.println(list.size() + "行データは保存しました。");
    }
}
```

# excelデータを読み込む

[参考](https://github.com/alibaba/easyexcel/blob/master/src/test/java/com/alibaba/easyexcel/test/demo/read/ReadTest.java)

```Java
//ExcelReaderクラスは古いバージョンにないらしい。
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.ReadSheet;

public class demo {

    public static void main(String[] args) {
        String filename = "src/main/resources/cellDataDemo.xlsx";
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(filename, DemoData.class, new DemoDataListener()).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
        } finally {
            if (excelReader != null) {
                excelReader.finish();
            }
        }
    }
}

```

# excelの書き込み

[参考](https://github.com/alibaba/easyexcel/blob/master/src/test/java/com/alibaba/easyexcel/test/demo/write/WriteTest.java)

```Java
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class demo {

    public static void main(String[] args) {
        String fileName = "src/main/resources/" + System.currentTimeMillis() + ".xlsx";
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(fileName, DemoData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("sheetName").build();
            excelWriter.write(data(), writeSheet);
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    private static List<DemoData> data() {
        List<DemoData> list = new ArrayList<DemoData>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("文字列" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
```

# まとめ
EasyExcelは一応アリババグループが開発したライブラリーらしいです。ネットで調査してみたら、jxlは確かにmemory leakなど問題がありそうですので、デモのコードによると、一行ずつのデータをメモリに読み込んで、定期的にArrayListのデータを削除するのは確かにメモリリークにはなりませんな。

> [stackoverflowでjxlのout of memoryに関する質問](https://stackoverflow.com/questions/3167029/java-out-of-memory-error-while-writing-excel-cells-in-jxl)
