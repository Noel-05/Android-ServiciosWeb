package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Evaluacion_consultar extends Activity {
    @SuppressLint("SetTextI18n")

    EditText editCodasignatura, editCodciclo, editNumeval, editFechaeval, editNomasignatura;
    Spinner spinTipoeval;
    ControladorBase helper;
    FrameLayout listDetail;
    private final String urLocal = "http://169.254.99.89/ws_consultar_evaluacion.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluacion_consultar);
        helper = new ControladorBase(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        editCodasignatura = (EditText) findViewById(R.id.editCodasignatura);
        editCodciclo = (EditText) findViewById(R.id.editCodciclo);
        editNumeval = (EditText) findViewById(R.id.editNumeval);
        editFechaeval = (EditText) findViewById(R.id.editFechaeval);
        editNomasignatura = (EditText) findViewById(R.id.editNomasignatura);
        spinTipoeval = (Spinner) findViewById(R.id.spinTipoEval);

        listDetail = findViewById(R.id.layoutDetail);
    }

    public void consultarEvaluacion(View v){
        Evaluacion eval = new Evaluacion();

        if(!editNumeval.getText().toString().isEmpty()){
            eval.setNumeroEvaluacion(Integer.parseInt(editNumeval.getText().toString()));
        }else{
            eval.setNumeroEvaluacion(0);
        }

        //Validacion de los Spinner para guardar los codigos.
        if(spinTipoeval.getSelectedItem().toString().equals("Examen Parcial")){
            String tipoEval = "EP";
            eval.setCodTipoEval(tipoEval);
        }else if(spinTipoeval.getSelectedItem().toString().equals("Examen Discusion")){
            String tipoEval = "ED";
            eval.setCodTipoEval(tipoEval);
        }else if(spinTipoeval.getSelectedItem().toString().equals("Examen Laboratorio")){
            String tipoEval = "EL";
            eval.setCodTipoEval(tipoEval);
        }else{
            eval.setCodTipoEval("");
        }

        helper.abrir();
        Evaluacion evaluacion = helper.consultarEvaluacion(editCodasignatura.getText().toString(), editCodciclo.getText().toString(), eval.getCodTipoEval(), eval.getNumeroEvaluacion());
        Asignatura asignatura = helper.consultarNomAsignatura(editCodasignatura.getText().toString());
        helper.cerrar();
        if(evaluacion == null){
            Toast.makeText(this, "Evaluacion no registrada", Toast.LENGTH_LONG).show();
        }else{
            editFechaeval.setText(evaluacion.getFechaEvaluacion());
            editNomasignatura.setText(asignatura.getNomasignatura());
        }
    }

    public void consultarEvaluacionWS(View v){
        String tipoEval = "%20";
        String ciclo = editCodciclo.getText().toString();
        String numeroevaluacion = editNumeval.getText().toString();
        String asignatura = editCodasignatura.getText().toString();
        int eval = 0;

        if(!editNumeval.getText().toString().isEmpty()){
            eval = Integer.parseInt(editNumeval.getText().toString());
        }

        //Validacion de los Spinner para guardar los codigos.
        if(spinTipoeval.getSelectedItem().toString().equals("Examen Parcial")){
            tipoEval = "EP";
        }else if(spinTipoeval.getSelectedItem().toString().equals("Examen Discusion")){
            tipoEval = "ED";
        }else if(spinTipoeval.getSelectedItem().toString().equals("Examen Laboratorio")){
            tipoEval = "EL";
        }

        String url = urLocal + "?codciclo=" +ciclo+ "&idtipoeval=" +tipoEval+ "&codasignatura=" +asignatura+ "&numeroevaluacion=" +numeroevaluacion;
        String solicitudWS = ControladorServicio.obtenerRepuestaPeticion(url, this);
        Evaluacion evaluacion = ControladorServicio.consultarEvaluacionWS(solicitudWS, this);

        if(evaluacion != null){
            listDetail.setVisibility(View.VISIBLE);
            editFechaeval.setText(evaluacion.getFechaEvaluacion());
            editNomasignatura.setText(evaluacion.getCodAsignatura());
        }else{
            Toast.makeText(getApplicationContext(), "Error al recuperar la Evaluación.", Toast.LENGTH_SHORT).show();
        }
    }

    public void limpiarTexto(View v){
        editCodasignatura.setText("");
        editCodciclo.setText("");
        spinTipoeval.setSelection(0);
        editNumeval.setText("");
        editFechaeval.setText("");
        editNomasignatura.setText("");
    }
}
