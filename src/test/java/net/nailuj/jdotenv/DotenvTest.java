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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Julian Blazek
 */
public class DotenvTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public DotenvTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of load method, of class Dotenv.
     */
    @org.junit.Test
    public void testLoad() {
        System.out.println("load");
        Dotenv instance = new Dotenv(new File("src/test/resources/load/"));
        instance.skipValidation(true);
        HashMap<String, String> expResult = new HashMap<>();
        expResult.put("Test_1", "Test_Value");
        expResult.put("Null_Value", null);
        expResult.put("a_number", "123321");
        HashMap<String, String> result = instance.load();
        assertEquals(expResult, result);
    }

    /**
     * Test of save method, of class Dotenv.
     */
    @org.junit.Test
    public void testSave() {
        try {
            System.out.println("save");
            Map<String, String> env = new HashMap<>();
            env.put("Test_1", "Test_Value");
            env.put("Null_Value", null);
            env.put("a_number", "123321");
            Dotenv instance = new Dotenv(tempFolder.getRoot());
            instance.save(env);
            
            List<String> generated = loadFileToStringList(new File(tempFolder.getRoot(), ".env"));
            List<String> expected = loadFileToStringList(new File(getClass().getClassLoader().getResource("load/.env").toURI()));
            
            for(String expectedString : expected) {
                if(generated.indexOf(expectedString) == -1) {
                    fail(String.format("%s not found in the generated file!", expectedString));
                }
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(DotenvTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private List<String> loadFileToStringList(File file) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }   bufferedReader.close();
            fileReader.close();
            return lines;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DotenvTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DotenvTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Test of skipValidation method, of class Dotenv.
     */
    @org.junit.Test
    public void testSkipValidation() {
        System.out.println("skipValidation");
        boolean skip = false;
        Dotenv instance = new Dotenv();
        Dotenv expResult = instance;
        Dotenv result = instance.skipValidation(skip);
        assertEquals(expResult, result);
    }

    /**
     * https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     * Test of writeToJavaEnv method, of class Dotenv.
     */
    @org.junit.Ignore
    @org.junit.Test
    public void testWriteToJavaEnv() {
        try {
            System.out.println("writeToJavaEnv");
            boolean write = false;
            Dotenv instance = new Dotenv(new File(getClass().getClassLoader().getResource("load/").toURI()));
            Dotenv expResult = null;
            HashMap<String, String> result = instance.writeToJavaEnv(write).skipValidation(true).load();
            for(String key : result.keySet()) {
                String envVar = System.getenv(key);
                if(envVar == null && result.get(key) != null) {
                    fail();
                }
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(DotenvTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of directory method, of class Dotenv.
     */
    @org.junit.Test
    public void testDirectory() {
        System.out.println("directory");
        String directory = "";
        Dotenv instance = new Dotenv();
        Dotenv expResult = instance;
        Dotenv result = instance.directory(directory);
        assertEquals(expResult, result);
    }

}
