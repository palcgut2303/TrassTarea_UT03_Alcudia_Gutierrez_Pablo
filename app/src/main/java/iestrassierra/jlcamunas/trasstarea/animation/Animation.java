package iestrassierra.jlcamunas.trasstarea.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animation extends View {
    private CircleThread[] circleThreads;
    private TriangleThread[] triangleThreads;
    private CuadradoHilo[] cuadradoHilos;
    private EstrellaHilo[] estrellaHilos;
    private int width;
    private int height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        circleThreads = new CircleThread[3];
        triangleThreads = new TriangleThread[3];
        cuadradoHilos = new CuadradoHilo[3];
        estrellaHilos = new EstrellaHilo[3];


        for (int i = 0; i < circleThreads.length; i++) {
            circleThreads[i] = new CircleThread(width,height);
            circleThreads[i].start();
        }


        for (int i = 0; i < triangleThreads.length; i++) {
            triangleThreads[i] = new TriangleThread(width,height);
            triangleThreads[i].start();
        }


        for (int i = 0; i < cuadradoHilos.length; i++) {
            cuadradoHilos[i] = new CuadradoHilo(width,height);
            cuadradoHilos[i].start();
        }


        for (int i = 0; i < estrellaHilos.length; i++) {
            estrellaHilos[i] = new EstrellaHilo(width,height);
            estrellaHilos[i].start();
        }
    }

    public Animation(Context context) {
        super(context);

    }

    public Animation(Context context, AttributeSet attrs) {
        super(context, attrs);

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        for (CircleThread circleThread : circleThreads) {
            canvas.drawCircle(circleThread.getX(), circleThread.getY(), circleThread.getRadius(), circlePaint);
        }


        Paint trianglePaint = new Paint();
        trianglePaint.setColor(Color.BLUE);
        for (TriangleThread triangleThread : triangleThreads) {
            canvas.drawPath(triangleThread.getTrianglePath(), trianglePaint);
        }


        Paint cuadradoPaint = new Paint();
        cuadradoPaint.setColor(Color.GREEN);
        for (CuadradoHilo cuadradoHilo : cuadradoHilos) {
            canvas.drawRect(cuadradoHilo.getX(), cuadradoHilo.getY(), cuadradoHilo.getX() + cuadradoHilo.getSize(), cuadradoHilo.getY() + cuadradoHilo.getSize(), cuadradoPaint);
        }


        Paint estrellaPaint = new Paint();
        estrellaPaint.setColor(Color.BLACK);
        for (EstrellaHilo estrellaHilo : estrellaHilos) {
            canvas.drawPath(estrellaHilo.creaEstrella(50),estrellaPaint);
        }
    }

    private class CircleThread extends Thread {
        private static final int RADIUS = 50;
        private static final int MOVE_SPEED = 5;

        private float x, y;
        private int dx, dy;

        public CircleThread(int width,int height) {
            Random random = new Random();
            x = random.nextInt(width - 2 * RADIUS) + RADIUS;
            y = random.nextInt(height - 2 * RADIUS) + RADIUS;
            dx = random.nextBoolean() ? MOVE_SPEED : -MOVE_SPEED;
            dy = random.nextBoolean() ? MOVE_SPEED : -MOVE_SPEED;
        }

        @Override
        public void run() {
            while (true) {
                updatePosition();
                postInvalidate();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void updatePosition() {

            x += dx;
            y += dy;

            if (x <= RADIUS || x >= width - RADIUS) {
                dx = -dx;
            }
            if (y <= RADIUS || y >= height - RADIUS) {
                dy = -dy;
            }
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getRadius() {
            return RADIUS;
        }



    }

    private class TriangleThread extends Thread {
        private static final int TRIANGLE_SIZE = 60;
        private static final int MOVE_SPEED = 5;

        private float x, y; // Posición del triángulo
        private int width, height; // Ancho y alto de la vista
        private int dx, dy; // Dirección del movimiento del triángulo

        public TriangleThread(int widthC,int heightC) {
            Random random = new Random();
            width = widthC;
            height = heightC;
            x = random.nextInt(width - TRIANGLE_SIZE);
            y = random.nextInt(height - TRIANGLE_SIZE);
            dx = random.nextBoolean() ? MOVE_SPEED : -MOVE_SPEED;
            dy = random.nextBoolean() ? MOVE_SPEED : -MOVE_SPEED;
        }

        @Override
        public void run() {
            while (true) {
                updatePosition();
                postInvalidate(); // Redibujar la vista
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void updatePosition() {

            x += dx;
            y += dy;

            if (x <= 0 || x >= width - TRIANGLE_SIZE) {
                dx = -dx;
            }
            if (y <= 0 || y >= height - TRIANGLE_SIZE) {
                dy = -dy;
            }
        }

        public Path getTrianglePath() {
            Path path = new Path();
            path.moveTo(x, y); // Punto superior
            path.lineTo(x + TRIANGLE_SIZE, y + TRIANGLE_SIZE); // Punto inferior derecho
            path.lineTo(x - TRIANGLE_SIZE, y + TRIANGLE_SIZE); // Punto inferior izquierdo
            path.lineTo(x, y); // Volver al punto superior
            path.close(); // Cerrar el camino
            return path;
        }


    }

    private class CuadradoHilo extends Thread {
        private float x, y;
        private float size = 100;
        private float dx = 7;
        private float dy = 7;
        private int maxWidth, maxHeight;

        public CuadradoHilo(int maxWidthT, int maxHeightT) {
            Random random = new Random();
            maxWidth = maxWidthT;
            maxHeight = maxHeightT;
            x = random.nextInt(maxWidth - (int) size);
            y = random.nextInt(maxHeight - (int) size);
        }

        @Override
        public void run() {
            while (true) {
                updatePosition();
                postInvalidate();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void updatePosition() {
            x += dx;
            y += dy;
            if (x <= 0 || x >= maxWidth - size) {
                dx = -dx;
            }
            if (y <= 0 || y >= maxHeight - size) {
                dy = -dy;
            }
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getSize() {
            return size;
        }

    }

    private class EstrellaHilo extends Thread{
        private float dxStar = 8; // Velocidad
        private float dyStar = 8;
        private int x, y;
        private float size = 50;
        private int maxWidth, maxHeight;

        public EstrellaHilo(int maxWidthT, int maxHeightT) {
            Random random = new Random();
            maxWidth = maxWidthT;
            maxHeight = maxHeightT;
            x = random.nextInt(maxWidth - (int) size);
            y = random.nextInt(maxHeight - (int) size);
        }

        @Override
        public void run() {
            while (true) {
                updatePosition();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void updatePosition() {
            x += dxStar;
            y += dyStar;
            if (x <= 0 || x >= maxWidth - size) {
                dxStar = -dxStar;
            }
            if (y <= 0 || y >= maxHeight - size) {
                dyStar = -dyStar;
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public float getSize() {
            return size;
        }

        // Método llamado cuando cambia el tamaño de la vista
        public void setMaxSize(int width, int height) {
            maxWidth = width;
            maxHeight = height;
        }

        public Path creaEstrella (int radio){
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

    }

}