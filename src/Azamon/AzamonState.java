package Azamon;

import IA.Azamon.Oferta;
import IA.Azamon.Paquete;
import IA.Azamon.Paquetes;
import IA.Azamon.Transporte;

import java.util.*;

public class AzamonState {
    //Attributes
    static Paquetes packets;
    static Transporte shipping;
    private ArrayList<Integer> offer;                                                                                   //Oferta assignada del paquet
    private ArrayList<Double> capacity;                                                                                 //Capacitat restant de la oferta
    private double cost;                                                                                                //Cost de transport total, preu + almacenatge
    private int happiness;                                                                                              //Felicitat

    //Constructors
    public AzamonState(int pack_size, int pack_seed, int trans_seed, double prop) {
        packets = new Paquetes(pack_size, pack_seed);
        shipping = new Transporte(packets, prop, trans_seed);
        offer = new ArrayList<Integer>(pack_size);
        capacity = new ArrayList<Double>(shipping.size());
        cost = happiness = 0;

        for (int i = 0; i < pack_size; ++i) offer.add(-1);
        for (int i = 0; i < shipping.size(); i++) capacity.add(0.0);
    }

    public AzamonState(AzamonState s) {
        packets = s.packets;
        shipping = s.shipping;
        offer = new ArrayList<Integer>(s.offer);
        capacity = new ArrayList<Double>(s.capacity);
        cost = s.cost;
        happiness = s.happiness;
    }

    //Mètodes públics
    public int get_happiness() {
        return happiness;
    }

    public double get_cost() {
        return cost;
    }

    //operadors
    public boolean move_package(int packet, int new_offer) {                                                            //Operador 1, moure un paquet a altra oferta
        if (offer.get(packet) != new_offer && valid_deadline(packet, new_offer) && enough_capacity(packet, new_offer, 0)) {
            update_cost(packet, -1);
            update_happiness(packet, -1);
            update_capacity(packet, -1);

            offer.set(packet, new_offer);

            update_cost(packet, 1);
            update_happiness(packet, 1);
            update_capacity(packet, 1);
            return true;
        }
        return false;
    }

    public boolean swap_offers(int packet1, int packet2) {                                                              //Operador 2, intercnavi d'ofertes de paquets
        if (packet1 != packet2 && valid_deadline(packet1, offer.get(packet2)) &&
                valid_deadline(packet2, offer.get(packet1)) &&
                enough_capacity(packet1, offer.get(packet2), -packets.get(packet2).getPeso()) &&
                enough_capacity(packet2, offer.get(packet1), -packets.get(packet1).getPeso())) {
            update_cost(packet1, -1);
            update_cost(packet2, -1);
            update_happiness(packet1, -1);
            update_happiness(packet2, -1);
            update_capacity(packet1, -1);
            update_capacity(packet2, -1);

            int aux_offer = offer.get(packet1);
            offer.set(packet1, offer.get(packet2));
            offer.set(packet2, aux_offer);

            update_cost(packet1, 1);
            update_cost(packet2, 1);
            update_happiness(packet1, 1);
            update_happiness(packet2, 1);
            update_capacity(packet1, 1);
            update_capacity(packet2, 1);
            return true;
        }
        return false;
    }

    public void generate_solution1() {
        sort_packets_completly(1);
        for (int i = 0; i < packets.size(); ++i) {
            int act_shippment = 0;
            while (!valid_deadline(i, act_shippment) || !enough_capacity(i, act_shippment, 0)) ++act_shippment;
            offer.set(i, act_shippment);
            update_capacity(i, 1);
            update_cost(i, 1);
            update_happiness(i, 1);
        }
    }

    public void generate_solution2() {
        sort_packets_priority();
        int act_shippment = 0;
        for (int i = 0; i < packets.size(); ++i) {
            while (!valid_deadline(i, act_shippment) || !enough_capacity(i, act_shippment, 0)) ++act_shippment;
            offer.set(i, act_shippment);
            update_capacity(i, 1);
            update_cost(i, 1);
            update_happiness(i, 1);
        }
    }

