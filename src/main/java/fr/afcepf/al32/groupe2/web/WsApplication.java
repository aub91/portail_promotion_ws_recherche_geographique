package fr.afcepf.al32.groupe2.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

//NB: @SpringBootApplication est un equivalent 
//    de @Configuration + @EnableAutoConfiguration 
//    + @ComponentScan/current package.
//    En mode @EnableAutoConfiguration le fichier application.properties
//    est automatiquement analysé et pris en compte

@SpringBootApplication(scanBasePackages= {"fr.afcepf.al32.groupe2"})
@EntityScan(basePackages= {"fr.afcepf.al32.groupe2.entity"})
public class WsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
    	//démarrage automatique d'un 
    	//"équivalent de tomcat embarqué/imbriqué dans l'application"
        SpringApplication.run(WsApplication.class, args);
        //afficher l'URL pour effectuer un test via un navigateur.
        System.out.println("http://localhost:8082/wsRechercheGeo");
    }

}