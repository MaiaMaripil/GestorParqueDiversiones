package RecusosCompartidos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Clases.Premio;

public class AreaPremios {
    // Cola sincrónica
    private SynchronousQueue<Integer> entregaFichas = new SynchronousQueue<>();
    private SynchronousQueue<Premio> entregaPremios = new SynchronousQueue<>();
    private Map<Integer, String> premios;
    private Lock lock;
    private boolean puedeIrse = false;
    private int fin = -1;

    public AreaPremios() {
        this.lock = new ReentrantLock();

        this.premios = new ConcurrentHashMap<>();
        premios.put(10, "Oso Gigante");
        premios.put(8, "Remera");
        premios.put(6, "Gorra");
        premios.put(3, "Llavero");
    }

    // visitante
    public Premio canjearFichas(String n, int cantFichas) throws InterruptedException {
      
        Premio miPremio = null;
        System.out.println(
                n + " Va a canjear: " + cantFichas + " fichas, las entrega  y espera el premio correspondiente...");
        entregaFichas.put(cantFichas); // se bloquea hasta que hagan take

        System.out.println(n + " Esperando el premio....");
        miPremio = entregaPremios.take();
        return miPremio;

    }

    // Encargado
    public void atender(String n) throws InterruptedException {
        System.out.println(n + " Espera a que llegue un visitante a intercambiar sus fichas");
        int fichas = entregaFichas.take();
        boolean irse;
        lock.lock();
        try {
            irse = puedeIrse;
        } finally {
            lock.unlock();
        }

        if (!irse) {
            int vuelto = fichas;
            int mejor = -1;
            String premio = "No tiene premio";

            for (Integer costo : premios.keySet()) {
                if (costo <= fichas && costo > mejor) {
                    mejor = costo;
                }
            }

            if (mejor != -1) {
                premio = premios.get(mejor);
                vuelto = fichas - mejor;
            }
            entregaPremios.put(new Premio(vuelto, premio));
        }

    }

    // Encargado
    public boolean getPuedeIrse() throws InterruptedException {
        boolean rta;
        lock.lock();
        try {
            rta = puedeIrse;

        } finally {
            lock.unlock();
        }

        return rta;
    }

    // Reloj
    public void setPuedeIrse() throws InterruptedException {

        lock.lock();
        try {
            puedeIrse = !puedeIrse;
            entregaFichas.put(fin);// para despertarlo

        } finally {
            lock.unlock();
        }

    }

}

/*
 * class Premio {
 * int vuelto;
 * String premio;
 * 
 * public Premio(int v, String p) {
 * this.vuelto = v;
 * this.premio = p;
 * }
 * 
 * public int getVuelto() {
 * return this.vuelto;
 * }
 * 
 * public String getPremio() {
 * return this.premio;
 * }
 * }
 */
