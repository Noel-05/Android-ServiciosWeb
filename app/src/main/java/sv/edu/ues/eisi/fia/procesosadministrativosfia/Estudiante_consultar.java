package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Estudiante_consultar extends AppCompatActivity {
    EditText editCarnet, editNombre, editApellido, editCarrera;
    ControladorBase helper;
    TextView lblNombre, lblApellido, lblCarrera;
    Button eliminarBtn, actualizarBtn;
    private final String urlLocalActualizarEstudiante = "http://192.168.1.8/ws_estudiante_update.php";
    private final String urlLocalEliminarEstudiante = "http://192.168.1.8/ws_estudiante_delete.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante_consultar);
        helper = new ControladorBase(this);
        editCarnet = findViewById(R.id.editCarnet);
        editNombre = findViewById(R.id.editNombreEstudiante);
        editApellido = findViewById(R.id.editApellidoEstudiante);
        editCarrera = findViewById(R.id.editCarreraEstudiante);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    public void limpiarTexto(View view) {
        editCarnet.setText("");
        editNombre.setText("");
        editApellido.setText("");
        editCarrera.setText("");
    }
    public void consultarEstudiante(View view){
        helper.abrir();
        Estudiante estudiante = helper.consultarEstudiante(editCarnet.getText().toString());
        helper.cerrar();
        if(estudiante == null)
            Toast.makeText(this, "Estudiante no encontrado", Toast.LENGTH_LONG).show();
        else{
            editCarnet.setText(estudiante.getCarnet());
            editNombre.setText(estudiante.getNombre());
            editApellido.setText(estudiante.getApellido());
            editCarrera.setText(estudiante.getCarrera());
        }
    }

    public void EliminarEstudiante(View view) {
       Estudiante estudiante = new Estudiante();
        estudiante.setCarnet(String.valueOf(editCarnet.getText()));
        estudiante.setNombre(editNombre.getText().toString());
        estudiante.setApellido(editApellido.getText().toString());
        estudiante.setCarrera(editCarrera.getText().toString());
        helper.abrir();
        String regAfectados = helper.eliminar(estudiante);
        helper.cerrar();
        Toast.makeText(this, regAfectados, Toast.LENGTH_SHORT).show();
        limpiarTexto(view);
    }

    public void actualizarEstudiante(View view) {
        Estudiante estudiante = new Estudiante();
        estudiante.setCarnet(String.valueOf(editCarnet.getText()));
        estudiante.setNombre(editNombre.getText().toString());
        estudiante.setApellido(editApellido.getText().toString());
        estudiante.setCarrera(editCarrera.getText().toString());
        helper.abrir();
        String regAfectado = helper.actualizar(estudiante);
        helper.cerrar();
        Toast.makeText(this,regAfectado,Toast.LENGTH_SHORT).show();
    }
    public void actualizarEstudianteWS(View view){
        String carnet = editCarnet.getText().toString();
        String nombres = editNombre.getText().toString().replace(" ","%20");
        String apellidos = editApellido.getText().toString().replace(" ","%20");
        String carrera = editCarrera.getText().toString().replace(" ","%20");
        try {

            String url = urlLocalActualizarEstudiante+"?carnet="+carnet+"&nombres="+nombres+"&apellidos="+apellidos+"&carrera="+carrera;
            String estudianteActualizado = ControladorServicio.actualizarEstudiante(url, this);
            Toast.makeText(getApplicationContext(), estudianteActualizado, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarEstudianteWS(View view) {
        String carnet = editCarnet.getText().toString();
        try {

            String url = urlLocalEliminarEstudiante+"?carnet="+carnet;
            String estudianteActualizado = ControladorServicio.eliminarEstudiante(url, this);
            Toast.makeText(getApplicationContext(), estudianteActualizado, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
