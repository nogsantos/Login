package nogsantos.br.login;

import android.app.Activity;
import android.os.Bundle;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements HttpRequest.GetJSONListener {

    private Activity context;
    private HttpRequest.GetJSONListener listener;
    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button p = (Button) findViewById(R.id.button1);
        /*
         * Capturamos o nome
         */
        final EditText nome = (EditText) findViewById(R.id.editText1);
        /*
         * Capturamos a senha
         */
        final EditText senha = (EditText) findViewById(R.id.editText2);
        context = this;
        listener = this;
        /*
         * Evento onclick no botão
         */
        p.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*
                 * Criamos nosso HashMap
                 */
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("nome", nome.getText().toString());
                data.put("senha",senha.getText().toString());
                /*
                 * Passamos nossos parâmetros
                 */
                HttpRequest http = new HttpRequest(data,listener,HttpRequest.TYPEPOST);
                http.setContext(context);
                http.execute("http://rafaelribeirowebmaster.com.br/teste_uol/login.php");
            }
        });
    }
    /**
     * Call Back para ver se o usuário é válido ou não
     */
    @Override
    public void onRemoteCallComplete(JSONArray response) {
        try {
            JSONObject json = (JSONObject) response.get(0);
            SharedPreferences preferences = getSharedPreferences("usuario-login",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            /*
             * Verificamos se o usuário é válido e guardamos nossa session com ShardePreferences
             */
            if(json.getString("sucess").equals("1")){
                editor.putString("usuario","1");
            }else{
                editor.putString("usuario","0");
            }
            /*
             * Gravamos a session
             */
            editor.commit();
            String s = preferences.getString("usuario","0");
            /*
             * Veirifcamos se o Usuário é válido
             */
            if(s.equals("0")){
                Toast.makeText(context,"Usuário Inválido",Toast.LENGTH_LONG).show();
            }else{
                Intent myIntent = new Intent(context, Activity2.class);
                MainActivity.this.startActivity(myIntent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRemoteCallFailed() {
        // TODO Auto-generated method stub
    }

}
