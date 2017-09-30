package team.nbcb.demo.ychttputils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wanglun on 2017/9/22.
 */

public class YCHTTPUtils {

    // 网络连接错误
    private static final int ERROR_CODE_NET = -900;

    // Json字符串转对象错误
    private static final int ERROR_CODE_FROM_JSON_TO_OBJECT = -800;

    private volatile static YCHTTPUtils mInstance;

    private OkHttpClient client;

    private static Gson gson;

    private Handler handler;

    public YCHTTPUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            client = okHttpClient;
        } else {
            client = new OkHttpClient();
        }
        gson = new Gson();
        handler = new Handler(Looper.getMainLooper());
    }

    public static YCHTTPUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (YCHTTPUtils.class) {
                if (mInstance == null) {
                    mInstance = new YCHTTPUtils(okHttpClient);
                }
            }
        } else {
            gson = new Gson();
        }
        return mInstance;
    }

    public static YCHTTPUtils getInstance() {
        return initClient(null);
    }

    /**
     * 同步get
     * @param url
     * @return
     * @throws IOException
     */
    private Response _get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 同步get返回String
     * @param url
     * @return
     * @throws IOException
     */
    private String _getAsString(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response execute = call.execute();
        return execute.body().string();
    }

    /**
     * 异步get
     * @param url
     * @param callBack
     */
    private void _getAsyn(String url, YCCallBack callBack) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(request, callBack);
    }

    /**
     * 异步post
     * @param url
     * @param params
     * @param callBack
     */
    private void _post(String url, YCParams params, YCCallBack callBack) {
        Request request = buildPostRequest(url, params);
        deliveryResult(request, callBack);
    }

    /**
     * 同步post上传文件
     * @param url
     * @param files
     * @param params
     * @return
     * @throws IOException
     */
    private Response _post(String url, File[] files, YCParams params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, params);
        return client.newCall(request).execute();
    }

    private Response _post(String url, File file, YCParams params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[] {file}, params);
        return client.newCall(request).execute();
    }

    /**
     * 异步post上传文件
     * @param url
     * @param files
     * @param params
     * @param callBack
     * @throws IOException
     */
    private void _post(String url, File[] files, YCParams params, YCCallBack callBack) throws IOException {
        Request request = buildMultipartFormRequest(url, files, params);
        deliveryResult(request, callBack);
    }

    private void _post(String url, File file, YCParams params, YCCallBack callBack) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, params);
        deliveryResult(request, callBack);
    }

    /*----------------- 对外公布的方法 -----------------*/
    /**
     * 同步get
     * @param url
     * @return
     * @throws IOException
     */
    public static Response get(String url) throws IOException {
        return getInstance()._get(url);
    }

    /**
     * 同步get返回String
     * @param url
     * @return
     * @throws IOException
     */
    public static String getAsString(String url) throws IOException {
        return getInstance()._getAsString(url);
    }

    /**
     * 异步get
     * @param url
     * @param callBack
     */
    public static void getAsyn(String url, YCCallBack callBack) {
        getInstance()._getAsyn(url, callBack);
    }

    /**
     * 异步post
     * @param url
     * @param params
     * @param callBack
     */
    public static void post(String url, YCParams params, YCCallBack callBack) {
        getInstance()._post(url, params, callBack);
    }

    /**
     * 构建post请求
     * @param url
     * @param params
     * @return
     */
    private Request buildPostRequest(String url, YCParams params) {
        int type = params.getParamType();
        Map<String, Object> mParams = params.getParams();
        String paramsString = "";
        if (mParams == null || mParams.size() <= 0) {
            mParams.put("", "");
        }
        StringBuilder sb = new StringBuilder();
        RequestBody body = null;
        if (type == YCParams.PARAM_TYPE_FORM) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : mParams.entrySet()) {
                builder.add(entry.getKey(), (String) entry.getValue());
            }
            body = builder.build();
        } else if (type == YCParams.PARAM_TYPE_JSON) {
            sb.append(gson.toJson(mParams));
            paramsString = sb.toString();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            body = RequestBody.create(JSON, paramsString);
        }
        return new Request.Builder().url(url).post(body).build();
    }

    private Request buildMultipartFormRequest(String url, File[] files, YCParams params) {
        Map<String, Object> mParams = params.getParams();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody fileBody = null;
        if (mParams == null || mParams.size() <= 0) {
            mParams.put("", "");
        }
        for (Map.Entry<String, Object> entry : mParams.entrySet()) {
            builder.addFormDataPart(entry.getKey(), (String) entry.getValue());
        }
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                builder.addPart(fileBody);
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url)
                .post(requestBody).build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void deliveryResult(Request request, final YCCallBack callBack) {
//        callBack.onPrepare();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureCallBack(ERROR_CODE_NET, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                try {
                    if (callBack.mType == String.class) {
                        sendSuccessResultCallBack(str, callBack);
                    } else {
                        Object object = gson.fromJson(str, callBack.mType);
                        sendSuccessResultCallBack(object, callBack);
                    }
                } catch (JsonParseException e) {
                    sendFailureCallBack(ERROR_CODE_FROM_JSON_TO_OBJECT, callBack);
                } catch (Exception e) {
                    sendFailureCallBack(response.code(), callBack);
                }
            }
        });
    }

    private void sendSuccessResultCallBack(final Object object, final YCCallBack callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onSuccess(object);
                }
            }
        });
    }

    private void sendFailureCallBack(final int errorCode, final YCCallBack callBack) {
        String errorMsg = "";
        if (errorCode == ERROR_CODE_NET) {
            errorMsg = "网络连接失败，请稍后重试";
        } else if (errorCode == ERROR_CODE_FROM_JSON_TO_OBJECT) {
            errorMsg = "对象转换失败";
        } else {
            errorMsg = "未知错误";
        }
        final String finalErrorMsg = errorMsg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onFailure(errorCode, finalErrorMsg);
                }
            }
        });
    }

}
