package ar.edu.unicen.isistan.asistan.storage.database.mobility.labels;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Label {

    HOME(0,"Casa","Estar en casa"),
    WORK(1,"Trabajo","Ir al trabajo o actividades relacionadas al trabajo"),
    STUDY(2,"Estudio","Asistir a clases, juntarse a estudiar, etc."),
    HEALTHY(3,"Salud","Ir al médico, visitar a un conocido internado, etc."),
    PERSONAL_CARE(4,"Cuidado personal","Por ejemplo, ir a la peluquería."),
    PAPERWORK(5,"Trámites","Realizar trámites burocraticos."),
    DAILY_SHOP(6,"Compras diarias","Hacer compras de insumos diarios como comida, productos de limpieza, etc."),
    SHOPPING(7,"Compras generales","Comprar ropa, electrodomésticos, tecnología, etc."),
    EAT(8,"Comer o tomar algo","Salir a comer o tomar algo."),
    SPORT(9,"Deporte","Realizar deporte o actividad física."),
    OUTDOOR(10,"Aire libre","Actividad al aire libre, como una plaza."),
    CULTURAL(11,"Cultural","Ir al teatro, al cine, a una biblioteca, a un recital, etc."),
    PLAYFUL(12,"Lúdico","Ir al casino, al bingo, a Sacoa, etc."),
    RECREATION(13,"Recreación","Cualquier actividad que me resulte recreativa."),
    WITH_FRIENDS(14,"Amigos","Estuve con amigos."),
    WITH_FAMILY(15,"Familia","Estuve con mi familia."),
    WITH_LOVE(16,"Mi pareja","Estuve con mi pareja"),
    WITH_WORK_PARTNERS(17,"Compañeros de trabajo","Estuve con compañeros de trabajo."),
    WITH_STUDY_PARTNERS(18,"Compañeros de estudio","Estuve con compañeros de estudio."),
    SOCIAL_EVENT(19,"Evento social","Un casamiento, un cumpleaños, una cena de fin de año, etc."),
    TAKING_SOMEONE(20,"Dejar/buscar a alguien","Pasar a bucar o dejar a alguien"),
    TAKING_SOMETHING(21,"Dejar/buscar algo","Pasar a bucar o dejar a algo (un paquete, una carta, etc.)"),
    WAIT(22,"Esperando","Esperar algo o algien. Esperar a un amigo, esperar en una parada de colectivo, en una terminal, etc."),
    ACCOMMODATION(23,"Alojamiento","Estar en lugar de hospedaje"),
    RELIGION(24,"Religion o culto","Ir a misa, al cementerio, etc."),
    REPAIR(25,"Reparación de bienes","Ir al taller por el auto, al técnico para arreglar un aparato roto, etc."),
    COMMUNITY_SERVICE(26,"Servicio voluntario","Servicio voluntario a la comunidad o similar."),
    MOVE(27,"Traslado","La razón del viaje es simplemente llegar a destino."),

    VISIT_ACTIVITY(98,"Actividad en el lugar","Actividades en el lugar",HOME,WORK,STUDY,HEALTHY,PERSONAL_CARE,PAPERWORK,DAILY_SHOP,SHOPPING,EAT,SPORT, OUTDOOR, CULTURAL, PLAYFUL,RECREATION,WITH_FRIENDS,WITH_FAMILY,WITH_LOVE,WITH_WORK_PARTNERS,WITH_STUDY_PARTNERS,SOCIAL_EVENT,TAKING_SOMEONE,TAKING_SOMETHING,WAIT,ACCOMMODATION,RELIGION,REPAIR,COMMUNITY_SERVICE),
    COMMUTE_REASON(99,"Motivo de viaje","Razo del viaje",SPORT, OUTDOOR,RECREATION,RELIGION,MOVE,WITH_FRIENDS,WITH_FAMILY,WITH_LOVE,WITH_WORK_PARTNERS,WITH_STUDY_PARTNERS,TAKING_SOMEONE,TAKING_SOMETHING);

    private int code;
    private String name;
    private String description;
    private ArrayList<Label> subLabels;

    Label(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.subLabels = null;
    }

    Label(int code, String name, String description, Label... labels) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.subLabels = new ArrayList<>(Arrays.asList(labels));
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Label> getSubLabels() {
        return subLabels;
    }

    public void setSubLabels(ArrayList<Label> subLabels) {
        this.subLabels = subLabels;
    }

    public static Label get(int code) {
        for (Label activity: Label.values())
            if (activity.getCode() == code)
                return activity;
        return null;
    }

    public static ArrayList<Label> get(List<Integer> codes) {
        ArrayList<Label> labels = new ArrayList<>();
        for (Integer code: codes) {
            Label label = Label.get(code);
            if (label != null)
                labels.add(label);
        }
        return labels;
    }

    public int getColor() {
        float value = ((float) this.code / Label.values().length) * 360;
        return Color.HSVToColor(215, new float[]{value, 0.8F, 0.7F});
    }

}
