package com.oracle.weblogic.wls12c.restdeployer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

/**
 * Loads weblogic.properties. Configuration automatically populated from pom.xml
 * 
 * @author bruno.borges@oracle.com
 */
public class WebLogicProperties {

    private Properties properties = new Properties();
    private static final String WEBLOGIC_USER = "weblogic.user";
    private static final String WEBLOGIC_PASSWORD = "weblogic.password";

    @PostConstruct
    public void postConstruct() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/weblogic.properties")) {
            this.properties = new Properties();
            this.properties.load(inputStream);
        } catch (IOException ex) {
            Logger.getLogger(WebLogicProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getUser() {
        return (String) properties.get(WEBLOGIC_USER);
    }

    public String getPassword() {
        return (String) properties.get(WEBLOGIC_PASSWORD);
    }

}
