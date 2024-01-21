package iestrassierra.jlcamunas.trasstarea.adaptadores;

import androidx.room.TypeConverter;

import java.util.Date;

//Clase convertidor para utilizar DATE en la base de datos ROOM
public class Convertidores {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
