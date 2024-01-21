package iestrassierra.jlcamunas.trasstarea.modelo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Tarea implements Parcelable {

    private static long contador_id = 0;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "titulo")
    private String titulo;
    @ColumnInfo(name = "fecha_creacion")
    private Date fechaCreacion;
    @ColumnInfo(name = "fecha_objetivo")
    private Date fechaObjetivo;
    @ColumnInfo(name = "progreso")
    private Integer progreso;
    @ColumnInfo(name = "prioritaria")
    private Boolean prioritaria;
    @ColumnInfo(name = "descripcion")
    private String descripcion;
    @ColumnInfo(name = "URL_doc")
    private String URL_doc;
    @ColumnInfo(name = "URL_img")
    private String URL_img;

    @ColumnInfo(name = "URL_aud")
    private String URL_aud;
    @ColumnInfo(name = "URL_vid")
    private String URL_vid;

    // Constructor

    public Tarea(String titulo, String fechaCreacion, String fechaObjetivo, int progreso, boolean prioritaria, String descripcion,String URL_doc,String URL_aud,String URL_img,String URL_vid) {
        this.titulo = titulo;
        this.fechaCreacion = validarFecha(fechaCreacion);
        this.fechaObjetivo = validarFecha(fechaObjetivo);
        this.progreso = progreso;
        this.prioritaria = prioritaria;
        this.descripcion = descripcion;
        this.URL_doc = URL_doc;
        this.URL_aud = URL_aud;
        this.URL_img = URL_img;
        this.URL_vid = URL_vid;
    }

    public Tarea(){

    }

    // Getters y setters para acceder y modificar los atributos

    public String getURL_doc() {
        return URL_doc;
    }

    public void setURL_doc(String URL_doc) {
        this.URL_doc = URL_doc;
    }

    public String getURL_img() {
        return URL_img;
    }

    public void setURL_img(String URL_img) {
        this.URL_img = URL_img;
    }

    public String getURL_aud() {
        return URL_aud;
    }

    public void setURL_aud(String URL_aud) {
        this.URL_aud = URL_aud;
    }

    public String getURL_vid() {
        return URL_vid;
    }

    public void setURL_vid(String URL_vid) {
        this.URL_vid = URL_vid;
    }


    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaObjetivo(Date fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    public String getCreacionFecha(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(fechaCreacion);
    }

    public Date validarFecha(@NonNull String fechaCreacion){
        Date fecha = new Date(); //Para evitar devolver null
        if (validarFormatoFecha(fechaCreacion)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                fecha = sdf.parse(fechaCreacion);
            } catch (Exception e) {
                Log.e("Error fecha","Parseo de fecha no válido");
            }
        } else {
            Log.e("Error fecha","Formato de fecha no válido");
        }
        return fecha;
    }

    public Date getFechaObjetivo() {
        return fechaObjetivo;
    }

    public String getObjetivoFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(fechaObjetivo);
    }
    public Integer getProgreso() {
        return progreso;
    }

    public void setProgreso(Integer progreso) {
        this.progreso = progreso;
    }

    public Boolean isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(Boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    //Otros métodos
    private boolean validarFormatoFecha(@NonNull String fecha) {
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d\\d$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fecha);
        return matcher.matches();
    }

    public int quedanDias() {
        Date hoy = new Date(); // Obtener la fecha actual
        long diferenciaMillis = fechaObjetivo.getTime() - fechaCreacion.getTime();
        long diasDiferencia = TimeUnit.DAYS.convert(diferenciaMillis, TimeUnit.MILLISECONDS);
        return (int) diasDiferencia;
    }

    @NonNull
    @Override
    public String toString() {
        return "Tarea: " + titulo + "\n" +
                "Descripcion='" + descripcion;
    }


    //Métodos equals y hashCode para que funcione bien buscar en la colección usando indexOf()
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Tarea tarea = (Tarea) other;
        return id == tarea.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //Métodos para la interfaz Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.titulo);
        dest.writeLong(this.fechaCreacion != null ? this.fechaCreacion.getTime() : -1);
        dest.writeLong(this.fechaObjetivo != null ? this.fechaObjetivo.getTime() : -1);
        dest.writeValue(this.progreso);
        dest.writeValue(this.prioritaria);
        dest.writeString(this.descripcion);
        dest.writeString(this.URL_aud);
        dest.writeString(this.URL_img);
        dest.writeString(this.URL_doc);
        dest.writeString(this.URL_vid);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.titulo = source.readString();
        long tmpFechaCreacion = source.readLong();
        this.fechaCreacion = tmpFechaCreacion == -1 ? null : new Date(tmpFechaCreacion);
        long tmpFechaObjetivo = source.readLong();
        this.fechaObjetivo = tmpFechaObjetivo == -1 ? null : new Date(tmpFechaObjetivo);
        this.progreso = (Integer) source.readValue(Integer.class.getClassLoader());
        this.prioritaria = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.descripcion = source.readString();
        this.URL_aud = source.readString();
        this.URL_img = source.readString();
        this.URL_doc = source.readString();
        this.URL_vid = source.readString();
    }

    protected Tarea(Parcel in) {
        this.id = in.readLong();
        this.titulo = in.readString();
        long tmpFechaCreacion = in.readLong();
        this.fechaCreacion = tmpFechaCreacion == -1 ? null : new Date(tmpFechaCreacion);
        long tmpFechaObjetivo = in.readLong();
        this.fechaObjetivo = tmpFechaObjetivo == -1 ? null : new Date(tmpFechaObjetivo);
        this.progreso = (Integer) in.readValue(Integer.class.getClassLoader());
        this.prioritaria = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.descripcion = in.readString();
        this.URL_aud = in.readString();
        this.URL_img = in.readString();
        this.URL_doc = in.readString();
        this.URL_vid = in.readString();
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel source) {
            return new Tarea(source);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };

    public static Comparator<Tarea> comparadorFechaCreacionAscendente() {
        return Comparator.comparing(Tarea::getCreacionFecha);
    }
    public static Comparator<Tarea> comparadorFechaCreacionDescendente() {
        return Comparator.comparing(Tarea::getCreacionFecha).reversed();
    }
    public static Comparator<Tarea> comparadorAlfabeticoAscendente() {
        return Comparator.comparing(Tarea::getTitulo);
    }

    public static Comparator<Tarea> comparadorAlfabeticoDescendente() {
        return Comparator.comparing(Tarea::getTitulo).reversed();
    }

    public static Comparator<Tarea> comparadorDiasRestantesAscendente() {
        return Comparator.comparing(Tarea::quedanDias);
    }

    public static Comparator<Tarea> comparadorDiasRestantesDescendente() {
        return Comparator.comparing(Tarea::quedanDias).reversed();
    }

    public static Comparator<Tarea> comparadorProgresoAscendente() {
        return Comparator.comparing(Tarea::getProgreso);
    }

    public static Comparator<Tarea> comparadorProgresoDescendente() {
        return Comparator.comparing(Tarea::getProgreso).reversed();
    }
}


