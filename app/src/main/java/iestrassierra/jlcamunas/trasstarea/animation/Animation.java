package iestrassierra.jlcamunas.trasstarea.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class Animation extends View {
    private float circleX, circleY; // Posición del círculo
    private float rectX, rectY; // Posición del rectángulo
    private float rectWidth = 100; // Ancho del rectángulo
    private float rectHeight = 100; // Altura del rectángulo
    private float dxRect = 7; // Velocidad de movimiento en la dirección X
    private float dyRect = 7; // Velocidad de movimiento en la dirección Y
    private Paint rectPaint;
    private float triangleX, triangleY; // Posición del triángulo
    private float triangleSize = 60; // Tamaño del triángulo
    private float dxRectangulo = 7; // Velocidad de movimiento en la dirección X
    private float dyRectangulo = 7; // Velocidad de movimiento en la dirección Y
    private Paint trianglePaint; // Pincel para dibujar el triángulo
    private float radiusCirculo = 50; // Radio del círculo
    private Paint circlePaint;
    private Paint starPaint;
    private float dxCircle = 8; // Velocidad en la dirección X para el círculo
    private float dyCircle = 8; // Velocidad en la dirección Y para el círculo
    private float dxStar = 8; // Velocidad en la dirección X para la estrella
    private float dyStar = 8;
    private int starX, starY;
    private float size = 50;

    public Animation(Context context) {
        super(context);
        init();
    }

    public Animation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Inicializar el pincel para dibujar el círculo
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);

        // Inicializar el pincel para dibujar el triángulo
        trianglePaint = new Paint();
        trianglePaint.setColor(Color.BLUE);

        // Inicializar el pincel para dibujar el rectángulo
        rectPaint = new Paint();
        rectPaint.setColor(Color.GREEN);

        //Iniciamos la estrella
        starPaint = new Paint();
        starPaint.setColor(Color.BLACK); // Color de la estrella
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Dibujar el círculo en la posición actual
        for(int i = 0; i <= 3;i++){
            canvas.drawCircle(circleX, circleY, radiusCirculo, circlePaint);
        }


        // Dibujar el triángulo en la posición actual
        drawTriangle(canvas, triangleX, triangleY);

        // Dibujar el rectángulo en la posición actual
        canvas.drawRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight, rectPaint);

        canvas.drawPath(creaEstrella(starX,starY,50),starPaint);
    }

    // Método para dibujar un triángulo
    private void drawTriangle(Canvas canvas, float x, float y) {
        Path path = new Path();
        path.moveTo(triangleX, triangleY); // Punto superior
        path.lineTo(triangleX + triangleSize, triangleY + triangleSize); // Punto inferior derecho
        path.lineTo(triangleX - triangleSize, triangleY + triangleSize); // Punto inferior izquierdo
        path.lineTo(triangleX, triangleY); // Volver al punto superior
        path.close(); // Cerrar el camino

        canvas.drawPath(path, trianglePaint);
    }

    public Path creaEstrella (int x, int y, int radio){
        Point centro = new Point(x, y);

        //Creamos 10 puntos para trazar la estrella
        Point[] starP = new Point[10];
        //Creamos los puntos utilizando coordenadas polares
        //En este bucle for tenemos dos variables incrementales, la del índice del punto 'i' y la del ángulo
        //que parte de un valor aleatorio entre 0~180 y se irá incrementando en cada iteración 360/(nº de puntos)
        for (int i = 0, angulo = (int)(Math.random()*180); i < starP.length; i++, angulo += 360/starP.length) {
            if (i % 2 == 0) //Los puntos pares tendrán un módulo 'radio' (puntas de la estrella)
                starP[i] = polar2rect(radio, angulo);
            else //Los puntos impares tendrán un módulo 'radio/2' (puntos interiores de la estrella)
                starP[i] = polar2rect(radio / 2, angulo);
        }
        //Creamos el Path: desde el punto 0 vamos creando líneas hasta cerrar la forma
        Path star = new Path();
        star.moveTo(starP[0].x + centro.x, starP[0].y + centro.y);
        for(int i=1; i<starP.length; i++)
            star.lineTo(starP[i].x + centro.x, starP[i].y + centro.y);
        star.lineTo(starP[0].x + centro.x, starP[0].y + centro.y); //Última línea para cerrar la estrella
        return star;
    }

    //Método conversor de coordenadas polares (módulo, ángulo en grados) a coordenadas binomiales (o rectangulares)
    public Point polar2rect (double radio, int grados){
        double rad = grados * Math.PI / 180; //para pasar el ángulo de grados a radianes
        return new Point(
                (int) Math.round(radio * Math.cos(rad)), //coordenada x
                (int) Math.round(radio * Math.sin(rad))  //coordenada y
        );
    }
    // Método para actualizar la posición del círculo, del triángulo y del rectángulo
    private void updatePositions() {
        // Actualizar la posición del círculo, del triángulo y del rectángulo
        circleX += dxCircle; // Velocidad del círculo
        circleY += dyCircle;

        // Actualizar la posición del triángulo según la velocidad
        triangleX += dxRectangulo;
        triangleY += dyRectangulo;

        // Actualizar la posición del rectángulo según la velocidad
        rectX += dxRect;
        rectY += dyRect;

        starX += dxStar; // Velocidad de la estrella
        starY += dyStar;

        // Rebotar en los bordes de la pantalla
        if (circleX <= radiusCirculo || circleX >= getWidth() - radiusCirculo) {
            dxCircle = -dxCircle; // Invertir la dirección en X para el círculo
        }
        if (circleY <= radiusCirculo || circleY >= getHeight() - radiusCirculo) {
            dyCircle = -dyCircle; // Invertir la dirección en Y para el círculo
        }

        // Rebotar en los bordes de la pantalla
        if (triangleX <= 0 || triangleX >= getWidth()) {
            dxRectangulo = -dxRectangulo; // Invertir la dirección en X
        }
        if (triangleY <= 0 || triangleY >= getHeight()) {
            dyRectangulo = -dyRectangulo; // Invertir la dirección en Y
        }

        // Rebotar en los bordes de la pantalla
        if (rectX <= 0 || rectX + rectWidth >= getWidth()) {
            dxRect = -dxRect; // Invertir la dirección en X
        }
        if (rectY <= 0 || rectY + rectHeight >= getHeight()) {
            dyRect = -dyRect; // Invertir la dirección en Y
        }

        if (starX <= 0 || starX >= getWidth()) {
            dxStar = -dxStar; // Invertir la dirección en X para la estrella
        }
        if (starY <= 0 || starY >= getHeight()) {
            dyStar = -dyStar; // Invertir la dirección en Y para la estrella
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        circleX = radiusCirculo * 2;
        circleY = radiusCirculo * 2;

        // Iniciar un hilo para actualizar la posición del círculo, del triángulo y del rectángulo continuamente
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Actualizar la posición del círculo, del triángulo y del rectángulo
                    updatePositions();
                    // Volver a dibujar la vista
                    postInvalidate();
                    try {
                        // Esperar un breve período de tiempo antes de volver a actualizar
                        Thread.sleep(16); // Aproximadamente 60 fotogramas por segundo
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}