package iestrassierra.jlcamunas.trasstarea.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaAdapter;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaViewModel;
import iestrassierra.jlcamunas.trasstarea.basededatos.ControladorBaseDatos;
import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;
import iestrassierra.jlcamunas.trasstarea.preferencias.SettingsActivity;

public class ListadoTareasActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RecyclerView rv;
    private TextView listadoVacio;
    private MenuItem menuItemPrior;
    private List<Tarea> tareas = new ArrayList<>();
    private TareaAdapter adaptador;
    private boolean boolPrior = false;
    private Tarea tareaSeleccionada;
    private boolean esCreate = false;

    private TareaViewModel tareasLista;

    private ControladorBaseDatos controladorBaseDatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

         establecerFuente();

         //Controlador de la base de datos donde lo iniciamos con la INSTANCIA.
        controladorBaseDatos = ControladorBaseDatos.getInstance(getApplicationContext());

        //Binding del TextView
        listadoVacio = findViewById(R.id.listado_vacio);

        //Binding del RecyclerView
        rv = findViewById(R.id.listado_recycler);


        //RESTAURACIÓN DEL ESTADO GLOBAL DE LA ACTIVIDAD
        if (savedInstanceState != null) {
            //Recuperamos la lista de Tareas y el booleano prioritarias
            tareas = savedInstanceState.getParcelableArrayList("listaTareas");

            boolPrior = savedInstanceState.getBoolean("boolPrior");
        } else {
            //Inicializamos la lista de tareas y el booleano prioritarias
           // inicializarListaTareas();
            boolPrior = false;
        }

        //Creamos el adaptador y lo vinculamos con el RecycleView
        adaptador = new TareaAdapter(this,new ArrayList<>(),boolPrior);
        rv.setAdapter(adaptador);

        // Obtener una instancia del TareaViewModel
        tareasLista = new ViewModelProvider(this).get(TareaViewModel.class);

        // Observar cambios en la lista de tareas
        tareasLista.getTareas().observe(this, tareas -> {
            // Actualizar el conjunto de datos del adaptador cuando cambie la lista de tareas
            adaptador.setDatos(tareas);
            //Comprobamos si el listado está vacío
            comprobarListadoVacio();
        });
        //Ejecutamos el método, en funcion de lo que cogamos en las preferencias se ordenará.
       // ordenTareas();
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));


        //Registramos el rv para el menú contextual
        registerForContextMenu(rv);


    }


    //SALVADO DEL ESTADO GLOBAL DE LA ACTIVIDAD
    //Salva la lista de tareas y el valor booleano de prioritarias para el caso en que la actividad
    // sea destruida por ejemplo al cambiar la orientación del dispositivo
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Tarea> tareasArrayList = new ArrayList<>(Objects.requireNonNull(tareasLista.getListaCopia()));
        outState.putParcelableArrayList("listaTareas",  tareasArrayList);
        outState.putBoolean("prioritarias", boolPrior);
    }

    ////////////////////////////////////// OPCIONES DEL MENÚ ///////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menuItemPrior = menu.findItem(R.id.item_priority);
        //Colocamos el icono adecuado
        iconoPrioritarias();
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onResume() {
        super.onResume();


       if(esCreate){
            recreate();
        }

        esCreate = true;

    }



    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        //OPCION CREAR TAREA
        if (id == R.id.item_add) {
            //Llamada al launcher con contrato y respuesta definidos
            lanzadorCrearTarea.launch(null);
        }

        //OPCION MOSTRAR PRIORITARIAS / TODAS
        else if (id == R.id.item_priority) {

            //Conmutamos el valor booleando
            boolPrior = !boolPrior;
            //Colocamos el icono adecuado
            iconoPrioritarias();
            adaptador.setBoolPrior(boolPrior);
            adaptador.notifyDataSetChanged();

            //Comprobamos que hay algún elemento que mostrar
            comprobarListadoVacio();
        }
        //En el fragmento de preferencias hacemos un intent solo de ida, ya que no queremos que nos devuelva nada,
        //se ejecutara dentro de el fragmento setting.
        else if(id == R.id.item_preferencias){
            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
        } else if (id == R.id.item_estadisticas) {
            if(tareasLista.getListaCopia().size() <= 0){
                Toast.makeText(this, "No tienes tareas disponibles. AGREGA ALGUNA TAREA", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(this, EstadisticasActivity.class);
                startActivity(intent);
            }

        }
        //OPCIÓN ACERCA DE...
        else if (id == R.id.item_about) {

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.icon_pablo);
            imageView.requestLayout();

            //Creamos un AlertDialog como cuadro de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.about_title);
            builder.setView(imageView);
            builder.setMessage(R.string.about_msg);
            // Botón "Aceptar"
            builder.setPositiveButton(R.string.about_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Cerrar el cuadro de diálogo.
                }
            });
            // Mostrar el cuadro de diálogo
            builder.create().show();
        }

        //OPCIÓN SALIR
        else if (id == R.id.item_exit) {
            Toast.makeText(this,R.string.msg_salida, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }


    //////////////////////////////// OPCIONES DEL MENÚ CONTEXTUAL  /////////////////////////////////
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){
        
        //Leemos la tarea seleccionada en el evento de mostrar el menú contextual
        tareaSeleccionada = adaptador.getTareaSeleccionada();

        int itemId = item.getItemId();

        //OPCION DESCRIPCIÓN
        if (itemId == R.id.item_detalles) {
            // Mostrar un cuadro de diálogo con la descripción de la tarea
            lanzadorActividadDetalles.launch(tareaSeleccionada);
            return true;
        }

        //OPCION EDITAR
        else if (itemId == R.id.item_editar) {
            lanzadorActividadEditar.launch(tareaSeleccionada);
            return true;
        }

        //OPCION BORRAR
        else if (itemId == R.id.item_borrar) {

            if(tareaSeleccionada != null){
                // Mostrar un cuadro de diálogo para confirmar el borrado
                AlertDialog.Builder builder = new AlertDialog.Builder(ListadoTareasActivity.this);
                builder.setTitle(R.string.dialog_confirmacion_titulo);
                builder.setMessage(getString(R.string.dialog_msg) + " \"" + tareaSeleccionada.getTitulo() + "\"?");
                builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position;

                        //Borramos la tarea seleccionada de la colección tareas
                        position = tareasLista.getListaCopia().indexOf(tareaSeleccionada);
                        Executor executor = Executors.newSingleThreadExecutor();
                        //Creamos un objeto de la clase BorrarProducto que realiza el borrado en un hilo aparte
                        executor.execute(new BorrarTarea(tareaSeleccionada));
                        tareasLista.getListaCopia().remove(tareaSeleccionada);
                        adaptador.notifyItemRemoved(position);

                        //Comprobamos si el listado ha quedado vacío
                        comprobarListadoVacio();

                        //Notificamos que la tarea ha sido borrada al usuario
                        Toast.makeText(ListadoTareasActivity.this, R.string.dialog_erased, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.dialog_no, null);
                builder.show();
            }else{
                // Si no se encuentra el elemento, mostrar un mensaje de error
                Toast.makeText(ListadoTareasActivity.this, R.string.dialog_not_found, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    ////////////////////////// COMUNICACIONES CON ACTIVIDADES SECUNDARIAS //////////////////////////

     //Contrato personalizado para el lanzador hacia la actividad CrearTareaActivity
    ActivityResultContract<Intent, Tarea> contratoCrear = new ActivityResultContract<Intent, Tarea>() {
        //En primer lugar se define el Intent de ida
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Intent intent) {
            return new Intent(context, CrearTareaActivity.class);
        }
        //Ahora se define el método de parseo de la respuesta. En este caso se recibe un objeto Tarea
        @Override
        public Tarea parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                try {

                    return (Tarea) intent.getExtras().get("NuevaTarea");
                } catch (NullPointerException e) {
                    Log.e("Error en intent devuelto", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),R.string.action_canceled, Toast.LENGTH_SHORT).show();
            }
            return null; // Devuelve null si no se pudo obtener una Tarea válida.
        }
    };

    //Registramos el lanzador hacia la actividad CrearTareaActivity con el contrato personalizado y respuesta con implementación anónima
    private final ActivityResultLauncher<Intent> lanzadorCrearTarea = registerForActivityResult(contratoCrear, new ActivityResultCallback<Tarea>() {
        @Override
        public void onActivityResult(Tarea nuevaTarea) {
            if (nuevaTarea != null) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new InsertarTarea(nuevaTarea));
                tareasLista.getListaCopia().add(nuevaTarea);
                adaptador.notifyItemInserted(tareasLista.getListaCopia().size() - 1); // Agregar el elemento nuevo al adaptador.
                Toast.makeText(ListadoTareasActivity.this.getApplicationContext(), R.string.tarea_add, Toast.LENGTH_SHORT).show();
                ListadoTareasActivity.this.comprobarListadoVacio();
            }
        }
    });

    /////////////////////////////////////Hilos que ejecutan los metodos DAO, SAVE,DELETE,UPDATE (CRUD)/////////////////////////////////////

    class InsertarTarea implements Runnable {

        private Tarea tarea;

        public InsertarTarea(Tarea tarea) {
            this.tarea = tarea;
        }

        @Override
        public void run() {
            controladorBaseDatos.tareaDAO().insertAll(tarea);
        } //Metodo del repositorio
    }

    class BorrarTarea implements Runnable {

        private Tarea tarea;

        public BorrarTarea(Tarea tarea) {
            this.tarea = tarea;
        }

        @Override
        public void run() {
            controladorBaseDatos.tareaDAO().delete(tarea);
        }//Metodo del repositorio
    }

    class ActualizarTarea implements Runnable {

        private Tarea tarea;

        public ActualizarTarea(Tarea tarea) {
            this.tarea = tarea;
        }

        @Override
        public void run() {
            controladorBaseDatos.tareaDAO().actualizarTarea(tarea);
        }//Metodo del repositorio
    }

    /////////////////////////////////////CONTRATO EDITAR/////////////////////////////////////
    //Contrato para el lanzador hacia la actividad EditarTareaActivity
    ActivityResultContract<Tarea, Tarea> contratoEditar = new ActivityResultContract<Tarea, Tarea>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Tarea tarea) {
            Intent intent = new Intent(context, EditarTareaActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("TareaEditable", tarea);
            intent.putExtras(bundle);
            return intent;
        }

        @Override
        public Tarea parseResult(int i, @Nullable Intent intent) {
            if (i == Activity.RESULT_OK && intent != null) {
                try {
                    return (Tarea) Objects.requireNonNull(intent.getExtras()).get("TareaEditada");
                } catch (NullPointerException e) {
                    Log.e("Error en intent devuelto", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }else if(i == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),R.string.action_canceled, Toast.LENGTH_SHORT).show();
            }
            return null; // Devuelve null si no se pudo obtener una Tarea válida.
        }
    };

    //Respuesta para el lanzador hacia la actividad EditarTareaActivity
    ActivityResultCallback<Tarea> resultadoEditar = new ActivityResultCallback<Tarea>() {
        @Override
        public void onActivityResult(Tarea tareaEditada) {
            if (tareaEditada != null) {
                //Seteamos el id de la tarea recibida para que coincida con el de la tarea editada
                tareaEditada.setId(tareaSeleccionada.getId());

                //Procedemos a la sustitución de la tarea editada por la seleccionada.
                int posicionEnColeccion = tareasLista.getListaCopia().indexOf(tareaSeleccionada);
                Executor executor = Executors.newSingleThreadExecutor();
                tareasLista.getListaCopia().remove(tareaSeleccionada);

                tareasLista.getListaCopia().add(posicionEnColeccion, tareaEditada);
                executor.execute(new ActualizarTarea(tareaEditada));


                //Notificamos al adaptador y comprobamos si el listado ha quedado vacío
                adaptador.notifyItemChanged(posicionEnColeccion);
                comprobarListadoVacio();

                //Comunicamos que la tarea ha sido editada al usuario
                Toast.makeText(getApplicationContext(), R.string.tarea_edit, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //Registramos el lanzador hacia la actividad EditarTareaActivity con el contrato y respuesta personalizados
    ActivityResultLauncher<Tarea> lanzadorActividadEditar = registerForActivityResult(contratoEditar, resultadoEditar);


    /////////////////////////////////////LANZADOR PARA ENVIAR LA TAREA SELECCIONADA A LA ACTIVIDAD DETALLES/////////////////////////////////////


    ActivityResultContract<Tarea, Tarea> contratoDetalle = new ActivityResultContract<Tarea, Tarea>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Tarea tarea) {
            Intent intent = new Intent(context, DetallesActividad.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("TareaDetallada", tarea);
            intent.putExtras(bundle);
            return intent;
        }

        @Override
        public Tarea parseResult(int i, @Nullable Intent intent) {
            if (i == Activity.RESULT_OK && intent != null) {
                try {
                    return (Tarea) Objects.requireNonNull(intent.getExtras()).get("TareaDetallada");
                } catch (NullPointerException e) {
                    Log.e("Error en intent devuelto", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }else if(i == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"Volviendo de los detalles", Toast.LENGTH_SHORT).show();
            }
            return null; // Devuelve null si no se pudo obtener una Tarea válida.
        }
    };

    //Respuesta para el lanzador hacia la actividad EditarTareaActivity
    ActivityResultCallback<Tarea> resultadoDetallada = new ActivityResultCallback<Tarea>() {
        @Override
        public void onActivityResult(Tarea tareaDetallada) {
            if (tareaDetallada != null) {
                Toast.makeText(getApplicationContext(),"Volviendo de los detalles", Toast.LENGTH_SHORT).show();
            }
        }
    };

    ActivityResultLauncher<Tarea> lanzadorActividadDetalles = registerForActivityResult(contratoDetalle, resultadoDetallada);

    //////////////////////////////////////// OTROS MÉTODOS /////////////////////////////////////////

    //Método para cambiar el icono de acción para mostrar todas las tareas o solo prioritarias
    private void iconoPrioritarias(){
        if(boolPrior)
            //Ponemos en la barra de herramientas el icono PRIORITARIAS
            menuItemPrior.setIcon(R.drawable.baseline_star_outline_24);
        else
            //Ponemos en la barra de herramientas el icono NO PRIORITARIAS
            menuItemPrior.setIcon(R.drawable.baseline_star_outline_24_crossed);
    }

    //Método que comprueba si el listado de tareas está vacío.
    //Cuando está vacío oculta el RecyclerView y muestra el TextView con el texto correspondiente.
    private void comprobarListadoVacio(){

        ViewTreeObserver vto = rv.getViewTreeObserver();

        //Observamos cuando el RecyclerView esté completamente terminado
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Contamos la altura total del RecyclerView
                int alturaRV = 0;
                for (int i = 0; i < adaptador.getItemCount(); i++) {
                    View itemView = rv.getChildAt(i);
                    if (itemView != null)
                        alturaRV += itemView.getHeight();
                }

                if (alturaRV == 0) {
                    listadoVacio.setText(boolPrior?R.string.listado_tv_no_prioritarias:R.string.listado_tv_vacio);
                    listadoVacio.setVisibility(View.VISIBLE);
                } else {
                    listadoVacio.setVisibility(View.GONE);
                }

                // Remueve el oyente para evitar llamadas innecesarias
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


    }


    //Metodo que establece la fuente segun las preferencias elegidas
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

    //Metodo para cambiar en toda la actividad la fuente
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

}