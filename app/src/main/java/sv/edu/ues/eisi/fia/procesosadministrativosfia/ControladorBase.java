package sv.edu.ues.eisi.fia.procesosadministrativosfia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Criteria;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

import java.sql.Date;
import java.util.ArrayList;

public class ControladorBase {

    private static final String[] camposUsuario = {"username", "password", "nombre_usuario"};
    private static final String[] camposEvaluacion = {"codasignatura", "codciclo", "codtipoeval", "numeroeval", "fechaevaluacion"};
    private static final String[] camposLocal = {"codlocal", "nomlocal", "ubicacionlocal"};
    private static final String[] camposPerInscRev = {"fechadesde", "fechahasta", "fecharevision", "horarevision", "codtiporevision", "coddocente", "codlocal", "codasignatura", "codtipoeval", "codciclo", "numeroeval"};
    private static final String[] camposPrimerRevision = {"estadoprimerrevision", "notadespuesprimerarevision", "asistio", "observacionesprimerarev", "coddocente", "carnet", "codasignatura", "codciclo", "codtipoeval", "numeroeval", "codtiporevision", "codmotivocambionota"};


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public ControladorBase(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String NOMBRE_BASE = "ProcesosAdmin.s3db";
        private static final int VERSION = 4;

        public DatabaseHelper(@Nullable Context context) {
            super(context, NOMBRE_BASE, null, VERSION);

        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL("CREATE TABLE usuario(username VARCHAR(7) NOT NULL PRIMARY KEY, password VARCHAR(10), nombre_usuario VARCHAR (256));");

                db.execSQL("CREATE TABLE asignatura(codasignatura VARCHAR(6) NOT NULL PRIMARY KEY, nomasignatura VARCHAR(30) NOT NULL, unidadesval VARCHAR(1));");
                db.execSQL("CREATE TABLE docente(coddocente VARCHAR(10) NOT NULL PRIMARY KEY, nombredocente VARCHAR(50) NOT NULL, apellidodocente VARCHAR(50));");
                db.execSQL("CREATE TABLE ciclo(codciclo VARCHAR(5) NOT NULL PRIMARY KEY, fechadesde DATE, fechahasta DATE);");

                db.execSQL("CREATE TABLE tipoevaluacion(codtipoeval VARCHAR(2) NOT NULL PRIMARY KEY, nomtipoeval VARCHAR(20) NOT NULL);");
                db.execSQL("CREATE TABLE tiporevision(codtiporevision VARCHAR(2) PRIMARY KEY NOT NULL, nomtiporevision VARCHAR(30) NOT NULL);");
                db.execSQL("CREATE TABLE local(codlocal VARCHAR(10) NOT NULL PRIMARY KEY, nomlocal VARCHAR(30) NOT NULL, ubicacionlocal VARCHAR(30));");
                db.execSQL("CREATE TABLE evaluacion(codasignatura VARCHAR(6) NOT NULL, codciclo VARCHAR(5) NOT NULL, codtipoeval VARCHAR(2) NOT NULL, numeroeval INTEGER NOT NULL, fechaevaluacion DATE, " +
                        "PRIMARY KEY(codasignatura, codciclo, codtipoeval, numeroeval));");

                db.execSQL("CREATE TABLE periodoinscripcionrevision(fechadesde DATE, fechahasta DATE, fecharevision DATE, horarevision TIME, " +
                        "codtiporevision VARCHAR(2) NOT NULL, coddocente VARCHAR(10) NOT NULL, codlocal VARCHAR(10) NOT NULL, " +
                        "codasignatura VARCHAR(6) NOT NULL, codciclo VARCHAR(5) NOT NULL, codtipoeval VARCHAR(2) NOT NULL, numeroeval INTEGER NOT NULL, " +
                        "PRIMARY KEY(codtiporevision, codasignatura, codciclo, codtipoeval, numeroeval));");
                db.execSQL("CREATE TABLE primerrevision(estadoprimerrevision VARCHAR(15) NOT NULL, notadespuesprimerarevision REAL NOT NULL, asistio VARCHAR(2) NOT NULL, observacionesprimerarev VARCHAR(200), coddocente VARCHAR(10) NOT NULL," +
                        "carnet VARCHAR(7) NOT NULL, codasignatura VARCHAR(6) NOT NULL, codciclo VARCHAR(5) NOT NULL, codtipoeval VARCHAR(2) NOT NULL, numeroeval INTEGER NOT NULL, codtiporevision VARCHAR(2) NOT NULL, codmotivocambionota VARCHAR(10) NOT NULL," +
                        "PRIMARY KEY(coddocente, carnet, codtiporevision, codasignatura, codciclo, codtipoeval, numeroeval));");

                db.execSQL("CREATE TABLE motivocambionota(codmotivocambionota VARCHAR(10) NOT NULL PRIMARY KEY, motivo VARCHAR(200) NOT NULL);");
                db.execSQL("CREATE TABLE solicitudrevision(fechasolicitudrevision DATE, notaantesrevision REAL NOT NULL, codtipogrupo VARCHAR(2) NOT NULL, numerogrupo INTEGER NOT NULL, motivorevision VARCHAR(200)," +
                        "carnet VARCHAR(7) NOT NULL, codasignatura VARCHAR(6) NOT NULL, codciclo VARCHAR(5) NOT NULL, codtipoeval VARCHAR(2) NOT NULL, numeroeval INTEGER NOT NULL, codtiporevision VARCHAR(2) NOT NULL," +
                        "PRIMARY KEY(carnet, codtiporevision, codasignatura, codciclo, codtipoeval, numeroeval));");

                /*
                 *
                 * Sector de creacion de tablas sin llaves foraneas
                 *
                 */
                db.execSQL("CREATE TABLE Estudiante(carnet VARCHAR(7) NOT NULL PRIMARY KEY, nombreEstudiante VARCHAR(50), apellidoEstudiante VARCHAR(50), carrera VARCHAR(50))");
                db.execSQL("CREATE TABLE TipoDiferidoRepetido(idTipoDiferidoRepetido CHARACTER(10) NOT NULL PRIMARY KEY, nombreTipo VARCHAR(50));");
                db.execSQL("CREATE TABLE MotivoDiferido(nombreMotivo CHARACTER(13) NOT NULL PRIMARY KEY)");
                //Finaliza sector de tablas sin llaves foraneas

                /*
                 *
                 * Sector de creacion de tablas con llaves foraneas
                 *
                 */


                db.execSQL("CREATE TABLE DetalleDiferidoRepetido(idDetalleDiferidoRepetido CHARACTER(25) NOT NULL PRIMARY KEY,idLocal CHARACTER(10) NOT NULL, numEval INTEGER NOT NULL,tipoEval CHARACTER(2) NOT NULL,codAsignatura CHARACTER(6), idDocente CHARACTER(10) NOT NULL, idTipoDiferidoRepetido CHARACTER(10) NOT NULL, fechaDesde DATE NOT NULL, fechaHasta DATE NOT NULL, fechaRealizacion DATE NOT NULL, horaRealizacion TIME NOT NULL);");
                db.execSQL("CREATE TABLE DetalleEstudianteDiferido(carnet CHARACTER(7) NOT NULL, idDetalleDiferidoRepetido CHARACTER(10) NOT NULL,FechaInscripcionDiferido DATE NOT NULL ,PRIMARY KEY(carnet,idDetalleDiferidoRepetido))");
                db.execSQL("CREATE TABLE DetalleEstudianteRepetido(carnet CHARACTER(7) NOT NULL, idDetalleDiferidoRepetido CHARACTER(10) NOT NULL, fechaInscripcionRepetido DATE NOT NULL, PRIMARY KEY(carnet, idDetalleDiferidoRepetido))");
                db.execSQL("CREATE TABLE SolicitudDiferido(idSolicitudDiferido NOT NULL, carnet VARCHAR(7) NOT NULL, numeroeval INTEGER NOT NULL, idMotivoDiferido CHARACTER(13) NOT NULL, fechaEvaluacion DATE NOT NULL,  horaEvaluacion TIME NOT NULL, descripcionMotivo VARCHAR(256), idAsignatura CHARACTER(6) NOT NULL, GT NUMERIC(2,0) NOT NULL, GD NUMERIC(2,0) NOT NULL, GL NUMERIC(2,0), tipoEvaluacion CHARACTER(2), estadoSolicitud CHARACTER(10) NOT NULL, PRIMARY KEY(idSolicitudDiferido, carnet, idAsignatura, tipoEvaluacion,numeroeval) )");
                db.execSQL("CREATE TRIGGER AprobarSolicitudDiferido AFTER UPDATE  ON SolicitudDiferido FOR EACH ROW WHEN NEW.estadoSolicitud = 'Aprobada'" +
                        " AND OLD.estadoSolicitud = 'Pendiente' BEGIN " +
                        "INSERT INTO DetalleEstudianteDiferido(carnet,idDetalleDiferidoRepetido,FechaInscripcionDiferido) " +
                        "VALUES(OLD.carnet,OLD.idAsignatura||OLD.tipoEvaluacion||OLD.numeroeval||'Diferido',CURRENT_DATE ); END;");
                //Finaliza sector de tablas con llaves foraneas
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //UPDATE DATABASE COMMANDS
            //if (oldVersion <= VERSION2 && newVersion >= VERSION)
        }
    }

