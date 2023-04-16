package ar.edu.unicen.isistan.asistan.storage.database.mobility.places;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ar.edu.unicen.isistan.asistan.R;

public enum PlaceCategory {

    // Especiales
    DELETED("Eliminado", -3, "Lugar eliminado", R.drawable.symbol_inter, R.drawable.ic_unknown_black_24dp),
    UNSPECIFIED("Desconocido", -2, "Sin especificar", R.drawable.symbol_inter, R.drawable.ic_unknown_black_24dp),
    NEW("Lugar nuevo", -1, "Lugar nuevo", R.drawable.new_icon, R.drawable.ic_new_black_24dp),

    // Lugares de trabajo
    CLIENT_HOME("Casa de un cliente", 101, "La casa de un cliente.", R.drawable.workcase, R.drawable.ic_work_black_24dp),
    CLIENT_PLACE("Negocio de un cliente", 102, "El negocio de un cliente.", R.drawable.workcase, R.drawable.ic_work_black_24dp),
    WORK_PLACE("Trabajo", 103, "Mi lugar de trabajo.", R.drawable.workcase, R.drawable.ic_work_black_24dp),
    SECONDARY_WORK_PLACE("Lugar secundario de trabajo", 104, "Lugar de trabajo secundario.", R.drawable.workcase, R.drawable.ic_work_black_24dp),
    OFFICE("Oficina", 106, "Oficina o edificio de oficinas.", R.drawable.workcase, R.drawable.ic_work_black_24dp),

    // Negocios y comercios
    DAIRY_STORE("Despensa", 201, "Despensa o almacen", R.drawable.conveniencestore, R.drawable.ic_store_black_24dp),
    GREENGROCER("Verdulería", 202, "Verdulería, frutería, o similar", R.drawable.fruits, R.drawable.ic_store_black_24dp),
    BUTCHER("Carnicería", 203, "Carnicería, avícola, o similar.", R.drawable.restaurant_steakhouse, R.drawable.ic_store_black_24dp),
    SEAFOOD("Pescadería", 204, "Pescadería o similar.", R.drawable.restaurant_fish, R.drawable.ic_store_black_24dp),
    SPICES("Dietética", 205, "Dietética.", R.drawable.grocery, R.drawable.ic_store_black_24dp),
    BAKERY("Panadería", 206, "Panadería.", R.drawable.bread, R.drawable.ic_store_black_24dp),
    KIOSK("Kiosko", 207, "Kiosko.", R.drawable.candy, R.drawable.ic_store_black_24dp),
    CLOTHES("Tienda de ropa", 208, "Tienda de ropa, zapatería, etc.", R.drawable.clothes, R.drawable.ic_store_black_24dp),
    JEWELRY("Joyería", 209, "Joyería, relojería, etc.", R.drawable.jewelry, R.drawable.ic_store_black_24dp),
    WINERY("Alcohol", 210, "Vinería o lugar dedicado a la venta de bebidas alcohólicas.", R.drawable.winebar, R.drawable.ic_store_black_24dp),
    PASTA("Fábrica de pasta", 211, "Fábrica de pasta.", R.drawable.restaurant_italian, R.drawable.ic_store_black_24dp),
    HAIRDRESSER("Peluquería", 212, "Peluquería.", R.drawable.barber, R.drawable.ic_store_black_24dp),
    ELECTRICAL("Tienda de electrónica", 213, "Tienda de electrónica.", R.drawable.computers, R.drawable.ic_store_black_24dp),
    ELECTRONICS("Tienda de electrodomesticos", 214, "Fravega, Garbarino, Musimundo, etc.", R.drawable.laundromat, R.drawable.ic_store_black_24dp),
    FURNITURE("Tienda de muebles", 215, "Tienda de muebles.", R.drawable.homecenter, R.drawable.ic_store_black_24dp),
    CRAFT_STORE("Tienda de herramientas", 216, "Ferretería, cerrajería, o similar.", R.drawable.tools, R.drawable.ic_store_black_24dp),
    HEALTHY("Farmacia", 217, "Farmacia, óptica, ortopedia, perfumería, etc.", R.drawable.drugstore, R.drawable.ic_local_hospital_black_24dp),
    PAY_PLACE("Lugar de pago", 218, "Ripsa, rapipago, pagofacil, etc.", R.drawable.bank_euro, R.drawable.ic_store_black_24dp),
    CARPENTER("Carpintería", 219, "Carpintería, maderera, etc.", R.drawable.sawmill, R.drawable.ic_store_black_24dp),
    MARKETPLACE("Feria", 220, "Feria o mercado de pulgas", R.drawable.market, R.drawable.ic_store_black_24dp),
    SUPERMARKET("Supermercado", 221, "Supermercado o hipermercado", R.drawable.supermarket, R.drawable.ic_store_black_24dp),
    SHOPPING("Shopping", 222, "Shopping o centro de compras", R.drawable.mall, R.drawable.ic_store_black_24dp),
    CRAFT("Taller", 223, "Taller, fábrica o lugar de producción de bienes", R.drawable.workshop, R.drawable.ic_store_black_24dp),
    CAR_REPAIR("Taller mecánico", 223, "Taller mecánico o similar", R.drawable.carrepair, R.drawable.ic_store_black_24dp),
    VARIETY_STORE("Bazar", 224, "Bazar o similar.", R.drawable.market, R.drawable.ic_store_black_24dp),

