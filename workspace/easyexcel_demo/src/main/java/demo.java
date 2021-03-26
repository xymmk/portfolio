import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class demo {

    public static void main(String[] args) {
        readExcel();
        writeExcel();
    }

    private static void readExcel(){
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

    private  static void writeExcel(){
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
