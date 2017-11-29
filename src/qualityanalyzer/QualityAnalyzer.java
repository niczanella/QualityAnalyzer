/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qualityanalyzer;

import utils.DateAnalyzer;
import dbUtils.DBManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;
import nickan.Client;
import nickan.Dataset;
import nickan.Resource;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.xml.sax.SAXException;
import utils.EmailChecker;
import utils.ResourceControls;

/**
 *
 * @author nicola
 */
public class QualityAnalyzer {

    static String link = "http://dati.trentino.it";
    static String modified;
    static DateAnalyzer da;
    public static  Map<String, String> tagsRules = new HashMap<>();
    static Client c;
    
    
    public static void main(String[] args) throws Exception {

        c = new Client(link);
        DBManager manager = new DBManager();
        
        Scanner sc = new Scanner(System.in);
        
        if(!new File(new File(QualityAnalyzer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getParent()+"/db.sqlite").exists()) {
        	System.err.println("Errore, eseguire installazione e riavviare il programma");
        	System.exit(0);
        }
        
        String info = FileUtils.readFileToString(new File(new File(QualityAnalyzer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getParent()+"/comandi.txt")).trim();
        
        System.out.println(info);
        System.out.println();
        
        while(sc.hasNextLine()){
            String input = sc.nextLine();
            if(input.equals("initDB")){
                manager.deleteTables();
                List<String> l = c.getDatasetList();
                initDB(l);
                System.out.println("DB inizializzato");
                System.out.println();
            }
            
            else if(input.equals("continue-initDB")){
                List<String> l = c.getDatasetList();
                l.removeAll(manager.getDatasetNames());
                initDB(l);
                System.out.println("DB inizializzato");
                System.out.println();
            }
            
            else if(input.equals("validate-email")){
                manager.deleteTable("email_verification");                
                List<String> l = manager.getDatasetNames();
                initTableEmailVerification(l);
                System.out.println("Validazione completata");
                System.out.println();
            }
            
            else if(input.equals("continue-validate-email")){
                List<String> l = manager.getDatasetNames();
                l.removeAll(manager.getNamesEmailChecked());
                initTableEmailVerification(l);
                System.out.println("Validazione completata");
                System.out.println();
            }
            
            else if(input.contains("validate-package ")){
                String dataset_name = input.replace("validate-package ", "");
                Dataset d = manager.getDatasetFromName(dataset_name);
                if(d!=null){
                    validateDataset(dataset_name);
                    System.out.println("Validazione completata");
                    System.out.println();
                }
                else{
                    System.out.println("Il package non esiste o non è presente nel database, provare ad aggiornare il db con il comando <continue-initDB>");
                    System.out.println();
                }
            }
            
            else if(input.equals("validate-all-resources")){
                manager.deleteTable("resource_controls");
                List<String> l = manager.getDatasetNames();
                validateAllDatasets(l);
                System.out.println("Validazione completata");
                System.out.println();
            }
            
            else if(input.equals("continue-validate-all-resources")){
                List<String> l = manager.getDatasetNames();
                l.removeAll(manager.getDatasetValidated());
                validateAllDatasets(l);
                System.out.println("Validazione completata");
                System.out.println();
            }
            
            else if(input.equals("exit")){
                System.exit(0);
            }
            
            else if(input.equals("info")){
                System.out.println(info);
                System.out.println();
            }
            else{
                System.out.println("Command not found");
                System.out.println();
            }
        }        
    }
    
    public static void validateAllDatasets(List <String> listDataset) throws Exception{
        DBManager manager = new DBManager();
        int index=1, listSize = listDataset.size();
        for(String s : listDataset){
            try{
                Dataset d = manager.getDatasetFromName(s);
                System.out.println("Package [" +index++ +"/"+ listSize + "] " + " - " + s);
                
                int resIndex = 1, resSize=d.getResources().size();
                for(Resource r : d.getResources()){
                    System.out.println("Risorsa [" +resIndex++ +"/"+ resSize + "] " + " - " + r.getName());
                    ResourceControls rc = new ResourceControls(r, d.getEncoding(), d.getGeographical_geonames_url());
                }
            }
            catch(IOException | ScriptException | ParserConfigurationException | SAXException e){
                System.err.println(s + " ERRORE \n" + e.toString());
            }
        }
    }
    
    public static void validateDataset(String dataset_name) throws Exception{
        DBManager manager = new DBManager();
        Dataset d = manager.getDatasetFromName(dataset_name);
        
        for(Resource r : d.getResources()){
            try{
                System.out.println("Validazione risorsa: " + r.getName());
                ResourceControls rc = new ResourceControls(r, d.getEncoding(), d.getGeographical_geonames_url());
            }
            catch(IOException | ScriptException | ParserConfigurationException | SAXException e){
                System.err.println(r.getId() + e);
            }
        }
    }
    
    public static void initDB(List <String> listDataset) throws MalformedURLException, JSONException, IOException, URISyntaxException{
        da = new DateAnalyzer();
        int index=1;
        int listSize = listDataset.size();
        for(String s:listDataset){
            Dataset d;
            
            try{
                d = new Dataset(c.getPortalUrl(), c.getApi3UrlDatasetShow(), s);
                
            }
            catch(IOException e){
                //API2
                d = new Dataset(c.getPortalUrl(), c.getApi2UrlDatasetShow(), s);
            }
            
            if(d.getType().equals("dataset")){
                System.out.println("Package [" +index++ +"/"+ listSize + "] " + s);
                
                DBManager manager = new DBManager();
                //popolamento tabella dataset
                manager.insertDataset(d);

                //popolamento tabella organization
                manager.insertOrganization(d.getOrganization());

                //popolamento tabella org_in_dataset
                manager.insertOrg_in_dataset(d.getId(), d.getOrganization().getId());

                //popolamento tabella dataset_is_updated
                String result = da.isUpdated(d);
                manager.insertDataset_is_updated(d.getId(), result);

                //popolamento tabelle resource e res_in_dataset
                for(Resource r : d.getResources()){
                    manager.insertResource(r);
                    manager.insertRes_in_dataset(d.getId(), r.getId());
                }
            }
            else{
                System.err.println("Package [" +index++ +"/"+ listSize + "] " + s + " - " + d.getType() + ", non aggiunto.");
            }
        }
    }
    
    public static void initTableEmailVerification(List <String> l) throws URISyntaxException{
        DBManager manager = new DBManager();
        EmailChecker ec = new EmailChecker();
        
        String maintainer_result;
        String author_result;
        String contact_result;
        int index=1, listSize = l.size();
        for(String s : l){
            System.out.println("Package [" +index++ +"/"+ listSize + "] " + s);
            try{
                maintainer_result="NOT VALID";
                author_result="NOT VALID";
                contact_result="NOT VALID";
                
                Dataset d = manager.getDatasetFromName(s);
                
                if(d.getMaintainer_email()!=null){
                    if(ec.isValidEmailAddress(d.getMaintainer_email())){
                        maintainer_result = "OK";
                    }
                }
                else{
                    maintainer_result = "NULL";
                }
                
                if(d.getAuthor_email()!=null){
                    if(ec.isValidEmailAddress(d.getAuthor_email())){
                        author_result = "OK";
                    }
                }
                else{
                    author_result = "NULL";
                }
                
                if(d.getContact()!=null){
                    if(ec.isValidEmailAddress(d.getContact())){
                        contact_result = "OK";
                    }
                }  
                else{
                    contact_result = "NULL";
                }
                
                manager.insertEmailVerification(d.getId(), author_result, maintainer_result, contact_result);
            }
            catch(Exception e){
                //System.err.println(s + " ERRORE \n" + e.toString());
            }
        }
    }
}
