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
import service.impl.JobServiceImpl;

import java.io.IOException;


/**
 * Controller for interception of requests:
 * /download: returns a csv file with lines from db if exists
 * /upload: receives a file with extension .xlsx or .xls, parse him and write parsed lines in a bd
 * (if extension is not xlsx/xls - returns bad request)
 *
 * @see JobServiceImpl
 */
@Controller
public class LoadController {

    @Autowired
    @Qualifier("JobServiceImpl")
    private JobServiceImpl jobService;

    private final static String INVALID_TYPE_FILE = "Invalid type of file";
    private final static String EMPTY_DB = "Db is empty";
    private final static String INTERNAL_SERVER_ERROR = "Internal server error";
    private final static String DOWNLOAD_IO_EXCEPTION = "Something going wrong";


    @Autowired
    private Logger logger;


    /**
     * Function recognize if exists any notes in db. If answer on this question - true - returns a stream of csv
     * Else - returns bad request
     */
    @GetMapping("/download")
    public ResponseEntity<?> download() {
        try {
            InputStreamResource resource = jobService.read();
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        } catch (IOException ex) {
            return new ResponseEntity<>(DOWNLOAD_IO_EXCEPTION, HttpStatus.BAD_REQUEST);
        } catch (EmptyDbException ex) {
            return new ResponseEntity<>(EMPTY_DB, HttpStatus.NOT_FOUND);
        }


    }


    /**
     * Method for uploading a file which contains a students timetable with extension xlsx/xls
     *
     * @param file with timetable which have extension xlsx/xls
     * @return httpStatus.ok
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        try {
            Lines lines = jobService.write(file);
            logger.debug("RECORDED LINES: " + lines.getRecordedLines());
            logger.debug("SKIPPED LINES: " + lines.getSkippedLines());
            return new ResponseEntity<>(lines, HttpStatus.OK);

        } catch (IOException ex) {
            return new ResponseEntity<>(INVALID_TYPE_FILE,HttpStatus.BAD_REQUEST);
        } catch (Exception ex){
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


}