    // Salud
    HOSPITAL("Centro médico", 301, "Hospital, clínica, consultorios médicos, etc.", R.drawable.hospital_building, R.drawable.ic_local_hospital_black_24dp),
    AESTHETIC("Centro de estética", 302, "Centro de estética y belleza.", R.drawable.beautysalon, R.drawable.ic_local_hospital_black_24dp),
    REHABILITATION("Centro de rehabilitacion", 303, "Centro de rehabilitacion, kinesiología, etc.",  R.drawable.hospital_building, R.drawable.ic_local_hospital_black_24dp),
    LABORATORY("Laboratorio", 304, "Laboratorio de análisis clínico o imágenes médicas.", R.drawable.laboratory, R.drawable.ic_local_hospital_black_24dp),
    VETERINARY("Veterinaria", 305, "Veterinaria, tienda de mascotas, peluquería de mascotas, etc.", R.drawable.veterinary, R.drawable.ic_store_black_24dp),

    // Educacion
    SCHOOL("Escuela", 331, "Escuela primaria o secundaria", R.drawable.school, R.drawable.ic_school_black_24dp),
    UNIVERSITY("Universidad", 332, "Universidad, terciario o instituto de educación superior", R.drawable.university, R.drawable.ic_school_black_24dp),
    KINDERGARTEN("Preescolar", 333, "Jardín maternal, jardín de infantes o guardería", R.drawable.childmuseum, R.drawable.ic_school_black_24dp),
    MUSIC_SCHOOL("Escuela de música", 334, "Escuela de música", R.drawable.music_rock, R.drawable.ic_school_black_24dp),
    LANGUAGE_SCHOOL("Escuela de idiomas", 335, "Escuela o institudo de inglés, francés, etc.", R.drawable.letter_a, R.drawable.ic_school_black_24dp),
    DRIVING_SCHOOL("Escuela de manejo", 336, "Escuela para aprender a manejar automóviles.", R.drawable.car, R.drawable.ic_school_black_24dp),
    RESEARCH_INSTITUTE("Instituto de investigación", 337, "Institudo de investigación o laboratorio.", R.drawable.university, R.drawable.ic_school_black_24dp),

    // Religion
    CHURCH("Iglesia", 361, "Iglesia, capilla, lugares de culto, etc.", R.drawable.prayer, R.drawable.ic_church_black_24dp),
    CEMETERY("Cementerio", 362, "Cementerio.", R.drawable.cemetary, R.drawable.ic_church_black_24dp),
    CREMATORIUM("Crematorio", 363, "Crematorio", R.drawable.crematorium, R.drawable.ic_church_black_24dp),

