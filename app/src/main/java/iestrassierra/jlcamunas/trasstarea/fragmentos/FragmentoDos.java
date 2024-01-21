package iestrassierra.jlcamunas.trasstarea.fragmentos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaViewModel;

public class FragmentoDos extends Fragment {

    private TareaViewModel tareaViewModel;
    private EditText etDescripcion;

    private TextView tvURLVideo,tvURLDOCUMENT,tvURLIMAGE,tvURLAudio;

    private String nombreArchivo;
    private String URL_doc;
    private String URL_img;
    private String URL_aud;
    private String URL_vid;

    //Interfaces de comunicación con la actividad para el botón Guardar y Volver
    public interface ComunicacionSegundoFragmento {
        void onBotonGuardarClicked();
        void onBotonVolverClicked();
    }

    private ComunicacionSegundoFragmento comunicadorSegundoFragmento;


    public FragmentoDos() {}

    //Sobrescribimos onAttach para establecer la comunicación entre el fragmento y la actividad
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComunicacionSegundoFragmento) { //Si la actividad implementa la interfaz
            comunicadorSegundoFragmento = (ComunicacionSegundoFragmento) context; //La actividad se convierte en escuchadora
        } else {
            throw new ClassCastException(context + " debe implementar la interfaz de comunicación con el segundo fragmento");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Vinculamos el fragmento con el ViewModel
        tareaViewModel = new ViewModelProvider(requireActivity()).get(TareaViewModel.class);

        //Inflamos el fragmento
        return inflater.inflate(R.layout.fragment_segundo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Binding y set EditText Descripción y URL`s
        etDescripcion = view.findViewById(R.id.et_descripcion);
        tareaViewModel.getDescripcion().observe(getViewLifecycleOwner(), s -> etDescripcion.setText(s));
        tvURLAudio = view.findViewById(R.id.urlAudio);
        tareaViewModel.getURL_aud().observe(getViewLifecycleOwner(), s -> tvURLAudio.setText(s));
        tvURLDOCUMENT = view.findViewById(R.id.urlDocument);
        tareaViewModel.getURL_doc().observe(getViewLifecycleOwner(), s -> tvURLDOCUMENT.setText(s));
        tvURLIMAGE = view.findViewById(R.id.urlImage);
        tareaViewModel.getURL_img().observe(getViewLifecycleOwner(), s -> tvURLIMAGE.setText(s));
        tvURLVideo = view.findViewById(R.id.urlVideo);
        tareaViewModel.getURL_vid().observe(getViewLifecycleOwner(), s -> tvURLVideo.setText(s));


        //Binding y config boton Volver
        Button btVolver = view.findViewById(R.id.bt_volver);
        btVolver.setOnClickListener(v -> {
            //Escribimos en el ViewModel
            escribirViewModel();
            //Llamamos al método onBotonVolverClicked que está implementado en la actividad
            if(comunicadorSegundoFragmento != null)
                comunicadorSegundoFragmento.onBotonVolverClicked();
        });

        //Binding y config boton Guardar
        Button btGuardar = view.findViewById(R.id.bt_guardar);
        btGuardar.setOnClickListener(v -> {
            //Escribimos en el ViewModel
            tareaViewModel.setDescripcion(etDescripcion.getText().toString());
            tareaViewModel.setURL_img(tvURLIMAGE.getText().toString());
            tareaViewModel.setURL_doc(tvURLDOCUMENT.getText().toString());
            tareaViewModel.setURL_vid(tvURLVideo.getText().toString());
            tareaViewModel.setURL_aud(tvURLAudio.getText().toString());



            //Llamamos al método onBotonGuardarClicked que está implementado en la actividad.
            if(comunicadorSegundoFragmento != null)
                comunicadorSegundoFragmento.onBotonGuardarClicked();
        });
        //Metodos que abren el seleccionado de archivos.
        ImageButton btDocumento = view.findViewById(R.id.btDocumento);
        btDocumento.setOnClickListener(v -> {

            seleccionarArchivoDocumentos();


        });

        ImageButton btImagen = view.findViewById(R.id.btImagen);
        btImagen.setOnClickListener(v -> {

            seleccionarArchivoImagen();


        });

        ImageButton btAudio = view.findViewById(R.id.btAudio);
        btAudio.setOnClickListener(v -> {

            seleccionarArchivoAudio();


        });

        ImageButton btVideo = view.findViewById(R.id.btVideo);
        btVideo.setOnClickListener(v -> {

            seleccionarArchivoVideo();


        });
    }

    private void seleccionarArchivoDocumentos() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");  //  ajustar el tipo de archivo

        // Empieza la actividad para seleccionar un archivo
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    private void seleccionarArchivoVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");  //  ajustar el tipo de archivo

        // Empieza la actividad para seleccionar un archivo
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    private void seleccionarArchivoAudio() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");  //  ajustar el tipo de archivo

        // Empieza la actividad para seleccionar un archivo
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    private void seleccionarArchivoImagen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");  //  ajustar el tipo de archivo

        // Empieza la actividad para seleccionar un archivo
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    private static final int PICK_FILE_REQUEST_CODE = 1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Obtiene la Uri del archivo seleccionado
                Uri uri = data.getData();


                //  mostrar el nombre del archivo
                 nombreArchivo = uri.getLastPathSegment();

            //Guardamos la ruta de los archivos y su nombre.
                 if(nombreArchivo.startsWith("document:")){
                     URL_doc = uri.getPath();
                     tvURLDOCUMENT.setText(nombreArchivo);


                 }else if(nombreArchivo.startsWith("video:")){
                     URL_vid = uri.getPath();
                     tvURLVideo.setText(nombreArchivo);


                 } else if (nombreArchivo.startsWith("audio:")) {
                     URL_aud = uri.getPath();
                     tvURLAudio.setText(nombreArchivo);


                 }else if(nombreArchivo.startsWith("image:")){
                     URL_img = uri.getPath();
                     tvURLIMAGE.setText(nombreArchivo);


                 }


            }
        }
    }

    //Método para guardar el estado del formulario en un Bundle
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("descripcion",  etDescripcion.getText().toString());
        outState.putString("urlVideo",  tvURLVideo.getText().toString());
        String tvImage = tvURLIMAGE.getText().toString();
        outState.putString("urlImage", tvImage );
        outState.putString("urlAudio",  tvURLAudio.getText().toString());
        outState.putString("urlDocument",  tvURLDOCUMENT.getText().toString());
        //Sincronizamos la información salvada también en el ViewModel
        escribirViewModel();
    }

    //Método para restaurar el estado del formulario
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            etDescripcion.setText(savedInstanceState.getString("descripcion"));
            tvURLDOCUMENT.setText(savedInstanceState.getString("urlDocument"));
            String urlImage = savedInstanceState.getString("urlImage");
            tvURLIMAGE.setText(urlImage);
            tvURLAudio.setText(savedInstanceState.getString("urlAudio"));
            tvURLVideo.setText(savedInstanceState.getString("urlVideo"));
        }
    }



    private void escribirViewModel(){
        tareaViewModel.setDescripcion(etDescripcion.getText().toString());
        tareaViewModel.setURL_img(tvURLIMAGE.getText().toString());
        tareaViewModel.setURL_doc(tvURLDOCUMENT.getText().toString());
        tareaViewModel.setURL_vid(tvURLVideo.getText().toString());
        tareaViewModel.setURL_aud(tvURLAudio.getText().toString());
    }



}