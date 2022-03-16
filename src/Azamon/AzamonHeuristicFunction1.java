package Azamon;

import aima.search.framework.HeuristicFunction;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AzamonHeuristicFunction1 implements HeuristicFunction{
    public double getHeuristicValue(Object state) {
        BigDecimal bd = new BigDecimal(Double.toString(((AzamonState) state).get_cost()));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}