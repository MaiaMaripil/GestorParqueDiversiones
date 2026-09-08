package RecusosCompartidos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Teatro {
    private boolean ingresoHabilitado, terminoFuncion, teatroCerrado;
    private CyclicBarrier grupoCompleto;
    private int capacidad, contPersonas;
    private Lock lock = new ReentrantLock();
    private Condition puedeIngresar = lock.newCondition();
    private Condition puedeSalir = lock.newCondition();

    public Teatro() {
        this.capacidad = 20;
        this.ingresoHabilitado = false;
        this.terminoFuncion = false;
        this.grupoCompleto = new CyclicBarrier(5);
        this.teatroCerrado=false;
    }

    // Visitante
    public boolean ingresarTeatro(String n) throws InterruptedException {
        boolean exito=true;
        lock.lock();
        try {
            while ((!ingresoHabilitado || contPersonas >= capacidad)&& !teatroCerrado) {
                puedeIngresar.await();

            }
            if(!teatroCerrado){
                 contPersonas++; // rserva el lugar
            }else{
                exito=false;
            }
           

        } finally {
            lock.unlock();
        }

        return exito;

    }

    public boolean verEspectaculo(String n) throws InterruptedException {
        boolean pudoEntrar;
        try {
            grupoCompleto.await();
            pudoEntrar = true;
            System.out.println(n + " Ingreso al teatro");

        } catch (BrokenBarrierException b) {
            pudoEntrar = false;
            lock.lock();
            try {
                contPersonas--;
            } finally {
                lock.unlock();
            }
            System.out.println(n + " no se completo el grupo antes de que comience la funcion y se va");
        }

        return pudoEntrar;
    }

    public void salir(String n) throws InterruptedException {
        lock.lock();
        try {
            while (!terminoFuncion) {
                puedeSalir.await();
            }
            contPersonas--;

        } finally {
            lock.unlock();
        }

    }

    // Reloj
    public void habilitarIngreso(String n) {
        teatroCerrado=false;
        lock.lock();
        try {
            ingresoHabilitado = true;
            terminoFuncion = false;
           // contPersonas = 0; deberia estar en 0 al terminar una funcion
            puedeIngresar.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void iniciarFuncion(String n) {
        lock.lock();
        try {
            System.out.println("Inicia funcion!!");
            ingresoHabilitado = false;

            grupoCompleto.reset(); //Si habia un grupo incompleto esperando para entrar se van

        } finally {
            lock.unlock();
        }
    }

    public void terminarFuncion(String n) {
        lock.lock();
        try {
            terminoFuncion = true;
            puedeSalir.signalAll();

        } finally {
            lock.unlock();
        }
    }

    public void cerrar(){
        lock.lock();
        teatroCerrado=true;
        puedeIngresar.signalAll();
        lock.unlock();

    }

    public void abrir(){
        lock.lock();
        teatroCerrado=false;
        puedeIngresar.signalAll();
        lock.unlock();

    }


}
