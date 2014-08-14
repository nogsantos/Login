package nogsantos.br.login;
/**
 * Created by nogsantos on 8/13/14.
 */
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class HttpRequest extends AsyncTask<String, String, String> {
    private HashMap<String, String> mData = null;// post data
    private Activity context;
    private DefaultHttpClient Client;
    private ProgressDialog Progress;
    private String[] param;
    private byte[] result;
    private static int TYPEREQUEST;
    private String str               = "";
    public final static int TYPEPOST = 1;
    public final static int TYPEGET  = 2;
    /*
     * Atributo interface para callback
     */
    GetJSONListener getJSONListener;
    /**
     * constructor
     * @return
     */
    public HttpRequest(HashMap<String, String> data,GetJSONListener listener, int type) {
        this.mData              = data;
        this.Client             = new DefaultHttpClient();
        this.getJSONListener    = listener;
        HttpRequest.TYPEREQUEST = type;
    }
    /**
     * background onde será processado nossa requisição
     */
    @Override
    protected String doInBackground(String... params) {
        this.setParam(params);
        switch (HttpRequest.TYPEREQUEST) {
            case 1:
                str = this.sendPost();
                break;
            case 2:
                str = this.sendGet();
                break;
            default:
                break;
        }
        return str;
    }
    /**
     * on getting result:
     * Retorno do doInBackground
     */
    @Override
    protected void onPostExecute(String result) {
        Progress.dismiss();
        if(result!=""){
            JSONArray jArray = null;
            try {
                jArray = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
             * se tudo der certo fazemos nosso callback com a interface
             */
            getJSONListener.onRemoteCallComplete(jArray);
        }else{
            /*
             * Se não der certo interface callback failed
             */
            getJSONListener.onRemoteCallFailed();
        }
    }
    @Override
    protected void onProgressUpdate(String... values) {
        // something...
    }
   /**
    * Verificamos a coneção com a internet antes de tudo começar
    * @see android.os.AsyncTask#onPreExecute()
    */
    @Override
    protected void onPreExecute() {
        /*
         * Verificamos se está conectado
         */
        if(!this.isOnline()){
            /*
             * Cancelamos
             */
            this.cancel(true);
        }else{
            Progress = ProgressDialog.show(this.getContext(),"Aguarde","processando..",true,false);
            Progress.setCancelable(false);
        }
    }
    /*
     * Enviamos o post
     */
    public String sendPost(){
        /*
         * in this case, params[0] is URL
         */
        HttpPost post = new HttpPost(this.param[0]);
        try {
            post.addHeader(new BasicHeader("Accept", "application/json"));
            ArrayList<NameValuePair> nameValuePair = this.setPost();
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = Client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
        }
        return str;
    }
    /**
     * Enviamos o get
     */
    public String sendGet(){
        /*
         * in this case, params[0] is URL
         */
        String url = this.setGet();
        HttpGet get = new HttpGet();
        try {
            get.setURI(new URI(this.param[0]+url));
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        try {
            get.addHeader(new BasicHeader("Accept", "application/json"));
            HttpResponse response = Client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
        }
        return str;
    }
    /*
     * Montamos nosso post
     */
    public ArrayList<NameValuePair> setPost(){
        /*
         * set up post data
         */
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        Iterator<String> it = mData.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
        }
        return nameValuePair;
    }
    /*
     * Montamos nosso get
     */
    public String setGet(){
        /*
         * set up post data
         */
        Iterator<String> it = mData.keySet().iterator();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        String ent = null;
        while (it.hasNext()) {
            String key = it.next();
            if(i==0){
                ent = "?";
            }else{
                ent = "&";
            }
            builder.append(ent+key+"="+mData.get(key));
            i++;
        }
        return builder.toString();
    }
    /**
     *
     */
    public Activity getContext() {
        return context;
    }
    /**
     *
     */
    public void setContext(Activity context) {
        this.context = context;
    }
    /**
     *
     */
    public boolean isOnline() {
        Activity c = this.getContext();
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    /**
     * @return the param
     */
    public String[] getParam() {
        return param;
    }
    /**
     * @param param the param to set
     */
    public void setParam(String[] param) {
        this.param = param;
    }
    /**
     * @return the result
     */
    public byte[] getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(byte[] result) {
        this.result = result;
    }
    /**
     * Nossa interface para callback
     */
    public interface GetJSONListener {
        public void onRemoteCallComplete(JSONArray response);
        public void onRemoteCallFailed();
    }
}