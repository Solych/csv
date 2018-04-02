package controller;

import model.Weights;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
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

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



/**
 * Controller for interception of requests:
 * /download: returns a lines from db if exists
 * /upload: receives a file with extension .csv, parse him and write parsed lines in a bd
 * (if extension is not csv - returns bad request)
 * For queries in db used service
 * @see service.CsvRepoImpl
 *
 */
@Controller
//@ComponentScan(basePackageClasses = service.CsvRepoImpl.class)
public class LoadController {

    private final String path = "Z://JavaProject//csv//temp2.csv";


    @Autowired
    @Qualifier("RepoImpl")
    private CsvRepoImpl csvRepoImpl;


    /**
     * Function for interception of request on /download
     * @return ok - if exists any lines in db, not_found - else
     */
    @GetMapping("/download")
    public ResponseEntity<?> download() throws IOException {
        boolean answer = csvRepoImpl.isFindAll();
        if(answer) {
            File file = new File(path);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        }
        return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
    }


    /**
     * Function for interception of request on /upload
     * Check extension of file: if .csv - creates the CSVParser like a
     * format.withIgnoreHeaderCase(if header not exists, if exists - withHeader()).parse(Stream from file)
     * After that added all lines in a list and save him in db
     * @param multipartFile file
     * @return ok - if file was parsed and wrote in db, bad request - if file isn't have extension .csv
     * @throws IOException for multipartFile.getInputStream()
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if(multipartFile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            csvRepoImpl.save(multipartFile);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);

    }




}
