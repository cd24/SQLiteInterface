package SQL;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by John on 4/16/2015.
 */
public class SQLizable {

    public String asSQLValue()  {
        String asSQL = "";
        try {
            String values = "(";
            String names = "(";
            Field[] fields = getClass().getFields();

            for (Field f : fields) {
                if (f.getName().equals("id")) continue;
                String value = fieldValueAsString(f);

                names += f.getName() + ",";
                values += value + ",";
            }

            values = values.substring(0, values.length() - 1);
            names = names.substring(0, names.length() - 1);

            values += ")";
            names += ")";

            asSQL = names + " VALUES " + values;

        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return asSQL;
    }

    public String fieldValueAsString(Field f) throws IllegalAccessException {
        String value = "";

        if (f.get(this) == null){
            throw new IllegalAccessException("THE ATTRIBUTE: " + f.getName() + " IN CLASS " + this.getClass().getName() + " CANNOT BE NULL");
        }
        else if (value.length() == 0) {
            value = f.get(this).toString();
        }
        else if (f.getType() == boolean.class)
            value = f.getBoolean(this) ? "0" : "1";

        else if (f.getType() == Date.class)
            value = getDateAsString((Date) f.get(this));

        else if (f.getType() != String.class  && f.getType() != int.class)
        {
            value = Integer.toString(getRelationID(f));
        }

        return "'" + escape(value) + "'";
    }

    public String updateValue() {
        String updateValues = "";
        try {
            Field[] fields = getClass().getFields();

            for (Field f : fields) {
                if (f.getName().equals("id")) continue;
                String value = fieldValueAsString(f);
                updateValues += f.getName() + "=" + value + ",";
            }
            updateValues = updateValues.substring(0, updateValues.length() - 1);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return updateValues;
    }

    public String getAsQueryable() {
        String asQueryable = "";
        try {
            Field[] fields = getClass().getFields();

            for (Field f : fields) {

                if (f.getName().equals("id")) continue;
                String value = fieldValueAsString(f);

                asQueryable += f.getName() + "=" + value;
                asQueryable += " AND ";
            }
            asQueryable = asQueryable.substring(0, asQueryable.length() - 4);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return  asQueryable;
    }

    public String getDateAsString(java.util.Date date){
        String time = Long.toString(date.getTime());
        return time;
    }

    public boolean isTable(Object o){
        return o.getClass().isAssignableFrom(SQLRow.class);
    }

    public int getRelationID(Field f) throws IllegalAccessException {

        if (isTable(f.get(this))){
            //instantiate to it's object type and determine ID
        }
        else {
            //manual save to object -- should probably create some statics for this.
        }

        Class<?> type = f.getType();
         SQLRow item;
        item = (SQLRow)f.get(this);

        if (item.existsInTable()){
            return item.id();
        }

        item.save();
        return item.id();
    }

    private String escape(String s){
        String escape = "";
        for (int i = 0; i < s.length(); ++i){
            char letter = s.charAt(i);
            if (letter == '\''){
                escape += "\'\'";
            }
            else {
                escape += letter;
            }
        }
        System.out.println("Escaped: " + escape);
        return escape;
    }
}
