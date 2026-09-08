package RecusosCompartidos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Comedor {
    private Semaphore lugarComedor;
    private CyclicBarrier barrera;
    private boolean abierto = true;
    private Semaphore mutex;

    public Comedor(int m) {
        this.lugarComedor = new Semaphore(m * 4);
        this.barrera = new CyclicBarrier(4);
        this.mutex = new Semaphore(1);
    }

    // Visitante
    public boolean entrarComedor(String n, boolean espera) throws InterruptedException {
        
        boolean pudoEntrar = false;

        mutex.acquire();
        boolean comAbierto=abierto;
        mutex.release();
        if (comAbierto) {
            System.out.println(n + " Entro al comedor y se fija si hay lugar. ES PACIENTE?: " + espera);

            if (espera) {

                lugarComedor.acquire();
                System.out.println(n + " Consiguio lugar. ES PACIENTE?: " + espera);
                pudoEntrar = true;

            } else {
                if (!lugarComedor.tryAcquire()) {
                    System.out.println(n + " No hay lugar. ES PACIENTE?: " + espera);
                } else {
                    pudoEntrar = true;
                    System.out.println(n + " Consiguio lugar y no tuvo que esperar. ES PACIENTE?: " + espera);
                }
            }

        }

        return pudoEntrar;
    }

    public boolean sentarseMesa(String n) throws InterruptedException {
        boolean comio = true;
        System.out.println(n + " Esperando a que se complete la mesa");
        try {
            int pos = barrera.await();
            if (pos == 0) {
                System.out.println(n + " MESA COMPLETA, pueden empezar a comer");

            }
        } catch (BrokenBarrierException b) {
            System.out.println(n + " El comedor cerro antes de que se complete la mesa y se va");
            comio = false;
            lugarComedor.release();

        }

        return comio;
    }

    public void salir(String n) {
        lugarComedor.release();

    }

    // Reloj
    public void cerrarComedor() throws InterruptedException {
        barrera.reset(); // si habian visitantes esperando se van.
        mutex.acquire();
        abierto = false;
        mutex.release();

    }

}
