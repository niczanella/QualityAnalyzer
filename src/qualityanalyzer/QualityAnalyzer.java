package qualityanalyzer;

import utils.DateAnalyzer;
import dbUtils.DBManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
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

    static String portal;
    static String modified;
    static DateAnalyzer da;
    public static  Map<String, String> tagsRules = new HashMap<>();
    static Client c;
    
    
    public static void main(String[] args) throws Exception {

        //c = new Client(link);
        DBManager manager = new DBManager();
        
        if(!new File(new File(QualityAnalyzer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getParent()+"/files/db.sqlite").exists()) {
            System.err.println("Errore, eseguire installazione e riavviare il programma");
            System.exit(0);
        }
        
        
        try {
            boolean portaleValido = false;
            System.out.println("Inserire portale da analizzare tra:");
            String portali = FileUtils.readFileToString(new File(new File(QualityAnalyzer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getParent()+"/files/portali.txt")).trim();
            System.out.println(portali);
            System.out.println();
            while(!portaleValido){
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));                
                portal = br.readLine();
                if(portali.contains(portal)){
                    portaleValido = true;
                }
                else{
                    System.out.println("Portale inserito non valido, inserine uno compreso nella lista\n");
                }
                
            }
            c = new Client(portal);
        }
        catch (IOException e) {
        }
                
        String info = FileUtils.readFileToString(new File(new File(QualityAnalyzer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getParent()+"/files/comandi.txt")).trim();
        
        System.out.println(info);
        System.out.println();
        
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            String input = sc.nextLine();
            if(input.equals("init-and-validate")){
                manager.deleteTables();
                List<String> l = c.getDatasetList();
                initAndValidate(l);
                System.out.println("Esecuzione terminata");
                System.out.println();
            }
            
            else if(input.equals("continue-init-and-validate")){
                List<String> l = c.getDatasetList();
                l.removeAll(manager.getDatasetNames());
                initAndValidate(l);
                System.out.println("Esecuzione terminata");
                System.out.println();
            }
            
            else if(input.contains("validate-package ")){
                String dataset_name = input.replace("validate-package ", "");
                validateDataset(dataset_name);
                System.out.println("Validazione completata");
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
    
    /**
     * Validazione di tutte le risorse del package indicato aggiornando le relative informazioni se già presenti
     * @param dataset_name
     * @throws Exception 
     */
    public static void validateDataset(String dataset_name) throws Exception{
        Dataset d;
        try{
            d = new Dataset(c.getPortalUrl(), c.getApi3UrlDatasetShow(), dataset_name);

        }
        catch(IOException e){
            try{
                //API2
                d = new Dataset(c.getPortalUrl(), c.getApi2UrlDatasetShow(), dataset_name);
            }
            catch(IOException | JSONException ex){
                System.err.println("Impossibile validare il package selezionato");
                return;
            }   
        }
        
        DBManager manager = new DBManager();
        manager.deleteDataset(dataset_name);
        manager.insertDataset(d);
        
        for(Resource r : d.getResources()){
            try{
                System.out.println("Validazione risorsa: " + r.getName());
                manager.deleteControlFromResId(r.getId());
                ResourceControls rc = new ResourceControls(r, d.getEncoding(), d.getGeographical_geonames_url());
            }
            catch(IOException | ScriptException | ParserConfigurationException | SAXException e){
                System.err.println(r.getId() + e);
            }
        }
    }
    
    public static void initAndValidate(List <String> listDataset) throws Exception{
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
                
                //popolamento tabella email_verification
                String [] emailResults = checkemail(d);
                manager.insertEmailVerification(d.getId(), emailResults[0], emailResults[1], emailResults[2]);
                
                //popolamento tabelle resource e res_in_dataset
                int resIndex = 1, resSize=d.getResources().size();
                for(Resource r : d.getResources()){
                    System.out.println("Risorsa [" +resIndex++ +"/"+ resSize + "] " + " - " + r.getName());
                    manager.insertResource(r);
                    manager.insertRes_in_dataset(d.getId(), r.getId());
                    try{
                        ResourceControls rc = new ResourceControls(r, d.getEncoding(), d.getGeographical_geonames_url());
                    }
                    catch(IOException | ScriptException | ParserConfigurationException | SAXException e){
                        System.err.println(s + " ERRORE \n" + e.toString() + "\n----------------------");
                    }
                }
            }
            else{
                System.err.println("Package [" +index++ +"/"+ listSize + "] " + s + " - " + d.getType() + ", non aggiunto.");
            }
        }
    }
    
    public static String [] checkemail(Dataset d){
        String maintainer_result="NOT VALID";
        String author_result="NOT VALID";
        String contact_result="NOT VALID";
        EmailChecker ec = new EmailChecker();
        
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
        
        String [] results = {author_result, maintainer_result, contact_result};
        return results;
    }
}
