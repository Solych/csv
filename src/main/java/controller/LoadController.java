package controller;

import model.Weights;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import service.CsvRepoImpl;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by Pavel on 29.03.2018.
 */
@Controller
@ComponentScan(basePackageClasses = service.CsvRepoImpl.class)
public class LoadController {

    @Autowired
    @Qualifier("RepoImpl")
    private CsvRepoImpl csvRepoImpl;


    @GetMapping("/welcome")
    public String welcome() {
        return "hello";
    }


    @GetMapping("/download")
    public ResponseEntity<List<Weights>> download() {
        List<Weights> weights = csvRepoImpl.findAll();
        if (weights.size() != 0)
            return new ResponseEntity<List<Weights>>(weights, HttpStatus.OK);
        return new ResponseEntity<List<Weights>>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        byte[] bytes = multipartFile.getBytes();
        System.out.println(bytes[0]);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }




}
