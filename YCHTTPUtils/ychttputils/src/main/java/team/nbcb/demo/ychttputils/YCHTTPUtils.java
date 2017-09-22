package team.nbcb.demo.ychttputils;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/9/22.
 */

public class YCHTTPUtils {

    private YCHTTPUtils mInstance;

    private OkHttpClient client;

    private Gson gson;

    public YCHTTPUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            client = okHttpClient;
        } else {
            client = new OkHttpClient();
        }
    }

    public YCHTTPUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (YCHTTPUtils.class) {
                if (mInstance == null) {
                    mInstance = new YCHTTPUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public YCHTTPUtils getInstance() {
        return initClient(null);
    }
}
