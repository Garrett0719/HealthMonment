package comp5216.sydney.edu.au.healthproject;

//package
import okhttp3.OkHttpClient;
import okhttp3.Request;

//http tools
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}

