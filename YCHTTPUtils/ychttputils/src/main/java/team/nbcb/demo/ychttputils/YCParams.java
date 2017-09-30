package team.nbcb.demo.ychttputils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglun on 2017/9/25.
 */

public class YCParams {

    public static final int PARAM_TYPE_JSON = 0;

    public static final int PARAM_TYPE_FORM = 1;

    private int mParamType;
    private Map<String, Object> mParams;

    public YCParams(int paramType) {
        this(paramType, new HashMap<String, Object>());
    }

    public YCParams(int paramType, Map<String, Object> params) {
        this.mParamType = paramType;
        this.mParams = params;
    }

    public void add(String key, Object value) {
        mParams.put(key, value);
    }

    public int getParamType() {
        return mParamType;
    }

    public Map<String, Object> getParams() {
        return mParams;
    }

}
