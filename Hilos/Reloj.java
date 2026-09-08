package Hilos;

import RecusosCompartidos.ParqueDiversiones;

//Se debe encagar de abrir y cerrar el parque, tambien de iniciar el espectaculo del teatro y finalizarlo.
public class Reloj implements Runnable {
    private String nombre;
    private ParqueDiversiones unParque;

    public Reloj(String n, ParqueDiversiones p) {
        this.nombre = n;
        this.unParque = p;
    }

    public void run() {
        try {

            unParque.abrirParque(); // 9 hs
        
            // gestiona el teatro hasta que se haga la hora de cerrar el ingreso
            gestionTeatro(50000);

            unParque.cerrarIngreso(); // cierra el ingreso

            // vuelve a gestionar el teatro
            gestionTeatro(15000); // 18-19

            unParque.cerrarActividades(); // cierra las actividades

            Thread.sleep(20000); // 19-23hs

            unParque.cerrarParque();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void gestionTeatro(long tiempo) throws InterruptedException {
         long fin = System.currentTimeMillis() + tiempo;

        while (System.currentTimeMillis()+12000 < fin) {
            Thread.sleep(2000);
            unParque.getTeatro().habilitarIngreso(nombre);
            Thread.sleep(4000);
            unParque.getTeatro().iniciarFuncion(nombre);
            Thread.sleep(5000);
            unParque.getTeatro().terminarFuncion(nombre);
        }

    }

}
