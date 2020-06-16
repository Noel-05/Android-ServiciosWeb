package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Diferido_consultar extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    EditText editNumEval, editCarnet, editCodMateria, editGT, editGD, editGL, editFechaEval, editHoraEval, editOtroMotivo, estadoSoli, ciclo;
    ControladorBase helper;
    TextView lblMateria, lblTipoEva, lblGT,lblGD,lblGL, lblFecha, lblHora, lblMotivo, lblOtro, lblEstado;
    Button eliminarBtn, modificarBtn;
    Spinner tipoEval, motivos;
    private int nYearIni, nMonthIni, nDayIni, sYearIni, sMonthIni, sDayIni, sHour, nHour, sMinute, nMinute;
    static final int DATE_ID = 0, HOUR_ID=1;
    Calendar c = Calendar.getInstance();
    String[] tipos ={"Seleccione el tipo de evaluacion","EP","ED","EL"};
    FrameLayout listDetail;
    private final String urlLocal = "http://192.168.1.8/ws_solicitud_diferido_query.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diferido_consultar);
        helper = new ControladorBase(this);
        editNumEval = (EditText) findViewById(R.id.editNumeval);
        editCarnet = (EditText) findViewById(R.id.editCarnet);
        editCodMateria = (EditText) findViewById(R.id.editAsignatura);
        editGT = (EditText) findViewById(R.id.editGrupoTeorico);
        editGD = (EditText) findViewById(R.id.editGrupoDiscusion);
        editGL = (EditText) findViewById(R.id.editGrupoLab);
        editFechaEval = (EditText) findViewById(R.id.editFechaRealizada);
        editHoraEval = (EditText) findViewById(R.id.editHoraRealizada);
        editOtroMotivo = (EditText) findViewById(R.id.editMotivo);
        estadoSoli = findViewById(R.id.editEstadoSolicitud);
        tipoEval=(Spinner) findViewById(R.id.spinTipoEval);
        motivos = (Spinner) findViewById(R.id.spinMotivos);
        ciclo = findViewById(R.id.editCodciclo);
        tipoEval.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,tipos));
        sMonthIni = c.get(Calendar.MONTH);
        sDayIni = c.get(Calendar.DAY_OF_MONTH);
        sYearIni = c.get(Calendar.YEAR);
        sHour = c.get(Calendar.HOUR_OF_DAY);
        sMinute = c.get(Calendar.MINUTE);
        listDetail = findViewById(R.id.layoutDetail);


        editFechaEval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(DATE_ID);
            }
        });
        editHoraEval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(HOUR_ID);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    public void consultarSolicitudDiferido(View view) {
        helper.abrir();
        SolicitudDiferido solicitudDiferido = helper.consultarSolicitudDiferido(editCarnet.getText().toString(),editCodMateria.getText().toString(),ciclo.getText().toString(),tipoEval.getSelectedItem().toString(), editNumEval.getText().toString());
        helper.cerrar();
        if(solicitudDiferido == null)
            Toast.makeText(this, "Solicitud no encontrada", Toast.LENGTH_LONG).show();
        else{
            listDetail.setVisibility(View.VISIBLE);
            editCarnet.setEnabled(false);
            editNumEval.setEnabled(false);
            editCodMateria.setEnabled(false);
            tipoEval.setEnabled(false);
            editOtroMotivo.setText(solicitudDiferido.getOtroMotivo());
            editCodMateria.setText(solicitudDiferido.getCodMateria());
            editGT.setText(solicitudDiferido.getGT());
            editGD.setText(solicitudDiferido.getGD());
            editGL.setText(solicitudDiferido.getGL());
            editFechaEval.setText(solicitudDiferido.getFechaEva());
            editHoraEval.setText(solicitudDiferido.getHoraEva());
            tipoEval.setSelection(tipoEval(solicitudDiferido.getTipoEva()));
            motivos.setSelection(colocarMotivo(solicitudDiferido.getMotivo()));
            if (solicitudDiferido.getOtroMotivo().isEmpty()){
                editOtroMotivo.setVisibility(View.GONE);
                lblOtro.setVisibility(View.GONE);
            }
            estadoSoli.setText(solicitudDiferido.getEstado());


        }


    }
    private void colocar_fecha() {
        if (String.valueOf(nMonthIni).length() == 1 && String.valueOf(nDayIni).length() == 1){
            editFechaEval.setText( nYearIni + "-0" + nMonthIni + "-0" + nDayIni );
        }else if (String.valueOf(nMonthIni).length() == 1){
            editFechaEval.setText( nYearIni + "-0" + nMonthIni + "-" + nDayIni );
        }else if (String.valueOf(nDayIni).length() == 1) {
            editFechaEval.setText( nYearIni + "-" + nMonthIni + "-0" + nDayIni );
        }else {
            editFechaEval.setText( nYearIni + "-" + nMonthIni + "-" + nDayIni );
        }
    }

    private void colocarHora(){
        if (String.valueOf(nMinute).length() == 1 && String.valueOf(nHour).length() == 1){
            editHoraEval.setText("0"+nHour + ":0"+nMinute+":00");
        }
        else if (String.valueOf(nHour).length()==1){
            editHoraEval.setText("0"+nHour + ":"+nMinute+":00");
        }else if (String.valueOf(nMinute).length()==1){
            editHoraEval.setText(nHour + ":0" + nMinute + ":00");
        }else {editHoraEval.setText(nHour + ":" + nMinute + ":00");}
    }
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            nHour = hourOfDay;
            nMinute = minute;
            colocarHora();
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    nYearIni = year;
                    nMonthIni = monthOfYear;
                    nDayIni = dayOfMonth;
                    colocar_fecha();
                }
            };
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_ID:
                return new DatePickerDialog(this, mDateSetListener, sYearIni, sMonthIni, sDayIni);
            case HOUR_ID:
                return new TimePickerDialog(this, mTimeSetListener,nHour,nMinute,true);
        }
        return null;
    }

    public void limpiarTexto(View view) {
        editCarnet.setText("");
        editNumEval.setText("");
        editCodMateria.setText("");
        listDetail.setVisibility(View.GONE);
        editGT.setText("");
        editGD.setText("");
        editGL.setText("");
        editFechaEval.setText("");
        editHoraEval.setText("");
        editOtroMotivo.setText("");
        tipoEval.setSelection(0);
        motivos.setSelection(0);
        editCarnet.setEnabled(true);
        editNumEval.setEnabled(true);
        editCodMateria.setEnabled(true);
        tipoEval.setEnabled(true);
        estadoSoli.setText("");
        ciclo.setText("");
    }

    public void EliminarSolicitud(View view) {
        SolicitudDiferido solicitudDiferido = new SolicitudDiferido();

        solicitudDiferido.setCarnet(String.valueOf(editCarnet.getText()));
        solicitudDiferido.setNumeroEval(Integer.parseInt(String.valueOf(editNumEval.getText())));
        solicitudDiferido.setCodMateria(String.valueOf(editCodMateria.getText()));
        solicitudDiferido.setCiclo(ciclo.getText().toString());
        solicitudDiferido.setTipoEva(String.valueOf(tipoEval.getSelectedItem()));
        helper.abrir();
        String regAfectados = helper.eliminar(solicitudDiferido);
        helper.cerrar();
        Toast.makeText(this, regAfectados, Toast.LENGTH_SHORT).show();
        limpiarTexto(view);
    }

    public void ModificarSolicitudDiferido(View view) {
        SolicitudDiferido solicitudDiferido = new SolicitudDiferido();
        solicitudDiferido.setCarnet(String.valueOf(editCarnet.getText()));
        solicitudDiferido.setCodMateria(String.valueOf(editCodMateria.getText()));
        solicitudDiferido.setCiclo(ciclo.getText().toString());
        solicitudDiferido.setNumeroEval(Integer.parseInt(String.valueOf(editNumEval.getText())));
        solicitudDiferido.setTipoEva(String.valueOf(tipoEval.getSelectedItem()));
        solicitudDiferido.setGT(editGT.getText().toString());
        solicitudDiferido.setGD(editGD.getText().toString());
        solicitudDiferido.setGL(editGL.getText().toString());
        solicitudDiferido.setFechaEva(editFechaEval.getText().toString());
        solicitudDiferido.setHoraEva(editHoraEval.getText().toString());
        solicitudDiferido.setMotivo(motivos.getSelectedItem().toString());
        solicitudDiferido.setOtroMotivo(editOtroMotivo.getText().toString());
        helper.abrir();
        String regAfectados = helper.actualizar(solicitudDiferido);
        helper.cerrar();
        Toast.makeText(this, regAfectados, Toast.LENGTH_SHORT).show();

    }

    public void consultarSolicitudWS(View v) {
        String carnet = editCarnet.getText().toString();
        String codmateria = editCodMateria.getText().toString();
        String codciclo = ciclo.getText().toString();
        String tipoevaluacion = tipoEval.getSelectedItem().toString();
        String numEval = editNumEval.getText().toString();
        String estado = "Pendiente";
        String url = urlLocal + "?carnet=" + carnet+"&codmateria="+codmateria+"&ciclo="+codciclo+"&tipoeval="+tipoevaluacion+"&numeval="+numEval+"&estado="+estado;
        String solicitudWS = ControladorServicio.obtenerRepuestaPeticion(url, this);
        SolicitudDiferido solicitudDiferido = ControladorServicio.consultarSolicitudWS(solicitudWS,this);
        if (solicitudDiferido!= null){
            listDetail.setVisibility(View.VISIBLE);
            editGT.setText(solicitudDiferido.getGT());
            editGD.setText(solicitudDiferido.getGD());
            editGL.setText(solicitudDiferido.getGL());
            motivos.setSelection(colocarMotivo(solicitudDiferido.getMotivo()));
            editFechaEval.setText(solicitudDiferido.getFechaEva());
            editHoraEval.setText(solicitudDiferido.getHoraEva());
            editOtroMotivo.setText(solicitudDiferido.getOtroMotivo());
            estadoSoli.setText(solicitudDiferido.getEstado());
        }else {
            Toast.makeText(getApplicationContext(), "Error al recuperar datos",Toast.LENGTH_SHORT).show();
        }

    }

    public int tipoEval(String tipo){
        switch (tipo){
            case "EP":
                return 1;
            case "ED":
                return 2;
            case "EL":
                return 3;
            default: return 0;
        }
    }
    public int colocarMotivo(String motivo){
        switch (motivo){
            case "Salud":
                return 1;
            case "Trabajo":
                return 2;
            case "Interferencia":
                return 3;
            case "Viaje":
                return 4;
            case "Duelo":
                return 5;
            case "Otro":
                return 6;
            default: return 0;
        }
    }

}
