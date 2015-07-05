package com.amusebouche.data;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * RecipesDatabaseHelper class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class helps open, create, and upgrade the database file containing the
 * recipes and their categories, ingredients and directions.
 *
 * See github.com/CindyPotvin/RowCounter
 */
public class ProjectsDatabaseHelper extends SQLiteOpenHelper {
    // When you change the database schema, this database version must be incremented
    public static final int DATABASE_VERSION = 1;
    // The name of the database file on the file system
    public static final String DATABASE_NAME = "Amuse.db";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ProjectsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    private static final String jsonData = "[\n" +
            "    {\n" +
            "        \"title\": \"Porrusalda\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 50,\n" +
            "        \"image\": \"https://c2.staticflickr.com/6/5341/17561617071_7ee1bdb54b_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 4,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/03/porrusalda/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"cebolla\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 4,\n" +
            "                \"name\": \"puerros\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"zanahorias\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"patata\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 1.5,\n" +
            "                \"name\": \"caldo\",\n" +
            "                \"measurement_unit\": \"l\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"albahaca\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Limpiar y pelar todas las verduras.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Cortar el puerro a lo largo y luego en juliana gruesa (dejando una forma semicircular). Cortar la cebolla en una brunoise muy finita y las patatas en cubos más grandes. Cortar las zanahorias en rodajas gruesas.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8800/18116348965_6099f82407_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Poner en una olla el aceite de oliva a calentar a fuego medio.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Sofreír la cebolla y el puerro hasta que empiecen a pocharse bien.\",\n" +
            "                \"image\": \"https://c2.staticflickr.com/8/7682/18112876572_28f83fa3ef_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Añadir la zanahoria y la patatas y seguir cocinando unos 2 o 3 minutos más.\",\n" +
            "                \"image\": \"https://c2.staticflickr.com/8/7712/17930125949_4f6839389b_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Salpimentar y echar la albahaca bien picada. Remover un poco y echar el caldo de pollo. Cocinar durante unos 30 o 40 minutos.\",\n" +
            "                \"image\": \"https://c2.staticflickr.com/8/7793/18089857396_8aff465b47_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 30\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Macarrones al horno con bechamel de gorgonzola\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"SECOND-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 25,\n" +
            "        \"image\": \"https://c1.staticflickr.com/9/8716/16941382873_d24d37c631_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 4,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/04/macarrones-al-horno-con-bechamel-de-gorgonzola/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 400,\n" +
            "                \"name\": \"macarrones\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 120,\n" +
            "                \"name\": \"harina\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 120,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 800,\n" +
            "                \"name\": \"leche\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 150,\n" +
            "                \"name\": \"queso gorgonzola\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta negra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"nuez moscada\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"queso mozzarella\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"orégano\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"patatas fritas\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Poner una olla con abundante agua (al menos un litro por cada 100 gramos de pasta) con sal a fuego fuerte.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Cuando hierva, añadir los macarrones y dejarlos cocinar el tiempo indicado por el fabricante (en torno a 10 minutos).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 10\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Utilizar un mortero para moler las patatas fritas (no demasiado o perderá el toque crujiente del plato). Reservar.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Rallar el queso mozzarella. Reservar.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"En un cazo poner a calentar el aceite de oliva a fuego medio. Añadir la harina y dejar que se tueste un poco, removiendo bien para que no queden grumos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Añadir la leche y seguir removiendo hasta que coja temperatura. Espesará poco a poco hasta que tenga la textura adecuada y empiece a hacer burbujas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Añadir el queso gorgonzola troceado, sal, pimienta y nuez moscada y remover hasta que se incorpore todo. Apartar del fuego y reservar.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Precalentar el horno a 180 grados centígrados.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Engrasar con un par de cucharadas de aceite de oliva una bandeja apta para horno.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"Escurrir los macarrones de toda el agua posible y mezclarlos bien con la bechamel. Colocarlos en la bandeja, procurando que queden bien repartidos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"description\": \"Repartir por encima las patatas fritas, el queso mozzarella y orégano al gusto.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"description\": \"Hornear hasta que el queso se gratine y quede todo dorado (de 2 a 5 minutos aproximadamente).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Spaghetti carbonara\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 25,\n" +
            "        \"image\": \"https://c2.staticflickr.com/8/7794/17373929738_1170a8bf1b_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/04/spaghetti-carbonara/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 180,\n" +
            "                \"name\": \"spaghetti\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 60,\n" +
            "                \"name\": \"queso parmesano\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 60,\n" +
            "                \"name\": \"bacon\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"yemas de huevo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta negra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Cocer los spaghetti en abundante agua hirviendo con sal (al menos 1 litro por cada 100 gramos de pasta).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Cortamos en juliana gorda el bacon.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Poner a fuego fuerte una sartén con una cucharada de aceite de oliva virgen extra. Dorar el bacon a fuego fuerte hasta que se vuelva completamente crujiente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"En un recipiente (a poder ser de un material que no mantenga mucho el calor, como porcelana) mezclar las yemas con el queso rallado, sal y pimienta. Batir enérgicamente y reservar.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Escurrir pasta y la pasamos a la sartén donde está el bacon. Saltear la pasta con el bacon y con un poco de agua de la cocción.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Una vez haya desaparecido el líquido, pasar la pasta al bol donde está la mezcla de huevo y removerlo inmediatamente de forma enérgica.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Garbanzos pizzeros\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 50,\n" +
            "        \"image\": \"https://c2.staticflickr.com/8/7775/17373934218_7abaa07bec_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/04/garbanzos-pizzeros/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"NORTH-AMERICAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 240,\n" +
            "                \"name\": \"garbanzos\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 0.5,\n" +
            "                \"name\": \"hierbas variadas\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta negra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"albahaca\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"tomate frito\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"tomates secos\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 30,\n" +
            "                \"name\": \"queso mozzarella\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"piñones\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Poner los garbanzos a remojo durante al menos 12 horas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Escurrir los garbanzos y cocerlos en abundante agua con sal. Usando una olla a presión, se tarda alrededor de 20 minutos; con una olla normal, serán unas 2 horas (puede que menos).\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8889/17965678779_dd92593fcd_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 20\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Precalentar el horno a 180 grados centígrados.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Mezclar los garbanzos con el aceite, las hierbas y salpimentar. Esparcirlos por una bandeja apta para horno. Tostar durante unos 15 minutos, hasta que queden crujientes y dorados.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8793/17529314384_d7e7dc4025_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 15\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Trocear los tomates secos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Sacarlos del horno y añadirles el tomate, los tomates secos, la albahaca, la mozzarella y los piñones. Mezclar bien y volver a meterlos al horno otro 5 minutos, hasta que el queso se haya derretido.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8845/17964206360_216e0c7f45_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 5\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Fideuá\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 40,\n" +
            "        \"image\": \"https://c2.staticflickr.com/6/5470/17559428122_a4322ca36c_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/04/fideua/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"dientes de ajo\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 0.5,\n" +
            "                \"name\": \"cebolla\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 300,\n" +
            "                \"name\": \"tomate triturado\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"cola de rape\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 150,\n" +
            "                \"name\": \"gambas\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 60,\n" +
            "                \"name\": \"almejas\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 60,\n" +
            "                \"name\": \"calamares\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 150,\n" +
            "                \"name\": \"fideos de fideuá\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 400,\n" +
            "                \"name\": \"fumet de pescado\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Lavar las almejas. Limpiar y pelar bien las gambas. Cortar en tiras de alrededor de 1 centímetro los calamares. Limpiar y trocear la cola de rape.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Cocer las chirlas con un poco de agua un par de minutos, hasta que se abran. Sacarlas de la concha y reservar.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Trocear la cebolla muy finita y laminar los ajos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Echar en una cazuela apta para horno unas 4 cucharadas de aceite de oliva y poner a calentar a fuego medio alto.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Echar el ajo en la sartén para que infusione el aceite. Con un minuto será suficiente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Poner a pochar la cebolla junto con el ajo.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Cuando la cebolla esté pochada, añadir el tomate, la sal y una pizca pequeña de azúcar. Cocinar durante unos 10 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 10\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Añadir los calamares y dejar que se evapore el agua que sueltan. Añadir las gambas y el rape y dejarlos un par de minutos más.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Precalentar el horno a 200 grados centígrados.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"Entonces añadir los fideos y dejar otro par de minutos mientras se sigue removiendo.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"description\": \"Añadir el caldo de fumet y dejar cocer a fuego fuerte durante 5 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"description\": \"Meter la cazuela en el horno y dejarla otros 5 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 13,\n" +
            "                \"description\": \"Dejar reposar antes de servir.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Macarrones con espinacas y alcachofas\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 40,\n" +
            "        \"image\": \"https://c1.staticflickr.com/9/8805/16939155954_edeba8786c_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 4,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/04/macarrones-con-espinacas-y-alcachofas/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"NORTH-AMERICAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"macarrones\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 0.5,\n" +
            "                \"name\": \"cebolla\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"dientes de ajo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"harina\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 500,\n" +
            "                \"name\": \"leche\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"queso crema\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 60,\n" +
            "                \"name\": \"queso parmesano\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 100,\n" +
            "                \"name\": \"queso mozzarella\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 10,\n" +
            "                \"name\": \"alcachofas\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"quantity\": 225,\n" +
            "                \"name\": \"espinacas\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta negra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Limpiar las alcachofas. Primero cortar el tallo y luego las hojas exteriores. Luego ir recortando más duras que quedan fuera. Recortar los pelitos que quedan dentro del propio corazón de la alcachofa. Echar los corazones en agua con perejil o con zumo de limón.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Cocer los corazones en ese mismo agua durante 15 minutos después de que rompan a hervir.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 15\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Rallar los quesos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Pelar y cortar la cebolla muy finita.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Cocer los macarrones en ese agua durante el tiempo indicado en las instrucciones.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Poner una sartén a fuego medio-alto con el aceite de oliva.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Echar el ajo en la sartén y dejar infusionar el aceite durante un minuto.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Añadir la cebolla y una pizca de sal y pimienta y cocinar hasta que esté pochada (durante unos 4 minutos).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 4\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Añadir la leche y cocinar mientras se remueve hasta espese (durante un par de minutos).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"Añadir los corazones de alcachofa cortados en 4 y las hojas de espinacas picadas. Cocinar durante 2 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"description\": \"Escurrir los macarrones y mantecarlos en la sartén con la salsa durante un minuto.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 1\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Puchero gaditano\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"MAIN-DISH\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 35,\n" +
            "        \"image\": \"https://c1.staticflickr.com/9/8731/17375393149_5279af0534_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/puchero/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"morcillo de ternera\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"muslo y contramuslo de pollo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 50,\n" +
            "                \"name\": \"jamón\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"hueso de pata de cerdo salada\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"hueso de espinazo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"trozo de tocino\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"trozo de añejo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"garbanzos\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"patatas\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"puerro\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"penca de apio\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"agua\",\n" +
            "                \"measurement_unit\": \"l\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Dejar los garbanzos en remojo durante unas 12 horas\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Escurrir y enjuagar los garbanzos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Lavar y pelar las verduras y cortarlas en 2 o 3 trozos para que quepa bien en la olla. También es posible trocearlas más pequeñitas (trozos de 1 o 2 centímetros). Al apio hay que quitarle las hebras duras del exterior (utilizando un pelador de patata es muy fácil).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Meter todos los ingredientes en una olla grande y llenarla con agua. Poner a fuego medio-alto.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Ir sacando con un cazo o una espumadera la espuma que salga (a este proceso se le llama desespumar).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Si se usa una olla a presión, cerrarla y dejar cocinar durante unos 20 o 30 minutos a partir de que suba la válvula). Si se usa una olla normal, dejar cocinar hasta que las verduras estén cocidas y sacarlas. Seguir cocinando hasta que las carnes y los garbanzos estén cocidos y sacarlos también. Colar el caldo.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 20\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Nidos Luigi\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 20,\n" +
            "        \"image\": \"https://c2.staticflickr.com/6/5458/17567211666_3aa166defd_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/nidos-luigi/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 220,\n" +
            "                \"name\": \"tagliatelle de espinacas\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"orégano\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 80,\n" +
            "                \"name\": \"queso roquefort\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 10,\n" +
            "                \"name\": \"brandy\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"nata de cocinar\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 60,\n" +
            "                \"name\": \"salmón ahumado\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"huevas de lumpo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Cocer la pasta en abundante agua con sal y orégano durante el tiempo indicado en las instrucciones.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Mientras tanto, poner en un cazo el queso cortado en taquitos junto con el chorrito de brandy. Dejar a fuego medio durante un par de minutos para que se derrita el queso mientras se evapora el alcohol del brandy.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Añadir la nata y pimienta al gusto. Dejar cocinar mientras se remueve durante otros 5 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Escurrir los tagliatelle y mezclarlos bien con la salsa.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Cortar tiras de salmón de un centímetro de ancho.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Colocar los tagliatelle en forma de nido en cada plato a servir, con varias tiras de salmón por encima y las huevas de lumpo en el centro.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Risotto de champiñones\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 35,\n" +
            "        \"image\": \"https://c1.staticflickr.com/9/8887/17479615718_3e7bb8d1fd_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/risotto-de-champinones/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 180,\n" +
            "                \"name\": \"arroz bomba\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 700,\n" +
            "                \"name\": \"caldo\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 250,\n" +
            "                \"name\": \"champiñones\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 100,\n" +
            "                \"name\": \"cebolla\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 60,\n" +
            "                \"name\": \"queso parmesano\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 100,\n" +
            "                \"name\": \"vino blanco\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 20,\n" +
            "                \"name\": \"mantequilla\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Limpiar y pelar los champiñones y laminarlos en tiras de medio centímetro\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Pelar y cortar la cebolla muy finamente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Rallar el queso parmesano.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Poner el caldo en un cazo a calentar. Rectificar de sal si es necesario.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Poner aceite de oliva en una sartén a fuego medio bajo. Echar la cebolla y dejar que se pochen un par de minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Añadir los champiñones, una pizca de sal y pimienta negra al gusto. Dejar cocinar hasta que pierdan todo el agua y estén bien hechos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Echar el arroz y remover durante un par de minutos. Luego añadir el vino blanco y dejar cocinar hasta que se evapore el alcohol.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Añadir varios cazos de caldo al arroz y dejar cocinar hasta que se evapore casi todo, removiendo constantemente. Añadir más caldo y dejarlo cocer cada vez que se quede sin líquido. Repetir el mismo proceso durante unos 20 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 20\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Apartar el arroz del fuego y añadir la mantequilla y el queso parmesano. Remover hasta que se integren bien con el arroz.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Spaghetti al pesto\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"APPETIZER\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 25,\n" +
            "        \"image\": \"https://c2.staticflickr.com/8/7687/17885060832_d63b553229_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/spaghetti-al-pesto/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 15,\n" +
            "                \"name\": \"albahaca fresca\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"diente de ajo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 50,\n" +
            "                \"name\": \"piñones\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 30,\n" +
            "                \"name\": \"queso parmesano\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 30,\n" +
            "                \"name\": \"queso pecorino\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Cocer los spaghetti según las instrucciones del fabricante en abundante agua con sal (al menos 1 litro de agua por cada 100 gramos de pasta).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Pelar y picar el diente de ajo muy finamente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Cortar en juliana muy fina las hojas de albahaca.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Rallar los quesos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Echar en un mortero el diente de ajo picado, la albahaca y los piñones y machacarlos muy bien.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Ir añadiendo poco a poco el aceite de oliva, mientras se sigue removiendo con el mazo de mortero.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Añadir los quesos e integrarlos bien con el resto de ingredientes.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Escurrir bien la pasta y mezclarla con la salsa.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Huevos rellenos con bacon, queso azul y espárragos verdes\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"APPETIZER\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 40,\n" +
            "        \"image\": \"https://c2.staticflickr.com/8/7792/17773021099_4337781d24_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 1,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/huevos-rellenos-con-bacon-queso-azul-y-esparragos-verdes/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"NORTH-AMERICAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"huevos\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"mayonesa\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 20,\n" +
            "                \"name\": \"queso azul\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"bacon\",\n" +
            "                \"measurement_unit\": \"rasher\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"espárragos verdes\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"ajo molido\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Limpiar y trocear en tiras el cerdo y salpimentarlas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"En un recipiente mezclar la miel y la mostaza e introducimos el cerdo. Lo dejamos macerar en la nevera durante un par de horas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Limpiar las patatas y cortarlas en gajos: primero en cuatro trozos iguales y estos, a su vez, en 2 o 3 gajos (dependiendo de lo grandes que sean las patatas).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Pasar las patatas a un recipiente amplio y bañarlas con unas gotas de aceite de oliva, procurando que todas queden impregnadas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Echar sobre las patatas sal, pimienta, pimentón dulce y picante, comino, orégano, ajo molido y garam masala. Remover bien con las manos para que todas las patatas se impregnen bien de las especias.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Freír las patatas a fuego fuerte (a unos 180 grados centígrados) hasta que estén bien doradas. Sacarlas a un plato con papel absorbente para eliminar el exceso de aceite y reservar.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Sacar el cerdo caramelizado de la nevera y, en una sartén con una cucharada de aceite, cocinar las tiras de cerdo escurridas a fuego medio hasta que se caramelicen y se doren un poco.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Introducir la mezcla de macerado sobrante en una cazuela y calentarla a fuego medio un minuto.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Añadir la nata y dejar que se reduzca bien hasta obtener una salsa algo espesa.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Empanadillas de ternera\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"APPETIZER\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 40,\n" +
            "        \"image\": \"https://c2.staticflickr.com/6/5452/17453724613_2ea25781eb_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 6,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/empanadillas-de-ternera/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"EUROPEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"láminas de hojaldre\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"cebolla\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 250,\n" +
            "                \"name\": \"carne de ternera picada\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 100,\n" +
            "                \"name\": \"tomate frito\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"harina\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 80,\n" +
            "                \"name\": \"leche\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Pelar y picar la cebolla muy finamente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Echar un par de cucharadas de aceite de oliva en una sartén calentada a fuego medio.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Añadir la cebolla y dejar que se poche.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8872/17886553780_9cc216af03_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Salpimentar la carne picada y echarla a la sartén. Dejar que se cocine.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Echar tomate frito y remover hasta que se incorpore.\",\n" +
            "                \"image\": \"https://c2.staticflickr.com/6/5441/17888019709_fca0eed792_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Desplazar la carne hacia un lado de la sartén y, en el hueco que quede libre, echar la harina y dejar que se tueste. Añadir la leche y formar la bechamel. Entonces mezclar con el resto de la carne que estaba separada.\",\n" +
            "                \"image\":  \"https://c2.staticflickr.com/8/7763/17888019379_882e98d904_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Apartar la sartén del fuego y reservar mientras se enfría el relleno.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8783/17453725383_8774d6b337_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Precalentar el horno a 180 grados centígrados.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Engrasar los moldes a utilizar con un poco de aceite de oliva. Colocar en ellos el hojaldre y pincharlo por toda la superficie con un tenedor pequeño.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8775/17886347208_817469ed91_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"Repartir el relleno entre todos los moldes y taparlos con otra lámina de hojaldre. Decorar con el hojaldre sobrante.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"description\": \"Pincelar la parte superior del hojaldre con una yema de huevo rebajada con una gota de agua.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8859/18047776456_d363b433b1_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"description\": \"Introducir las empanadillas en el horno y dejar cocinar hasta que estén bien doradas (unos 20 o 30 minutos).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 20\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 13,\n" +
            "                \"description\": \"Sacarlas del horno y desmoldarlas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Cerdo caramelizado con patatas deluxe\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"MAIN-DISH\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 40,\n" +
            "        \"image\": \"https://c2.staticflickr.com/8/7767/18149798392_c24103ff78_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/cerdo-caramelizado-con-patatas-deluxe/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"cerdo\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"miel\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"mostaza de Dijon\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"nata de cocinar\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 4,\n" +
            "                \"name\": \"patatas\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"pimentón dulce\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"pimentón picante\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"comino molido\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"orégano\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 13,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"ajo molido\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 14,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"garam masala\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Limpiar y trocear en tiras el cerdo y salpimentarlas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"En un recipiente mezclar la miel y la mostaza e introducimos el cerdo. Lo dejamos macerar en la nevera durante un par de horas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Limpiar las patatas y cortarlas en gajos: primero en cuatro trozos iguales y estos, a su vez, en 2 o 3 gajos (dependiendo de lo grandes que sean las patatas).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Pasar las patatas a un recipiente amplio y bañarlas con unas gotas de aceite de oliva, procurando que todas queden impregnadas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Echar sobre las patatas sal, pimienta, pimentón dulce y picante, comino, orégano, ajo molido y garam masala. Remover bien con las manos para que todas las patatas se impregnen bien de las especias.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Freír las patatas a fuego fuerte (a unos 180 grados centígrados) hasta que estén bien doradas. Sacarlas a un plato con papel absorbente para eliminar el exceso de aceite y reservar.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Sacar el cerdo caramelizado de la nevera y, en una sartén con una cucharada de aceite, cocinar las tiras de cerdo escurridas a fuego medio hasta que se caramelicen y se doren un poco.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Introducir la mezcla de macerado sobrante en una cazuela y calentarla a fuego medio un minuto.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Añadir la nata y dejar que se reduzca bien hasta obtener una salsa algo espesa.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Souvlaki de pollo en pan de pita con tzatziki\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"MAIN-DISH\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 35,\n" +
            "        \"image\": \"https://c2.staticflickr.com/8/7795/18148443858_f228cb9ed1_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/05/cerdo-caramelizado-con-patatas-deluxe/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"pechugas de pollo\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 80,\n" +
            "                \"name\": \"aceite de oliva virgen\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"jengibre molido\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"ajo molido\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"pimentón picante\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"semillas de sésamo\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"comino molido\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"canela\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"guindilla molida\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"cilantro\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 13,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"tomillo\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 14,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"cúrcuma\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 15,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"pepino\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 16,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"yogur griego\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 17,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"eneldo\",\n" +
            "                \"measurement_unit\": \"tsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 18,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"lechuga\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 19,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"tomate\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 20,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"tomate\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 21,\n" +
            "                \"quantity\": 40,\n" +
            "                \"name\": \"queso feta\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 22,\n" +
            "                \"quantity\": 4,\n" +
            "                \"name\": \"pan de pita\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 23,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"vinagre de Jerez\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Limpiar, pelar y rallar el pepino.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Mezclarlo con el yogur, una cucharada de eneldo, una cucharadita de ajo en polvo, sal y pimienta. Reservar en la nevera.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Limpiar el pollo y cortarlo en pedazos pequeños de unos 2 centímetros aproximadamente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"En un recipiente, poner el aceite junto con una cucharadita de cada una de estas especias: pimienta, jengibre, ajo, pimentón, semillas de sésamo, comino, canela, guindilla, cilantro, tomillo y cúrcuma. También se pueden sustituir por 3 cucharadas de garam masala y una cucharadita de semillas de sésamo.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Añadir el pollo troceado y salpimentado al recipiente. Remover bien para que todos los trozos de pollo queden impregnados con la mezcla.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Tapar el recipiente con film transparente y reservar en la nevera durante al menos 2 horas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 120\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Precalentar el horno a 180 grados centígrados.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8766/18224915680_83699038ac_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Sacar el pollo de la nevera y repartirlo por una bandeja de horno bien extendido. Hornear durante unos 15 minutos, sin dejar que se tueste demasiado (debe quedar jugoso por dentro).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 15\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Limpiar y trocear la lechuga y el tomate como para hacer una ensalada. Aliñarlos con aceite de oliva, vinagre y una pizca de sal.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"Trocear el queso feta en taquitos pequeños o desmenuzarlo a mano.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"description\": \"Tostar el pan de pita durante un par de minutos. Partirlo por la mitad y abrirlo.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"description\": \"Introducir una cucharada de salsa de yogur en el fondo, la mezcla de lechuga y tomate, el queso feta y el souvlaki de pollo. Completar con otra cucharada de salsa de yogur por encima.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Tamagoyaki\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"APPETIZER\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 25,\n" +
            "        \"image\": \"https://c1.staticflickr.com/9/8829/18226471849_a3b5feb064_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/06/tamagoyaki/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"ASIAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"huevos\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"leche\",\n" +
            "                \"measurement_unit\": \"tbsp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 0.25,\n" +
            "                \"name\": \"cebolleta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 0.5,\n" +
            "                \"name\": \"pimiento verde de freír\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 0.5,\n" +
            "                \"name\": \"pimiento rojo de freír\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Cascar los huevos en un recipiente y agregar la leche. Batir hasta que se mezclen. Pasar por un colador fino para eliminar la chalaza.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Picar muy finamente la cebolleta y los pimientos.\",\n" +
            "                \"image\": \"https://c2.staticflickr.com/8/7779/17791968093_cf6831b655_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Añadir las verduras al recipiente con los huevos batidos, salpimentar y remover hasta que se integren.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Añadir la cebolla, la cebolla, la cebolleta y la pimienta y remover hasta que se mezclen.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Engrasar ligeramente una sartén con aceite vegetal y calentar la sartén. Verter la mitad de la mezcla de huevo y cocinar a fuego lento hasta que esté a medio hacer.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/503/18408483792_0473c5b685_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Enrollar la tortilla hasta la mitad. Si no hay suficiente aceite, engrasar ligeramente la sartén cada vez que se enrolle. Añadir la mitad de la mezcla de huevo restante a un lado de la tortilla. Cocinar hasta que a medio hacer.\",\n" +
            "                \"image\": \"https://c2.staticflickr.com/8/7789/18414297831_0d84a7c5a4_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Enrollar de nuevo hasta la mitad y mover el rollo de huevo hasta el centro de la sartén. Agregue la mezcla de huevo restante y cocinar hasta que esté hecho.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8766/18224915680_83699038ac_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Terminar de enrollar el resto de la tortilla.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Pionono de calabacín\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 60,\n" +
            "        \"image\": \"https://c1.staticflickr.com/1/369/18293800010_a3fc9f9cff_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/06/pionono-de-calabacin/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"EUROPEAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 40,\n" +
            "                \"name\": \"aceite de oliva\",\n" +
            "                \"measurement_unit\": \"ml\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 150,\n" +
            "                \"name\": \"cebolla\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 200,\n" +
            "                \"name\": \"champiñones\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 100,\n" +
            "                \"name\": \"leche\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 120,\n" +
            "                \"name\": \"queso emmental\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 100,\n" +
            "                \"name\": \"bonito\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimienta\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"calabacín\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"quantity\": 4,\n" +
            "                \"name\": \"huevos\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"quantity\": 50,\n" +
            "                \"name\": \"harina\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 10,\n" +
            "                \"name\": \"levadura\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Pelar y trocear la cebolla en brunoise muy finita.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Limpiar y pelar los champiñones y laminarlos en tiras de menos de medio centímetro.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Rallar la mitad del queso.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Poner una sartén a fuego medio-alto con el aceite de oliva. Cuando esté caliente, añadir la cebolla y sofreír durante un par de minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Añadir los champiñones laminados y la sal y cocinar hasta que suelten el agua (unos 5 minutos aproximadamente).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Echar la harina y dejar que se tueste durante un par de minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Incorporar la leche y el queso y mezclar bien todo hasta que se forme una crema. Apartar del fuego.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 10\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Añadir el bonito desmigándolo con las manos. Repartirlo e integrarlo bien en la mezcla.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Lavar bien y pelar el calabacín. Cortar tiras muy finas del calabacín (con un pelador es muchísimo más fácil).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"En un recipiente cascar los huevos y batirlos hasta conseguir que espumen y cojan cuerpo durante unos 5 minutos (con un robot de cocina o unas varillas eléctricas, este proceso se puede hacer muchísimo más rápido). Echar un poco de sal a la mezcla y batir unos segundos más.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/444/18293798980_11e160f841_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"description\": \"Tamizar la harina una o dos veces (dependiendo de lo suelta que esté).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 12,\n" +
            "                \"description\": \"Espolvorear la harina sobre los huevos poco a poco mientras vamos mezclando con movimientos envolventes.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 13,\n" +
            "                \"description\": \"Poner a calentar una sartén a fuego medio con una gota de aceite y el horno a precalentar a 180 grados centígrados.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 14,\n" +
            "                \"description\": \"En una bandeja de unos 20 por 30 centímetros aproximadamente colocar papel de horno.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 15,\n" +
            "                \"description\": \"Pasar las tiras de calabacín por la sartén el tiempo justo para que se doren un poco, secarles el aceite y colocarlas sobre el papel de horno. Repetir el proceso hasta rellenar la bandeja por completo.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/293/18886903825_8d81d5f59b_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 16,\n" +
            "                \"description\": \"Echar la masa sobre las tiras de calabacín de la bandeja, moviendo la bandeja lateralmente para cubrirla perfectamente toda entera.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/375/17858863364_16d8a6719a_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 17,\n" +
            "                \"description\": \"Hornear durante unos 10 minutos (el bizcocho deberá haber cogido algo de color y al introducir un palillo en el centro deberá salir limpio).\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/444/18295328919_8ff868c5eb_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 10\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 18,\n" +
            "                \"description\": \"Sacar el bizcocho del horno y verter el relleno bien extendido por encima.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/544/18293703318_b166de48b9_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 19,\n" +
            "                \"description\": \"Enrollar sobre sí mismo el bizcocho desde el lado más estrecho.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/504/18455095026_7fd8229fce_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 20,\n" +
            "                \"description\": \"Reservar el bizcocho enrollado tapado con el papel de horno durante 2 horas.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/449/18455094756_3aa0789116_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 21,\n" +
            "                \"description\": \"Precalentar el horno a 200 grados centígrados.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 22,\n" +
            "                \"description\": \"Rallar la otra mitad del queso.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 23,\n" +
            "                \"description\": \"Colocar el bizcocho enrollado sobre una bandeja de horno con papel de hornear.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 24,\n" +
            "                \"description\": \"Meterlo en el horno y dejar que se gratine durante 5 o 10 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 25,\n" +
            "                \"description\": \"Sacarlo del horno y reservar hasta que se enfríe un poco (unos 5 minutos).\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/534/17860837503_197331b89c_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 26,\n" +
            "                \"description\": \"Cortarlo en rodajas de unos 2 centímetros y servir.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Endivias horneadas con cebolla, jamón serrano y queso emmental\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"LOW\",\n" +
            "        \"cooking_time\": 25,\n" +
            "        \"image\": \"https://c1.staticflickr.com/1/519/18588390862_78dd3b88d9_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 2,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/06/endivias-horneadas-con-cebolla-jamon-serrano-y-queso-emmental/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"EUROPEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"endivias\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 1,\n" +
            "                \"name\": \"cebolla\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 3,\n" +
            "                \"name\": \"jamón serrano\",\n" +
            "                \"measurement_unit\": \"rasher\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 100,\n" +
            "                \"name\": \"queso emmental\",\n" +
            "                \"measurement_unit\": \"g\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Poner una olla con abundante agua con sal a hervir.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/349/18594988541_af3bf4a89d_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Lavar las endivias e introducirlas en el agua hirviendo. Dejar cocer durante unos 10 o 15 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 10\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"description\": \"Sacar las endivias y escurrirlas muy bien. Dejarlas en un recipiente con papel absorbente para que terminen de soltar todo el agua.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Pelar y cortar la cebolla en juliana.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Trocear el jamón serrano y el queso en tiras finas de un centímetro de ancho aproximadamente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Poner una sartén con un poco de aceite de oliva a calentar a fuego medio.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Añadir la cebolla a la sartén con un poco de sal y dejar que se pochen lentamente durante unos 10 minutos. Reservar.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/431/18592872805_08d6d52998_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 10\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Precalentar el horno a 180 grados centígrados.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Abrir las endivias por la mitad transversalmente y colocar las mitades en una bandeja apta para horno.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/9/8854/18566500346_2f140acd4d_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"Repartir sobre las endivias la cebolla, el jamón y el queso.\",\n" +
            "                \"image\": \"https://c1.staticflickr.com/1/344/18406712659_3822245240_o.png\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 11,\n" +
            "                \"description\": \"Introducir la bandeja en el horno y dejar un par de minutos (el tiempo justo para que se derrita el queso).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Pulpo a la gallega\",\n" +
            "        \"language\": \"ES\",\n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 60,\n" +
            "        \"image\": \"https://c1.staticflickr.com/1/399/18479688630_aab1fdd86a_o.png\",\n" +
            "        \"total_rating\": 0,\n" +
            "        \"users_rating\": 0,\n" +
            "        \"servings\": 5,\n" +
            "        \"source\": \"http://noeliarcado.es/2015/06/pulpo-a-la-gallega/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ingredients\": [\n" +
            "            {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"quantity\": 2,\n" +
            "                \"name\": \"pulpo\",\n" +
            "                \"measurement_unit\": \"kg\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"quantity\": 8,\n" +
            "                \"name\": \"patatas\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 3,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"sal gorda\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"pimentón picante\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"quantity\": 0,\n" +
            "                \"name\": \"aceite de oliva virgen extra\",\n" +
            "                \"measurement_unit\": \"unit\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "                \"sort_number\": 1,\n" +
            "                \"description\": \"Antes de nada, hay que lavar con abundante agua el pulpo. Hay que repasar bien todos los brazos y tentáculos y luego darle la vuelta para limpiar la cabeza.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 2,\n" +
            "                \"description\": \"Poner una olla con abundante agua (que el pulpo quede bien cubierto de agua) y sal a fuego fuerte.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": \"3\",\n" +
            "                \"description\": \"Cuando el agua esté hirviendo, hay que agarrar el pulpo por la cabeza (con un tenedor, unas pinzas o simplemente con las manos, con mucho cuidado) e introducirlo en el agua 3 segundos y sacarlo otros 3. Este proceso hay que repetirlo unas 3 o 4 veces (depende de a quien le preguntes son más o menos).\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 4,\n" +
            "                \"description\": \"Dejar que el pulpo se cueza durante el tiempo necesario. Dejarlo cocer e ir pinchando con el tenedor (como cuando se cuecen patatas) para ver si está listo.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 30\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 5,\n" +
            "                \"description\": \"Ahora solo queda limpiar bien el pulpo. Cortar para separar la cabeza de los tentáculos y quitar la boca cortado alrededor.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 6,\n" +
            "                \"description\": \"Cocer las patatas en abundante agua con sal durante unos 15 o 20 minutos.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 15\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 7,\n" +
            "                \"description\": \"Trocearlas en rodajas de un centímetro o 2 de grosor.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 8,\n" +
            "                \"description\": \"Trocear el pulpo en rodajas de un centímetro aproximadamente.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 9,\n" +
            "                \"description\": \"Colocar las patatas sobre la fuente o bandeja a servir. Encima colocar las rodajas de pulpo bien repartidas.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"sort_number\": 10,\n" +
            "                \"description\": \"Espolvorear con sal gorda, pimentón picante y añadir un buen chorro de aceite de oliva.\",\n" +
            "                \"image\": \"\",\n" +
            "                \"video\": \"\",\n" +
            "                \"time\": 0\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Rancho canario\", \n" +
            "        \"language\": \"ES\", \n" +
            "        \"type_of_dish\": \"SECOND-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 1,\n" +
            "        \"image\": \"https://c2.staticflickr.com/6/5322/16939147204_1d42ed1345_o.png\",\n" +
            "        \"total_rating\": 0, \n" +
            "        \"users_rating\": 0, \n" +
            "        \"servings\": 4, \n" +
            "        \"source\": \"http://noeliarcado.es/2015/04/rancho-canario/\", \n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ], \n" +
            "        \"ingredients\": [\n" +
            "        {\n" +
            "            \"sort_number\": 1,\n" +
            "            \"quantity\": 120,\n" +
            "            \"name\": \"garbanzos\",\n" +
            "            \"measurement_unit\": \"g\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 2,\n" +
            "            \"quantity\": 2,\n" +
            "            \"name\": \"muslos de pollo\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 3,\n" +
            "            \"quantity\": 80,\n" +
            "            \"name\": \"ternera\",\n" +
            "            \"measurement_unit\": \"g\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 4,\n" +
            "            \"quantity\": 2,\n" +
            "            \"name\": \"chorizos\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 5,\n" +
            "            \"quantity\": 80,\n" +
            "            \"name\": \"pasta\",\n" +
            "            \"measurement_unit\": \"g\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 6,\n" +
            "            \"quantity\": 2,\n" +
            "            \"name\": \"patatas\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 7,\n" +
            "            \"quantity\": 0.5,\n" +
            "            \"name\": \"cebolleta\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 8,\n" +
            "            \"quantity\": 1,\n" +
            "            \"name\": \"tomate\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 9,\n" +
            "            \"quantity\": 2,\n" +
            "            \"name\": \"dientes de ajo\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 10,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"aceite de oliva\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 11,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"sal\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 12,\n" +
            "            \"quantity\": 1,\n" +
            "            \"name\": \"pimentón dulce\",\n" +
            "            \"measurement_unit\": \"tbsp\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 13,\n" +
            "            \"quantity\": 1,\n" +
            "            \"name\": \"orégano\",\n" +
            "            \"measurement_unit\": \"tbsp\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 14,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"azafrán\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 15,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"perejil\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        }\n" +
            "        ], \n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "            \"sort_number\": 1,\n" +
            "            \"description\": \"Poner los garbanzos a remojo durante al menos 8 horas (mejor 12).\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 2,\n" +
            "            \"description\": \"Poner las carnes en la olla a presión, cubrir con agua, sazonar y calentar. Ir desespumando hasta que empiece a hervir.\",\n" +
            "            \"image\": \"https://c1.staticflickr.com/9/8776/17495374644_8b3147e050_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 3,\n" +
            "            \"description\": \"Añadir los garbanzos. Tapar la olla y dejar cocinar durante unos 30 minutos a partir de que empiece a salir el vapor. Si no se usa olla a presión, cocinar los garbanzos durante al menos una hora y media (puede que dos).\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 30\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 4,\n" +
            "            \"description\": \"Pelar y picar la cebolleta y los ajos. Ponerlos en una sartén a fuego medio para que se pochen.\",\n" +
            "            \"image\": \"https://c2.staticflickr.com/8/7696/18117991875_c13ded76ca_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 5,\n" +
            "            \"description\": \"Echar el tomate bien picado y sazonar. Rehogar un poco y añadir el pimentón, el orégano y el azafrán. Remover.\",\n" +
            "            \"image\": \"https://c2.staticflickr.com/8/7664/18091502376_0dfac331cf_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 6,\n" +
            "            \"description\": \"Pelar y trocear las patatas en dados medianos.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 7,\n" +
            "            \"description\": \"Cuando el cocido este listo, retirar las carnes de la olla y trocearlas quitando los huesos y sobrantes.\",\n" +
            "            \"image\": \"https://c2.staticflickr.com/8/7675/18091501906_d48086b175_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 8,\n" +
            "            \"description\": \"Echar las verduras pochadas, las patatas y la pasta a la olla.\",\n" +
            "            \"image\": \"https://c1.staticflickr.com/9/8837/17931761929_214891034d_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 9,\n" +
            "            \"description\": \"Cocinar durante unos 6 u 8 minutos (dependiendo de las instrucciones de la pasta).\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 6\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 10,\n" +
            "            \"description\": \"Abrir la olla, volver a introducir las carnes y remover. Espolvorear perejil picado al servir.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Torrijas de vino con miel\", \n" +
            "        \"language\": \"ES\", \n" +
            "        \"type_of_dish\": \"DESSERT\",\n" +
            "        \"difficulty\": \"HIGH\",\n" +
            "        \"cooking_time\": 40, \n" +
            "        \"image\": \"https://c2.staticflickr.com/6/5332/16939142964_9e672531d0_o.png\",\n" +
            "        \"total_rating\": 0, \n" +
            "        \"users_rating\": 0, \n" +
            "        \"servings\": 10, \n" +
            "        \"source\": \"http://noeliarcado.es/2015/03/torrijas-de-vino-con-miel/\", \n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"EUROPEAN\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"VEGETARIAN\"\n" +
            "            }\n" +
            "        ], \n" +
            "        \"ingredients\": [\n" +
            "        {\n" +
            "            \"sort_number\": 1,\n" +
            "            \"quantity\": 1,\n" +
            "            \"name\": \"barra de pan\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 2,\n" +
            "            \"quantity\": 500,\n" +
            "            \"name\": \"leche\",\n" +
            "            \"measurement_unit\": \"ml\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 3,\n" +
            "            \"quantity\": 1,\n" +
            "            \"name\": \"rama de canela\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 4,\n" +
            "            \"quantity\": 1,\n" +
            "            \"name\": \"limón\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 5,\n" +
            "            \"quantity\": 120,\n" +
            "            \"name\": \"vino moscatel\",\n" +
            "            \"measurement_unit\": \"ml\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 6,\n" +
            "            \"quantity\": 3,\n" +
            "            \"name\": \"azúcar\",\n" +
            "            \"measurement_unit\": \"tsp\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 7,\n" +
            "            \"quantity\": 3,\n" +
            "            \"name\": \"huevos\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 8,\n" +
            "            \"quantity\": 0.5,\n" +
            "            \"name\": \"miel\",\n" +
            "            \"measurement_unit\": \"cup\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 9,\n" +
            "            \"quantity\": 0.25,\n" +
            "            \"name\": \"agua\",\n" +
            "            \"measurement_unit\": \"cup\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 10,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"aceite de girasol\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 11,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"azúcar glas\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 12,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"canela en polvo\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        }\n" +
            "        ], \n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "            \"sort_number\": 1,\n" +
            "            \"description\": \"Si se va a usar una barra de pan duro, cortarla en rebanadas de poco más de un centímetro. Si el pan no está suficientemente duro, hacer las rebanadas más anchas para que no se deshagan.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 2,\n" +
            "            \"description\": \"Echar el vino en un cazo y calentarlo a fuego medio. Añadir el azúcar y dejar que se diluya sin llegar a ebullición. Apartar y dejar enfriar.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 3,\n" +
            "            \"description\": \"Cortar tiras de la piel del limón con cuidado de no dejar la parte blanca, que amarga. Con un pelapatatas resulta muy fácil.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 4,\n" +
            "            \"description\": \"En el mismo o en otro cazo infusionar llevar la leche casi al punto de ebullición. Apartar del fuego y añadir la rama de canela y la piel de limón. Dejar que se enfríe hasta llegar a temperatura ambiente.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 5,\n" +
            "            \"description\": \"Cuando la leche y el vino estén fríos, quitar la rama de canela y la piel de limón de la leche. Mezclar la leche y el vino en un mismo recipiente.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 6,\n" +
            "            \"description\": \"Bañar las rebanadas de pan en la mezcla de leche y vino. Dejarlas sobre una bandeja para que vayan soltando el líquido sobrante.\",\n" +
            "            \"image\": \"https://c2.staticflickr.com/8/7733/17929925800_08c852ab39_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 7,\n" +
            "            \"description\": \"Poner el aceite en una sartén a fuego fuerte y batir los huevos en otro recipiente.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 8,\n" +
            "            \"description\": \"Pasar las rebanadas por el huevo por ambas caras y freír en el aceite bien caliente. Cuando estén doradas por un lado, darles la vuelta y dejar que se tuesten por el otro. Sacarlas a una bandeja con papel absorbente para que escurran el aceite sobrante.\",\n" +
            "            \"image\": \"https://c1.staticflickr.com/9/8857/18114165392_d4a01bcde5_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 9,\n" +
            "            \"description\": \"Espolvorear canela en polvo y azúcar glas sobre las torrijas.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 10,\n" +
            "            \"description\": \"Echar la miel y el agua en un cazo y calentar hasta que la miel se diluya un poco, para que no quede tan espesa.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 11,\n" +
            "            \"description\": \"Bañar las torrijas en la miel.\",\n" +
            "            \"image\": \"https://c1.staticflickr.com/9/8795/18117638795_99488b1564_o.png\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"Ensalada de judías verdes y bonito\", \n" +
            "        \"language\": \"ES\", \n" +
            "        \"type_of_dish\": \"FIRST-COURSE\",\n" +
            "        \"difficulty\": \"MEDIUM\",\n" +
            "        \"cooking_time\": 55,\n" +
            "        \"image\": \"https://c2.staticflickr.com/6/5449/17374117920_089425c4b3_o.png\",\n" +
            "        \"total_rating\": 0, \n" +
            "        \"users_rating\": 0, \n" +
            "        \"servings\": 0, \n" +
            "        \"source\": \"http://noeliarcado.es/2015/03/ensalada-de-judias-verdes-y-bonito/\",\n" +
            "        \"categories\": [\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-CELIACS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"SUITABLE-FOR-LACTOSE-INTOLERANTS\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"MEDITERRANEAN\"\n" +
            "            }\n" +
            "        ], \n" +
            "        \"ingredients\": [\n" +
            "        {\n" +
            "            \"sort_number\": 1,\n" +
            "            \"quantity\": 2,\n" +
            "            \"name\": \"patatas\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 2,\n" +
            "            \"quantity\": 2,\n" +
            "            \"name\": \"tomates\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 3,\n" +
            "            \"quantity\": 200,\n" +
            "            \"name\": \"judías verdes\",\n" +
            "            \"measurement_unit\": \"g\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 4,\n" +
            "            \"quantity\": 60,\n" +
            "            \"name\": \"bonito\",\n" +
            "            \"measurement_unit\": \"g\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 5,\n" +
            "            \"quantity\": 4,\n" +
            "            \"name\": \"huevos de codorniz\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 6,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"sal\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 7,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"aceite de oliva\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 8,\n" +
            "            \"quantity\": 0,\n" +
            "            \"name\": \"vinagre de jerez\",\n" +
            "            \"measurement_unit\": \"unit\"\n" +
            "        }\n" +
            "        ], \n" +
            "        \"directions\": [\n" +
            "        {\n" +
            "            \"sort_number\": 1,\n" +
            "            \"description\": \"Quitar las puntas a las judías verdes, cortarlas longitudinalmente en dos y luego cada trozo resultante en tres de igual tamaño.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 2,\n" +
            "            \"description\": \"Cocer las judías en abundante agua con sal durante unos 15 minutos.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 15\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 3,\n" +
            "            \"description\": \"Cocer las patatas y los huevos en abundante agua con sal. Los huevos deben cocer durante unos 10 minutos y las patatas de 20 a 40 minutos (dependiendo del tamaño).\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 20\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 4,\n" +
            "            \"description\": \"Pelar y trocear las patatas en láminas de unos 2 centímetros.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 5,\n" +
            "            \"description\": \"Quitar las cáscaras de los huevos y cortarlos por la mitad a lo largo.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 6,\n" +
            "            \"description\": \"Lavar y laminar los tomates muy finalmente (en tiras de medio centímetro aproximadamente).\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"sort_number\": 7,\n" +
            "            \"description\": \"Cuando estén todos los ingredientes a temperatura ambiente, montar el plato poniendo primero una capa de patata, luego tomate, siguiendo con las judías, el bonito y por último los huevos. Aliñar con sal, aceite de oliva y vinagre de Jerez.\",\n" +
            "            \"image\": \"\",\n" +
            "            \"video\": \"\",\n" +
            "            \"time\": 0\n" +
            "        }\n" +
            "        ]\n" +
            "    }\n" +
            "]\n";


    /**
     * Create a new recipe in the database with its categories, ingredients and directions.
     *
     * @param recipe Recipe to insert in the database
     */
    public void createRecipe(Recipe recipe) {

        SQLiteDatabase db = getWritableDatabase();

        // Create the database row for the project and keep its unique identifier
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_ID, recipe.getId());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE, recipe.getTitle());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_OWNER, recipe.getOwner());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE, recipe.getLanguage());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH,
                recipe.getTypeOfDish());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY,
                recipe.getDifficulty());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP,
                dateFormat.format(recipe.getCreatedTimestamp()));
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP,
                dateFormat.format(recipe.getUpdatedTimestamp()));
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME,
                recipe.getCookingTime());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE, recipe.getImage());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING,
                recipe.getTotalRating());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING,
                recipe.getUsersRating());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS, recipe.getServings());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE, recipe.getSource());

        long recipeId;
        recipeId = db.insert(RecipeContract.TABLE_NAME, null, recipeValues);

        // Set database id in recipe
        recipe.setDatabaseId(Objects.toString(recipeId));

        // Insert the database rows for the categories of the recipe in the database
        for (int i = 0; i < recipe.getCategories().size(); i++) {
            RecipeCategory category = (RecipeCategory) recipe.getCategories().get(i);

            ContentValues categoryValues = new ContentValues();
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID,
                    recipeId);
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME,
                    category.getName());
            db.insert(RecipeCategoryContract.TABLE_NAME, null, categoryValues);
        }

        // Insert the database rows for the ingredients of the recipe in the database
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            RecipeIngredient ingredient = (RecipeIngredient) recipe.getIngredients().get(i);

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID,
                    recipeId);
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER,
                    ingredient.getSortNumber());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME,
                    ingredient.getName());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY,
                    ingredient.getQuantity());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT,
                    ingredient.getMeasurementUnit());
            db.insert(RecipeIngredientContract.TABLE_NAME, null, ingredientValues);
        }

        // Insert the database rows for the directions of the recipe in the database
        for (int i = 0; i < recipe.getDirections().size(); i++) {
            RecipeDirection direction = (RecipeDirection) recipe.getDirections().get(i);

            ContentValues directionValues = new ContentValues();
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID,
                    recipeId);
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_SORT_NUMBER,
                    direction.getSortNumber());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_DESCRIPTION,
                    direction.getDescription());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_IMAGE,
                    direction.getImage());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_VIDEO,
                    direction.getVideo());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_TIME,
                    direction.getTime());
            db.insert(RecipeDirectionContract.TABLE_NAME, null, directionValues);
        }
    }


    /**
     * Deletes the specified recipe from the database.
     *
     * @param recipe the recipe to remove
     */
    public void deleteRecipe(Recipe recipe) {
        SQLiteDatabase db = getWritableDatabase();

        /* Delete the database rows for the categories, ingredients and directions of the recipe
         * in the database and the proper recipe
         */
        if (recipe.getDatabaseId() != "") {
            db.delete(RecipeCategoryContract.TABLE_NAME,
                    RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            db.delete(RecipeIngredientContract.TABLE_NAME,
                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            db.delete(RecipeDirectionContract.TABLE_NAME,
                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            db.delete(RecipeContract.TABLE_NAME,
                    RecipeContract.RecipeEntry._ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });
        }
    }

    /**
     * Gets the specified recipe from the database.
     *
     * @param recipeId the API identifier of the project to get
     * @return the specified recipe
     */
    public Recipe getRecipe(String recipeId) {
        // Gets the database in the current database helper in read-only mode
        SQLiteDatabase db = getReadableDatabase();
        Recipe recipe = null;


        ArrayList<RecipeCategory> categories = new ArrayList<>();
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        ArrayList<RecipeDirection> directions = new ArrayList<>();

        // Get all categories with this recipe id
        Cursor catCursor = db.query(RecipeCategoryContract.TABLE_NAME,
                null,
                RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);
        while (catCursor.moveToNext()) {
            RecipeCategory category = new RecipeCategory(
                    catCursor.getString(catCursor.getColumnIndex(
                            RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME)
                    )
            );
            categories.add(category);
        }
        catCursor.close();


        // Get all ingredients with this recipe id
        Cursor ingCursor = db.query(RecipeIngredientContract.TABLE_NAME,
                null,
                RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);
        while (ingCursor.moveToNext()) {
            RecipeIngredient ingredient = new RecipeIngredient(
                    ingCursor.getInt(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER)
                    ),
                    ingCursor.getString(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME)
                    ),
                    ingCursor.getFloat(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY)
                    ),
                    ingCursor.getString(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT)
                    )
            );
            ingredients.add(ingredient);
        }
        ingCursor.close();


        // Get all directions with this recipe id
        Cursor dirCursor = db.query(RecipeDirectionContract.TABLE_NAME,
                null,
                RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);
        while (dirCursor.moveToNext()) {
            RecipeDirection direction = new RecipeDirection(
                    dirCursor.getInt(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_SORT_NUMBER)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_DESCRIPTION)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_IMAGE)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_VIDEO)
                    ),
                    dirCursor.getFloat(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_TIME)
                    )
            );
            directions.add(direction);
        }
        dirCursor.close();

        /* After the query, the cursor points to the first database row
         * returned by the request */
        Cursor projCursor = db.query(RecipeContract.TABLE_NAME,
                null,
                RecipeContract.RecipeEntry.COLUMN_NAME_ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);
        try {
            /* Get the value for each column for the database row pointed by
             * the cursor using the getColumnIndex method of the cursor and
             * use it to initialize a Project object by database row */
            recipe = new Recipe(
                    projCursor.getString(projCursor.getColumnIndex(RecipeContract.RecipeEntry._ID)),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_ID)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TITLE)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_OWNER)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY)
                    ),
                    dateFormat.parse(projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP))
                    ),
                    dateFormat.parse(projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP))
                    ),
                    projCursor.getFloat(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE)
                    ),
                    projCursor.getInt(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING)
                    ),
                    projCursor.getInt(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING)
                    ),
                    projCursor.getInt(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE)
                    ),
                    categories,
                    ingredients,
                    directions
            );
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        projCursor.close();

        return recipe;
    }

    /**
     * Gets the list of recipes from the database.
     *
     * @return the current recipes from the database.
     */
    public ArrayList<Recipe> getProjects() {
        return getProjects(10, 0, null);
    }

    /**
     * Gets the list of recipes from the database.
     *
     * @param limit Max number of results to return
     * @param offset Position of the first element to return
     * @return the current recipes from the database.
     */
    public ArrayList<Recipe> getProjects(Integer limit, Integer offset) {
        return getProjects(limit, offset, null);
    }

    /**
     * Gets the list of recipes from the database.
     *
     * @param limit Max number of results to return
     * @param offset Position of the first element to return
     * @param where Conditions to match
     * @return the current recipes from the database.
     */
    public ArrayList<Recipe> getProjects(Integer limit, Integer offset, String where) {
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Gets the database in the current database helper in read-only mode
        SQLiteDatabase db = getReadableDatabase();

        // After the query, the cursor points to the first database row
        // returned by the request.
        String[] columns = {"_id"};
        Cursor projCursor = db.query(RecipeContract.TABLE_NAME,
                columns,
                where,
                null,
                null,
                null,
                null,
                "limit " + limit + " offset " + offset);
        while (projCursor.moveToNext()) {
            long recipeId = projCursor.getLong(projCursor.getColumnIndex(
                    RecipeContract.RecipeEntry._ID)
            );

            Recipe recipe = getRecipe(Objects.toString(recipeId));
            recipes.add(recipe);
        }
        projCursor.close();

        return recipes;
    }


    /**
     * Initialize example data to show when the application is first installed.
     *
     * @param db the database being initialized.
     */
    private void initializeExampleData(SQLiteDatabase db) {
        JSONArray results = new JSONObject(jsonData);
        for (int i = 0; i < results.length(); i++) {
            Recipe recipe = new Recipe(results.getJSONObject(i));
            this.createRecipe(recipe);
        }
    }

    /**
     * Creates the underlying database with the SQL_CREATE_TABLE queries from
     * the contract classes to create the tables and initialize the data.
     * The onCreate is triggered the first time someone tries to access
     * the database with the getReadableDatabase or
     * getWritableDatabase methods.
     *
     * @param db the database being accessed and that should be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database to contain the data for the projects
        db.execSQL(RecipeContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeCategoryContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeIngredientContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeDirectionContract.SQL_CREATE_TABLE);

        // TODO: Undo this!
        initializeExampleData(db);
    }

    /**
     *
     * This method must be implemented if your application is upgraded and must
     * include the SQL query to upgrade the database from your old to your new
     * schema.
     *
     * @param db the database being upgraded.
     * @param oldVersion the current version of the database before the upgrade.
     * @param newVersion the version of the database after the upgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs that the database is being upgraded
        Log.i(ProjectsDatabaseHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion);
    }
}
