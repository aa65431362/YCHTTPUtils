package team.nbcb.demo.ychttputils;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by wanglun on 2017/9/27.
 */

public  abstract class YCCallBack<T> {

    Type mType;

    public YCCallBack() {
        mType = getSuperClassTypeParameter(getClass());
    }

    Type getSuperClassTypeParameter(Class<?> subclass) {

        Type superclass = subclass.getGenericSuperclass();

        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }

        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }

    public abstract void onSuccess(T result);

    public abstract void onFailure(int errorCode, String errorMsg);
}
