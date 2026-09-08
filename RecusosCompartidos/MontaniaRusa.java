package RecusosCompartidos;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.BrokenBarrierException;

//lock para entrar y ver la capacidad, cyclic barrier para verificar que esten todos los asientos ocupados para empezar
//el juego.
public class MontaniaRusa {
    private int espacioEspera, contEspera;
    private CyclicBarrier comenzarJuego, bajarJuego;
    private int contTurno, total = 5;
    private final int fichas = 5;

    private Lock lock = new ReentrantLock();
    private Condition subir = lock.newCondition();

    private boolean enViaje = false;
    private boolean abierto = true;

    public MontaniaRusa(int cEspera) {
        this.espacioEspera = cEspera;
        this.contTurno = 0;
        this.contEspera = 0;
        this.comenzarJuego = new CyclicBarrier(5);
        this.bajarJuego = new CyclicBarrier(5);
    }

    public boolean intentarSubir(String n) throws InterruptedException {
        
        boolean exito;
        lock.lock();
        try {
            if (contEspera < espacioEspera) {
                contEspera++;
                exito = true;
            } else {
                exito = false;
            }
        } finally {
            lock.unlock();
        }
        return exito;
    }

    public boolean subirMontania(String n) throws InterruptedException {
        boolean exito = true;
        lock.lock();
        try {
            while ((enViaje || contTurno >= total) && abierto) {
                subir.await();
            }

            if (!abierto) {
                //Si la montaña cerro no puede jugar y devuelve false
                exito = false;
            } else {
                //Si no significa que puede subir y ocupa un asiento
                contTurno++;
            }

            contEspera--; //Deja libre su lugar en el espacio de espera

        } finally {
            lock.unlock();
        }
        return exito;
    }

    public boolean iniciarJuego(String n) throws InterruptedException, BrokenBarrierException {
        boolean jugo = true;
        try {
            int pos = comenzarJuego.await(); // Devuelve la posicion en que llegaron, siendo 0 el ultimo.

            if (pos == 0) {
                System.out.println(n + " Comienza el juego!!");
                lock.lock();
                enViaje = true;
                lock.unlock();
            }

        } catch (BrokenBarrierException b) {
            System.out.println(n + " La Montaña rusa cerro antes de que se llenen los asientos y se va");
            jugo = false;
        }

        return jugo;
    }

    public int bajarMontania(String n) throws InterruptedException, BrokenBarrierException {
        bajarJuego.await(); //Para que todos bajen al mismo tiempo
        lock.lock();
        try {

            contTurno--;
            System.out.println(n + " bajo de la montania rusa" + contTurno);
            if (contTurno == 0) {
                enViaje = false;
                subir.signalAll();
                System.out.println("TERMINO EL TURNO MONTAÑA RUSA");
            }

        } finally {
            lock.unlock();
        }

        return fichas;
    }

    // Reloj
    public void cerrarMontania() {
        lock.lock();
        try { // blockingqueue desbloquearlos
            comenzarJuego.reset(); // Si habian visitantes esperando se van.
            abierto = false;
        } finally {
            lock.unlock();
        }

    }

}