package controller;

import ch.qos.logback.classic.Logger;
import model.Weights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import service.CsvRepoImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


/**
 * Controller for interception of requests:
 * /download: returns a csv file with lines from db if exists
 * /upload: receives a file with extension .xlsx, parse him and write parsed lines in a bd
 * (if extension is not xlsx - returns bad request)
 * For queries in db used service
 *
 * @see service.CsvRepoImpl
 */
@Controller
//@ComponentScan(basePackageClasses = service.CsvRepoImpl.class)
public class LoadController {

    private final String PATH = "Z://JavaProject//csv//temp2.csv";


    @Autowired
    @Qualifier("RepoImpl")
    private CsvRepoImpl csvRepoImpl;


    @Autowired
    Logger logger;


    /**
     * Function recognize if exists any notes in db. If answer on this question - true - returns a stream of csv
     * Else - returns bad request
     */
    @GetMapping("/download")
    public ResponseEntity<?> download() throws IOException {
        boolean answer = csvRepoImpl.isFindAll();
        if (answer) {
            File file = new File(PATH);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    /**
     * Function check if file have a extension xlsx, if good - save all data from file in db, else - bad request
     *
     * @param multipartFile file from client which expected as xlsx or xls
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile multipartFile)
            throws IOException, NullPointerException {
            return csvRepoImpl.save(multipartFile) ?
                    new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }




    @PostMapping("/uploadThread")
    public ResponseEntity<Void> uploadThread(@RequestParam("file") MultipartFile file) throws IOException{
        csvRepoImpl.createTasks(file);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/get")
    public ResponseEntity<Void> getBdLines(){
        List<Weights> weightsList = csvRepoImpl.get();
        int counter = 0;
        for(int i = 0;i<weightsList.size();i++){
            String temp1 = weightsList.get(i).getWord();
            for(int j = 0 ;j<weightsList.size();j++){
                if(i==j)
                    continue;
                if(temp1.equals(weightsList.get(j).getWord()))
                    counter++;
            }
        }
        logger.debug(""+counter);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
