package iestrassierra.jlcamunas.trasstarea.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

//Clase Detalles donde tenemos la opcion del menu contextual
public class DetallesActividad extends AppCompatActivity {
    private Tarea tareaDetallada;

    private TextView tvNombreTarea,tvDescripcion,txtFecha,tvProgreso;
    private ImageButton btnIm, btnDoc,btnAud,btnVid;

    private Button btnCerrar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tarea);

        tvNombreTarea = findViewById(R.id.tvNombre);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        txtFecha = findViewById(R.id.txtFecha);
        tvProgreso = findViewById(R.id.tvProgreso);

        btnIm = findViewById(R.id.btnIm);
        btnDoc = findViewById(R.id.btDoc);
        btnAud = findViewById(R.id.btnAud);
        btnVid = findViewById(R.id.btnVid);
        btnCerrar = findViewById(R.id.btnCerrar2);



        //Recibimos la tarea que va a ser detallada
        Bundle bundle = getIntent().getExtras();
        try {
            if (bundle != null) {
                this.tareaDetallada = bundle.getParcelable("TareaDetallada");
            }
        }catch (NullPointerException e){
            Log.e("Bundle recibido nulo", e.toString());
        }

        //Obtenemos del intent enviado, la tarea seleccionado y hacemos un set de sus propiedad a los textView.
        tvNombreTarea.setText(tareaDetallada.getTitulo().toString());
        tvDescripcion.setText(tareaDetallada.getDescripcion().toString());
        txtFecha.setText(tareaDetallada.getObjetivoFecha().toString());
        tvProgreso.setText(tareaDetallada.getProgreso().toString()+"%");

        //Metodos escuchadores de los botones para cuando se pulse aparezca el archivo de la tarea.
        btnIm.setOnClickListener(v ->{

            mostrarURL("IMAGEN",tareaDetallada.getURL_img());


        });

        btnDoc.setOnClickListener(v ->{

            mostrarURL("DOCUMENTO",tareaDetallada.getURL_doc());


        });
        btnAud.setOnClickListener(v ->{

            mostrarContenidoAudio();


        });
        btnVid.setOnClickListener(v ->{

            mostrarURL("VIDEO",tareaDetallada.getURL_vid());


        });

        btnCerrar.setOnClickListener(v->{

            Intent aListado = new Intent();
            //Indicamos en el resultado que ha sido cancelada la actividad
            setResult(RESULT_CANCELED, aListado);
            //Volvemos a la actividad Listado
            finish();


        });

    }

    //Metodo para mostrar en una ventana el nombre del archivo de la tarea
    public void mostrarURL(String titulo,String url){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetallesActividad.this);
        builder.setTitle(titulo);
        builder.setMessage(url);

        builder.setPositiveButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void mostrarContenidoAudio(){
        lanzadorActividadDetalles.launch(tareaDetallada);
    }

    ActivityResultContract<Tarea, Tarea> contratoDetalle = new ActivityResultContract<Tarea, Tarea>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Tarea tarea) {
            Intent intent = new Intent(context, AudioActividad.class);
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
}
