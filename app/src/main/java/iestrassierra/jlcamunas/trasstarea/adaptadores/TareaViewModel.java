package iestrassierra.jlcamunas.trasstarea.adaptadores;

import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorAlfabeticoAscendente;
import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorAlfabeticoDescendente;
import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorDiasRestantesAscendente;
import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorDiasRestantesDescendente;
import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorFechaCreacionAscendente;
import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorFechaCreacionDescendente;
import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorProgresoAscendente;
import static iestrassierra.jlcamunas.trasstarea.modelo.Tarea.comparadorProgresoDescendente;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import iestrassierra.jlcamunas.trasstarea.DAO.TareaDAO;
import iestrassierra.jlcamunas.trasstarea.basededatos.ControladorBaseDatos;
import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

public class TareaViewModel extends AndroidViewModel {

    //Usamos MutableLiveData para evitar problemas de ciclo de vida de los fragmentos
    private final MutableLiveData<String> titulo = new MutableLiveData<>();
    private final MutableLiveData<String> fechaCreacion = new MutableLiveData<>();
    private final MutableLiveData<String> fechaObjetivo = new MutableLiveData<>();
    private final MutableLiveData<Integer> progreso = new MutableLiveData<>();
    private final MutableLiveData<Boolean> prioritaria = new MutableLiveData<>();
    private final MutableLiveData<String> descripcion = new MutableLiveData<>();
    private final MutableLiveData<String> URL_doc = new MutableLiveData<>();
    private final MutableLiveData<String> URL_img = new MutableLiveData<>();
    private final MutableLiveData<String> URL_vid = new MutableLiveData<>();
    private final MutableLiveData<String> URL_aud = new MutableLiveData<>();

    public MutableLiveData<String> getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo.setValue(titulo);
    }

    public MutableLiveData<String> getURL_doc() {
        return URL_doc;
    }

    public void setURL_doc(String urlDoc) {
        this.URL_doc.setValue(urlDoc);
    }

    public MutableLiveData<String> getURL_img() {
        return URL_img;
    }

    public void setURL_img(String urlImg) {
        this.URL_img.setValue(urlImg);
    }

    public MutableLiveData<String> getURL_vid() {
        return URL_vid;
    }

    public void setURL_vid(String urlVid) {
        this.URL_vid.setValue(urlVid);
    }

    public MutableLiveData<String> getURL_aud() {
        return URL_aud;
    }

    public void setURL_aud(String urlAud) {
        this.URL_aud.setValue(urlAud);
    }

    public MutableLiveData<String> getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion.setValue(fechaCreacion);
    }

    public MutableLiveData<String> getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(String fechaObjetivo) {
        this.fechaObjetivo.setValue(fechaObjetivo);
    }

    public MutableLiveData<Integer> getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso.setValue(progreso);
    }

    public MutableLiveData<Boolean> isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(boolean prioritaria) {
        this.prioritaria.setValue(prioritaria);
    }

    public MutableLiveData<String> getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.setValue(descripcion);
    }
    private final LiveData<List<Tarea>> tareas;


    public TareaViewModel(@NonNull Application application) {
        super(application);
        //Inicializamos el contenido de la lista, al de la tabla de la base de datos
        tareas =  ControladorBaseDatos
                .getInstance(application)
                .tareaDAO().getAll();

    }

    private String CriterioOrden = "";
    private boolean orden;

    // Método para establecer la lista de tareas
    public void setListaTareas(List<Tarea> listaTareas) {
        //tareas.setValue(listaTareas);
    }
    List<Tarea> listaCopia;

    //Metodo donde obtengo las tareas para mostrarlas en el recyclerView
    public LiveData<List<Tarea>> getTareas() {

        Application application = getApplication();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        CriterioOrden = sharedPreferences.getString("criterio","Fecha de Creación");
        orden = sharedPreferences.getBoolean("orden",true);

        return Transformations.switchMap(tareas, input -> {
            //CriterioOrdenacion criterio = criterioOrdenacionLiveData.getValue();

            // Copiar la lista original para evitar modificaciones directas
             listaCopia = new ArrayList<>(input);

            // Aplicar el criterio de ordenación
            if(CriterioOrden.equalsIgnoreCase("1")){
                if(orden){
                    listaCopia.sort(comparadorAlfabeticoAscendente());
                }else{
                    listaCopia.sort(comparadorAlfabeticoDescendente());
                }
            } else if (CriterioOrden.equalsIgnoreCase("2")) {
                if(orden){
                    listaCopia.sort(comparadorFechaCreacionAscendente());
                }else{
                    listaCopia.sort(comparadorFechaCreacionDescendente());
                }
            } else if (CriterioOrden.equalsIgnoreCase("4")) {
                if(orden){
                    listaCopia.sort(comparadorProgresoAscendente());
                }else{
                    listaCopia.sort(comparadorProgresoDescendente());
                }
            } else {
                if(orden){
                    listaCopia.sort(comparadorDiasRestantesAscendente());
                }else{
                    listaCopia.sort(comparadorDiasRestantesDescendente());
                }
            }

            return new MutableLiveData<>(listaCopia);
        });


    }

    public List<Tarea> getListaCopia() {
        return listaCopia;
    }



}