    // Transporte
    PARKING("Estacionamiento", 401, "Estacionamiento", R.drawable.parking, R.drawable.ic_in_vehicle_24dp),
    BUS_STOP("Parada de colectivo", 402, "Parada de colectivos", R.drawable.busstop, R.drawable.ic_in_vehicle_24dp),
    TAXI_STOP("Parada de taxi o remiseria", 403, "Parada de taxis o remiseria", R.drawable.taxi, R.drawable.ic_in_vehicle_24dp),
    TERMINAL("Terminal", 404, "Terminal de omnibus", R.drawable.bus, R.drawable.ic_in_vehicle_24dp),
    AIRPORT("Aeropuerto", 405, "Aeropuerto o aeroparque", R.drawable.airport_terminal, R.drawable.ic_in_vehicle_24dp),
    PORT("Puerto", 406, "Puerto o marina", R.drawable.marina, R.drawable.ic_in_vehicle_24dp),
    CAR_WASH("Lavadero de autos", 407, "Lavadero de autos", R.drawable.carwash,  R.drawable.ic_in_vehicle_24dp),
    CAR_RENTAL("Renta de autos", 408, "Lugar donde se alquilan automóviles", R.drawable.carrental, R.drawable.ic_in_vehicle_24dp),
    FUEL("Estación de servicio", 409, "Estación de servicio", R.drawable.fillingstation, R.drawable.ic_in_vehicle_24dp),
    TRAIN_STATION("Estación de trenes", 410, "Estación de trenes", R.drawable.train,  R.drawable.ic_in_vehicle_24dp),

    // Entidades
    PUBLIC_SERVICE("Servicio público", 501, "Luz, gas, obras sanitarias, etc.", R.drawable.office_building, R.drawable.ic_entity_black_24dp),
    FINANCIAL_ENTITY("Entidad financiera", 502, "Banco, casa de cambio, financiera, etc.", R.drawable.bank_euro, R.drawable.ic_entity_black_24dp),
    PUBLIC_ORGANIZATION("Entidad pública", 503, "Municipalidad, anses, etc.", R.drawable.townhouse, R.drawable.ic_entity_black_24dp),
    PRIVATE_SERVICE("Servicio privado", 504, "Cablevision, arnet, telefónica, etc.", R.drawable.office_building, R.drawable.ic_entity_black_24dp),
    COMMUNITY_CENTRE("Centro comunitario", 505, "Centro comunitario", R.drawable.communitycentre, R.drawable.ic_entity_black_24dp),
    SOCIAL_FACILITY("Centro de asistencia social", 506, "Centro de asistencia social", R.drawable.welfareroom, R.drawable.ic_entity_black_24dp),
    EMBASSY("Embajada", 507, "Embajada", R.drawable.embassy, R.drawable.ic_entity_black_24dp),
    COURTHOUSE("Juzgado", 508, "Corte o juzgado", R.drawable.court, R.drawable.ic_entity_black_24dp),
    POLICE("Comisaria", 509, "Comisaria o estación de policia",R.drawable.police, R.drawable.ic_entity_black_24dp),
    FIRE_STATION("Bomberos", 510, "Cuartel de bomberos", R.drawable.firemen, R.drawable.ic_entity_black_24dp),
    PRISON("Prision", 511, "Carcel o prisión", R.drawable.prison, R.drawable.ic_entity_black_24dp),
    POST_OFFICE("Correo", 512, "Central o sucursal del correo", R.drawable.postal, R.drawable.ic_entity_black_24dp),
    LABOR_UNION("Sindicato", 513, "Sede de Sindicato laboral.", R.drawable.sozialeeinrichtung, R.drawable.ic_entity_black_24dp),

