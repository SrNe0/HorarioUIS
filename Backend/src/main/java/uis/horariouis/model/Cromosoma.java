package uis.horariouis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter // Anotación de Lombok que genera automáticamente los métodos setter para los atributos de la clase.
@Getter // Anotación de Lombok que genera automáticamente los métodos getter para los atributos de la clase.
public class Cromosoma {

    private List<Gen> genes; // Lista de genes que componen este cromosoma.
    private int aptitud; // Valor de aptitud del cromosoma, que indica qué tan buena es la solución representada por este cromosoma.

    // Constructor vacío
    public Cromosoma() {
    }

    // Constructor que inicializa el cromosoma con una lista de genes
    public Cromosoma(List<Gen> genes) {
        this.genes = genes;
    }

    @Override
    public String toString() {
        // Método para representar el objeto Cromosoma como una cadena de texto.
        // Incluye la aptitud y los detalles de cada gen que compone el cromosoma.
        StringBuilder sb = new StringBuilder();
        sb.append("Cromosoma{");
        sb.append("aptitud=").append(aptitud).append(", genes=[");
        for (Gen gen : genes) {
            sb.append(gen.toString()).append(", "); // Añade la representación de cada gen a la cadena.
        }
        sb.append("]}");
        return sb.toString(); // Retorna la representación completa del cromosoma.
    }
}
