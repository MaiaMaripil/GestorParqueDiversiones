package Clases;

import Hilos.*;
import RecusosCompartidos.*;

public class MenuParque {
    public static void main(String arg[]) {
        MontaniaRusa unaMontania = new MontaniaRusa(2);
        RealidadVirtual rV = new RealidadVirtual(2, 2, 2);
        AreaPremios aPremios = new AreaPremios();
        Comedor unComedor = new Comedor(1);
        Teatro unTeatro = new Teatro();
        ParqueDiversiones unParque = new ParqueDiversiones(2, unaMontania, rV, aPremios, unComedor, unTeatro);

        Thread reloj = new Thread(new Reloj("RELOJ", unParque));
        reloj.start();

        Thread encargadoPremios = new Thread(new Encargado("ENCARGADO PREMIOS", unParque));
        Thread encargadoRV = new Thread(new Encargado("ENCARGADO RV", unParque));
        encargadoPremios.start();
        encargadoRV.start();

        Thread[] visitantes = new Thread[10];
        for (int i = 0; i < visitantes.length; i++) {
            Thread visitante = new Thread(new Visitante("VISITANTE " + (i + 1), unParque));
            visitantes[i] = visitante;
            visitante.start();
            try{
                Thread.sleep((int)Math.random()*1000);

            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        for (int i = 0; i < visitantes.length; i++) {
            try {
                visitantes[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        try {
            reloj.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("TERMINO LA EJECUCION");

    }
}
