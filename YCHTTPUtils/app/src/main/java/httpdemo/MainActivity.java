package httpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import team.nbcb.demo.ychttputils.YCParams;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YCParams params = new YCParams(YCParams.PARAM_TYPE_JSON);
    }
}
