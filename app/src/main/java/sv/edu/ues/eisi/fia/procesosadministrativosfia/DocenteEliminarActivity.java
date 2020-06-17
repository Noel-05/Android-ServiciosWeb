package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DocenteEliminarActivity extends Activity {

    ControladorBase helper;
    EditText editCoddocente, editNombredocente, editApellidodocente;

    private final String urlLocal = "http://169.254.99.89/ws_eliminar_docente.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docente_eliminar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        helper= new ControladorBase(this);
        editCoddocente=(EditText)findViewById(R.id.editCoddocente);
        editNombredocente=(EditText)findViewById(R.id.editNombredocente);
        editApellidodocente=(EditText)findViewById(R.id.editApellidodocente);

    }
    public void eliminarDocente(View v){
        String regEliminadas;
        Docente docente=new Docente();
        docente.setCoddocente(editCoddocente.getText().toString());
        docente.setNomdocente(editNombredocente.getText().toString());
        docente.setApellidodocente(editApellidodocente.getText().toString());
        helper.abrir();
        regEliminadas=helper.eliminar(docente);
        helper.cerrar();
        Toast.makeText(this, regEliminadas, Toast.LENGTH_SHORT).show();
    }

    public void eliminarDocenteWS(View view) {
        String docente = editCoddocente.getText().toString();
        try {
            String url = urlLocal + "?coddocente=" +docente;
            String docenteEliminado = ControladorServicio.eliminarDocenteWS(url, this);
            Toast.makeText(getApplicationContext(), docenteEliminado, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void limpiar(View v){
        editCoddocente.setText("");
        editNombredocente.setText("");
        editApellidodocente.setText("");
    }
}
