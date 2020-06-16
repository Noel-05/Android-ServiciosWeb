package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

@SuppressLint("NewApi")
public class Local_insertar extends Activity {
    EditText editCodlocal, editNomlocal, editUbicacionLocal;
    ControladorBase helper;
    private final String urlLocal = "http://169.254.99.89/ws_insertar_local.php";

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_insertar);
        helper = new ControladorBase(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        editCodlocal = (EditText) findViewById(R.id.editCodlocal);
        editNomlocal = (EditText) findViewById(R.id.editNomlocal);
        editUbicacionLocal = (EditText) findViewById(R.id.editUbicacionlocal);
    }

    public void insertarLocal(View v){
        String codlocal = editCodlocal.getText().toString();
        String nomlocal = editNomlocal.getText().toString();
        String ubiclocal = editUbicacionLocal.getText().toString();
        String regInsertados;

        Local local = new Local();
        local.setCodlocal(codlocal);
        local.setNomlocal(nomlocal);
        local.setUbicacionlocal(ubiclocal);

        helper.abrir();
        regInsertados = helper.insertar(local);
        helper.cerrar();
        Toast.makeText(this, regInsertados, Toast.LENGTH_SHORT).show();
    }

    public void insertarLocalWS(View v){
        String codlocal = editCodlocal.getText().toString();
        String nomlocal = editNomlocal.getText().toString();
        String ubiclocal = editUbicacionLocal.getText().toString();

        String nombrelocal = nomlocal.replace(" ", "%20");
        String ubicacionlocal = ubiclocal.replace(" ", "%20");

        String url = null;
        JSONObject datosLocal = new JSONObject();
        JSONObject local = new JSONObject();

        url = urlLocal + "?codlocal=" +codlocal+ "&nomlocal=" +nombrelocal+ "&ubicacion=" +ubicacionlocal;
        ControladorServicio.insertarNotaLocalWS(url, this);
    }

    public void limpiarTexto(View v){
        editCodlocal.setText("");
        editNomlocal.setText("");
        editUbicacionLocal.setText("");
    }
}
