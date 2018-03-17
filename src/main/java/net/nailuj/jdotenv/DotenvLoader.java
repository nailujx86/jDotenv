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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julian Blazek
 */
public class DotenvLoader {

    private File envFile;
    private List<String> envLines;
    private boolean validate = true;

    public DotenvLoader(File envFile, boolean validate) {
        this.envFile = envFile;
        this.validate = validate;
    }

    public HashMap parse() throws FileNotFoundException, IOException, DotenvException {
        loadFile();
        if (validate) {
            try {
                validate();
            } catch (DotenvException dex) {
                dex.printStackTrace();
            }
        }
        HashMap<String, String> map;
        map = new HashMap<>();
        for (String curLine : envLines) {
            if (curLine.startsWith("#")) {
                continue;
            } else {
                String[] split = curLine.split("=");
                if (split.length == 1) {
                    if (curLine.endsWith("=")) {
                        map.put(split[0], null);
                    } else {
                        continue;
                    }
                } else if (split.length > 2) {
                    continue;
                } else {
                    map.put(split[0], split[1]);
                }
            }
        }
        return map;
    }

    public void validate() throws DotenvException {
        loadFile();
        int lineCounter = 0;
        for (String curLine : envLines) {
            lineCounter++;
            if (curLine.startsWith("#")) {
                continue;
            }
            String[] split = curLine.split("=");
            if (split.length == 1) {
                if (!curLine.equals("")) {
                    throw new DotenvException(String.format("Validation failed. No assignment in line %d", lineCounter));
                }
                if (curLine.trim().endsWith("=")) {
                    throw new DotenvException(String.format("Validation failed. Empty value in line %d: %s", lineCounter, curLine));
                }
            }
            if (split.length > 2) {
                throw new DotenvException(String.format("Validation failed. More than one assignment in one line %d", lineCounter));
            }
        }
    }

    public void reloadFile() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(envFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            envLines = lines;
            return;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DotenvLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DotenvLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {
                Logger.getLogger(DotenvLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return;
    }

    private void loadFile() {
        if (envLines != null) {
            return;
        } else {
            reloadFile();
        }
    }
}