    // Aire libre y recreación
    PARK("Parque", 601, "Parque y plaza", R.drawable.urbanpark, R.drawable.ic_nature_people_black_24dp),
    BEACH("Playa", 602, "Balneario", R.drawable.beach_icon, R.drawable.ic_nature_people_black_24dp),
    GOLF("Golf", 603, "Campo de golf", R.drawable.golf, R.drawable.ic_nature_people_black_24dp),
    CAMP_SITE("Camping", 604, "Camping o lugar de caravanas", R.drawable.tents, R.drawable.ic_nature_people_black_24dp),
    DOG_PARK("Parque canino", 605, "Parque o plaza para perros", R.drawable.dogs_leash, R.drawable.ic_nature_people_black_24dp),
    HORSE_RIDING("Campo de equitación", 606, "Campo de equitación", R.drawable.horseriding, R.drawable.ic_nature_people_black_24dp),
    NATURE_RESERVE("Reserva natural", 607, "Reserva natural", R.drawable.tree, R.drawable.ic_nature_people_black_24dp),
    WATER_PARK("Parque acuatico", 608, "Aquasol, Aquopolis, etc.", R.drawable.waterpark, R.drawable.ic_nature_people_black_24dp),
    WOOD("Lugar natural", 609, "Bosque, paramo, matorral, pradera, desierto, etc.", R.drawable.forest, R.drawable.ic_nature_people_black_24dp),
    BARE_ROCK("Colina", 610, "Colina, monte o elevación", R.drawable.hill, R.drawable.ic_nature_people_black_24dp),
    ZOO("Zoológico", 611, "Zoológico.", R.drawable.zoo, R.drawable.ic_nature_people_black_24dp),
    THEME_PARK("Parque temático", 612, "Parque temático.", R.drawable.themepark, R.drawable.ic_nature_people_black_24dp),
    AQUARIUM("Aquarium", 613, "Aquarium", R.drawable.aquarium, R.drawable.ic_nature_people_black_24dp),
    VIEWPOINT("Punto de vista", 614, "Punto panorámico", R.drawable.panoramicview, R.drawable.ic_nature_people_black_24dp),
    ATTRACTION("Atracción", 1500, "Atracción turística", R.drawable.urbanpark, R.drawable.ic_nature_people_black_24dp),

    // Deporte
    GYM("Gimnasio", 701, "Gimnasio, centro de fitness o spa", R.drawable.weights, R.drawable.ic_fitness_center_black_24dp),
    SPORT_CLUB("Club deportivo", 702, "Club deportivo", R.drawable.fitness, R.drawable.ic_fitness_center_black_24dp),
    SPORT_FIELD("Cancha", 703, "Cancha de fútbol, de basquet, etc.", R.drawable.soccer, R.drawable.ic_fitness_center_black_24dp),
    SWIMMING_POOL("Natación", 704, "Pileta de natación.", R.drawable.swimming, R.drawable.ic_fitness_center_black_24dp),
    STADIUM("Estadio", 705, "Estadio de fútbol, de basquet, polideportivo, etc.", R.drawable.stadium, R.drawable.ic_fitness_center_black_24dp),
    DOJO("Dojo", 706, "Dojo o lugar donde se practian artes marciales", R.drawable.taekwondo, R.drawable.ic_fitness_center_black_24dp),

    // Comida
    RESTAURANT("Restaurante", 801, "Restaurante o cervezeria", R.drawable.restaurant, R.drawable.ic_restaurant_black_24dp),
    CAFE("Café", 802, "Café o Bar", R.drawable.coffee, R.drawable.ic_restaurant_black_24dp),
    FAST_FOOD("Comida rápida", 803, "Lugar de comida rápida.", R.drawable.fastfood, R.drawable.ic_restaurant_black_24dp),
    ICE_CREAM("Heladería", 804, "Heladería", R.drawable.icecream, R.drawable.ic_restaurant_black_24dp),
    PUB("Pub", 805, "Pub", R.drawable.bar, R.drawable.ic_restaurant_black_24dp),
    PIZZA("Pizzería", 806, "Pizzería", R.drawable.pizzeria, R.drawable.ic_restaurant_black_24dp),

    // Arte y entretenimiento
    LIBRARY("Biblioteca", 901, "Biblioteca o sala de lectura", R.drawable.book, R.drawable.ic_ticket_black_24dp),
    MUSEUM("Museo", 902, "Museo", R.drawable.art_museum, R.drawable.ic_ticket_black_24dp),
    THEATRE("Teatro", 903, "Teatro", R.drawable.theater, R.drawable.ic_ticket_black_24dp),
    CINEMA("Cine", 904, "Cine", R.drawable.cinema, R.drawable.ic_ticket_black_24dp),
    CASINO("Casino", 905, "Casino, bingo o lugar de apuestas", R.drawable.poker, R.drawable.ic_ticket_black_24dp),
    AMPHITHEATER("Anfiteatro", 906, "Anfiteatro", R.drawable.amphitheater, R.drawable.ic_ticket_black_24dp),
    CIRCUS("Circo", 907, "Circo", R.drawable.circus, R.drawable.ic_ticket_black_24dp),
    MINIATURE_GOLF("Golf miniatura", 908, "Golf miniatura o similares", R.drawable.golfing, R.drawable.ic_ticket_black_24dp),
    BOWLING("Bowling", 909, "Lugar de bolos, pool, etc.", R.drawable.bowling, R.drawable.ic_ticket_black_24dp),
    AMUSEMENT_ARCADE("Sala de juegos", 910, "Sacoa, Playland, etc.", R.drawable.videogames, R.drawable.ic_ticket_black_24dp),
    ARTS_CENTRE("Galería de arte", 911, "Galería de arte o sala de exposición.", R.drawable.museum_art, R.drawable.ic_ticket_black_24dp),
    PLANETARIUM("Planetario", 912, "Planetario.", R.drawable.planetarium, R.drawable.ic_ticket_black_24dp),

