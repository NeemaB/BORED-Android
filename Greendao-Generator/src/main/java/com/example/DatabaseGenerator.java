package com.example;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class DatabaseGenerator {

    public static void main(String[] args)  throws Exception {

        //place where db folder will be created inside the project folder
        Schema schema = new Schema(1,"com.codekrypt.greendao.db");
        schema.enableKeepSectionsByDefault();

        Entity LocalNote = schema.addEntity("LocalNote");
        LocalNote.addIdProperty();
        LocalNote.addDateProperty("Date").notNull();
        LocalNote.addStringProperty("Title").notNull();
        LocalNote.addStringProperty("Topic");
        LocalNote.addStringProperty("filePath").notNull();

        Entity screenInfo = schema.addEntity("ScreenInfo");
        screenInfo.addIntProperty("Width").notNull();
        screenInfo.addIntProperty("Height").notNull();

        //  ./app/src/main/java/   ----   com/codekrypt/greendao/db is the full path
        new DaoGenerator().generateAll(schema, "./app/src/main/java/");

    }

}