    public void abrir() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return;
    }

    public void cerrar() {
        db.close();
    }


    public boolean consultarUsuario(String username, String password) {
        String[] id = {username};
        Cursor cursor = db.rawQuery("select * from usuario where username ='" + username + "' and password ='" + password + "';", null);
        if (cursor.moveToFirst() == true) {
            String user = cursor.getString(0);
            String pass = cursor.getString(1);
            cerrar();
            if (user.equals(username) && pass.equals(password)) {
                return true;
            } else return false;
        } else return false;
    }

    public String insertar(Usuario user) {
        String regAfectados = "Registro insertado Nª= ";
        long contador = 0;

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user.getUsername());
        contentValues.put("password", user.getPassword());
        contentValues.put("nombre_usuario", user.getNombreUsuario());
        contador = db.insert("usuario", null, contentValues);
        if (contador == -1 || contador == 0) {
            regAfectados = "Error al Insertar el registro, Registro duplicado. Verificar inserción";
        } else {
            regAfectados = regAfectados + contador;
        }
        return regAfectados;
    }

    public String insertar(SolicitudDiferido solicitudDiferido) {
        String regAfectados = "Registro insertado Nª= ";
        long contador = 0;
        if (verificarIntegridadReferencial(solicitudDiferido, 5)) {

            ContentValues contentValues = new ContentValues();
            contentValues.put("idSolicitudDiferido", solicitudDiferido.getIdSolicitud());
            contentValues.put("carnet", solicitudDiferido.getCarnet());
            contentValues.put("numeroeval", solicitudDiferido.getNumeroEval());
            contentValues.put("tipoEvaluacion", solicitudDiferido.getTipoEva());
            contentValues.put("idMotivoDiferido", solicitudDiferido.getMotivo());
            contentValues.put("fechaEvaluacion", solicitudDiferido.getFechaEva());
            contentValues.put("horaEvaluacion", solicitudDiferido.getHoraEva());
            contentValues.put("descripcionMotivo", solicitudDiferido.getOtroMotivo());
            contentValues.put("GT", solicitudDiferido.getGT());
            contentValues.put("GD", solicitudDiferido.getGD());
            contentValues.put("GL", solicitudDiferido.getGL());
            contentValues.put("idAsignatura", solicitudDiferido.getCodMateria());
            contentValues.put("descripcionMotivo", solicitudDiferido.getOtroMotivo());
            contentValues.put("estadoSolicitud", solicitudDiferido.getEstado());
            contador = db.insert("SolicitudDiferido", null, contentValues);
            if (contador == -1 || contador == 0) {
                regAfectados = "Error al Insertar el registro, Registro duplicado. Verificar inserción";
            } else {
                regAfectados = regAfectados + contador;
            }
        }
        return regAfectados;
    }

    public String actualizar(SolicitudDiferido solicitudDiferido) {
        if (verificarIntegridadReferencial(solicitudDiferido, 5)) {
            String[] id = {solicitudDiferido.getIdSolicitud()};
            ContentValues cv = new ContentValues();
            cv.put("GT", solicitudDiferido.getGT());
            cv.put("GD", solicitudDiferido.getGD());
            cv.put("GL", solicitudDiferido.getGL());
            cv.put("idMotivoDiferido", solicitudDiferido.getMotivo());
            cv.put("fechaEvaluacion", solicitudDiferido.getFechaEva());
            cv.put("horaEvaluacion", solicitudDiferido.getHoraEva());
            cv.put("descripcionMotivo", solicitudDiferido.getOtroMotivo());
            db.update("SolicitudDiferido", cv, "idSolicitudDiferido = ?", id);
            return "Registro Actualizado Correctamente";
        } else return "Registro no existe";
    }

    public String insertar(DetalleDiferidoRepetido detalle) {
        String regAfectados = "Registro insertado Nº= ";
        long contador = 0;
        if (verificarIntegridadReferencial(detalle, 7)) {

            ContentValues contentValues = new ContentValues();
            contentValues.put("idDetalleDiferidoRepetido", detalle.getIdDetalle());
            contentValues.put("idLocal", detalle.getIdLocal());
            contentValues.put("numEval", detalle.getNumEval());
            contentValues.put("tipoEval", detalle.getIdTipoEval());
            contentValues.put("codAsignatura", detalle.getIdAsignatura());
            contentValues.put("idDocente", detalle.getIdDocente());
            contentValues.put("idTipoDiferidoRepetido", detalle.getIdTipoDifRep());
            contentValues.put("fechaDesde", detalle.getFechaDesde());
            contentValues.put("fechaHasta", detalle.getFechaHasta());
            contentValues.put("fechaRealizacion", detalle.getFechaRealizacion());
            contentValues.put("horaRealizacion", detalle.getHoraRealizacion());
            contador = db.insert("DetalleDiferidoRepetido", null, contentValues);
        }
        if (contador == -1 || contador == 0) {
            regAfectados = "Error al Insertar el registro, Registro duplicado. Verificar inserción";
        } else {
            regAfectados = regAfectados + contador;
        }
        return regAfectados;
    }

    public DetalleDiferidoRepetido consultarDetalleDifRep(String materia, String tipoEval, Integer numeroEval, String tipoDifRep) {
        String[] id = {materia + tipoEval + numeroEval + tipoDifRep};
        Cursor cursor = db.query("DetalleDiferidoRepetido", null, "idDetalleDiferidoRepetido = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            DetalleDiferidoRepetido detalle = new DetalleDiferidoRepetido();
            detalle.setIdDetalle(cursor.getString(0));
            detalle.setIdLocal(cursor.getString(1));
            detalle.setNumEval(cursor.getInt(2));
            detalle.setIdTipoEval(cursor.getString(3));
            detalle.setIdAsignatura(cursor.getString(4));
            detalle.setIdDocente(cursor.getString(5));
            detalle.setIdTipoDifRep(cursor.getString(6));
            detalle.setFechaDesde(cursor.getString(7));
            detalle.setFechaHasta(cursor.getString(8));
            detalle.setFechaRealizacion(cursor.getString(9));
            detalle.setHoraRealizacion(cursor.getString(10));
            return detalle;
        } else return null;
    }

    public String eliminar(DetalleDiferidoRepetido detalleDiferidoRepetido) {
        String regAfectados = "filas afectadas= ";
        int contador = 0;
        contador += db.delete("DetalleDiferidoRepetido", "idDetalleDiferidoRepetido='" + detalleDiferidoRepetido.getIdDetalle() + "'", null);
        regAfectados += contador;
        return regAfectados;
    }

    public String actualizar(DetalleDiferidoRepetido detalle) {
        if (verificarIntegridadReferencial(detalle, 7)) {
            String[] id = {detalle.getIdDetalle()};
            ContentValues cv = new ContentValues();
            cv.put("idLocal", detalle.getIdLocal());
            cv.put("idDocente", detalle.getIdDocente());
            cv.put("fechaDesde", detalle.getFechaDesde());
            cv.put("fechaHasta", detalle.getFechaHasta());
            cv.put("fechaRealizacion", detalle.getFechaRealizacion());
            cv.put("horaRealizacion", detalle.getHoraRealizacion());
            db.update("DetalleDiferidoRepetido", cv, "idDetalleDiferidoRepetido = ?", id);
            return "Registro Actualizado Correctamente";
        } else return "Registro no existe";
    }

    public String insertar(DetalleEstudianteDiferido detalle) {
        String regAfectados = "Registro insertado Nº= ";
        long contador = 0;
        if (verificarIntegridadReferencial(detalle, 8)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("carnet", detalle.getCarnet());
            contentValues.put("idDetalleDiferidoRepetido", detalle.getIdDetalleDifeRep());
            contentValues.put("FechaInscripcionDiferido", detalle.getFechaInscripcionDiferido());
            contador = db.insert("DetalleEstudianteDiferido", null, contentValues);
        }
        if (contador == -1 || contador == 0) {
            regAfectados = "Error al Insertar el registro, Registro duplicado. Verificar inserción";
        } else {
            regAfectados = regAfectados + contador;
        }
        return regAfectados;
    }

    public String insertar(DetalleEstudianteRepetido detalle) {
        String regAfectados = "Registro insertado Nº= ";
        long contador = 0;
        if (verificarIntegridadReferencial(detalle, 9)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("carnet", detalle.getCarnet());
            contentValues.put("idDetalleDiferidoRepetido", detalle.getIdDetalleDifRep());
            contentValues.put("FechaInscripcionRepetido", detalle.getFechaInscripcion());
            contador = db.insert("DetalleEstudianteRepetido", null, contentValues);
        }
        if (contador == -1 || contador == 0) {
            regAfectados = "Error al Insertar el registro, Registro duplicado. Verificar inserción";
        } else {
            regAfectados = regAfectados + contador;
        }
        return regAfectados;
    }

    public DetalleEstudianteRepetido consultarDetallEstudRep(String carnet, String materia, String tipoEval, int numEval) {
        String id = materia + tipoEval + numEval;
        String idcarnet = carnet;
        Cursor cursor = db.query("DetalleEstudianteRepetido", null, "carnet = '" + carnet + "' AND idDetalleDiferidoRepetido = '" + id + "Repetido'", null, null, null, null);
        if (cursor.moveToFirst()) {
            DetalleEstudianteRepetido detalle = new DetalleEstudianteRepetido();
            detalle.setCarnet(cursor.getString(0));
            detalle.setIdDetalleDifRep(cursor.getString(1));
            detalle.setFechaInscripcion(cursor.getString(2));
            return detalle;
        } else return null;
    }

    public String eliminar(DetalleEstudianteRepetido detalleRep) throws SQLException {
        String regAfectados;
        int contador = 0;
        String[] id = {detalleRep.getCarnet(), detalleRep.getIdDetalleDifRep()};
        contador += db.delete("DetalleEstudianteRepetido", "carnet = ? AND idDetalleDiferidoRepetido= ?", id);
        if (!(contador == -1 || contador == 0)) {
            regAfectados = "Se eliminó el registro correctamente";
        } else regAfectados = "No existen registros, revise los datos";
        return regAfectados;
    }

    public String actualizarEstado(SolicitudDiferido solicitudDiferido) throws SQLException {
        if (verificarIntegridadReferencial(solicitudDiferido, 5)) {
            String[] id = {solicitudDiferido.getIdSolicitud()};
            ContentValues contentValues = new ContentValues();
            contentValues.put("estadoSolicitud", solicitudDiferido.getEstado());
            db.update("SolicitudDiferido", contentValues, "idSolicitudDiferido = ?", id);

            return "Registro Actualizado Correctamente";
        } else return "Registro no existe";
    }

    public ArrayList<String> consultarEstudiantesInscritos(String materia, String tipoEval, int numEval, String tipo) {
        ArrayList<String> solicitudes = new ArrayList<>();
        String[] id = {materia + tipoEval + numEval + tipo};
        Cursor cursor = db.query("DetalleEstudiante" + tipo, null, "idDetalleDiferidoRepetido = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            do {

                solicitudes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return solicitudes;
    }

    public DetalleEstudianteDiferido consultarDetallEstuDifIndividual(String carnet, String materia, String tipoEval, int numEval) {
        String[] id = {carnet, materia + tipoEval + numEval + "Diferido"};
        DetalleEstudianteDiferido detalle = new DetalleEstudianteDiferido();
        Cursor cursor = db.query("DetalleEstudianteDiferido", null, "carnet = ? AND idDetalleDiferidoRepetido = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            detalle.setCarnet(cursor.getString(0));
            detalle.setIdDetalleDifeRep(cursor.getString(1));
            detalle.setFechaInscripcionDiferido(cursor.getString(2));
            return detalle;
        } else return null;
    }

    public String insertar(MotivoDiferido motivoDiferido) {
        String regAfectados = "Registro insertado Nº= ";
        long contador = 0;

        ContentValues contentValues = new ContentValues();
        contentValues.put("nombreMotivo", motivoDiferido.getNombreMotivoDiferido());
        contador = db.insert("MotivoDiferido", null, contentValues);
        if (contador == -1 || contador == 0) {
            regAfectados = "Error al Insertar el registro, Registro duplicado. Verificar inserción";
        } else {
            regAfectados = regAfectados + contador;
        }
        return regAfectados;
    }

    public ArrayList<String> consultarSolicitudesPendiente(String materia, String tipoEval, int numeroEval, String estado) {
        ArrayList<String> solicitudes = new ArrayList<>();
        String[] ids = {materia, tipoEval, String.valueOf(numeroEval), estado};
        Cursor cursor = db.rawQuery("SELECT * FROM SolicitudDiferido WHERE idAsignatura ='" + materia + "' AND tipoEvaluacion = '" + tipoEval + "' AND numeroeval = " + numeroEval + " AND estadoSolicitud = '" + estado + "'", null);
        if (cursor.moveToFirst()) {
            do {

                solicitudes.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return solicitudes;
    }

    public SolicitudDiferido consultarSolicitudDiferido(String carnet, String codMateria, String tipoEval, String numEval) {
        String[] id = {carnet + codMateria + tipoEval + numEval};
        Cursor cursor = db.query("SolicitudDiferido", null, "idSolicitudDiferido = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            SolicitudDiferido solicitudDiferido = new SolicitudDiferido();
            solicitudDiferido.setIdSolicitud(cursor.getString(0));
            solicitudDiferido.setCarnet(cursor.getString(1));
            solicitudDiferido.setNumeroEval(cursor.getInt(2));
            solicitudDiferido.setMotivo(cursor.getString(3));
            solicitudDiferido.setFechaEva(cursor.getString(4));
            solicitudDiferido.setHoraEva(cursor.getString(5));
            solicitudDiferido.setOtroMotivo(cursor.getString(6));
            solicitudDiferido.setCodMateria(cursor.getString(7));
            solicitudDiferido.setGT(cursor.getString(8));
            solicitudDiferido.setGD(cursor.getString(9));
            solicitudDiferido.setGL(cursor.getString(10));
            solicitudDiferido.setTipoEva(cursor.getString(11));
            solicitudDiferido.setEstado(cursor.getString(12));
            return solicitudDiferido;
        } else return null;
    }

    public String eliminar(SolicitudDiferido solicitudDiferido) {
        String regAfectados = "filas afectadas= ";
        int contador = 0;
        contador += db.delete("SolicitudDiferido", "idSolicitudDiferido='" + solicitudDiferido.getIdSolicitud() + "'", null);
        regAfectados += contador;
        return regAfectados;
    }

    public String insertar(Estudiante estudiante) {
        String regAfectados = "Registro insertado Nº: ";
        long contador = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put("carnet", estudiante.getCarnet());
        contentValues.put("nombreEstudiante", estudiante.getNombre());
        contentValues.put("apellidoEstudiante", estudiante.getApellido());
        contentValues.put("carrera", estudiante.getCarrera());
        contador = db.insert("Estudiante", null, contentValues);
        if (contador == -1 || contador == 0) {
            regAfectados = "Error al Insertar el registro, Registro duplicado. Verificar inserción";
        } else {
            regAfectados = regAfectados + contador;
        }
        return regAfectados;
    }

    public String actualizar(Estudiante estudiante) {
        if (verificarIntegridadReferencial(estudiante, 6)) {
            String[] id = {estudiante.getCarnet()};
            ContentValues cv = new ContentValues();
            cv.put("nombreEstudiante", estudiante.getNombre());
            cv.put("apellidoEstudiante", estudiante.getApellido());
            cv.put("carrera", estudiante.getCarrera());
            db.update("Estudiante", cv, "carnet = ?", id);
            return "Registro Actualizado Correctamente";
        } else return "Registro no existe";
    }

    public Estudiante consultarEstudiante(String carnet) {
        String[] id = {carnet};
        Cursor cursor = db.query("Estudiante", null, "carnet = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Estudiante estudiante = new Estudiante();
            estudiante.setCarnet(cursor.getString(0));
            estudiante.setNombre(cursor.getString(1));
            estudiante.setApellido(cursor.getString(2));
            estudiante.setCarrera(cursor.getString(3));
            return estudiante;
        } else return null;
    }

    public String eliminar(Estudiante estudiante) {
        String regAfectados = "filas afectadas= ";
        int contador = 0;
        contador += db.delete("Estudiante", "carnet='" + estudiante.getCarnet() + "'", null);
        regAfectados += contador;
        return regAfectados;
    }

    public String insertar(Asignatura asignatura) {
        String regInsertados = "Registro Insertado No. = ";
        long contador = 0;

        ContentValues asig = new ContentValues();
        asig.put("codasignatura", asignatura.getCodasignatura());
        asig.put("nomasignatura", asignatura.getNomasignatura());
        asig.put("unidadesval", asignatura.getUnidadesval());
        contador = db.insert("asignatura", null, asig);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(Ciclo ciclo) {
        String regInsertados = "Registro Insertado No. = ";
        long contador = 0;

        ContentValues cic = new ContentValues();
        cic.put("codciclo", ciclo.getCodciclo());
        cic.put("fechadesde", String.valueOf(ciclo.getFechadesde()));
        cic.put("fechahasta", String.valueOf(ciclo.getFechahasta()));
        contador = db.insert("ciclo", null, cic);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(Docente docente) {
        String regInsertados = "Regitro No. = ";
        long contador = 0;

        ContentValues doc = new ContentValues();
        doc.put("coddocente", docente.getCoddocente());
        doc.put("nombredocente", docente.getNomdocente());
        doc.put("apellidodocente", docente.getApellidodocente());
        contador = db.insert("docente", null, doc);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(MotivoCambioNota motivo) {
        String regInsertados = "Registro No. = ";
        long contador = 0;

        ContentValues mot = new ContentValues();
        mot.put("codmotivocambionota", motivo.getCodmotivocambionota());
        mot.put("motivo", motivo.getMotivo());
        contador = db.insert("motivocambionota", null, mot);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(SolicitudRevision solicitud) {
        String regInsertados = "Registro No. = ";
        long contador = 0;

        ContentValues sol = new ContentValues();
        sol.put("fechasolicitudrevision", String.valueOf(solicitud.getFechasolicitudrevision()));
        sol.put("notaantesrevision", solicitud.getNotaantesrevision());
        sol.put("codtipogrupo", solicitud.getCodtipogrupo());
        sol.put("numerogrupo", solicitud.getNumerogrupo());
        sol.put("motivorevision", solicitud.getMotivorevision());
        sol.put("carnet", solicitud.getCarnet());
        sol.put("codasignatura", solicitud.getCodasignatura());
        sol.put("codciclo", solicitud.getCodciclo());
        sol.put("codtipoeval", solicitud.getCodtipoeval());
        sol.put("numeroeval", solicitud.getNumeroeval());
        sol.put("codtiporevision", solicitud.getCodtiporevision());
        contador = db.insert("solicitudrevision", null, sol);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(TipoRevision tipoRevision) {
        String regInsertados = "Registro Insertado No. = ";
        long contador = 0;

        ContentValues tiprev = new ContentValues();
        tiprev.put("codtiporevision", tipoRevision.getCodTipoRev());
        tiprev.put("nomtiporevision", tipoRevision.getNomTipoRev());
        contador = db.insert("tiporevision", null, tiprev);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Isercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(TipoEvaluacion tipoEval) {
        String regInsertados = "Registro Insertado No. = ";
        long contador = 0;

        ContentValues teval = new ContentValues();
        teval.put("codtipoeval", tipoEval.getCodTipoEval());
        teval.put("nomtipoeval", tipoEval.getNomTipoEval());
        contador = db.insert("tipoevaluacion", null, teval);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(Evaluacion evaluacion) {
        String regInsertados = "Registro Insertado No. = ";
        long contador = 0;

        if (verificarIntegridadReferencial(evaluacion, 1)) {
            ContentValues evaluaciones = new ContentValues();
            evaluaciones.put("codasignatura", evaluacion.getCodAsignatura());
            evaluaciones.put("codciclo", evaluacion.getCodCiclo());
            evaluaciones.put("codtipoeval", evaluacion.getCodTipoEval());
            evaluaciones.put("fechaevaluacion", evaluacion.getFechaEvaluacion());
            evaluaciones.put("numeroeval", evaluacion.getNumeroEvaluacion());
            contador = db.insert("evaluacion", null, evaluaciones);
        }
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public String insertar(Local local) {
        String regInsertados = "Registro No. = ";
        long contador = 0;

        ContentValues locales = new ContentValues();
        locales.put("codlocal", local.getCodlocal());
        locales.put("nomlocal", local.getNomlocal());
        locales.put("ubicacionlocal", local.getUbicacionlocal());
        contador = db.insert("local", null, locales);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        return regInsertados;
    }

    public void insertar(TipoDiferidoRepetido tipo) {
        try {
            long counter = 0;
            ContentValues contentValues = new ContentValues();
            contentValues.put("idTipoDiferidoRepetido", tipo.getIdTipo());
            contentValues.put("nombreTipo", tipo.getNombreTipo());
            counter = db.insert("TipoDiferidoRepetido", null, contentValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String insertar(PeriodoInscripcionRevision perInscRev) {
        String regInsertados = "Registro No. = ";
        long contador = 0;

        if (verificarIntegridadReferencial(perInscRev, 3)) {
            ContentValues inscripciones = new ContentValues();
            inscripciones.put("fechadesde", perInscRev.getFechaDesde());
            inscripciones.put("fechahasta", perInscRev.getFechaHasta());
            inscripciones.put("fecharevision", perInscRev.getFechaRevision());
            inscripciones.put("horarevision", perInscRev.getHoraRevision());
            inscripciones.put("coddocente", perInscRev.getCodDocente());
            inscripciones.put("codasignatura", perInscRev.getCodAsignatura());
            inscripciones.put("codciclo", perInscRev.getCodCiclo());
            inscripciones.put("codtipoeval", perInscRev.getCodTipoEval());
            inscripciones.put("codtiporevision", perInscRev.getTipoRevision());
            inscripciones.put("codlocal", perInscRev.getCodLocal());
            inscripciones.put("numeroeval", perInscRev.getNumeroEval());
            contador = db.insert("periodoinscripcionrevision", null, inscripciones);
        }
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }

    public String insertar(PrimeraRevision primRev) {
        String regInsertados = "Registro No. = ";
        long contador = 0;

        if (verificarIntegridadReferencial(primRev, 5)) {
            ContentValues primerrevision = new ContentValues();
            primerrevision.put("estadoprimerrevision", primRev.getEstadoprimerrevision());
            primerrevision.put("notadespuesprimerarevision", primRev.getNotadespuesprimerarevision());
            primerrevision.put("asistio", primRev.getAsistio());
            primerrevision.put("observacionesprimerarev", primRev.getObservacionesprimerarev());
            primerrevision.put("coddocente", primRev.getCoddocente());
            primerrevision.put("carnet", primRev.getCarnet());
            primerrevision.put("codasignatura", primRev.getCodasignatura());
            primerrevision.put("codciclo", primRev.getCodciclo());
            primerrevision.put("codtipoeval", primRev.getCodtipoeval());
            primerrevision.put("numeroeval", primRev.getNumeroeval());
            primerrevision.put("codtiporevision", primRev.getCodtiporevision());
            primerrevision.put("codmotivocambionota", primRev.getMotivoCambioNota());
            contador = db.insert("primerrevision", null, primerrevision);
        }
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al insertar el registro, Registro Duplicado. Verificar Insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }

    public Asignatura consultarNomAsignatura(String codAsignatura) {
        String[] id = {codAsignatura};
        String[] camposAsigConsul = {"nomasignatura"};
        Cursor cursor = db.query("asignatura", camposAsigConsul, "codasignatura = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Asignatura asig = new Asignatura();
            asig.setNomasignatura(cursor.getString(0));
            return asig;
        } else {
            return null;
        }
    }

    public Docente consultarNomDocente(String codDocente) {
        String[] id = {codDocente};
        String[] camposDocente = {"nombredocente", "apellidodocente"};
        Cursor cursor = db.query("docente", camposDocente, "coddocente = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Docente doc = new Docente();
            doc.setNomdocente(cursor.getString(0));
            doc.setApellidodocente(cursor.getString(1));
            return doc;
        } else {
            return null;
        }
    }

    public MotivoCambioNota consultarMotCambNota(String codMotCambNota) {
        String[] id = {codMotCambNota};
        String[] camposMotCambNota = {"motivo"};
        Cursor cursor = db.query("motivocambionota", camposMotCambNota, "codmotivocambionota = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            MotivoCambioNota motCambio = new MotivoCambioNota();
            motCambio.setMotivo(cursor.getString(0));
            return motCambio;
        } else {
            return null;
        }
    }

    public Local consultarLocal(String codLocal) {
        String[] id = {codLocal};
        Cursor cursor = db.query("local", camposLocal, "codlocal = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Local local = new Local();
            local.setCodlocal(cursor.getString(0));
            local.setNomlocal(cursor.getString(1));
            local.setUbicacionlocal(cursor.getString(2));
            return local;
        } else {
            return null;
        }
    }

    public Evaluacion consultarEvaluacion(String codAsignatura, String codCiclo, String codTipoEval, int numEval) {
        String[] id = {codAsignatura, codCiclo, codTipoEval, String.valueOf(numEval)};
        Cursor cursor = db.query("evaluacion", camposEvaluacion, "codasignatura = ? AND codciclo = ? AND codtipoeval = ? AND numeroeval = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Evaluacion evaluacion = new Evaluacion();
            evaluacion.setCodAsignatura(cursor.getString(0));
            evaluacion.setCodCiclo(cursor.getString(1));
            evaluacion.setCodTipoEval(cursor.getString(2));
            evaluacion.setNumeroEvaluacion(Integer.parseInt(cursor.getString(3)));
            evaluacion.setFechaEvaluacion(cursor.getString(4));
            return evaluacion;
        } else {
            return null;
        }
    }

    public PeriodoInscripcionRevision consultarPeriodoInscripcion(String codTipoRev, String codAsignatura, String codTipoEva, String codCiclo, int numEva) {
        String[] id = {codTipoRev, codAsignatura, codTipoEva, codCiclo, String.valueOf(numEva)};
        Cursor cursor = db.query("periodoinscripcionrevision", camposPerInscRev, "codtiporevision = ? AND codasignatura = ? AND codtipoeval = ? AND codciclo = ? AND numeroeval = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            PeriodoInscripcionRevision perInsRev = new PeriodoInscripcionRevision();
            perInsRev.setFechaDesde(cursor.getString(0));
            perInsRev.setFechaHasta(cursor.getString(1));
            perInsRev.setFechaRevision(cursor.getString(2));
            perInsRev.setHoraRevision(cursor.getString(3));
            perInsRev.setTipoRevision(cursor.getString(4));
            perInsRev.setCodDocente(cursor.getString(5));
            perInsRev.setCodLocal(cursor.getString(6));
            perInsRev.setCodAsignatura(cursor.getString(7));
            perInsRev.setCodCiclo(cursor.getString(8));
            perInsRev.setCodTipoEval(cursor.getString(9));
            perInsRev.setNumeroEval(Integer.parseInt(cursor.getString(10)));
            return perInsRev;
        } else {
            return null;
        }
    }

    public PrimeraRevision consultarPrimerRevision(String codAsignatura, String codCiclo, int numEval, String carnet, String tipoEval) {
        String[] id = {codAsignatura, codCiclo, String.valueOf(numEval), carnet, tipoEval};
        Cursor cursor = db.query("primerrevision", camposPrimerRevision, "codasignatura = ? AND codciclo = ? AND numeroeval = ? AND carnet = ? AND codtipoeval = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            PrimeraRevision primRev = new PrimeraRevision();
            primRev.setEstadoprimerrevision(cursor.getString(0));
            primRev.setNotadespuesprimerarevision(Float.parseFloat(cursor.getString(1)));
            primRev.setAsistio(cursor.getString(2));
            primRev.setObservacionesprimerarev(cursor.getString(3));
            primRev.setCoddocente(cursor.getString(4));
            primRev.setCarnet(cursor.getString(5));
            primRev.setCodasignatura(cursor.getString(6));
            primRev.setCodciclo(cursor.getString(7));
            primRev.setCodtipoeval(cursor.getString(8));
            primRev.setNumeroeval(Integer.parseInt(cursor.getString(9)));
            primRev.setCodtiporevision(cursor.getString(10));
            primRev.setMotivoCambioNota(cursor.getString(11));
            return primRev;
        } else {
            return null;
        }
    }


    public String actualizar(Evaluacion evaluacion) {
        if (verificarIntegridadReferencial(evaluacion, 2)) {
            String[] id = {evaluacion.getCodAsignatura(), evaluacion.getCodCiclo(), evaluacion.getCodTipoEval(), String.valueOf(evaluacion.getNumeroEvaluacion())};
            ContentValues cv = new ContentValues();
            cv.put("fechaevaluacion", evaluacion.getFechaEvaluacion());
            db.update("evaluacion", cv, "codasignatura = ? AND codciclo = ? AND codtipoeval = ? AND numeroeval = ?", id);
            return "Registro Actualizado correctamente";
        } else {
            return "Registro no Existe";
        }
    }

    public String actualizar(Local local) {
        if (verificarIntegridadReferencial(local, 7)) {
            String[] id = {local.getCodlocal()};
            ContentValues cv = new ContentValues();
            cv.put("nomlocal", local.getNomlocal());
            cv.put("ubicacionlocal", local.getUbicacionlocal());
            db.update("local", cv, "codlocal = ? ", id);
            return "Registro Actualizado Correctamente";
        } else {
            return "Registro no Existe";
        }
    }

    public String actualizar(PeriodoInscripcionRevision perInscRev) {
        if (verificarIntegridadReferencial(perInscRev, 4)) {
            String[] id = {perInscRev.getTipoRevision(), perInscRev.getCodAsignatura(), perInscRev.getCodTipoEval(), perInscRev.getCodCiclo(), String.valueOf(perInscRev.getNumeroEval())};
            ContentValues cv = new ContentValues();
            cv.put("coddocente", perInscRev.getCodDocente());
            cv.put("codlocal", perInscRev.getCodLocal());
            cv.put("fechadesde", perInscRev.getFechaDesde());
            cv.put("fechahasta", perInscRev.getFechaHasta());
            cv.put("fecharevision", perInscRev.getFechaRevision());
            cv.put("horarevision", perInscRev.getHoraRevision());
            db.update("periodoinscripcionrevision", cv, "codtiporevision = ? AND codasignatura = ? AND codtipoeval = ? AND codciclo = ?  AND numeroeval = ?", id);
            return "Registro Actualizado correctamente";
        } else {
            return "Registro no Existe";
        }
    }

    public String actualizar(PrimeraRevision primRev) {
        if (verificarIntegridadReferencial(primRev, 6)) {
            String[] id = {primRev.getCoddocente(), primRev.getCarnet(), primRev.getCodasignatura(), primRev.getCodtiporevision(), primRev.getCodciclo(), primRev.getCodtipoeval(), String.valueOf(primRev.getNumeroeval())};
            ContentValues cv = new ContentValues();
            cv.put("estadoprimerrevision", primRev.getEstadoprimerrevision());
            cv.put("asistio", primRev.getAsistio());
            cv.put("notadespuesprimerarevision", primRev.getNotadespuesprimerarevision());
            cv.put("codmotivocambionota", primRev.getMotivoCambioNota());
            cv.put("observacionesprimerarev", primRev.getObservacionesprimerarev());
            db.update("primerrevision", cv, "coddocente = ? AND carnet = ? AND codasignatura = ? AND codtiporevision = ? AND codciclo = ? AND codtipoeval = ? AND numeroeval = ?", id);
            return "Registro Actualizado Correctamente";
        } else {
            return "Registro no Existe";
        }
    }

    public String eliminar(Evaluacion evaluacion) {
        String regAfectados = "Filas afectadas = ";
        int contador = 0;

        String where = "codasignatura = '" + evaluacion.getCodAsignatura() + "'";
        where = where + "AND codciclo = '" + evaluacion.getCodCiclo() + "'";
        where = where + "AND codtipoeval = '" + evaluacion.getCodTipoEval() + "'";
        where = where + "AND numeroeval = '" + evaluacion.getNumeroEvaluacion() + "'";
        contador += db.delete("evaluacion", where, null);
        regAfectados += contador;
        return regAfectados;
    }

    public String eliminar(Local local) {
        String regAfectados = "Filas afectadas = ";
        int contador = 0;

        String where = "codlocal = '" + local.getCodlocal() + "'";
        where = where + "AND nomlocal = '" + local.getNomlocal() + "'";
        contador += db.delete("local", where, null);
        regAfectados += contador;
        return regAfectados;
    }

    public String eliminar(PeriodoInscripcionRevision perInscRev) {
        String regAfectados = "Filas afectadas = ";
        int contador = 0;

        String where = "codtiporevision = '" + perInscRev.getTipoRevision() + "'";
        where = where + "AND codasignatura = '" + perInscRev.getCodAsignatura() + "'";
        where = where + "AND codciclo = '" + perInscRev.getCodCiclo() + "'";
        where = where + "AND codtipoeval = '" + perInscRev.getCodTipoEval() + "'";
        where = where + "AND numeroeval = '" + perInscRev.getNumeroEval() + "'";
        contador += db.delete("periodoinscripcionrevision", where, null);
        regAfectados += contador;
        return regAfectados;
    }

    public String eliminar(PrimeraRevision primRev) {
        String regAfectados = "Filas afectadas = ";
        int contador = 0;

        String where = "coddocente = '" + primRev.getCoddocente() + "'";
        where = where + "AND carnet = '" + primRev.getCarnet() + "'";
        where = where + "AND codasignatura = '" + primRev.getCodasignatura() + "'";
        where = where + "AND codtiporevision = '" + primRev.getCodtiporevision() + "'";
        where = where + "AND codciclo = '" + primRev.getCodciclo() + "'";
        where = where + "AND codtipoeval = '" + primRev.getCodtipoeval() + "'";
        where = where + "AND numeroeval = '" + primRev.getNumeroeval() + "'";
        contador += db.delete("primerrevision", where, null);
        regAfectados += contador;
        return regAfectados;
    }

    public boolean verificarIntegridadReferencial(Object dato, int relacion) throws SQLException {
        switch (relacion) {
            case 1: {
                //Verificar que al Insertar Evaluacion exista el TipoEvaluacion, Asignatura y Ciclo
                Evaluacion evaluacion = (Evaluacion) dato;
                String[] id1 = {evaluacion.getCodAsignatura()};
                String[] id2 = {evaluacion.getCodCiclo()};
                String[] id3 = {evaluacion.getCodTipoEval()};
                abrir();

                Cursor cursor1 = db.query("asignatura", null, "codasignatura = ?", id1, null, null, null);
                Cursor cursor2 = db.query("ciclo", null, "codciclo = ?", id2, null, null, null);
                Cursor cursor3 = db.query("tipoevaluacion", null, "codtipoeval = ?", id3, null, null, null);

                if (cursor1.moveToFirst() && cursor2.moveToFirst() && cursor3.moveToFirst()) {
                    //Se encontraron los datos
                    return true;
                } else return false;
            }
            case 2: {
                //verificar que al Modificar Evaluacion exista el TipoEvaluacion, Asignatura, Ciclo y Numero de Evaluacion
                Evaluacion evaluacion = (Evaluacion) dato;
                String[] ids = {evaluacion.getCodAsignatura(), evaluacion.getCodCiclo(), evaluacion.getCodTipoEval(), String.valueOf(evaluacion.getNumeroEvaluacion())};
                abrir();

                Cursor c = db.query("evaluacion", null, "codasignatura = ? AND codciclo = ? AND codtipoeval = ? AND numeroeval = ?", ids, null, null, null);

                if (c.moveToFirst()) {
                    return true;
                } else return false;
            }
            case 3: {
                //Verificar que al Insertar PeriodoInscripcionRevision exista Asignatura, Ciclo, TipoEvaluacion, NumeroEvaluacion, Docente, Local, TipoRevision
                PeriodoInscripcionRevision perInscRev = (PeriodoInscripcionRevision) dato;
                String[] id1 = {perInscRev.getCodAsignatura()};
                String[] id2 = {perInscRev.getCodCiclo()};
                String[] id3 = {perInscRev.getCodTipoEval()};
                String[] id4 = {String.valueOf(perInscRev.getNumeroEval())};
                String[] id5 = {perInscRev.getCodDocente()};
                String[] id6 = {perInscRev.getCodLocal()};
                String[] id7 = {perInscRev.getTipoRevision()};
                abrir();

                Cursor cursor1 = db.query("asignatura", null, "codasignatura = ?", id1, null, null, null);
                Cursor cursor2 = db.query("ciclo", null, "codciclo = ?", id2, null, null, null);
                Cursor cursor3 = db.query("tipoevaluacion", null, "codtipoeval = ?", id3, null, null, null);
                Cursor cursor4 = db.query("evaluacion", null, "numeroeval = ?", id4, null, null, null);
                Cursor cursor5 = db.query("docente", null, "coddocente = ?", id5, null, null, null);
                Cursor cursor6 = db.query("local", null, "codlocal = ?", id6, null, null, null);
                Cursor cursor7 = db.query("tiporevision", null, "codtiporevision = ?", id7, null, null, null);

                if (cursor1.moveToFirst() && cursor2.moveToFirst() && cursor3.moveToFirst() && cursor4.moveToFirst() && cursor5.moveToFirst() && cursor6.moveToFirst() && cursor7.moveToFirst()) {
                    return true;
                } else return false;
            }
            case 4: {
                //Verificar que al modificar el PeriodoInscripcionRevision exista Asignatura, Ciclo, TipoEvaluacion, NumeroEvaluacion y TipoRevision
                PeriodoInscripcionRevision perInscRev = (PeriodoInscripcionRevision) dato;
                String[] ids = {perInscRev.getCodAsignatura(), perInscRev.getCodCiclo(), perInscRev.getCodTipoEval(), String.valueOf(perInscRev.getNumeroEval()), perInscRev.getTipoRevision()};
                abrir();


                Cursor c = db.query("periodoinscripcionrevision", null, "codasignatura = ? AND codciclo = ? AND codtipoeval = ? AND numeroeval = ? AND codtiporevision = ?", ids, null, null, null);

                if (c.moveToFirst()) {
                    return true;
                } else return false;

            }
            case 5: {
                SolicitudDiferido solicitudDiferido = (SolicitudDiferido) dato;
                String[] id1 = {solicitudDiferido.getCodMateria()};
                String[] id3 = {solicitudDiferido.getCarnet()};
                String[] id4 = {solicitudDiferido.getMotivo()};
                String[] id5 = {solicitudDiferido.getCodMateria(), solicitudDiferido.getTipoEva()};
                abrir();
                Cursor cursor1 = db.query("asignatura", null, "codasignatura = ?", id1, null, null, null);
                Cursor cursor3 = db.query("Estudiante", null, "carnet = ?", id3, null, null, null);
                Cursor cursor4 = db.query("MotivoDiferido", null, "nombreMotivo = ?", id4, null, null, null);
                Cursor cursor5 = db.query("evaluacion", null, "codasignatura = ? AND codtipoeval = ?", id5, null, null, null);

                if (cursor1.moveToFirst() && cursor3.moveToFirst() && cursor4.moveToFirst() && cursor5.moveToFirst()) {
                    return true;
                } else return false;
            }
            case 6: {
                Estudiante estudiante = (Estudiante) dato;
                String[] id = {estudiante.getCarnet()};
                abrir();
                Cursor cursor = db.query("Estudiante", null, "carnet = ?", id, null, null, null);
                if (cursor.moveToFirst()) {
                    return true;
                } else return false;
            }
            case 7: {
                DetalleDiferidoRepetido detalle = (DetalleDiferidoRepetido) dato;
                String[] id1 = {detalle.getIdLocal()};
                String[] id2 = {detalle.getIdAsignatura()};
                String[] id3 = {detalle.getIdDocente()};
                String[] id4 = {detalle.getIdTipoDifRep()};
                String[] id5 = {detalle.getIdTipoEval(), String.valueOf(detalle.getNumEval()), detalle.getIdAsignatura()};


                Cursor cursor1 = db.query("local", null, "codlocal = ?", id1, null, null, null);
                Cursor cursor2 = db.query("asignatura", null, "codasignatura = ?", id2, null, null, null);
                Cursor cursor3 = db.query("docente", null, "coddocente = ?", id3, null, null, null);
                Cursor cursor4 = db.query("TipoDiferidoRepetido", null, "idTipoDiferidoRepetido = ?", id4, null, null, null);
                Cursor cursor5 = db.query("evaluacion", null, "codtipoeval = ? AND numeroeval = ? AND codasignatura = ?", id5, null, null, null);

                if (cursor1.moveToFirst() && cursor2.moveToFirst() && cursor3.moveToFirst() && cursor4.moveToFirst() && cursor5.moveToFirst()) {
                    return true;
                }
                return false;
            }
            case 8: {
                DetalleEstudianteDiferido detalle = (DetalleEstudianteDiferido) dato;
                String[] id1 = {detalle.getCarnet()};
                String[] id2 = {detalle.getIdDetalleDifeRep()};

                Cursor cursor1 = db.query("Estudiante", null, "carnet = ?", id1, null, null, null);
                Cursor cursor2 = db.query("DetalleDiferidoRepetido", null, "idDetalleDiferidoRepetido = ?", id2, null, null, null);

                if (cursor1.moveToFirst() && cursor2.moveToFirst()) {
                    return true;
                }
                return false;
            }
            case 9: {
                DetalleEstudianteRepetido detalle = (DetalleEstudianteRepetido) dato;
                String[] id1 = {detalle.getCarnet()};
                String[] id2 = {detalle.getIdDetalleDifRep()};

                Cursor cursor1 = db.query("Estudiante", null, "carnet = ?", id1, null, null, null);
                Cursor cursor2 = db.query("DetalleDiferidoRepetido", null, "idDetalleDiferidoRepetido = ?", id2, null, null, null);

                if (cursor1.moveToFirst() && cursor2.moveToFirst()) {
                    return true;
                }
                return false;
            }
            case 5: {
                //Verificar que al insertar  Primera Revision exista SolicitudRevision, Docente y Motivo Cambio Nota
                PrimeraRevision primRev = (PrimeraRevision) dato;
                String[] id1 = {primRev.getCarnet(), primRev.getCodtiporevision(), primRev.getCodasignatura(), primRev.getCodciclo(), primRev.getCodtipoeval(), String.valueOf(primRev.getNumeroeval())};
                String[] id2 = {primRev.getCoddocente()};
                String[] id3 = {primRev.getMotivoCambioNota()};
                abrir();

                Cursor cursor1 = db.query("solicitudrevision", null, "carnet = ? AND codtiporevision = ? AND codasignatura = ? AND codciclo = ? AND codtipoeval = ? AND numeroeval = ?", id1, null, null, null);
                Cursor cursor2 = db.query("docente", null, "coddocente = ?", id2, null, null, null);
                Cursor cursor3 = db.query("motivocambionota", null, "codmotivocambionota = ?", id3, null, null, null);

                if (cursor1.moveToFirst() && cursor2.moveToFirst() && cursor3.moveToFirst()) {
                    return true;
                }
            }
            case 6: {
                //Verificar que al modificar la PrimerRevision exista la solicitud de revision Solicitud Revision
                PrimeraRevision primRev = (PrimeraRevision) dato;
                String[] ids = {primRev.getCoddocente(), primRev.getCarnet(), primRev.getCodasignatura(), primRev.getCodtiporevision(), primRev.getCodciclo(), primRev.getCodtipoeval(), String.valueOf(primRev.getNumeroeval())};
                abrir();

                Cursor c = db.query("primerrevision", null, "coddocente = ? AND carnet = ? AND codasignatura = ? AND codtiporevision = ? AND codciclo = ? AND codtipoeval = ? AND numeroeval = ?", ids, null, null, null);
                if (c.moveToFirst()) {
                    return true;
                }
                return false;
            }
            case 7: {
                //Verificar que al actualizar el local exista dich local
                Local loc = (Local) dato;
                String[] ids = {loc.getCodlocal()};
                abrir();

                Cursor c = db.query("local", null, "codlocal = ?", ids, null, null, null);
                if (c.moveToFirst()) {
                    return true;
                }
                return false;
            }
            default:
                return false;
        }
    }

    public String LlenarDatos() {
        final String[] usersId = {"CM17048", "RM17039", "AG17023", "MM14030", "PR17017"};
        final String[] names = {"Victor", "Shaky", "Daniel", "Cristian", "Roberto"};
        final String[] userPass = {"0123456789", "0123456789", "0123456789", "0123456789", "0123456789"};
        final String[] motivos = {"Salud", "Trabajo", "Interferencia", "Viaje", "Duelo", "Otro"};
        final String[] tipoDifRep = {"Diferido", "Repetido"};
        final String[] nombreTipoDifRep = {"Evaluacion diferida", "Evaluacion repetida"};
        final String[] TTEcodtipoeval = {"EP", "ED", "EL"};
        final String[] TTEnomtipoeval = {"Examen Parcial", "Examen Discusion", "Examen Laboratorio"};

        final String[] TRcodrevision = {"PR", "SR"};
        final String[] TRnomrevision = {"Primera Revision", "Segunda Revision"};


        final String[] TAcodasignatura = {"MAT115", "FIR115"};
        final String[] TAnomasignatura = {"Matematicas I", "Fisicas I"};
        final String[] TAunidadesval = {"4", "4"};

        final String[] TCcodciclo = {"12020", "22020"};
        final Date[] TCfechadesde = {Date.valueOf("2020-02-20"), Date.valueOf("2020-10-08")};
        final Date[] TCfechahasta = {Date.valueOf("2020-06-20"), Date.valueOf("2020-12-20")};

        final String[] TDcoddocente = {"CV00001", "MN00002"};
        final String[] TDnombredocente = {"Rudy Wilfredo", "Boris Alexander"};
        final String[] TDapellidodocente = {"Chicas Villegas", "Montano Navarrete"};


        final String[] TMCNcodmotivocambnota = {"ERRSUM", "ERRELPR", "ERRCAL"};
        final String[] TMCNmotivo = {"Error de suma", "Error de elaboracion de preguntas", "Error de calificacion"};

        final Date[] TSoRfechasolicitudrev = {Date.valueOf("2020-05-10"), Date.valueOf("2020-05-15")};
        final String[] TSoRmotivorevision = {"Mal calculo en la suma", "Pregunta incoherente"};
        final String[] TSoRcarnet = {"RM17039", "PR17017"};
        final String[] TSoRcodasignatura = {"MAT115", "FIR115"};
        final String[] TSoRcodcilo = {"12020", "22020"};
        final String[] TSoRcodtipoeval = {"EP", "ED"};
        final String[] TSoRcodtiporev = {"PR", "PR"};
        final float[] TSoRnotaantesrev = {Float.parseFloat("7.5"), Float.parseFloat("6.0")};
        final String[] TSoRcodtipogrupo = {"GD", "GL"};
        final int[] TSoRnumerogrupo = {Integer.parseInt("1"), Integer.parseInt("3")};
        final int[] TSoRcodnumeroeval = {Integer.parseInt("1"), Integer.parseInt("2")};

        abrir();
        db.execSQL("DELETE FROM usuario;");
        db.execSQL("DELETE FROM asignatura");
        db.execSQL("DELETE FROM ciclo");
        db.execSQL("DELETE FROM tipoevaluacion");
        db.execSQL("DELETE FROM tiporevision");
        db.execSQL("DELETE FROM docente");
        db.execSQL("DELETE FROM TipoDiferidoRepetido");
        db.execSQL("DELETE FROM MotivoDiferido");
        db.execSQL("DELETE FROM motivocambionota");
        db.execSQL("DELETE FROM solicitudrevision");

        Usuario user = new Usuario();
        for (int i = 0; i < usersId.length; i++) {
            user.setUsername(usersId[i]);
            user.setNombreUsuario(names[i]);
            user.setPassword(userPass[i]);
            insertar(user);
        }

        MotivoDiferido motivoDiferido = new MotivoDiferido();
        for (int i = 0; i < motivos.length; i++) {
            motivoDiferido.setNombreMotivoDiferido(motivos[i]);
            insertar(motivoDiferido);
        }

        Asignatura asignatura = new Asignatura();
        for (int i = 0; i < TAcodasignatura.length; i++) {
            asignatura.setCodasignatura(TAcodasignatura[i]);
            asignatura.setNomasignatura(TAnomasignatura[i]);
            asignatura.setUnidadesval(TAunidadesval[i]);
            insertar(asignatura);
        }

        Ciclo ciclo = new Ciclo();
        for (int i = 0; i < TCcodciclo.length; i++) {
            ciclo.setCodciclo(TCcodciclo[i]);
            ciclo.setFechadesde(TCfechadesde[i]);
            ciclo.setFechahasta(TCfechahasta[i]);
            insertar(ciclo);
        }

        TipoEvaluacion tipoeval = new TipoEvaluacion();
        for (int i = 0; i < TTEcodtipoeval.length; i++) {
            tipoeval.setCodTipoEval(TTEcodtipoeval[i]);
            tipoeval.setNomTipoEval(TTEnomtipoeval[i]);
            insertar(tipoeval);
        }

        TipoRevision tiporev = new TipoRevision();
        for (int i = 0; i < TRcodrevision.length; i++) {
            tiporev.setCodTipoRev(TRcodrevision[i]);
            tiporev.setNomTipoRev(TRnomrevision[i]);
            insertar(tiporev);
        }

        Docente docente = new Docente();
        for (int i = 0; i < TDcoddocente.length; i++) {
            docente.setCoddocente(TDcoddocente[i]);
            docente.setNomdocente(TDnombredocente[i]);
            docente.setApellidodocente(TDapellidodocente[i]);
            insertar(docente);
        }
        TipoDiferidoRepetido tipo = new TipoDiferidoRepetido();
        for (int i = 0; i < tipoDifRep.length; i++) {
            tipo.setIdTipo(tipoDifRep[i]);
            tipo.setNombreTipo(nombreTipoDifRep[i]);
            insertar(tipo);

            MotivoCambioNota motivo = new MotivoCambioNota();
            for (int i = 0; i < TMCNcodmotivocambnota.length; i++) {
                motivo.setCodmotivocambionota(TMCNcodmotivocambnota[i]);
                motivo.setMotivo(TMCNmotivo[i]);
                insertar(motivo);
            }

            SolicitudRevision solRev = new SolicitudRevision();
            for (int i = 0; i < TSoRcarnet.length; i++) {
                solRev.setCarnet(TSoRcarnet[i]);
                solRev.setCodasignatura(TSoRcodasignatura[i]);
                solRev.setCodciclo(TSoRcodcilo[i]);
                solRev.setCodtipoeval(TSoRcodtipoeval[i]);
                solRev.setCodtiporevision(TSoRcodtiporev[i]);
                solRev.setFechasolicitudrevision(String.valueOf(TSoRfechasolicitudrev[i]));
                solRev.setMotivorevision(TSoRmotivorevision[i]);
                solRev.setNotaantesrevision(TSoRnotaantesrev[i]);
                solRev.setNumeroeval(TSoRcodnumeroeval[i]);
                solRev.setNumerogrupo(TSoRnumerogrupo[i]);
                solRev.setCodtipogrupo(TSoRcodtipogrupo[i]);
                insertar(solRev);
            }

            cerrar();
            return "Guardado correctamente";
        }
}
