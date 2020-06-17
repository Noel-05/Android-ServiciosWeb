package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ControladorServicio {

    public static  String obtenerRepuestaPeticion(String url, Context ctx){
        String respuesta = " ";

        //Estableciendo tiempo de espera del servicio
        HttpParams parametros = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(parametros, 5000);
        HttpConnectionParams.setSoTimeout(parametros, 5000);

        // Creando objetos de conexion
        HttpClient cliente = new DefaultHttpClient(parametros);
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse httpRespuesta = cliente.execute(httpGet);
            StatusLine estado = httpRespuesta.getStatusLine();
            int codigoEstado = estado.getStatusCode();

            if (codigoEstado == 200) {
                HttpEntity entidad = httpRespuesta.getEntity();
                respuesta = EntityUtils.toString(entidad);
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Error en la conexión.", Toast.LENGTH_LONG).show();

            // Desplegando el error en el LogCat
            Log.v("Error de Conexion", e.toString());
        }

        return respuesta;
    }

    public static String obtenerRespuestaPost(String url, JSONObject obj, Context ctx) {
        String respuesta = " ";

        try {
            // Estableciendo tiempo de espera del servicio
            HttpParams parametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(parametros, 3000);
            HttpConnectionParams.setSoTimeout(parametros, 5000);

            // Creando objetos de conexion
            HttpClient cliente = new DefaultHttpClient(parametros);
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("content-type", "application/json");
            StringEntity nuevaEntidad = new StringEntity(obj.toString());
            httpPost.setEntity(nuevaEntidad);
            Log.v("Peticion", url);
            Log.v("POST", httpPost.toString());
            HttpResponse httpRespuesta = cliente.execute(httpPost);
            StatusLine estado = httpRespuesta.getStatusLine();

            int codigoEstado = estado.getStatusCode();
            if (codigoEstado == 200) {
                respuesta = Integer.toString(codigoEstado);
                Log.v("respuesta", respuesta);
            } else {
                Log.v("respuesta", Integer.toString(codigoEstado));
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Error en la conexion", Toast.LENGTH_LONG).show();

            // Desplegando el error en el LogCat
            Log.v("Error de Conexion", e.toString());
        }

        return respuesta;
    }

    public static String insertarLocalWS(String peticion, Context ctx) {
        String json = obtenerRepuestaPeticion(peticion, ctx);
        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(ctx, "Local ingresado" + resultado.getString("resultado"), Toast.LENGTH_LONG).show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                return "Local ingresado correctamente.";
            else
                return "Error al insertar Local, registro duplicado.";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String actualizarPeriodoInscripcionRevision(String peticion, Context ctx){
        String json = obtenerRepuestaPeticion(peticion, ctx);
        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(ctx, "Periodo Actualizado" + resultado.getString("resultado"), Toast.LENGTH_LONG).show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                return "Periodo actualizado correctamente.";
            else
                return "Error al actualizar Periodo, registro duplicado.";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Evaluacion consultarEvaluacionWS(String json, Context context){
        try {
            JSONArray evaluacionJSON = new JSONArray(json);
            JSONObject obj = evaluacionJSON.getJSONObject(0);

            Evaluacion eval = new Evaluacion();
            eval.setCodCiclo(obj.getString("CODCICLO"));
            eval.setCodTipoEval(obj.getString("IDTIPOEVAL"));
            eval.setCodAsignatura(obj.getString("IDASIGNATURA"));
            eval.setNumeroEvaluacion(obj.getInt("NUMEROEVALUACION"));
            eval.setFechaEvaluacion(obj.getString("FECHAEVALUACION"));

            Toast.makeText(context, "Datos obtenidos correctamente.", Toast.LENGTH_SHORT).show();
            return eval;
        }catch (Exception e){
            Toast.makeText(context, "Error en parseO de JSON", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static String eliminarDocenteWS(String url, Context context){
        String json = obtenerRepuestaPeticion(url, context);
        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(context, "Docente eliminado" + resultado.getString("resultado"), Toast.LENGTH_LONG).show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                return  "Docente eliminado correctamente.";
            else
                return  "Error al eliminar Docente.";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
