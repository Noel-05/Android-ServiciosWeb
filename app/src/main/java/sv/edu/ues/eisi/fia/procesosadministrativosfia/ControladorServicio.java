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

    public static void insertarNotaLocalWS(String peticion, Context ctx) {
        String json = obtenerRepuestaPeticion(peticion, ctx);
        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(ctx, "Local ingresado" + resultado.getJSONArray("resultado").toString(), Toast.LENGTH_LONG).show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                    Toast.makeText(ctx, "Local ingresado correctamente.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ctx, "Error al insertar Local, registro duplicado.", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static String insertarMateriaWS(String peticion, Context ctx) {
        String json = obtenerRepuestaPeticion(peticion, ctx);

        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(ctx, "Materia registrada" + resultado.getJSONArray("resultado").toString(), Toast.LENGTH_LONG).show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                return  "Materia ingresada correctamente.";
            else
                return  "Error al insertar Materia, registro duplicado.";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String actualizarEstudiante(String url, Context context) {
        String json = obtenerRepuestaPeticion(url, context);
        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(context, "Estudiante actualiado" + resultado.getJSONArray("resultado").toString(), Toast.LENGTH_LONG).show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                return  "Estudiante actualizado correctamente.";
            else
                return  "Error al actualizar estudiante";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String eliminarEstudiante(String url, Context context) {
        String json = obtenerRepuestaPeticion(url, context);
        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(context, "Estudiante eliminado" + resultado.getJSONArray("resultado").toString(), Toast.LENGTH_LONG).show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                return  "Estudiante eliminado correctamente.";
            else
                return  "Error al eliminar estudiante";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SolicitudDiferido consultarSolicitudWS(String json, Context context) {
        try {
            JSONArray SoliJSON = new JSONArray(json);

                JSONObject obj = SoliJSON.getJSONObject(0);
                SolicitudDiferido soli = new SolicitudDiferido();
                soli.setCarnet(obj.getString("CARNET"));
                soli.setCiclo(obj.getString("CODCICLO"));
                soli.setTipoEva(obj.getString("IDTIPOEVAL"));
                soli.setCodMateria(obj.getString("IDASIGNATURA"));
                soli.setNumeroEval(obj.getInt("NUMEROEVALUACION"));
                soli.setFechaEva(obj.getString("FECHAEVALUACIONSD"));
                soli.setHoraEva(obj.getString("HORAEVALUACIONSD"));
                soli.setMotivo(obj.getString("IDMOTIVODIFERIDO"));
                soli.setOtroMotivo(obj.getString("DESCRIPCIONMOTIVO"));
                soli.setGT(obj.getString("GT"));
                soli.setGD(obj.getString("GD"));
                soli.setGL(obj.getString("GT"));
                soli.setEstado(obj.getString("ESTADOSOLICITUD"));
            return soli;
        } catch (Exception e) {
            Toast.makeText(context, "Error en parseO de JSON", Toast.LENGTH_LONG).show();
            return null;
        }    }
}