    // Social
    NIGHT_CLUB("Club nocturno", 1002, "Club nocturno o boliche", R.drawable.dancinghall, R.drawable.ic_people_black_24dp),
    PARTY_ROOM("Salón de fiestas", 1003, "Salón de fiestas, salón de cumpleaños, etc.", R.drawable.party, R.drawable.ic_people_black_24dp),

    // Otros
    RURAL_AREA("Area rural", 1102, "Fuera de zona urbana", R.drawable.farm, R.drawable.ic_others_black_25dp),
    MONUMENT("Monumento", 1103, "Monumento o edificio histórico", R.drawable.modernmonument, R.drawable.ic_others_black_25dp),
    INTEREST_POINT("Punto turístico", 1104, "Punto turístico", R.drawable.star, R.drawable.ic_others_black_25dp),

    // Residencias
    HOME("Casa", 1201, "Mi casa", R.drawable.home, R.drawable.ic_home_black_24dp),
    RELATIVE_HOME("Casa de un pariente", 1202, "La casa de un pariente", R.drawable.otherhome, R.drawable.ic_home_black_24dp),
    FRIEND_HOME("Casa de un amigo", 1203,  "La casa de un amigo", R.drawable.otherhome, R.drawable.ic_home_black_24dp),
    LOVE_HOME("Casa de la pareja", 1204, "La casa de mi pareja", R.drawable.otherhome, R.drawable.ic_home_black_24dp),
    ACCOMMODATION("Alojamiento", 1205, "Hotel, hostel, cabañas, etc.", R.drawable.lodging, R.drawable.ic_home_black_24dp),