    public void generate_solution3() {
        sort_packets_completly(-1);
        sort_shipping_completly();
        for (int i = 0; i < packets.size(); ++i) {
            int act_shippment = 0;
            while (!valid_deadline(i, act_shippment) || !enough_capacity(i, act_shippment, 0)) ++act_shippment;
            offer.set(i, act_shippment);
            update_capacity(i, 1);
            update_cost(i, 1);
            update_happiness(i, 1);
        }
    }

    public void print_offers() {
        for (int i = 0; i < offer.size(); ++i)
            System.out.println("Packet " + i + " assigned to offer: " + offer.get(i));
        System.out.println();
    }

    public void print_capacity() {
        for (int i = 0; i < capacity.size(); ++i)
            System.out.println("Capacity of container " + i + ": " + capacity.get(i));
        System.out.println();
    }

    public void print_packets() {
        for (int i = 0; i < packets.size(); ++i) System.out.println(packets.get(i));
        System.out.println();
    }

    public void print_shipping() {
        for (int i = 0; i < shipping.size(); ++i) System.out.println(shipping.get(i));
        System.out.println();
    }

    public boolean is_goal_state() {
        return false;                                                                                                    //Sempre fals per a búsqueda local
    }

    //Mètodes privats
    private boolean valid_deadline(int packet, int new_offer) {                                                        //Comprova si el paquet arriba en el temps establert
        return ((packets.get(packet).getPrioridad() == 0 && shipping.get(new_offer).getDias() == 1) ||
               (packets.get(packet).getPrioridad() == 1 && shipping.get(new_offer).getDias() <= 3) ||
               (packets.get(packet).getPrioridad() == 2 && shipping.get(new_offer).getDias() <= 5));
    }

    private boolean enough_capacity(int packet, int new_offer, double weight) {                                         //Comprova si no es sobrepassa la capacitat de la nova oferta
        return (packets.get(packet).getPeso() + capacity.get(new_offer) + weight <=
                shipping.get(new_offer).getPesomax());
    }

    private void update_cost(int packet, int sign) {
        cost += shipping.get(offer.get(packet)).getPrecio() * packets.get(packet).getPeso() * sign;                     //Cost transport paquet
        if (shipping.get(offer.get(packet)).getDias() == 5) cost += 0.5 * sign * packets.get(packet).getPeso();         //Cost almacenatge, 2 dies * 0,25 €/dia
        else if (shipping.get(offer.get(packet)).getDias() >= 3) cost += 0.25 * sign * packets.get(packet).getPeso();   //Cost almacenatge, 1 dia * 0,25 €/dia
    }

    private void update_happiness(int packet, int sign) {
        if (packets.get(packet).getPrioridad() == 1)  happiness += (3 - shipping.get(offer.get(packet)).getDias()) * sign;
        else if (packets.get(packet).getPrioridad() == 2) happiness += (5 - shipping.get(offer.get(packet)).getDias()) * sign;
    }

    private void update_capacity(int packet, int sign) {
        capacity.set(offer.get(packet), capacity.get(offer.get(packet)) + (packets.get(packet).getPeso() * sign));
    }

    void sort_packets_priority () {
        Collections.sort(packets, new Comparator<Paquete>() {
            @Override
            public int compare(Paquete a, Paquete b) {
                return a.getPrioridad() - b.getPrioridad();
            }
        });
    }

    void sort_packets_completly(int sign) {
        Collections.sort(packets, new Comparator<Paquete>() {
            @Override
            public int compare(Paquete a, Paquete b) {
                int priority = a.getPrioridad() - b.getPrioridad();
                if (priority == 0) {
                    double weight = a.getPeso() - b.getPeso();
                    if (weight < 0) return -1*sign;
                    else if (weight == 0) return 0;
                    else return 1*sign;
                }
                else return priority;
            }
        });
    }

    void sort_shipping_completly () {
        Collections.sort(shipping, new Comparator<Oferta>() {
            @Override
            public int compare(Oferta a, Oferta b) {
                int days = a.getDias() - b.getDias();
                if (days == 0) {
                    double price = a.getPrecio() - b.getPrecio();
                    if (price == 0) {
                        double weight = a.getPesomax() - b.getPesomax();
                        if (weight < 0) return 1;
                        if (weight == 0) return 0;
                        else return -1;
                    }
                    else {
                        if (price < 0) return -1;
                        else if (price == 0) return 0;
                        else return 1;
                    }
                }
                else return days;
            }
        });
    }

}
