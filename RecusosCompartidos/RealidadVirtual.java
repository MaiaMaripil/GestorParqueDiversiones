package RecusosCompartidos;

import java.util.concurrent.Semaphore;

public class RealidadVirtual {
    private Semaphore vr;
    private Semaphore manoplas;
    private Semaphore base;
    private Semaphore cliente, equipoCompleto;
    private final int fichas = 5;
    private boolean puedeIrse = false;
    private Semaphore mutex = new Semaphore(1);

    public RealidadVirtual(int cantVr, int cantManoplas, int cantBase) {
        this.vr = new Semaphore(cantVr);
        this.manoplas = new Semaphore(cantManoplas);
        this.base = new Semaphore(cantBase);
        this.cliente = new Semaphore(0);
        this.equipoCompleto = new Semaphore(0);

    }

    // Encargado
    public void atenderRV(String n) throws InterruptedException {
        System.out.println(n + " Esperando a que llegue cliente...");
        cliente.acquire(); // espera a cliente
        mutex.acquire();
        if (!puedeIrse) {
            mutex.release();
            vr.acquire();
            manoplas.acquire(2);
            base.acquire();
            System.out.println(n + " Llego un cliente y puedo entregarle el equipo completo, se lo doy");
            equipoCompleto.release();

        } else {
            mutex.release();
        }

    }

    // Visitante
    public void ingresarRV(String n) throws InterruptedException {
        cliente.release(); // avisa al encargado que esta esperando a ser atendido
        System.out.println(n + " Esperando a adquirir el equipo completo");
        equipoCompleto.acquire();

    }

    public int salirRV(String n) {
        vr.release();
        manoplas.release(2);
        base.release();

        return fichas;
    }

    
    public boolean getPuedeIrse() throws InterruptedException {
        mutex.acquire();
        boolean rta = puedeIrse;
        mutex.release();

        return rta;
    }

    // Reloj
    public void setPuedeIrse() throws InterruptedException {
        mutex.acquire();
        puedeIrse =!puedeIrse;
        cliente.release(); // para despertarlo
        mutex.release();
    }
}
