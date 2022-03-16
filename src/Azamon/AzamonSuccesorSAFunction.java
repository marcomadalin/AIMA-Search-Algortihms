package Azamon;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class AzamonSuccesorSAFunction implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList retval = new ArrayList();
        AzamonState actual_State = (AzamonState) state;


        /* Si tenim n paquets i m ofertes tenim les següents posibles solucions:
            -Amb move_package -> n*m
            -Amb swap_offer -> n*n
            Buscarem un numero aleatori entre 0 i n*n+m*m
         */
        int n = actual_State.packets.size();
        int m = actual_State.shipping.size();

        int mover_vs_swap = (int) Math.floor(Math.random() * ((n * m + n * n) + 1));          // Valor entre 1 y (n*m+n*n), ambos incluidos.

        if (mover_vs_swap <= n * m) {                                                  //utilizamos move

            int paq = (int) Math.floor(Math.random() * n);                    // Valor entre 0 y n-1, ambos incluidos.
            int offr = (int) Math.floor(Math.random() * m);                   // Valor entre 0 y m-1, ambos incluidos.

            AzamonState new_State = new AzamonState(actual_State);
            boolean valid = new_State.move_package(paq, offr);

            while (!valid) {
                paq = (int) Math.floor(Math.random() *n);
                offr = (int) Math.floor(Math.random() *m);
                valid = new_State.move_package(paq, offr);

            }
            retval.add(new Successor("package " + paq + " moved to offer "  + offr, new_State));
        } else {                                                                    //utilizamos swap

            int paq1 = (int) Math.floor(Math.random() * n);
            int paq2 = (int) Math.floor(Math.random() * n);

            AzamonState new_State = new AzamonState(actual_State);
            boolean valid = new_State.swap_offers(paq1, paq2);

            while ((paq1 == paq2) || (!valid)) {
                paq1 = (int) Math.floor(Math.random() * n);
                paq2 = (int) Math.floor(Math.random() * n);

                valid = new_State.swap_offers(paq1, paq2);
            }

            retval.add(new Successor("package " + paq1 + " swapped with" + paq2, new_State));
        }

        return retval;
    }
}