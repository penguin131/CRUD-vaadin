package com.utils;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * read config.xml from root directory and save params
 */

public class DbConfiguration {
    static {
        File file = new File("config.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            inputDirectory = doc.getElementsByTagName("input_folder").item(0).getTextContent();
            outputDirectory = doc.getElementsByTagName("output_folder").item(0).getTextContent();
            userName = doc.getElementsByTagName("username").item(0).getTextContent();
            password = doc.getElementsByTagName("password").item(0).getTextContent();
            url = doc.getElementsByTagName("url").item(0).getTextContent();
            createDirectoryIfExists(getInputDirectory());
            createDirectoryIfExists(getOutputDirectory());
            connect = DriverManager.getConnection(getUrl(), getUserName(), getPassword());
        } catch (Exception ex) {
            throw new Error();
        }
    }

    private static void createDirectoryIfExists(String nameDir) throws Exception {
        File fir = new File(nameDir);
        if (!fir.exists()) {
            if (!fir.mkdir()) {
                throw new Exception("Create directory error!");
            }
        }
    }

    private static final String inputDirectory;
    private static final String outputDirectory;
    private static final String userName;
    private static final String password;
    private static final String url;
    private static final Connection connect;

    public static String getInputDirectory() { return inputDirectory; }

    public static String getOutputDirectory() { return outputDirectory; }

    public static String getUserName() { return userName; }

    public static String getPassword() { return password; }

    public static String getUrl() { return url; }

    public static Connection getConnect() {
        return connect;
    }
}
