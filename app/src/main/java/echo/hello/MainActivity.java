package echo.hello;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BLCountDownProgressCircle countDownProgressCircle = findViewById(R.id.bcc_icon);
        countDownProgressCircle.setCenterText("1");
        countDownProgressCircle.setDuration(30000, 1000, null);

        Map<String, String> keyValuePair = new HashMap<String, String>();
        keyValuePair.put("Name", "小智");
        keyValuePair.put("Age", "10");
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);
        list.add(keyValuePair);

        ListView lvContent = findViewById(R.id.lv_content);
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[]{"Name", "Age"}, new int[]{android.R.id.text1, android.R.id.text2});

        final View footerView = getLayoutInflater().inflate(R.layout.item_footer, null);
        
        lvContent.addFooterView(footerView);
        lvContent.setAdapter(simpleAdapter);

        BLTools.GetNetworkType(MainActivity.this);
    }
}
