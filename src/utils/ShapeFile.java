package utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author nicola
 */
public class ShapeFile {
    private String log = "";
    private boolean correct;
    private boolean shapefile;
    private boolean diretto = true;
    private boolean allFiles;
    private CoordinateReferenceSystem crs;
    private Geo geo;
    int a=0;
    
    private final List<File> shpList;
    
    
    
    public ShapeFile() {
        this.shpList = new LinkedList<>();
    }
    
    
    public void checkShape(String geonamesUrl) throws Exception{
        
        int numInvalidGeometries = 0;
        for(File f: shpList){
            geo = new Geo(geonamesUrl);
            List<Integer> distanze = new LinkedList<>();
            
            FileDataStore store = FileDataStoreFinder.getDataStore(f);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureType schema = featureSource.getSchema();
            crs = schema.getCoordinateReferenceSystem();
            numInvalidGeometries += validateFeatureGeometry(featureSource, f.getName(), distanze);
            
            if(!distanze.isEmpty()){
                if(distanze.size()==1){
                    log += "- Il file " + f.getName() + " contiene un oggetto fuori dal confine di riferimento di " + distanze.get(0) + "m\n";
                }
                else{
                    log += Utils.stats(distanze);
                }
            }
            
            if(geonamesUrl!=null){
                log += geo.getLog();
            }
            if(numInvalidGeometries!=0)
                log += "- Il file " + f.getName() + " contiene " + numInvalidGeometries + " geometrie invalide\n";
            store.dispose();
        }
        correct = numInvalidGeometries==0 && geo.isCorrect();
        
    }
    
    private int validateFeatureGeometry(SimpleFeatureSource featureSource, String shpName, List<Integer> distanze) throws IOException, FactoryException, MismatchedDimensionException, TransformException {
        final SimpleFeatureCollection featureCollection = featureSource.getFeatures();

        // Rather than use an iterator, create a FeatureVisitor to check each fature
        class ValidationVisitor implements FeatureVisitor {
            public int numInvalidGeometries = 0;
            @Override
            public void visit(Feature f) {
                SimpleFeature feature = (SimpleFeature) f;
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                
                try {
                    //a++;                    
                    if(geom != null && geo.getGeonamesUrl()!=null){
                        boolean insideGeometry = geo.containsGeometry(geom, crs);    

                        if(!insideGeometry){
                            boolean intersectGeometry = geo.intersectGeometry(geom);
                            if(!intersectGeometry){
                                geo.setCorrect(false);
                                int dist = geo.getDistanceGeometry(geom);
                                if(dist!=0){
                                    distanze.add(dist);
                                }
                            }
                        }
                    }
                }
                
                catch (com.vividsolutions.jts.io.ParseException | IOException | ParseException | MismatchedDimensionException | FactoryException | TransformException ex) {
                } catch (URISyntaxException ex) {
                    Logger.getLogger(ShapeFile.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if (geom != null && !geom.isValid()) {
                    numInvalidGeometries++;
                }
            }
        }

        ValidationVisitor visitor = new ValidationVisitor();
        
        featureCollection.accepts(visitor, null);
        return visitor.numInvalidGeometries;
    }
    /**
     * verifica della presenza di tutti i file necessari per utilizzare lo shape file (.prj, .shp, .shx, .dbf). Viene aggiornato la stringa log della classe
     * @param dirPath 
     */
    public void checkDir(String dirPath) {
        
        List<File> files = new LinkedList<>();
        List<String> directories = new LinkedList<>();
        directories.add(dirPath);
                
        boolean tmp = false;

        listFilesAndDir(dirPath, files, directories);
        
        for (String dir : directories) {
            List<File> fList = new LinkedList<>(Arrays.asList(new File(dir).listFiles()));
            
            shapefile = false;

            List<Integer> shpIndexes = getIndecesSubString(fList, ".shp");
            for(int i:shpIndexes){
                if(diretto)
                    diretto = dir.equals(dirPath);
                shapefile = true;
                shpList.add(fList.get(i).getAbsoluteFile());
                String name = fList.get(i).getName().replace(".shp", "");
                boolean isCorrectShape = isCorrectShapeFile(fList, name, dir);

                if(isCorrectShape){
                    if(!tmp)
                        setAllFiles(true);
                    tmp = true;
                }
                else{
                    tmp = true;
                    setAllFiles(false);
                }
            }
            List<Integer> indexesLyr = Utils.getIndexesFromExtension(files, ".lyr");
            
            for(int i : indexesLyr){
                setLog(getLog() + "- E' presente il file lyr " + files.get(i).getName() + " in " + dir + "\n");
            }
        }
        if(tmp && isAllFiles()){
            log += "- In " + dirPath + " sono presenti uno o più file .shp con tutte le estensioni necessarie per comporlo\n";
        }
        else if(tmp && !isAllFiles()){
            log += "- In " + dirPath + " sono presenti uno o più file .shp ma non è/sono completo/i\n";
        }
    }
    
    public List<Integer> getIndecesSubString(List<File> files, String sub_string) {
        
        List<Integer> indeces = new LinkedList<>();
        
        int index = 0;
        for (File f : files) {
            String s = f.getName();
            
            int lastPointIndex = s.lastIndexOf(".");
            if(lastPointIndex!=-1){
                if(s.substring(lastPointIndex).equals(sub_string)){
                    indeces.add(index);
                }
            }
            index++;
        }
        return indeces;
    }
    /**
     * metodo di supporto per checkDir(String dirPath)
     */
    private boolean isCorrectShapeFile(List<File> files, String shpName, String dir) {
        boolean shx = true, dbf = true, prj = true;

        if (Utils.containsFileName(files, shpName + ".shx") == -1) {
            setLog(getLog() + "- Manca il file " + shpName + ".shx in " + dir + "\n");
            shx = false;
        }

        if (Utils.containsFileName(files, shpName + ".dbf") == -1) {
            setLog(getLog() + "- Manca il file " + shpName + ".dbf in " + dir + "\n");
            dbf = false;
        }

        if (Utils.containsFileName(files, shpName + ".prj") == -1) {
            setLog(getLog() + "- Manca il file " + shpName + ".prj in " + dir + "\n");
            prj = false;
        }        
        return shx && dbf && prj;
    }

    private void listFilesAndDir(String directoryName, List<File> files, List<String> directories) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                directories.add(file.getAbsolutePath());
                listFilesAndDir(file.getAbsolutePath(), files, directories);
            }
        }
    }

    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     * @return the correct
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * @param correct the correct to set
     */
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    /**
     * @return the shapefile
     */
    public boolean isShapefile() {
        return shapefile;
    }

    /**
     * @param shapefile the shapefile to set
     */
    public void setShapefile(boolean shapefile) {
        this.shapefile = shapefile;
    }

    /**
     * @return the diretto
     */
    public boolean isDiretto() {
        return diretto;
    }

    /**
     * @param diretto the diretto to set
     */
    public void setDiretto(boolean diretto) {
        this.diretto = diretto;
    }

    /**
     * @return the allFiles
     */
    public boolean isAllFiles() {
        return allFiles;
    }

    /**
     * @param allFiles the allFiles to set
     */
    public void setAllFiles(boolean allFiles) {
        this.allFiles = allFiles;
    }

}
