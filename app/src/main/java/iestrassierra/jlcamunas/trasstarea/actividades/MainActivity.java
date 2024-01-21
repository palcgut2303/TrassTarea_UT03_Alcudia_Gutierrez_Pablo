package iestrassierra.jlcamunas.trasstarea.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import iestrassierra.jlcamunas.trasstarea.R;

public class MainActivity extends AppCompatActivity {

    private boolean theme = true;
    private boolean esCreate = false;
    //Ruta donde tenemos los archivos SD
    private final String rutaSD = "/storage/emulated/0/Android/data/iestrassierra.jlcamunas.trasstarea/files";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        theme = sharedPreferences.getBoolean("tema",true);
        Button btEmpezar = findViewById(R.id.main_bt_empezar);
        btEmpezar.setOnClickListener(this::empezar);
        //getTheme();
        establecerFuente();
        borrarArchivosInterno();
        if(tarjetaSDMontada()){
            borrarArchivosSD(rutaSD);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        theme = sharedPreferences.getBoolean("tema",true);


        if(esCreate){
            recreate();
        }

        esCreate = true;

    }

    private void establecerFuente(){
        String fontSize = sharedPreferences.getString("tamañoLetra", "Mediana");
        // float size = getResources().getConfiguration().fontScale;
        if(fontSize.equalsIgnoreCase("1")){
            ajustarTamanoLetraEnTodaLaApp(getResources(),0.8f);
        }else if (fontSize.equalsIgnoreCase("2") ){
            ajustarTamanoLetraEnTodaLaApp(getResources(),1.2f);
        }else if (fontSize.equalsIgnoreCase("3") ){
            ajustarTamanoLetraEnTodaLaApp(getResources(),1.5f);
        }


    }

    public static void ajustarTamanoLetraEnTodaLaApp(Resources resources, float nuevoTamano) {
        Configuration configuration = resources.getConfiguration();

        // Crear una nueva configuración basada en la configuración actual
        Configuration newConfig = new Configuration(configuration);

        // Modificar la escala de fuente en la nueva configuración
        newConfig.fontScale = nuevoTamano;

        // Aplicar la nueva configuración al recurso
        resources.updateConfiguration(newConfig, null);

        // Actualizar la densidad de píxeles en función de la nueva configuración
        DisplayMetrics metrics = resources.getDisplayMetrics();
        resources.updateConfiguration(newConfig, metrics);
    }

    //Metodo que aplica el tema oscuro una vez reiniciado la aplicacio, eso si, si esta habilitado en las preferencias si no tema claro.
    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tema",false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        return super.getTheme();
    }

    private void empezar(View v){
        Intent aEmpezar = new Intent(this, ListadoTareasActivity.class);
        startActivity(aEmpezar);
    }

    //Metodo de limpieza de archivos internos.
    //La diferencia que tengo yo esque la URL de las tareas,
    // no las necesito ya que lo que hago es obtener el directorio donde tengo todos los ficheros y comprobar ese tiempo,
    // si se cumple se borra
    public void borrarArchivosInterno(){
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        int diasParaEliminar = Integer.parseInt(preferencias.getString("limpieza", "30"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -diasParaEliminar);

        Date fechaLimite = cal.getTime();
        File directorio = new File(getFilesDir(), "");

        File[] archivos = directorio.listFiles();
        for (File archivo : archivos) {
            if (archivo.lastModified() < fechaLimite.getTime()) {
                archivo.delete();
            }
        }
    }

    //Metodo de limpieza de archivosSD
    //Mismo funcionamiento pero coge el directorio de los archivos donde esten en la SD
    public void borrarArchivosSD(String directorioSD) {
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        int diasParaEliminar = Integer.parseInt(preferencias.getString("limpieza", "30"));


        File directorio = new File(directorioSD);

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    long tiempoUltimaModificacion = archivo.lastModified();
                    long tiempoActual = System.currentTimeMillis();
                    long diferenciaTiempo = tiempoActual - tiempoUltimaModificacion;

                    // Convertir el límite de tiempo de días a milisegundos
                    long limiteTiempoMilisegundos = diasParaEliminar * 24 * 60 * 60 * 1000;

                    if (diferenciaTiempo > limiteTiempoMilisegundos) {
                        archivo.delete();
                    }
                }
            }
        }
    }

    //Metodo para comprobar si la tarjeta esta montada
    public static boolean tarjetaSDMontada() {
        // Comprueba si el dispositivo tiene montada una tarjeta SD
        String estado = Environment.getExternalStorageState();
        return estado.equals(Environment.MEDIA_MOUNTED);
    }


}