package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class AsignaturaInsertarActivity extends Activity {
    ControladorBase helper;
    EditText editCodasignatura, editNombreasignatura, editUnidadesval;
    private final String urlLocal = "http://192.168.1.8/ws_materia_insert.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignatura_insertar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        helper= new ControladorBase(this);
        editCodasignatura= (EditText) findViewById(R.id.editCodasignatura);
        editNombreasignatura= (EditText) findViewById(R.id.editNombreasignatura);
        editUnidadesval= (EditText) findViewById(R.id.editUnidadesval);
    }
    public void insertarAsignatura(View v){

        String codasignatura=editCodasignatura.getText().toString();
        String nombreasignatura=editNombreasignatura.getText().toString();
        String unidadesval=editUnidadesval.getText().toString();
        String regInsertados;
        Asignatura asignatura= new Asignatura();
        asignatura.setCodasignatura(codasignatura);
        asignatura.setNomasignatura(nombreasignatura);
        asignatura.setUnidadesval(unidadesval);
        helper.abrir();
        regInsertados=helper.insertar(asignatura);
        helper.cerrar();
        Toast.makeText(this, regInsertados, Toast.LENGTH_SHORT).show();
    }

    public void limpiarTexto(View v) {
        editCodasignatura.setText("");
        editNombreasignatura.setText("");
        editUnidadesval.setText("");
    }

    public void insertarAsignaturaWS(View v){
        String codAsignatura = editCodasignatura.getText().toString();
        String nomAsig = editNombreasignatura.getText().toString();
        String uvs = editUnidadesval.getText().toString();

        String nombreAsignatura = nomAsig.replace(" ", "%20");

        String url = null;
        JSONObject datosLocal = new JSONObject();
        JSONObject local = new JSONObject();

        url = urlLocal + "?codmateria=" +codAsignatura+"&nombre=" +nombreAsignatura+"&uvs=" +uvs;
        String resultado = ControladorServicio.insertarMateriaWS(url, this);
        Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();
    }

}
