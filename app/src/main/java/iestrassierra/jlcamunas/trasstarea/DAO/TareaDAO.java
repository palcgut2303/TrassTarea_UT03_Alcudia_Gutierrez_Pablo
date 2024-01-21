package iestrassierra.jlcamunas.trasstarea.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

//Clase donde tengo los métodos CRUD y los de estadisticas.
@Dao
public interface TareaDAO {
    @Insert
    void insertAll(Tarea... tarea);

    //Anotación que permite realizar el borrado de una tarea
    @Delete
    void delete(Tarea tarea);

    @Update
    void actualizarTarea(Tarea tarea);

    @Query("SELECT * FROM tarea")
        //En este caso haremos que esta consulta se regenere cada vez que se produzcan cambios
        //en la base de datos mediante un objeto LiveData.
    LiveData<List<Tarea>> getAll();

    @Query("SELECT COUNT(*) FROM tarea")
    LiveData<Integer> numeroTareas();

    @Query("SELECT AVG(progreso) FROM tarea")
    LiveData<Double> calcularPromedioDeProgreso();
    @Query("SELECT AVG(fecha_creacion) FROM tarea")
    LiveData<Double> calcularPromedioDeFecha();

    @Query("SELECT * FROM Tarea WHERE titulo LIKE '%' || :cadena || '%'")
    LiveData<List<Tarea>> buscarTareasPorNombre(String cadena);

}
