package controller;

import ch.qos.logback.classic.Logger;
import exceptions.EmptyDbException;
import model.Lines;
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
import service.CsvService;

import java.io.IOException;


/**
 * Controller for interception of requests:
 * /download: returns a csv file with lines from db if exists
 * /upload: receives a file with extension .xlsx or .xls, parse him and write parsed lines in a bd
 * (if extension is not xlsx/xls - returns bad request)
 *
 * @see CsvService
 */
@Controller
public class LoadController {

    @Autowired
    @Qualifier("Service")
    private CsvService csvService;


    @Autowired
    private Logger logger;


    /**
     * Function recognize if exists any notes in db. If answer on this question - true - returns a stream of csv
     * Else - returns bad request
     */
    @GetMapping("/download")
    public ResponseEntity<?> download() {
        try {
            InputStreamResource resource = csvService.getAllByStream();
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EmptyDbException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }


    /**
     * Method for uploading a file which contains a students timetable with extension xlsx/xls
     *
     * @param file timetable
     * @return httpStatus.ok
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadThread(@RequestParam("file") MultipartFile file) {

        try {
            Lines lines = csvService.createTasks(file);
            logger.debug("RECORDED LINES: " + lines.getRecordedLines());
            logger.debug("SKIPPED LINES: " + lines.getSkippedLines());
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }


}