    RESIDENCE_CATEGORY("RESIDENCE_CATEGORY",1200,"Hogar y alojamiento", R.drawable.home, R.drawable.ic_home_black_24dp, HOME,RELATIVE_HOME,FRIEND_HOME,LOVE_HOME,ACCOMMODATION),
    WORK("Otro",199,"Otro lugar relacionado al trabajo.",R.drawable.workcase,R.drawable.ic_work_black_24dp),
    WORK_CATEGORY("WORK_CATEGORY",100,"Trabajo",R.drawable.workcase, R.drawable.ic_work_black_24dp,CLIENT_HOME,CLIENT_PLACE,WORK_PLACE,SECONDARY_WORK_PLACE,WORK, OFFICE),
    SHOP("Tienda",295, "Otro tipo de tienda.",R.drawable.mall, R.drawable.ic_store_black_24dp),
    COMMERCE("Negocio",299, "Otro tipo de negocio.",R.drawable.kiosk, R.drawable.ic_store_black_24dp),
    DAILY_STORE("DAILY_STORE_CATEGORY",298,"Compras generales",R.drawable.conveniencestore, R.drawable.ic_shopping_basket_black_24dp,DAIRY_STORE, GREENGROCER, BUTCHER, SEAFOOD, SPICES, BAKERY, KIOSK, WINERY, PASTA, SUPERMARKET, HEALTHY),
    SHOP_CATEGORY("SHOP_CATEGORY",297,"Tiendas",R.drawable.supermarket, R.drawable.ic_shopping_cart_black_24dp,CLOTHES, JEWELRY, ELECTRICAL, ELECTRONICS, FURNITURE, MARKETPLACE, SHOPPING, SHOP, CRAFT_STORE, VARIETY_STORE),
    BUSINESS("BUSINESS_CATEGORY",296,"Negocios",R.drawable.kiosk, R.drawable.ic_store_black_24dp,HAIRDRESSER, AESTHETIC, PAY_PLACE, CARPENTER, CAR_REPAIR,COMMERCE),
    COMMERCE_CATEGORY("COMMERCE_CATEGORY",200,"Negocios y comercios",R.drawable.kiosk, R.drawable.ic_store_black_24dp, DAILY_STORE, SHOP_CATEGORY, BUSINESS),
    HEALTH("Otro",399,"Otro lugar relacionado a la salud",R.drawable.firstaid, R.drawable.ic_local_hospital_black_24dp),
    HEALTH_CATEGORY("HEALTH_CATEGORY",300,"Salud",R.drawable.firstaid, R.drawable.ic_local_hospital_black_24dp,HOSPITAL,HEALTHY, REHABILITATION, LABORATORY, AESTHETIC, VETERINARY, HEALTH),
    EDUCATION("Otro",359,"Otro lugare de enseñanza.",R.drawable.university, R.drawable.ic_school_black_24dp),
    EDUCATION_CATEGORY("EDUCATION_CATEGORY",330,"Educación",R.drawable.university, R.drawable.ic_school_black_24dp,SCHOOL,UNIVERSITY,KINDERGARTEN,MUSIC_SCHOOL,LANGUAGE_SCHOOL,DRIVING_SCHOOL,LIBRARY,MUSEUM,RESEARCH_INSTITUTE,EDUCATION),
    RELIGION("Otro",389,"Otro lugar religioso o de culto.",R.drawable.prayer, R.drawable.ic_church_black_24dp),
    RELIGION_CATEGORY("RELIGION_CATEGORY",360,"Religion y culto",R.drawable.prayer, R.drawable.ic_church_black_24dp,CHURCH,CEMETERY,CREMATORIUM,RELIGION),
    TRANSPORT("Otro",499,"Otro lugare relacionado al transporte",R.drawable.car,R.drawable.ic_in_vehicle_24dp),
    TRANSPORT_CATEGORY("TRANSPORT_CATEGORY",400,"Transporte", R.drawable.car, R.drawable.ic_in_vehicle_24dp, PARKING,BUS_STOP,TAXI_STOP,TERMINAL,AIRPORT,PORT,TRAIN_STATION,FUEL,CAR_WASH,CAR_RENTAL,TRANSPORT),
    ENTITY("Otro",599,"Otra entidad u organización",R.drawable.office_building,R.drawable.ic_entity_black_24dp),
    ENTITIES_CATEGORY("ENTITIES_CATEGORY",500,"Entidades y organizaciones", R.drawable.office_building, R.drawable.ic_entity_black_24dp, PUBLIC_SERVICE, FINANCIAL_ENTITY,PUBLIC_ORGANIZATION,PRIVATE_SERVICE,COMMUNITY_CENTRE,SOCIAL_FACILITY,EMBASSY,COURTHOUSE,POLICE,FIRE_STATION,PRISON,POST_OFFICE,LABOR_UNION,ENTITY),
    OUTDOOR("Otro",699,"Otro lugar para realizar actividades al aire libre",R.drawable.tree, R.drawable.ic_nature_people_black_24dp),
    OUTDOOR_CATEGORY("OUTDOOR_CATEGORY",600,"Al aire libre",R.drawable.tree, R.drawable.ic_nature_people_black_24dp,PARK,BEACH,GOLF,CAMP_SITE,DOG_PARK,HORSE_RIDING,NATURE_RESERVE,WATER_PARK,WOOD,BARE_ROCK,ZOO,THEME_PARK,AQUARIUM,VIEWPOINT,OUTDOOR),
    SPORT("Otro",799,"Otro lugar dedicado a la practica de deportes o actividad fisica",R.drawable.weights,R.drawable.ic_fitness_center_black_24dp),
    SPORT_CATEGORY("SPORT_CATEGORY",700,"Deporte y actividad física",R.drawable.weights,R.drawable.ic_fitness_center_black_24dp,GYM,SPORT_CLUB,SPORT_FIELD,STADIUM,DOJO,SPORT,SWIMMING_POOL),
    FOOD("Otro",899,"Otro lugar de comida.",R.drawable.restaurant,R.drawable.ic_restaurant_black_24dp),
    FOOD_CATEGORY("FOOD_CATEGORY",800,"Comida",R.drawable.restaurant,R.drawable.ic_restaurant_black_24dp,RESTAURANT,CAFE,FAST_FOOD,ICE_CREAM,PUB,PIZZA,FOOD),
    ART_AND_ENTERTAINMENT("Otro",999,"Otros lugares relacionados al arte o entretenimiento",R.drawable.theater, R.drawable.ic_ticket_black_24dp),
    ART_AND_ENTERTAINMENT_CATEGORY("ART_AND_ENTERTAINMENT_CATEGORY",900,"Arte y entretenimiento",R.drawable.theater, R.drawable.ic_ticket_black_24dp,LIBRARY,MUSEUM,THEATRE,CINEMA,CASINO,AMPHITHEATER,CIRCUS,MINIATURE_GOLF,BOWLING,AMUSEMENT_ARCADE,ARTS_CENTRE,PLANETARIUM,ART_AND_ENTERTAINMENT),
    SOCIAL("Otro",1099,"Otro lugar para interactuar con otras personas",R.drawable.group, R.drawable.ic_people_black_24dp),
    SOCIAL_CATEGORY("SOCIAL_CATEGORY",1000,"Social",R.drawable.group, R.drawable.ic_people_black_24dp,NIGHT_CLUB,PARTY_ROOM),
    OTHERS("Otros",1199,"Otros lugares",R.drawable.notvisited, R.drawable.ic_others_black_25dp),
    OTHERS_CATEGORY("OTHERS_CATEGORY",1100,"Otros",R.drawable.notvisited, R.drawable.ic_others_black_25dp,RURAL_AREA,MONUMENT,INTEREST_POINT,OTHERS),

