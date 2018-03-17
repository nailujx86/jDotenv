# jDotenv
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Build Status](https://travis-ci.org/nailujx86/jDotenv.svg?branch=master)](https://travis-ci.org/nailujx86/jDotenv)
Sample Usage:
```java
import net.nailuj.jdotenv.Dotenv;

public class jDotenvExample {
    public static void main(String[] args) {
        Dotenv dotenv = new Dotenv();
        HashMap<String, String> envValues = dotenv
                .skipValidation(true) // Skip the validation of .env Files
                .directory("") // Specifies the directory of the .env File. Defaults to the cwd. "" sets it to the cwd
                .writeToJavaEnv(true) // Writes the parsed env File also to the internal java Environment so you could also do System.getenv() to get values
                .load(); // Loads the .env File and returns a HashMap
        
        System.out.println(envValues.get("example_key")); // in case of .writeToJavaEnv this prints the same value as the line below
        System.out.println(System.getenv("example_key"));
    }
}
```
Sample .env:
```env
example_key=1234test

invalid_key
# the key below returns null when skipValidation(true) otherwise it is invalid
invalid_key2=
invalid_key3=value=4
```
