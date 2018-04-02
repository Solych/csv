package controller;

import model.Weights;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import service.CsvRepoImpl;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
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


    @GetMapping("/download")
    public ResponseEntity<List<Weights>> download() {
        List<Weights> weights = csvRepoImpl.findAll();
        if (weights.size() != 0)
            return new ResponseEntity<List<Weights>>(weights, HttpStatus.OK);
        return new ResponseEntity<List<Weights>>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if(multipartFile.getContentType().equals("application/vnd.ms-excel")) {
            CSVParser csvParser = CSVFormat.EXCEL.withHeader().parse(new InputStreamReader(multipartFile.getInputStream()));
            List<Weights> weights = new ArrayList<Weights>();
            for (CSVRecord csvRecord : csvParser)
                weights.add(new Weights(csvRecord.get(0), new BigDecimal(csvRecord.get(1))));
            csvRepoImpl.save(weights);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }




}
