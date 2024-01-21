package iestrassierra.jlcamunas.trasstarea.basededatos;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import iestrassierra.jlcamunas.trasstarea.DAO.TareaDAO;
import iestrassierra.jlcamunas.trasstarea.adaptadores.Convertidores;
import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

//Clase Controlador de la base de datos donde defino la base de datos, sus clases, la instancia.
@Database(entities = {Tarea.class}, version = 1, exportSchema = false)
@TypeConverters(Convertidores.class)
public abstract class ControladorBaseDatos  extends RoomDatabase {
    @Override
    public void clearAllTables() {

    }
    private static ControladorBaseDatos INSTANCIA;

    public static ControladorBaseDatos getInstance(Context context) {
        if (INSTANCIA == null) {
            INSTANCIA = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ControladorBaseDatos.class,
                            "TareaBD")
                    .build();
        }
        return INSTANCIA;
    }
    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(@NonNull DatabaseConfiguration databaseConfiguration) {
        return null;
    }

    public abstract TareaDAO tareaDAO();
    public static void destroyInstance() {
        INSTANCIA = null;
    }

}
