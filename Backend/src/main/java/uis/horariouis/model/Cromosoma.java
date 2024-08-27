package uis.horariouis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Cromosoma {

    private List<Gen> genes;
    private int aptitud;

    // Constructor vacío
    public Cromosoma() {
    }

    // Constructor con lista de genes
    public Cromosoma(List<Gen> genes) {
        this.genes = genes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cromosoma{");
        sb.append("aptitud=").append(aptitud).append(", genes=[");
        for (Gen gen : genes) {
            sb.append(gen.toString()).append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }
}
