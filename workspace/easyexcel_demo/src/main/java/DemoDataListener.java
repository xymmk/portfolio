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
