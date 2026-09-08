package RecusosCompartidos;

import java.util.concurrent.Semaphore;

public class ParqueDiversiones {
    private Semaphore molinetes;
    private boolean abierto = false;
    private boolean actividadesAbiertas=false;
    private Semaphore mutex;

    MontaniaRusa montaniaRusa;
    RealidadVirtual realidadVirtual;
    AreaPremios areaPremios;
    Comedor comedor;
    Teatro teatro;

    public ParqueDiversiones(int k, MontaniaRusa mont, RealidadVirtual rV, AreaPremios aP, Comedor c, Teatro t) {
        this.molinetes = new Semaphore(k);
        this.mutex=new Semaphore(1);
        this.montaniaRusa = mont;
        this.realidadVirtual = rV;
        this.areaPremios = aP;
        this.comedor = c;
        this.teatro = t;
    }

    // Visitante
    public void ingresoMolinete(String n) throws InterruptedException {
        molinetes.acquire();
        System.out.println(n + " Pasando por molinete...");
    }

    public void salirMolinete(String n) {
        molinetes.release();
        System.out.println(n + " Entro al parque!!");
    }

    public MontaniaRusa getMontania() {
        return this.montaniaRusa;
    }

    public RealidadVirtual getRealidadVirtual() {
        return this.realidadVirtual;
    }

    public AreaPremios getAreaPremios() {
        return this.areaPremios;
    }

    public Comedor getComedor() {
        return this.comedor;

    }

    public Teatro getTeatro() {
        return this.teatro;
    }

    // Reloj
    public void abrirParque() throws InterruptedException {
        mutex.acquire();
        this.abierto=true;
        this.actividadesAbiertas=true;
        teatro.abrir();
        System.out.println("***9 hs!! ABRIO EL PARQUE***");
        mutex.release();

    }

     public void cerrarIngreso()throws InterruptedException{
        mutex.acquire();
        this.abierto=false;
        System.out.println("*** 18hs!! CERRO EL INGRESO***");
        mutex.release();
    }

    public void cerrarActividades() throws InterruptedException {
        mutex.acquire();
        this.actividadesAbiertas=false;
        this.comedor.cerrarComedor();
        this.montaniaRusa.cerrarMontania();
        this.teatro.cerrar();
        
        System.out.println("***19 hs!!! CERRARON LAS ACTIVIDADES***");
        mutex.release();

    }

   

    public void cerrarParque()throws InterruptedException{
        mutex.acquire();
        areaPremios.setPuedeIrse();
        realidadVirtual.setPuedeIrse();
        System.out.println("***23hs!! CERRO EL PARQUE***");
        
        mutex.release();
    }

    public boolean abierto() throws InterruptedException {
        mutex.acquire();
        boolean exito=abierto;
        mutex.release();
        return exito;
    }

     public boolean actAbiertas() throws InterruptedException {
        mutex.acquire();
        boolean exito=actividadesAbiertas;
        mutex.release();
        return exito;
    }



}