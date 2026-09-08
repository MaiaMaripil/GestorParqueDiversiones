package Hilos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

import Clases.Premio;
import RecusosCompartidos.ParqueDiversiones;

public class Visitante implements Runnable {
    private String nombre;
    private boolean esPaciente;
    private boolean irShopping;
    private ParqueDiversiones parque;
    private int fichas;
    private List<Integer> listaActividades;

    public Visitante(String n, ParqueDiversiones p) {
        this.nombre = n;
        this.parque = p;
        this.fichas = 0;
        Random random = new Random();
        this.irShopping = random.nextBoolean();

        this.esPaciente = random.nextBoolean();
        listaActividades = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        Collections.shuffle(this.listaActividades); // Mezcla el orden de las actividades
    }

    private boolean actividades(int act) throws InterruptedException, BrokenBarrierException {
        boolean exito = true;
        switch (act) {
            case 0:
                exito = montaniaRusa();
                break;
            case 1:
                realidadVirtual();
                break;
            case 2:
                areaPremios();
                break;
            case 3:
                exito = comedor();
                break;
            case 4:
                exito = teatro();
                break;
        }

        return exito;

    }

    private void actividadesObligatorias() throws InterruptedException, BrokenBarrierException {

        // Mientras haya actividades por completar de la lista
        while (!listaActividades.isEmpty() && parque.actAbiertas()) {

            int act = listaActividades.get(0);

            if (actividades(act)) {
                // Si pudo realizar la actividad la saca de la lista
                listaActividades.remove(0);

            } else {
                // Si no pudo realizarla la saca y la agrega al final
                int actividadPendiente = listaActividades.remove(0);
                listaActividades.add(actividadPendiente);
            }

        }
    }

    private void actividadesRandom() throws InterruptedException, BrokenBarrierException {

        while (parque.actAbiertas()) {

            int act = (int) (Math.random() * 5);

            actividades(act);
        }
    }

    private boolean montaniaRusa() throws InterruptedException, BrokenBarrierException {
        System.out.println(nombre + " VA A MONTAÑA RUSA");
        // intenta ingresar a la atraccion
        boolean exito = parque.getMontania().intentarSubir(nombre);

        if (exito) {
            // si pudo ingresar
            System.out.println(nombre + " esperando a conseguir asiento en MONTAÑA RUSA");
            exito = parque.getMontania().subirMontania(nombre);
            if (exito) {
                System.out.println(nombre + " consiguio asiento en Montaña rusa");
                exito = parque.getMontania().iniciarJuego(nombre);
                if (exito) {
                    System.out.println(nombre + " viajando en montaña!!!");
                    Thread.sleep(5000);
                    fichas = +parque.getMontania().bajarMontania(nombre);
                }

            } else {
                System.out.println(nombre + " La montaña rusa no esta abierta");
            }

        }
        return exito;
    }

    private void realidadVirtual() throws InterruptedException {
        System.out.println(nombre + " VA A REALIDAD VIRTUAL");

        parque.getRealidadVirtual().ingresarRV(nombre);
        System.out.println(nombre + " Obtuvo el equipo completo, jugando...");
        Thread.sleep(5000);
        System.out.println(nombre + " Termino de jugar y devuelve el equipo");
        fichas += parque.getRealidadVirtual().salirRV(nombre);
        System.out.println(nombre + " Recibio fichas RV");

    }

    private void areaPremios() throws InterruptedException {
        System.out.println(nombre + " VA A AREA DE PREMIOS, tiene " + fichas + " fichas en total");

        int canjeo = (int) (Math.random() * fichas);
        fichas -= canjeo;
        Premio miPremio = parque.getAreaPremios().canjearFichas(nombre, canjeo);
        fichas += miPremio.getVuelto();
        System.out.println(nombre + " Recibi como premio: " + miPremio.getPremio() + "| vuelto de fichas: "
                + miPremio.getVuelto() + "| Fichas en total: " + fichas);

    }

    private boolean comedor() throws InterruptedException, BrokenBarrierException {
        System.out.println(nombre + " VA A COMEDOR");
        boolean exito = parque.getComedor().entrarComedor(nombre, esPaciente);

        if (exito) {
            exito = parque.getComedor().sentarseMesa(nombre);
            if (exito) {
                System.out.println(nombre + " Comiendo....");
                Thread.sleep(4000);

                parque.getComedor().salir(nombre);
                System.out.println(nombre + "Sale del comedor");
            }

        } else {
            System.out.println(nombre + " No encontro lugar en el comedor y se va");
        }

        return exito;
    }

    private boolean teatro() throws InterruptedException, BrokenBarrierException {
        System.out.println(nombre + " VA A TEATRO");
        boolean exito;

        exito = parque.getTeatro().ingresarTeatro(nombre);
        if (exito) {
            exito = parque.getTeatro().verEspectaculo(nombre);

            if (exito) {
                // pudo entrar al teatro
                System.out.println(nombre + " Viendo espectaculo...");
                Thread.sleep(4000);
                parque.getTeatro().salir(nombre);
                System.out.println(nombre + " Termino la funcion y Salio del teatro...");
            } else {
                System.out.println(nombre + " No alcanzo a entrar a la funcion...");
            }
        } else {
            System.out.println(nombre + " El teatro cerro.");
        }

        return exito;
    }

    public void run() {
        try {

            if (parque.abierto()) {
                parque.ingresoMolinete(nombre);
                // System.out.println(nombre+" Entrando al parque...");
                Thread.sleep(2000);

                parque.salirMolinete(nombre);

                if (!irShopping) {
                    // primero realiza el paso por cada una de las atracciones
                    actividadesObligatorias();

                    // una vez que paso por todas las atracciones puede ir a las que quiera
                    actividadesRandom();
                    System.out
                            .println(nombre + " Sale del parque. Realizo todas las actividades: "
                                    + listaActividades.isEmpty());
                } else {
                    System.out.println(nombre + " VA AL SHOPPING");
                    Thread.sleep((int) Math.random() * 1000);
                    System.out.println(nombre + " SALIO DEL SHOPPING. Saliendo del parque....");
                }

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}