package Hilos;

import RecusosCompartidos.ParqueDiversiones;

public class Encargado implements Runnable {
    private String nombre;
    private ParqueDiversiones parque;

    public Encargado(String n, ParqueDiversiones p) {
        this.nombre = n;
        this.parque = p;
    }

    public void run() {
        try {
            switch (nombre) {
                case "ENCARGADO PREMIOS":
                    encargadoPremios();
                    break;
                case "ENCARGADO RV":
                    encargadoRV();
                    break;

            }
            System.out.println("***"+nombre + " TERMINO SU TRABAJO Y SE VA DEL PARQUE ***");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void encargadoPremios() throws InterruptedException {
        System.out.println(nombre + ": atendiendo premios");
        while (!parque.getAreaPremios().getPuedeIrse()) {
            
            parque.getAreaPremios().atender(nombre);

        }
    }

    private void encargadoRV() throws InterruptedException {
        System.out.println(nombre + ": atendiendo RV");
        while (!parque.getRealidadVirtual().getPuedeIrse()) {
            
            parque.getRealidadVirtual().atenderRV(nombre);

        }
    }
}