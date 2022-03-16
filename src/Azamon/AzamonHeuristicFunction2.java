package Azamon;

import aima.search.framework.HeuristicFunction;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class AzamonHeuristicFunction2 implements HeuristicFunction {
    public double getHeuristicValue(Object state) {
        AzamonState s = (AzamonState) state;
        BigDecimal bd = new BigDecimal(Double.toString(s.get_cost()));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return (Math.log(bd.doubleValue()) / Math.log(2)) - (Math.log(s.get_happiness()) / Math.log(2));
    }
}