    ROOT_CATEGORY("ROOT",-10,"Categorias de lugares",R.drawable.map, R.drawable.ic_place_black_24dp,RESIDENCE_CATEGORY,WORK_CATEGORY,COMMERCE_CATEGORY,EDUCATION_CATEGORY,HEALTH_CATEGORY,RELIGION_CATEGORY,TRANSPORT_CATEGORY,ENTITIES_CATEGORY,SPORT_CATEGORY,FOOD_CATEGORY,ART_AND_ENTERTAINMENT_CATEGORY,OUTDOOR_CATEGORY,SOCIAL_CATEGORY,OTHERS_CATEGORY);

    private int code;
    private String name;
    private String description;
    private PlaceCategory parent;
    private List<PlaceCategory> sub_types;
    private int iconSrc;
    private int markerSrc;

    PlaceCategory(String name, int code, String description, int markerSrc, int icon_src, PlaceCategory... categories) {
        this.name = name;
        this.code = code;
        this.iconSrc = icon_src;
        this.description = description;
        this.sub_types = Arrays.asList(categories);
        this.parent = null;
        this.markerSrc = markerSrc;
        for (PlaceCategory child: this.sub_types)
            child.setParent(this);
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public List<PlaceCategory> getSubTypes() {
        return this.sub_types;
    }

    public String getDescription() {
        return this.description;
    }

    public static PlaceCategory get(int code) {
        for (PlaceCategory mode: PlaceCategory.values())
            if (mode.getCode() == code)
                return mode;
        return PlaceCategory.NEW;
    }

    public static PlaceCategory getRoot() {
        return ROOT_CATEGORY;
    }

    public int getIconSrc() {
        return iconSrc;
    }

    public int getMarkerSrc() {
        return markerSrc;
    }

    public void setMarkerSrc(int markerSrc) {
        this.markerSrc = markerSrc;
    }

    public PlaceCategory getParent() {
        return parent;
    }

    public void setParent(PlaceCategory parent) {
        this.parent = parent;
    }

    public boolean includes(PlaceCategory category) {
        if (this == category)
            return true;

        for (PlaceCategory child: this.sub_types)
            if (child.includes(category))
                return true;

        return false;
    }

    public void filter(Filter filter, ArrayList<PlaceCategory> categories) {
        if (!this.equals(PlaceCategory.ROOT_CATEGORY) && filter.check(this)) {
            if (!categories.contains(this))
                categories.add(this);
        } else {
            for (PlaceCategory child : this.sub_types) {
                child.filter(filter, categories);
            }
        }
    }

}
