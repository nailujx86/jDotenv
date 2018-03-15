/*
 * Copyright (C) 2018 Julian Blazek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.nailuj.jdotenv;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julian Blazek
 */
public class Dotenv {

    private File dir = new File("").getAbsoluteFile();
    private boolean validation = true;
    private boolean javaEnvOverwrite = false;

    public HashMap<String, String> load() {
        DotenvLoader loader = new DotenvLoader(new File(dir, ".env"), validation);
        try {
            HashMap parse = loader.parse();
            if(javaEnvOverwrite) setEnv(parse);
            return parse;
        } catch (IOException | DotenvException ex) {
            Logger.getLogger(Dotenv.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Dotenv.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Dotenv skipValidation(boolean skip) {
        validation = !skip;
        return this;
    }

    public Dotenv writeToJavaEnv(boolean write) {
        javaEnvOverwrite = true;
        return this;
    }

    public Dotenv directory(String directory) {
        dir = new File(directory).getAbsoluteFile();
        if (!dir.isDirectory()) {
            throw new RuntimeException("Path specified is not a Directory!");
        }
        return this;
    }

    private static void setEnv(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }
    }
}
